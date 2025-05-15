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
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class AfficherDossierController {

    @FXML private AnchorPane rootPane;
    @FXML private Label dateDepotLabel;
    @FXML private Label statusLabel;
    @FXML private Button modifierButton;
    @FXML private ImageView cinPreview;
    @FXML private ImageView photoPreview;
    @FXML private ImageView diplomeBacPreview;
    @FXML private ImageView releveNotePreview;
    @FXML private ImageView diplomeObtenuPreview;
    @FXML private ImageView lettreMotivationPreview;
    @FXML private ImageView dossierSantePreview;
    @FXML private ImageView cvPreview;

    private ServiceDossier serviceDossier;
    private int currentEtudiantId = -1;
    private Dossier displayedDossier;

    @FXML
    public void initialize() {
        System.out.println("Initialisation du contrôleur AfficherDossierController");
        serviceDossier = new ServiceDossier();
        statusLabel.setText("");
        modifierButton.setVisible(false);
        initializeImageViews();
    }

    public void setEtudiantId(int id) {
        this.currentEtudiantId = id;
        System.out.println("Current Etudiant ID set to: " + this.currentEtudiantId);
        loadDossier(id);
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

    public void loadDossier(int etudiantId) {
        this.currentEtudiantId = etudiantId;
        try {
            System.out.println("Chargement du dossier pour l'étudiant ID: " + etudiantId);
            Dossier dossier = serviceDossier.recupererParEtudiantId(etudiantId);
            if (dossier != null) {
                System.out.println("Dossier trouvé: " + dossier.getId_dossier());
                displayedDossier = dossier;
                displayDossier(dossier);
                modifierButton.setVisible(true);
            } else {
                System.out.println("Aucun dossier trouvé pour l'étudiant ID: " + etudiantId);
                clearImageViews();
                dateDepotLabel.setText("Aucun dossier");
                statusLabel.setText("Aucun dossier trouvé.");
                modifierButton.setVisible(false);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement du dossier: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger le dossier: " + e.getMessage());
            clearImageViews();
            dateDepotLabel.setText("Erreur");
            statusLabel.setText("Erreur lors du chargement du dossier.");
            modifierButton.setVisible(false);
        }
    }

    private void clearImageViews() {
        ImageView[] imageViews = {
            cinPreview, photoPreview, diplomeBacPreview, releveNotePreview,
            diplomeObtenuPreview, lettreMotivationPreview, dossierSantePreview, cvPreview
        };

        for (ImageView imageView : imageViews) {
            if (imageView != null) {
                imageView.setImage(null);
            }
        }
    }

    private void displayDossier(Dossier dossier) {
        try {
            System.out.println("Affichage du dossier: " + dossier.getId_dossier());
            // Charger les images
            loadImage(cinPreview, dossier.getCin());
            loadImage(photoPreview, dossier.getPhoto());
            loadImage(diplomeBacPreview, dossier.getDiplome_baccalauréat());
            loadImage(releveNotePreview, dossier.getReleve_note());
            loadImage(diplomeObtenuPreview, dossier.getDiplome_obtenus());
            loadImage(lettreMotivationPreview, dossier.getLettre_motivations());
            loadImage(dossierSantePreview, dossier.getDossier_sante());
            loadImage(cvPreview, dossier.getCv());

            // Afficher la date de dépôt
            if (dossier.getDatedepot() != null) {
                dateDepotLabel.setText(dossier.getDatedepot().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            } else {
                dateDepotLabel.setText("N/A");
            }

            statusLabel.setText("Dossier chargé avec succès.");
            modifierButton.setVisible(true);
            System.out.println("Dossier affiché avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage du dossier: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher le dossier: " + e.getMessage());
        }
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

    @FXML
    void handleModifierDossier(ActionEvent event) {
        if (displayedDossier == null) {
            showAlert(Alert.AlertType.WARNING, "Action Impossible", "Aucun dossier n'est chargé pour la modification.");
            return;
        }

        try {
            System.out.println("AfficherDossierController: Opening ModifierDossier view.");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierDossier.fxml"));
            Parent root = loader.load();

            ModifierDossierController modifierController = loader.getController();
            modifierController.loadDossierData(displayedDossier);

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Mon Dossier");
            stage.setMinWidth(1144);
            stage.setMinHeight(696);
            stage.setResizable(true);
            stage.centerOnScreen();

            // Add a listener to detect when the modification window is closed
            stage.setOnCloseRequest(e -> {
                System.out.println("AfficherDossierController: Refreshing data after modification window closed.");
                loadDossier(currentEtudiantId);
            });

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Chargement FXML", "Impossible de charger la vue 'Modifier Dossier'.");
            System.err.println("Error loading ModifierDossier.fxml:");
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur s'est produite en ouvrant la vue de modification.");
            System.err.println("Unexpected error opening ModifierDossier view:");
            e.printStackTrace();
        }
    }

    @FXML
    public void acceuilbutton(ActionEvent actionEvent) {
        try {
            System.out.println("AfficherDossierController: Opening Accueil view");
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
            System.err.println("AfficherDossierController: Error loading accueil view: " + e.getMessage());
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
            System.out.println("AfficherDossierController: Opening EditProfile view for User ID: " + this.currentEtudiantId);
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
            System.err.println("AfficherDossierController: Error loading EditProfile.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue du profil.");
            e.printStackTrace();
        }
    }

    @FXML
    public void dossierbutton(ActionEvent actionEvent) {
        try {
            System.out.println("AfficherDossierController: Opening AjoutDossier view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDossier.fxml"));
            Parent root = loader.load();

            // Get the controller and set the user ID
            AjoutDossierController controller = loader.getController();
            controller.setEtudiantId(this.currentEtudiantId);

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajout Dossier");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("AfficherDossierController: Error loading AjoutDossier.fxml: " + e.getMessage());
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
            System.err.println("AfficherDossierController: Error loading adminconditature.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des candidatures.");
            e.printStackTrace();
        }
    }

    @FXML
    public void evenementbutton(ActionEvent actionEvent) {
        try {
            System.out.println("AfficherDossierController: Opening Affiche Evenement view");
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
            System.err.println("AfficherDossierController: Error loading affiche_evenement.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des événements.");
            e.printStackTrace();
        }
    }

    @FXML
    public void hebergementbutton(ActionEvent actionEvent) {
        try {
            System.out.println("AfficherDossierController: Opening ListFoyerClient view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Foyers");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("AfficherDossierController: Error loading ListFoyerClient.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des foyers.");
            e.printStackTrace();
        }
    }

    @FXML
    public void restaurantbutton(ActionEvent actionEvent) {
        try {
            System.out.println("AfficherDossierController: Opening ListRestaurantClient view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurantClient.fxml"));
            Parent root = loader.load();

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Restaurants");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("AfficherDossierController: Error loading ListRestaurantClient.fxml: " + e.getMessage());
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
            System.err.println("AfficherDossierController: Error loading login view: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion.");
            e.printStackTrace();
        }
    }

    @FXML
    public void handleRetour(ActionEvent event) {
        try {
            System.out.println("AfficherDossierController: Opening AjoutDossier view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDossier.fxml"));
            Parent root = loader.load();

            // Get the controller and set the user ID
            AjoutDossierController controller = loader.getController();
            controller.setEtudiantId(this.currentEtudiantId);

            // Get the current stage and update its scene
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajout Dossier");
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("AfficherDossierController: Error loading AjoutDossier.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour à la page d'ajout de dossier.");
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() != null) {
            alert.initOwner(rootPane.getScene().getWindow());
        }
        alert.showAndWait();
    }
} 