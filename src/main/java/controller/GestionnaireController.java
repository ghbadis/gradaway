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
    public void initialize() {
        try {
            // Load images if they exist
            if (logoImageView != null) {
                Image logoImage = new Image(getClass().getResourceAsStream("/images/logo.png"));
                if (logoImage != null) {
                    logoImageView.setImage(logoImage);
                }
            }
            
            if (expertIcon != null) {
                Image expertImage = new Image(getClass().getResourceAsStream("/images/expert.png"));
                if (expertImage != null) {
                    expertIcon.setImage(expertImage);
                }
            }
            
            if (entretienIcon != null) {
                Image entretienImage = new Image(getClass().getResourceAsStream("/images/entretien.png"));
                if (entretienImage != null) {
                    entretienIcon.setImage(entretienImage);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading images: " + e.getMessage());
            e.printStackTrace();
        }

        // Set button text
        btnGestionExperts.setText("Gestion des Experts");
        btnGestionEntretiens.setText("Gestion des Entretiens");

        // Set button actions
        btnGestionExperts.setOnAction(event -> handleGestionExperts());
        btnGestionEntretiens.setOnAction(event -> handleGestionEntretiens());
    }

    @FXML
    private void handleGestionExperts() {
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

    @FXML
    private void handleGestionEntretiens() {
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
} 