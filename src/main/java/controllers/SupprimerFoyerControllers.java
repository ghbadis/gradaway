package controllers;

import Services.ServiceFoyer;
import entities.Foyer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class SupprimerFoyerControllers {

    @FXML
    private TextField tf_id;  // TextField to enter the ID of the foyer to be deleted

    private ServiceFoyer serviceFoyer = new ServiceFoyer();  // Service to manage Foyer operations

    @FXML
    public void modifierfoyer(ActionEvent event) {
        try {
            // Retrieve the ID entered by the user
            String foyerIdText = tf_id.getText();
            if (foyerIdText.isEmpty()) {
                showAlert("Erreur", "L'ID du foyer est obligatoire.", Alert.AlertType.ERROR);
                return;
            }

            int foyerId = Integer.parseInt(foyerIdText);  // Convert the ID to an integer

            // Create a Foyer object using the ID
            Foyer foyer = new Foyer();
            foyer.setIdFoyer(foyerId);  // Set the ID of the foyer

            // Call the service to delete the foyer
            boolean isDeleted = serviceFoyer.supprimer(foyer);  // Pass the Foyer object to the service

            if (isDeleted) {
                showAlert("Succès", "Foyer supprimé avec succès.", Alert.AlertType.INFORMATION);
                tf_id.clear();  // Clear the TextField after successful deletion
            } else {
                showAlert("Erreur", "Foyer non trouvé avec cet ID.", Alert.AlertType.ERROR);
            }

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer un ID valide.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de la suppression du foyer.", Alert.AlertType.ERROR);
        }
    }

    // Method to display an alert with a custom message
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
