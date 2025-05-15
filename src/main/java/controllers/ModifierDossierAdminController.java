package controllers;

import entities.Dossier;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Services.ServiceDossier;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ModifierDossierAdminController implements Initializable {

    @FXML private TextField photoField;
    @FXML private TextField cinField;
    @FXML private TextField diplomeBacField;
    @FXML private TextField releveNoteField;
    @FXML private TextField diplomeObtenusField;
    @FXML private TextField lettreMotivationsField;
    @FXML private TextField dossierSanteField;
    @FXML private TextField cvField;

    private Dossier dossier;
    private ServiceDossier serviceDossier;
    private Stage stage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceDossier = new ServiceDossier();
    }

    public void setDossier(Dossier dossier) {
        this.dossier = dossier;
        // Remplir les champs avec les données du dossier
        photoField.setText(dossier.getPhoto());
        cinField.setText(dossier.getCin());
        diplomeBacField.setText(dossier.getDiplome_baccalauréat());
        releveNoteField.setText(dossier.getReleve_note());
        diplomeObtenusField.setText(dossier.getDiplome_obtenus());
        lettreMotivationsField.setText(dossier.getLettre_motivations());
        dossierSanteField.setText(dossier.getDossier_sante());
        cvField.setText(dossier.getCv());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handlePhotoChoose() {
        handleFileChoose(photoField, "Choisir une photo", "Images", "*.png", "*.jpg", "*.jpeg", "*.gif");
    }

    @FXML
    private void handleCinChoose() {
        handleFileChoose(cinField, "Choisir un fichier CIN", "Documents", "*.pdf", "*.doc", "*.docx");
    }

    @FXML
    private void handleDiplomeBacChoose() {
        handleFileChoose(diplomeBacField, "Choisir un fichier Diplôme Bac", "Documents", "*.pdf", "*.doc", "*.docx");
    }

    @FXML
    private void handleReleveNoteChoose() {
        handleFileChoose(releveNoteField, "Choisir un fichier Relevé Notes", "Documents", "*.pdf", "*.doc", "*.docx");
    }

    @FXML
    private void handleDiplomeObtenusChoose() {
        handleFileChoose(diplomeObtenusField, "Choisir un fichier Diplômes Obtenus", "Documents", "*.pdf", "*.doc", "*.docx");
    }

    @FXML
    private void handleLettreMotivationsChoose() {
        handleFileChoose(lettreMotivationsField, "Choisir un fichier Lettre Motivation", "Documents", "*.pdf", "*.doc", "*.docx");
    }

    @FXML
    private void handleDossierSanteChoose() {
        handleFileChoose(dossierSanteField, "Choisir un fichier Dossier Santé", "Documents", "*.pdf", "*.doc", "*.docx");
    }

    @FXML
    private void handleCvChoose() {
        handleFileChoose(cvField, "Choisir un fichier CV", "Documents", "*.pdf", "*.doc", "*.docx");
    }

    private void handleFileChoose(TextField field, String title, String description, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(description, extensions));
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            field.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Mettre à jour le dossier avec les nouvelles valeurs
            dossier.setPhoto(photoField.getText());
            dossier.setCin(cinField.getText());
            dossier.setDiplome_baccalauréat(diplomeBacField.getText());
            dossier.setReleve_note(releveNoteField.getText());
            dossier.setDiplome_obtenus(diplomeObtenusField.getText());
            dossier.setLettre_motivations(lettreMotivationsField.getText());
            dossier.setDossier_sante(dossierSanteField.getText());
            dossier.setCv(cvField.getText());

            // Sauvegarder les modifications
            serviceDossier.modifier(dossier);

            // Afficher un message de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le dossier a été modifié avec succès.");

            // Fermer la fenêtre
            stage.close();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification du dossier: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 