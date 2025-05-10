package controllers;

import Services.ServiceRestaurant;
import entities.Restaurant;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ModifierRestaurantController {

    @FXML private TextField tf_id;
    @FXML private TextField tf_nom;
    @FXML private TextField tf_adresse;
    @FXML private TextField tf_ville;
    @FXML private TextField tf_pays;
    @FXML private TextField tf_capaciteTotale;
    @FXML private ComboBox<String> cb_horaireOuverture;
    @FXML private ComboBox<String> cb_horaireFermeture;
    @FXML private TextField tf_telephone;
    @FXML private TextField tf_email;
    @FXML private ImageView uploadedImageView;
    @FXML private Button btnEnregistrer;
    @FXML private Button btnSupprimer;
    @FXML private Button btnAnnuler;
    @FXML private Button btnUploadImage;
    @FXML private StackPane statusMessagePane;
    @FXML private Label statusMessage;

    private final ServiceRestaurant serviceRestaurant = new ServiceRestaurant();
    private Restaurant currentRestaurant;
    private String imagePath;
    private String originalImagePath;

    @FXML
    public void initialize() {
        setupTimeComboBoxes();
        
        // Disable buttons initially until a restaurant is loaded
        btnEnregistrer.setDisable(true);
        btnSupprimer.setDisable(true);
    }
    
    /**
     * Configure les ComboBox pour les horaires avec des valeurs de temps
     */
    private void setupTimeComboBoxes() {
        // Générer des horaires de 00:00 à 23:30 par intervalles de 30 minutes
        for (int hour = 0; hour < 24; hour++) {
            String hourStr = String.format("%02d", hour);
            cb_horaireOuverture.getItems().add(hourStr + ":00");
            cb_horaireOuverture.getItems().add(hourStr + ":30");
            
            cb_horaireFermeture.getItems().add(hourStr + ":00");
            cb_horaireFermeture.getItems().add(hourStr + ":30");
        }
    }

    /**
     * Initialise le formulaire avec les données du restaurant à modifier
     */
    public void setRestaurant(Restaurant restaurant) {
        if (restaurant != null) {
            currentRestaurant = restaurant;
            
            // Set text fields
            tf_id.setText(String.valueOf(restaurant.getIdRestaurant()));
            tf_nom.setText(restaurant.getNom());
            tf_adresse.setText(restaurant.getAdresse());
            tf_ville.setText(restaurant.getVille());
            tf_pays.setText(restaurant.getPays());
            tf_capaciteTotale.setText(String.valueOf(restaurant.getCapaciteTotale()));
            
            // Vérifier et formater les heures correctement
            String horaireOuverture = restaurant.getHoraireOuverture();
            String horaireFermeture = restaurant.getHoraireFermeture();
            
            // S'assurer que les heures sont dans le format correct avant de les définir
            try {
                // Vérifier si les heures sont au bon format
                if (horaireOuverture != null && !horaireOuverture.isEmpty()) {
                    if (horaireOuverture.matches("\\d{2}:\\d{2}")) {
                        cb_horaireOuverture.setValue(horaireOuverture);
                    } else {
                        // Si le format n'est pas correct, utiliser une valeur par défaut
                        cb_horaireOuverture.setValue("08:00");
                    }
                } else {
                    cb_horaireOuverture.setValue("08:00");
                }
                
                if (horaireFermeture != null && !horaireFermeture.isEmpty()) {
                    if (horaireFermeture.matches("\\d{2}:\\d{2}")) {
                        cb_horaireFermeture.setValue(horaireFermeture);
                    } else {
                        // Si le format n'est pas correct, utiliser une valeur par défaut
                        cb_horaireFermeture.setValue("18:00");
                    }
                } else {
                    cb_horaireFermeture.setValue("18:00");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du formatage des heures: " + e.getMessage());
                // Utiliser des valeurs par défaut en cas d'erreur
                cb_horaireOuverture.setValue("08:00");
                cb_horaireFermeture.setValue("18:00");
            }
            
            tf_telephone.setText(restaurant.getTelephone());
            tf_email.setText(restaurant.getEmail());
            
            // Load and display the image
            originalImagePath = restaurant.getImage();
            if (originalImagePath != null && !originalImagePath.isEmpty()) {
                try {
                    String fullPath = "src/main/resources/" + originalImagePath;
                    File file = new File(fullPath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        uploadedImageView.setImage(image);
                    } else {
                        // Use default image if file not found
                        Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                        uploadedImageView.setImage(defaultImage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    // Use default image if loading fails
                    try {
                        Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                        uploadedImageView.setImage(defaultImage);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            
            // Enable buttons
            btnEnregistrer.setDisable(false);
            btnSupprimer.setDisable(false);
        }
    }

    @FXML
    void uploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) uploadedImageView.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            uploadedImageView.setImage(image);
        }
    }

    @FXML
    void enregistrerModifications() {
        try {
            if (!validateFields()) {
                return;
            }

            // Mettre à jour les données du restaurant
            currentRestaurant.setNom(tf_nom.getText().trim());
            currentRestaurant.setAdresse(tf_adresse.getText().trim());
            currentRestaurant.setVille(tf_ville.getText().trim());
            currentRestaurant.setPays(tf_pays.getText().trim());
            currentRestaurant.setCapaciteTotale(Integer.parseInt(tf_capaciteTotale.getText().trim()));
            currentRestaurant.setHoraireOuverture(cb_horaireOuverture.getValue());
            currentRestaurant.setHoraireFermeture(cb_horaireFermeture.getValue());
            currentRestaurant.setTelephone(tf_telephone.getText().trim());
            currentRestaurant.setEmail(tf_email.getText().trim());

            // Traiter l'image si elle a été modifiée
            if (imagePath != null && !imagePath.isEmpty()) {
                String savedImagePath = saveImage();
                currentRestaurant.setImage(savedImagePath);
            }

            serviceRestaurant.modifier(currentRestaurant);
            
            // Afficher message de succès
            showSuccessMessage("Restaurant modifié avec succès!");
            
            // Rediriger vers la liste après 1.5 secondes
            redirectToList(2000);

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur s'est produite: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void supprimerRestaurant() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le restaurant");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce restaurant ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    serviceRestaurant.supprimer(currentRestaurant);
                    showSuccessMessage("Restaurant supprimé avec succès!");
                    redirectToList(1500);
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    void annuler(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListRestaurant.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnAnnuler.getScene().getWindow();
            
            // Transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            stage.setScene(scene);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la vue: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Enregistre l'image sélectionnée dans le dossier des ressources
     */
    private String saveImage() {
        try {
            File sourceFile = new File(imagePath);
            String destDir = "src/main/resources/images/restaurants/";
            new File(destDir).mkdirs(); // Crée le dossier si inexistant
            
            String fileName = "resto_" + System.currentTimeMillis() + getFileExtension(imagePath);
            Path destinationPath = Paths.get(destDir + fileName);
            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Chemin relatif pour l'accès depuis l'application
            return "images/restaurants/" + fileName;
        } catch (IOException ex) {
            showAlert("Erreur", "Erreur lors de l'enregistrement de l'image: " + ex.getMessage(), Alert.AlertType.ERROR);
            ex.printStackTrace();
            return originalImagePath; // Retour à l'image originale en cas d'erreur
        }
    }

    private String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filePath.substring(dotIndex);
    }

    /**
     * Validation des champs du formulaire
     */
    private boolean validateFields() {
        if (tf_nom.getText().trim().isEmpty() ||
                tf_adresse.getText().trim().isEmpty() ||
                tf_ville.getText().trim().isEmpty() ||
                tf_pays.getText().trim().isEmpty() ||
                tf_capaciteTotale.getText().trim().isEmpty() ||
                cb_horaireOuverture.getValue() == null ||
                cb_horaireFermeture.getValue() == null) {

            showAlert("Erreur", "Tous les champs obligatoires doivent être remplis", Alert.AlertType.ERROR);
            return false;
        }

        // Validation de la capacité
        try {
            int capacite = Integer.parseInt(tf_capaciteTotale.getText().trim());
            if (capacite <= 0) {
                showAlert("Erreur", "La capacité doit être un nombre positif", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La capacité doit être un nombre valide", Alert.AlertType.ERROR);
            return false;
        }

        // Validation des horaires
        try {
            String ouvertureStr = cb_horaireOuverture.getValue();
            String fermetureStr = cb_horaireFermeture.getValue();
            
            // Assurez-vous que les formats d'heure sont corrects (HH:mm)
            if (!ouvertureStr.matches("\\d{2}:\\d{2}") || !fermetureStr.matches("\\d{2}:\\d{2}")) {
                showAlert("Erreur", "Format d'heure invalide. Utilisez le format HH:mm", Alert.AlertType.ERROR);
                return false;
            }
            
            LocalTime ouverture = LocalTime.parse(ouvertureStr, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime fermeture = LocalTime.parse(fermetureStr, DateTimeFormatter.ofPattern("HH:mm"));
            
            if (!fermeture.isAfter(ouverture)) {
                showAlert("Erreur", "L'heure de fermeture doit être après l'heure d'ouverture", Alert.AlertType.ERROR);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Format d'heure invalide: " + e.getMessage(), Alert.AlertType.ERROR);
            return false;
        }

        // Validation de l'email s'il est renseigné
        if (!tf_email.getText().trim().isEmpty() && !isValidEmail(tf_email.getText().trim())) {
            showAlert("Erreur", "Format d'email invalide", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    /**
     * Vérifie si l'email est valide
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * Affiche un message d'alerte
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Affiche un message de succès temporaire
     */
    private void showSuccessMessage(String message) {
        statusMessage.setText(message);
        statusMessagePane.setVisible(true);
        statusMessagePane.setManaged(true);
        
        // Change background color to green
        statusMessagePane.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 5px;");
    }
    
    /**
     * Redirige vers la liste des restaurants après un délai
     */
    private void redirectToList(int delayMillis) {
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    javafx.application.Platform.runLater(() -> {
                        try {
                            Parent root = FXMLLoader.load(getClass().getResource("/ListRestaurant.fxml"));
                            Scene scene = new Scene(root);
                            Stage stage = (Stage) btnEnregistrer.getScene().getWindow();
                            
                            // Transition de fondu
                            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
                            fadeIn.setFromValue(0.0);
                            fadeIn.setToValue(1.0);
                            
                            stage.setScene(scene);
                            fadeIn.play();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            },
            delayMillis
        );
    }
} 