package controllers;

import Services.ServiceRestaurant;
import entities.Restaurant;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class AjouterRestaurantControllers {

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
    
    // Add error label references 
    @FXML private Label nomErrorLabel;
    @FXML private Label adresseErrorLabel;
    @FXML private Label villeErrorLabel;
    @FXML private Label paysErrorLabel;
    @FXML private Label capaciteErrorLabel;
    @FXML private Label ouvertureErrorLabel;
    @FXML private Label fermetureErrorLabel;
    @FXML private Label telephoneErrorLabel;
    @FXML private Label emailErrorLabel;
    @FXML private Label imageErrorLabel;
    
    // Status message elements
    @FXML private StackPane statusMessagePane;
    @FXML private Label statusMessage;
    @FXML private Button submitButton;

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

    private ServiceRestaurant serviceRestaurant = new ServiceRestaurant();
    private String imagePath;

    @FXML
    void initialize() {
        // Appliquer des effets de survol aux champs
        setupHoverEffects();
        
        // Initialiser l'image par défaut
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
            if (defaultImage != null && !defaultImage.isError()) {
                uploadedImageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            System.err.println("Impossible de charger l'image par défaut: " + e.getMessage());
        }
        
        // Initialiser les ComboBox pour les horaires
        setupTimeComboBoxes();
        
        // Ajouter des validateurs en temps réel
        setupValidators();
        
        // Hide all error labels initially
        hideAllErrorLabels();
    }
    
    /**
     * Masque tous les messages d'erreur au démarrage
     */
    private void hideAllErrorLabels() {
        Label[] errorLabels = {
            nomErrorLabel, adresseErrorLabel, villeErrorLabel, paysErrorLabel,
            capaciteErrorLabel, ouvertureErrorLabel, fermetureErrorLabel,
            telephoneErrorLabel, emailErrorLabel, imageErrorLabel
        };
        
        for (Label label : errorLabels) {
            if (label != null) {
                label.setVisible(false);
                label.setManaged(false);
            }
        }
        
        // Hide status message
        if (statusMessagePane != null) {
            statusMessagePane.setVisible(false);
            statusMessagePane.setManaged(false);
        }
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
        
        // Définir des valeurs par défaut
        cb_horaireOuverture.setValue("08:00");
        cb_horaireFermeture.setValue("18:00");
    }
    
    /**
     * Méthode pour configurer les effets de survol sur les champs
     */
    private void setupHoverEffects() {
        TextField[] fields = {tf_nom, tf_adresse, tf_ville, tf_pays, tf_capaciteTotale, 
                           tf_telephone, tf_email};
        
        for (TextField field : fields) {
            field.setOnMouseEntered(e -> {
                field.setStyle("-fx-background-color: #f0f7ff; -fx-background-radius: 8;");
            });
            field.setOnMouseExited(e -> {
                field.setStyle("-fx-background-radius: 8;");
            });
        }
        
        // Appliquer des effets de survol aux ComboBox
        ComboBox<?>[] comboBoxes = {cb_horaireOuverture, cb_horaireFermeture};
        for (ComboBox<?> comboBox : comboBoxes) {
            comboBox.setOnMouseEntered(e -> {
                comboBox.setStyle("-fx-background-color: #f0f7ff; -fx-background-radius: 8;");
            });
            comboBox.setOnMouseExited(e -> {
                comboBox.setStyle("-fx-background-radius: 8;");
            });
        }
    }
    
    /**
     * Configure les validateurs en temps réel pour les champs
     */
    private void setupValidators() {
        // Validation pour le champ capacité (nombres uniquement)
        tf_capaciteTotale.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tf_capaciteTotale.setText(newValue.replaceAll("[^\\d]", ""));
            }
            // Show error if empty or 0
            boolean isValid = !newValue.isEmpty() && Integer.parseInt(newValue.isEmpty() ? "0" : newValue) > 0;
            capaciteErrorLabel.setVisible(!isValid);
            capaciteErrorLabel.setManaged(!isValid);
            updateFieldStyle(tf_capaciteTotale, isValid);
        });
        
        // Validation pour le champ téléphone (format numérique)
        tf_telephone.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                tf_telephone.setText(newValue.replaceAll("[^\\d]", ""));
            }
            
            // Toujours montrer l'erreur si vide ou moins de 8 chiffres
            boolean isValid = !newValue.isEmpty() && newValue.length() >= 8;
            telephoneErrorLabel.setText(newValue.isEmpty() ? "Le numéro de téléphone est requis" : 
                                      "Le numéro doit contenir au moins 8 chiffres");
            telephoneErrorLabel.setVisible(!isValid);
            telephoneErrorLabel.setManaged(!isValid);
            updateFieldStyle(tf_telephone, isValid);
        });
        
        // Validation pour le champ email (format visuel)
        tf_email.textProperty().addListener((observable, oldValue, newValue) -> {
            // Toujours montrer l'erreur si vide ou format invalide
            boolean isValid = !newValue.isEmpty() && isValidEmail(newValue);
            emailErrorLabel.setText(newValue.isEmpty() ? "L'email est requis" : 
                                  "Format d'email invalide");
            emailErrorLabel.setVisible(!isValid);
            emailErrorLabel.setManaged(!isValid);
            updateFieldStyle(tf_email, isValid);
        });
        
        // Required fields validation
        tf_nom.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValid = !newValue.trim().isEmpty();
            nomErrorLabel.setVisible(!isValid);
            nomErrorLabel.setManaged(!isValid);
            updateFieldStyle(tf_nom, isValid);
        });
        
        tf_adresse.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValid = !newValue.trim().isEmpty();
            adresseErrorLabel.setVisible(!isValid);
            adresseErrorLabel.setManaged(!isValid);
            updateFieldStyle(tf_adresse, isValid);
        });
        
        tf_ville.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValid = !newValue.trim().isEmpty();
            villeErrorLabel.setVisible(!isValid);
            villeErrorLabel.setManaged(!isValid);
            updateFieldStyle(tf_ville, isValid);
        });
        
        tf_pays.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValid = !newValue.trim().isEmpty();
            paysErrorLabel.setVisible(!isValid);
            paysErrorLabel.setManaged(!isValid);
            updateFieldStyle(tf_pays, isValid);
        });
        
        // ComboBox validation
        cb_horaireOuverture.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValid = newValue != null;
            ouvertureErrorLabel.setVisible(!isValid);
            ouvertureErrorLabel.setManaged(!isValid);
            updateComboBoxStyle(cb_horaireOuverture, isValid);
            
            // Check if fermeture is after ouverture
            validateHoraires();
        });
        
        cb_horaireFermeture.valueProperty().addListener((observable, oldValue, newValue) -> {
            boolean isValid = newValue != null;
            fermetureErrorLabel.setVisible(!isValid);
            fermetureErrorLabel.setManaged(!isValid);
            updateComboBoxStyle(cb_horaireFermeture, isValid);
            
            // Check if fermeture is after ouverture
            validateHoraires();
        });
    }
    
    /**
     * Vérifie que l'horaire de fermeture est après l'horaire d'ouverture
     */
    private void validateHoraires() {
        if (cb_horaireOuverture.getValue() != null && cb_horaireFermeture.getValue() != null) {
            LocalTime ouverture = LocalTime.parse(cb_horaireOuverture.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime fermeture = LocalTime.parse(cb_horaireFermeture.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
            
            boolean isValid = fermeture.isAfter(ouverture);
            fermetureErrorLabel.setText(isValid ? "Veuillez sélectionner un horaire de fermeture" : 
                                               "L'horaire de fermeture doit être après l'ouverture");
            fermetureErrorLabel.setVisible(!isValid);
            fermetureErrorLabel.setManaged(!isValid);
            updateComboBoxStyle(cb_horaireFermeture, isValid);
        }
    }
    
    /**
     * Vérifie si l'email est valide
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * Met à jour le style d'un champ selon sa validité
     */
    private void updateFieldStyle(TextField field, boolean isValid) {
        if (isValid) {
            field.setStyle("-fx-background-radius: 5px; -fx-border-radius: 5px; -fx-border-color: #e0e0e0; -fx-padding: 8px; -fx-focus-color: #2196F3; -fx-faint-focus-color: #2196F322;");
        } else {
            field.setStyle("-fx-background-radius: 5px; -fx-border-radius: 5px; -fx-border-color: #e53935; -fx-padding: 8px; -fx-focus-color: #e53935; -fx-faint-focus-color: #e5393522; -fx-background-color: #ffebee;");
        }
    }
    
    /**
     * Met à jour le style d'une ComboBox selon sa validité
     */
    private void updateComboBoxStyle(ComboBox<?> comboBox, boolean isValid) {
        if (isValid) {
            comboBox.setStyle("-fx-background-radius: 5px; -fx-border-radius: 5px; -fx-border-color: #e0e0e0; -fx-padding: 5px; -fx-focus-color: #2196F3; -fx-faint-focus-color: #2196F322;");
        } else {
            comboBox.setStyle("-fx-background-radius: 5px; -fx-border-radius: 5px; -fx-border-color: #e53935; -fx-padding: 5px; -fx-focus-color: #e53935; -fx-faint-focus-color: #e5393522; -fx-background-color: #ffebee;");
        }
    }

    @FXML
    void ajouterRestaurant(ActionEvent event) {
        // Animation du bouton
        Button source = (Button) event.getSource();
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), source);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(0.95);
        scaleTransition.setToY(0.95);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        
        scaleTransition.play(); // Start the animation
        
        // --- Perform validation and logic immediately after starting animation --- 
        
        // Validation des champs
        if (!validateFields()) {
            return; // Stop if validation fails
        }
        
        try {
            // Création du restaurant
            Restaurant restaurant = new Restaurant();
            restaurant.setNom(tf_nom.getText().trim());
            restaurant.setAdresse(tf_adresse.getText().trim());
            restaurant.setVille(tf_ville.getText().trim());
            restaurant.setPays(tf_pays.getText().trim());
            restaurant.setCapaciteTotale(Integer.parseInt(tf_capaciteTotale.getText().trim()));
            restaurant.setHoraireOuverture(cb_horaireOuverture.getValue());
            restaurant.setHoraireFermeture(cb_horaireFermeture.getValue());
            restaurant.setTelephone(tf_telephone.getText().trim());
            restaurant.setEmail(tf_email.getText().trim());
            
            // Gestion de l'image
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    // Enregistrer l'image dans un dossier permanent
                    File sourceFile = new File(imagePath);
                    String destDir = "src/main/resources/images/restaurants/";
                    new File(destDir).mkdirs(); // Crée le dossier si inexistant
                    
                    String fileName = "resto_" + System.currentTimeMillis() + getFileExtension(imagePath);
                    Path destinationPath = Paths.get(destDir + fileName);
                    Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    
                    // Chemin relatif pour l'accès depuis l'application
                    String savedImagePath = "images/restaurants/" + fileName;
                    restaurant.setImage(savedImagePath);
                } catch (IOException ex) {
                    showErrorAlert("Erreur lors de l'enregistrement de l'image: " + ex.getMessage());
                    return; // Stop if image saving fails
                }
            }
            
            // Sauvegarde en base de données
            serviceRestaurant.ajouter(restaurant);
            
            // Afficher un message de succès stylisé
            showSuccessAlert("Restaurant ajouté avec succès!");
            
            // Supprimer la redirection automatique vers la liste des restaurants
            // Nettoyer les champs pour permettre l'ajout d'un autre restaurant
            clearFields();
            
        } catch (NumberFormatException ex) {
            showErrorAlert("La capacité doit être un nombre valide");
        } catch (SQLException ex) {
            showErrorAlert("Erreur base de données: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            showErrorAlert("Une erreur inattendue est survenue: " + ex.getMessage());
            ex.printStackTrace();
        }
        // --- End of logic --- 
    }

    /**
     * Valide tous les champs du formulaire
     */
    private boolean validateFields() {
        boolean isValid = true;
        
        // Vérification des champs obligatoires
        if (tf_nom.getText().trim().isEmpty()) {
            nomErrorLabel.setVisible(true);
            nomErrorLabel.setManaged(true);
            updateFieldStyle(tf_nom, false);
            isValid = false;
        } else {
            nomErrorLabel.setVisible(false);
            nomErrorLabel.setManaged(false);
            updateFieldStyle(tf_nom, true);
        }
        
        if (tf_adresse.getText().trim().isEmpty()) {
            adresseErrorLabel.setVisible(true);
            adresseErrorLabel.setManaged(true);
            updateFieldStyle(tf_adresse, false);
            isValid = false;
        } else {
            adresseErrorLabel.setVisible(false);
            adresseErrorLabel.setManaged(false);
            updateFieldStyle(tf_adresse, true);
        }
        
        if (tf_ville.getText().trim().isEmpty()) {
            villeErrorLabel.setVisible(true);
            villeErrorLabel.setManaged(true);
            updateFieldStyle(tf_ville, false);
            isValid = false;
        } else {
            villeErrorLabel.setVisible(false);
            villeErrorLabel.setManaged(false);
            updateFieldStyle(tf_ville, true);
        }
        
        if (tf_pays.getText().trim().isEmpty()) {
            paysErrorLabel.setVisible(true);
            paysErrorLabel.setManaged(true);
            updateFieldStyle(tf_pays, false);
            isValid = false;
        } else {
            paysErrorLabel.setVisible(false);
            paysErrorLabel.setManaged(false);
            updateFieldStyle(tf_pays, true);
        }
        
        if (tf_capaciteTotale.getText().trim().isEmpty()) {
            capaciteErrorLabel.setText("La capacité totale est requise");
            capaciteErrorLabel.setVisible(true);
            capaciteErrorLabel.setManaged(true);
            updateFieldStyle(tf_capaciteTotale, false);
            isValid = false;
        } else {
            try {
                int capacite = Integer.parseInt(tf_capaciteTotale.getText().trim());
                if (capacite <= 0) {
                    capaciteErrorLabel.setText("La capacité doit être un nombre positif");
                    capaciteErrorLabel.setVisible(true);
                    capaciteErrorLabel.setManaged(true);
                    updateFieldStyle(tf_capaciteTotale, false);
                    isValid = false;
                } else {
                    capaciteErrorLabel.setVisible(false);
                    capaciteErrorLabel.setManaged(false);
                    updateFieldStyle(tf_capaciteTotale, true);
                }
            } catch (NumberFormatException e) {
                capaciteErrorLabel.setText("La capacité doit être un nombre valide");
                capaciteErrorLabel.setVisible(true);
                capaciteErrorLabel.setManaged(true);
                updateFieldStyle(tf_capaciteTotale, false);
                isValid = false;
            }
        }
        
        // Validation de l'email (obligatoire)
        if (tf_email.getText().trim().isEmpty()) {
            emailErrorLabel.setText("L'email est requis");
            emailErrorLabel.setVisible(true);
            emailErrorLabel.setManaged(true);
            updateFieldStyle(tf_email, false);
            isValid = false;
        } else if (!isValidEmail(tf_email.getText().trim())) {
            emailErrorLabel.setText("Format d'email invalide");
            emailErrorLabel.setVisible(true);
            emailErrorLabel.setManaged(true);
            updateFieldStyle(tf_email, false);
            isValid = false;
        } else {
            emailErrorLabel.setVisible(false);
            emailErrorLabel.setManaged(false);
            updateFieldStyle(tf_email, true);
        }
        
        // Validation du téléphone (obligatoire)
        if (tf_telephone.getText().trim().isEmpty()) {
            telephoneErrorLabel.setText("Le numéro de téléphone est requis");
            telephoneErrorLabel.setVisible(true);
            telephoneErrorLabel.setManaged(true);
            updateFieldStyle(tf_telephone, false);
            isValid = false;
        } else if (tf_telephone.getText().trim().length() < 8) {
            telephoneErrorLabel.setText("Le numéro doit contenir au moins 8 chiffres");
            telephoneErrorLabel.setVisible(true);
            telephoneErrorLabel.setManaged(true);
            updateFieldStyle(tf_telephone, false);
            isValid = false;
        } else {
            telephoneErrorLabel.setVisible(false);
            telephoneErrorLabel.setManaged(false);
            updateFieldStyle(tf_telephone, true);
        }
        
        // Validation des horaires
        if (cb_horaireOuverture.getValue() == null) {
            ouvertureErrorLabel.setVisible(true);
            ouvertureErrorLabel.setManaged(true);
            updateComboBoxStyle(cb_horaireOuverture, false);
            isValid = false;
        } else {
            ouvertureErrorLabel.setVisible(false);
            ouvertureErrorLabel.setManaged(false);
            updateComboBoxStyle(cb_horaireOuverture, true);
        }
        
        if (cb_horaireFermeture.getValue() == null) {
            fermetureErrorLabel.setText("Veuillez sélectionner un horaire de fermeture");
            fermetureErrorLabel.setVisible(true);
            fermetureErrorLabel.setManaged(true);
            updateComboBoxStyle(cb_horaireFermeture, false);
            isValid = false;
        } else {
            fermetureErrorLabel.setVisible(false);
            fermetureErrorLabel.setManaged(false);
            updateComboBoxStyle(cb_horaireFermeture, true);
            
            // Vérifier que l'heure de fermeture est après l'heure d'ouverture
            if (cb_horaireOuverture.getValue() != null) {
                LocalTime ouverture = LocalTime.parse(cb_horaireOuverture.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
                LocalTime fermeture = LocalTime.parse(cb_horaireFermeture.getValue(), DateTimeFormatter.ofPattern("HH:mm"));
                
                if (!fermeture.isAfter(ouverture)) {
                    fermetureErrorLabel.setText("L'heure de fermeture doit être après l'heure d'ouverture");
                    fermetureErrorLabel.setVisible(true);
                    fermetureErrorLabel.setManaged(true);
                    updateComboBoxStyle(cb_horaireFermeture, false);
                    isValid = false;
                }
            }
        }
        
        // Validation de l'image (obligatoire)
        if (imagePath == null || imagePath.isEmpty()) {
            imageErrorLabel.setText("Une image est requise");
            imageErrorLabel.setVisible(true);
            imageErrorLabel.setManaged(true);
            isValid = false;
        } else {
            imageErrorLabel.setVisible(false);
            imageErrorLabel.setManaged(false);
        }
        
        return isValid;
    }

    private Restaurant createRestaurantFromFields() throws IOException {
        String nom = tf_nom.getText();
        String adresse = tf_adresse.getText();
        String ville = tf_ville.getText();
        String pays = tf_pays.getText();
        int capaciteTotale = Integer.parseInt(tf_capaciteTotale.getText());
        String horaireOuverture = cb_horaireOuverture.getValue();
        String horaireFermeture = cb_horaireFermeture.getValue();
        String telephone = tf_telephone.getText();
        String email = tf_email.getText();

        // Gestion de l'image
        String savedImagePath = null;
        if (imagePath != null && !imagePath.isEmpty()) {
            File sourceFile = new File(imagePath);
            String destDir = "src/main/resources/images/restaurants/";
            new File(destDir).mkdirs(); // Crée le dossier si inexistant

            String fileName = "resto_" + System.currentTimeMillis() + getFileExtension(imagePath);
            Path destinationPath = Paths.get(destDir + fileName);
            Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            savedImagePath = "images/restaurants/" + fileName;
        }

        return new Restaurant(nom, adresse, ville, pays, capaciteTotale,
                horaireOuverture, horaireFermeture,
                telephone, email, savedImagePath);
    }

    private String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filePath.substring(dotIndex);
    }

    @FXML
    void addimages(ActionEvent event) {
        // Cacher le message d'erreur d'image au début
        if (imageErrorLabel != null) {
            imageErrorLabel.setVisible(false);
            imageErrorLabel.setManaged(false);
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            this.imagePath = selectedFile.getAbsolutePath();
            Image image = new Image(selectedFile.toURI().toString());
            uploadedImageView.setImage(image);
        }
    }

    private void clearFields() {
        tf_nom.clear();
        tf_adresse.clear();
        tf_ville.clear();
        tf_pays.clear();
        tf_capaciteTotale.clear();
        cb_horaireOuverture.setValue("08:00");
        cb_horaireFermeture.setValue("18:00");
        tf_telephone.clear();
        tf_email.clear();
        
        // Safely load the placeholder image
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
            if (defaultImage != null && !defaultImage.isError()) {
                uploadedImageView.setImage(defaultImage);
            } else {
                // Optionally set a default background or leave it empty if image fails
                System.err.println("Placeholder image not found or failed to load in clearFields.");
                 uploadedImageView.setImage(null); // Clear the image view
            }
        } catch (Exception e) {
            System.err.println("Error loading placeholder image in clearFields: " + e.getMessage());
             uploadedImageView.setImage(null); // Clear the image view on error
        }
        
        imagePath = null;
    }

    @FXML
    void ListResto(ActionEvent event) {
        loadScene("/ListRestaurant.fxml", event);
    }

    @FXML
    void listreservation(ActionEvent event) {
        loadScene("/ListeReservationRestaurant.fxml", event);
    }

    @FXML
    void listresto(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListRestaurant.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
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

    private void loadScene(String fxmlPath, ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);

            // Animation de transition
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

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showErrorAlert(String message) {
        showAlert("Erreur", message, Alert.AlertType.ERROR);
    }
    
    private void showSuccessAlert(String message) {
        // Use the built-in status message instead of alert dialog
        if (statusMessagePane != null && statusMessage != null) {
            statusMessage.setText(message);
            statusMessagePane.setVisible(true);
            statusMessagePane.setManaged(true);
            
            // Set a timer to hide the message after 3 seconds
            new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        javafx.application.Platform.runLater(() -> {
                            statusMessagePane.setVisible(false);
                            statusMessagePane.setManaged(false);
                        });
                    }
                },
                3000
            );
        } else {
            // Fallback to alert if the status message elements are not available
            showAlert("Succès", message, Alert.AlertType.INFORMATION);
        }
    }

    public void Ajouter(ActionEvent actionEvent) {
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
        navigateTo("/ListRestaurant.fxml", "Liste des Restaurants");
    }
    
    /**
     * Navigation vers la page Vols
     */
    @FXML
    private void navigateToVols() {
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
            //showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture", e.getMessage());
        }
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
            
            Stage stage = (Stage) tf_nom.getScene().getWindow();
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
}