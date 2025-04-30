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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class AfficherDossierController {

    @FXML private AnchorPane rootPane;
    @FXML private Label cinLabel;
    @FXML private Label photoLabel;
    @FXML private Label diplomeBacLabel;
    @FXML private Label releveNoteLabel;
    @FXML private Label diplomeObtenuLabel;
    @FXML private Label lettreMotivationLabel;
    @FXML private Label dossierSanteLabel;
    @FXML private Label cvLabel;
    @FXML private Label dateDepotLabel;
    @FXML private Label statusLabel; // To show messages like "Dossier non trouvé"
    @FXML private Button modifierButton; // Injected button

    private ServiceDossier serviceDossier;
    private int currentEtudiantId = -1;
    private Dossier displayedDossier; // Store the currently displayed dossier

    @FXML
    public void initialize() {
        serviceDossier = new ServiceDossier();
        statusLabel.setText(""); // Clear status initially
        modifierButton.setVisible(false); // Hide button initially
        // Data loading is triggered by setEtudiantId
    }

    public void setEtudiantId(int id) {
        this.currentEtudiantId = id;
        System.out.println("AfficherDossierController: Received Etudiant ID: " + this.currentEtudiantId);
        loadDossierData();
    }

    private void loadDossierData() {
        if (currentEtudiantId <= 0) {
            statusLabel.setText("Erreur: ID Étudiant invalide.");
            modifierButton.setVisible(false);
            displayedDossier = null;
            return;
        }

        try {
            displayedDossier = serviceDossier.recupererParEtudiantId(currentEtudiantId);

            if (displayedDossier != null) {
                statusLabel.setText(""); // Clear status if found
                populateFields(displayedDossier);
                modifierButton.setVisible(true); // Show button if dossier found
            } else {
                statusLabel.setText("Aucun dossier trouvé pour cet étudiant.");
                // Optionally clear fields or leave them as "-"
                clearFields();
                modifierButton.setVisible(false); // Hide button if no dossier found
                displayedDossier = null;
            }
        } catch (SQLException e) {
            statusLabel.setText("Erreur base de données lors de la récupération du dossier.");
            showAlert(Alert.AlertType.ERROR, "Erreur Base de Données", "Impossible de charger les données du dossier: \n" + e.getMessage());
            System.err.println("Database error loading dossier:");
            e.printStackTrace();
            modifierButton.setVisible(false);
            displayedDossier = null;
        }
    }

    // Handler for the Modifier button
    @FXML
    void handleModifierDossier(ActionEvent event) {
        if (displayedDossier == null) {
            showAlert(Alert.AlertType.WARNING, "Action Impossible", "Aucun dossier n'est chargé pour la modification.");
            return;
        }

        try {
            System.out.println("AfficherDossierController: Opening ModifierDossier view.");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierDossier.fxml"));
            Parent root = loader.load();

            ModifierDossierController modifierController = loader.getController();
            modifierController.loadDossierData(displayedDossier); // Pass the current dossier

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Mon Dossier");
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();

            // Optional: Close the AfficherDossier window when opening ModifierDossier
            // Stage currentStage = (Stage) modifierButton.getScene().getWindow();
            // currentStage.close();

            stage.showAndWait(); // Use showAndWait if you want to wait for modification before refreshing

            // After the modification window is closed, refresh the data in this view
            System.out.println("AfficherDossierController: Refreshing data after modification window closed.");
            loadDossierData();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Chargement FXML", "Impossible de charger la vue 'Modifier Dossier'.");
            System.err.println("Error loading ModifierDossier.fxml:");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur s'est produite en ouvrant la vue de modification.");
            System.err.println("Unexpected error opening ModifierDossier view:");
            e.printStackTrace();
        }
    }

    private void populateFields(Dossier dossier) {
        cinLabel.setText(dossier.getCin() != null ? dossier.getCin() : "N/A");
        photoLabel.setText(dossier.getPhoto() != null ? dossier.getPhoto() : "N/A");
        diplomeBacLabel.setText(dossier.getDiplome_baccalauréat() != null ? dossier.getDiplome_baccalauréat() : "N/A");
        releveNoteLabel.setText(dossier.getReleve_note() != null ? dossier.getReleve_note() : "N/A");
        diplomeObtenuLabel.setText(dossier.getDiplome_obtenus() != null ? dossier.getDiplome_obtenus() : "N/A");
        lettreMotivationLabel.setText(dossier.getLettre_motivations() != null ? dossier.getLettre_motivations() : "N/A");
        dossierSanteLabel.setText(dossier.getDossier_sante() != null ? dossier.getDossier_sante() : "N/A");
        cvLabel.setText(dossier.getCv() != null ? dossier.getCv() : "N/A");
        if (dossier.getDatedepot() != null) {
            dateDepotLabel.setText(dossier.getDatedepot().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
        } else {
            dateDepotLabel.setText("N/A");
        }
    }

    private void clearFields() {
        cinLabel.setText("-");
        photoLabel.setText("-");
        diplomeBacLabel.setText("-");
        releveNoteLabel.setText("-");
        diplomeObtenuLabel.setText("-");
        lettreMotivationLabel.setText("-");
        dossierSanteLabel.setText("-");
        cvLabel.setText("-");
        dateDepotLabel.setText("-");
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        // Ensure rootPane is not null before getting the window
        if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
             alert.initOwner(rootPane.getScene().getWindow());
        }
        alert.showAndWait();
    }
} 