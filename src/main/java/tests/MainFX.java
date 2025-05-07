package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class MainFX extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        try {
//            Parent root = FXMLLoader.load(getClass().getResource("/MesReservationsRestaurant.fxml"));
//           Parent root = FXMLLoader.load(getClass().getResource("/ListFoyer.fxml"));
            Parent root = FXMLLoader.load(getClass().getResource("/MesReservationsFoyer.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setTitle("Ajouter un Foyer");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showModifierFoyerView() {
        try {
            Parent root = FXMLLoader.load(MainFX.class.getResource("/ModifierFoyer.fxml"));
            primaryStage.setTitle("Modifier un Foyer");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showSupprimerFoyerView() {
        try {
            Parent root = FXMLLoader.load(MainFX.class.getResource("/SupprimerFoyer.fxml"));
            primaryStage.setTitle("Supprimer un Foyer");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
   public static void main(String[] args) {
       launch(args);
    }

}



