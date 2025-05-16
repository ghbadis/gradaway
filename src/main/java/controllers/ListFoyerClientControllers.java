package controllers;

import Services.ServiceFoyer;
import entities.Foyer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class ListFoyerClientControllers {

    @FXML private TilePane foyerContainer;
    @FXML private TextField searchField;
    @FXML private Button btnSearch;
    @FXML private MenuButton locationMenu;
    @FXML private Button mesReservationsBtn;
    @FXML private Label noResultsLabel;
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

    private ServiceFoyer serviceFoyer = new ServiceFoyer();

    @FXML
    public void initialize() {
        loadFoyers();
        setupSearch();
        setupNavigationButton();
        setupLocationMenu();
        setupNavigationButtons();
        animateReservationsButton();
    }

    private void setupNavigationButtons() {
        // Setup action handlers for all navigation buttons
        accueilButton.setOnAction(this::onAccueilButtonClick);
        userButton.setOnAction(this::onProfileButtonClick);
        dossierButton.setOnAction(this::ondossierButtonClick);
        universiteButton.setOnAction(this::onuniversiteButtonClick);
        entretienButton.setOnAction(this::onentretienButtonClick);
        evenementButton.setOnAction(this::onevenementButtonClick);
        hebergementButton.setOnAction(this::onhebergementButtonClick);
        restaurantButton.setOnAction(this::onrestaurantButtonClick);
        volsButton.setOnAction(this::onvolsButtonClick);
        logoutButton.setOnAction(this::onlogoutButtonClick);
    }

    @FXML
    private void onAccueilButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
            Parent root = loader.load();
            navigateToScene(root, event);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation vers l'accueil: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onProfileButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Mon Profil");
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture du profil: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void ondossierButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDossier.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion du Dossier");
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture du dossier: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onuniversiteButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminconditature.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Candidatures");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des candidatures: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onentretienButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemanderEntretien.fxml"));
            Parent root = loader.load();
            navigateToScene(root, event);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture de l'entretien: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onevenementButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affiche_evenement.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Événements");
            stage.setMinWidth(1133);
            stage.setMinHeight(691);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des événements: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onhebergementButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();
            navigateToScene(root, event);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des foyers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onrestaurantButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurantClient.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Restaurants");
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des restaurants: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onvolsButtonClick(ActionEvent event) {
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
            //showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des vols.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("AcceuilController: Unexpected error: " + e.getMessage());
           // showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onlogoutButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            Scene scene = new Scene(root);
            loginStage.setScene(scene);
            loginStage.setTitle("Login - GradAway");
            loginStage.setResizable(true);
            loginStage.centerOnScreen();
            loginStage.show();
            
            // Close current window
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la déconnexion: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void navigateToScene(Parent root, ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        // Add fade transition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    /**
     * Anime le bouton Mes Réservations pour attirer l'attention
     */
    private void animateReservationsButton() {
        // Animation de pulsation pour attirer l'attention
        ScaleTransition pulse = new ScaleTransition(Duration.millis(800), mesReservationsBtn);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setCycleCount(4);
        pulse.setAutoReverse(true);
        
        // Animation de brillance
        FadeTransition glow = new FadeTransition(Duration.millis(1000), mesReservationsBtn);
        glow.setFromValue(0.9);
        glow.setToValue(1.0);
        glow.setCycleCount(4);
        glow.setAutoReverse(true);
        
        // Jouer les animations séquentiellement
        SequentialTransition sequence = new SequentialTransition(pulse, glow);
        sequence.play();
    }

    private void loadFoyers() {
        try {
            List<Foyer> foyers = serviceFoyer.recuperer();
            foyerContainer.getChildren().clear();
            
            // Configurer le TilePane pour avoir exactement 3 colonnes
            foyerContainer.setPrefColumns(3);
            foyerContainer.setMaxWidth(900); // Limiter la largeur pour garantir 3 colonnes
            foyerContainer.setMinWidth(900);
            foyerContainer.setPrefWidth(900);
            
            // Configurer les dimensions des tuiles
            double tileWidth = 280;
            double tileHeight = 320;
            foyerContainer.setPrefTileWidth(tileWidth);
            foyerContainer.setPrefTileHeight(tileHeight);
            foyerContainer.setHgap(15);
            foyerContainer.setVgap(15);
            foyerContainer.setAlignment(javafx.geometry.Pos.CENTER);
            foyerContainer.setTileAlignment(javafx.geometry.Pos.CENTER);
            
            // Calculer le nombre de lignes nécessaires
            int numRows = (int) Math.ceil(foyers.size() / 3.0);
            foyerContainer.setPrefRows(numRows);
            foyerContainer.setPrefHeight(numRows * (tileHeight + 15) + 15); // hauteur = (hauteur de tuile + vgap) * nombre de lignes + vgap

            // Cacher le message "Aucun résultat" par défaut
            noResultsLabel.setVisible(false);
            
            if (foyers.isEmpty()) {
                // Afficher le message si aucun foyer n'est disponible
                noResultsLabel.setVisible(true);
            } else {
                // Sinon, ajouter les cartes de foyers
                for (Foyer foyer : foyers) {
                    foyerContainer.getChildren().add(createFoyerCard(foyer));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des foyers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private VBox createFoyerCard(Foyer foyer) {
        // Conteneur principal - utiliser VBox pour un affichage vertical plus compact
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 8, 0, 0, 2);");
        // Dimensions fixes pour garantir 3 par ligne
        card.setPrefHeight(320);
        card.setMinHeight(320);
        card.setMaxHeight(320);
        card.setPrefWidth(280);
        card.setMinWidth(280);
        card.setMaxWidth(280);
        card.setSpacing(10);
        card.setPadding(new Insets(10));
        card.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        // Image du foyer
        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(260);
        imageView.setPreserveRatio(true);

        // Gérer l'image de manière plus robuste
        boolean imageLoaded = false;
        if (foyer.getImage() != null && !foyer.getImage().isEmpty()) {
            try {
                Image image = new Image(foyer.getImage());
                imageView.setImage(image);
                imageLoaded = true;
            } catch (Exception e) {
                System.out.println("Erreur lors du chargement de l'image: " + e.getMessage());
                // L'image n'a pas pu être chargée, on utilisera l'image par défaut
            }
        }

        // Si l'image n'a pas été chargée, utiliser une couleur de fond ou une image par défaut
        if (!imageLoaded) {
            // Créer un rectangle coloré comme placeholder
            imageView.setStyle("-fx-background-color: #e0e0e0;");

            // On peut aussi définir une image par défaut si elle existe
            try {
                // Essayer plusieurs chemins possibles pour l'image par défaut
                java.io.InputStream is = getClass().getResourceAsStream("/iamge/images.png");
                if (is == null) {
                    is = getClass().getResourceAsStream("/placeholder/placeholder.png");
                }
                if (is == null) {
                    is = getClass().getResourceAsStream("/iamge/téléchargé.png");
                }

                if (is != null) {
                    Image defaultImage = new Image(is);
                    imageView.setImage(defaultImage);
                }
            } catch (Exception e) {
                System.out.println("Impossible de charger l'image par défaut: " + e.getMessage());
            }
        }

        // Conteneur pour les informations
        VBox infoContainer = new VBox();
        infoContainer.setSpacing(5);
        infoContainer.setPrefWidth(260);
        infoContainer.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        // Nom du foyer
        Label nameLabel = new Label(foyer.getNom());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #3b82f6;");
        nameLabel.setWrapText(true);

        // Adresse - plus courte pour s'adapter à la carte
        Label locationLabel = new Label(foyer.getVille() + ", " + foyer.getPays());
        locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575;");
        locationLabel.setWrapText(true);
        
        // Capacité
        Label capacityLabel = new Label("Capacité: " + foyer.getCapacite() + " personnes");
        capacityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
        capacityLabel.setWrapText(true);
        
        // Chambres
        Label roomsLabel = new Label("Chambres: " + foyer.getNombreDeChambre());
        roomsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
        roomsLabel.setWrapText(true);
        
        // Ajouter les éléments au conteneur d'informations
        infoContainer.getChildren().addAll(nameLabel, locationLabel, capacityLabel, roomsLabel);
        
        // Ajouter un espace flexible pour pousser le bouton vers le bas
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        infoContainer.getChildren().add(spacer);

        // Bouton Réserver
        Button reserverButton = new Button("Réserver");
        reserverButton.setStyle("-fx-background-color: linear-gradient(to right, #6366f1, #8b5cf6); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 1);");
        reserverButton.setPrefWidth(200);
        reserverButton.setPrefHeight(30);

        // Configurer l'action du bouton pour naviguer vers la page de réservation avec le foyer sélectionné
        reserverButton.setOnAction(e -> {
            try {
                navigateToReservation(foyer);
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Erreur", "Erreur lors de la navigation vers la page de réservation: " + ex.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Ajouter les éléments à la carte
        card.getChildren().addAll(imageView, infoContainer, reserverButton);
        
        return card;
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                try {
                    List<Foyer> foyers = serviceFoyer.recuperer();
                    foyerContainer.getChildren().clear();
                    boolean foyerFound = false;

                    for (Foyer foyer : foyers) {
                        if (matchesSearch(foyer, newValue.toLowerCase())) {
                            foyerContainer.getChildren().add(createFoyerCard(foyer));
                            foyerFound = true;
                        }
                    }

                    // Afficher ou masquer le message en fonction des résultats
                    noResultsLabel.setVisible(!foyerFound);
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                // Si le champ de recherche est vide, afficher tous les foyers
                try {
                    loadFoyers();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Erreur lors du chargement des foyers: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private boolean matchesSearch(Foyer foyer, String searchTerm) {
        return foyer.getNom().toLowerCase().contains(searchTerm) ||
                foyer.getVille().toLowerCase().contains(searchTerm) ||
                foyer.getPays().toLowerCase().contains(searchTerm);
    }

    private void setupNavigationButton() {
        btnSearch.setOnAction(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                try {
                    List<Foyer> foyers = serviceFoyer.recuperer();
                    foyerContainer.getChildren().clear();
                    boolean foyerFound = false;

                    for (Foyer foyer : foyers) {
                        if (matchesSearch(foyer, searchTerm.toLowerCase())) {
                            foyerContainer.getChildren().add(createFoyerCard(foyer));
                            foyerFound = true;
                        }
                    }

                    // Afficher le message dans l'interface plutôt qu'une alerte
                    noResultsLabel.setVisible(!foyerFound);
                    if (!foyerFound) {
                        // Personnaliser le message avec le terme recherché
                        noResultsLabel.setText("Aucun foyer ne correspond à votre recherche : '" + searchTerm + "'");
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                    showAlert("Erreur", "Erreur lors de la recherche: " + e1.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                try {
                    loadFoyers(); // Recharger tous les foyers si le champ de recherche est vide
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showAlert("Erreur", "Erreur lors du chargement des foyers: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
    }

    private void setupLocationMenu() {
        try {
            List<Foyer> foyers = serviceFoyer.recuperer();
            // Clear existing items
            locationMenu.getItems().clear();

            // Add section title for Locations
            MenuItem locationTitle = new MenuItem("📍 Locations");
            locationTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            locationTitle.setDisable(true);
            locationMenu.getItems().add(locationTitle);

            // Create a set to store unique locations
            Set<String> uniqueLocations = new HashSet<>();

            // Add all unique locations to the set
            for (Foyer foyer : foyers) {
                uniqueLocations.add(foyer.getVille() + ", " + foyer.getPays());
            }

            // Create menu items for each unique location
            for (String location : uniqueLocations) {
                MenuItem item = new MenuItem(location);
                item.setOnAction(e -> {
                    // Filter by city or country
                    String[] parts = location.split(", ");
                    if (parts.length == 2) {
                        String city = parts[0];
                        String country = parts[1];

                        try {
                            List<Foyer> filteredFoyers = serviceFoyer.recuperer();
                            foyerContainer.getChildren().clear();

                            for (Foyer foyer : filteredFoyers) {
                                if (foyer.getVille().equalsIgnoreCase(city) &&
                                        foyer.getPays().equalsIgnoreCase(country)) {
                                    foyerContainer.getChildren().add(createFoyerCard(foyer));
                                }
                            }
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                            showAlert("Erreur", "Erreur lors du filtrage par localisation: " + ex.getMessage(), Alert.AlertType.ERROR);
                        }
                    }
                });
                locationMenu.getItems().add(item);
            }

            // Style the menu button
            locationMenu.setText("📍 Location");
            locationMenu.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e0e0e0; " +
                    "-fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 8 15;");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des locations: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void navigateToReservation(Foyer foyer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReserverFoyer.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui passer le foyer sélectionné
            ReserverFoyerControllers controller = loader.getController();
            controller.setSelectedFoyer(foyer);

            Stage stage = (Stage) foyerContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Add fade transition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void navigateToListFoyer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyer.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) foyerContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Add fade transition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void Mes_Reservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MesReservationsFoyer.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et lui passer l'ID de l'utilisateur connecté
            MesReservationsFoyerController controller = loader.getController();
            controller.setUserId(utils.SessionManager.getInstance().getUserId());

            Stage stage = (Stage) foyerContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Add fade transition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
}