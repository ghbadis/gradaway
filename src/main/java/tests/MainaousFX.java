package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainaousFX extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Charger le fichier FXML principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_evenement.fxml"));
            Parent root = loader.load();

            // Configurer la scène
            Scene scene = new Scene(root);

            // Charger le CSS
            scene.getStylesheets().add(getClass().getResource("/design.css").toExternalForm());


            // Configurer la fenêtre principale
            primaryStage.setTitle("Gestion des Événements");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'interface : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
            }