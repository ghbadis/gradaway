package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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
import models.Candidature;
import Services.CandidatureService;
import Services.ServiceUniversite;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CandidatureCardsController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private FlowPane candidatureCardsPane;
    @FXML
    private Button refreshButton;
    @FXML
    private Button retourButton;
    
    private CandidatureService candidatureService;
    private ServiceUniversite universiteService;
    private ObservableList<Candidature> candidatureList = FXCollections.observableArrayList();
    private FilteredList<Candidature> filteredCandidatures;
    
    private Connection connection;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing CandidatureCardsController...");
        candidatureService = new CandidatureService();
        universiteService = new ServiceUniversite();
        
        // Setup database connection
        initializeConnection();
        
        // Load candidatures
        loadCandidatures();
        
        System.out.println("CandidatureCardsController initialized successfully.");
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
            loadCandidatures();
        } else {
            try {
                List<Candidature> allCandidatures = candidatureService.getAllCandidatures();
                List<Candidature> filteredCandidatures = allCandidatures.stream()
                        .filter(c -> {
                            String universityName = getUniversityName(c.getId_universite()).toLowerCase();
                            return universityName.contains(searchText);
                        })
                        .collect(Collectors.toList());
                
                displayCandidatures(filteredCandidatures);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de recherche", 
                        "Une erreur est survenue lors de la recherche", e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleRefreshButton() {
        searchField.clear();
        loadCandidatures();
    }
    
    @FXML
    private void handleRetourButton() {
        try {
            closeConnection();
            Parent root = FXMLLoader.load(getClass().getResource("/adminconditature.fxml"));
            Stage stage = (Stage) retourButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation",
                    "Impossible de retourner à la page précédente", e.getMessage());
        }
    }
    
    private void loadCandidatures() {
        try {
            List<Candidature> candidatures = candidatureService.getAllCandidatures();
            System.out.println("Retrieved " + candidatures.size() + " candidatures from service");
            
            // Clear our observable list
            candidatureList.clear();
            
            // Add each candidature to ensure they're properly loaded
            for (Candidature c : candidatures) {
                candidatureList.add(c);
            }
            
            displayCandidatures(candidatureList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", 
                    "Une erreur est survenue lors du chargement des candidatures", e.getMessage());
        }
    }
    
    private void displayCandidatures(List<Candidature> candidatures) {
        candidatureCardsPane.getChildren().clear();
        
        if (candidatures.isEmpty()) {
            Label noResultsLabel = new Label("Aucune candidature trouvée");
            noResultsLabel.setTextFill(Color.WHITE);
            noResultsLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            candidatureCardsPane.getChildren().add(noResultsLabel);
            return;
        }
        
        for (Candidature candidature : candidatures) {
            VBox card = createCandidatureCard(candidature);
            candidatureCardsPane.getChildren().add(card);
        }
    }
    
    private VBox createCandidatureCard(Candidature candidature) {
        // Main card container
        VBox card = new VBox(10);
        card.setPrefWidth(280);
        card.setPrefHeight(350);
        card.setStyle("-fx-background-color: #1A3473; -fx-background-radius: 10px;");
        card.setPadding(new Insets(15));
        
        // Get user and university info
        String userName = getUserName(candidature.getUser_id());
        String universityName = getUniversityName(candidature.getId_universite());
        
        // Header with domaine
        Label domaineLabel = new Label(candidature.getDomaine());
        domaineLabel.setTextFill(Color.WHITE);
        domaineLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        domaineLabel.setWrapText(true);
        domaineLabel.setTextAlignment(TextAlignment.CENTER);
        domaineLabel.setAlignment(Pos.CENTER);
        domaineLabel.setPrefWidth(250);
        
        // Info box
        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.TOP_LEFT);
        
        // Student info
        Label studentLabel = new Label("Étudiant: " + userName);
        studentLabel.setTextFill(Color.WHITE);
        studentLabel.setWrapText(true);
        
        // University info
        Label universityLabel = new Label("Université: " + universityName);
        universityLabel.setTextFill(Color.WHITE);
        universityLabel.setWrapText(true);
        
        // Date de remise
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = candidature.getDate_de_remise_c() != null ? 
                                dateFormat.format(candidature.getDate_de_remise_c()) : "Non définie";
        Label dateLabel = new Label("Date de soumission: " + formattedDate);
        dateLabel.setTextFill(Color.WHITE);
        
        // Add a label for the university image
        Label imageLabel = new Label("Logo de l'université:");
        imageLabel.setTextFill(Color.WHITE);
        imageLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        imageLabel.setAlignment(Pos.CENTER);
        
        // Add university image instead of status
        ImageView universityImageView = new ImageView();
        universityImageView.setFitHeight(80);
        universityImageView.setFitWidth(100);
        universityImageView.setPreserveRatio(true);
        
        // Load university image
        try {
            // Get the university info from service
            entities.Universite universite = universiteService.recuperer(candidature.getId_universite());
            
            if (universite != null && universite.getPhotoPath() != null && !universite.getPhotoPath().isEmpty()) {
                // Try to load from resources folder
                String defaultImagePath = "/images/default_university.png";
                try {
                    // Try to load from resources
                    URL photoUrl = getClass().getResource("/" + universite.getPhotoPath());
                    if (photoUrl != null) {
                        Image universityImage = new Image(photoUrl.toExternalForm());
                        universityImageView.setImage(universityImage);
                    } else {
                        // Try as a file path
                        File photoFile = new File("src/main/resources/" + universite.getPhotoPath());
                        if (photoFile.exists()) {
                            Image universityImage = new Image(photoFile.toURI().toString());
                            universityImageView.setImage(universityImage);
                        } else {
                            // If file not found, use default image
                            URL defaultUrl = getClass().getResource(defaultImagePath);
                            if (defaultUrl != null) {
                                Image defaultImage = new Image(defaultUrl.toExternalForm());
                                universityImageView.setImage(defaultImage);
                            }
                        }
                    }
                } catch (Exception e) {
                    // Load default image on error
                    URL defaultUrl = getClass().getResource(defaultImagePath);
                    if (defaultUrl != null) {
                        Image defaultImage = new Image(defaultUrl.toExternalForm());
                        universityImageView.setImage(defaultImage);
                    }
                    System.err.println("Error loading image: " + e.getMessage());
                }
            } else {
                // Load default image if no photo path
                URL defaultUrl = getClass().getResource("/images/default_university.png");
                if (defaultUrl != null) {
                    Image defaultImage = new Image(defaultUrl.toExternalForm());
                    universityImageView.setImage(defaultImage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Center the image
        HBox imageBox = new HBox();
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPadding(new Insets(5));
        imageBox.setStyle("-fx-background-color: white; -fx-background-radius: 5px; -fx-border-color: #3E92CC; -fx-border-radius: 5px;");
        imageBox.getChildren().add(universityImageView);
        
        infoBox.getChildren().addAll(studentLabel, universityLabel, dateLabel, imageLabel, imageBox);
        
        // Action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button viewButton = new Button("supprimer");
        viewButton.setStyle("-fx-background-color: #3E92CC; -fx-text-fill: white;");
        viewButton.setPrefWidth(120);
        
        // Set view button action
        viewButton.setOnAction(e -> supprimerCandidature(candidature));
        
        buttonBox.getChildren().add(viewButton);
        
        // Add some spacing
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // Assemble the card
        card.getChildren().addAll(domaineLabel, infoBox, spacer, buttonBox);
        
        return card;
    }
    
    private String getUserName(int userId) {
        try {
            if (connection != null && !connection.isClosed()) {
                String query = "SELECT nom, prenom FROM utilisateur WHERE id_user = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    return resultSet.getString("prenom") + " " + resultSet.getString("nom");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting user name: " + e.getMessage());
        }
        return "Utilisateur #" + userId;
    }
    
    private String getUniversityName(int universiteId) {
        try {
            if (connection != null && !connection.isClosed()) {
                String query = "SELECT nom FROM universite WHERE id_universite = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, universiteId);
                ResultSet resultSet = statement.executeQuery();
                
                if (resultSet.next()) {
                    return resultSet.getString("nom");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting university name: " + e.getMessage());
        }
        return "Université #" + universiteId;
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
    
    private void supprimerCandidature(Candidature candidature) {
        boolean success = candidatureService.deleteCandidature(candidature.getId_c());
        if (success) {
            try {
                loadCandidatures(); // Refresh the list
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur de suppression", "La suppression a échoué.", "Veuillez réessayer.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur de suppression", "La suppression a échoué.", "Veuillez réessayer.");
        }
    }
} 