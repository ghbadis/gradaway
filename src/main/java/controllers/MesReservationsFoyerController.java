package controllers;

import Services.ServiceReservationFoyer;
import Services.ServiceFoyer;
import entities.ReservationFoyer;
import entities.Foyer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MesReservationsFoyerController {

    @FXML private TilePane reservationsContainer;
    
    // Navigation buttons
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
    
    private ServiceReservationFoyer serviceReservation;
    private ServiceFoyer serviceFoyer;
    private int userId;
    
    @FXML
    public void initialize() {
        serviceReservation = new ServiceReservationFoyer();
        serviceFoyer = new ServiceFoyer();
        // userId = 2; // SUPPRIMÉ : ne pas initialiser ici
        // loadReservations(); // SUPPRIMÉ : ne pas charger ici
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
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Restaurants");
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
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
           // showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue.");
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

    private void navigateToScene(Parent root, ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        // Add fade transition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    
    /**
     * Définit l'ID de l'utilisateur et charge ses réservations
     * @param userId ID de l'utilisateur
     */
    public void setUserId(int userId) {
        this.userId = userId;
        
        // Recharger les réservations avec le nouvel ID
        reservationsContainer.getChildren().clear();
        loadReservations();
    }
    
    /**
     * Charge toutes les réservations de foyer
     */
    private void loadReservations() {
        try {
            System.out.println("Chargement des réservations de foyer pour l'utilisateur ID: " + userId);
            
            // Récupérer les réservations filtrées par ID d'utilisateur
            List<ReservationFoyer> reservations = serviceReservation.getReservationsByEtudiantId(userId);
            
            System.out.println("Nombre de réservations trouvées pour l'utilisateur: " + reservations.size());
            
            if (reservations.isEmpty()) {
                System.out.println("Aucune réservation trouvée, affichage du message 'pas de réservations'");
                showNoReservationsMessage();
                return;
            }
            
            // Configurer le TilePane pour afficher exactement 3 foyers par ligne
            reservationsContainer.setPrefColumns(3); // Force 3 colonnes
            reservationsContainer.setMinWidth(900); // Largeur minimale pour contenir 3 cartes et leurs espacements
            reservationsContainer.setPrefTileWidth(270); // Largeur légèrement réduite pour s'assurer que 3 cartes tiennent
            reservationsContainer.setPrefTileHeight(380);//uteur ajustée
            reservationsContainer.setHgap(15); // Espacement horizontal réduit
            reservationsContainer.setVgap(30);//ecement vertical réduit
            reservationsContainer.setAlignment(Pos.CENTER);
            reservationsContainer.setTileAlignment(Pos.CENTER);
            reservationsContainer.setPadding(new Insets(20, 10, 20, 10)); // Padding réduit
            
            // Calculer le nombre de lignes nécessaires
            int numRows = (int) Math.ceil(reservations.size() / 3.0);
            reservationsContainer.setPrefRows(numRows);
            // Ajuster la hauteur totale en tenant compte du grand espacement vertical
            reservationsContainer.setPrefHeight(numRows * (400 + 50) + 60);
            
            // Afficher chaque réservation
            for (ReservationFoyer reservation : reservations) {
                try {
                    // Récupérer le foyer associé à la réservation
                    Foyer foyer = serviceFoyer.getFoyerById(reservation.getFoyerId());
                    
                    // Créer la carte de réservation
                    VBox reservationCard = createReservationCard(reservation, foyer);
                    reservationsContainer.getChildren().add(reservationCard);
                } catch (SQLException e) {
                    System.out.println("Erreur lors de la récupération du foyer: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des réservations: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Affiche un message quand il n'y a pas de réservations
     */
    private void showNoReservationsMessage() {
        Label noReservationsLabel = new Label("Vous n'avez pas encore de réservations.");
        noReservationsLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #757575;");
        reservationsContainer.getChildren().add(noReservationsLabel);
    }
    
    /**
     * Crée une carte pour afficher une réservation
     */
    private VBox createReservationCard(ReservationFoyer reservation, Foyer foyer) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8px; "
                + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3); -fx-border-color: #e0e0e0; -fx-border-radius: 8px;");
        card.setPrefHeight(380); // Hauteur ajustée pour s'adapter au TilePane
        card.setMaxHeight(380); // Hauteur maximale réduite
        card.setMinHeight(380); // Hauteur minimale réduite
        card.setPrefWidth(270); // Largeur ajustée pour permettre 3 cartes par ligne
        card.setMaxWidth(270); // Largeur maximale ajustée
        card.setSpacing(8); // Réduction de l'espacement entre les éléments pour gagner de la place
        card.setPadding(new Insets(15, 15, 15, 15)); // Padding uniforme
        card.setAlignment(Pos.TOP_CENTER);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(140);
        imageView.setFitWidth(250);
        imageView.setPreserveRatio(true);

        try {
            Image image;
            String imageName = foyer.getImage();
            if (imageName != null && imageName.contains("/")) {
                imageName = imageName.substring(imageName.lastIndexOf('/') + 1);
            }
            if (imageName != null && !imageName.isEmpty()) {
                String imagePath = "/images/" + imageName;
                System.out.println("Tentative de chargement de l'image: " + imagePath);
                InputStream imageStream = getClass().getResourceAsStream(imagePath);
                if (imageStream != null) {
                    image = new Image(imageStream);
                    System.out.println("Image chargée avec succès: " + imagePath);
                } else {
                    System.out.println("Image introuvable dans le chemin: " + imagePath);
                    System.out.println("Tentative de chargement de l'image placeholder");
                    // Try multiple fallback options
                    imageStream = getClass().getResourceAsStream("/images/hotel.png");
                    if (imageStream != null) {
                        image = new Image(imageStream);
                    } else {
                        // If all else fails, set a default background
                        imageView.setStyle("-fx-background-color: #e0e0e0;");
                        return card;
                    }
                }
            } else {
                System.out.println("Aucune image spécifiée pour le foyer, utilisation du placeholder");
                InputStream imageStream = getClass().getResourceAsStream("/images/hotel.png");
                if (imageStream != null) {
                    image = new Image(imageStream);
                } else {
                    imageView.setStyle("-fx-background-color: #e0e0e0;");
                    return card;
                }
            }
            imageView.setImage(image);
        } catch (Exception e) {
            System.out.println("Erreur image: " + e.getMessage());
            imageView.setStyle("-fx-background-color: #e0e0e0;");
        }

        // Afficher le nom du foyer clairement et en plus grand
        Label nomLabel = new Label(foyer.getNom());
        nomLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1a237e;");
        nomLabel.setWrapText(true);
        nomLabel.setAlignment(Pos.CENTER);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        Label dateLabel = new Label("Du " + reservation.getDateDebut().format(formatter)
                + " au " + reservation.getDateFin().format(formatter));
        dateLabel.setStyle("-fx-font-size: 14px;");

        // Afficher la localisation (ville et pays) avec une icône
        Label localisationLabel = new Label("📍 " + foyer.getVille() + ", " + foyer.getPays());
        localisationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2196F3; -fx-font-weight: bold;");
        localisationLabel.setWrapText(true);
        localisationLabel.setAlignment(Pos.CENTER);
        
        // Afficher l'adresse complète du foyer avec une icône
        Label adresseLabel = new Label("🏠 " + foyer.getAdresse());
        adresseLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575;");
        adresseLabel.setWrapText(true);
        adresseLabel.setAlignment(Pos.CENTER);
        
        // Afficher la capacité du foyer avec une icône
        Label capaciteLabel = new Label("👥 Capacité: " + foyer.getCapacite() + " personnes");
        capaciteLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;");
        capaciteLabel.setWrapText(true);
        capaciteLabel.setAlignment(Pos.CENTER);

        Button cancelButton = new Button("Annuler la réservation");
        cancelButton.setStyle("-fx-background-color: #1565C0; -fx-text-fill: white; -fx-background-radius: 4px; -fx-font-weight: bold;");
        cancelButton.setPrefWidth(240);
        cancelButton.setMaxWidth(240);
        cancelButton.setPrefHeight(35);
        cancelButton.setOnAction(e -> cancelReservation(reservation));
        
        // Ajouter un padding en bas pour s'assurer que le bouton reste à l'intérieur de la carte
        VBox.setMargin(cancelButton, new Insets(0, 0, 10, 0));

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        card.getChildren().addAll(imageView, nomLabel, localisationLabel, adresseLabel, capaciteLabel, dateLabel, spacer, cancelButton);

        card.setOnMouseEntered(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(100), card);
            ft.setFromValue(1.0);
            ft.setToValue(0.9);
            ft.play();
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8px; "
                    + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 5);");
        });

        card.setOnMouseExited(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(100), card);
            ft.setFromValue(0.9);
            ft.setToValue(1.0);
            ft.play();
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8px; "
                    + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        });

        return card;
    }

    /**
     * Annule une réservation
     */
    private void cancelReservation(ReservationFoyer reservation) {
        try {
            // Supprimer la réservation
            serviceReservation.supprimer(reservation);
            
            // Recharger les réservations
            reservationsContainer.getChildren().clear();
            loadReservations();
            
            showAlert("Succès", "Votre réservation a été annulée avec succès.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'annulation de la réservation: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
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

    @FXML
    private void retourListeFoyer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) reservationsContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Add fade transition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
