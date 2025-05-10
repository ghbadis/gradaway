package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML files for both views
            FXMLLoader gestionnaireLoader = new FXMLLoader(getClass().getResource("/Gestionnaire.fxml"));
            Parent gestionnaireRoot = gestionnaireLoader.load();
            Scene gestionnaireScene = new Scene(gestionnaireRoot);

            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent loginRoot = loginLoader.load();
            Scene loginScene = new Scene(loginRoot);

            // Initially show the login view
            primaryStage.setTitle("GradAway - Inscription");
            primaryStage.setScene(loginScene);
            primaryStage.show();

            // Optionally, add logic to switch between scenes if needed
            // Example: primaryStage.setScene(gestionnaireScene);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
