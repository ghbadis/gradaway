package controllers;

import Services.ServiceDossier;
import Services.ServiceUser;
import entities.Dossier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.MqttService;
import utils.EmailSender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AjoutDossierController {

    @FXML private ImageView cinPreview;
    @FXML private ImageView photoPreview;
    @FXML private ImageView diplomeBacPreview;
    @FXML private ImageView releveNotePreview;
    @FXML private ImageView diplomeObtenuPreview;
    @FXML private ImageView lettreMotivationPreview;
    @FXML private ImageView dossierSantePreview;
    @FXML private ImageView cvPreview;
    @FXML private DatePicker dateDepotPicker;
    @FXML private Button submitButton;
    @FXML private Button cancelButton;
    @FXML private Button viewDossierButton;
    @FXML private AnchorPane rootPane; // Assuming the root element has fx:id="rootPane"

    private int currentEtudiantId = -1; // Placeholder for the student ID
    private ServiceDossier serviceDossier;
    private ServiceUser serviceUser;
    private String cinPath;
    private String photoPath;
    private String diplomeBacPath;
    private String releveNotePath;
    private String diplomeObtenuPath;
    private String lettreMotivationPath;
    private String dossierSantePath;
    private String cvPath;

    // Updated method to check for existing dossier
    public void setEtudiantId(int id) {
        this.currentEtudiantId = id;
        System.out.println("Current Etudiant ID set to: " + this.currentEtudiantId);

        if (id <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID étudiant invalide.");
            submitButton.setDisable(true);
            viewDossierButton.setDisable(true);
        } else {
            // Check if dossier exists for this student
            checkExistingDossier(id);
        }
    }

    private void checkExistingDossier(int etudiantId) {
        try {
            Dossier existingDossier = serviceDossier.recupererParEtudiantId(etudiantId);
            if (existingDossier != null) {
                System.out.println("Dossier already exists for student ID: " + etudiantId + ". Disabling submit button.");
                submitButton.setDisable(true); // Disable submit if dossier exists
                viewDossierButton.setDisable(false); // Enable view button
                // Optional: Show a message indicating dossier exists

            } else {
                System.out.println("No existing dossier found for student ID: " + etudiantId + ". Enabling submit button.");
                submitButton.setDisable(false); // Enable submit if no dossier exists
                viewDossierButton.setDisable(true); // Disable view button if no dossier (as there's nothing to view yet)
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Base de Données", "Impossible de vérifier l'existence d'un dossier existant.\n" + e.getMessage());
            System.err.println("Database error while checking for existing dossier:");
            e.printStackTrace();
            // Disable both buttons in case of error
            submitButton.setDisable(true);
            viewDossierButton.setDisable(true);
        }
    }

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur AjoutDossierController");
        
        dateDepotPicker.setValue(LocalDate.now());
        dateDepotPicker.setDisable(true);
        dateDepotPicker.setStyle("-fx-opacity: 0.7;");
        serviceDossier = new ServiceDossier();
        serviceUser = new ServiceUser();

        submitButton.setDisable(true);
        viewDossierButton.setDisable(true);

        // Initialiser les ImageViews
        initializeImageViews();
    }

    private void initializeImageViews() {
        System.out.println("Initialisation des ImageViews");
        
        // Configurer tous les ImageViews avec un style par défaut
        ImageView[] imageViews = {
            cinPreview, photoPreview, diplomeBacPreview, releveNotePreview,
            diplomeObtenuPreview, lettreMotivationPreview, dossierSantePreview, cvPreview
        };

        String[] imageViewNames = {
            "cinPreview", "photoPreview", "diplomeBacPreview", "releveNotePreview",
            "diplomeObtenuPreview", "lettreMotivationPreview", "dossierSantePreview", "cvPreview"
        };

        for (int i = 0; i < imageViews.length; i++) {
            ImageView imageView = imageViews[i];
            if (imageView != null) {
                System.out.println("Configuration de " + imageViewNames[i]);
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");
            } else {
                System.err.println("ImageView est null pour " + imageViewNames[i]);
            }
        }
    }

    private void handleFileUpload(ActionEvent event, ImageView imageView, String fileType) {
        System.out.println("Début handleFileUpload pour " + fileType);
        
        if (imageView == null) {
            System.err.println("ImageView est null pour " + fileType);
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        
        // Configurer les extensions de fichiers acceptées
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);

        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try {
                System.out.println("Fichier sélectionné: " + selectedFile.getAbsolutePath());
                
                // Vérifier si le fichier existe
                if (!selectedFile.exists()) {
                    System.err.println("Le fichier n'existe pas: " + selectedFile.getAbsolutePath());
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Le fichier sélectionné n'existe pas.");
                    return;
                }

                // Vérifier la taille du fichier
                long fileSize = selectedFile.length();
                System.out.println("Taille du fichier: " + fileSize + " bytes");
                
                // Configurer l'ImageView
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                
                // Charger l'image
                FileInputStream input = new FileInputStream(selectedFile);
                Image image = new Image(input);
                
                // Vérifier si l'image a été chargée correctement
                if (image.isError()) {
                    System.err.println("Erreur lors du chargement de l'image");
                    showAlert(Alert.AlertType.ERROR, "Erreur", "L'image n'a pas pu être chargée correctement.");
                    input.close();
                    return;
                }
                
                imageView.setImage(image);
                input.close();
                
                System.out.println("Image chargée avec succès dans l'ImageView");
                
                // Sauvegarder le chemin du fichier
                switch (fileType) {
                    case "cin": 
                        cinPath = selectedFile.getAbsolutePath();
                        System.out.println("CIN path saved: " + cinPath);
                        break;
                    case "photo": 
                        photoPath = selectedFile.getAbsolutePath();
                        System.out.println("Photo path saved: " + photoPath);
                        break;
                    case "diplomeBac": 
                        diplomeBacPath = selectedFile.getAbsolutePath();
                        System.out.println("Diplome Bac path saved: " + diplomeBacPath);
                        break;
                    case "releveNote": 
                        releveNotePath = selectedFile.getAbsolutePath();
                        System.out.println("Releve Note path saved: " + releveNotePath);
                        break;
                    case "diplomeObtenu": 
                        diplomeObtenuPath = selectedFile.getAbsolutePath();
                        System.out.println("Diplome Obtenu path saved: " + diplomeObtenuPath);
                        break;
                    case "lettreMotivation": 
                        lettreMotivationPath = selectedFile.getAbsolutePath();
                        System.out.println("Lettre Motivation path saved: " + lettreMotivationPath);
                        break;
                    case "dossierSante": 
                        dossierSantePath = selectedFile.getAbsolutePath();
                        System.out.println("Dossier Sante path saved: " + dossierSantePath);
                        break;
                    case "cv": 
                        cvPath = selectedFile.getAbsolutePath();
                        System.out.println("CV path saved: " + cvPath);
                        break;
                }
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'image: " + e.getMessage());
            }
        } else {
            System.out.println("Aucun fichier sélectionné");
        }
    }

    @FXML void handleUploadCin(ActionEvent event) { handleFileUpload(event, cinPreview, "cin"); }
    @FXML void handleUploadPhoto(ActionEvent event) { handleFileUpload(event, photoPreview, "photo"); }
    @FXML void handleUploadDiplomeBac(ActionEvent event) { handleFileUpload(event, diplomeBacPreview, "diplomeBac"); }
    @FXML void handleUploadReleveNote(ActionEvent event) { handleFileUpload(event, releveNotePreview, "releveNote"); }
    @FXML void handleUploadDiplomeObtenu(ActionEvent event) { handleFileUpload(event, diplomeObtenuPreview, "diplomeObtenu"); }
    @FXML void handleUploadLettreMotivation(ActionEvent event) { handleFileUpload(event, lettreMotivationPreview, "lettreMotivation"); }
    @FXML void handleUploadDossierSante(ActionEvent event) { handleFileUpload(event, dossierSantePreview, "dossierSante"); }
    @FXML void handleUploadCv(ActionEvent event) { handleFileUpload(event, cvPreview, "cv"); }

    private void sendConfirmationEmail(Dossier dossier) {
        try {
            // Formater la date pour l'affichage
            String dateDepot = dossier.getDatedepot().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            // Générer le contenu HTML de l'email sans l'ID du dossier
            String emailContent = EmailSender.generateDossierConfirmationEmail(dateDepot);
            
            // Générer le contenu du code QR sans l'ID du dossier
            String qrContent = EmailSender.generateDossierQRContent(dateDepot);
            
            // Nom du fichier QR code
            String qrFileName = "dossier_" + dossier.getId_dossier() + ".png";
            
            // Récupérer l'email de l'étudiant depuis la base de données
            String email = getStudentEmail(dossier.getId_etudiant());
            
            if (email != null) {
                // Envoyer l'email avec le code QR en pièce jointe
                boolean sent = EmailSender.sendEmail(
                    email, 
                    "Confirmation de dépôt de dossier - GradAway", 
                    emailContent,
                    qrContent,
                    qrFileName
                );
                
                if (sent) {
                    System.out.println("Email de confirmation avec code QR envoyé avec succès à " + email);
                } else {
                    System.err.println("Erreur lors de l'envoi de l'email de confirmation à " + email);
                }
            } else {
                System.err.println("Impossible de trouver l'email de l'étudiant avec l'ID: " + dossier.getId_etudiant());
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getStudentEmail(int studentId) {
        try {
            return serviceUser.getUserById(studentId).getEmail();
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'email de l'étudiant: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    void handleSubmit(ActionEvent event) {
        if (currentEtudiantId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Soumission", "ID de l'étudiant non défini ou invalide.");
            return;
        }

        try {
            if (serviceDossier.recupererParEtudiantId(currentEtudiantId) != null) {
                showAlert(Alert.AlertType.ERROR, "Erreur de Soumission", "Un dossier existe déjà pour cet étudiant.");
                submitButton.setDisable(true);
                return;
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Base de Données", "Impossible de vérifier l'existence d'un dossier avant la soumission.\n" + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Vérifier si toutes les images ont été sélectionnées
        if (cinPath == null || photoPath == null || diplomeBacPath == null || 
            releveNotePath == null || diplomeObtenuPath == null || lettreMotivationPath == null || 
            dossierSantePath == null || cvPath == null || dateDepotPicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Champs Incomplets", "Veuillez sélectionner toutes les images requises.");
            return;
        }

        try {
            // Créer et sauvegarder le dossier
            Dossier dossier = new Dossier(
                currentEtudiantId,
                cinPath,
                photoPath,
                diplomeBacPath,
                releveNotePath,
                diplomeObtenuPath,
                lettreMotivationPath,
                dossierSantePath,
                cvPath,
                dateDepotPicker.getValue()
            );

            serviceDossier.ajouter(dossier);
            // Récupérer le dossier avec son ID
            Dossier savedDossier = serviceDossier.recupererParEtudiantId(currentEtudiantId);
            if (savedDossier != null) {
                // Envoyer l'email de confirmation avec le code QR
                sendConfirmationEmail(savedDossier);
            }

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Dossier ajouté avec succès !");
            clearForm();
            submitButton.setDisable(true);
            viewDossierButton.setDisable(false);
            MqttService mqttService = new MqttService();
            ServiceUser serviceUser = new ServiceUser();
            mqttService.publishSms(String.valueOf(serviceUser.getUserById(currentEtudiantId).getTelephone()), "Bonjour" + serviceUser.getUserById(currentEtudiantId).getNom()+ "✅ Le dossier a ete cree avec succes.");
            mqttService.disconnect();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Base de Données", "Échec de l'ajout du dossier à la base de données: \n" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleViewDossier(ActionEvent event) {
        if (currentEtudiantId <= 0) {
            showAlert(Alert.AlertType.WARNING, "Action Impossible", "L'ID de l'étudiant n'est pas défini.");
            return;
        }

        try {
            System.out.println("AjoutDossierController: Opening AfficherDossier view for Etudiant ID: " + currentEtudiantId);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDossier.fxml"));
            Parent root = loader.load();

            AfficherDossierController afficherController = loader.getController();
            afficherController.setEtudiantId(currentEtudiantId);

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Mon Dossier");
            stage.setMinWidth(900);
            stage.setMinHeight(700);
            stage.setResizable(true);
            stage.centerOnScreen();
          
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Chargement FXML", "Impossible de charger la vue 'Afficher Dossier'.");
            System.err.println("Error loading AfficherDossier.fxml:");
            e.printStackTrace();
        } catch (Exception e) {
             showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur s'est produite en ouvrant la vue du dossier.");
             System.err.println("Unexpected error opening AfficherDossier view:");
             e.printStackTrace();
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        clearForm();
         Stage stage = (Stage) cancelButton.getScene().getWindow();
         stage.close();
        System.out.println("Ajout annulé.");
    }

    private void clearForm() {
        cinPreview.setImage(null);
        photoPreview.setImage(null);
        diplomeBacPreview.setImage(null);
        releveNotePreview.setImage(null);
        diplomeObtenuPreview.setImage(null);
        lettreMotivationPreview.setImage(null);
        dossierSantePreview.setImage(null);
        cvPreview.setImage(null);
        dateDepotPicker.setValue(LocalDate.now());
        
        cinPath = null;
        photoPath = null;
        diplomeBacPath = null;
        releveNotePath = null;
        diplomeObtenuPath = null;
        lettreMotivationPath = null;
        dossierSantePath = null;
        cvPath = null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
            alert.initOwner(rootPane.getScene().getWindow());
        } else {
            System.err.println("Warning: Could not set owner for alert dialog. Root pane or scene not ready.");
        }
        alert.showAndWait();
    }

    @FXML
    public void acceuilbutton(ActionEvent actionEvent) {
        try {
            System.out.println("AjoutDossierController: Opening Accueil view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
            Parent root = loader.load();

            // Get the controller and set the user ID
            Acceuilcontroller controller = loader.getController();
            controller.setUserId(this.currentEtudiantId);

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil - GradAway");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("AjoutDossierController: Error loading accueil view: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la page d'accueil.");
            e.printStackTrace();
        }
    }

    @FXML
    public void userbutton(ActionEvent actionEvent) {
        if (this.currentEtudiantId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID utilisateur invalide. Impossible d'ouvrir le profil.");
            return;
        }

        try {
            System.out.println("AjoutDossierController: Opening EditProfile view for User ID: " + this.currentEtudiantId);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();

            EditProfileController editProfileController = loader.getController();
            editProfileController.setUserId(this.currentEtudiantId);

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Mon Profil");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("AjoutDossierController: Error loading EditProfile.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue du profil.");
            e.printStackTrace();
        }
    }

    @FXML
    public void universitébutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminconditature.fxml"));
            Parent root = loader.load();

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Candidatures");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("AjoutDossierController: Error loading adminconditature.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des candidatures.");
            e.printStackTrace();
        }
    }

    @FXML
    public void evenementbutton(ActionEvent actionEvent) {
        try {
            System.out.println("AjoutDossierController: Opening Affiche Evenement view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affiche_evenement.fxml"));
            Parent root = loader.load();

            Ajouterafficheevenementcontrolleur controller = loader.getController();
            controller.setCurrentUserId(this.currentEtudiantId);

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Événements");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("AjoutDossierController: Error loading affiche_evenement.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des événements.");
            e.printStackTrace();
        }
    }

    @FXML
    public void hebergementbutton(ActionEvent actionEvent) {
        try {
            System.out.println("AjoutDossierController: Opening ListFoyerClient view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Foyers");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("AjoutDossierController: Error loading ListFoyerClient.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des foyers.");
            e.printStackTrace();
        }
    }

    @FXML
    public void restaurantbutton(ActionEvent actionEvent) {
        try {
            System.out.println("AjoutDossierController: Opening ListRestaurantClient view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurantClient.fxml"));
            Parent root = loader.load();

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Restaurants");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("AjoutDossierController: Error loading ListRestaurantClient.fxml: " + e.getMessage());
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
            System.err.println("AjoutDossierController: Error loading login view: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion.");
            e.printStackTrace();
        }
    }

    @FXML
    public void volsbutton(ActionEvent actionEvent) {
        // To be implemented when the flights functionality is ready
        showAlert(Alert.AlertType.INFORMATION, "Information", "La fonctionnalité des vols sera bientôt disponible.");
    }

}