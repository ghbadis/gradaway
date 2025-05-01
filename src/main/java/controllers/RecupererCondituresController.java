package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Candidature;
import Services.CandidatureService;
import entities.User;
import Services.ServiceUser;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RecupererCondituresController implements Initializable {

    @FXML
    private TableView<Candidature> candidaturesTable;
    
    @FXML
    private TableColumn<Candidature, Integer> userColumn;
    
    @FXML
    private TableColumn<Candidature, Integer> dossierColumn;
    
    @FXML
    private TableColumn<Candidature, Integer> universiteColumn;
    
    @FXML
    private TableColumn<Candidature, Date> dateColumn;
    
    @FXML
    private TableColumn<Candidature, String> domaineColumn;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private Button retourButton;
    
    @FXML
    private Button logoutButton;
    
    private CandidatureService candidatureService;
    private ObservableList<Candidature> candidaturesList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        candidatureService = new CandidatureService();
        
        // Configuration des colonnes du TableView
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user_id"));
        dossierColumn.setCellValueFactory(new PropertyValueFactory<>("id_dossier"));
        universiteColumn.setCellValueFactory(new PropertyValueFactory<>("id_universite"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date_de_remise_c"));
        domaineColumn.setCellValueFactory(new PropertyValueFactory<>("domaine"));
        
        // Charger les candidatures
        loadCandidatures();
    }
    
    private void loadCandidatures() {
        try {
            List<Candidature> candidatures = candidatureService.getAllCandidatures();
            candidaturesList = FXCollections.observableArrayList(candidatures);
            candidaturesTable.setItems(candidaturesList);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les candidatures", e.getMessage());
        }
    }
    
    @FXML
    private void handleRefreshButton() {
        loadCandidatures();
    }
    
    @FXML
    private void handleSupprimerButton() {
        Candidature selectedCandidature = candidaturesTable.getSelectionModel().getSelectedItem();
        
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
                candidaturesList.remove(selectedCandidature);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Candidature supprimée", 
                        "La candidature a été supprimée avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de la suppression", 
                        "Impossible de supprimer la candidature: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleRetourButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/adminconditature.fxml"));
            Stage stage = (Stage) retourButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible de retourner à l'écran précédent", 
                    e.getMessage());
        }
    }
    
    @FXML
    private void handleLogoutButton() {
        try {
            candidatureService.closeConnection();
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Déconnexion", "Impossible de se déconnecter", 
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