package controllers;

import Services.ServiceFoyer;
import entities.Foyer;
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

public class ListFoyerControllers {

    @FXML private GridPane foyerGrid;
    @FXML private TextField searchField;
    @FXML private MenuButton locationMenu;
    @FXML private Button btnListeReservation;
    
    private final ServiceFoyer serviceFoyer = new ServiceFoyer();

    @FXML
    public void initialize() {
        try {
            // Setup location menu items with event handlers
            for (MenuItem item : locationMenu.getItems()) {
                item.setOnAction(e -> filterByLocation(item.getText()));
            }

            // Setup search field listener
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                try {
                    searchFoyers(newVal);
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage(), Alert.AlertType.ERROR);
                }
            });

            // Initial display
            displayFoyers(serviceFoyer.recuperer());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'initialisation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void displayFoyers(List<Foyer> foyers) {
        foyerGrid.getChildren().clear();
        int column = 0;
        int row = 0;

        for (Foyer foyer : foyers) {
            VBox foyerCard = createFoyerCard(foyer);
            
            // Add click handler
            foyerCard.setOnMouseClicked(e -> openModifierFoyer(foyer));
            
            // Add hover effect
            foyerCard.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;");
            foyerCard.setOnMouseEntered(e -> foyerCard.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-border-color: #999999; -fx-border-radius: 5;"));
            foyerCard.setOnMouseExited(e -> foyerCard.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #cccccc; -fx-border-radius: 5;"));

            foyerGrid.add(foyerCard, column, row);

            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createFoyerCard(Foyer foyer) {
        VBox card = new VBox(10);
        card.setPrefWidth(250);
        card.setPrefHeight(300);
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        
        try {
            Image image = new Image(foyer.getImage());
            imageView.setImage(image);
        } catch (Exception e) {
            // Use default image if loading fails
            imageView.setImage(new Image(getClass().getResourceAsStream("/default-foyer.png")));
        }

        Label nameLabel = new Label(foyer.getNom());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        Label locationLabel = new Label(foyer.getVille() + ", " + foyer.getPays());
        Label capacityLabel = new Label("Capacité: " + foyer.getCapacite() + " personnes");
        Label roomsLabel = new Label("Chambres: " + foyer.getNombreDeChambre());

        card.getChildren().addAll(imageView, nameLabel, locationLabel, capacityLabel, roomsLabel);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ModifierFoyer.fxml"));
            Parent root = loader.load();
            
            ModifierFoyerControllers controller = loader.getController();
            controller.initData(foyer);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier Foyer");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Make it modal
            stage.show();
            
            // Add a listener to refresh the list when the window closes
            stage.setOnHidden(e -> {
                try {
                    displayFoyers(serviceFoyer.recuperer());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre de modification: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void navigateToAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFoyer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
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
        alert.showAndWait();
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
}
