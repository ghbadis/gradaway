package controllers;

import entities.Conditature;
import entities.Universite;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import Services.ServiceConditature;
import Services.ServiceUniversite;
import Services.CandidatureService;
import models.Candidature;
import utils.EmailService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UniversiteCardsController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private FlowPane universityCardsPane;
    @FXML
    private Button refreshButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button retourButton;
    
    private final ServiceUniversite serviceUniversite = new ServiceUniversite();
    private final ServiceConditature serviceConditature = new ServiceConditature();
    private final CandidatureService candidatureService = new CandidatureService();
    
    // Hardcoded user ID and dossier ID for testing - in a real app, these would come from logged in user
    private final int currentUserId = 31; // Example user ID 
    private final int currentDossierId = 37; // Example dossier ID
    
    private Connection connection;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup database connection
        initializeConnection();
        
        // Load universities
        loadUniversities();
    }
    
    private void initializeConnection() {
        try {
            // Database connection
            String url = "jdbc:mysql://localhost:3306/gradaway";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion", 
                      "Impossible de se connecter à la base de données", e.getMessage());
        }
    }
    
    @FXML
    private void handleSearchButton() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            loadUniversities();
        } else {
            try {
                List<Universite> allUniversities = serviceUniversite.getAllUniversites();
                List<Universite> filteredUniversities = allUniversities.stream()
                        .filter(u -> u.getNom().toLowerCase().contains(searchText) || 
                                     u.getDomaine().toLowerCase().contains(searchText) || 
                                     u.getVille().toLowerCase().contains(searchText))
                        .collect(Collectors.toList());
                
                displayUniversities(filteredUniversities);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de recherche", 
                        "Une erreur est survenue lors de la recherche", e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleRefreshButton() {
        searchField.clear();
        loadUniversities();
    }
    
    @FXML
    private void handleCloseButton() {
        try {
            closeConnection();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminconditature.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation",
                    "Impossible de retourner à la page précédente", e.getMessage());
        }
    }


    

    private void loadUniversities() {
        try {
            List<Universite> universities = serviceUniversite.getAllUniversites();
            displayUniversities(universities);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", 
                    "Une erreur est survenue lors du chargement des universités", e.getMessage());
        }
    }
    
    private void displayUniversities(List<Universite> universities) {
        universityCardsPane.getChildren().clear();
        
        if (universities.isEmpty()) {
            Label noResultsLabel = new Label("Aucune université trouvée");
            noResultsLabel.setTextFill(Color.WHITE);
            noResultsLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            universityCardsPane.getChildren().add(noResultsLabel);
            return;
        }
        
        for (Universite university : universities) {
            VBox card = createUniversityCard(university);
            universityCardsPane.getChildren().add(card);
        }
    }
    
    private VBox createUniversityCard(Universite university) {
        // Main card container
        VBox card = new VBox(10);
        card.setPrefWidth(280);
        card.setPrefHeight(380); // Increased height to accommodate the image
        card.setStyle("-fx-background-color: #1A3473; -fx-background-radius: 10px;");
        card.setPadding(new Insets(15));
        
        // University photo
        javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView();
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
                    javafx.scene.image.Image universityImage = new javafx.scene.image.Image(photoUrl.toExternalForm());
                    imageView.setImage(universityImage);
                } else {
                    // Try as a file path if not found in resources
                    File photoFile = new File("src/main/resources/" + university.getPhotoPath());
                    if (photoFile.exists()) {
                        javafx.scene.image.Image universityImage = new javafx.scene.image.Image(photoFile.toURI().toString());
                        imageView.setImage(universityImage);
                    } else {
                        // Use default image if photo not found
                        URL defaultUrl = getClass().getResource(defaultImagePath);
                        if (defaultUrl != null) {
                            javafx.scene.image.Image defaultImage = new javafx.scene.image.Image(defaultUrl.toExternalForm());
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
                        javafx.scene.image.Image defaultImage = new javafx.scene.image.Image(defaultUrl.toExternalForm());
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
                    javafx.scene.image.Image defaultImage = new javafx.scene.image.Image(defaultUrl.toExternalForm());
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
        
        // Apply button
        Button applyButton = new Button("Soumettre Candidature");
        applyButton.setStyle("-fx-background-color: #3E92CC; -fx-text-fill: white;");
        applyButton.setPrefWidth(250);
        applyButton.setPrefHeight(40);
        
        // Set up button action
        applyButton.setOnAction(e -> handleApplicationSubmission(university));
        
        // Add some spacing at the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // Assemble the card
        card.getChildren().addAll(imageView, nameLabel, infoBox, spacer, applyButton);
        
        return card;
    }
    
    private void handleApplicationSubmission(Universite university) {
        try {
            // Create a dialog to confirm the application
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Soumettre une Candidature");
            dialog.setHeaderText("Candidature pour " + university.getNom());
            
            // Add buttons
            ButtonType submitButtonType = new ButtonType("Soumettre", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
            
            // Create fields for the dialog
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            DatePicker datePicker = new DatePicker(LocalDate.now());
            TextField domaineField = new TextField(university.getDomaine());
            
            grid.add(new Label("Date de soumission:"), 0, 0);
            grid.add(datePicker, 1, 0);
            grid.add(new Label("Domaine d'étude:"), 0, 1);
            grid.add(domaineField, 1, 1);
            
            dialog.getDialogPane().setContent(grid);
            
            // Request focus on the date field by default
            Platform.runLater(datePicker::requestFocus);
            
            // Show the dialog and process the result
            dialog.showAndWait().ifPresent(response -> {
                if (response == submitButtonType) {
                    submitApplication(university.getId_universite(), 
                            datePicker.getValue().format(DateTimeFormatter.ISO_DATE), 
                            domaineField.getText());
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Une erreur est survenue lors de la soumission", e.getMessage());
        }
    }
    
    private void submitApplication(int universiteId, String submissionDate, String domaine) {
        try {
            // Create new Candidature object
            Candidature candidature = new Candidature();
            candidature.setId_dossier(currentDossierId);
            candidature.setUser_id(currentUserId);
            candidature.setId_universite(universiteId);
            candidature.setDate_de_remise_c(java.sql.Date.valueOf(submissionDate));
            candidature.setDomaine(domaine);
            
            // Call service to add the candidature
            boolean success = candidatureService.addCandidature(candidature);
            
            if (success) {
                // Get university name for email
                String universiteName = "";
                try {
                    universiteName = serviceUniversite.recuperer(universiteId).getNom();
                } catch (SQLException e) {
                    System.err.println("Error getting university name: " + e.getMessage());
                }
                
                // Send email notification
                EmailService.sendCandidatureConfirmationEmail("mnbettaieb@gmail.com", universiteName, domaine, submissionDate);
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Succès", 
                        "Votre candidature a été soumise avec succès", "Une notification a été envoyée par email.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                        "Impossible de soumettre la candidature - vérifiez que l'utilisateur et le dossier existent", "");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Une erreur est survenue lors de la soumission de la candidature", e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 