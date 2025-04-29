package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFXaous extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger le fichier FXML de gestion des événements
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_evenement.fxml"));
            Parent root = loader.load();

            // Configurer la scène
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/design.css").toExternalForm());

            // Configurer la fenêtre principale
            primaryStage.setTitle("Gestion des Événements");
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

