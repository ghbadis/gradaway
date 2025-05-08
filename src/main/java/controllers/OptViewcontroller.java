package controllers;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.MyDatabase;
import utils.EmailService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OptViewcontroller {
    @FXML
    private TextField tfemailpassword;

    private Connection connection;
    private static String currentOTP;
    private static String currentEmail;
    @FXML
    private Group back1;
    @FXML
    private Text backlogin;

    public OptViewcontroller() {
        try {
            connection = MyDatabase.getInstance().getCnx();
        } catch (Exception e) {
            System.err.println("Error establishing database connection: " + e.getMessage());
        }
    }

    @FXML
    public void back(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) back1.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void toEnterOTP() {
        String email = tfemailpassword.getText();
        if (email.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer votre email");
            return;
        }

        try {
            // Verify if email exists in database
            String query = "SELECT id FROM user WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Generate and send OTP
                currentOTP = EmailService.generateOTP();
                currentEmail = email;
                EmailService.sendOTPEmail(email, currentOTP);

                // Open the enterOPT-view.fxml interface
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/enterOPT-view.fxml"));
                Parent root = loader.load();
                
                Stage currentStage = (Stage) tfemailpassword.getScene().getWindow();
                currentStage.close();
                
                Stage otpStage = new Stage();
                Scene scene = new Scene(root);
                otpStage.setScene(scene);
                otpStage.setTitle("Enter OTP - GradAway");
                otpStage.setResizable(true);
                otpStage.centerOnScreen();
                otpStage.show();
                
                showAlert("Succès", "Un code OTP a été envoyé à votre email");
            } else {
                // Email doesn't exist in database
                showAlert("Erreur", "Votre email n'est pas valide");
            }
        } catch (SQLException e) {
            System.err.println("Database error during email verification: " + e.getMessage());
            showAlert("Erreur", "Erreur lors de la vérification de l'email");
        } catch (IOException e) {
            System.err.println("Error loading enterOPT view: " + e.getMessage());
            showAlert("Erreur", "Erreur lors de l'ouverture de la page OTP");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static String getCurrentOTP() {
        return currentOTP;
    }

    public static String getCurrentEmail() {
        return currentEmail;
    }
}
