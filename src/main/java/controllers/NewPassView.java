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
import utils.PasswordHasher;
import javafx.scene.control.PasswordField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NewPassView {
    @FXML
    private PasswordField newPass;
    @FXML
    private TextField newPassVisible;
    @FXML
    private PasswordField confNewPass;
    @FXML
    private TextField confNewPassVisible;
    @FXML
    private Group back1;
    @FXML
    private Group back;
    @FXML
    private Text welcome;

    private Connection connection;
    @FXML
    private ImageView togglePasswordIcon2;
    @FXML
    private ImageView togglePasswordIcon1;

    private boolean isNewPassVisible = false;
    private boolean isConfNewPassVisible = false;

    @FXML
    public void initialize() {
        // Initialize password visibility toggle
        if (togglePasswordIcon1 != null) {
            togglePasswordIcon1.setOnMouseClicked(event -> toggleNewPassVisibility());
        }
        if (togglePasswordIcon2 != null) {
            togglePasswordIcon2.setOnMouseClicked(event -> toggleConfNewPassVisibility());
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

        if (password.length() < 6) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 6 caractères");
            return;
        }

        try {
            // Hasher le nouveau mot de passe
            String hashedPassword = PasswordHasher.hashPassword(password);

            String query = "UPDATE user SET mdp = ? WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, hashedPassword);
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

    private void toggleNewPassVisibility() {
        if (isNewPassVisible) {
            // Hide password
            newPass.setText(newPassVisible.getText());
            newPass.setVisible(true);
            newPassVisible.setVisible(false);
        } else {
            // Show password
            newPassVisible.setText(newPass.getText());
            newPassVisible.setVisible(true);
            newPass.setVisible(false);
        }
        isNewPassVisible = !isNewPassVisible;
    }

    private void toggleConfNewPassVisibility() {
        if (isConfNewPassVisible) {
            // Hide password
            confNewPass.setText(confNewPassVisible.getText());
            confNewPass.setVisible(true);
            confNewPassVisible.setVisible(false);
        } else {
            // Show password
            confNewPassVisible.setText(confNewPass.getText());
            confNewPassVisible.setVisible(true);
            confNewPass.setVisible(false);
        }
        isConfNewPassVisible = !isConfNewPassVisible;
    }
} 