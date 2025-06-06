package controllers;

import Services.ServiceReservationRestaurant;
import entities.ReservationRestaurant;
import entities.Restaurant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import utils.EmailSender;
import utils.QRCodeGeneratorF;
import utils.SessionManager;

public class ReserverRestaurantController {

    @FXML private Label headerLabel;
    @FXML private ImageView restaurantImage;
    @FXML private Label nomLabel;
    @FXML private Label adresseLabel;
    @FXML private Label villeLabel;
    @FXML private Label capaciteLabel;
    @FXML private Label horairesLabel;
    
    @FXML private TextField nomClientField;
    @FXML private TextField emailField;
    @FXML private Spinner<Integer> personnesSpinner;
    
    @FXML private Button reserverButton;
    @FXML private Button annulerButton;
    // Dashboard navigation buttons
    @FXML private Button accueilButton;
    @FXML private Button userButton;
    @FXML private Button dossierButton;
    @FXML private Button universiteButton;
    @FXML private Button entretienButton;
    @FXML private Button evenementButton;
    @FXML private Button hebergementButton;
    @FXML private Button restaurantButton;
    @FXML private Button volsButton;
    @FXML private Button logoutButton;
    
    private Restaurant restaurant;
    private ServiceReservationRestaurant serviceReservation;
    
    @FXML
    public void initialize() {
        serviceReservation = new ServiceReservationRestaurant();
        
        // Configuration du spinner pour le nombre de personnes (1-20 par défaut)
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 2);
        personnesSpinner.setValueFactory(valueFactory);
        
        // Vérifier que tous les champs sont correctement initialisés
        if (nomClientField == null || emailField == null || personnesSpinner == null) {
            System.err.println("Erreur: Certains champs FXML ne sont pas correctement initialisés (nomClientField, emailField, personnesSpinner)");
            showAlert("Erreur", "Une erreur est survenue lors de l'initialisation de l'interface. Veuillez redémarrer l'application.", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            // Récupérer l'email de l'utilisateur connecté depuis le SessionManager
            String userEmail = SessionManager.getInstance().getUserEmail();
            if (userEmail != null && !userEmail.isEmpty()) {
                // Afficher l'email dans le champ et le rendre non modifiable
                emailField.setText(userEmail);
                emailField.setEditable(false);
                emailField.setDisable(true);
                emailField.setStyle("-fx-background-color: #e9ecef; -fx-opacity: 0.8; -fx-text-fill: #666666;");
                System.out.println("ReserverRestaurantController: Email utilisateur récupéré depuis SessionManager: " + userEmail);
            } else {
                System.out.println("ReserverRestaurantController: Aucun email utilisateur trouvé dans SessionManager");
                showAlert("Erreur", "Vous devez être connecté pour faire une réservation", Alert.AlertType.ERROR);
                // Rediriger vers la page de connexion
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) emailField.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Login - GradAway");
                    stage.centerOnScreen();
                } catch (IOException e) {
                    System.err.println("Erreur lors de la redirection vers la page de connexion: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'email utilisateur: " + e.getMessage());
            showAlert("Erreur", "Une erreur est survenue lors de la récupération de vos informations", Alert.AlertType.ERROR);
        }
        
        // Setup dashboard navigation
        setupNavigationButtons();
    }
    
    private void setupNavigationButtons() {
        accueilButton.setOnAction(this::onAccueilButtonClick);
        userButton.setOnAction(this::onProfileButtonClick);
        dossierButton.setOnAction(this::ondossierButtonClick);
        universiteButton.setOnAction(this::onuniversiteButtonClick);
        entretienButton.setOnAction(this::onentretienButtonClick);
        evenementButton.setOnAction(this::onevenementButtonClick);
        hebergementButton.setOnAction(this::onhebergementButtonClick);
        restaurantButton.setOnAction(this::onrestaurantButtonClick);
        volsButton.setOnAction(this::onvolsButtonClick);
        logoutButton.setOnAction(this::onlogoutButtonClick);
    }

    private void navigateToScene(Parent root, ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        // Add fade transition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    private void onAccueilButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
            Parent root = loader.load();
            navigateToScene(root, event);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation vers l'accueil: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onProfileButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Mon Profil");
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture du profil: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void ondossierButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDossier.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion du Dossier");
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture du dossier: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onuniversiteButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminconditature.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Candidatures");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des candidatures: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onentretienButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemanderEntretien.fxml"));
            Parent root = loader.load();
            navigateToScene(root, event);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture de l'entretien: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onevenementButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affiche_evenement.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Événements");
            stage.setMinWidth(1133);
            stage.setMinHeight(691);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des événements: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onhebergementButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();
            navigateToScene(root, event);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des foyers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onrestaurantButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurantClient.fxml"));
            Parent root = loader.load();
            navigateToScene(root, event);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des restaurants: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onvolsButtonClick(ActionEvent event) {
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
    private void onlogoutButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            Scene scene = new Scene(root);
            loginStage.setScene(scene);
            loginStage.setTitle("Login - GradAway");
            loginStage.setResizable(true);
            loginStage.centerOnScreen();
            loginStage.show();
            // Close current window
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la déconnexion: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Définit le restaurant à réserver et met à jour l'interface
     */
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        
        if (restaurant != null) {
            // Mettre à jour l'en-tête
            headerLabel.setText("Réserver chez " + restaurant.getNom());
            
            // Mettre à jour les informations du restaurant
            nomLabel.setText(restaurant.getNom());
            adresseLabel.setText(restaurant.getAdresse());
            villeLabel.setText(restaurant.getVille() + ", " + restaurant.getPays());
            capaciteLabel.setText(String.valueOf(restaurant.getCapaciteTotale()) + " personnes");
            horairesLabel.setText(restaurant.getHoraireOuverture() + " - " + restaurant.getHoraireFermeture());
            
            // Charger l'image du restaurant
            try {
                if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
                    String imagePath = "src/main/resources/" + restaurant.getImage();
                    File file = new File(imagePath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        restaurantImage.setImage(image);
                    } else {
                        // Image par défaut si l'image n'existe pas
                        Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                        restaurantImage.setImage(defaultImage);
                    }
                } else {
                    // Image par défaut si pas d'image
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                    restaurantImage.setImage(defaultImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
                // Image par défaut en cas d'erreur
                try {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholder-restaurant.png"));
                    restaurantImage.setImage(defaultImage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            
            // Mettre à jour le spinner pour le nombre de personnes (max = capacité du restaurant)
            int maxPersonnes = Math.min(restaurant.getCapaciteTotale(), 50); // Limiter à 50 pour l'interface
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxPersonnes, 2);
            personnesSpinner.setValueFactory(valueFactory);
        }
    }
    
    /**
     * Gère la réservation du restaurant
     */
    @FXML
    void reserverRestaurant(ActionEvent event) {
        if (!validateFields()) {
            return;
        }
        
        try {
            // Récupérer l'email saisi
            String email = emailField.getText().trim();
            
            // Vérifier si l'utilisateur existe dans la base de données
            int idEtudiantValue = getUserIdByEmail(email);
            
            // Si l'utilisateur n'existe pas, créer un nouvel utilisateur
            if (idEtudiantValue == -1) {
                idEtudiantValue = createNewUser(nomClientField.getText().trim(), email);
                
                // Si la création a échoué, afficher une erreur et arrêter
                if (idEtudiantValue == -1) {
                    showAlert("Erreur", "Impossible de créer un compte utilisateur. Veuillez réessayer.", Alert.AlertType.ERROR);
                    return;
                }
            }
            
            // Vérifier que le restaurant n'est pas null
            if (restaurant == null) {
                showAlert("Erreur", "Aucun restaurant sélectionné. Veuillez retourner à la liste et sélectionner un restaurant.", Alert.AlertType.ERROR);
                return;
            }
            
            // Créer une nouvelle réservation avec les valeurs
            int idRestaurantValue = restaurant.getIdRestaurant();
            LocalDate dateReservationValue = LocalDate.now();
            int nombrePersonnesValue = personnesSpinner.getValue();
            
            // Créer l'objet ReservationRestaurant avec le constructeur approprié
            ReservationRestaurant reservation = new ReservationRestaurant(
                idRestaurantValue, 
                idEtudiantValue, 
                dateReservationValue, 
                nombrePersonnesValue
            );
            
            // Enregistrer la réservation
            serviceReservation.ajouter(reservation);
            
            // Envoyer un email de confirmation
            sendConfirmationEmail(email, restaurant.getNom(), dateReservationValue, nombrePersonnesValue);
            
            // Afficher un message de succès avec option pour voir les réservations
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Réservation confirmée");
            alert.setHeaderText(null);
            alert.setContentText(
                "Votre réservation chez " + restaurant.getNom() + " a été enregistrée avec succès.\n" +
                "Nombre de personnes: " + nombrePersonnesValue + "\n\n" +
                "Un email de confirmation a été envoyé à " + email
            );
            
            // Ajouter des boutons personnalisés
            ButtonType voirReservationsButton = new ButtonType("Voir mes réservations");
            ButtonType retourButton = new ButtonType("Retour à la liste");
            
            alert.getButtonTypes().setAll(voirReservationsButton, retourButton);
            
            // Créer une copie finale de l'ID de l'étudiant pour l'utiliser dans le lambda
            final int idEtudiantFinal = idEtudiantValue;
            
            // Gérer la fermeture de la boîte de dialogue (croix rouge)
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.setOnCloseRequest(windowEvent -> {
                // Retourner à la liste des restaurants quand on clique sur la croix
                retournerALaListe();
            });
            
            // Attendre la réponse de l'utilisateur
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == voirReservationsButton) {
                    // Naviguer vers la page des réservations
                    naviguerVersMesReservations(idEtudiantFinal);
                } else {
                    // Retourner à la liste des restaurants
                    retournerALaListe();
                }
            });
            
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'enregistrement de la réservation: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur inattendue est survenue: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Annule la réservation et retourne à la liste des restaurants
     */
    @FXML
    void annulerReservation(ActionEvent event) {
        retournerALaListe();
    }
    
    /**
     * Retourne à la liste des restaurants
     */
    private void retournerALaListe() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ListRestaurantClient.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) reserverButton.getScene().getWindow();
            
            // Transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            stage.setScene(scene);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la liste des restaurants: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Navigue vers la page "Mes Réservations" avec l'ID de l'étudiant
     * @param idEtudiant ID de l'étudiant
     */
    private void naviguerVersMesReservations(int idEtudiant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MesReservationsRestaurant.fxml"));
            Parent root = loader.load();
            // Récupérer le contrôleur et définir l'ID de l'utilisateur connecté
            MesReservationsRestaurantController controller = loader.getController();
            controller.setUserId(SessionManager.getInstance().getUserId());
            // Afficher la vue
            Scene scene = new Scene(root);
            Stage stage = (Stage) reserverButton.getScene().getWindow();
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            stage.setScene(scene);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la page des réservations: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Valide les champs du formulaire
     */
    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        
        // Vérifier si les champs sont null
        if (nomClientField == null) {
            errors.append("- Le champ nom n'est pas initialisé\n");
        } else if (nomClientField.getText().trim().isEmpty()) {
            errors.append("- Le nom est requis\n");
        }
        
        if (emailField == null) {
            errors.append("- Le champ email n'est pas initialisé\n");
        } else if (emailField.getText().trim().isEmpty()) {
            errors.append("- L'email est requis\n");
        } else if (!isValidEmail(emailField.getText().trim())) {
            errors.append("- Format d'email invalide\n");
        }
        
        if (personnesSpinner == null) {
            errors.append("- Le champ nombre de personnes n'est pas initialisé\n");
        }
        
        if (errors.length() > 0) {
            showAlert("Validation échouée", "Veuillez corriger les erreurs suivantes:\n" + errors.toString(), Alert.AlertType.ERROR);
            return false;
        }
        
        return true;
    }
    
    /**
     * Vérifie si l'email est valide
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }
    
    /**
     * Affiche une alerte
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Récupère l'ID d'un utilisateur à partir de son email
     * @param email Email de l'utilisateur
     * @return ID de l'utilisateur ou -1 si non trouvé
     */
    private int getUserIdByEmail(String email) {
        try {
            String query = "SELECT id FROM user WHERE email = ?";
            PreparedStatement ps = serviceReservation.getCon().prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Utilisateur non trouvé
    }
    
    /**
     * Crée un nouvel utilisateur dans la base de données
     * @param nom Nom de l'utilisateur
     * @param email Email de l'utilisateur
     * @return ID du nouvel utilisateur ou -1 en cas d'échec
     */
    private int createNewUser(String nom, String email) {
        try {
            // Générer un mot de passe temporaire (à remplacer par un système plus sécurisé)
            String tempPassword = "temp" + System.currentTimeMillis() % 10000;
            
            // Insérer le nouvel utilisateur
            String query = "INSERT INTO user (nom, email, password, role) VALUES (?, ?, ?, 'ETUDIANT')";
            PreparedStatement ps = serviceReservation.getCon().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, nom);
            ps.setString(2, email);
            ps.setString(3, tempPassword);
            ps.executeUpdate();
            
            // Récupérer l'ID généré
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Échec de la création
    }
    
    /**
     * Envoie un email de confirmation de réservation avec code QR
     * @param email Email du destinataire
     * @param nomRestaurant Nom du restaurant
     * @param dateReservation Date de la réservation
     * @param nombrePersonnes Nombre de personnes
     */
    private void sendConfirmationEmail(String email, String nomRestaurant, LocalDate dateReservation, int nombrePersonnes) {
        try {
            // Formater la date pour l'affichage
            String dateFormatted = dateReservation.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            
            // Générer le contenu HTML de l'email
            String htmlContent = generateReservationConfirmationEmail(nomRestaurant, dateFormatted, nombrePersonnes);
            
            // Générer le contenu du code QR
            String qrContent = generateQRContent(nomRestaurant, dateFormatted, nombrePersonnes);
            
            // Nom du fichier QR code
            String qrFileName = "reservation_restaurant_" + System.currentTimeMillis() + ".png";
            
            // Générer le QR code
            BufferedImage qrImage = QRCodeGeneratorF.generateQRCodeBufferedImage(qrContent, 300, 300);
            if (qrImage == null) {
                throw new Exception("QR code generation failed (image is null)");
            }
            
            // Convertir l'image en tableau de bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);
            byte[] imageBytes = baos.toByteArray();
            
            // Envoyer l'email avec le code QR en pièce jointe
            boolean sent = EmailSender.sendEmail(
                email, 
                "Confirmation de réservation - " + nomRestaurant, 
                htmlContent,
                qrContent,
                qrFileName
            );
            
            if (sent) {
                System.out.println("Email de confirmation avec code QR envoyé avec succès à " + email);
            } else {
                throw new Exception("Échec de l'envoi de l'email");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'envoi de l'email de confirmation. Veuillez vérifier votre adresse email.", Alert.AlertType.WARNING);
        }
    }
    
    /**
     * Génère le contenu HTML pour l'email de confirmation de réservation
     */
    private String generateReservationConfirmationEmail(String nomRestaurant, String dateReservation, int nombrePersonnes) {
        return "<!DOCTYPE html>" +
             "<html>" +
             "<head>" +
             "    <meta charset='UTF-8'>" +
             "    <style>" +
             "        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
             "        .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
             "        .header { background-color: #4CAF50; color: white; padding: 10px; text-align: center; }" +
             "        .content { padding: 20px; border: 1px solid #ddd; }" +
             "        .footer { text-align: center; margin-top: 20px; font-size: 12px; color: #777; }" +
             "        table { width: 100%; border-collapse: collapse; margin: 20px 0; }" +
             "        th, td { padding: 10px; text-align: left; border-bottom: 1px solid #ddd; }" +
             "        th { background-color: #f2f2f2; }" +
             "        .qr-code { text-align: center; margin: 20px 0; }" +
             "    </style>" +
             "</head>" +
             "<body>" +
             "    <div class='container'>" +
             "        <div class='header'>" +
             "            <h1>Confirmation de Réservation</h1>" +
             "        </div>" +
             "        <div class='content'>" +
             "            <p>Cher(e) client(e),</p>" +
             "            <p>Nous sommes heureux de vous confirmer que votre réservation au restaurant a été confirmée.</p>" +
             "            <h2>Détails de la réservation :</h2>" +
             "            <table>" +
             "                <tr><th>Restaurant</th><td>" + nomRestaurant + "</td></tr>" +
             "                <tr><th>Date</th><td>" + dateReservation + "</td></tr>" +
             "                <tr><th>Nombre de personnes</th><td>" + nombrePersonnes + "</td></tr>" +
             "            </table>" +
             "            <div class='qr-code' style='text-align: center; margin: 20px 0;'>\n" +
             "                <h3 style='color: #4CAF50;'>Votre code QR de réservation :</h3>\n" +
             "                <p><strong>Vous trouverez votre code QR de réservation en pièce jointe de cet email.</strong></p>\n" +
             "                <p><strong>Présentez ce code QR lors de votre arrivée au restaurant.</strong></p>\n" +
             "            </div>" +
             "            <p>Nous vous remercions pour votre réservation et sommes impatients de vous accueillir.</p>" +
             "            <p>Cordialement,<br>L'équipe du restaurant</p>" +
             "        </div>" +
             "        <div class='footer'>" +
             "            <p>Ceci est un email automatique, merci de ne pas y répondre.</p>" +
             "        </div>" +
             "    </div>" +
             "</body>" +
             "</html>";
    }
    
    /**
     * Génère le contenu du code QR pour une réservation de restaurant
     * @param nomRestaurant Nom du restaurant
     * @param dateReservation Date de la réservation formatée
     * @param nombrePersonnes Nombre de personnes
     * @return Contenu du code QR
     */
    private String generateQRContent(String nomRestaurant, String dateReservation, int nombrePersonnes) {
        return "Réservation Restaurant\n" +
               "Restaurant: " + nomRestaurant + "\n" +
               "Date: " + dateReservation + "\n" +
               "Nombre de personnes: " + nombrePersonnes + "\n" +
               "Réservation effectuée le: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
}
