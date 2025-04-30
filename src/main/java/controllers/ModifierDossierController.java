package controllers;

import Services.ServiceDossier;
import entities.Dossier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;

public class ModifierDossierController {

    @FXML private TextField cinPathField;
    @FXML private TextField photoPathField;
    @FXML private TextField diplomeBacPathField;
    @FXML private TextField releveNotePathField;
    @FXML private TextField diplomeObtenuPathField;
    @FXML private TextField lettreMotivationPathField;
    @FXML private TextField dossierSantePathField;
    @FXML private TextField cvPathField;
    @FXML private DatePicker dateDepotPicker;
    @FXML private Button updateButton;
    @FXML private Button cancelButton;
    @FXML private AnchorPane rootPane;

    private ServiceDossier serviceDossier;
    private Dossier currentDossier; // Store the dossier being modified

    @FXML
    public void initialize() {
        serviceDossier = new ServiceDossier();
        // Initialization logic if needed, fields populated by loadDossierData
    }

    // Method to load data from the dossier object passed from AfficherDossierController
    public void loadDossierData(Dossier dossier) {
        if (dossier == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun dossier à modifier n'a été fourni.");
            updateButton.setDisable(true);
            return;
        }
        this.currentDossier = dossier;

        // Populate fields with existing data
        cinPathField.setText(dossier.getCin());
        photoPathField.setText(dossier.getPhoto());
        diplomeBacPathField.setText(dossier.getDiplome_baccalauréat());
        releveNotePathField.setText(dossier.getReleve_note());
        diplomeObtenuPathField.setText(dossier.getDiplome_obtenus());
        lettreMotivationPathField.setText(dossier.getLettre_motivations());
        dossierSantePathField.setText(dossier.getDossier_sante());
        cvPathField.setText(dossier.getCv());
        dateDepotPicker.setValue(dossier.getDatedepot());
    }

    // File upload handlers (same as AjoutDossierController)
    private void handleFileUpload(ActionEvent event, TextField pathField) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier");
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            pathField.setText(selectedFile.getAbsolutePath());
        } else {
             // Keep the existing path if the user cancels
             // pathField.setText(currentDossier.get corresponding field); <--- Add this if needed
        }
    }

    @FXML void handleUploadCin(ActionEvent event) { handleFileUpload(event, cinPathField); }
    @FXML void handleUploadPhoto(ActionEvent event) { handleFileUpload(event, photoPathField); }
    @FXML void handleUploadDiplomeBac(ActionEvent event) { handleFileUpload(event, diplomeBacPathField); }
    @FXML void handleUploadReleveNote(ActionEvent event) { handleFileUpload(event, releveNotePathField); }
    @FXML void handleUploadDiplomeObtenu(ActionEvent event) { handleFileUpload(event, diplomeObtenuPathField); }
    @FXML void handleUploadLettreMotivation(ActionEvent event) { handleFileUpload(event, lettreMotivationPathField); }
    @FXML void handleUploadDossierSante(ActionEvent event) { handleFileUpload(event, dossierSantePathField); }
    @FXML void handleUploadCv(ActionEvent event) { handleFileUpload(event, cvPathField); }

    @FXML
    void handleUpdateDossier(ActionEvent event) {
        if (currentDossier == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun dossier chargé pour la modification.");
            return;
        }

        // Basic Validation (optional: add more specific checks if needed)
        if (cinPathField.getText().isEmpty() || photoPathField.getText().isEmpty() ||
            diplomeBacPathField.getText().isEmpty() || releveNotePathField.getText().isEmpty() ||
            diplomeObtenuPathField.getText().isEmpty() || lettreMotivationPathField.getText().isEmpty() ||
            dossierSantePathField.getText().isEmpty() || cvPathField.getText().isEmpty() ||
            dateDepotPicker.getValue() == null) {

            showAlert(Alert.AlertType.WARNING, "Champs Incomplets", "Veuillez remplir tous les champs et sélectionner tous les fichiers requis.");
            return;
        }

        try {
            // Create an updated Dossier object using the existing IDs
            Dossier updatedDossier = new Dossier(
                    currentDossier.getId_dossier(),       // Keep original dossier ID
                    currentDossier.getId_etudiant(),      // Keep original student ID
                    cinPathField.getText(),
                    photoPathField.getText(),
                    diplomeBacPathField.getText(),
                    releveNotePathField.getText(),
                    diplomeObtenuPathField.getText(),
                    lettreMotivationPathField.getText(),
                    dossierSantePathField.getText(),
                    cvPathField.getText(),
                    dateDepotPicker.getValue()
            );

            System.out.println("Attempting to update Dossier: " + updatedDossier);
            serviceDossier.modifier(updatedDossier);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Dossier mis à jour avec succès!");

            // Close the modification window
            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.close();

            // You might want to refresh the AfficherDossier view if it's still open

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Base de Données", "Échec de la mise à jour du dossier: \n" + e.getMessage());
            System.err.println("Database error while updating dossier:");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue: " + e.getMessage());
            System.err.println("Unexpected error while updating dossier:");
            e.printStackTrace();
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        // Close the modification window without saving
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
        System.out.println("Modification annulée.");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(rootPane.getScene().getWindow());
        alert.showAndWait();
    }
} 