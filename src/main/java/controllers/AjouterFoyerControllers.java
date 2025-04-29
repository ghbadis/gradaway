package controllers;

import Services.ServiceFoyer;
import entities.Foyer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

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
    private ImageView imageUploaded;
    
    @FXML
    private Button btnListeReservation;

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
            String image = null;
            if (imageUploaded.getImage() != null) {
                image = imageUploaded.getImage().getUrl();
            }

            // Créer un objet Foyer
            Foyer foyer = new Foyer(nom, adresse, ville, pays, nombreDeChambre, capacite, image);

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
            imageUploaded.setImage(new Image("@placeholder/placeholder.png")); // Reset to placeholder

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
    void addimages(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");

        // Limiter les types de fichiers visibles
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String imagePath = selectedFile.toURI().toString(); // chemin URI utilisable

            // Afficher l'image dans ImageView
            Image image = new Image(imagePath);
            imageUploaded.setImage(image); // Mettre l'image dans l'ImageView
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
    void ListFoyer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    void navigateToListeReservation(ActionEvent event) {
        try {
            System.out.println("Navigating to Liste Reservation...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeReservation.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Add fade transition for smooth navigation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation vers la liste des réservations: " + e.getMessage(), Alert.AlertType.ERROR);
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
        assert imageUploaded != null : "fx:id=\"imageUploaded\" was not injected: check your FXML file 'AjouterFoyer.fxml'.";
        btnListeReservation.setOnAction(e -> navigateToListeReservation(e));
    }
}
