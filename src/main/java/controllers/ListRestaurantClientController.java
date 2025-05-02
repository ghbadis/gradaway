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
    @FXML private VBox restaurantsContainer;
    
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
            Label emptyLabel = new Label("Aucun restaurant ne correspond à votre recherche");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #757575;");
            restaurantsContainer.getChildren().add(emptyLabel);
            return;
        }
        
        // Ajouter chaque restaurant dans une nouvelle ligne
        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant restaurant = restaurants.get(i);
            HBox restaurantCard = createRestaurantCard(restaurant);
            restaurantsContainer.getChildren().add(restaurantCard);
        }
    }
    
    /**
     * Crée une carte pour afficher un restaurant
     */
    private HBox createRestaurantCard(Restaurant restaurant) {
        // Conteneur principal
        HBox card = new HBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        card.setPrefHeight(150);
        card.setPrefWidth(850);
        card.setSpacing(15);
        card.setPadding(new Insets(10));
        card.setAlignment(Pos.CENTER_LEFT);
        
        // Image du restaurant
        ImageView imageView = new ImageView();
        imageView.setFitHeight(130);
        imageView.setFitWidth(130);
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
        infoContainer.setPrefWidth(500);
        infoContainer.setAlignment(Pos.CENTER_LEFT);
        
        // Nom du restaurant
        Label nameLabel = new Label(restaurant.getNom());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        
        // Adresse
        Label addressLabel = new Label(restaurant.getAdresse() + ", " + restaurant.getVille() + ", " + restaurant.getPays());
        addressLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
        
        // Capacité
        Label capacityLabel = new Label("Capacité: " + restaurant.getCapaciteTotale() + " personnes");
        capacityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        
        // Horaires
        Label hoursLabel = new Label("Ouvert de " + restaurant.getHoraireOuverture() + " à " + restaurant.getHoraireFermeture());
        hoursLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
        
        // Ajouter les informations au conteneur
        infoContainer.getChildren().addAll(nameLabel, addressLabel, capacityLabel, hoursLabel);
        
        // Conteneur pour le bouton de réservation
        VBox buttonContainer = new VBox();
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPrefWidth(200);
        
        // Bouton de réservation
        Button reserveButton = new Button("Réserver");
        reserveButton.setPrefHeight(40);
        reserveButton.setPrefWidth(120);
        reserveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20px;");
        reserveButton.setOnAction(event -> reserveRestaurant(restaurant));
        
        buttonContainer.getChildren().add(reserveButton);
        
        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(imageView, infoContainer, buttonContainer);
        
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
    void search(ActionEvent event) {
        applyFilters();
    }
    
    /**
     * Méthode appelée par le bouton de filtrage par ville
     */
    @FXML
    void filterByLocation(ActionEvent event) {
        // Cette méthode est maintenant déclenchée par le bouton Filtrer
        // Les actions de filtrage sont déjà gérées par les MenuItem du MenuButton
        // Mais on peut l'utiliser pour rafraîchir la liste des villes
        setupLocationFilter();
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
    
    /**
     * Affiche une alerte
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
