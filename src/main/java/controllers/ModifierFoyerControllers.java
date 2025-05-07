package controllers;

import Services.ServiceFoyer;
import entities.Foyer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ModifierFoyerControllers {

    @FXML private TextField tf_id;
    @FXML private TextField tf_nom;
    @FXML private TextField tf_adresse;
    @FXML private TextField tf_ville;
    @FXML private TextField tf_pays;
    @FXML private TextField tf_nombre_de_chambre;
    @FXML private TextField tf_capacite;
    @FXML private TextField tf_image;
    @FXML private Button btn_modifier;
    @FXML private Button btn_supprimer;
    @FXML private ImageView imageView;

    private final ServiceFoyer serviceFoyer = new ServiceFoyer();
    private Foyer currentFoyer;

    @FXML
    public void initialize() {
        // Disable buttons initially
        if (btn_modifier != null) btn_modifier.setDisable(true);
        if (btn_supprimer != null) btn_supprimer.setDisable(true);
        
        // Make ID field read-only
        if (tf_id != null) tf_id.setEditable(false);
    }

    public void initData(Foyer foyer) {
        if (foyer != null) {
            currentFoyer = foyer;
            
            // Set text fields
            if (tf_id != null) tf_id.setText(String.valueOf(foyer.getIdFoyer()));
            if (tf_nom != null) tf_nom.setText(foyer.getNom());
            if (tf_adresse != null) tf_adresse.setText(foyer.getAdresse());
            if (tf_ville != null) tf_ville.setText(foyer.getVille());
            if (tf_pays != null) tf_pays.setText(foyer.getPays());
            if (tf_nombre_de_chambre != null) tf_nombre_de_chambre.setText(String.valueOf(foyer.getNombreDeChambre()));
            if (tf_capacite != null) tf_capacite.setText(String.valueOf(foyer.getCapacite()));
            if (tf_image != null) tf_image.setText(foyer.getImage());
            
            // Load and display the image
            if (imageView != null && foyer.getImage() != null && !foyer.getImage().isEmpty()) {
                try {
                    Image image = new Image(foyer.getImage());
                    imageView.setImage(image);
                } catch (Exception e) {
                    // Use default image if loading fails
                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-foyer.png")));
                }
            }
            
            // Enable buttons
            if (btn_modifier != null) btn_modifier.setDisable(false);
            if (btn_supprimer != null) btn_supprimer.setDisable(false);
            
            // Enable text fields except ID
            setFieldsEditable(true);
        }
    }

    @FXML
    void enregistrerModifications() {
        try {
            if (!validateFields()) {
                return;
            }

            currentFoyer.setNom(tf_nom.getText().trim());
            currentFoyer.setAdresse(tf_adresse.getText().trim());
            currentFoyer.setVille(tf_ville.getText().trim());
            currentFoyer.setPays(tf_pays.getText().trim());
            currentFoyer.setNombreDeChambre(Integer.parseInt(tf_nombre_de_chambre.getText().trim()));
            currentFoyer.setCapacite(Integer.parseInt(tf_capacite.getText().trim()));
            currentFoyer.setImage(tf_image.getText().trim());

            serviceFoyer.modifier(currentFoyer);
            showAlert("Succès", "Foyer modifié avec succès", Alert.AlertType.INFORMATION);
            
            // Close the window
            closeWindow();

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void supprimerFoyer() {
        try {
            serviceFoyer.supprimer(currentFoyer);
            showAlert("Succès", "Foyer supprimé avec succès", Alert.AlertType.INFORMATION);
            
            // Close the window
            closeWindow();
            
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) btn_modifier.getScene().getWindow();
        stage.close();
    }

    private boolean validateFields() {
        if (tf_nom.getText().trim().isEmpty() ||
                tf_adresse.getText().trim().isEmpty() ||
                tf_ville.getText().trim().isEmpty() ||
                tf_pays.getText().trim().isEmpty() ||
                tf_nombre_de_chambre.getText().trim().isEmpty() ||
                tf_capacite.getText().trim().isEmpty()) {

            showAlert("Erreur", "Tous les champs doivent être remplis", Alert.AlertType.ERROR);
            return false;
        }

        try {
            int chambres = Integer.parseInt(tf_nombre_de_chambre.getText().trim());
            int capacite = Integer.parseInt(tf_capacite.getText().trim());

            if (chambres <= 0 || capacite <= 0) {
                showAlert("Erreur", "Les valeurs numériques doivent être positives", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Valeurs numériques invalides", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void setFieldsEditable(boolean editable) {
        // Check each field for null before setting editable
        if (tf_nom != null) tf_nom.setEditable(editable);
        if (tf_adresse != null) tf_adresse.setEditable(editable);
        if (tf_ville != null) tf_ville.setEditable(editable);
        if (tf_pays != null) tf_pays.setEditable(editable);
        if (tf_nombre_de_chambre != null) tf_nombre_de_chambre.setEditable(editable);
        if (tf_capacite != null) tf_capacite.setEditable(editable);
        if (tf_image != null) tf_image.setEditable(editable);
        if (tf_id != null) tf_id.setEditable(false); // ID should never be editable
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}