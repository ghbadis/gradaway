package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class GestionnaireController {
    @FXML
    private ImageView logoImageView;
    @FXML
    private ImageView expertIcon;
    @FXML
    private ImageView entretienIcon;
    @FXML
    private Button btnGestionExperts;
    @FXML
    private Button btnGestionEntretiens;
    @FXML
    private Button btnDemanderEntretien;

    @FXML
    public void initialize() {
        // Load images
        logoImageView.setImage(new Image(getClass().getResourceAsStream("/images/logo.png")));
        expertIcon.setImage(new Image(getClass().getResourceAsStream("/images/expert.png")));
        entretienIcon.setImage(new Image(getClass().getResourceAsStream("/images/entretien.png")));

        // Set button text
        btnGestionExperts.setText("Gestion des Experts");
        btnGestionEntretiens.setText("Gestion des Entretiens");
        btnDemanderEntretien.setText("Demander un Entretien");

        // Set button actions
        btnGestionExperts.setOnAction(event -> openListeExperts());
        btnGestionEntretiens.setOnAction(event -> openListeEntretiens());
        btnDemanderEntretien.setOnAction(event -> openDemanderEntretien());
    }

    private void openListeExperts() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ListeExperts.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Liste des Experts");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openListeEntretiens() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ListeEntretiens.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Liste des Entretiens");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDemanderEntretien() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/DemanderEntretien.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("Demander un Entretien");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 