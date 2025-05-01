package controllers;

import entities.Universite;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Candidature;
import Services.CandidatureService;
import Services.ServiceUniversite;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class NewCandidatureController implements Initializable {

    @FXML
    private ComboBox<String> dossierComboBox;
    
    @FXML
    private ComboBox<String> universiteComboBox;
    
    @FXML
    private TextField domaineField;
    
    @FXML
    private DatePicker datePicker;
    
    @FXML
    private TextArea commentaireArea;
    
    @FXML
    private Button submitButton;
    
    @FXML
    private Button cancelButton;
    
    private CandidatureService candidatureService;
    private ServiceUniversite universiteService;
    
    private ObservableList<String> dossierList = FXCollections.observableArrayList();
    private ObservableList<String> universiteList = FXCollections.observableArrayList();
    
    // Hardcoded user ID for testing - in a real app, this would come from logged in user
    private final int currentUserId = 31;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        candidatureService = new CandidatureService();
        universiteService = new ServiceUniversite();
        
        // Initialize date picker with current date
        datePicker.setValue(LocalDate.now());
        
        // Load dosiers and universities
        loadDossiers();
        loadUniversites();
    }
    
    private void loadDossiers() {
        // For demo purposes, add some example dossier IDs
        dossierList.add("37");
        dossierList.add("38");
        dossierList.add("39");
        
        dossierComboBox.setItems(dossierList);
        
        // Select first dossier
        if (!dossierList.isEmpty()) {
            dossierComboBox.setValue(dossierList.get(0));
        }
    }
    
    private void loadUniversites() {
        try {
            List<Universite> universities = universiteService.recuperer();
            
            // Format universities for display
            universiteList.addAll(universities.stream()
                .map(u -> u.getId_universite() + " - " + u.getNom())
                .collect(Collectors.toList()));
            
            universiteComboBox.setItems(universiteList);
            
            // Select first university
            if (!universiteList.isEmpty()) {
                universiteComboBox.setValue(universiteList.get(0));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les universités", e.getMessage());
        }
    }
    
    @FXML
    private void handleSubmitButton() {
        if (validateInputs()) {
            try {
                Candidature candidature = new Candidature();
                
                // Set dossier ID
                String selectedDossier = dossierComboBox.getValue();
                candidature.setId_dossier(Integer.parseInt(selectedDossier));
                
                // Set user ID
                candidature.setUser_id(currentUserId);
                
                // Set university ID
                String selectedUniversity = universiteComboBox.getValue();
                int universityId = Integer.parseInt(selectedUniversity.split(" - ")[0]);
                candidature.setId_universite(universityId);
                
                // Set date
                LocalDate localDate = datePicker.getValue();
                candidature.setDate_de_remise_c(Date.valueOf(localDate));
                
                // Set domain
                candidature.setDomaine(domaineField.getText());
                
                // Add candidature
                boolean success = candidatureService.addCandidature(candidature);
                
                if (success) {
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Candidature créée", 
                            "Votre candidature a été créée avec succès!");
                    clearForm();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de création", 
                            "Impossible de créer la candidature. Veuillez vérifier vos données.");
                }
                
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de création", 
                        "Erreur lors de la création de la candidature: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleCancelButton() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        
        if (dossierComboBox.getValue() == null || dossierComboBox.getValue().isEmpty()) {
            errorMessage.append("- Veuillez sélectionner un dossier\n");
        }
        
        if (universiteComboBox.getValue() == null || universiteComboBox.getValue().isEmpty()) {
            errorMessage.append("- Veuillez sélectionner une université\n");
        }
        
        if (domaineField.getText() == null || domaineField.getText().isEmpty()) {
            errorMessage.append("- Veuillez entrer un domaine d'étude\n");
        }
        
        if (datePicker.getValue() == null) {
            errorMessage.append("- Veuillez sélectionner une date\n");
        }
        
        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.WARNING, "Données manquantes", "Veuillez corriger les erreurs suivantes:", 
                    errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    private void clearForm() {
        if (!dossierList.isEmpty()) {
            dossierComboBox.setValue(dossierList.get(0));
        }
        
        if (!universiteList.isEmpty()) {
            universiteComboBox.setValue(universiteList.get(0));
        }
        
        domaineField.clear();
        datePicker.setValue(LocalDate.now());
        commentaireArea.clear();
    }
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 