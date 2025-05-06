package controllers;

import Services.ServiceRestaurant;
import entities.Restaurant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListRestaurantController {

    @FXML private Button refreshBtn;
    @FXML private GridPane restaurantGrid;
    @FXML private TextField searchField;
    @FXML private MenuButton locationMenu;
    @FXML private Button btnListeReservation;
    
    private final ServiceRestaurant serviceRestaurant = new ServiceRestaurant();

    @FXML
    public void initialize() {
        try {
            // Setup search field listener
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    searchRestaurants(newVal);
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });
            
            // Initial display
            displayRestaurants(serviceRestaurant.recuperer());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'initialisation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void displayRestaurants(List<Restaurant> restaurants) {
        restaurantGrid.getChildren().clear();
        int column = 0;
        int row = 0;

        for (Restaurant restaurant : restaurants) {
            VBox restaurantCard = createRestaurantCard(restaurant);
            
            // Add click handler
            restaurantCard.setOnMouseClicked(e -> openModifierRestaurant(restaurant));
            
            // Add hover effect
            restaurantCard.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");
            restaurantCard.setOnMouseEntered(e -> restaurantCard.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-color: #999999; -fx-border-radius: 5;"));
            restaurantCard.setOnMouseExited(e -> restaurantCard.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;"));

            restaurantGrid.add(restaurantCard, column, row);

            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createRestaurantCard(Restaurant restaurant) {
        VBox card = new VBox(10);
        card.setPrefWidth(250);
        card.setPrefHeight(300);
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        
        // Gérer l'image de manière plus robuste
        boolean imageLoaded = false;
        if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
            try {
                String imagePath = "src/main/resources/" + restaurant.getImage();
                java.io.File file = new java.io.File(imagePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                    imageLoaded = true;
                }
            } catch (Exception e) {
                System.out.println("Erreur lors du chargement de l'image: " + e.getMessage());
                // L'image n'a pas pu être chargée, on utilisera l'image par défaut
            }
        }
        
        // Si l'image n'a pas été chargée, utiliser une couleur de fond au lieu d'une image par défaut
        if (!imageLoaded) {
            // Créer un rectangle coloré comme placeholder
            imageView.setStyle("-fx-background-color: #e0e0e0;");
            // On peut aussi définir une image par défaut si elle existe
            try {
                // Essayer plusieurs chemins possibles pour l'image par défaut
                java.io.InputStream is = getClass().getResourceAsStream("/images/placeholder-restaurant.png");
                if (is == null) {
                    is = getClass().getResourceAsStream("/placeholder/placeholder.png");
                }
                if (is == null) {
                    is = getClass().getResourceAsStream("/images/restaurant-default.png");
                }
                
                if (is != null) {
                    Image defaultImage = new Image(is);
                    imageView.setImage(defaultImage);
                }
            } catch (Exception e) {
                System.out.println("Impossible de charger l'image par défaut: " + e.getMessage());
            }
        }

        Label nameLabel = new Label(restaurant.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        Label locationLabel = new Label(restaurant.getVille() + ", " + restaurant.getPays());
        Label hoursLabel = new Label("Horaires: " + restaurant.getHoraireOuverture() + " - " + restaurant.getHoraireFermeture());

        card.getChildren().addAll(imageView, nameLabel, locationLabel, hoursLabel);
        return card;
    }

    @FXML
    void search(ActionEvent event) {
        try {
            searchRestaurants(searchField.getText());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void searchRestaurants(String searchText) throws SQLException {
        List<Restaurant> restaurants = serviceRestaurant.recuperer();
        restaurants.removeIf(restaurant -> 
            !restaurant.getNom().toLowerCase().contains(searchText.toLowerCase()) &&
            !restaurant.getVille().toLowerCase().contains(searchText.toLowerCase()) &&
            !restaurant.getPays().toLowerCase().contains(searchText.toLowerCase())
        );
        displayRestaurants(restaurants);
    }

    @FXML
    void filterByLocation(ActionEvent event) {
        try {
            MenuItem menuItem = (MenuItem) event.getSource();
            String location = menuItem.getText();
            
            List<Restaurant> restaurants = serviceRestaurant.recuperer();
            restaurants.removeIf(restaurant -> !restaurant.getPays().equalsIgnoreCase(location));
            displayRestaurants(restaurants);
            
            // Mettre à jour le texte du MenuButton
            locationMenu.setText(location);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du filtrage: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    void resetFilter(ActionEvent event) {
        try {
            displayRestaurants(serviceRestaurant.recuperer());
            locationMenu.setText("Pays");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la réinitialisation du filtre: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    void refreshTable(ActionEvent event) {
        try {
            displayRestaurants(serviceRestaurant.recuperer());
            locationMenu.setText("Pays");
            searchField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'actualisation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void openModifierRestaurant(Restaurant restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierRestaurant.fxml"));
            Parent root = loader.load();
            
            ModifierRestaurantController controller = loader.getController();
            controller.setRestaurant(restaurant);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier Restaurant");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Make it modal
            stage.show();
            
            // Add a listener to refresh the list when the window closes
            stage.setOnHidden(e -> {
                try {
                    displayRestaurants(serviceRestaurant.recuperer());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    showAlert("Erreur", "Erreur lors du rafraîchissement: " + ex.getMessage(), Alert.AlertType.ERROR);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre de modification: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void navigateToAdd(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterRestaurant.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            stage.setScene(scene);
            fadeIn.play();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la vue: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    void navigateToListeReservation() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListeReservationRestaurant.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) restaurantGrid.getScene().getWindow();
            
            // Transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            stage.setScene(scene);
            fadeIn.play();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la vue: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}