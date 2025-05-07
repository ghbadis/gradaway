package controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class EnterOPTViewcontroller {
    @FXML
    private Group back1;
    @FXML
    private TextField input5;
    @FXML
    private TextField input4;
    @FXML
    private TextField input3;
    @FXML
    private TextField input2;
    @FXML
    private TextField input1;
    @FXML
    private Button verifyButton;

    @FXML
    public void back(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/otp-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) back1.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void verifyOTP() {
        String enteredOTP = input1.getText() + input2.getText() + input3.getText() + input4.getText() + input5.getText();
        String correctOTP = OptViewcontroller.getCurrentOTP();

        if (enteredOTP.equals(correctOTP)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/newPass-view.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) verifyButton.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Erreur", "Code OTP incorrect");
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
