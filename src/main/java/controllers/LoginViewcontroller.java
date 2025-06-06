package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import utils.MyDatabase;
import utils.PasswordHasher;
import utils.SessionManager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginViewcontroller {
    @FXML
    private TextField loginEmail;
    
    @FXML
    private PasswordField loginPasswd;

    private Connection connection;
    @FXML
    private TextField VloginPasswd;
    @FXML
    private Text welcome;
    @FXML
    private Button signUpButton;
    @FXML
    private javafx.scene.image.ImageView togglePasswordIcon;
    @FXML
    private Text forgotPass;

    private boolean isPasswordVisible = false;

    public LoginViewcontroller() {
        System.out.println("LoginViewcontroller constructor called");
        try {
            connection = MyDatabase.getInstance().getCnx();
            if (connection == null) {
                System.err.println("Database connection is null");
            } else {
                System.out.println("Database connection established successfully");
            }
        } catch (Exception e) {
            System.err.println("Error establishing database connection: " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        System.out.println("Initialize method called");
        System.out.println("loginEmail reference: " + loginEmail);
        System.out.println("loginPasswd reference: " + loginPasswd);
        
        if (loginEmail == null) {
            System.err.println("loginEmail is null - FXML injection failed");
        }
        if (loginPasswd == null) {
            System.err.println("loginPasswd is null - FXML injection failed");
        }

        // Initialize password visibility toggle
        if (togglePasswordIcon != null) {
            togglePasswordIcon.setOnMouseClicked(event -> togglePasswordVisibility());
        }
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            loginPasswd.setText(VloginPasswd.getText());
            loginPasswd.setVisible(true);
            VloginPasswd.setVisible(false);
        } else {
            // Show password
            VloginPasswd.setText(loginPasswd.getText());
            VloginPasswd.setVisible(true);
            loginPasswd.setVisible(false);
        }
        isPasswordVisible = !isPasswordVisible;
    }

    @FXML
    private void handleLogin() {
        System.out.println("LoginViewcontroller: handleLogin called");
        System.out.println("Current email value: " + (loginEmail != null ? loginEmail.getText() : "null"));
        System.out.println("Current password value: " + (loginPasswd != null ? "****" : "null"));
        
        if (loginEmail == null || loginPasswd == null) {
            System.err.println("LoginViewcontroller: Fields are null in handleLogin");
            showAlert("Erreur", "Les champs ne sont pas initialisés correctement");
            return;
        }

        String email = loginEmail.getText();
        String password = loginPasswd.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Erreur", "Veuillez remplir tous les champs");
            return;
        }

        try {
            System.out.println("LoginViewcontroller: Attempting to query database for user: " + email);
            String query = "SELECT id, role, mdp FROM user WHERE email = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            
            System.out.println("LoginViewcontroller: Executing query: " + query.replace("?", "'" + email + "'"));
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("mdp");
                if (PasswordHasher.verifyPassword(password, hashedPassword)) {
                    int userId = resultSet.getInt("id");
                    String role = resultSet.getString("role");
                    System.out.println("LoginViewcontroller: Login successful for user ID: " + userId + " with role: " + role);
                    
                    // Stocker les informations de l'utilisateur dans SessionManager
                    SessionManager.getInstance().setUserInfo(userId, email, role);
                    System.out.println("LoginViewcontroller: User info stored in SessionManager - ID: " + userId + ", Email: " + email + ", Role: " + role);
                    
                    Stage loginStage = (Stage) loginEmail.getScene().getWindow();
                    loginStage.close();

                    if ("admin".equalsIgnoreCase(role)) {
                        System.out.println("Opening Admin interface");
                        openAdminInterface(userId);
                    } else if ("etudiant".equalsIgnoreCase(role)) {
                        System.out.println("Opening Etudiant interface");
                        openAccueil(userId);
                    } else {
                        System.err.println("Role non reconnu: '" + role + "'");
                        showAlert("Erreur", "Rôle non reconnu: " + role);
                    }
                } else {
                    System.out.println("LoginViewcontroller: Login failed - invalid password for user: " + email);
                    showAlert("Erreur", "Email ou mot de passe incorrect");
                }
            } else {
                System.out.println("LoginViewcontroller: Login failed - user not found: " + email);
                showAlert("Erreur", "Email ou mot de passe incorrect");
            }
        } catch (SQLException e) {
            System.err.println("LoginViewcontroller: Database error during login: " + e.getMessage());
            showAlert("Erreur", "Erreur lors de la connexion à la base de données");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("LoginViewcontroller: Error loading view: " + e.getMessage());
            showAlert("Erreur", "Erreur lors de l'ouverture de la vue.");
            e.printStackTrace();
        }
    }

    private void openAccueil(int userId) throws IOException {
        System.out.println("LoginViewcontroller: Opening Accueil view for User ID: " + userId);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
        Parent root = loader.load();

        Acceuilcontroller acceuilController = loader.getController();
        acceuilController.setUserId(userId);

        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Accueil - GradAway");
        stage.setMinWidth(1200);
        stage.setMinHeight(700);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    private void openAdminInterface(int userId) throws IOException {
        System.out.println("LoginViewcontroller: Opening Admin interface for User ID: " + userId);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AcceuilAdmin.fxml"));
        Parent root = loader.load();

        AcceuilAdmincontroller adminController = loader.getController();
        // You can set any necessary data in the admin controller here

        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Admin Dashboard - GradAway");
        stage.setMinWidth(1200);
        stage.setMinHeight(700);
        stage.setResizable(true);
        stage.centerOnScreen();
        stage.show();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleSignUp() {
        System.out.println("LoginViewcontroller: handleSignUp called");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SignUpView1.fxml"));
            Parent root = loader.load();

            Stage loginStage = (Stage) signUpButton.getScene().getWindow();
            loginStage.close();

            Stage signUpStage = new Stage();
            Scene scene = new Scene(root);
            signUpStage.setScene(scene);
            signUpStage.setTitle("Sign Up - Step 1");
            signUpStage.setResizable(true);
            signUpStage.centerOnScreen();
            signUpStage.show();

        } catch (IOException e) {
            System.err.println("LoginViewcontroller: Error loading SignUpView1.fxml: " + e.getMessage());
            showAlert("Erreur", "Erreur lors de l'ouverture de la page d'inscription.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/otp-view.fxml"));
            Parent root = loader.load();
            
            Stage currentStage = (Stage) forgotPass.getScene().getWindow();
            currentStage.close();
            
            Stage otpStage = new Stage();
            Scene scene = new Scene(root);
            otpStage.setScene(scene);
            otpStage.setTitle("Reset Password - GradAway");
            otpStage.setResizable(true);
            otpStage.centerOnScreen();
            otpStage.show();
        } catch (IOException e) {
            System.err.println("LoginViewcontroller: Error loading OTP view: " + e.getMessage());
            showAlert("Erreur", "Erreur lors de l'ouverture de la page de réinitialisation du mot de passe.");
            e.printStackTrace();
        }
    }
}
