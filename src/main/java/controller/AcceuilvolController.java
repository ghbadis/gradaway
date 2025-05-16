package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class AcceuilvolController {

    @FXML
    private ImageView logoImageView;

    @FXML
    private Button btnAdminVols;

    @FXML
    private Button btnAdminReservations;

    @FXML
    private Button btnUserVols;

    @FXML
    private void ouvrirVols(ActionEvent event) {
        ouvrirFenetre("/views/AfficherVols.fxml", "Gestion des Vols");
    }

    @FXML
    private void ouvrirReservations(ActionEvent event) {
        ouvrirFenetre("/views/AfficherReservationVol.fxml", "Gestion des Réservations");
    }


    private void ouvrirFenetre(String fxmlPath, String titre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titre);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'interface : " + fxmlPath);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void initialize() {
        // Exemple pour charger un logo si besoin :
        // logoImageView.setImage(new Image(getClass().getResourceAsStream("/images/ton_logo.png")));
    }
}