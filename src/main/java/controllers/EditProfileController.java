package controllers;

import Services.ServiceUser;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class EditProfileController {
    @FXML
    private TextField tfnomprofil;
    @FXML
    private TextField tfprenomprofil;
    @FXML
    private TextField tfemailprofil;
    @FXML
    private TextField tfcinprofil;
    @FXML
    private TextField tftelephoneprofil;
    @FXML
    private ComboBox<String> tfageprofil;
    @FXML
    private DatePicker tfdatenaissanceprofil;
    @FXML
    private TextField tfnationaliteprofil;
    @FXML
    private ComboBox<String> tfdomaineprofil;
    @FXML
    private ComboBox<Integer> tfdiplomeprofil;
    @FXML
    private TextField tfuniversiteprofil;
    @FXML
    private TextField tfmoyenneprofil;
    @FXML
    private Label messageLabel;
    @FXML
    private Button editProfileButton;

    private ServiceUser serviceUser;
    private User currentUser;
    private int userId;
    @FXML
    private ImageView ajouterimageprofil;
    @FXML
    private PasswordField oldPassword;
    @FXML
    private PasswordField newPassword;

    public EditProfileController() {
        serviceUser = new ServiceUser();
    }

    @FXML
    public void initialize() {
        System.out.println("EditProfileController: initialize() called");
        
        // Add listener for date of birth changes
        tfdatenaissanceprofil.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                calculateAndDisplayAge(newValue);
            }
        });

        // Initialize domaine_etude ComboBox
        ObservableList<String> domaines = FXCollections.observableArrayList(
            "Mathématiques",
            "Sciences expérimentales",
            "Économie et gestion",
            "Sciences techniques",
            "Lettres",
            "Sport",
            "Sciences de l'informatique"
        );
        tfdomaineprofil.setItems(domaines);

        // Initialize annee_obtention_diplome ComboBox
        List<Integer> annees = new ArrayList<>();
        for (int i = 1999; i <= 2025; i++) {
            annees.add(i);
        }
        tfdiplomeprofil.setItems(FXCollections.observableArrayList(annees));

        // If userId was set before initialize, load the user data
        if (userId > 0) {
            System.out.println("EditProfileController: Loading user data in initialize()");
            loadUserData();
        }
    }

    private void calculateAndDisplayAge(LocalDate birthDate) {
        LocalDate referenceDate = LocalDate.of(2025, 1, 1);
        Period period = Period.between(birthDate, referenceDate);
        int age = period.getYears();
        tfageprofil.setValue(String.valueOf(age));
    }

    public void setUserId(int userId) {
        System.out.println("EditProfileController: Setting user ID: " + userId);
        this.userId = userId;
        
        // If fields are already initialized, load the user data
        if (tfageprofil != null) {
            System.out.println("EditProfileController: Loading user data in setUserId()");
            loadUserData();
        }
    }

    private void loadUserData() {
        System.out.println("EditProfileController: loadUserData() called");
        try {
            currentUser = serviceUser.getUserById(userId);
            if (currentUser != null) {
                System.out.println("EditProfileController: User loaded successfully - " + 
                    currentUser.getNom() + " " + currentUser.getPrenom());
                populateFields();
            } else {
                setMessage("Utilisateur non trouvé");
                System.err.println("EditProfileController: User not found with ID: " + userId);
            }
        } catch (SQLException e) {
            setMessage("Erreur lors du chargement des données: " + e.getMessage());
            System.err.println("EditProfileController: Error loading user data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateFields() {
        System.out.println("EditProfileController: populateFields() called");
        if (currentUser != null) {
            try {
                System.out.println("EditProfileController: Populating fields for user: " + currentUser.getNom());
                
                // Basic Information
                tfnomprofil.setText(currentUser.getNom());
                tfprenomprofil.setText(currentUser.getPrenom());
                tfemailprofil.setText(currentUser.getEmail());
                tfcinprofil.setText(String.valueOf(currentUser.getCin()));
                tfnationaliteprofil.setText(currentUser.getNationalite());
                tftelephoneprofil.setText(String.valueOf(currentUser.getTelephone()));
                
                // Load profile image if exists, otherwise set default
                String imagePath = (currentUser.getImage() != null && !currentUser.getImage().isEmpty())
                    ? currentUser.getImage()
                    : "src/main/resources/images/profilee.jpg";
                try {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        Image image = new Image(imageFile.toURI().toString());
                        ajouterimageprofil.setImage(image);
                    }
                } catch (Exception e) {
                    System.err.println("Error loading profile image: " + e.getMessage());
                }
                
                // Age and Date of Birth
                if (tfageprofil != null) {
                    tfageprofil.setValue(String.valueOf(currentUser.getAge()));
                }
                if (tfdatenaissanceprofil != null) {
                    tfdatenaissanceprofil.setValue(currentUser.getDateNaissance());
                }
                
                // Additional Information
                tfdomaineprofil.setValue(currentUser.getDomaine_etude());
                tfdiplomeprofil.setValue(currentUser.getAnnee_obtention_diplome());
                tfuniversiteprofil.setText(currentUser.getUniversite_origine());
                tfmoyenneprofil.setText(String.valueOf(currentUser.getMoyennes()));
                
                System.out.println("EditProfileController: Fields populated successfully");
            } catch (Exception e) {
                setMessage("Erreur lors du remplissage des champs: " + e.getMessage());
                System.err.println("EditProfileController: Error populating fields: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("EditProfileController: Cannot populate fields - currentUser is null");
        }
    }

    private void setMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
        } else {
            System.err.println("Message Label is not available: " + message);
        }
    }

    @FXML
    private void handleEditProfile() {
        try {
            System.out.println("Edit profile button clicked");
            
            // Validate required fields
            if (tfnomprofil.getText().isEmpty() || tfprenomprofil.getText().isEmpty() || 
                tfemailprofil.getText().isEmpty() || tfcinprofil.getText().isEmpty() || 
                tftelephoneprofil.getText().isEmpty() || tfageprofil.getValue() == null || 
                tfdatenaissanceprofil.getValue() == null || tfnationaliteprofil.getText().isEmpty() || 
                tfdomaineprofil.getValue() == null || tfdiplomeprofil.getValue() == null || 
                tfuniversiteprofil.getText().isEmpty() || tfmoyenneprofil.getText().isEmpty()) {
                setMessage("Veuillez remplir tous les champs obligatoires");
                return;
            }

            // Validate date of birth
            LocalDate birthDate = tfdatenaissanceprofil.getValue();
            LocalDate maxDate = LocalDate.of(2007, 1, 1);
            if (birthDate.isAfter(maxDate)) {
                setMessage("Vous devez être majeur (-18)");
                return;
            }

            // Update the currentUser object with new values
            currentUser.setNom(tfnomprofil.getText());
            currentUser.setPrenom(tfprenomprofil.getText());
            currentUser.setEmail(tfemailprofil.getText());
            currentUser.setCin(Integer.parseInt(tfcinprofil.getText()));
            currentUser.setNationalite(tfnationaliteprofil.getText());
            currentUser.setTelephone(Integer.parseInt(tftelephoneprofil.getText()));
            currentUser.setAge(Integer.parseInt(tfageprofil.getValue()));
            currentUser.setDateNaissance(tfdatenaissanceprofil.getValue());
            currentUser.setDomaine_etude(tfdomaineprofil.getValue());
            currentUser.setAnnee_obtention_diplome(tfdiplomeprofil.getValue());
            currentUser.setUniversite_origine(tfuniversiteprofil.getText());
            String moyenneStr = tfmoyenneprofil.getText().replace(',', '.');
            currentUser.setMoyennes(Double.parseDouble(moyenneStr));

            // Update the user in the database
            serviceUser.modifier(currentUser);
            setMessage("Profil mis à jour avec succès!");
            System.out.println("Profile updated successfully for user: " + currentUser.getNom());
        } catch (NumberFormatException e) {
            setMessage("Erreur: Veuillez entrer des nombres valides pour les champs numériques (CIN, téléphone, moyenne)");
            System.err.println("Number format error: " + e.getMessage());
        } catch (Exception e) {
            setMessage("Erreur lors de la mise à jour du profil: " + e.getMessage());
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void changephoto(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        
        // Set extension filters
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        
        // Show open file dialog
        File selectedFile = fileChooser.showOpenDialog(null);
        
        if (selectedFile != null) {
            try {
                // Create an image from the selected file
                Image image = new Image(selectedFile.toURI().toString());
                
                // Set the image to the ImageView
                ajouterimageprofil.setImage(image);
                
                // Save the image path to the database
                if (currentUser != null) {
                    // Create a copy of the image in the application's resources directory
                    String destinationPath = "src/main/resources/images/profiles/" + selectedFile.getName();
                    File destinationFile = new File(destinationPath);
                    
                    // Create directories if they don't exist
                    destinationFile.getParentFile().mkdirs();
                    
                    // Copy the file
                    java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destinationFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                    
                    // Update user with the new image path
                    currentUser.setImage(destinationPath);
                    serviceUser.modifier(currentUser);
                    setMessage("Photo de profil mise à jour avec succès!");
                    System.out.println("Profile picture updated successfully for user: " + currentUser.getNom());
                } else {
                    setMessage("Erreur: Aucun utilisateur connecté");
                }
            } catch (Exception e) {
                setMessage("Erreur lors du chargement de l'image: " + e.getMessage());
                System.err.println("Error loading image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void acceuilbutton(ActionEvent actionEvent) {
        try {
            System.out.println("EditProfileController: Opening Accueil view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
            Parent root = loader.load();

            // Get the controller and set the user ID
            Acceuilcontroller controller = loader.getController();
            controller.setUserId(this.userId);

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil - GradAway");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("EditProfileController: Error loading accueil view: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la page d'accueil.");
            e.printStackTrace();
        }
    }

    @FXML
    public void userbutton(ActionEvent actionEvent) {
        if (this.userId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID utilisateur invalide. Impossible d'ouvrir le profil.");
            return;
        }

        try {
            System.out.println("EditProfileController: Opening EditProfile view for User ID: " + this.userId);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();

            EditProfileController editProfileController = loader.getController();
            editProfileController.setUserId(this.userId);

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Mon Profil");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("EditProfileController: Error loading EditProfile.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue du profil.");
            e.printStackTrace();
        }
    }

    @FXML
    public void universitébutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listcandidaturecards.fxml"));
            Parent root = loader.load();

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Candidatures");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("EditProfileController: Error loading adminconditature.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des candidatures.");
            e.printStackTrace();
        }
    }

    @FXML
    public void evenementbutton(ActionEvent actionEvent) {
        try {
            System.out.println("EditProfileController: Opening Affiche Evenement view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affiche_evenement.fxml"));
            Parent root = loader.load();

            Ajouterafficheevenementcontrolleur controller = loader.getController();
            controller.setCurrentUserId(this.userId);

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Événements");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("EditProfileController: Error loading affiche_evenement.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des événements.");
            e.printStackTrace();
        }
    }

    @FXML
    public void hebergementbutton(ActionEvent actionEvent) {
        try {
            System.out.println("EditProfileController: Opening ListFoyerClient view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Foyers");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("EditProfileController: Error loading ListFoyerClient.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des foyers.");
            e.printStackTrace();
        }
    }

    @FXML
    public void restaurantbutton(ActionEvent actionEvent) {
        try {
            System.out.println("EditProfileController: Opening ListRestaurantClient view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurantClient.fxml"));
            Parent root = loader.load();

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Restaurants");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("EditProfileController: Error loading ListRestaurantClient.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des restaurants.");
            e.printStackTrace();
        }
    }

    @FXML
    public void entretienbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemanderEntretien.fxml"));
            Parent root = loader.load();

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Demander Entretien");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des entretiens.");
        }
    }

    @FXML
    public void volsbutton(ActionEvent actionEvent) {
        try {
            System.out.println("AcceuilController: Opening AfficherVolsUtilisateur view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AfficherVolsUtilisateur.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Vols Disponibles");
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("AcceuilController: Error loading AfficherVolsUtilisateur.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des vols.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("AcceuilController: Unexpected error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue.");
            e.printStackTrace();
        }
    }

    @FXML
    public void logoutbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - GradAway");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("EditProfileController: Error loading login view: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion.");
            e.printStackTrace();
        }
    }

    @FXML
    public void dossierbutton(ActionEvent actionEvent) {
        try {
            System.out.println("EditProfileController: Opening AjoutDossier view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDossier.fxml"));
            Parent root = loader.load();

            // Get the controller and set the user ID
            AjoutDossierController controller = loader.getController();
            controller.setEtudiantId(this.userId);

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajout Dossier");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("EditProfileController: Error loading AjoutDossier.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue du dossier.");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void changepassword(ActionEvent actionEvent) {
        try {
            // Validate input fields
            if (oldPassword.getText().isEmpty() || newPassword.getText().isEmpty()) {
                setMessage("Veuillez remplir tous les champs de mot de passe");
                return;
            }

            // Verify old password
            if (!serviceUser.verifyPassword(userId, oldPassword.getText())) {
                setMessage("Ancien mot de passe incorrect");
                return;
            }

            // Validate new password (minimum 6 characters)
            if (newPassword.getText().length() < 6) {
                setMessage("Le nouveau mot de passe doit contenir au moins 6 caractères");
                return;
            }

            // Update password in database
            serviceUser.updatePassword(userId, newPassword.getText());
            
            // Clear password fields
            oldPassword.clear();
            newPassword.clear();
            
            setMessage("Mot de passe modifié avec succès!");
            System.out.println("Password updated successfully for user ID: " + userId);
            
        } catch (Exception e) {
            setMessage("Erreur lors du changement de mot de passe: " + e.getMessage());
            System.err.println("Error changing password: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

