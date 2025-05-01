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

public class AfficherCondituresController {

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