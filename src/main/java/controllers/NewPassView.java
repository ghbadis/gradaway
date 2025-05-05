package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class NewPassView {
    @FXML
    private TextField newPass;
    @FXML
    private TextField confNewPass;
    @FXML
    private Button updateButton;

    @FXML
    public void passUpdate() {
        String password = newPass.getText();
        String confirmPassword = confNewPass.getText();

        if (password.equals(confirmPassword)) {
            // Here you should update the password in your database
            // After successful update, navigate to login page
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) updateButton.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void back() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forgetPass-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) updateButton.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 