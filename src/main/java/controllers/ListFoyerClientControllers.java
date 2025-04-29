package controllers;

import Services.ServiceFoyer;
import entities.Foyer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
//import models.Foyer;
//import services.ServiceFoyer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class ListFoyerClientControllers {

    @FXML private VBox foyerContainer;
    @FXML private TextField searchField;
    @FXML private Button btnB;
    @FXML private MenuButton locationMenu;

    private ServiceFoyer serviceFoyer = new ServiceFoyer();

    @FXML
    public void initialize() {
        loadFoyers();
        setupSearch();
        setupNavigationButton();
        setupLocationMenu();
    }

    private void loadFoyers() {
        try {
            List<Foyer> foyers = serviceFoyer.recuperer();
            foyerContainer.getChildren().clear();

            for (Foyer foyer : foyers) {
                foyerContainer.getChildren().add(createFoyerCard(foyer));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des foyers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private HBox createFoyerCard(Foyer foyer) {
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        // Image
        ImageView imageView = new ImageView();
        try {
            Image image = new Image(foyer.getImage());
            imageView.setImage(image);
        } catch (Exception e) {
            // Use a default image if the foyer image is not available
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/default_foyer.png")));
        }
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Info VBox
        VBox infoBox = new VBox(10);
        // Foyer name with bigger, bolder styling
        Label nameLabel = new Label(foyer.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #2196F3;");

        Label locationLabel = new Label("📍 " + foyer.getVille() + ", " + foyer.getPays());
        locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        Label capacityLabel = new Label("👥 Capacité: " + foyer.getCapacite() + " personnes");
        capacityLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        Label roomsLabel = new Label("🛏 Chambres: " + foyer.getNombreDeChambre());
        roomsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        // Add address
        Label addressLabel = new Label("🏠 " + foyer.getAdresse());
        addressLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");

        // Réserver button
        Button reserverButton = new Button("Réserver");
        reserverButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        reserverButton.setOnMouseEntered(e -> reserverButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;"));
        reserverButton.setOnMouseExited(e -> reserverButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;"));
        reserverButton.setOnAction(e -> navigateToReservation(foyer));

        infoBox.getChildren().addAll(nameLabel, locationLabel, addressLabel, capacityLabel, roomsLabel, reserverButton);
        card.getChildren().addAll(imageView, infoBox);

        return card;
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                List<Foyer> foyers = serviceFoyer.recuperer();
                foyerContainer.getChildren().clear();

                for (Foyer foyer : foyers) {
                    if (matchesSearch(foyer, newValue.toLowerCase())) {
                        foyerContainer.getChildren().add(createFoyerCard(foyer));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private boolean matchesSearch(Foyer foyer, String searchTerm) {
        return foyer.getNom().toLowerCase().contains(searchTerm) ||
               foyer.getVille().toLowerCase().contains(searchTerm) ||
               foyer.getPays().toLowerCase().contains(searchTerm);
    }

    private void setupNavigationButton() {
        btnB.setOnAction(e -> navigateToListFoyer());
    }

    private void setupLocationMenu() {
        try {
            List<Foyer> foyers = serviceFoyer.recuperer();
            // Clear existing items
            locationMenu.getItems().clear();
            
            // Add section title for Foyers
            MenuItem foyerTitle = new MenuItem("📋 Foyers");
            foyerTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            foyerTitle.setDisable(true);
            locationMenu.getItems().add(foyerTitle);
            
            // Add foyer names as menu items first
            for (Foyer foyer : foyers) {
                MenuItem item = new MenuItem(foyer.getNom());
                item.setOnAction(e -> {
                    try {
                        // Show only this specific foyer
                        foyerContainer.getChildren().clear();
                        foyerContainer.getChildren().add(createFoyerCard(foyer));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showAlert("Erreur", "Erreur lors de l'affichage du foyer: " + ex.getMessage(), Alert.AlertType.ERROR);
                    }
                });
                locationMenu.getItems().add(item);
            }
            
            // Add separator
            locationMenu.getItems().add(new SeparatorMenuItem());
            
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
            locationMenu.setText("📍 Localisation");
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

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
}
