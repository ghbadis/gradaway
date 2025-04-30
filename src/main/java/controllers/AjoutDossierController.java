package controllers;

import Services.ServiceDossier;
import entities.Dossier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class AjoutDossierController {

    @FXML private TextField cinPathField;
    @FXML private TextField photoPathField;
    @FXML private TextField diplomeBacPathField;
    @FXML private TextField releveNotePathField;
    @FXML private TextField diplomeObtenuPathField;
    @FXML private TextField lettreMotivationPathField;
    @FXML private TextField dossierSantePathField;
    @FXML private TextField cvPathField;
    @FXML private DatePicker dateDepotPicker;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;
    @FXML private Button viewDossierButton;
    @FXML private AnchorPane rootPane; // Assuming the root element has fx:id="rootPane"

    private int currentEtudiantId = -1; // Placeholder for the student ID
    private ServiceDossier serviceDossier; // Declare the service

    // Updated method to check for existing dossier
    public void setEtudiantId(int id) {
        this.currentEtudiantId = id;
        System.out.println("Current Etudiant ID set to: " + this.currentEtudiantId);

        if (id <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID étudiant invalide.");
            submitButton.setDisable(true);
            viewDossierButton.setDisable(true);
        } else {
            // Check if dossier exists for this student
            checkExistingDossier(id);
        }
    }

    private void checkExistingDossier(int etudiantId) {
        try {
            Dossier existingDossier = serviceDossier.recupererParEtudiantId(etudiantId);
            if (existingDossier != null) {
                System.out.println("Dossier already exists for student ID: " + etudiantId + ". Disabling submit button.");
                submitButton.setDisable(true); // Disable submit if dossier exists
                viewDossierButton.setDisable(false); // Enable view button
                // Optional: Show a message indicating dossier exists

            } else {
                System.out.println("No existing dossier found for student ID: " + etudiantId + ". Enabling submit button.");
                submitButton.setDisable(false); // Enable submit if no dossier exists
                viewDossierButton.setDisable(true); // Disable view button if no dossier (as there's nothing to view yet)
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Base de Données", "Impossible de vérifier l'existence d'un dossier existant.\n" + e.getMessage());
            System.err.println("Database error while checking for existing dossier:");
            e.printStackTrace();
            // Disable both buttons in case of error
            submitButton.setDisable(true);
            viewDossierButton.setDisable(true);
        }
    }

    @FXML
    public void initialize() {
        dateDepotPicker.setValue(LocalDate.now());
        serviceDossier = new ServiceDossier(); // Instantiate the service
        // Disable submit button initially until a valid ID is set
        // This depends on whether setEtudiantId is called before or after initialize
        // If setEtudiantId can be called later, we might need to enable it there.
        submitButton.setDisable(true); // Start disabled
        viewDossierButton.setDisable(true);
    }

    // Generic handler for file chooser
    private void handleFileUpload(ActionEvent event, TextField pathField) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir un fichier");
        // You can add extension filters if needed
        // fileChooser.getExtensionFilters().addAll(
        //     new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
        //     new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        // );

        // Get the stage from the event source
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            pathField.setText(selectedFile.getAbsolutePath());
        } else {
            // Optional: handle case where no file was selected
            // pathField.setText(""); // Clear if previously set
        }
    }

    // Specific handlers calling the generic one
    @FXML void handleUploadCin(ActionEvent event) { handleFileUpload(event, cinPathField); }
    @FXML void handleUploadPhoto(ActionEvent event) { handleFileUpload(event, photoPathField); }
    @FXML void handleUploadDiplomeBac(ActionEvent event) { handleFileUpload(event, diplomeBacPathField); }
    @FXML void handleUploadReleveNote(ActionEvent event) { handleFileUpload(event, releveNotePathField); }
    @FXML void handleUploadDiplomeObtenu(ActionEvent event) { handleFileUpload(event, diplomeObtenuPathField); }
    @FXML void handleUploadLettreMotivation(ActionEvent event) { handleFileUpload(event, lettreMotivationPathField); }
    @FXML void handleUploadDossierSante(ActionEvent event) { handleFileUpload(event, dossierSantePathField); }
    @FXML void handleUploadCv(ActionEvent event) { handleFileUpload(event, cvPathField); }

    @FXML
    void handleSubmit(ActionEvent event) {
        // Basic Validation
        if (currentEtudiantId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Soumission", "ID de l'étudiant non défini ou invalide.");
            return;
        }
        // Double check dossier doesn't exist just before submitting (optional but safer)
        try {
             if (serviceDossier.recupererParEtudiantId(currentEtudiantId) != null) {
                 showAlert(Alert.AlertType.ERROR, "Erreur de Soumission", "Un dossier existe déjà pour cet étudiant.");
                 submitButton.setDisable(true);
                 return;
             }
        } catch (SQLException e) {
             showAlert(Alert.AlertType.ERROR, "Erreur Base de Données", "Impossible de vérifier l'existence d'un dossier avant la soumission.\n" + e.getMessage());
             e.printStackTrace();
             return;
        }

        if (cinPathField.getText().isEmpty() || photoPathField.getText().isEmpty() ||
            diplomeBacPathField.getText().isEmpty() || releveNotePathField.getText().isEmpty() ||
            diplomeObtenuPathField.getText().isEmpty() || lettreMotivationPathField.getText().isEmpty() ||
            dossierSantePathField.getText().isEmpty() || cvPathField.getText().isEmpty() ||
            dateDepotPicker.getValue() == null) {

            showAlert(Alert.AlertType.WARNING, "Champs Incomplets", "Veuillez remplir tous les champs et sélectionner tous les fichiers requis.");
            return;
        }

        try {
            // Create Dossier object
            Dossier newDossier = new Dossier(
                    currentEtudiantId,
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

            System.out.println("Attempting to submit Dossier: " + newDossier);

            serviceDossier.ajouter(newDossier);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Dossier ajouté avec succès!");
            clearForm();
            // After successful submission, disable submit and enable view
            submitButton.setDisable(true);
            viewDossierButton.setDisable(false);

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Base de Données", "Échec de l'ajout du dossier à la base de données: \n" + e.getMessage());
            System.err.println("Database error while adding dossier:");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue: " + e.getMessage());
            System.err.println("Unexpected error while adding dossier:");
            e.printStackTrace();
        }
    }

    // Action handler for the new button
    @FXML
    void handleViewDossier(ActionEvent event) {
        if (currentEtudiantId <= 0) {
            showAlert(Alert.AlertType.WARNING, "Action Impossible", "L'ID de l'étudiant n'est pas défini.");
            return;
        }

        try {
            System.out.println("AjoutDossierController: Opening AfficherDossier view for Etudiant ID: " + currentEtudiantId);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDossier.fxml"));
            Parent root = loader.load();

            AfficherDossierController afficherController = loader.getController();
            afficherController.setEtudiantId(currentEtudiantId);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Mon Dossier");
            stage.setMinWidth(900);
            stage.setMinHeight(700);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

            // Optionally close the current AjoutDossier window
            // Stage currentStage = (Stage) viewDossierButton.getScene().getWindow();
            // currentStage.close();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Chargement FXML", "Impossible de charger la vue 'Afficher Dossier'.");
            System.err.println("Error loading AfficherDossier.fxml:");
            e.printStackTrace();
        } catch (Exception e) {
             showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur s'est produite en ouvrant la vue du dossier.");
             System.err.println("Unexpected error opening AfficherDossier view:");
             e.printStackTrace();
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        // Optional: Confirm before clearing or closing
        clearForm();
        // Optionally close the window
         Stage stage = (Stage) cancelButton.getScene().getWindow();
         stage.close();
        System.out.println("Ajout annulé.");
    }

    private void clearForm() {
        cinPathField.clear();
        photoPathField.clear();
        diplomeBacPathField.clear();
        releveNotePathField.clear();
        diplomeObtenuPathField.clear();
        lettreMotivationPathField.clear();
        dossierSantePathField.clear();
        cvPathField.clear();
        dateDepotPicker.setValue(LocalDate.now()); // Reset date to today
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        // Check if rootPane and its scene are initialized before setting owner
        if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
            alert.initOwner(rootPane.getScene().getWindow());
        } else {
            System.err.println("Warning: Could not set owner for alert dialog. Root pane or scene not ready.");
        }
        alert.showAndWait();
    }
} 