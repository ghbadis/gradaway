package controllers;

import entities.Universite;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import Services.ServiceUniversite;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RecupererUniversiteController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private FlowPane universiteListView;
    @FXML
    private Button refreshButton;
    @FXML
    private Button closeButton;

    private final ServiceUniversite serviceUniversite = new ServiceUniversite();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Load universities when the view is initialized
        loadUniversites();
    }

    @FXML
    private void handleSearchButton(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase().trim();
        try {
            List<Universite> universities;
            if (searchText.isEmpty()) {
                universities = serviceUniversite.getAllUniversites();
            } else {
                universities = serviceUniversite.searchUniversites(searchText);
            }
            displayUniversities(universities);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de recherche", 
                    "Une erreur est survenue lors de la recherche", e.getMessage());
        }
    }

    @FXML
    private void handleRefreshButton(ActionEvent event) {
        searchField.clear();
        loadUniversites();
    }

    @FXML
    private void handleCloseButton(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    private void loadUniversites() {
        try {
            List<Universite> universities = serviceUniversite.getAllUniversites();
            displayUniversities(universities);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", 
                    "Une erreur est survenue lors du chargement des universités", e.getMessage());
        }
    }

    private void displayUniversities(List<Universite> universities) {
        universiteListView.getChildren().clear();
        
        if (universities.isEmpty()) {
            Label noResultsLabel = new Label("Aucune université trouvée");
            noResultsLabel.setTextFill(Color.WHITE);
            noResultsLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            universiteListView.getChildren().add(noResultsLabel);
            return;
        }
        
        for (Universite university : universities) {
            VBox card = createUniversityCard(university);
            universiteListView.getChildren().add(card);
        }
    }
    
    private VBox createUniversityCard(Universite university) {
        // Main card container
        VBox card = new VBox(10);
        card.setPrefWidth(280);
        card.setPrefHeight(400);
        card.setStyle("-fx-background-color: #1A3473; -fx-background-radius: 10px;");
        card.setPadding(new Insets(15));
        
        // University photo
        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        
        // Set default image
        String defaultImagePath = "/images/default_university.png";
        
        // Try to load the university's photo if available
        if (university.getPhotoPath() != null && !university.getPhotoPath().isEmpty()) {
            try {
                // First try to load from the resources folder
                URL photoUrl = getClass().getResource("/" + university.getPhotoPath());
                if (photoUrl != null) {
                    Image universityImage = new Image(photoUrl.toExternalForm());
                    imageView.setImage(universityImage);
                } else {
                    // Try as a file path if not found in resources
                    File photoFile = new File("src/main/resources/" + university.getPhotoPath());
                    if (photoFile.exists()) {
                        Image universityImage = new Image(photoFile.toURI().toString());
                        imageView.setImage(universityImage);
                    } else {
                        // Use default image if photo not found
                        URL defaultUrl = getClass().getResource(defaultImagePath);
                        if (defaultUrl != null) {
                            Image defaultImage = new Image(defaultUrl.toExternalForm());
                            imageView.setImage(defaultImage);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading university photo: " + e.getMessage());
                // Fallback to default image
                try {
                    URL defaultUrl = getClass().getResource(defaultImagePath);
                    if (defaultUrl != null) {
                        Image defaultImage = new Image(defaultUrl.toExternalForm());
                        imageView.setImage(defaultImage);
                    }
                } catch (Exception ex) {
                    System.err.println("Error loading default image: " + ex.getMessage());
                }
            }
        } else {
            // Use default image if no photo path
            try {
                URL defaultUrl = getClass().getResource(defaultImagePath);
                if (defaultUrl != null) {
                    Image defaultImage = new Image(defaultUrl.toExternalForm());
                    imageView.setImage(defaultImage);
                }
            } catch (Exception e) {
                System.err.println("Error loading default image: " + e.getMessage());
            }
        }
        
        // Style the image view
        imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
        
        // University name
        Label nameLabel = new Label(university.getNom());
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setPrefWidth(250);
        
        // University info
        VBox infoBox = new VBox(5);
        infoBox.setAlignment(Pos.TOP_LEFT);
        
        Label locationLabel = new Label("Ville: " + university.getVille());
        locationLabel.setTextFill(Color.WHITE);
        
        Label addressLabel = new Label("Adresse: " + university.getAdresse_universite());
        addressLabel.setTextFill(Color.WHITE);
        addressLabel.setWrapText(true);
        
        Label fieldLabel = new Label("Domaine: " + university.getDomaine());
        fieldLabel.setTextFill(Color.WHITE);
        fieldLabel.setWrapText(true);
        
        Label feesLabel = new Label(String.format("Frais: %.2f €", university.getFrais()));
        feesLabel.setTextFill(Color.WHITE);
        
        infoBox.getChildren().addAll(locationLabel, addressLabel, fieldLabel, feesLabel);
        
        // Buttons container
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        
        // Modify button
        Button modifierButton = new Button("Modifier");
        modifierButton.setStyle("-fx-background-color: #3E92CC; -fx-text-fill: white;");
        modifierButton.setPrefWidth(120);
        modifierButton.setPrefHeight(30);
        modifierButton.setOnAction(e -> handleModifierUniversite(university));
        
        // Delete button
        Button supprimerButton = new Button("Supprimer");
        supprimerButton.setStyle("-fx-background-color: #D8315B; -fx-text-fill: white;");
        supprimerButton.setPrefWidth(120);
        supprimerButton.setPrefHeight(30);
        supprimerButton.setOnAction(e -> handleSupprimerUniversite(university));
        
        buttonsBox.getChildren().addAll(modifierButton, supprimerButton);
        
        // Add some spacing at the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // Assemble the card
        card.getChildren().addAll(imageView, nameLabel, infoBox, spacer, buttonsBox);
        
        return card;
    }
    
    private void handleModifierUniversite(Universite universite) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifieruniversite.fxml"));
            Parent root = loader.load();
            
            // Get the controller and pass the university to modify
            ModifierUniversiteController controller = loader.getController();
            controller.setUniversite(universite);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier l'Université");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Refresh the list when the modification window is closed
            stage.setOnHidden(e -> loadUniversites());
            
        } catch (IOException e) {
                e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible d'ouvrir la fenêtre de modification", e.getMessage());
        }
    }
    
    private void handleSupprimerUniversite(Universite universite) {
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmer la suppression");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'université '" + 
                                  universite.getNom() + "' ?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Delete the university
                serviceUniversite.supprimer(universite);
                
                // Refresh the list
                loadUniversites();
                
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                        "Université supprimée avec succès", null);
                
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                        "Erreur lors de la suppression de l'université", e.getMessage());
            }
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 