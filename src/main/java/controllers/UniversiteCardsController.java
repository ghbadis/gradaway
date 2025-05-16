package controllers;

import entities.Conditature;
import entities.Universite;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
import utils.PDFGenerator;
import utils.QRCodeGenerator;
import javafx.scene.web.WebView;
import java.awt.Desktop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import java.util.Base64;
import javafx.scene.control.ScrollPane;

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

    private final ServiceUniversite serviceUniversite = new ServiceUniversite();
    private final ServiceConditature serviceConditature = new ServiceConditature();
    private final CandidatureService candidatureService = new CandidatureService();

    private int currentUserId = 0;
    private int currentDossierId;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listcandidaturecards.fxml"));
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

    @FXML
    private void handleRetourButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listcandidaturecards.fxml"));
            Parent root = loader.load();
            CandidatureCardsController controller = loader.getController();
            controller.setUserId(this.getCurrentUserId());
            Stage stage = (Stage) retourButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Mes Candidatures");
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation",
                    "Impossible de retourner à la liste des candidatures", e.getMessage());
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
                System.out.println("Trying to load image from path: " + university.getPhotoPath());
                
                // First try to load from the resources folder
                URL photoUrl = getClass().getResource("/" + university.getPhotoPath());
                if (photoUrl != null) {
                    System.out.println("Loading from resources URL: " + photoUrl);
                    javafx.scene.image.Image universityImage = new javafx.scene.image.Image(photoUrl.toExternalForm());
                    imageView.setImage(universityImage);
                    System.out.println("Image loaded successfully from resources");
                } else {
                    // Try as a file path if not found in resources
                    String resourcePath = "src/main/resources/" + university.getPhotoPath();
                    File photoFile = new File(resourcePath);
                    System.out.println("Trying to load from file path: " + photoFile.getAbsolutePath());
                    
                    if (photoFile.exists()) {
                        System.out.println("File exists, loading from: " + photoFile.toURI());
                        javafx.scene.image.Image universityImage = new javafx.scene.image.Image(photoFile.toURI().toString());
                        imageView.setImage(universityImage);
                        System.out.println("Image loaded successfully from file");
                    } else {
                        // Try as a direct file path (absolute path)
                        File directFile = new File(university.getPhotoPath());
                        System.out.println("Trying as direct file path: " + directFile.getAbsolutePath());
                        
                        if (directFile.exists()) {
                            System.out.println("Direct file exists, loading from: " + directFile.toURI());
                            javafx.scene.image.Image universityImage = new javafx.scene.image.Image(directFile.toURI().toString());
                            imageView.setImage(universityImage);
                            System.out.println("Image loaded successfully from direct path");
                        } else {
                            // Use default image if photo not found
                            System.out.println("Could not find image, using default");
                            URL defaultUrl = getClass().getResource(defaultImagePath);
                            if (defaultUrl != null) {
                                javafx.scene.image.Image defaultImage = new javafx.scene.image.Image(defaultUrl.toExternalForm());
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
            // Check if candidature already exists for this user and university
            if (candidatureService.candidatureExists(currentUserId, university.getId_universite())) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Vous avez déjà soumis une candidature à cette université.", "");
                return;
            }

            // Get today's date
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            String domaine = university.getDomaine();

            // Get dossier ID for the current user
            int dossierId = getDossierIdForUser(currentUserId);
            if (dossierId == -1) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun dossier trouvé pour cet utilisateur.", "");
                return;
            }

            Candidature candidature = new Candidature();
            candidature.setId_dossier(dossierId);
            candidature.setUser_id(currentUserId);
            candidature.setId_universite(university.getId_universite());
            candidature.setDate_de_remise_c(today);
            candidature.setDomaine(domaine);

            boolean success = candidatureService.addCandidature(candidature);

            if (success) {
                // Get university name for email
                String tempUniversiteName;
                try {
                    tempUniversiteName = serviceUniversite.recuperer(university.getId_universite()).getNom();
                } catch (SQLException e) {
                    System.err.println("Error getting university name: " + e.getMessage());
                    tempUniversiteName = "Université"; // Default fallback
                }
                final String universiteName = tempUniversiteName;

                // Get user information from database
                final String userEmail = getUserEmail(currentUserId);
                if (userEmail == null || userEmail.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", 
                        "Impossible de soumettre la candidature", 
                        "Aucune adresse email trouvée pour votre compte. Veuillez mettre à jour votre profil avec une adresse email valide.");
                    return;
                }
                final String userName = getUserName(currentUserId);
                final String name = (userName == null || userName.isEmpty()) ? 
                    "Étudiant" : userName; // Default fallback name

                // Format date for display
                String tempFormattedDate;
                try {
                    java.time.LocalDate localDate = today.toLocalDate();
                    tempFormattedDate = localDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                } catch (Exception e) {
                    System.err.println("Error formatting date: " + e.getMessage());
                    tempFormattedDate = today.toString();
                }
                final String formattedDate = tempFormattedDate;

                // Get university image path for the QR code card
                final String universityImagePath = university.getPhotoPath();

                // Send email notification with PDF
                try {
                    EmailService.sendCandidatureConfirmationEmail(userEmail, universiteName, domaine, formattedDate);
                    System.out.println("Email sent to: " + userEmail);
                } catch (Exception e) {
                    System.err.println("Error sending email: " + e.getMessage());
                    e.printStackTrace();
                }

                // Show success message with option to view the QR code
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Votre candidature a été soumise avec succès");
                alert.setContentText("Une confirmation a été envoyée par email. " +
                                    "Vous pouvez voir le QR code de votre candidature en cliquant sur le bouton ci-dessous.");
                
                // Add button to view QR code
                ButtonType viewQRCodeButton = new ButtonType("Voir le QR Code");
                ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
                
                alert.getButtonTypes().setAll(viewQRCodeButton, closeButton);
                
                alert.showAndWait().ifPresent(buttonType -> {
                    if (buttonType == viewQRCodeButton) {
                        try {
                            // Générer le PDF avec QR code
                            String pdfPath = utils.PDFGenerator.generateCandidatureCard(
                                name, universiteName, domaine, formattedDate, universityImagePath
                            );
                            File pdfFile = new File(pdfPath);
                            if (pdfFile.exists()) {
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().open(pdfFile);
                                } else {
                                    showAlert(Alert.AlertType.INFORMATION, "PDF généré", "Le PDF a été généré ici : " + pdfFile.getAbsolutePath(), "");
                                }
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Erreur", "Le PDF n'a pas pu être généré.", "");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showAlert(Alert.AlertType.ERROR, "Erreur", 
                                      "Impossible de générer ou d'ouvrir le PDF", e.getMessage());
                        }
                    }
                });
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

    /**
     * Shows a window with just the candidature QR code
     */
    private void showQRCodeWindow(String userName, String universityName, String domaine, 
                                 String submissionDate, String universityImagePath) {
        try {
            // Generate QR code with candidature information in exact format
            String qrData = "Candidat: " + userName + "\n" +
                            "Université: " + universityName + "\n" +
                            "Domaine: " + domaine + "\n" +
                            "Date: " + submissionDate;
            
            // Use QRCodeGenerator to create the QR code image
            java.awt.image.BufferedImage bufferedImage = QRCodeGenerator.generateQRCode(qrData, 400, 400);
            
            // Convert BufferedImage to JavaFX Image
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            javax.imageio.ImageIO.write(bufferedImage, "png", outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            javafx.scene.image.Image qrImage = new javafx.scene.image.Image(inputStream);
            
            // Create a new stage for displaying just the QR code
            Stage qrStage = new Stage();
            qrStage.setTitle("Code QR - " + universityName);
            
            // Create the layout
            VBox root = new VBox(15);
            root.setPadding(new Insets(20));
            root.setAlignment(Pos.CENTER);
            
            // Create and add ImageView for QR code with higher resolution
            javafx.scene.image.ImageView qrView = new javafx.scene.image.ImageView(qrImage);
            qrView.setFitWidth(400);
            qrView.setFitHeight(400);
            qrView.setPreserveRatio(true);
            qrView.setSmooth(true);
            
            // Make QR code zoomable with scroll
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setContent(qrView);
            scrollPane.setPannable(true);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPrefViewportHeight(400);
            scrollPane.setPrefViewportWidth(400);
            
            // Add a close button
            Button closeButton = new Button("Fermer");
            closeButton.setOnAction(e -> qrStage.close());
            closeButton.setPrefWidth(150);
            
            // Add components to layout
            root.getChildren().addAll(scrollPane, closeButton);
            
            // Create scene and show stage
            Scene scene = new Scene(root);
            qrStage.setScene(scene);
            qrStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                      "Impossible de générer le code QR", e.getMessage());
        }
    }
    
    /**
     * Get user's full name from database
     * @param userId User ID
     * @return User's full name or null if not found
     */
    private String getUserName(int userId) {
        String name = null;
        try {
            String query = "SELECT nom, prenom FROM user WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                name = (prenom != null ? prenom + " " : "") + (nom != null ? nom : "");
            }
        } catch (SQLException e) {
            System.err.println("Error getting user name: " + e.getMessage());
        }
        return name;
    }

    /**
     * Get user email from database
     * @param userId User ID
     * @return User's email address or null if not found
     */
    private String getUserEmail(int userId) {
        String email = null;
        try {
            String query = "SELECT email FROM user WHERE id = ?";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                email = rs.getString("email");
            }
        } catch (SQLException e) {
            System.err.println("Error getting user email: " + e.getMessage());
        }
        return email;
    }

    private int getDossierIdForUser(int userId) throws SQLException {
        String query = "SELECT id_dossier FROM dossier WHERE id_etudiant = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("id_dossier");
        }
        return -1;
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

    public void setUserId(int userId) {
        this.currentUserId = userId;
        System.out.println("[DEBUG] UniversiteCardsController: setUserId called with " + userId);
    }

    public void setDossierId(int dossierId) {
        this.currentDossierId = dossierId;
        candidatureService.setCurrentDossierId(dossierId);
    }

    public int getCurrentUserId() {
        return this.currentUserId;
    }

    /**
     * Debug method to test email functionality directly
     * This can be called from a test button or debug menu
     */
    private void debugTestEmail() {
        String email = getUserEmail(currentUserId);
        if (email == null || email.isEmpty()) {
            email = "mnbettaieb@gmail.com"; // Default fallback email
        }
        
        try {
            boolean success = EmailService.testSendPDF(email);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Test Email");
            if (success) {
                alert.setHeaderText("Test Email Sent");
                alert.setContentText("A test email with PDF attachment has been sent to: " + email);
            } else {
                alert.setHeaderText("Test Email Failed");
                alert.setContentText("Failed to send test email. Check console for details.");
            }
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Failed to send test email", e.getMessage());
        }
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
           // showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des vols.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("AcceuilController: Unexpected error: " + e.getMessage());
           // showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue.");
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