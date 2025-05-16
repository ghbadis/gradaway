package controllers;

import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import Services.ServiceUniversite;
import entities.Universite;

public class AjouterUniversiteController {

    @FXML private Button retourButton;
    @FXML private Button selectPhotoButton;
    @FXML private Button ajouterButton;
    @FXML private Button fermerButton;
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
    
    @FXML private TextField nomField;
    @FXML private TextField villeField;
    @FXML private TextField adresseField;
    @FXML private TextField domaineField;
    @FXML private TextField fraisField;

    @FXML private Label nomErrorLabel;
    @FXML private Label villeErrorLabel;
    @FXML private Label adresseErrorLabel;
    @FXML private Label domaineErrorLabel;
    @FXML private Label fraisErrorLabel;
    @FXML private Label photoPathLabel;

    @FXML private ImageView photoPreview;

    private String selectedPhotoPath;
    private ExecutorService executorService;
    
    @FXML
    public void initialize() {
        try {
            // Initialize the executor service for background tasks
            executorService = Executors.newSingleThreadExecutor();
        } catch (Exception e) {
            System.err.println("Erreur d'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetourButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recupereruniversite.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retourButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Universités");
            stage.show();
        } catch (IOException e) {
            // Handle error
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur de navigation", 
                     "Impossible de retourner à la liste des universités", 
                     e.getMessage());
        }
    }

    @FXML
    private void handleSelectPhotoButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) selectPhotoButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                // Create resources/images directory if it doesn't exist
                File imagesDir = new File("src/main/resources/images/universities");
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs();
                }
                
                // Generate unique filename based on timestamp and original filename
                String originalFilename = selectedFile.getName();
                String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
                String uniqueFilename = "university_" + System.currentTimeMillis() + extension;
                
                // Create destination file
                File destinationFile = new File(imagesDir, uniqueFilename);
                
                // Copy image to resources
                java.nio.file.Files.copy(
                    selectedFile.toPath(),
                    destinationFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                
                // Set the path to be saved in the database - use relative path that works with the resource loading
                selectedPhotoPath = "images/universities/" + uniqueFilename;
                
                // Show the selected image and filename
                photoPathLabel.setText(originalFilename);
                Image image = new Image(destinationFile.toURI().toString());
                photoPreview.setImage(image);
                
                System.out.println("Image saved at: " + destinationFile.getAbsolutePath());
                System.out.println("Path to be stored in database: " + selectedPhotoPath);
                
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Erreur", 
                         "Impossible de copier l'image", 
                         "Erreur: " + e.getMessage());
                
                // Fallback to the old behavior if copying fails
                selectedPhotoPath = selectedFile.getAbsolutePath();
                photoPathLabel.setText(selectedFile.getName());
                Image image = new Image(selectedFile.toURI().toString());
                photoPreview.setImage(image);
            }
        }
    }

    @FXML
    private void handleAjouterButton() {
        boolean isValid = validateInputs();
        
        if (isValid) {
            try {
                // Create university object
                Universite universite = createUniversiteFromInputs();
                
                // Save university to database (would be implemented in a service)
                saveUniversite(universite);
                
                // Show success message
                showAlert(AlertType.INFORMATION, "Université ajoutée", 
                          "L'université a été ajoutée avec succès !", null);
                
                // Return to university list
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/recupereruniversite.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ajouterButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Liste des Universités");
                stage.show();
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Erreur", 
                          "Une erreur est survenue lors de l'ajout", e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    private Universite createUniversiteFromInputs() {
        String nom = nomField.getText().trim();
        String ville = villeField.getText().trim();
        String adresse_universite = adresseField.getText().trim();
        String domaine = domaineField.getText().trim();
        double frais = Double.parseDouble(fraisField.getText().trim());
        
        // Create entities.Universite object using constructor with no ID
        return new Universite(nom, ville, adresse_universite, domaine, frais, selectedPhotoPath);
    }
    
    private void saveUniversite(Universite universite) {
        try {
            // Use ServiceUniversite to save the university to the database
            ServiceUniversite serviceUniversite = new ServiceUniversite();
            serviceUniversite.ajouter(universite);
            System.out.println("University successfully added to the database: " + universite);
        } catch (Exception e) {
            System.err.println("Error saving university to database: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to save university: " + e.getMessage(), e);
        }
    }
    
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private boolean validateInputs() {
        boolean isValid = true;
        
        // Validate Nom
        if (nomField.getText().trim().isEmpty()) {
            nomErrorLabel.setText("Le nom est obligatoire");
            nomErrorLabel.setVisible(true);
            isValid = false;
        } else {
            nomErrorLabel.setVisible(false);
        }
        
        // Validate Ville
        if (villeField.getText().trim().isEmpty()) {
            villeErrorLabel.setText("La ville est obligatoire");
            villeErrorLabel.setVisible(true);
            isValid = false;
        } else {
            villeErrorLabel.setVisible(false);
        }
        
        // Validate Adresse
        if (adresseField.getText().trim().isEmpty()) {
            adresseErrorLabel.setText("L'adresse est obligatoire");
            adresseErrorLabel.setVisible(true);
            isValid = false;
        } else {
            adresseErrorLabel.setVisible(false);
        }
        
        // Validate Domaine
        if (domaineField.getText().trim().isEmpty()) {
            domaineErrorLabel.setText("Le domaine est obligatoire");
            domaineErrorLabel.setVisible(true);
            isValid = false;
        } else {
            domaineErrorLabel.setVisible(false);
        }
        
        // Validate Frais
        try {
            if (fraisField.getText().trim().isEmpty()) {
                fraisErrorLabel.setText("Les frais sont obligatoires");
                fraisErrorLabel.setVisible(true);
                isValid = false;
            } else {
                double frais = Double.parseDouble(fraisField.getText().trim());
                if (frais < 0) {
                    fraisErrorLabel.setText("Les frais doivent être positifs");
                    fraisErrorLabel.setVisible(true);
                    isValid = false;
                } else {
                    fraisErrorLabel.setVisible(false);
                }
            }
        } catch (NumberFormatException e) {
            fraisErrorLabel.setText("Veuillez entrer un nombre valide");
            fraisErrorLabel.setVisible(true);
            isValid = false;
        }
        
        // Validate Photo
        if (selectedPhotoPath == null || selectedPhotoPath.isEmpty()) {
            showAlert(AlertType.WARNING, "Photo manquante", 
                     "Aucune photo n'a été sélectionnée", 
                     "Veuillez sélectionner une photo pour l'université.");
            isValid = false;
        }
        
        return isValid;
    }

    @FXML
    private void handleFermerButton() {
        Stage stage = (Stage) fermerButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Cleanup resources when controller is no longer needed
     */
    public void cleanup() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @FXML
    private void handleAccueilButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AcceuilAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) accueilButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil Admin");
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir l'accueil admin: " + e.getMessage(), null);
        }
    }

    @FXML
    private void handleUserButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) userButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Utilisateurs");
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des utilisateurs: " + e.getMessage(), null);
        }
    }

    @FXML
    private void handleDossierButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDossier.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dossierButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Dossiers");
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des dossiers: " + e.getMessage(), null);
        }
    }

    @FXML
    private void handleUniversiteButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recupereruniversite.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) universiteButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Universités");
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des universités: " + e.getMessage(), null);
        }
    }

    @FXML
    private void handleEntretienButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gestionnaire.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) entretienButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Entretiens");
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des entretiens: " + e.getMessage(), null);
        }
    }

    @FXML
    private void handleEvenementButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_evenement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) evenementButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Événements");
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des événements: " + e.getMessage(), null);
        }
    }

    @FXML
    private void handleHebergementButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFoyer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) hebergementButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Foyers");
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des foyers: " + e.getMessage(), null);
        }
    }

    @FXML
    private void handleRestaurantButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterRestaurant.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) restaurantButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Restaurants");
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des restaurants: " + e.getMessage(), null);
        }
    }

    @FXML
    private void handleVolsButton() {
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture", e.getMessage());
        }
    }

    @FXML
    private void handleLogoutButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - GradAway");
            stage.show();
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur de Déconnexion", "Impossible de se déconnecter: " + e.getMessage(), null);
        }
    }
} 