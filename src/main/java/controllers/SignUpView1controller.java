package controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;

public class SignUpView1controller {
    @FXML
    private TextField tfprenom;
    @FXML
    private PasswordField tfmdp;
    @FXML
    private TextField tfnom;
    @FXML
    private TextField tfemail;
    @FXML
    private Text login;
    @FXML
    private javafx.scene.image.ImageView togglePasswordIcon;
    @FXML
    private TextField visiblePasswordField;

    private boolean isPasswordVisible = false;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    @FXML
    private Group backlogin;

    @FXML
    public void initialize() {
        // Initialize password visibility toggle
        if (togglePasswordIcon != null) {
            togglePasswordIcon.setOnMouseClicked(event -> togglePasswordVisibility());
        }
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            tfmdp.setText(visiblePasswordField.getText());
            tfmdp.setVisible(true);
            visiblePasswordField.setVisible(false);
        } else {
            // Show password
            visiblePasswordField.setText(tfmdp.getText());
            visiblePasswordField.setVisible(true);
            tfmdp.setVisible(false);
        }
        isPasswordVisible = !isPasswordVisible;
    }

    @FXML
    public void back(Event event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/LoginView.fxml"));
            Stage stage = (Stage) backlogin.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page précédente");
        }
    }

    @FXML
    public void login(Event event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/login-view.fxml"));
            Stage stage = (Stage) login.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page de connexion");
        }
    }

    @FXML
    public void continuer(ActionEvent actionEvent) {
        if (validateFields()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUpView2.fxml"));
                Parent root = loader.load();
                SignUpView2controller controller = loader.getController();
                controller.setUserData(tfnom.getText(), tfprenom.getText(), tfemail.getText(), tfmdp.getText());
                
                Stage stage = (Stage) tfnom.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                showAlert("Erreur", "Impossible de charger la page suivante");
            }
        }
    }

    private boolean validateFields() {
        if (tfnom.getText().isEmpty() || tfprenom.getText().isEmpty() || 
            tfemail.getText().isEmpty() || tfmdp.getText().isEmpty()) {
            showAlert("Erreur", "Tous les champs sont obligatoires");
            return false;
        }

        if (!EMAIL_PATTERN.matcher(tfemail.getText()).matches()) {
            showAlert("Erreur", "Format d'email invalide");
            return false;
        }

        if (tfmdp.getText().length() < 6) {
            showAlert("Erreur", "Le mot de passe doit contenir au moins 6 caractères");
            return false;
        }

        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
