package controllers;

import entities.Foyer;
import Services.ServiceFoyer;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.input.KeyCode;

public class ListFoyerControllers {
    @FXML
    private GridPane foyerGrid;
    @FXML
    private TextField searchField;
    @FXML
    private MenuButton locationMenu;

    private ServiceFoyer serviceFoyer = new ServiceFoyer();

    @FXML
    public void search() {
        try {
            String searchText = searchField.getText().toLowerCase();
            List<Foyer> allFoyers = serviceFoyer.recuperer();
            
            List<Foyer> filteredFoyers = allFoyers.stream()
                .filter(foyer -> 
                    foyer.getNom().toLowerCase().contains(searchText) ||
                    foyer.getPays().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
            
            displayFoyers(filteredFoyers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        try {
            List<Foyer> foyers = serviceFoyer.recuperer();
            displayFoyers(foyers);
            
            // Setup search field to search on enter key
            if (searchField != null) {
                searchField.setOnKeyPressed(event -> {
                    if (event.getCode() == KeyCode.ENTER) {
                        search();
                    }
                });
            }

            // Add event handlers for location menu items
            if (locationMenu != null && locationMenu.getItems() != null) {
                for (MenuItem item : locationMenu.getItems()) {
                    item.setOnAction(event -> {
                        String selectedLocation = item.getText();
                        locationMenu.setText(selectedLocation);
                        filterFoyersByLocation(selectedLocation);
                    });
                }
            }

            // Add "Tous les pays" option
            if (locationMenu != null) {
                MenuItem allLocations = new MenuItem("Tous les pays");
                allLocations.setOnAction(event -> {
                    locationMenu.setText("Location"); // Reset to default text
                    displayFoyers(foyers); // Show all foyers
                });
                if (locationMenu.getItems() != null) {
                    locationMenu.getItems().add(0, allLocations); // Add at the beginning of the menu
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayFoyers(List<Foyer> foyers) {
        foyerGrid.getChildren().clear();
        int col = 0;
        int row = 0;
        
        for (Foyer foyer : foyers) {
            VBox card = createFoyerCard(foyer);
            foyerGrid.add(card, col, row);
            
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private void filterFoyersByLocation(String location) {
        try {
            List<Foyer> allFoyers = serviceFoyer.recuperer();
            List<Foyer> filteredFoyers;
            
            if (location == null || location.isEmpty()) {
                filteredFoyers = allFoyers;
            } else {
                filteredFoyers = allFoyers.stream()
                    .filter(foyer -> location.equals(foyer.getPays()))
                    .collect(Collectors.toList());
            }
            
            displayFoyers(filteredFoyers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createFoyerCard(Foyer foyer) {
        // Main card container
        VBox card = new VBox(0);
        card.setPrefWidth(250);
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2); -fx-background-radius: 8;");
        card.setAlignment(Pos.TOP_CENTER); // Center align the content

        // Image container
        StackPane imageContainer = new StackPane();
        imageContainer.setMinHeight(160);
        imageContainer.setMaxHeight(160);
        imageContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8 8 0 0;");

        // Image setup
        ImageView imageView;
        if (foyer.getImage() != null && !foyer.getImage().isEmpty()) {
            try {
                Image image = new Image(foyer.getImage(), 250, 160, true, true);
                imageView = new ImageView(image);
                imageView.setFitWidth(250);
                imageView.setFitHeight(160);
                imageView.setStyle("-fx-background-radius: 8 8 0 0;");
            } catch (Exception e) {
                imageView = createPlaceholderImage();
            }
        } else {
            imageView = createPlaceholderImage();
        }
        
        imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 4, 0, 0, 0); -fx-background-radius: 8 8 0 0;");
        imageContainer.getChildren().add(imageView);

        // Content container
        VBox content = new VBox(10); // Increased spacing between elements
        content.setPadding(new Insets(15));
        content.setStyle("-fx-background-color: white; -fx-background-radius: 0 0 8 8;");
        content.setAlignment(Pos.TOP_LEFT);
        content.setMaxWidth(250);

        // Foyer name
        Label nomLabel = new Label(foyer.getNom());
        nomLabel.setWrapText(true);
        nomLabel.setStyle("-fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        nomLabel.setMaxWidth(220);

        // Location with icon
        HBox locationBox = new HBox(8);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        Label locationIcon = new Label("📍");
        locationIcon.setStyle("-fx-font-size: 12px;");
        Label paysLabel = new Label(foyer.getPays());
        paysLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        locationBox.getChildren().addAll(locationIcon, paysLabel);

        // Add spacing between location and capacity
        Region spacer = new Region();
        spacer.setMinHeight(5);

        // Capacity with icon
        HBox capacityBox = new HBox(8);
        capacityBox.setAlignment(Pos.CENTER_LEFT);
        Label capacityIcon = new Label("👥");
        capacityIcon.setStyle("-fx-font-size: 12px;");
        Label capaciteLabel = new Label(foyer.getCapacite() + " places");
        capaciteLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        capacityBox.getChildren().addAll(capacityIcon, capaciteLabel);

        content.getChildren().addAll(nomLabel, locationBox, spacer, capacityBox);
        card.getChildren().addAll(imageContainer, content);

        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 12, 0, 0, 3); -fx-background-radius: 8; -fx-cursor: hand;");
        });
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2); -fx-background-radius: 8;");
        });

        return card;
    }

    private ImageView createPlaceholderImage() {
        ImageView placeholder = new ImageView();
        placeholder.setFitWidth(250);
        placeholder.setFitHeight(160);
        placeholder.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8 8 0 0;");
        return placeholder;
    }
}
