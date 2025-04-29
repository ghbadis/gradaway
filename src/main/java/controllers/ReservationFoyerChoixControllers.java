package controllers;

import Services.ServiceFoyer;
import entities.Foyer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ReservationFoyerChoixControllers {

    @FXML private GridPane foyerGrid;
    @FXML private TextField searchField;
    
    private final ServiceFoyer serviceFoyer = new ServiceFoyer();
    private static final int ITEMS_PER_ROW = 3;

    @FXML
    public void initialize() {
        loadFoyers();
        
        // Add search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                List<Foyer> foyers = serviceFoyer.recuperer();
                displayFoyers(foyers.stream()
                    .filter(f -> f.getNom().toLowerCase().contains(newValue.toLowerCase()) ||
                               f.getPays().toLowerCase().contains(newValue.toLowerCase()))
                    .toList());
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });
    }

    private void loadFoyers() {
        try {
            List<Foyer> foyers = serviceFoyer.recuperer();
            displayFoyers(foyers);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des foyers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void displayFoyers(List<Foyer> foyers) {
        foyerGrid.getChildren().clear();
        
        int row = 0;
        int col = 0;
        
        for (Foyer foyer : foyers) {
            VBox foyerBox = createFoyerBox(foyer);
            foyerGrid.add(foyerBox, col, row);
            
            col++;
            if (col == ITEMS_PER_ROW) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createFoyerBox(Foyer foyer) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setPrefWidth(220);
        box.setStyle("-fx-background-color: white; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3); " +
                    "-fx-background-radius: 10; " +
                    "-fx-border-radius: 10;");
        
        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(190);
        imageView.setFitHeight(140);
        imageView.setPreserveRatio(true);
        
        try {
            Image image = new Image(foyer.getImage());
            imageView.setImage(image);
        } catch (Exception e) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-foyer.png")));
        }
        
        // Image container with rounded corners
        VBox imageContainer = new VBox(imageView);
        imageContainer.setStyle("-fx-background-radius: 5; -fx-border-radius: 5; -fx-padding: 0;");
        imageContainer.setPrefHeight(140);
        
        // Labels with better styling
        Label nomLabel = new Label(foyer.getNom());
        nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");
        nomLabel.setWrapText(true);
        
        Label paysLabel = new Label("📍 " + foyer.getPays());
        paysLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
        
        Label capaciteLabel = new Label("👥 Capacité: " + foyer.getCapacite());
        capaciteLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");
        
        // Réserver button with hover effect
        Button reserverBtn = new Button("Réserver");
        reserverBtn.setStyle("-fx-background-color: #2196F3; " +
                           "-fx-text-fill: white; " +
                           "-fx-font-weight: bold; " +
                           "-fx-background-radius: 20; " +
                           "-fx-cursor: hand;");
        reserverBtn.setPrefWidth(160);
        reserverBtn.setPrefHeight(35);
        
        // Add hover effect
        reserverBtn.setOnMouseEntered(e -> 
            reserverBtn.setStyle("-fx-background-color: #1976D2; " +
                               "-fx-text-fill: white; " +
                               "-fx-font-weight: bold; " +
                               "-fx-background-radius: 20; " +
                               "-fx-cursor: hand;"));
        
        reserverBtn.setOnMouseExited(e -> 
            reserverBtn.setStyle("-fx-background-color: #2196F3; " +
                               "-fx-text-fill: white; " +
                               "-fx-font-weight: bold; " +
                               "-fx-background-radius: 20; " +
                               "-fx-cursor: hand;"));
        
        reserverBtn.setOnAction(event -> {
            // TODO: Implement reservation logic
            showAlert("Info", "Fonctionnalité de réservation à implémenter", Alert.AlertType.INFORMATION);
        });
        
        // Add some spacing between elements
        VBox.setMargin(nomLabel, new Insets(10, 0, 5, 0));
        VBox.setMargin(reserverBtn, new Insets(10, 0, 0, 0));
        
        box.getChildren().addAll(imageContainer, nomLabel, paysLabel, capaciteLabel, reserverBtn);
        return box;
    }

    @FXML
    private void back() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterFoyer.fxml"));
            Stage stage = (Stage) foyerGrid.getScene().getWindow();
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
}
