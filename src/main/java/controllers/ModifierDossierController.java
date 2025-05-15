package controllers;

import Services.ServiceDossier;
import entities.Dossier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;

public class ModifierDossierController {

    @FXML private ImageView cinPreview;
    @FXML private ImageView photoPreview;
    @FXML private ImageView diplomeBacPreview;
    @FXML private ImageView releveNotePreview;
    @FXML private ImageView diplomeObtenuPreview;
    @FXML private ImageView lettreMotivationPreview;
    @FXML private ImageView dossierSantePreview;
    @FXML private ImageView cvPreview;
    @FXML private DatePicker dateDepotPicker;
    @FXML private Button updateButton;
    @FXML private Button cancelButton;
    @FXML private AnchorPane rootPane;

    private ServiceDossier serviceDossier;
    private Dossier currentDossier;
    private String cinPath;
    private String photoPath;
    private String diplomeBacPath;
    private String releveNotePath;
    private String diplomeObtenuPath;
    private String lettreMotivationPath;
    private String dossierSantePath;
    private String cvPath;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur ModifierDossierController");
        serviceDossier = new ServiceDossier();
        initializeImageViews();
    }

    private void initializeImageViews() {
        ImageView[] imageViews = {
            cinPreview, photoPreview, diplomeBacPreview, releveNotePreview,
            diplomeObtenuPreview, lettreMotivationPreview, dossierSantePreview, cvPreview
        };

        for (ImageView imageView : imageViews) {
            if (imageView != null) {
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                imageView.setStyle("-fx-background-color: #f0f0f0; -fx-background-radius: 5;");
            }
        }
    }

    public void loadDossierData(Dossier dossier) {
        if (dossier == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun dossier à modifier n'a été fourni.");
            updateButton.setDisable(true);
            return;
        }
        this.currentDossier = dossier;
        System.out.println("Chargement du dossier: " + dossier.getId_dossier());

        // Load images
        loadImage(cinPreview, dossier.getCin());
        loadImage(photoPreview, dossier.getPhoto());
        loadImage(diplomeBacPreview, dossier.getDiplome_baccalauréat());
        loadImage(releveNotePreview, dossier.getReleve_note());
        loadImage(diplomeObtenuPreview, dossier.getDiplome_obtenus());
        loadImage(lettreMotivationPreview, dossier.getLettre_motivations());
        loadImage(dossierSantePreview, dossier.getDossier_sante());
        loadImage(cvPreview, dossier.getCv());

        // Store paths
        cinPath = dossier.getCin();
        photoPath = dossier.getPhoto();
        diplomeBacPath = dossier.getDiplome_baccalauréat();
        releveNotePath = dossier.getReleve_note();
        diplomeObtenuPath = dossier.getDiplome_obtenus();
        lettreMotivationPath = dossier.getLettre_motivations();
        dossierSantePath = dossier.getDossier_sante();
        cvPath = dossier.getCv();

        // Désactiver la modification de la date de dépôt
        dateDepotPicker.setValue(dossier.getDatedepot());
        dateDepotPicker.setDisable(true);
        dateDepotPicker.setStyle("-fx-opacity: 0.7;");
        
        System.out.println("Chemins des fichiers chargés:");
        System.out.println("CIN: " + cinPath);
        System.out.println("Photo: " + photoPath);
        System.out.println("Diplôme Bac: " + diplomeBacPath);
        System.out.println("Relevé de Notes: " + releveNotePath);
        System.out.println("Diplômes Obtenus: " + diplomeObtenuPath);
        System.out.println("Lettre de Motivation: " + lettreMotivationPath);
        System.out.println("Dossier Santé: " + dossierSantePath);
        System.out.println("CV: " + cvPath);
    }

    private void loadImage(ImageView imageView, String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File file = new File(imagePath);
                if (file.exists()) {
                    System.out.println("Chargement de l'image: " + imagePath);
                    FileInputStream input = new FileInputStream(file);
                    Image image = new Image(input);
                    imageView.setImage(image);
                    input.close();
                } else {
                    System.err.println("Le fichier n'existe pas: " + imagePath);
                    imageView.setImage(null);
                }
            } catch (IOException e) {
                System.err.println("Erreur lors du chargement de l'image " + imagePath + ": " + e.getMessage());
                e.printStackTrace();
                imageView.setImage(null);
            }
        } else {
            imageView.setImage(null);
        }
    }

    private void handleFileUpload(ActionEvent event, ImageView imageView, String[] paths) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            try {
                System.out.println("Fichier sélectionné: " + selectedFile.getAbsolutePath());
                FileInputStream input = new FileInputStream(selectedFile);
                Image image = new Image(input);
                imageView.setImage(image);
                input.close();
                
                // Mettre à jour le chemin du fichier
                String newPath = selectedFile.getAbsolutePath();
                paths[0] = newPath;
                System.out.println("Nouveau chemin enregistré: " + newPath);
                
                // Mettre à jour le chemin correspondant dans le dossier
                if (imageView == cinPreview) {
                    cinPath = newPath;
                } else if (imageView == photoPreview) {
                    photoPath = newPath;
                } else if (imageView == diplomeBacPreview) {
                    diplomeBacPath = newPath;
                } else if (imageView == releveNotePreview) {
                    releveNotePath = newPath;
                } else if (imageView == diplomeObtenuPreview) {
                    diplomeObtenuPath = newPath;
                } else if (imageView == lettreMotivationPreview) {
                    lettreMotivationPath = newPath;
                } else if (imageView == dossierSantePreview) {
                    dossierSantePath = newPath;
                } else if (imageView == cvPreview) {
                    cvPath = newPath;
                }
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger l'image: " + e.getMessage());
            }
        }
    }

    @FXML void handleUploadCin(ActionEvent event) { handleFileUpload(event, cinPreview, new String[]{cinPath}); }
    @FXML void handleUploadPhoto(ActionEvent event) { handleFileUpload(event, photoPreview, new String[]{photoPath}); }
    @FXML void handleUploadDiplomeBac(ActionEvent event) { handleFileUpload(event, diplomeBacPreview, new String[]{diplomeBacPath}); }
    @FXML void handleUploadReleveNote(ActionEvent event) { handleFileUpload(event, releveNotePreview, new String[]{releveNotePath}); }
    @FXML void handleUploadDiplomeObtenu(ActionEvent event) { handleFileUpload(event, diplomeObtenuPreview, new String[]{diplomeObtenuPath}); }
    @FXML void handleUploadLettreMotivation(ActionEvent event) { handleFileUpload(event, lettreMotivationPreview, new String[]{lettreMotivationPath}); }
    @FXML void handleUploadDossierSante(ActionEvent event) { handleFileUpload(event, dossierSantePreview, new String[]{dossierSantePath}); }
    @FXML void handleUploadCv(ActionEvent event) { handleFileUpload(event, cvPreview, new String[]{cvPath}); }

    @FXML
    void handleUpdateDossier(ActionEvent event) {
        if (currentDossier == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun dossier chargé pour la modification.");
            return;
        }

        // Validation
        if (cinPath == null || photoPath == null || diplomeBacPath == null || 
            releveNotePath == null || diplomeObtenuPath == null || lettreMotivationPath == null || 
            dossierSantePath == null || cvPath == null || dateDepotPicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Champs Incomplets", "Veuillez remplir tous les champs et sélectionner tous les fichiers requis.");
            return;
        }

        try {
            System.out.println("Mise à jour du dossier avec les chemins suivants:");
            System.out.println("CIN: " + cinPath);
            System.out.println("Photo: " + photoPath);
            System.out.println("Diplôme Bac: " + diplomeBacPath);
            System.out.println("Relevé de Notes: " + releveNotePath);
            System.out.println("Diplômes Obtenus: " + diplomeObtenuPath);
            System.out.println("Lettre de Motivation: " + lettreMotivationPath);
            System.out.println("Dossier Santé: " + dossierSantePath);
            System.out.println("CV: " + cvPath);

            Dossier updatedDossier = new Dossier(
                currentDossier.getId_dossier(),
                currentDossier.getId_etudiant(),
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

            serviceDossier.modifier(updatedDossier);
            System.out.println("Dossier mis à jour avec succès!");
            
            // Mettre à jour le dossier courant
            this.currentDossier = updatedDossier;
            
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Dossier mis à jour avec succès!");

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour du dossier: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur Base de Données", "Échec de la mise à jour du dossier: \n" + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de la mise à jour du dossier: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue: " + e.getMessage());
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        try {
            System.out.println("ModifierDossierController: Opening AfficherDossier view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherDossier.fxml"));
            Parent root = loader.load();

            // Get the controller and set the user ID
            AfficherDossierController controller = loader.getController();
            
            // Vérification de sécurité pour s'assurer que currentDossier n'est pas null
            if (this.currentDossier != null && this.currentDossier.getId_etudiant() > 0) {
                controller.setEtudiantId(this.currentDossier.getId_etudiant());
                System.out.println("Etudiant ID set to: " + this.currentDossier.getId_etudiant());
            } else {
                System.err.println("Warning: currentDossier is null or has invalid ID");
                showAlert(Alert.AlertType.WARNING, "Attention", "Impossible de récupérer l'ID de l'étudiant.");
                return;
            }

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Afficher Dossier");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("ModifierDossierController: Error loading AfficherDossier view: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la page d'affichage du dossier.");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void acceuilbutton(ActionEvent actionEvent) {
        try {
            System.out.println("ModifierDossierController: Opening Accueil view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
            Parent root = loader.load();

            // Get the controller and set the user ID
            Acceuilcontroller controller = loader.getController();
            controller.setUserId(this.currentDossier.getId_etudiant());

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil - GradAway");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("ModifierDossierController: Error loading accueil view: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la page d'accueil.");
            e.printStackTrace();
        }
    }

    @FXML
    public void userbutton(ActionEvent actionEvent) {
        if (this.currentDossier == null || this.currentDossier.getId_etudiant() <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID utilisateur invalide. Impossible d'ouvrir le profil.");
            return;
        }

        try {
            System.out.println("ModifierDossierController: Opening EditProfile view for User ID: " + this.currentDossier.getId_etudiant());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();

            EditProfileController editProfileController = loader.getController();
            editProfileController.setUserId(this.currentDossier.getId_etudiant());

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Mon Profil");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("ModifierDossierController: Error loading EditProfile.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue du profil.");
            e.printStackTrace();
        }
    }

    @FXML
    public void dossierbutton(ActionEvent actionEvent) {
        try {
            System.out.println("ModifierDossierController: Opening AjoutDossier view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDossier.fxml"));
            Parent root = loader.load();

            // Get the controller and set the user ID
            AjoutDossierController controller = loader.getController();
            controller.setEtudiantId(this.currentDossier.getId_etudiant());

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajout Dossier");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("ModifierDossierController: Error loading AjoutDossier.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue du dossier.");
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
            System.err.println("ModifierDossierController: Error loading adminconditature.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des candidatures.");
            e.printStackTrace();
        }
    }

    @FXML
    public void evenementbutton(ActionEvent actionEvent) {
        try {
            System.out.println("ModifierDossierController: Opening Affiche Evenement view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affiche_evenement.fxml"));
            Parent root = loader.load();

            Ajouterafficheevenementcontrolleur controller = loader.getController();
            controller.setCurrentUserId(this.currentDossier.getId_etudiant());

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Événements");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("ModifierDossierController: Error loading affiche_evenement.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des événements.");
            e.printStackTrace();
        }
    }

    @FXML
    public void hebergementbutton(ActionEvent actionEvent) {
        try {
            System.out.println("ModifierDossierController: Opening ListFoyerClient view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Foyers");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("ModifierDossierController: Error loading ListFoyerClient.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des foyers.");
            e.printStackTrace();
        }
    }

    @FXML
    public void restaurantbutton(ActionEvent actionEvent) {
        try {
            System.out.println("ModifierDossierController: Opening ListRestaurantClient view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurantClient.fxml"));
            Parent root = loader.load();

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Restaurants");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("ModifierDossierController: Error loading ListRestaurantClient.fxml: " + e.getMessage());
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
        // To be implemented when the flights functionality is ready
        showAlert(Alert.AlertType.INFORMATION, "Information", "La fonctionnalité des vols sera bientôt disponible.");
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
            System.err.println("ModifierDossierController: Error loading login view: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion.");
            e.printStackTrace();
        }
    }
} 