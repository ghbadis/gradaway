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
            // Charger le fichier FXML de la première vue
            Parent root = FXMLLoader.load(getClass().getResource("/dashboard.fxml"));
            Scene scene = new Scene(root);
            
            // Configurer la fenêtre principale
            primaryStage.setTitle("GradAway - Inscription");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
