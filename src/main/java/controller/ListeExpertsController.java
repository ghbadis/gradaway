package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import entities.Expert;
import Services.ServiceExpert;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.FileInputStream;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.event.ActionEvent;
import java.io.IOException;

public class ListeExpertsController {
    @FXML
    private FlowPane cardsPane;
    @FXML
    private Button ajouterButton;
    @FXML
    private Button fermerButton;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> specialiteComboBox;
    @FXML
    private Button searchButton;

    private final ServiceExpert serviceExpert = new ServiceExpert();
    private ObservableList<Expert> expertsData = FXCollections.observableArrayList();
    private ObservableList<Expert> allExperts = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            // Load specialties from database for filter
            List<String> domaines = serviceExpert.recupererDomaines();
            specialiteComboBox.getItems().add("Toutes"); // Keep the "All" option
            specialiteComboBox.getItems().addAll(domaines);
            specialiteComboBox.setValue("Toutes");
        } catch (SQLException e) {
            System.err.println("Error loading domains: " + e.getMessage());
            // Fallback to default values if database load fails
            specialiteComboBox.getItems().add("Toutes");
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
            specialiteComboBox.setValue("Toutes");
        }

        // Configure event handlers
        specialiteComboBox.setOnAction(e -> filterExperts());
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterExperts());

        // Configurer les boutons
        ajouterButton.setOnAction(event -> ouvrirFenetreAjout());
        fermerButton.setOnAction(event -> fermerFenetre());

        // Charger les données
        chargerDonnees();
    }

    private void chargerDonnees() {
        try {
            List<Expert> experts = serviceExpert.recuperer();
            allExperts.clear();
            allExperts.addAll(experts);
            expertsData.setAll(experts);
            refreshCards(expertsData);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des experts: " + e.getMessage());
        }
    }

    private void refreshCards(List<Expert> experts) {
        cardsPane.getChildren().clear();
        int count = 0;
        for (Expert expert : experts) {
            VBox card = createExpertCard(expert);
            cardsPane.getChildren().add(card);
            count++;
        }
    }

    private VBox createExpertCard(Expert expert) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: #1a237e; -fx-background-radius: 16; -fx-padding: 18; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 10);");
        card.setPrefWidth(320);
        card.setMaxWidth(320);
        card.setMinWidth(320);
        card.setAlignment(Pos.TOP_CENTER);

        // Create a container for the photo with rounded corners
        VBox photoContainer = new VBox();
        photoContainer.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        photoContainer.setPrefHeight(200);
        photoContainer.setMaxHeight(200);
        photoContainer.setAlignment(Pos.CENTER);

        // Photo view with proper sizing and clipping
        ImageView photoView = new ImageView();
        photoView.setFitHeight(180);
        photoView.setFitWidth(280);
        photoView.setPreserveRatio(true);
        photoView.setSmooth(true);

        // Create clip for rounded corners
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(
            280, 180
        );
        clip.setArcWidth(16);
        clip.setArcHeight(16);
        photoView.setClip(clip);

        // Default image path
        String defaultImagePath = "/images/default-avatar.png";
        boolean imageSet = false;

        // Try to load the expert's photo if available
        if (expert.getPhotoPath() != null && !expert.getPhotoPath().isEmpty()) {
            try {
                Path photoPath;
                if (Paths.get(expert.getPhotoPath()).isAbsolute()) {
                    // If it's an absolute path, use it directly
                    photoPath = Paths.get(expert.getPhotoPath());
                } else {
                    // If it's a relative path, resolve it against the current working directory
                    photoPath = Paths.get(System.getProperty("user.dir"), expert.getPhotoPath());
                }

                if (Files.exists(photoPath)) {
                    System.out.println("[DEBUG] Loading photo from: " + photoPath);
                    Image expertImage = new Image(photoPath.toUri().toString());
                            if (!expertImage.isError()) {
                                photoView.setImage(expertImage);
                                imageSet = true;
                                System.out.println("[DEBUG] Successfully loaded photo from file");
                    } else {
                        System.err.println("[ERROR] Failed to load image: " + photoPath);
                    }
                } else {
                    System.err.println("[ERROR] Photo file not found: " + photoPath);
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Error loading expert photo: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Load default image if no photo was set
        if (!imageSet) {
            try {
                java.net.URL defaultUrl = getClass().getResource("/images/default-avatar.png");
                if (defaultUrl != null) {
                    System.out.println("[DEBUG] Loading default avatar");
                    Image defaultImage = new Image(defaultUrl.toExternalForm());
                    photoView.setImage(defaultImage);
                } else {
                    System.err.println("[ERROR] Default avatar resource not found");
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Error loading default avatar: " + e.getMessage());
            }
        }

        photoContainer.getChildren().add(photoView);

        // Name bold and centered
        Label nameLabel = new Label(expert.getNom_expert() + " " + expert.getPrenom_expert());
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 10 0 0 0;");
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(Double.MAX_VALUE);

        // Info below
        Label specialiteLabel = new Label("Spécialité: " + (expert.getSpecialite() != null ? expert.getSpecialite() : "-"));
        specialiteLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #e3e3e3;");
        Label emailLabel = new Label("Email: " + (expert.getEmail() != null ? expert.getEmail() : "-"));
        emailLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #e3e3e3;");
        Label telephoneLabel = new Label("Téléphone: " + (expert.getTelephone() != null ? expert.getTelephone() : "-"));
        telephoneLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #e3e3e3;");
        Label experienceLabel = new Label("Expérience: " + (expert.getAnneeExperience() > 0 ? expert.getAnneeExperience() + " ans" : "-"));
        experienceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #e3e3e3;");

        VBox infoBox = new VBox(2, specialiteLabel, emailLabel, telephoneLabel, experienceLabel);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setStyle("-fx-padding: 8 0 8 0;");

        // Buttons at the bottom
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        Button modifierBtn = new Button("Modifier");
        modifierBtn.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 24;");
        modifierBtn.setOnAction(event -> ouvrirFenetreModification(expert));
        Button supprimerBtn = new Button("Supprimer");
        supprimerBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 6 24;");
        supprimerBtn.setOnAction(event -> supprimerExpert(expert));
        buttonBox.getChildren().addAll(modifierBtn, supprimerBtn);

        card.getChildren().setAll(photoContainer, nameLabel, infoBox, buttonBox);
        card.setSpacing(12);
        return card;
    }

    private void filterExperts() {
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String specialite = specialiteComboBox.getValue();
        expertsData.setAll(
            allExperts.filtered(expert ->
                (search.isEmpty() ||
                    (expert.getNom_expert() != null && expert.getNom_expert().toLowerCase().contains(search)) ||
                    (expert.getPrenom_expert() != null && expert.getPrenom_expert().toLowerCase().contains(search)) ||
                    (expert.getEmail() != null && expert.getEmail().toLowerCase().contains(search))
                ) &&
                (specialite == null || specialite.equals("Toutes") || specialite.isEmpty() || specialite.equals(expert.getSpecialite()))
            )
        );
        refreshCards(expertsData);
    }

    private void ouvrirFenetreAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreerExpert.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Expert");
            stage.setScene(new Scene(root));
            stage.show();
            
            stage.setOnHidden(event -> chargerDonnees());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre d'ajout: " + e.getMessage());
        }
    }

    private void ouvrirFenetreModification(Expert expert) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierExpert.fxml"));
            Parent root = loader.load();
            ModifierExpertController controller = loader.getController();
            controller.setExpert(expert);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier un Expert");
            stage.setScene(new Scene(root));
            stage.show();
            
            stage.setOnHidden(event -> chargerDonnees());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre de modification: " + e.getMessage());
        }
    }

    private void supprimerExpert(Expert expert) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer l'expert " + expert.getNom_expert() + " " + expert.getPrenom_expert() + " ?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                serviceExpert.supprimer(expert);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "L'expert a été supprimé avec succès");
                chargerDonnees();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression de l'expert: " + e.getMessage());
            }
        }
    }

    private void fermerFenetre() {
        Stage stage = (Stage) fermerButton.getScene().getWindow();
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