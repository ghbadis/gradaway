package controllers;

import Services.ServiceFoyer;
import entities.Foyer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ListFoyerControllers {
    
    /**
     * Méthode statique pour charger correctement l'interface ListFoyer.fxml
     * Cette méthode essaie plusieurs chemins possibles pour trouver le fichier FXML
     */
    /**
     * Charge le fichier FXML et configure la scène en plein écran
     * @param stage La fenêtre à configurer en plein écran
     * @return La scène configurée
     * @throws IOException Si le fichier FXML ne peut pas être chargé
     */
    public static Scene loadFXMLAndSetFullScreen(Stage stage) throws IOException {
        Parent root = loadFXML();
        Scene scene = new Scene(root);
        
        // Configurer la fenêtre en plein écran
        stage.setMaximized(true);
        stage.setFullScreen(false); // Plein écran sans le mode F11
        
        // Ajouter une transition de fondu pour une navigation plus fluide
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
        
        return scene;
    }
    
    /**
     * Méthode statique pour charger correctement l'interface ListFoyer.fxml
     * Cette méthode essaie plusieurs chemins possibles pour trouver le fichier FXML
     */
    public static Parent loadFXML() throws IOException {
        // Essayer plusieurs chemins possibles pour trouver le fichier FXML
        FXMLLoader loader = new FXMLLoader(ListFoyerControllers.class.getResource("/ListFoyer.fxml"));
        
        if (loader.getLocation() == null) {
            // Si le premier chemin échoue, essayer un autre
            loader = new FXMLLoader(ListFoyerControllers.class.getResource("/main/resources/ListFoyer.fxml"));
            
            if (loader.getLocation() == null) {
                // Si le deuxième chemin échoue, essayer sans le slash
                loader = new FXMLLoader(ListFoyerControllers.class.getResource("ListFoyer.fxml"));
                
                if (loader.getLocation() == null) {
                    // Si le troisième chemin échoue, essayer avec views/
                    loader = new FXMLLoader(ListFoyerControllers.class.getResource("/views/ListFoyer.fxml"));
                    
                    if (loader.getLocation() == null) {
                        throw new IOException("Impossible de trouver le fichier FXML ListFoyer.fxml");
                    }
                }
            }
        }
        
        return loader.load();
    }

    @FXML private GridPane foyerGrid;
    @FXML private TextField searchField;
    @FXML private MenuButton locationMenu;
    @FXML private Button btnListeReservation;
    @FXML private Label messageLabel;
    
    private final ServiceFoyer serviceFoyer = new ServiceFoyer();

    // Dashboard navigation buttons
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

    @FXML
    public void initialize() {
        try {
            // Setup dashboard navigation
            setupNavigationButtons();

            // Vérifier si locationMenu existe avant d'essayer d'y accéder
            if (locationMenu != null) {
                // Setup location menu items with event handlers
                for (MenuItem item : locationMenu.getItems()) {
                    item.setOnAction(e -> filterByLocation(item.getText()));
                }
            }

            // Setup search field listener si searchField existe
            if (searchField != null) {
                searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                    try {
                        searchFoyers(newVal);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage(), Alert.AlertType.ERROR);
                    }
                });
            }

            // Initial display
            displayFoyers(serviceFoyer.recuperer());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'initialisation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void displayFoyers(List<Foyer> foyers) {
        foyerGrid.getChildren().clear();
        
        // Définir les contraintes de colonnes pour avoir exactement 3 colonnes
        foyerGrid.getColumnConstraints().clear();
        for (int i = 0; i < 3; i++) {
            javafx.scene.layout.ColumnConstraints columnConstraints = new javafx.scene.layout.ColumnConstraints();
            columnConstraints.setPercentWidth(33.33); // Chaque colonne prend 1/3 de la largeur
            columnConstraints.setFillWidth(true);
            columnConstraints.setHgrow(javafx.scene.layout.Priority.ALWAYS);
            foyerGrid.getColumnConstraints().add(columnConstraints);
        }
        
        int column = 0;
        int row = 0;

        for (Foyer foyer : foyers) {
            VBox foyerCard = createFoyerCard(foyer);
            
            // Add click handler
            foyerCard.setOnMouseClicked(e -> openModifierFoyer(foyer));
            
            // Pas besoin d'ajouter un style ici car il est déjà défini dans createFoyerCard
            // Ajouter un effet de survol
            foyerCard.setOnMouseEntered(e -> {
                foyerCard.setStyle("-fx-background-color: #2c4c7c; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);");
            });
            foyerCard.setOnMouseExited(e -> {
                foyerCard.setStyle("-fx-background-color: #1a365d; -fx-background-radius: 10; -fx-padding: 15;");
            });

            foyerGrid.add(foyerCard, column, row);

            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createFoyerCard(Foyer foyer) {
        VBox card = new VBox(15);
        card.setPrefWidth(300);
        card.setPrefHeight(400);
        card.setMinWidth(280);
        card.setMaxWidth(320);
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setStyle("-fx-background-color: #1a365d; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 5);");
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true); // Préserver le ratio pour éviter la déformation
        
        // Gérer l'image de manière plus robuste
        boolean imageLoaded = false;
        if (foyer.getImage() != null && !foyer.getImage().isEmpty()) {
            try {
                // Vérifier si l'image est une URL complète ou un chemin relatif
                String imagePath = foyer.getImage();
                if (!imagePath.startsWith("http") && !imagePath.startsWith("file:")) {
                    // Essayer de charger depuis les ressources
                    java.io.File file = new java.io.File("src/main/resources/" + imagePath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        imageView.setImage(image);
                        imageLoaded = true;
                    }
                } else {
                    // C'est une URL ou un chemin absolu
                    Image image = new Image(imagePath);
                    imageView.setImage(image);
                    imageLoaded = true;
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            }
        }
        
        if (!imageLoaded) {
            // Image par défaut si aucune image n'est disponible ou si le chargement échoue
            try {
                // Essayer plusieurs chemins pour l'image par défaut
                java.io.InputStream is = getClass().getResourceAsStream("/images/default_foyer.jpg");
                if (is == null) {
                    is = getClass().getResourceAsStream("/images/placeholder-foyer.png");
                }
                if (is == null) {
                    is = getClass().getResourceAsStream("/images/foyer-default.png");
                }
                if (is == null) {
                    is = getClass().getResourceAsStream("/placeholder/placeholder.png");
                }
                
                if (is != null) {
                    Image defaultImage = new Image(is);
                    imageView.setImage(defaultImage);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image par défaut: " + e.getMessage());
            }
        }
        
        // Ajouter un effet d'ombre et de bordure à l'image
        imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0); -fx-background-radius: 10;");
        
        // Créer un conteneur pour l'image avec un fond blanc et des coins arrondis
        VBox imageContainer = new VBox();
        imageContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 10;");
        imageContainer.setPrefWidth(270);
        imageContainer.setPrefHeight(200);
        imageContainer.setAlignment(javafx.geometry.Pos.CENTER);
        imageContainer.getChildren().add(imageView);

        // Nom du foyer en grand et en gras, centré
        Label nameLabel = new Label(foyer.getNom());
        nameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-alignment: center;");
        nameLabel.setAlignment(javafx.geometry.Pos.CENTER);
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        
        // Ville et pays
        Label locationLabel = new Label(foyer.getVille() + ", " + foyer.getPays());
        locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #a0c8f0;");
        
        // Adresse
        Label adresseLabel = new Label("Adresse: " + foyer.getAdresse());
        adresseLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        
        // Capacité
        Label capacityLabel = new Label("Capacité: " + foyer.getCapacite() + " personnes");
        capacityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        
        // Nombre de chambres
        Label roomsLabel = new Label("Chambres: " + foyer.getNombreDeChambre());
        roomsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        
        // Nous supprimons l'affichage de l'ID comme demandé
        
        // Aucun bouton de modification ou de suppression
        
        // Créer un conteneur pour les informations textuelles
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        infoBox.getChildren().addAll(locationLabel, adresseLabel, capacityLabel, roomsLabel);
        
        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(imageContainer, nameLabel, infoBox);
        return card;
    }

    @FXML
    void search(ActionEvent event) {
        try {
            searchFoyers(searchField.getText());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void searchFoyers(String searchText) throws SQLException {
        List<Foyer> foyers = serviceFoyer.recuperer();
        foyers.removeIf(foyer -> 
            !foyer.getNom().toLowerCase().contains(searchText.toLowerCase()) &&
            !foyer.getVille().toLowerCase().contains(searchText.toLowerCase()) &&
            !foyer.getPays().toLowerCase().contains(searchText.toLowerCase())
        );
        
        // Afficher un message si aucun foyer n'est trouvé
        if (foyers.isEmpty() && !searchText.isEmpty()) {
            // Afficher le message dans le label au lieu d'une boîte de dialogue
            showMessage("Aucun foyer ne correspond à votre recherche.");
        } else {
            // Cacher le message s'il y a des résultats
            hideMessage();
        }
        
        displayFoyers(foyers);
    }

    private void filterByLocation(String location) {
        try {
            List<Foyer> foyers = serviceFoyer.recuperer();
            foyers.removeIf(foyer -> !foyer.getPays().equalsIgnoreCase(location));
            displayFoyers(foyers);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du filtrage: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void openModifierFoyer(Foyer foyer) {
        try {
            // Utiliser le chemin standardisé pour le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierFoyer.fxml"));
            Parent root = loader.load();
            
            // Configurer le contrôleur avec les données du foyer
            ModifierFoyerControllers controller = loader.getController();
            controller.initData(foyer);
            
            // Récupérer la fenêtre actuelle et changer sa scène
            Stage stage = (Stage) foyerGrid.getScene().getWindow();
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

    @FXML
    private void navigateToAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFoyer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Rafraîchit la liste des foyers sans afficher de message de confirmation
     */
    @FXML
    private void refreshFoyers(ActionEvent event) {
        try {
            // Récupérer la liste des foyers depuis la base de données
            List<Foyer> foyers = serviceFoyer.recuperer();
            
            // Afficher les foyers dans la grille
            displayFoyers(foyers);
            
            // Effacer le champ de recherche
            if (searchField != null) {
                searchField.clear();
            }
            
            // Cacher tout message précédemment affiché
            hideMessage();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'actualisation de la liste: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Affiche un message simple dans le label de l'interface
     * @param message Le message à afficher
     */
    private void showMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
            messageLabel.setVisible(true);
            
            // Style pour le message
            messageLabel.setStyle("-fx-text-fill: white; -fx-background-color: #e74c3c; -fx-padding: 5 10; -fx-background-radius: 5;");
        }
    }
    
    /**
     * Cache le message dans l'interface
     */
    private void hideMessage() {
        if (messageLabel != null) {
            messageLabel.setVisible(false);
        }
    }
    
    @FXML
    private void navigateToListeReservation() {
        try {
            System.out.println("Navigating to Liste Reservation...");
            // Try different ways to load the FXML file
            Parent root = null;
            try {
                // Try with leading slash
                root = FXMLLoader.load(getClass().getResource("/ListeReservation.fxml"));
            } catch (Exception e1) {
                try {
                    // Try without leading slash
                    root = FXMLLoader.load(getClass().getResource("ListeReservation.fxml"));
                } catch (Exception e2) {
                    try {
                        // Try with full path - this is a debugging approach
                        String fxmlPath = "file:///" + System.getProperty("user.dir").replace("\\", "/") + "/src/main/resources/ListeReservation.fxml";
                        System.out.println("Trying full path: " + fxmlPath);
                        root = FXMLLoader.load(new java.net.URL(fxmlPath));
                    } catch (Exception e3) {
                        // If all approaches fail, throw the original exception
                        throw e1;
                    }
                }
            }
            
            if (root != null) {
                Stage stage = (Stage) btnListeReservation.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                
                // Configurer la fenêtre en plein écran
                stage.setMaximized(true);
                
                // Add fade transition for smooth navigation
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            } else {
                throw new IOException("Could not load ListeReservation.fxml");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    // Reservation navigation method removed
    
    // Cette méthode est déjà définie plus haut
    
    private void deleteFoyer(Foyer foyer) {
        try {
            // Demander confirmation avant de supprimer
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation de suppression");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer le foyer " + foyer.getNom() + " ?");
            
            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                // Supprimer le foyer
                boolean success = serviceFoyer.supprimer(foyer);
                
                if (success) {
                    showAlert("Succès", "Le foyer a été supprimé avec succès.", Alert.AlertType.INFORMATION);
                    // Rafraîchir la liste
                    displayFoyers(serviceFoyer.recuperer());
                } else {
                    showAlert("Erreur", "Impossible de supprimer le foyer.", Alert.AlertType.ERROR);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression du foyer: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Méthode pour afficher l'interface des statistiques des réservations de foyers
     */
    @FXML
    private void afficherStatistiques() {
        try {
            // Charger l'interface StatistiqueReservationFoyer.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StatistiqueReservationFoyer.fxml"));
            Parent root = loader.load();
            
            // Récupérer la scène actuelle
            Scene scene = foyerGrid.getScene();
            Stage stage = (Stage) scene.getWindow();
            
            // Remplacer le contenu de la scène
            scene.setRoot(root);
            
            // Configurer la fenêtre
            stage.setTitle("Statistiques des Réservations de Foyers");
            stage.setMaximized(true);
            
            // Ajouter une transition de fondu pour une navigation plus fluide
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'interface des statistiques: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupNavigationButtons() {
        accueilButton.setOnAction(this::onAccueilButtonClick);
        userButton.setOnAction(this::onUserButtonClick);
        dossierButton.setOnAction(this::ondossierButtonClick);
        universiteButton.setOnAction(this::onuniversiteButtonClick);
        entretienButton.setOnAction(this::onentretienButtonClick);
        evenementButton.setOnAction(this::onevenementButtonClick);
        hebergementButton.setOnAction(this::onhebergementButtonClick);
        restaurantButton.setOnAction(this::onrestaurantButtonClick);
        volsButton.setOnAction(this::onvolsButtonClick);
        logoutButton.setOnAction(this::onlogoutButtonClick);
    }

    private void navigateToScene(String fxmlPath, ActionEvent event, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            // Add fade transition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onAccueilButtonClick(ActionEvent event) {
        navigateToScene("/AcceuilAdmin.fxml", event, "Accueil Admin");
    }

    @FXML
    private void onUserButtonClick(ActionEvent event) {
        navigateToScene("/AdminUser.fxml", event, "Gestion des Utilisateurs");
    }

    @FXML
    private void ondossierButtonClick(ActionEvent event) {
        navigateToScene("/AdminDossier.fxml", event, "Gestion des Dossiers");
    }

    @FXML
    private void onuniversiteButtonClick(ActionEvent event) {
        navigateToScene("/adminuniversite.fxml", event, "Gestion des Universités");
    }

    @FXML
    private void onentretienButtonClick(ActionEvent event) {
        navigateToScene("/Gestionnaire.fxml", event, "Gestion des Entretiens");
    }

    @FXML
    private void onevenementButtonClick(ActionEvent event) {
        navigateToScene("/gestion_evenement.fxml", event, "Gestion des Événements");
    }

    @FXML
    private void onhebergementButtonClick(ActionEvent event) {
        navigateToScene("/AjouterFoyer.fxml", event, "Gestion des Foyers");
    }

    @FXML
    private void onrestaurantButtonClick(ActionEvent event) {
        navigateToScene("/AjouterRestaurant.fxml", event, "Gestion des Restaurants");
    }

    @FXML
    private void onvolsButtonClick(ActionEvent event) {
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
           // showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture", e.getMessage());
        }
    }

    @FXML
    private void onlogoutButtonClick(ActionEvent event) {
        navigateToScene("/login-view.fxml", event, "Login - GradAway");
    }
}
