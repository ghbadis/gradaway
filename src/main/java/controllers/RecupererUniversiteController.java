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
    private Button closeButton;
    @FXML
    private Button ajouterButton;

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
    private void handleCloseButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AcceuilAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) closeButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil Admin");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible de retourner à l'accueil admin", e.getMessage());
        }
    }

    @FXML
    private void handleAjouterButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouteruniversite.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ajouterButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajouter Université");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'interface d'ajout d'université", e.getMessage());
        }
    }

    @FXML
    private void handleGestionCandidaturesButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestioncandidatures.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) universiteListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Candidatures");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible d'ouvrir l'interface de gestion des candidatures", e.getMessage());
        }
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
        
        // Add universities to the view
        for (Universite university : universities) {
            VBox card = createUniversityCard(university);
            universiteListView.getChildren().add(card);
        }
    }
    
    private VBox createUniversityCard(Universite university) {
        // Main card container
        VBox card = new VBox(15);
        card.setPrefWidth(740);
        card.setPrefHeight(180);
        card.getStyleClass().addAll("white-bg", "shadow");
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 10);");
        card.setPadding(new Insets(15));
        
        // Create the horizontal layout for the card content
        HBox contentBox = new HBox(20);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        
        // Left side: University photo
        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);
        
        // Set default image
        String defaultImagePath = "/images/default_university.png";
        
        // Try to load the university's photo if available
        if (university.getPhotoPath() != null && !university.getPhotoPath().isEmpty()) {
            try {
                System.out.println("RecupererUniversiteController: Trying to load image from path: " + university.getPhotoPath());
                
                // First try to load from the resources folder
                URL photoUrl = getClass().getResource("/" + university.getPhotoPath());
                if (photoUrl != null) {
                    System.out.println("Loading from resources URL: " + photoUrl);
                    Image universityImage = new Image(photoUrl.toExternalForm());
                    imageView.setImage(universityImage);
                    System.out.println("Image loaded successfully from resources");
                } else {
                    // Try as a file path if not found in resources
                    String resourcePath = "src/main/resources/" + university.getPhotoPath();
                    File photoFile = new File(resourcePath);
                    System.out.println("Trying to load from file path: " + photoFile.getAbsolutePath());
                    
                    if (photoFile.exists()) {
                        System.out.println("File exists, loading from: " + photoFile.toURI());
                        Image universityImage = new Image(photoFile.toURI().toString());
                        imageView.setImage(universityImage);
                        System.out.println("Image loaded successfully from file");
                    } else {
                        // Try as a direct file path (absolute path)
                        File directFile = new File(university.getPhotoPath());
                        System.out.println("Trying as direct file path: " + directFile.getAbsolutePath());
                        
                        if (directFile.exists()) {
                            System.out.println("Direct file exists, loading from: " + directFile.toURI());
                            Image universityImage = new Image(directFile.toURI().toString());
                            imageView.setImage(universityImage);
                            System.out.println("Image loaded successfully from direct path");
                        } else {
                            // Use default image if photo not found
                            System.out.println("Could not find image, using default");
                            URL defaultUrl = getClass().getResource(defaultImagePath);
                            if (defaultUrl != null) {
                                Image defaultImage = new Image(defaultUrl.toExternalForm());
                                imageView.setImage(defaultImage);
                                System.out.println("Default image loaded");
                            } else {
                                System.out.println("Even default image couldn't be loaded!");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading university photo: " + e.getMessage());
                e.printStackTrace();
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
        imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 0); -fx-background-radius: 5px;");
        
        // Middle: University info
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPrefWidth(400);
        
        // University name - now in the info box
        Label nameLabel = new Label(university.getNom());
        nameLabel.setTextFill(Color.valueOf("#1A237E"));
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        nameLabel.setWrapText(true);
        
        Label locationLabel = new Label("Ville: " + university.getVille());
        locationLabel.setTextFill(Color.valueOf("#303F9F"));
        locationLabel.setFont(Font.font("System", 14));
        
        Label addressLabel = new Label("Adresse: " + university.getAdresse_universite());
        addressLabel.setTextFill(Color.valueOf("#303F9F"));
        addressLabel.setFont(Font.font("System", 14));
        addressLabel.setWrapText(true);
        
        Label fieldLabel = new Label("Domaine: " + university.getDomaine());
        fieldLabel.setTextFill(Color.valueOf("#303F9F"));
        fieldLabel.setFont(Font.font("System", 14));
        fieldLabel.setWrapText(true);
        
        Label feesLabel = new Label(String.format("Frais: %.2f €", university.getFrais()));
        feesLabel.setTextFill(Color.valueOf("#303F9F"));
        feesLabel.setFont(Font.font("System", 14));
        
        infoBox.getChildren().addAll(nameLabel, locationLabel, addressLabel, fieldLabel, feesLabel);
        
        // Right side: Buttons in vertical arrangement
        VBox buttonsBox = new VBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setPrefWidth(150);
        
        // Modify button
        Button modifierButton = new Button("Modifier");
        modifierButton.getStyleClass().add("update-btn");
        modifierButton.setStyle("-fx-background-color: #3E92CC; -fx-text-fill: white; -fx-background-radius: 5px; -fx-cursor: hand;");
        modifierButton.setPrefWidth(120);
        modifierButton.setPrefHeight(35);
        modifierButton.setFont(Font.font("System", 14));
        modifierButton.setOnAction(e -> handleModifierUniversite(university));
        
        // Delete button
        Button supprimerButton = new Button("Supprimer");
        supprimerButton.getStyleClass().add("clear-btn");
        supprimerButton.setStyle("-fx-background-color: #D8315B; -fx-text-fill: white; -fx-background-radius: 5px; -fx-cursor: hand;");
        supprimerButton.setPrefWidth(120);
        supprimerButton.setPrefHeight(35);
        supprimerButton.setFont(Font.font("System", 14));
        supprimerButton.setOnAction(e -> handleSupprimerUniversite(university));
        
        buttonsBox.getChildren().addAll(modifierButton, supprimerButton);
        
        // Add a vertical separator
        Separator separator = new Separator();
        separator.setOrientation(javafx.geometry.Orientation.VERTICAL);
        separator.setPrefHeight(130);
        separator.setStyle("-fx-background-color: #E0E0E0;");
        
        // Assemble the horizontal content box
        contentBox.getChildren().addAll(imageView, infoBox, separator, buttonsBox);
        
        // Add the content box to the card
        card.getChildren().add(contentBox);
        
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
    
    private void handleGestionCandidatures(Universite universite) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestioncandidatures.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) universiteListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Candidatures");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible d'ouvrir l'interface de gestion des candidatures", e.getMessage());
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