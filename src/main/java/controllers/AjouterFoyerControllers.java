package controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import Services.ServiceFoyer;
import entities.Foyer;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class AjouterFoyerControllers {

    @FXML
    private TextField tf_adresse;

    @FXML
    private TextField tf_capacite;

    @FXML
    private TextField tf_nom;

    @FXML
    private TextField tf_nombre_de_chambre;

    @FXML
    private TextField tf_pays;

    @FXML
    private TextField tf_ville;

    @FXML
    private AnchorPane rootPane;

    private ServiceFoyer serviceFoyer = new ServiceFoyer();

    @FXML
    void AjouterFoyer(ActionEvent event) {
        try {
            // Lire les valeurs entrées par l'utilisateur
            String nom = tf_nom.getText();
            String adresse = tf_adresse.getText();
            String ville = tf_ville.getText();
            String pays = tf_pays.getText();
            int nombreDeChambre = Integer.parseInt(tf_nombre_de_chambre.getText());
            int capacite = Integer.parseInt(tf_capacite.getText());

            // Créer un objet Foyer
            Foyer foyer = new Foyer(nom, adresse, ville, pays, nombreDeChambre, capacite);

            // Ajouter à la base via le service
            serviceFoyer.ajouter(foyer);

            // Alerte succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("Foyer ajouté avec succès !");
            alert.showAndWait();

            // Vider les champs
            tf_nom.clear();
            tf_adresse.clear();
            tf_ville.clear();
            tf_pays.clear();
            tf_nombre_de_chambre.clear();
            tf_capacite.clear();

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer des nombres valides pour nombre de chambres et capacité.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void navigateToModify(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ModifierFoyer.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void navigateToDelete(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/SupprimerFoyer.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        assert tf_adresse != null : "fx:id=\"tf_adresse\" was not injected: check your FXML file 'AjouterFoyer.fxml'.";
        assert tf_capacite != null : "fx:id=\"tf_capacite\" was not injected: check your FXML file 'AjouterFoyer.fxml'.";
        assert tf_nom != null : "fx:id=\"tf_nom\" was not injected: check your FXML file 'AjouterFoyer.fxml'.";
        assert tf_nombre_de_chambre != null : "fx:id=\"tf_nombre_de_chambre\" was not injected: check your FXML file 'AjouterFoyer.fxml'.";
        assert tf_pays != null : "fx:id=\"tf_pays\" was not injected: check your FXML file 'AjouterFoyer.fxml'.";
        assert tf_ville != null : "fx:id=\"tf_ville\" was not injected: check your FXML file 'AjouterFoyer.fxml'.";
    }
}
