package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Candidature;
import Services.CandidatureService;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.Optional;

public class SupprimerCondituresController {

    @FXML
    private TableView<Candidature> candidatureTable;
    
    @FXML
    private TableColumn<Candidature, String> nomColumn;
    
    @FXML
    private TableColumn<Candidature, String> prenomColumn;
    
    @FXML
    private TableColumn<Candidature, String> emailColumn;
    
    @FXML
    private TableColumn<Candidature, Date> dateColumn;
    
    @FXML
    private TableColumn<Candidature, String> universiteColumn;
    
    @FXML
    private TableColumn<Candidature, String> domaineColumn;
    
    @FXML
    private Button supprimerButton;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private Button retourButton;
    
    private CandidatureService candidatureService;
    private ObservableList<Candidature> candidatureList;
    
    public void initialize() {
        candidatureService = new CandidatureService();
        
        // Configuration des colonnes du TableView
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date_de_remise_c"));
        universiteColumn.setCellValueFactory(new PropertyValueFactory<>("universite"));
        domaineColumn.setCellValueFactory(new PropertyValueFactory<>("domaine"));
        
        // Charger les candidatures
        loadCandidatures();
    }
    
    private void loadCandidatures() {
        try {
            List<Candidature> candidatures = candidatureService.getAllCandidatures();
            candidatureList = FXCollections.observableArrayList(candidatures);
            candidatureTable.setItems(candidatureList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les candidatures", e.getMessage());
        }
    }
    
    @FXML
    private void handleSupprimerButton() {
        Candidature selectedCandidature = candidatureTable.getSelectionModel().getSelectedItem();
        
        if (selectedCandidature == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune Sélection", "Aucune candidature sélectionnée", 
                    "Veuillez sélectionner une candidature à supprimer.");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de Suppression");
        confirmAlert.setHeaderText("Supprimer la candidature");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette candidature?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                candidatureService.deleteCandidature(selectedCandidature.getId_c());
                candidatureList.remove(selectedCandidature);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Candidature supprimée", 
                        "La candidature a été supprimée avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression", 
                        "Impossible de supprimer la candidature: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleRefreshButton() {
        loadCandidatures();
    }
    
    @FXML
    private void handleRetourButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/adminconditures.fxml"));
            Stage stage = (Stage) retourButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible de retourner à l'écran précédent", 
                    e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 