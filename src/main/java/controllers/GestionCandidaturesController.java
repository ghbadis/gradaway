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
import javafx.stage.Stage;
import Services.CandidatureService;
import Services.ServiceUniversite;
import models.Candidature;
import utils.EmailService;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GestionCandidaturesController implements Initializable {

    @FXML
    private TextField chercher_txtf;
    
    @FXML
    private Button chercher_button;
    
    @FXML
    private ComboBox<String> domaine_comb;
    
    @FXML
    private ComboBox<String> status_comb;
    
    @FXML
    private VBox candidatures_container;
    
    @FXML
    private Button retour_button;
    
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

    private final CandidatureService candidatureService = new CandidatureService();
    private final ServiceUniversite universiteService = new ServiceUniversite();
    private Connection connection;
    private List<Candidature> allCandidatures = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Établir la connexion à la base de données
            String dbUrl = "jdbc:mysql://localhost:3306/gradaway";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(dbUrl, user, password);
            
            // Charger les candidatures
            loadCandidatures();
            
            // Configurer les événements
            setupEventHandlers();
            
            // Initialiser le combobox des domaines
            initializeDomainComboBox();
            
            // Initialiser le combobox des statuts
            initializeStatusComboBox();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion", 
                    "Impossible de se connecter à la base de données", e.getMessage());
        }
    }

    private void initializeDomainComboBox() {
        try {
            // Obtenir tous les domaines distincts des universités
            List<String> domains = universiteService.getAllUniversites()
                    .stream()
                    .map(Universite::getDomaine)
                    .distinct()
                    .collect(Collectors.toList());
            
            // Ajouter l'option "Tous" au début
            domains.add(0, "Tous les domaines");
            
            // Remplir le combobox
            domaine_comb.getItems().addAll(domains);
            domaine_comb.setValue("Tous les domaines");
            
            // Ajouter l'écouteur pour filtrer lorsque le domaine change
            domaine_comb.setOnAction(event -> filterCandidatures());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible de récupérer les domaines d'étude", e.getMessage());
        }
    }

    private void initializeStatusComboBox() {
        // Créer la liste des statuts possibles avec des traductions en français
        List<String> statuses = new ArrayList<>();
        statuses.add("Tous les statuts");
        statuses.add("En attente");  // pending
        statuses.add("Acceptée");    // accepted
        statuses.add("Refusée");     // rejected
        
        // Remplir le combobox
        status_comb.getItems().addAll(statuses);
        status_comb.setValue("Tous les statuts");
        
        // Ajouter l'écouteur pour filtrer lorsque le statut change
        status_comb.setOnAction(event -> filterCandidatures());
    }

    private void setupEventHandlers() {
        // Recherche par nom d'université
        chercher_button.setOnAction(event -> filterCandidatures());
        
        // Retour à l'écran précédent
        retour_button.setOnAction(event -> handleRetourButton());
    }

    private void loadCandidatures() {
        try {
            // Récupérer toutes les candidatures
            allCandidatures = candidatureService.getAllCandidatures();
            displayCandidatures(allCandidatures);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible de charger les candidatures", e.getMessage());
        }
    }

    private void filterCandidatures() {
        String searchText = chercher_txtf.getText().toLowerCase().trim();
        String selectedDomain = domaine_comb.getValue();
        String selectedStatus = status_comb.getValue();
        
        // Map French status labels to English database values
        String statusFilter = null;
        if (selectedStatus != null && !selectedStatus.equals("Tous les statuts")) {
            switch (selectedStatus) {
                case "En attente":
                    statusFilter = "pending";
                    break;
                case "Acceptée":
                    statusFilter = "accepted";
                    break;
                case "Refusée":
                    statusFilter = "rejected";
                    break;
            }
        }
        
        final String finalStatusFilter = statusFilter;
        
        List<Candidature> filteredCandidatures = allCandidatures.stream()
                .filter(candidature -> {
                    boolean matchesSearch = true;
                    boolean matchesDomain = true;
                    boolean matchesStatus = true;
                    
                    // Filtrer par nom d'université
                    if (!searchText.isEmpty()) {
                        try {
                            Universite universite = universiteService.recuperer(candidature.getId_universite());
                            matchesSearch = universite.getNom().toLowerCase().contains(searchText);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            matchesSearch = false;
                        }
                    }
                    
                    // Filtrer par domaine
                    if (selectedDomain != null && !selectedDomain.equals("Tous les domaines")) {
                        matchesDomain = candidature.getDomaine().equals(selectedDomain);
                    }
                    
                    // Filtrer par statut
                    if (finalStatusFilter != null) {
                        String status = candidature.getStatus();
                        // Si le statut est null, on considère qu'il est "pending"
                        if (status == null) {
                            status = "pending";
                        }
                        matchesStatus = status.equals(finalStatusFilter);
                    }
                    
                    return matchesSearch && matchesDomain && matchesStatus;
                })
                .collect(Collectors.toList());
        
        displayCandidatures(filteredCandidatures);
    }

    private void displayCandidatures(List<Candidature> candidatures) {
        candidatures_container.getChildren().clear();
        
        if (candidatures.isEmpty()) {
            Label noResultsLabel = new Label("Aucune candidature trouvée");
            noResultsLabel.setTextFill(Color.valueOf("#1A237E"));
            noResultsLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            
            VBox noResultsBox = new VBox(noResultsLabel);
            noResultsBox.setAlignment(Pos.CENTER);
            noResultsBox.setPrefHeight(200);
            noResultsBox.setPrefWidth(760);
            noResultsBox.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 5);");
            
            candidatures_container.getChildren().add(noResultsBox);
            return;
        }
        
        // Ajouter chaque candidature dans le conteneur vertical
        for (Candidature candidature : candidatures) {
            VBox candidatureCard = createCandidatureCard(candidature);
            // Définir la largeur du card pour qu'il occupe tout l'espace horizontal disponible
            candidatureCard.setPrefWidth(760);
            candidatureCard.setMaxWidth(760);
            candidatures_container.getChildren().add(candidatureCard);
        }
    }

    private VBox createCandidatureCard(Candidature candidature) {
        // Récupérer les informations de l'université
        final Universite[] universite = {null};
        final String[] userName = {""};
        final String[] userEmail = {""};
        final String[] universiteNom = {"Inconnue"};
        
        // Debug information
        System.out.println("Creating card for candidature ID: " + candidature.getId_c());
        System.out.println("University ID in candidature: " + candidature.getId_universite());
        
        try {
            // Retrieve university info with better error handling
            try {
                universite[0] = universiteService.recuperer(candidature.getId_universite());
                if (universite[0] != null) {
                    universiteNom[0] = universite[0].getNom();
                    System.out.println("Found university: " + universiteNom[0] + " (ID: " + universite[0].getId_universite() + ")");
                } else {
                    System.out.println("ERROR: University with ID " + candidature.getId_universite() + " not found via service!");
                    
                    // Try direct database query as fallback
                    String checkQuery = "SELECT * FROM universite WHERE id_universite = ?";
                    PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
                    checkStmt.setInt(1, candidature.getId_universite());
                    ResultSet checkRs = checkStmt.executeQuery();
                    
                    if (checkRs.next()) {
                        universiteNom[0] = checkRs.getString("Nom");
                        System.out.println("Direct DB query found university: " + universiteNom[0]);
                    } else {
                        System.out.println("University ID " + candidature.getId_universite() + " does not exist in database!");
                    }
                }
            } catch (SQLException ue) {
                System.out.println("Error retrieving university: " + ue.getMessage());
                // Try direct database query as fallback
                String checkQuery = "SELECT * FROM universite WHERE id_universite = ?";
                PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
                checkStmt.setInt(1, candidature.getId_universite());
                ResultSet checkRs = checkStmt.executeQuery();
                
                if (checkRs.next()) {
                    universiteNom[0] = checkRs.getString("Nom");
                    System.out.println("Fallback: Direct DB query found university: " + universiteNom[0]);
                }
            }
            
            // Récupérer les informations de l'utilisateur
            String userQuery = "SELECT nom, prenom, email FROM user WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(userQuery);
            ps.setInt(1, candidature.getUser_id());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                userName[0] = rs.getString("nom") + " " + rs.getString("prenom");
                userEmail[0] = rs.getString("email");
                System.out.println("Found user: " + userName[0] + " (Email: " + userEmail[0] + ")");
            } else {
                System.out.println("User with ID " + candidature.getUser_id() + " not found!");
            }
        } catch (SQLException e) {
            System.out.println("ERROR retrieving data for candidature ID " + candidature.getId_c() + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        // Création de la carte
        VBox card = new VBox(15);
        card.setPrefWidth(760);
        card.setMinHeight(200);
        card.getStyleClass().addAll("white-bg", "shadow");
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 10);");
        card.setPadding(new Insets(20));
        
        // Créer un conteneur horizontal pour organiser les informations et les boutons
        HBox contentBox = new HBox(30);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        
        // Conteneur pour les informations de la candidature
        VBox infoBox = new VBox(10);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        infoBox.setPrefWidth(500);
        
        Label universiteLabel = new Label("Université: " + universiteNom[0]);
        universiteLabel.setTextFill(Color.valueOf("#1A237E"));
        universiteLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        universiteLabel.setWrapText(true);
        
        Label domaineLabel = new Label("Domaine: " + candidature.getDomaine());
        domaineLabel.setTextFill(Color.valueOf("#303F9F"));
        domaineLabel.setFont(Font.font("System", 16));
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Label dateLabel = new Label("Date de soumission: " + 
                (candidature.getDate_de_remise_c() != null ? 
                        dateFormat.format(candidature.getDate_de_remise_c()) : "Non spécifiée"));
        dateLabel.setTextFill(Color.valueOf("#303F9F"));
        dateLabel.setFont(Font.font("System", 16));
        
        Label etudiantLabel = new Label("Étudiant: " + userName[0]);
        etudiantLabel.setTextFill(Color.valueOf("#303F9F"));
        etudiantLabel.setFont(Font.font("System", 16));
        
        Label emailLabel = new Label("Email: " + userEmail[0]);
        emailLabel.setTextFill(Color.valueOf("#303F9F"));
        emailLabel.setFont(Font.font("System", 16));
        
        infoBox.getChildren().addAll(universiteLabel, domaineLabel, dateLabel, etudiantLabel, emailLabel);
        
        // Conteneur pour les boutons ou le statut
        VBox rightBox = new VBox(15);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setPrefWidth(200);
        
        // Get the status of the candidature
        String status = candidature.getStatus();
        if (status == null) {
            status = "pending";
        }
        
        // Display the appropriate status and action buttons based on status
        switch (status) {
            case "accepted":
                // Get acceptance date
                Date acceptationDate = candidature.getDate_acceptation();
                String formattedAcceptationDate = acceptationDate != null ? 
                        dateFormat.format(acceptationDate) : "Date inconnue";
                
                // Create status label with green background
                Label statusLabel = new Label("ACCEPTÉE");
                statusLabel.setTextFill(Color.WHITE);
                statusLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
                statusLabel.setAlignment(Pos.CENTER);
                statusLabel.setPrefWidth(180);
                statusLabel.setPrefHeight(40);
                statusLabel.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 5px; -fx-padding: 10px;");
                
                // Create date label
                Label acceptationDateLabel = new Label("Date: " + formattedAcceptationDate);
                acceptationDateLabel.setTextFill(Color.valueOf("#1A237E"));
                acceptationDateLabel.setFont(Font.font("System", 14));
                acceptationDateLabel.setAlignment(Pos.CENTER);
                
                rightBox.getChildren().addAll(statusLabel, acceptationDateLabel);
                break;
                
            case "rejected":
                // Create status label with red background
                Label rejectedLabel = new Label("REFUSÉE");
                rejectedLabel.setTextFill(Color.WHITE);
                rejectedLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
                rejectedLabel.setAlignment(Pos.CENTER);
                rejectedLabel.setPrefWidth(180);
                rejectedLabel.setPrefHeight(40);
                rejectedLabel.setStyle("-fx-background-color: #F44336; -fx-background-radius: 5px; -fx-padding: 10px;");
                
                rightBox.getChildren().add(rejectedLabel);
                break;
                
            case "pending":
            default:
                // Show action buttons for pending candidatures
                addActionButtons(rightBox, candidature, userEmail[0], universiteNom[0]);
                
                // Add pending status label above buttons
                Label pendingLabel = new Label("EN ATTENTE");
                pendingLabel.setTextFill(Color.BLACK);
                pendingLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
                pendingLabel.setAlignment(Pos.CENTER);
                pendingLabel.setPrefWidth(180);
                pendingLabel.setPrefHeight(30);
                pendingLabel.setStyle("-fx-background-color: #FFC107; -fx-background-radius: 5px; -fx-padding: 5px;");
                
                // Add the pending label at the beginning of the rightBox children
                rightBox.getChildren().add(0, pendingLabel);
                break;
        }
        
        // Ligne de séparation
        Separator separator = new Separator();
        separator.setOrientation(javafx.geometry.Orientation.VERTICAL);
        separator.setPrefHeight(150);
        separator.setStyle("-fx-background-color: #E0E0E0;");
        
        // Assemblage du conteneur horizontal
        contentBox.getChildren().addAll(infoBox, separator, rightBox);
        
        // Assembler la carte
        card.getChildren().add(contentBox);
        
        return card;
    }
    
    // Helper method to add action buttons to the card
    private void addActionButtons(VBox container, Candidature candidature, String userEmail, String universiteName) {
        final String candidatEmail = userEmail; // Store the email to ensure it's used correctly
        
        Button accepterButton = new Button("Accepter");
        accepterButton.getStyleClass().add("update-btn");
        accepterButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5px;");
        accepterButton.setPrefWidth(150);
        accepterButton.setPrefHeight(40);
        accepterButton.setFont(Font.font("System", 14));
        accepterButton.setOnAction(e -> handleAccepterCandidature(candidature, candidatEmail, universiteName));
        
        Button refuserButton = new Button("Refuser");
        refuserButton.getStyleClass().add("clear-btn");
        refuserButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 5px;");
        refuserButton.setPrefWidth(150);
        refuserButton.setPrefHeight(40);
        refuserButton.setFont(Font.font("System", 14));
        refuserButton.setOnAction(e -> handleRefuserCandidature(candidature, candidatEmail, universiteName));
        
        container.getChildren().addAll(accepterButton, refuserButton);
    }

    private void handleAccepterCandidature(Candidature candidature, String userEmail, String universiteName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Accepter la candidature");
        alert.setContentText("Êtes-vous sûr de vouloir accepter cette candidature ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Update candidature status to accepted
                boolean statusUpdated = candidatureService.acceptCandidature(candidature.getId_c());
                
                if (statusUpdated) {
                    // Envoyer l'email de confirmation
                    EmailService.sendCandidatureAcceptationEmail(userEmail, universiteName, candidature.getDomaine());
                    
                    // Actualiser l'affichage
                    loadCandidatures();
                    
                    showAlert(Alert.AlertType.INFORMATION, "Succès", 
                            "La candidature a été acceptée", 
                            "Un email de confirmation a été envoyé à l'étudiant à l'adresse: " + userEmail);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", 
                            "Erreur lors de l'acceptation de la candidature", 
                            "Impossible de mettre à jour le statut de la candidature.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                        "Erreur lors de l'acceptation de la candidature", e.getMessage());
            }
        }
    }

    private void handleRefuserCandidature(Candidature candidature, String userEmail, String universiteName) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Refuser la candidature");
        alert.setContentText("Êtes-vous sûr de vouloir refuser cette candidature ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Update candidature status to rejected
                boolean statusUpdated = candidatureService.rejectCandidature(candidature.getId_c());
                
                if (statusUpdated) {
                    // Envoyer l'email de refus
                    EmailService.sendCandidatureRejectionEmail(userEmail, universiteName, candidature.getDomaine());
                    
                    // Actualiser l'affichage
                    loadCandidatures();
                    
                    showAlert(Alert.AlertType.INFORMATION, "Succès", 
                            "La candidature a été refusée", 
                            "Un email de notification a été envoyé à l'étudiant à l'adresse: " + userEmail);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Erreur", 
                            "Erreur lors du refus de la candidature", 
                            "Impossible de mettre à jour le statut de la candidature.");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                        "Erreur lors du refus de la candidature", e.getMessage());
            }
        }
    }

    private void deleteCandidature(int candidatureId) throws SQLException {
        String query = "DELETE FROM candidature WHERE id_c = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, candidatureId);
        ps.executeUpdate();
    }

    private void sendAcceptationEmail(String toEmail, String universiteName, String domaine) {
        EmailService.sendCandidatureAcceptationEmail(toEmail, universiteName, domaine);
    }

    private void sendRefusalEmail(String toEmail, String universiteName, String domaine) {
        EmailService.sendCandidatureRejectionEmail(toEmail, universiteName, domaine);
    }

    private void handleRetourButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recupereruniversite.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retour_button.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Universités");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Impossible de revenir à la liste des universités", e.getMessage());
        }
    }

    @FXML
    private void handleAccueilButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AcceuilAdmin.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) accueilButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil Admin");
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir l'accueil admin: " + e.getMessage());
        }
    }

    @FXML
    private void handleUserButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) userButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Utilisateurs");
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des utilisateurs: " + e.getMessage());
        }
    }

    @FXML
    private void handleDossierButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDossier.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dossierButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Dossiers");
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des dossiers: " + e.getMessage());
        }
    }

    @FXML
    private void handleUniversiteButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recupereruniversite.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) universiteButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Universités");
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des universités: " + e.getMessage());
        }
    }

    @FXML
    private void handleEntretienButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gestionnaire.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) entretienButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Entretiens");
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des entretiens: " + e.getMessage());
        }
    }

    @FXML
    private void handleEvenementButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_evenement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) evenementButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Événements");
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des événements: " + e.getMessage());
        }
    }

    @FXML
    private void handleHebergementButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFoyer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) hebergementButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Foyers");
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des foyers: " + e.getMessage());
        }
    }

    @FXML
    private void handleRestaurantButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterRestaurant.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) restaurantButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Restaurants");
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible d'ouvrir la gestion des restaurants: " + e.getMessage());
        }
    }

    @FXML
    private void handleVolsButton() {
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Déconnexion", "Impossible de se déconnecter: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Helper overload for convenience
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        showAlert(alertType, title, null, content);
    }
} 