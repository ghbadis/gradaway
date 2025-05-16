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

    // Dashboard navigation buttons
    @FXML private Button accueilButton;
    @FXML private Button userButton;
    @FXML private Button dossierButton;
    @FXML private Button universiteButton;
    @FXML private Button entretienButton;
    @FXML private Button evenementButton;
    @FXML private Button hebergementButton;
    @FXML private Button restaurantButton;
    @FXML private Button volsButton;
    @FXML private Button logoutButton;

    private ServiceFoyer serviceFoyer = new ServiceFoyer();

    @FXML
    void AjouterFoyer(ActionEvent event) {
        try {
            // Lire les valeurs entrées par l'utilisateur
            String nom = tf_nom.getText().trim();
            String adresse = tf_adresse.getText().trim();
            String ville = tf_ville.getText().trim();
            String pays = tf_pays.getText().trim();
            String nombreDeChambreText = tf_nombre_de_chambre.getText().trim();
            String capaciteText = tf_capacite.getText().trim();
            
            // Vérifier que tous les champs texte sont remplis
            if (nom.isEmpty() || adresse.isEmpty() || ville.isEmpty() || pays.isEmpty() || 
                nombreDeChambreText.isEmpty() || capaciteText.isEmpty()) {
                showAlert("Champs obligatoires", "Tous les champs sont obligatoires. Veuillez remplir tous les champs.", Alert.AlertType.WARNING);
                return;
            }
            
            // Convertir les valeurs numériques
            int nombreDeChambre = Integer.parseInt(nombreDeChambreText);
            int capacite = Integer.parseInt(capaciteText);
            
            // Vérifier que les valeurs numériques sont positives
            if (nombreDeChambre <= 0 || capacite <= 0) {
                showAlert("Valeurs invalides", "Le nombre de chambres et la capacité doivent être des nombres positifs.", Alert.AlertType.WARNING);
                return;
            }
            
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
            
            // Réinitialiser les champs du formulaire pour permettre l'ajout d'un autre foyer
            resetFormFields();

        } catch (NumberFormatException e) {
            showAlert("Format incorrect", "Veuillez entrer des nombres valides pour le nombre de chambres et la capacité.", Alert.AlertType.ERROR);
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
            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Utiliser la méthode qui configure la scène en plein écran
            Scene scene = ListFoyerControllers.loadFXMLAndSetFullScreen(stage);
            
            // Appliquer la scène à la fenêtre
            stage.setScene(scene);
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
        setupNavigationButtons();
    }
    
    private void setupNavigationButtons() {
        accueilButton.setOnAction(this::onAccueilButtonClick);
        userButton.setOnAction(this::onUserButtonClick);
        dossierButton.setOnAction(this::ondossierButtonClick);
        universiteButton.setOnAction(this::onuniversiteButtonClick);
        entretienButton.setOnAction(this::onentretienButtonClick);
        evenementButton.setOnAction(this::onevenementButtonClick);
        hebergementButton.setOnAction(this::onhebergementButtonClick);
        restaurantButton.setOnAction(this::onrestaurantButtonClick);
        volsButton.setOnAction(this::onvolsButtonClick);
        logoutButton.setOnAction(this::onlogoutButtonClick);
    }

    private void navigateToScene(String fxmlPath, ActionEvent event, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            // Add fade transition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onAccueilButtonClick(ActionEvent event) {
        navigateToScene("/AcceuilAdmin.fxml", event, "Accueil Admin");
    }

    @FXML
    private void onUserButtonClick(ActionEvent event) {
        navigateToScene("/AdminUser.fxml", event, "Gestion des Utilisateurs");
    }

    @FXML
    private void ondossierButtonClick(ActionEvent event) {
        navigateToScene("/AdminDossier.fxml", event, "Gestion des Dossiers");
    }

    @FXML
    private void onuniversiteButtonClick(ActionEvent event) {
        navigateToScene("/adminuniversite.fxml", event, "Gestion des Universités");
    }

    @FXML
    private void onentretienButtonClick(ActionEvent event) {
        navigateToScene("/Gestionnaire.fxml", event, "Gestion des Entretiens");
    }

    @FXML
    private void onevenementButtonClick(ActionEvent event) {
        navigateToScene("/gestion_evenement.fxml", event, "Gestion des Événements");
    }

    @FXML
    private void onhebergementButtonClick(ActionEvent event) {
        navigateToScene("/AjouterFoyer.fxml", event, "Gestion des Foyers");
    }

    @FXML
    private void onrestaurantButtonClick(ActionEvent event) {
        navigateToScene("/AjouterRestaurant.fxml", event, "Gestion des Restaurants");
    }

    @FXML
    private void onvolsButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Accueilvol.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) volsButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Vols");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
           // showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture", e.getMessage());
        }
    }

    @FXML
    private void onlogoutButtonClick(ActionEvent event) {
        navigateToScene("/login-view.fxml", event, "Login - GradAway");
    }

    /**
     * Réinitialise tous les champs du formulaire après un ajout réussi
     */
    private void resetFormFields() {
        // Vider tous les champs texte
        tf_nom.clear();
        tf_adresse.clear();
        tf_ville.clear();
        tf_pays.clear();
        tf_nombre_de_chambre.clear();
        tf_capacite.clear();
        
        // Réinitialiser l'image avec l'image par défaut
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/placeholder/placeholder.png"));
            imageUploaded.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image par défaut: " + e.getMessage());
        }
        
        // Remettre le focus sur le premier champ
        tf_nom.requestFocus();
    }
}
