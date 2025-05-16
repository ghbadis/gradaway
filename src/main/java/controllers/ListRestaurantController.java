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
    @FXML private Button btnStatistiques;
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
            // Setup dashboard navigation
            setupNavigationButtons();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'initialisation: " + e.getMessage(), Alert.AlertType.ERROR);
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
            
            // Configurer la fenêtre en plein écran
            stage.setMaximized(true);
            
            // Ajouter une transition de fondu pour une navigation plus fluide
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
            stage.show();
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
            //showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture", e.getMessage());
        }
    }

    @FXML
    private void onlogoutButtonClick(ActionEvent event) {
        navigateToScene("/login-view.fxml", event, "Login - GradAway");
    }

    private void displayRestaurants(List<Restaurant> restaurants) {
        restaurantGrid.getChildren().clear();
        int column = 0;
        int row = 0;
        
        // Vérifier si la liste est vide
        if (restaurants.isEmpty()) {
            // Afficher le message "Aucun restaurant trouvé"
            noResultsLabel.setVisible(true);
        } else {
            // Cacher le message et afficher les restaurants
            noResultsLabel.setVisible(false);
            
            for (Restaurant restaurant : restaurants) {
                VBox restaurantCard = createRestaurantCard(restaurant);
                
                // Add hover effect
                restaurantCard.setOnMouseEntered(e -> restaurantCard.setStyle("-fx-background-color: #2c5999; -fx-padding: 15; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5); -fx-cursor: hand;"));
                restaurantCard.setOnMouseExited(e -> restaurantCard.setStyle("-fx-background-color: #1e3c72; -fx-padding: 15; -fx-background-radius: 5;"));
                
                // Ajouter un gestionnaire de clic pour ouvrir l'interface de modification
                restaurantCard.setOnMouseClicked(e -> openModifierRestaurant(restaurant));

                restaurantGrid.add(restaurantCard, column, row);

                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
        }
    }

    private VBox createRestaurantCard(Restaurant restaurant) {
        // Création de la carte principale avec un fond bleu foncé
        VBox card = new VBox(10);
        card.setPrefWidth(250);
        card.setPrefHeight(380); // Hauteur augmentée pour les boutons
        card.setAlignment(javafx.geometry.Pos.TOP_CENTER);
        card.setStyle("-fx-background-color: #1e3c72; -fx-padding: 15; -fx-background-radius: 5;");
        
        // Création du conteneur pour l'image avec un fond blanc et des bords arrondis
        VBox imageContainer = new VBox();
        imageContainer.setStyle("-fx-background-color: white; -fx-padding: 5; -fx-background-radius: 5;");
        imageContainer.setPrefWidth(220);
        imageContainer.setPrefHeight(160);
        imageContainer.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Configuration de l'image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(210);
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
            }
        }
        
        // Si l'image n'a pas été chargée, utiliser une image par défaut
        if (!imageLoaded) {
            try {
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
                } else {
                    // Si aucune image par défaut n'est trouvée, utiliser une couleur de fond
                    imageView.setStyle("-fx-background-color: #e0e0e0;");
                }
            } catch (Exception e) {
                System.out.println("Impossible de charger l'image par défaut: " + e.getMessage());
            }
        }
        
        // Ajouter l'image au conteneur
        imageContainer.getChildren().add(imageView);
        
        // Création des labels avec texte blanc
        Label nameLabel = new Label(restaurant.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: white; -fx-padding: 10 0 5 0;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(220);
        nameLabel.setAlignment(javafx.geometry.Pos.CENTER);
        
        Label locationLabel = new Label(restaurant.getVille() + ", " + restaurant.getPays());
        locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-opacity: 0.9;");
        locationLabel.setWrapText(true);
        locationLabel.setMaxWidth(220);
        
        // Information supplémentaire
        Label adresseLabel = new Label("Adresse: " + restaurant.getAdresse());
        adresseLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-opacity: 0.8;");
        adresseLabel.setWrapText(true);
        adresseLabel.setMaxWidth(220);
        
        Label capaciteLabel = new Label("Capacité: " + restaurant.getCapacite() + " personnes");
        capaciteLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: white; -fx-opacity: 0.8;");
        capaciteLabel.setWrapText(true);
        capaciteLabel.setMaxWidth(220);
        
        // Aucune indication textuelle n'est ajoutée pour garder l'interface simple
        
        // Assembler tous les éléments
        card.getChildren().addAll(imageContainer, nameLabel, locationLabel, adresseLabel, capaciteLabel);
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
        
        // Si le champ de recherche n'est pas vide, filtrer les restaurants
        if (!searchText.trim().isEmpty()) {
            restaurants.removeIf(restaurant -> 
                !restaurant.getNom().toLowerCase().contains(searchText.toLowerCase()) &&
                !restaurant.getVille().toLowerCase().contains(searchText.toLowerCase()) &&
                !restaurant.getPays().toLowerCase().contains(searchText.toLowerCase())
            );
            
            // Personnaliser le message avec le terme recherché si aucun résultat
            if (restaurants.isEmpty()) {
                noResultsLabel.setText("Aucun restaurant ne correspond à votre recherche : '" + searchText + "'");
            }
        } else {
            // Si le champ est vide, on affiche le message par défaut
            noResultsLabel.setText("Aucun restaurant ne correspond à votre recherche");
        }
        
        displayRestaurants(restaurants);
    }

    @FXML
    void filterByLocation(ActionEvent event) {
        try {
            MenuItem menuItem = (MenuItem) event.getSource();
            String location = menuItem.getText();
            
            List<Restaurant> restaurants = serviceRestaurant.recuperer();
            restaurants.removeIf(restaurant -> !restaurant.getPays().equalsIgnoreCase(location));
            
            // Personnaliser le message si aucun restaurant n'est trouvé pour ce pays
            if (restaurants.isEmpty()) {
                noResultsLabel.setText("Aucun restaurant trouvé dans " + location);
            }
            
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
            // Réinitialiser le message d'erreur
            noResultsLabel.setText("Aucun restaurant ne correspond à votre recherche");
            noResultsLabel.setVisible(false);
            
            // Réinitialiser l'affichage
            displayRestaurants(serviceRestaurant.recuperer());
            locationMenu.setText("Pays");
            searchField.clear();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'actualisation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void supprimerRestaurant(Restaurant restaurant) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer le restaurant " + restaurant.getNom() + " ?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    serviceRestaurant.supprimer(restaurant.getId());
                    displayRestaurants(serviceRestaurant.recuperer());
                    showAlert("Succès", "Restaurant supprimé avec succès", Alert.AlertType.INFORMATION);
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
        });
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
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterRestaurant.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) restaurantGrid.getScene().getWindow();
            
            // Configurer la fenêtre en plein écran
            stage.setMaximized(true);
            
            // Transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            stage.setScene(scene);
            fadeIn.play();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @FXML
    void afficherStatistiques(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/StatistiqueReservationRestaurant.fxml"));
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
            showAlert("Erreur", "Erreur lors du chargement de la vue des statistiques: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}