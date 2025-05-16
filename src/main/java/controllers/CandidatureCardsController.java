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
import utils.UserSession;

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
    @FXML
    private Button ajouterButton;
    @FXML
    private ComboBox<String> statusFilterComboBox;
    @FXML
    private Button accueilButton;
    @FXML
    private Button userButton;
    @FXML
    private Button dossierButton;
    @FXML
    private Button universiteButton;
    @FXML
    private Button entretienButton;
    @FXML
    private Button evenementButton;
    @FXML
    private Button hebergementButton;
    @FXML
    private Button restaurantButton;
    @FXML
    private Button volsButton;
    @FXML
    private Button logoutButton;
    
    private CandidatureService candidatureService;
    private ServiceUniversite universiteService;
    private ObservableList<Candidature> candidatureList = FXCollections.observableArrayList();
    private FilteredList<Candidature> filteredCandidatures;

    private int currentUserId ;
    
    private Connection connection;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing CandidatureCardsController...");
        candidatureService = new CandidatureService();
        universiteService = new ServiceUniversite();
        
        // Setup database connection
        initializeConnection();
        
        // Initialize status filter
        initializeStatusFilter();
        
        // Load candidatures will be called after setUserId is called
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
    
    private void initializeStatusFilter() {
        // Create a status filter if the ComboBox exists in FXML
        if (statusFilterComboBox != null) {
            // Add status options
            statusFilterComboBox.getItems().addAll(
                "Tous les statuts",
                "En attente",  // pending
                "Acceptée",    // accepted
                "Refusée"      // rejected
            );
            statusFilterComboBox.setValue("Tous les statuts");
            
            // Add listener for status changes
            statusFilterComboBox.setOnAction(event -> handleFilterByStatus());
        }
    }
    
    private void handleFilterByStatus() {
        if (statusFilterComboBox == null) return;
        
        String selectedStatus = statusFilterComboBox.getValue();
        String searchText = searchField.getText().toLowerCase().trim();
        
        try {
            List<Candidature> allCandidatures = candidatureService.getAllCandidatures();
            List<Candidature> filteredCandidatures = allCandidatures.stream()
                    .filter(c -> {
                        // Filter by university name if search text is not empty
                        boolean matchesSearch = true;
                        if (!searchText.isEmpty()) {
                            String universityName = getUniversityName(c.getId_universite()).toLowerCase();
                            matchesSearch = universityName.contains(searchText);
                        }
                        
                        // Filter by status if not "All statuses"
                        boolean matchesStatus = true;
                        if (selectedStatus != null && !selectedStatus.equals("Tous les statuts")) {
                            String status = c.getStatus();
                            if (status == null) status = "pending";
                            
                            // Map French labels to status values in database
                            switch (selectedStatus) {
                                case "En attente":
                                    matchesStatus = status.equals("pending");
                                    break;
                                case "Acceptée":
                                    matchesStatus = status.equals("accepted");
                                    break;
                                case "Refusée":
                                    matchesStatus = status.equals("rejected");
                                    break;
                                default:
                                    matchesStatus = true;
                            }
                        }
                        
                        return matchesSearch && matchesStatus;
                    })
                    .collect(Collectors.toList());
            
            displayCandidatures(filteredCandidatures);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de filtrage", 
                    "Une erreur est survenue lors du filtrage des candidatures", e.getMessage());
        }
    }
    
    @FXML
    private void handleSearchButton() {
        // Call the filter method to handle both name and status filtering
        handleFilterByStatus();
    }
    
    @FXML
    private void handleRefreshButton() {
        searchField.clear();
        if (statusFilterComboBox != null) {
            statusFilterComboBox.setValue("Tous les statuts");
        }
        loadCandidatures();
    }
    
    @FXML
    private void handleRetourButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Acceuil.fxml"));
            Parent root = loader.load();
            Acceuilcontroller controller = loader.getController();
            controller.setUserId(this.getCurrentUserId()); // Pass the current user ID

            Stage stage = (Stage) retourButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", 
                    "Impossible de retourner à l'accueil", e.getMessage());
        }
    }
    
    @FXML
    private void handleAjouterButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listuniversitecards.fxml"));
            Parent root = loader.load();
            UniversiteCardsController controller = loader.getController();
            // Pass the current user ID to the UniversiteCardsController
            controller.setUserId(this.getCurrentUserId());
            Stage stage = (Stage) ajouterButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Universités");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la liste des universités", e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadCandidatures() {
        try {
            if (currentUserId <= 0) {
                System.out.println("[DEBUG] No user ID set, not loading candidatures.");
                candidatureList.clear();
                displayCandidatures(candidatureList);
                return;
            }
            
            System.out.println("[DEBUG] Loading candidatures for user ID: " + currentUserId);
            List<Candidature> candidatures = candidatureService.getAllCandidatures();
            System.out.println("[DEBUG] Retrieved " + candidatures.size() + " candidatures for current user");
            
            // Clear the list before adding new items
            candidatureList.clear();
            
            // Add all candidatures to the list
            candidatureList.addAll(candidatures);
            
            // Display the candidatures
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
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setPrefHeight(300);
        card.setStyle("-fx-background-color: #1A3473; -fx-background-radius: 10px;");
        card.setPadding(new Insets(15));
        
        // Get user and university info
        String userName = getUserName(candidature.getUser_id());
        String universityName = getUniversityName(candidature.getId_universite());
        
        // Add university image at the top
        ImageView universityImageView = new ImageView();
        universityImageView.setFitHeight(100);
        universityImageView.setFitWidth(130);
        universityImageView.setPreserveRatio(true);
        
        // Load university image
        try {
            // Get the university info from service
            entities.Universite universite = universiteService.recuperer(candidature.getId_universite());
            
            if (universite != null && universite.getPhotoPath() != null && !universite.getPhotoPath().isEmpty()) {
                // Try to load from resources folder
                String defaultImagePath = "/images/default_university.png";
                try {
                    System.out.println("CandidatureCardsController: Trying to load image from path: " + universite.getPhotoPath());
                    
                    // Try to load from resources
                    URL photoUrl = getClass().getResource("/" + universite.getPhotoPath());
                    if (photoUrl != null) {
                        System.out.println("Loading from resources URL: " + photoUrl);
                        Image universityImage = new Image(photoUrl.toExternalForm());
                        universityImageView.setImage(universityImage);
                        System.out.println("Image loaded successfully from resources");
                    } else {
                        // Try as a file path
                        String resourcePath = "src/main/resources/" + universite.getPhotoPath();
                        File photoFile = new File(resourcePath);
                        System.out.println("Trying to load from file path: " + photoFile.getAbsolutePath());
                        
                        if (photoFile.exists()) {
                            System.out.println("File exists, loading from: " + photoFile.toURI());
                            Image universityImage = new Image(photoFile.toURI().toString());
                            universityImageView.setImage(universityImage);
                            System.out.println("Image loaded successfully from file");
                        } else {
                            // Try as direct file path
                            File directFile = new File(universite.getPhotoPath());
                            System.out.println("Trying as direct file path: " + directFile.getAbsolutePath());
                            
                            if (directFile.exists()) {
                                System.out.println("Direct file exists, loading from: " + directFile.toURI());
                                Image universityImage = new Image(directFile.toURI().toString());
                                universityImageView.setImage(universityImage);
                                System.out.println("Image loaded successfully from direct path");
                            } else {
                                // If file not found, use default image
                                System.out.println("Could not find image, using default");
                                URL defaultUrl = getClass().getResource(defaultImagePath);
                                if (defaultUrl != null) {
                                    Image defaultImage = new Image(defaultUrl.toExternalForm());
                                    universityImageView.setImage(defaultImage);
                                    System.out.println("Default image loaded");
                                } else {
                                    System.out.println("Even default image couldn't be loaded!");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Load default image on error
                    System.err.println("Error loading university photo: " + e.getMessage());
                    e.printStackTrace();
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
        
        // Center the image in a box with background
        HBox imageBox = new HBox();
        imageBox.setAlignment(Pos.CENTER);
        imageBox.setPadding(new Insets(8));
        imageBox.setStyle("-fx-background-color: white; -fx-background-radius: 8px; -fx-border-color: #3E92CC; -fx-border-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
        imageBox.getChildren().add(universityImageView);
        
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
        
        // Add status label with appropriate styling
        String status = candidature.getStatus();
        if (status == null) {
            status = "pending";
        }
        
        Label statusLabel = new Label();
        statusLabel.setPrefWidth(150);
        statusLabel.setPrefHeight(30);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Style based on status
        switch (status) {
            case "accepted":
                statusLabel.setText("ACCEPTÉE");
                statusLabel.setTextFill(Color.WHITE);
                statusLabel.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 5px; -fx-padding: 5px;");
                break;
            case "rejected":
                statusLabel.setText("REFUSÉE");
                statusLabel.setTextFill(Color.WHITE);
                statusLabel.setStyle("-fx-background-color: #F44336; -fx-background-radius: 5px; -fx-padding: 5px;");
                break;
            case "pending":
            default:
                statusLabel.setText("EN ATTENTE");
                statusLabel.setTextFill(Color.BLACK);
                statusLabel.setStyle("-fx-background-color: #FFC107; -fx-background-radius: 5px; -fx-padding: 5px;");
                break;
        }
        
        // Add status to info box
        infoBox.getChildren().addAll(studentLabel, universityLabel, dateLabel, statusLabel);
        
        // Buttons
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        
        Button supprimerButton = new Button("Supprimer");
        supprimerButton.setStyle("-fx-background-color: #D8315B; -fx-text-fill: white;");
        supprimerButton.setPrefWidth(80);
        supprimerButton.setPrefHeight(30);
        supprimerButton.setOnAction(e -> supprimerCandidature(candidature));
        
        // Check if user is admin to add gestion button
        boolean isAdmin = checkIfUserIsAdmin();
        
        if (isAdmin) {
            Button gestionButton = new Button("Gérer");
            gestionButton.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
            gestionButton.setPrefWidth(80);
            gestionButton.setPrefHeight(30);
            gestionButton.setOnAction(e -> navigateToGestionCandidatures());
            
            buttonsBox.getChildren().addAll(supprimerButton, gestionButton);
        } else {
            buttonsBox.getChildren().add(supprimerButton);
        }
        
        // Add some spacing at the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // Assemble the card
        card.getChildren().addAll(imageBox, domaineLabel, infoBox, spacer, buttonsBox);
        
        return card;
    }
    
    private String getUserName(int userId) {
        try {
            if (connection != null && !connection.isClosed()) {
                String query = "SELECT nom, prenom FROM user WHERE id = ?";
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
    
    private boolean checkIfUserIsAdmin() {
        try {
            String query = "SELECT role FROM user WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, currentUserId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String role = rs.getString("role");
                return "admin".equalsIgnoreCase(role);
            }
            
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void navigateToGestionCandidatures() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestioncandidatures.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) candidatureCardsPane.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Candidatures");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible d'ouvrir l'interface de gestion des candidatures", e.getMessage());
        }
    }
    
    public void setUserId(int userId) {
        this.currentUserId = userId;
        System.out.println("[DEBUG] CandidatureCardsController: setUserId called with " + userId);
        if (candidatureService != null) {
            candidatureService.setCurrentUserId(userId);
            // Load candidatures after setting the user ID
            loadCandidatures();
        }
    }

    public int getCurrentUserId() {
        return this.currentUserId;
    }

    @FXML
    private void handleAccueilButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Acceuil.fxml"));
            Parent root = loader.load();
            Acceuilcontroller controller = loader.getController();
            controller.setUserId(this.getCurrentUserId());
            Stage stage = (Stage) accueilButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir l'accueil", e.getMessage());
        }
    }

    @FXML
    private void handleUserButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();
            EditProfileController controller = loader.getController();
            controller.setUserId(this.getCurrentUserId());
            Stage stage = (Stage) userButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Mon Profil");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir le profil", e.getMessage());
        }
    }

    @FXML
    private void handleDossierButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDossier.fxml"));
            Parent root = loader.load();
            AjoutDossierController controller = loader.getController();
            controller.setEtudiantId(this.getCurrentUserId());
            Stage stage = (Stage) dossierButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion du Dossier");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion du dossier", e.getMessage());
        }
    }

    @FXML
    private void handleUniversiteButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listuniversitecards.fxml"));
            Parent root = loader.load();
            UniversiteCardsController controller = loader.getController();
            controller.setUserId(this.getCurrentUserId());
            Stage stage = (Stage) universiteButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Universités");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la liste des universités", e.getMessage());
        }
    }

    @FXML
    private void handleEntretienButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemanderEntretien.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) entretienButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Demander Entretien");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la vue entretien", e.getMessage());
        }
    }

    @FXML
    private void handleEvenementButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affiche_evenement.fxml"));
            Parent root = loader.load();
            Ajouterafficheevenementcontrolleur controller = loader.getController();
            controller.setCurrentUserId(this.getCurrentUserId());
            Stage stage = (Stage) evenementButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Événements");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la vue des événements", e.getMessage());
        }
    }

    @FXML
    private void handleHebergementButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) hebergementButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Foyers");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la vue des foyers", e.getMessage());
        }
    }

    @FXML
    private void handleRestaurantButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurantClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) restaurantButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Restaurants");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la vue des restaurants", e.getMessage());
        }
    }

    @FXML
    private void handleVolsButton() {
        try {
            System.out.println("AcceuilController: Opening AfficherVolsUtilisateur view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AfficherVolsUtilisateur.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Vols Disponibles");
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("AcceuilController: Error loading AfficherVolsUtilisateur.fxml: " + e.getMessage());
            //showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des vols.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("AcceuilController: Unexpected error: " + e.getMessage());
            //showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogoutButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - GradAway");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Déconnexion", "Impossible de se déconnecter", e.getMessage());
        }
    }
} 