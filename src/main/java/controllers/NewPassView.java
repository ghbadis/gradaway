package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.Group;
import utils.MyDatabase;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewPassView {
    @FXML
    private TextField newPass;
    @FXML
    private TextField confNewPass;
    @FXML
    private ImageView togglePasswordIcon1;
    @FXML
    private ImageView togglePasswordIcon;
    @FXML
    private Group back1;
    @FXML
    private Group back;
    @FXML
    private Text welcome;

    private Connection connection;
    private String storedPassword = "";

    @FXML
    public void initialize() {
        // Set password fields to be masked by default
        newPass.setPromptText("••••••••");
        confNewPass.setPromptText("••••••••");

        // Add click handlers for password toggle icons
        togglePasswordIcon.setOnMouseClicked(event -> togglePasswordVisibility(newPass, togglePasswordIcon));
        togglePasswordIcon1.setOnMouseClicked(event -> togglePasswordVisibility(confNewPass, togglePasswordIcon1));
    }

    private void togglePasswordVisibility(TextField passwordField, ImageView toggleIcon) {
        if (passwordField.getPromptText().equals("••••••••")) {
            // Sauvegarder le mot de passe actuel
            storedPassword = passwordField.getText();
            // Afficher le mot de passe en clair
            passwordField.setText(storedPassword);
            passwordField.setPromptText("");
        } else {
            // Masquer le mot de passe
            passwordField.setText("");
            passwordField.setPromptText("••••••••");
        }
    }

    public NewPassView() {
        try {
            connection = MyDatabase.getInstance().getCnx();
        } catch (Exception e) {
            System.err.println("Error establishing database connection: " + e.getMessage());
        }
    }

    @FXML
    public void passUpdate() {
        String password = newPass.getText();
        String confirmPassword = confNewPass.getText();

        if (password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Erreur", "Les mots de passe ne correspondent pas");
            return;
        }

        try {
            String query = "UPDATE user SET mdp = ? WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, password); // Dans un cas réel, il faudrait hasher le mot de passe
            preparedStatement.setString(2, OptViewcontroller.getCurrentEmail());

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                showAlert("Succès", "Mot de passe mis à jour avec succès");
                // Navigate to login page
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);

                    // Get the current stage from any node in the current scene
                    Stage currentStage = null;
                    if (newPass != null && newPass.getScene() != null) {
                        currentStage = (Stage) newPass.getScene().getWindow();
                    } else if (confNewPass != null && confNewPass.getScene() != null) {
                        currentStage = (Stage) confNewPass.getScene().getWindow();
                    }

                    if (currentStage != null) {
                        currentStage.setScene(scene);
                        currentStage.centerOnScreen();
                        currentStage.show();
                    } else {
                        // If we can't get the current stage, create a new one
                        Stage newStage = new Stage();
                        newStage.setScene(scene);
                        newStage.centerOnScreen();
                        newStage.show();
                    }
                } catch (IOException e) {
                    System.err.println("Error loading login view: " + e.getMessage());
                    showAlert("Erreur", "Erreur lors de la navigation vers la page de connexion");
                }
            } else {
                showAlert("Erreur", "Erreur lors de la mise à jour du mot de passe");
            }
        } catch (SQLException e) {
            System.err.println("Database error during password update: " + e.getMessage());
            showAlert("Erreur", "Erreur lors de la mise à jour du mot de passe");
        }
    }

    @FXML
    public void back() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/enterOPT-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) newPass.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 