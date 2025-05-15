package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.WebServer;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Démarrer le serveur web
            WebServer.start();

            FXMLLoader loader = new FXMLLoader();
            // Load the login FXML file
                loader.setLocation(getClass().getResource("/login-view.fxml "));
            Parent root = loader.load();

            // Configurer la scène
            Scene scene = new Scene(root);

            // Configurer la fenêtre
            primaryStage.setTitle("Gestion des Dossiers - Login");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(600);
            primaryStage.setMinHeight(400);
            primaryStage.setResizable(true);

            // Centrer la fenêtre
            primaryStage.centerOnScreen();

            // Afficher la fenêtre
            primaryStage.show();

            // Arrêter le serveur web à la fermeture de l'application
            primaryStage.setOnCloseRequest(event -> WebServer.stop());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} 