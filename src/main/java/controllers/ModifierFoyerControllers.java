package controllers;

import Services.ServiceFoyer;
import entities.Foyer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Optional;
import javafx.scene.control.ButtonType;

public class ModifierFoyerControllers {
    @FXML private TextField tf_id;
    @FXML private TextField tf_nom;
    @FXML private TextField tf_adresse;
    @FXML private TextField tf_ville;
    @FXML private TextField tf_pays;
    @FXML private TextField tf_nombre_de_chambre;
    @FXML private TextField tf_capacite;
    @FXML private TextField tf_image;
    @FXML private Button btn_modifier;
    @FXML private Button btn_supprimer;
    @FXML private Button btnAnnuler;
    @FXML private Button btnUploadImage;
    @FXML private ImageView imageView;
    @FXML private StackPane statusMessagePane;
    @FXML private Label statusMessage;

    private final ServiceFoyer serviceFoyer = new ServiceFoyer();
    private Foyer currentFoyer;

    @FXML
    public void initialize() {
        // Disable buttons initially
        if (btn_modifier != null) btn_modifier.setDisable(true);
        if (btn_supprimer != null) btn_supprimer.setDisable(true);
        
        // Make ID field read-only
        if (tf_id != null) tf_id.setEditable(false);
        
        // Hide status message initially
        if (statusMessagePane != null) statusMessagePane.setVisible(false);
    }

    public void initData(Foyer foyer) {
        if (foyer != null) {
            currentFoyer = foyer;
            
            // Set text fields
            if (tf_id != null) tf_id.setText(String.valueOf(foyer.getIdFoyer()));
            if (tf_nom != null) tf_nom.setText(foyer.getNom());
            if (tf_adresse != null) tf_adresse.setText(foyer.getAdresse());
            if (tf_ville != null) tf_ville.setText(foyer.getVille());
            if (tf_pays != null) tf_pays.setText(foyer.getPays());
            if (tf_nombre_de_chambre != null) tf_nombre_de_chambre.setText(String.valueOf(foyer.getNombreDeChambre()));
            if (tf_capacite != null) tf_capacite.setText(String.valueOf(foyer.getCapacite()));
            if (tf_image != null) tf_image.setText(foyer.getImage());
            
            // Load and display the image
            if (imageView != null && foyer.getImage() != null && !foyer.getImage().isEmpty()) {
                try {
                    Image image = new Image(foyer.getImage());
                    imageView.setImage(image);
                } catch (Exception e) {
                    // Use default image if loading fails
                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-foyer.png")));
                }
            }
            
            // Enable buttons
            if (btn_modifier != null) btn_modifier.setDisable(false);
            if (btn_supprimer != null) btn_supprimer.setDisable(false);
            
            // Enable text fields except ID
            setFieldsEditable(true);
        }
    }

    @FXML
    void enregistrerModifications() {
        try {
            if (!validateFields()) {
                return;
            }

            currentFoyer.setNom(tf_nom.getText().trim());
            currentFoyer.setAdresse(tf_adresse.getText().trim());
            currentFoyer.setVille(tf_ville.getText().trim());
            currentFoyer.setPays(tf_pays.getText().trim());
            currentFoyer.setNombreDeChambre(Integer.parseInt(tf_nombre_de_chambre.getText().trim()));
            currentFoyer.setCapacite(Integer.parseInt(tf_capacite.getText().trim()));
            
            // Gérer l'image si le champ existe
            if (tf_image != null && !tf_image.getText().isEmpty()) {
                currentFoyer.setImage(tf_image.getText().trim());
            }

            serviceFoyer.modifier(currentFoyer);
            
            // Afficher le message de succès avec animation
            if (statusMessagePane != null && statusMessage != null) {
                statusMessage.setText("Modifications enregistrées avec succès");
                statusMessagePane.setVisible(true);
                
                // Cacher le message après 3 secondes
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        Platform.runLater(() -> {
                            statusMessagePane.setVisible(false);
                            retourToList();
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                // Fallback si les éléments d'interface ne sont pas disponibles
                showAlert("Succès", "Modifications enregistrées avec succès", Alert.AlertType.INFORMATION);
                closeWindow();
            }

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void supprimerFoyer() {
        try {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation de suppression");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce foyer ?");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                serviceFoyer.supprimer(currentFoyer);
                showAlert("Succès", "Foyer supprimé avec succès", Alert.AlertType.INFORMATION);
                retourToList();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        Stage stage = (Stage) imageView.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            try {
                // Créer le dossier d'images s'il n'existe pas
                Path imagesDir = Paths.get("src/main/resources/images");
                if (!Files.exists(imagesDir)) {
                    Files.createDirectories(imagesDir);
                }
                
                // Copier l'image dans le dossier des ressources
                String fileName = "foyer_" + System.currentTimeMillis() + "_" + selectedFile.getName();
                Path targetPath = imagesDir.resolve(fileName);
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                
                // Mettre à jour l'image dans l'interface
                Image image = new Image(targetPath.toUri().toString());
                imageView.setImage(image);
                
                // Mettre à jour le chemin de l'image dans l'objet Foyer
                String imagePath = "images/" + fileName;
                if (tf_image != null) {
                    tf_image.setText(imagePath);
                } else {
                    // Si le champ tf_image n'existe pas, mettre à jour directement l'objet Foyer
                    if (currentFoyer != null) {
                        currentFoyer.setImage(imagePath);
                    }
                }
                
                showAlert("Succès", "Image téléchargée avec succès", Alert.AlertType.INFORMATION);
                
            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors du téléchargement de l'image: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    void annuler() {
        retourToList();
    }
    
    private void closeWindow() {
        Stage stage = (Stage) tf_id.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Retourne à la liste des foyers dans la même fenêtre
     */
    private void retourToList() {
        try {
            // Charger la vue ListFoyer
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyer.fxml"));
            Parent root = loader.load();
            
            // Récupérer la fenêtre actuelle et changer sa scène
            Stage stage = (Stage) tf_id.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Configurer la fenêtre en plein écran
            stage.setMaximized(true);
            
            // Ajouter une transition de fondu pour une navigation plus fluide
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation", Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Navigation vers la page d'accueil
     */
    @FXML
    private void navigateToAccueil() {
        navigateTo("/AcceuilAdmin.fxml", "Accueil Admin");
    }
    
    /**
     * Navigation vers la page Admin
     */
    @FXML
    private void navigateToAdmin() {
        navigateTo("/AdminUser.fxml", "Gestion des Utilisateurs");
    }
    
    /**
     * Navigation vers la page Dossier
     */
    @FXML
    private void navigateToDossier() {
        navigateTo("/AdminDossier.fxml", "Gestion des Dossiers");
    }
    
    /**
     * Navigation vers la page Université
     */
    @FXML
    private void navigateToUniversite() {
        navigateTo("/adminuniversite.fxml", "Gestion des Universités");
    }
    
    /**
     * Navigation vers la page Entretien
     */
    @FXML
    private void navigateToEntretien() {
        navigateTo("/Gestionnaire.fxml", "Gestion des Entretiens");
    }
    
    /**
     * Navigation vers la page Événement
     */
    @FXML
    private void navigateToEvenement() {
        navigateTo("/gestion_evenement.fxml", "Gestion des Événements");
    }
    
    /**
     * Navigation vers la page Hébergement
     */
    @FXML
    private void navigateToHebergement() {
        navigateTo("/ListFoyer.fxml", "Liste des Foyers");
    }
    
    /**
     * Navigation vers la page Restaurant
     */
    @FXML
    private void navigateToRestaurant() {
        navigateTo("/AjouterRestaurant.fxml", "Gestion des Restaurants");
    }
    
    /**
     * Navigation vers la page Vols
     */
    @FXML
    private void navigateToVols() {

    }
    
    /**
     * Méthode de déconnexion
     */
    @FXML
    private void logout() {
        navigateTo("/login-view.fxml", "Login - GradAway");
    }
    
    /**
     * Méthode générique pour la navigation
     */
    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = (Stage) tf_id.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            
            // Configurer la fenêtre en plein écran
            stage.setMaximized(true);
            
            // Ajouter une transition de fondu pour une navigation plus fluide
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation", Alert.AlertType.ERROR);
        }
    }

    private boolean validateFields() {
        if (tf_nom.getText().trim().isEmpty() ||
                tf_adresse.getText().trim().isEmpty() ||
                tf_ville.getText().trim().isEmpty() ||
                tf_pays.getText().trim().isEmpty() ||
                tf_nombre_de_chambre.getText().trim().isEmpty() ||
                tf_capacite.getText().trim().isEmpty()) {

            showAlert("Erreur", "Tous les champs doivent être remplis", Alert.AlertType.ERROR);
            return false;
        }

        try {
            int chambres = Integer.parseInt(tf_nombre_de_chambre.getText().trim());
            int capacite = Integer.parseInt(tf_capacite.getText().trim());

            if (chambres <= 0 || capacite <= 0) {
                showAlert("Erreur", "Les valeurs numériques doivent être positives", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Valeurs numériques invalides", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void setFieldsEditable(boolean editable) {
        // Check each field for null before setting editable
        if (tf_nom != null) tf_nom.setEditable(editable);
        if (tf_adresse != null) tf_adresse.setEditable(editable);
        if (tf_ville != null) tf_ville.setEditable(editable);
        if (tf_pays != null) tf_pays.setEditable(editable);
        if (tf_nombre_de_chambre != null) tf_nombre_de_chambre.setEditable(editable);
        if (tf_capacite != null) tf_capacite.setEditable(editable);
        if (tf_image != null) tf_image.setEditable(editable);
        if (tf_id != null) tf_id.setEditable(false); // ID should never be editable
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}