package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import entities.Expert;
import Services.ServiceExpert;
import java.util.regex.Pattern;
import java.sql.SQLException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class CreerExpertController {
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telephoneField;
    @FXML
    private ComboBox<String> specialiteComboBox;
    @FXML
    private TextField experienceField;
    @FXML
    private Button creerButton;
    @FXML
    private Button annulerButton;
    @FXML
    private ImageView photoPreview;
    @FXML
    private Label photoStatusLabel;

    private String photoPath;
    private final ServiceExpert serviceExpert = new ServiceExpert();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");

    @FXML
    public void initialize() {
        try {
            // Load specialties from database
            List<String> domaines = serviceExpert.recupererDomaines();
            specialiteComboBox.getItems().addAll(domaines);
            if (!domaines.isEmpty()) {
                specialiteComboBox.setValue(domaines.get(0)); // Set first domain as default
            }
        } catch (SQLException e) {
            System.err.println("Error loading domains: " + e.getMessage());
            // Fallback to default values if database load fails
        specialiteComboBox.getItems().addAll(
            "Java",
            "Python",
            "JavaScript",
            "DevOps",
            "Base de données",
            "Web",
            "Mobile",
            "Cloud",
            "Sécurité"
        );
        }
        
        // Set default image
        try {
            String defaultImagePath = "/images/default-avatar.png";
            boolean imageSet = false;
            if (photoPath != null && !photoPath.isEmpty()) {
                try {
                    java.net.URL photoUrl = getClass().getResource("/" + photoPath);
                    if (photoUrl != null) {
                        Image expertImage = new Image(photoUrl.toExternalForm());
                        photoPreview.setImage(expertImage);
                        imageSet = true;
                    } else {
                        File photoFile = new File(photoPath);
                        if (photoFile.exists()) {
                            Image expertImage = new Image(photoFile.toURI().toString());
                            photoPreview.setImage(expertImage);
                            imageSet = true;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error loading expert photo: " + e.getMessage());
                }
            }
            if (!imageSet) {
                java.net.URL defaultUrl = getClass().getResource(defaultImagePath);
                if (defaultUrl != null) {
                    Image defaultImage = new Image(defaultUrl.toExternalForm());
                    photoPreview.setImage(defaultImage);
                }
            }
        } catch (Exception e) {
            // Handle case where default image is not found
            photoPreview.setStyle("-fx-background-color: white;");
        }
    }

    @FXML
    private void uploadPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(photoPreview.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Create photos directory if it doesn't exist
                Path photosDir = Paths.get("photos");
                if (!Files.exists(photosDir)) {
                    Files.createDirectory(photosDir);
                }

                // Copy file to photos directory with unique name
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                Path targetPath = photosDir.resolve(fileName);
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

                // Update photoPath with relative path
                photoPath = "photos/" + fileName;

                // Update preview using the file we just copied
                Image image = new Image(targetPath.toUri().toString());
                photoPreview.setImage(image);
                
                photoStatusLabel.setText("Photo ajoutée avec succès");
                photoStatusLabel.setStyle("-fx-text-fill: #4CAF50;");
                
                System.out.println("[DEBUG] Photo saved to: " + photoPath);
            } catch (IOException e) {
                photoStatusLabel.setText("Erreur lors de l'ajout de la photo");
                photoStatusLabel.setStyle("-fx-text-fill: #f44336;");
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void creerExpert() {
        if (!validateFields()) {
            return;
        }

        try {
            Expert expert = new Expert();
            expert.setNom_expert(nomField.getText().trim());
            expert.setPrenom_expert(prenomField.getText().trim());
            expert.setEmail(emailField.getText().trim());
            expert.setSpecialite(specialiteComboBox.getValue());
            
            // Champs optionnels
            String telephone = telephoneField.getText().trim();
            if (!telephone.isEmpty()) {
                expert.setTelephone(telephone);
            }
            
            String experience = experienceField.getText().trim();
            if (!experience.isEmpty()) {
                expert.setAnneeExperience(Integer.parseInt(experience));
            }

            // Add photo path if available
            if (photoPath != null) {
                expert.setPhotoPath(photoPath);
            }

            serviceExpert.ajouter(expert);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'expert a été créé avec succès");
            fermerFenetre();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la création de l'expert dans la base de données: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        // Vérifier que tous les champs obligatoires sont remplis
        if (nomField.getText().trim().isEmpty() ||
            prenomField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            specialiteComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires (nom, prénom, email, spécialité)");
            return false;
        }

        // Valider l'email
        if (!EMAIL_PATTERN.matcher(emailField.getText().trim()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format d'email invalide");
            return false;
        }

        // Valider le numéro de téléphone s'il est fourni
        String telephone = telephoneField.getText().trim();
        if (!telephone.isEmpty() && !PHONE_PATTERN.matcher(telephone).matches()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le numéro de téléphone doit contenir 8 chiffres");
            return false;
        }

        // Valider l'année d'expérience si elle est fournie
        String experience = experienceField.getText().trim();
        if (!experience.isEmpty()) {
            try {
                int exp = Integer.parseInt(experience);
                if (exp < 0) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "L'année d'expérience doit être un nombre positif");
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'année d'expérience doit être un nombre");
                return false;
            }
        }

        return true;
    }

    @FXML
    private void annuler() {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void onAccueilAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AcceuilAdmin.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil Admin");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onUserAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminUser.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Utilisateurs");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void ondossierAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDossier.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Dossiers");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onuniversiteAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recupereruniversite.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Universités");
            stage.setMinWidth(1256);
            stage.setMinHeight(702);
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onentretienAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gestionnaire.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Entretiens");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onevenementAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_evenement.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Événements");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onhebergementAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFoyer.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Foyers");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onrestaurantAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterRestaurant.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Restaurants");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onvolsAdminButtonClick(ActionEvent actionEvent) {

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Accueilvol.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Gestion des Vols");
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
                // showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());


        }
    }

    @FXML
    public void onlogoutAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - GradAway");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion: " + e.getMessage());
        }
    }
}
