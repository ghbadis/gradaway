package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Gestionnaire.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Gestionnaire des Entretiens et Experts");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

}