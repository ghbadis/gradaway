package controllers;

import Services.ServiceRestaurant;
import entities.Restaurant;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ListRestaurantClientController {

    @FXML private TextField tf_search;
    @FXML private MenuButton locationMenu;
    @FXML private TilePane restaurantsContainer;
    @FXML private Label noResultsLabel;
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
    @FXML private Button mesReservationsButton;
    
    private ServiceRestaurant serviceRestaurant;
    private ObservableList<Restaurant> restaurantList;
    private FilteredList<Restaurant> filteredRestaurants;
    private String currentFilter = "";
    private String currentLocation = "";
    
    @FXML
    public void initialize() {
        serviceRestaurant = new ServiceRestaurant();
        
        // Charger les restaurants
        loadRestaurants();
        
        // Configurer la recherche
        setupSearch();
        
        // Configurer le filtre par ville
        setupLocationFilter();
        
        // Animer le bouton Mes Réservations pour attirer l'attention
        animateReservationsButton();
        
        // Setup dashboard navigation
        setupNavigationButtons();
        
        // Ajouter l'action au bouton Mes Réservations
        if (mesReservationsButton != null) {
            mesReservationsButton.setOnAction(this::ouvrirMesReservations);
        }
    }
    
    /**
     * Anime le bouton Mes Réservations pour attirer l'attention
     */
    private void animateReservationsButton() {
        // Animation de pulsation pour attirer l'attention
        ScaleTransition pulse = new ScaleTransition(Duration.millis(800), locationMenu);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setCycleCount(4);
        pulse.setAutoReverse(true);
        
        // Animation de brillance
        FadeTransition glow = new FadeTransition(Duration.millis(1000), locationMenu);
        glow.setFromValue(0.9);
        glow.setToValue(1.0);
        glow.setCycleCount(4);
        glow.setAutoReverse(true);
        
        // Jouer les animations séquentiellement
        SequentialTransition sequence = new SequentialTransition(pulse, glow);
        sequence.play();
    }
    
    /**
     * Charge tous les restaurants depuis la base de données
     */
    private void loadRestaurants() {
        try {
            List<Restaurant> restaurants = serviceRestaurant.recuperer();
            restaurantList = FXCollections.observableArrayList(restaurants);
            filteredRestaurants = new FilteredList<>(restaurantList);
            
            // Afficher les restaurants
            displayRestaurants(filteredRestaurants);
            
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des restaurants: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Configure la recherche par nom de restaurant
     */
    private void setupSearch() {
        tf_search.textProperty().addListener((observable, oldValue, newValue) -> {
            currentFilter = newValue.toLowerCase();
            applyFilters();
        });
    }
    
    /**
     * Configure le filtre par ville
     */
    private void setupLocationFilter() {
        // Récupérer toutes les villes uniques
        Set<String> cities = new HashSet<>();
        try {
            for (Restaurant restaurant : serviceRestaurant.recuperer()) {
                if (restaurant.getVille() != null && !restaurant.getVille().isEmpty()) {
                    cities.add(restaurant.getVille());
                }
            }
            
            // Ajouter les villes au menu
            locationMenu.getItems().clear();
            for (String city : cities) {
                MenuItem item = new MenuItem(city);
                item.setOnAction(event -> {
                    currentLocation = city;
                    locationMenu.setText(city);
                    applyFilters();
                });
                locationMenu.getItems().add(item);
            }
            
            // Ajouter l'option "Toutes les villes"
            MenuItem allCities = new MenuItem("Toutes les villes");
            allCities.setOnAction(event -> {
                currentLocation = "";
                locationMenu.setText("Filtrer par ville");
                applyFilters();
            });
            locationMenu.getItems().add(allCities);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Applique les filtres (recherche et ville)
     */
    private void applyFilters() {
        filteredRestaurants.setPredicate(createPredicate(currentFilter, currentLocation));
        
        // Personnaliser le message en fonction des filtres appliqués
        if (!currentFilter.isEmpty() && !currentLocation.isEmpty()) {
            noResultsLabel.setText("Aucun restaurant ne correspond à '" + currentFilter + "' dans " + currentLocation);
        } else if (!currentFilter.isEmpty()) {
            noResultsLabel.setText("Aucun restaurant ne correspond à '" + currentFilter + "'");
        } else if (!currentLocation.isEmpty()) {
            noResultsLabel.setText("Aucun restaurant trouvé dans " + currentLocation);
        } else {
            noResultsLabel.setText("Aucun restaurant disponible");
        }
        
        displayRestaurants(filteredRestaurants);
    }
    
    /**
     * Crée un prédicat pour filtrer les restaurants
     */
    private Predicate<Restaurant> createPredicate(String searchText, String location) {
        return restaurant -> {
            boolean matchesSearch = true;
            boolean matchesLocation = true;
            
            // Filtre par texte de recherche
            if (searchText != null && !searchText.isEmpty()) {
                matchesSearch = restaurant.getNom().toLowerCase().contains(searchText) ||
                               restaurant.getAdresse().toLowerCase().contains(searchText) ||
                               restaurant.getVille().toLowerCase().contains(searchText);
            }
            
            // Filtre par ville
            if (location != null && !location.isEmpty()) {
                matchesLocation = restaurant.getVille().equals(location);
            }
            
            return matchesSearch && matchesLocation;
        };
    }
    
    /**
     * Affiche les restaurants filtrés dans le conteneur
     */
    private void displayRestaurants(List<Restaurant> restaurants) {
        restaurantsContainer.getChildren().clear();
        
        if (restaurants.isEmpty()) {
            // Afficher le message d'erreur stylisé
            noResultsLabel.setVisible(true);
            return;
        }
        
        // Cacher le message d'erreur s'il y a des résultats
        noResultsLabel.setVisible(false);
        
        // Configurer le TilePane pour avoir exactement 3 colonnes
        restaurantsContainer.setPrefColumns(3);
        restaurantsContainer.setMaxWidth(900); // Limiter la largeur pour garantir 3 colonnes
        restaurantsContainer.setMinWidth(900);
        restaurantsContainer.setPrefWidth(900);
        
        // Configurer les dimensions des tuiles
        double tileWidth = 280;
        double tileHeight = 320;
        restaurantsContainer.setPrefTileWidth(tileWidth);
        restaurantsContainer.setPrefTileHeight(tileHeight);
        restaurantsContainer.setHgap(15);
        restaurantsContainer.setVgap(15);
        restaurantsContainer.setAlignment(Pos.CENTER);
        restaurantsContainer.setTileAlignment(Pos.CENTER);
        
        // Calculer le nombre de lignes nécessaires
        int numRows = (int) Math.ceil(restaurants.size() / 3.0);
        restaurantsContainer.setPrefRows(numRows);
        restaurantsContainer.setPrefHeight(numRows * (tileHeight + 15) + 15); // hauteur = (hauteur de tuile + vgap) * nombre de lignes + vgap
        
        // Ajouter chaque restaurant dans la grille
        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant restaurant = restaurants.get(i);
            VBox restaurantCard = createRestaurantCard(restaurant);
            restaurantsContainer.getChildren().add(restaurantCard);
        }
    }
    
    /**
     * Crée une carte pour afficher un restaurant
     */
    private VBox createRestaurantCard(Restaurant restaurant) {
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
        card.setAlignment(Pos.TOP_CENTER);
        
        // Image du restaurant
        ImageView imageView = new ImageView();
        imageView.setFitHeight(150);
        imageView.setFitWidth(260);
        imageView.setPreserveRatio(true);
        
        // Charger l'image
        try {
            if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
                String imagePath = "src/main/resources/" + restaurant.getImage();
                File file = new File(imagePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                } else {
                    // Image par défaut si l'image n'existe pas
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                    imageView.setImage(defaultImage);
                }
            } else {
                // Image par défaut si pas d'image
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Image par défaut en cas d'erreur
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                imageView.setImage(defaultImage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        
        // Conteneur pour les informations
        VBox infoContainer = new VBox();
        infoContainer.setSpacing(5);
        infoContainer.setPrefWidth(260);
        infoContainer.setAlignment(Pos.CENTER_LEFT);
        
        // Nom du restaurant
        Label nameLabel = new Label(restaurant.getNom());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #3b82f6;");
        nameLabel.setWrapText(true);
        
        // Adresse - plus courte pour s'adapter à la carte
        Label addressLabel = new Label(restaurant.getVille() + ", " + restaurant.getPays());
        addressLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575;");
        addressLabel.setWrapText(true);
        
        // Capacité
        Label capacityLabel = new Label("Capacité: " + restaurant.getCapaciteTotale() + " personnes");
        capacityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
        capacityLabel.setWrapText(true);
        
        // Horaires - format plus court
        Label hoursLabel = new Label(restaurant.getHoraireOuverture() + " - " + restaurant.getHoraireFermeture());
        hoursLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333333;");
        hoursLabel.setWrapText(true);
        
        // Bouton Réserver
        Button reserveButton = new Button("Réserver");
        reserveButton.setStyle("-fx-background-color: linear-gradient(to right, #6366f1, #8b5cf6); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 1);");
        reserveButton.setPrefWidth(200);
        reserveButton.setPrefHeight(30);
        reserveButton.setOnAction(e -> reserveRestaurant(restaurant));
        
        // Ajouter les éléments au conteneur d'informations
        infoContainer.getChildren().addAll(nameLabel, addressLabel, capacityLabel, hoursLabel);
        
        // Ajouter un espace flexible pour pousser le bouton vers le bas
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        infoContainer.getChildren().add(spacer);
        
        // Ajouter les éléments à la carte
        card.getChildren().addAll(imageView, infoContainer, reserveButton);
        
        return card;
    }
    
    /**
     * Gère la réservation d'un restaurant
     */
    private void reserveRestaurant(Restaurant restaurant) {
        try {
            // Charger la vue de réservation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReserverRestaurant.fxml"));
            Parent root = loader.load();
            
            // Passer le restaurant au contrôleur de réservation
            ReserverRestaurantController controller = loader.getController();
            controller.setRestaurant(restaurant);
            
            // Afficher la vue
            Scene scene = new Scene(root);
            Stage stage = (Stage) tf_search.getScene().getWindow();
            
            // Transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            stage.setScene(scene);
            fadeIn.play();
            
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la vue de réservation: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Méthode appelée par le bouton de recherche
     */
    @FXML
    private void search(ActionEvent event) {
        currentFilter = tf_search.getText().toLowerCase();
        
        // Personnaliser le message pour la recherche par bouton
        if (!currentFilter.isEmpty()) {
            noResultsLabel.setText("Aucun restaurant ne correspond à '" + currentFilter + "'");
        } else {
            noResultsLabel.setText("Aucun restaurant disponible");
        }
        
        applyFilters();
    }
    
    /**
     * Méthode appelée par le bouton de filtrage par ville
     */
    @FXML
    void filterByLocation(ActionEvent event) {
        // Mais on peut l'utiliser pour rafraîchir la liste des villes
        setupLocationFilter();
    }
    
    /**
     * Navigue vers la page "Mes Réservations"
     */
    @FXML
    void voirMesReservations(ActionEvent event) {
        ouvrirMesReservations(event);
    }
    
    /**
     * Affiche une alerte
     * @param title Titre de l'alerte
     * @param message Message de l'alerte
     * @param type Type de l'alerte
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Méthode pour retourner à l'accueil
     */
    @FXML
    void retourAccueil(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Accueil.fxml"));
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
    
    private void setupNavigationButtons() {
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
            navigateToScene(root, event);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des restaurants: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onvolsButtonClick(ActionEvent event) {
        // Implement when needed
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

    @FXML
    private void ouvrirMesReservations(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MesReservationsRestaurant.fxml"));
            Parent root = loader.load();
            MesReservationsRestaurantController controller = loader.getController();
            controller.setUserId(utils.SessionManager.getInstance().getUserId());
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            stage.setScene(scene);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la page des réservations: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}
