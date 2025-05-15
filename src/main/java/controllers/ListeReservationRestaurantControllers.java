package controllers;

import Services.ServiceRestaurant;
import Services.ServiceReservationRestaurant;
import Services.ServiceUser;
import entities.Restaurant;
import entities.ReservationRestaurant;
import entities.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ListeReservationRestaurantControllers {

    @FXML private ListView<ReservationRestaurant> reservationListView;
    @FXML private TextField searchField;
    @FXML private Button btnRetour;
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
    
    private ServiceReservationRestaurant serviceReservation = new ServiceReservationRestaurant();
    private ServiceRestaurant serviceRestaurant = new ServiceRestaurant();
    private ServiceUser serviceUser = new ServiceUser();
    
    // Suppression de la carte de statuts
    
    // Observable list to hold all reservations
    private ObservableList<ReservationRestaurant> allReservations = FXCollections.observableArrayList();
    // Filtered list for dynamic filtering
    private FilteredList<ReservationRestaurant> filteredReservations;
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // Variables pour stocker temporairement l'ImageView et le conteneur d'image en cours de traitement
    private ImageView currentImageView;
    private VBox currentImageContainer;

    @FXML
    public void initialize() {
        setupListView();
        loadReservations();
        setupSearch();
        
        // Désactiver la sélection multiple
        reservationListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Ajouter un gestionnaire pour réagir aux changements de sélection
        reservationListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            reservationListView.refresh();
        });
    }
    
    private void setupListView() {
        // Style du ListView
        reservationListView.getStyleClass().add("reservation-list");
        reservationListView.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");
        
        // Désactiver l'effet de focus par défaut qui cause le flou
        reservationListView.setFocusTraversable(false);
        
        // Configure the ListView with a custom cell factory
        reservationListView.setCellFactory(param -> new ListCell<ReservationRestaurant>() {
            private final Label idLabel = new Label();
            private final Label restaurantLabel = new Label();
            private final Label etudiantLabel = new Label();
            private final Label dateReservationLabel = new Label();
            // Suppression des éléments de statut et des boutons
            private final VBox container = new VBox(8);
            private final HBox infoBox = new HBox(20);
            private final HBox actionBox = new HBox(10);
            private final ImageView restaurantImageView = new ImageView();
            private final VBox imageContainer = new VBox();
            
            {
                // Style the components
                container.setPadding(new Insets(12));
                container.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 5px;");
                
                // Style the labels and their containers
                idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                restaurantLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                etudiantLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                dateReservationLabel.setStyle("-fx-font-size: 13px;");
                
                // Suppression du style des boutons et du statut
                // Configuration de l'image du restaurant
                restaurantImageView.setFitWidth(120);
                restaurantImageView.setFitHeight(90);
                restaurantImageView.setPreserveRatio(true);
                
                // Création du conteneur pour l'image avec un fond blanc et des bords arrondis
                imageContainer.setStyle("-fx-background-color: white; -fx-padding: 5; -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");
                imageContainer.setPrefWidth(130);
                imageContainer.setPrefHeight(100);
                imageContainer.setAlignment(Pos.CENTER);
                imageContainer.getChildren().add(restaurantImageView);
                
                // Organize layout with better spacing and alignment
                VBox restaurantBox = new VBox(3, new Label("Restaurant:"), restaurantLabel);
                VBox etudiantBox = new VBox(3, new Label("Étudiant:"), etudiantLabel);
                VBox dateReservationBox = new VBox(3, new Label("Date Réservation:"), dateReservationLabel);
                
                // Style des titres des champs
                for (VBox box : new VBox[]{restaurantBox, etudiantBox, dateReservationBox}) {
                    ((Label)box.getChildren().get(0)).setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");
                }
                
                // Ajouter les informations au conteneur infoBox
                infoBox.getChildren().addAll(restaurantBox, etudiantBox, dateReservationBox);
                infoBox.setAlignment(Pos.CENTER_LEFT);
                
                // Créer un HBox pour l'image et les informations
                HBox contentBox = new HBox(15);
                contentBox.setAlignment(Pos.CENTER_LEFT);
                contentBox.getChildren().addAll(imageContainer, infoBox);
                
                container.getChildren().add(contentBox);
                
                // Ajouter un espace entre les éléments de la liste
                setStyle("-fx-padding: 5 0 5 0;");
            }
            
            @Override
            protected void updateItem(ReservationRestaurant reservation, boolean empty) {
                super.updateItem(reservation, empty);
                
                if (empty || reservation == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    try {
                        // Set restaurant name and image
                        Restaurant restaurant = serviceRestaurant.recuperer().stream()
                            .filter(r -> r.getIdRestaurant() == reservation.getIdRestaurant())
                            .findFirst()
                            .orElse(null);
                        restaurantLabel.setText(restaurant != null ? restaurant.getNom() : "Restaurant #" + reservation.getIdRestaurant());
                        
                        // Charger l'image du restaurant
                        if (restaurant != null && restaurant.getImage() != null) {
                            // Stocker les références aux composants actuels avant de charger l'image
                            currentImageView = restaurantImageView;
                            currentImageContainer = imageContainer;
                            loadRestaurantImage(restaurant);
                        } else {
                            // Stocker les références aux composants actuels avant de charger l'image par défaut
                            currentImageView = restaurantImageView;
                            currentImageContainer = imageContainer;
                            loadDefaultImage();
                        }
                        
                        // Set student info with full name
                        try {
                            User etudiant = serviceUser.getUserById(reservation.getIdEtudiant());
                            if (etudiant != null) {
                                etudiantLabel.setText(etudiant.getPrenom() + " " + etudiant.getNom());
                            } else {
                                etudiantLabel.setText("Nom non disponible");
                            }
                        } catch (SQLException e) {
                            System.err.println("Erreur lors de la récupération de l'étudiant: " + e.getMessage());
                            etudiantLabel.setText("Nom non disponible");
                        }
                        
                        // Set date
                        dateReservationLabel.setText(reservation.getDateReservation().format(dateFormatter));
                        
                        setGraphic(container);
                        setText(null);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        setGraphic(null);
                        setText("Erreur de chargement");
                    }
                }
            }
        });
    }
    
    private void loadReservations() {
        try {
            // Effacer les données précédentes
            allReservations.clear();
            
            // Charger les réservations depuis la base de données
            allReservations.addAll(serviceReservation.recuperer());
            
            // Créer la liste filtrée
            filteredReservations = new FilteredList<>(allReservations, p -> true);
            reservationListView.setItems(filteredReservations);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des réservations: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredReservations.setPredicate(reservation -> {
                // Si le champ de recherche est vide, afficher toutes les réservations
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                try {
                    // Recherche par ID
                    if (String.valueOf(reservation.getIdReservation()).contains(lowerCaseFilter)) {
                        return true;
                    }

                    // Recherche par nom de restaurant
                    Restaurant restaurant = serviceRestaurant.recuperer().stream()
                            .filter(r -> r.getIdRestaurant() == reservation.getIdRestaurant())
                            .findFirst()
                            .orElse(null);
                    if (restaurant != null && restaurant.getNom().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }

                    // Recherche par ID étudiant
                    if (String.valueOf(reservation.getIdEtudiant()).contains(lowerCaseFilter)) {
                        return true;
                    }

                    // Recherche par date
                    if (reservation.getDateReservation().format(dateFormatter).contains(lowerCaseFilter)) {
                        return true;
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
                        return false;
                    }
            );
            
        });
    }
    
    @FXML
    private void showAllReservations() {
        filteredReservations.setPredicate(p -> true);
    }
    
    // Méthodes de filtrage supprimées
    
    @FXML
    private void retourAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterRestaurant.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Configurer la fenêtre en plein écran
            stage.setMaximized(true);
            
            // Ajouter une transition de fondu pour une navigation plus fluide
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
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
    
    /**
     * Charge l'image du restaurant dans l'ImageView
     * @param restaurant Le restaurant dont on veut charger l'image
     */
    private void loadRestaurantImage(Restaurant restaurant) {
        boolean imageLoaded = false;
        if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
            try {
                String imagePath = "src/main/resources/" + restaurant.getImage();
                java.io.File file = new java.io.File(imagePath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    currentImageView.setImage(image);
                    imageLoaded = true;
                }
            } catch (Exception e) {
                System.out.println("Erreur lors du chargement de l'image: " + e.getMessage());
            }
        }
        
        // Si l'image n'a pas été chargée, utiliser une image par défaut
        if (!imageLoaded) {
            loadDefaultImage();
        }
    }
    
    /**
     * Charge une image par défaut dans l'ImageView
     */
    private void loadDefaultImage() {
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/images/placeholder-restaurant.png");
            if (is == null) {
                is = getClass().getResourceAsStream("/placeholder/placeholder.png");
            }
            if (is == null) {
                is = getClass().getResourceAsStream("/images/restaurant-default.png");
            }
            
            if (is != null) {
                Image defaultImage = new Image(is);
                currentImageView.setImage(defaultImage);
            } else {
                // Si aucune image par défaut n'est trouvée, utiliser une couleur de fond
                currentImageView.setImage(null);
                currentImageContainer.setStyle(currentImageContainer.getStyle() + "; -fx-background-color: #e0e0e0;");
            }
        } catch (Exception e) {
            System.out.println("Impossible de charger l'image par défaut: " + e.getMessage());
        }
    }

    /**
     * Navigation vers la page d'accueil
     */
    @FXML
    private void navigateToAccueil() {
        navigateTo("/AcceuilAdmin.fxml", "Accueil Admin");
    }
    
    /**
     * Navigation vers la page Admin
     */
    @FXML
    private void navigateToAdmin() {
        navigateTo("/AdminUser.fxml", "Gestion des Utilisateurs");
    }
    
    /**
     * Navigation vers la page Dossier
     */
    @FXML
    private void navigateToDossier() {
        navigateTo("/AdminDossier.fxml", "Gestion des Dossiers");
    }
    
    /**
     * Navigation vers la page Université
     */
    @FXML
    private void navigateToUniversite() {
        navigateTo("/adminuniversite.fxml", "Gestion des Universités");
    }
    
    /**
     * Navigation vers la page Entretien
     */
    @FXML
    private void navigateToEntretien() {
        navigateTo("/Gestionnaire.fxml", "Gestion des Entretiens");
    }
    
    /**
     * Navigation vers la page Événement
     */
    @FXML
    private void navigateToEvenement() {
        navigateTo("/gestion_evenement.fxml", "Gestion des Événements");
    }
    
    /**
     * Navigation vers la page Hébergement
     */
    @FXML
    private void navigateToHebergement() {
        navigateTo("/ListFoyer.fxml", "Liste des Foyers");
    }
    
    /**
     * Navigation vers la page Restaurant
     */
    @FXML
    private void navigateToRestaurant() {
        navigateTo("/ListRestaurant.fxml", "Liste des Restaurants");
    }
    
    /**
     * Navigation vers la page Vols
     */
    @FXML
    private void navigateToVols() {
        // À implémenter si besoin
    }
    
    /**
     * Méthode de déconnexion
     */
    @FXML
    private void logout() {
        navigateTo("/login-view.fxml", "Login - GradAway");
    }
    
    /**
     * Méthode générique pour la navigation
     */
    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Stage stage = (Stage) reservationListView.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            
            // Configurer la fenêtre en plein écran
            stage.setMaximized(true);
            
            // Ajouter une transition de fondu pour une navigation plus fluide
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
