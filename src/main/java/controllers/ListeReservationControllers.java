package controllers;

import Services.ServiceFoyer;
import Services.ServiceReservationFoyer;
import Services.ServiceUser;
import entities.Foyer;
import entities.ReservationFoyer;
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
import javafx.scene.Node;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ListeReservationControllers {

    @FXML private ListView<ReservationFoyer> reservationListView;
    @FXML private TextField searchField;
    @FXML private Button btnRetour;
    
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
    
    private ServiceReservationFoyer serviceReservation = new ServiceReservationFoyer();
    private ServiceFoyer serviceFoyer = new ServiceFoyer();
    private ServiceUser serviceUser = new ServiceUser();
    
    // Suppression de la carte de statuts
    
    // Observable list to hold all reservations
    private ObservableList<ReservationFoyer> allReservations = FXCollections.observableArrayList();
    // Filtered list for dynamic filtering
    private FilteredList<ReservationFoyer> filteredReservations;
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        try {
            // Setup dashboard navigation
            setupNavigationButtons();
            
            setupListView();
            loadReservations();
            setupSearch();
            
            // Désactiver la sélection multiple
            reservationListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            
            // Ajouter un gestionnaire pour réagir aux changements de sélection
            reservationListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                reservationListView.refresh();
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'initialisation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void setupListView() {
        // Style du ListView
        reservationListView.getStyleClass().add("reservation-list");
        reservationListView.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1px;");
        
        // Désactiver l'effet de focus par défaut qui cause le flou
        reservationListView.setFocusTraversable(false);
        
        // Configure the ListView with a custom cell factory
        reservationListView.setCellFactory(param -> new ListCell<ReservationFoyer>() {
            private final Label idLabel = new Label();
            private final Label foyerLabel = new Label();
            private final Label etudiantLabel = new Label();
            private final Label dateDebutLabel = new Label();
            private final Label dateFinLabel = new Label();
            private final Label dateReservationLabel = new Label();
            // Suppression des éléments de statut et des boutons
            private final VBox container = new VBox(8);
            private final HBox infoBox = new HBox(20);
            private final HBox actionBox = new HBox(10);
            private final ImageView foyerImageView = new ImageView();
            private final VBox imageContainer = new VBox();
            
            {
                // Style the components
                container.setPadding(new Insets(12));
                container.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 5px;");
                
                // Style the labels and their containers
                idLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                foyerLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                etudiantLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                dateDebutLabel.setStyle("-fx-font-size: 13px;");
                dateFinLabel.setStyle("-fx-font-size: 13px;");
                dateReservationLabel.setStyle("-fx-font-size: 13px;");
                
                // Configure image view for foyer
                foyerImageView.setFitWidth(120);
                foyerImageView.setFitHeight(90);
                foyerImageView.setPreserveRatio(true);
                
                // Style image container
                imageContainer.setStyle("-fx-background-color: white; -fx-background-radius: 5; -fx-padding: 5;");
                imageContainer.setPrefWidth(130);
                imageContainer.setPrefHeight(100);
                imageContainer.setAlignment(Pos.CENTER);
                imageContainer.getChildren().add(foyerImageView);
                
                // Suppression du style et des actions des boutons et du statut
                
                // Create labels for headers
                Label foyerHeaderLabel = new Label("Foyer:");
                Label etudiantHeaderLabel = new Label("Étudiant:");
                Label dateDebutHeaderLabel = new Label("Date Début:");
                Label dateFinHeaderLabel = new Label("Date Fin:");
                Label dateReservationHeaderLabel = new Label("Date Réservation:");
                
                // Style des titres des champs
                for (Label headerLabel : new Label[]{foyerHeaderLabel, etudiantHeaderLabel, dateDebutHeaderLabel, dateFinHeaderLabel, dateReservationHeaderLabel}) {
                    headerLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");
                }
                
                // Organize layout with all information on the same line
                HBox infoLineBox = new HBox(20);
                
                // Create individual info boxes
                VBox foyerBox = new VBox(3, foyerHeaderLabel, foyerLabel);
                VBox etudiantBox = new VBox(3, etudiantHeaderLabel, etudiantLabel);
                VBox dateDebutBox = new VBox(3, dateDebutHeaderLabel, dateDebutLabel);
                VBox dateFinBox = new VBox(3, dateFinHeaderLabel, dateFinLabel);
                VBox dateReservationBox = new VBox(3, dateReservationHeaderLabel, dateReservationLabel);
                
                // Add all info boxes to the same horizontal line
                infoLineBox.getChildren().addAll(foyerBox, etudiantBox, dateDebutBox, dateFinBox, dateReservationBox);
                infoLineBox.setAlignment(Pos.CENTER_LEFT);
                
                // Create a container for the text information
                VBox textInfoBox = new VBox(10);
                textInfoBox.getChildren().add(infoLineBox);
                textInfoBox.setAlignment(Pos.CENTER_LEFT);
                
                // Ajouter l'image et les informations textuelles dans le conteneur principal
                infoBox.getChildren().addAll(imageContainer, textInfoBox);
                infoBox.setAlignment(Pos.CENTER_LEFT);
                infoBox.setSpacing(15);
                
                // Suppression de l'ajout des boutons d'action
                
                container.getChildren().add(infoBox);
                
                // Ajouter un espace entre les éléments de la liste
                setStyle("-fx-padding: 5 0 5 0;");
            }
            
            @Override
            protected void updateItem(ReservationFoyer reservation, boolean empty) {
                super.updateItem(reservation, empty);
                
                if (empty || reservation == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    try {
                        // Set foyer name and image
                        Foyer foyer = serviceFoyer.getFoyerById(reservation.getFoyerId());
                        if (foyer != null) {
                            foyerLabel.setText(foyer.getNom());
                            
                            // Charger l'image du foyer
                            boolean imageLoaded = false;
                            if (foyer.getImage() != null && !foyer.getImage().isEmpty()) {
                                try {
                                    // Vérifier si l'image est une URL complète ou un chemin relatif
                                    String imagePath = foyer.getImage();
                                    if (!imagePath.startsWith("http") && !imagePath.startsWith("file:")) {
                                        // Essayer de charger depuis les ressources
                                        java.io.File file = new java.io.File("src/main/resources/" + imagePath);
                                        if (file.exists()) {
                                            Image image = new Image(file.toURI().toString());
                                            foyerImageView.setImage(image);
                                            imageLoaded = true;
                                        }
                                    } else {
                                        // C'est une URL ou un chemin absolu
                                        Image image = new Image(imagePath);
                                        foyerImageView.setImage(image);
                                        imageLoaded = true;
                                    }
                                } catch (Exception e) {
                                    System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
                                }
                            }
                            
                            if (!imageLoaded) {
                                // Image par défaut si aucune image n'est disponible ou si le chargement échoue
                                try {
                                    // Essayer plusieurs chemins pour l'image par défaut
                                    java.io.InputStream is = getClass().getResourceAsStream("/images/default_foyer.jpg");
                                    if (is == null) {
                                        is = getClass().getResourceAsStream("/images/placeholder-foyer.png");
                                    }
                                    if (is == null) {
                                        is = getClass().getResourceAsStream("/images/foyer-default.png");
                                    }
                                    if (is == null) {
                                        is = getClass().getResourceAsStream("/placeholder/placeholder.png");
                                    }
                                    
                                    if (is != null) {
                                        Image defaultImage = new Image(is);
                                        foyerImageView.setImage(defaultImage);
                                    }
                                } catch (Exception e) {
                                    System.err.println("Erreur lors du chargement de l'image par défaut: " + e.getMessage());
                                }
                            }
                        } else {
                            foyerLabel.setText("Foyer #" + reservation.getFoyerId());
                            // Charger une image par défaut pour les foyers non trouvés
                            try {
                                java.io.InputStream is = getClass().getResourceAsStream("/images/default_foyer.jpg");
                                if (is != null) {
                                    Image defaultImage = new Image(is);
                                    foyerImageView.setImage(defaultImage);
                                }
                            } catch (Exception e) {
                                System.err.println("Erreur lors du chargement de l'image par défaut: " + e.getMessage());
                            }
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
                        
                        // Set dates
                        dateDebutLabel.setText(reservation.getDateDebut().format(dateFormatter));
                        dateFinLabel.setText(reservation.getDateFin().format(dateFormatter));
                        dateReservationLabel.setText(reservation.getDateReservation().format(dateFormatter));
                        
                        // Suppression de l'affichage du statut et des boutons d'action
                        
                        // Appliquer un style différent si l'élément est sélectionné
                        if (isSelected()) {
                            container.setStyle("-fx-background-color: #f0f8ff; -fx-border-color: #2196F3; -fx-border-width: 1px; -fx-border-radius: 5px;");
                        } else {
                            container.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 1px; -fx-border-radius: 5px;");
                        }
                        
                        // Désactiver l'effet de focus standard
                        setStyle("-fx-background-color: transparent; -fx-padding: 5 0 5 0;");
                        setText(null);
                        setGraphic(container);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        setGraphic(null);
                        setText(null);
                    }
                }
            }
        });
    }
    
    private void loadReservations() {
        try {
            // Clear previous data
            allReservations.clear();
            
            // Load reservations from database
            allReservations.addAll(serviceReservation.recuperer());
            
            // Suppression de l'initialisation des statuts
            
            // Create filtered list
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
                // If search field is empty, show all reservations
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                try {
                    // Search by ID
                    if (String.valueOf(reservation.getIdReservation()).contains(lowerCaseFilter)) {
                        return true;
                    }
                    
                    // Search by foyer name
                    Foyer foyer = serviceFoyer.getFoyerById(reservation.getFoyerId());
                    if (foyer != null && foyer.getNom().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    
                    // Search by student ID
                    if (String.valueOf(reservation.getIdEtudiant()).contains(lowerCaseFilter)) {
                        return true;
                    }
                    
                    // Search by dates
                    if (reservation.getDateDebut().format(dateFormatter).contains(lowerCaseFilter) ||
                        reservation.getDateFin().format(dateFormatter).contains(lowerCaseFilter) ||
                        reservation.getDateReservation().format(dateFormatter).contains(lowerCaseFilter)) {
                        return true;
                    }
                    
                    return false; // Suppression de la recherche par statut
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            });
        });
    }
    
    @FXML
    private void showAllReservations() {
        filteredReservations.setPredicate(p -> true);
        reservationListView.refresh();
    }
    
    @FXML
    // Méthodes de gestion des réservations supprimées

    private void retourAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFoyer.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Configurer la fenêtre en plein écran
            stage.setMaximized(true);
            
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
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupNavigationButtons() {
        accueilButton.setOnAction(this::onAccueilButtonClick);
        userButton.setOnAction(this::onUserButtonClick);
        dossierButton.setOnAction(this::ondossierButtonClick);
        universiteButton.setOnAction(this::onuniversiteButtonClick);
        entretienButton.setOnAction(this::onentretienButtonClick);
        evenementButton.setOnAction(this::onevenementButtonClick);
        hebergementButton.setOnAction(this::onhebergementButtonClick);
        restaurantButton.setOnAction(this::onrestaurantButtonClick);
        volsButton.setOnAction(this::onvolsButtonClick);
        logoutButton.setOnAction(this::onlogoutButtonClick);
    }

    private void navigateToScene(String fxmlPath, ActionEvent event, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            // Add fade transition
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onAccueilButtonClick(ActionEvent event) {
        navigateToScene("/AcceuilAdmin.fxml", event, "Accueil Admin");
    }

    @FXML
    private void onUserButtonClick(ActionEvent event) {
        navigateToScene("/AdminUser.fxml", event, "Gestion des Utilisateurs");
    }

    @FXML
    private void ondossierButtonClick(ActionEvent event) {
        navigateToScene("/AdminDossier.fxml", event, "Gestion des Dossiers");
    }

    @FXML
    private void onuniversiteButtonClick(ActionEvent event) {
        navigateToScene("/adminuniversite.fxml", event, "Gestion des Universités");
    }

    @FXML
    private void onentretienButtonClick(ActionEvent event) {
        navigateToScene("/Gestionnaire.fxml", event, "Gestion des Entretiens");
    }

    @FXML
    private void onevenementButtonClick(ActionEvent event) {
        navigateToScene("/gestion_evenement.fxml", event, "Gestion des Événements");
    }

    @FXML
    private void onhebergementButtonClick(ActionEvent event) {
        navigateToScene("/AjouterFoyer.fxml", event, "Gestion des Foyers");
    }

    @FXML
    private void onrestaurantButtonClick(ActionEvent event) {
        navigateToScene("/AjouterRestaurant.fxml", event, "Gestion des Restaurants");
    }

    @FXML
    private void onvolsButtonClick(ActionEvent event) {
        // À implémenter si besoin
    }

    @FXML
    private void onlogoutButtonClick(ActionEvent event) {
        navigateToScene("/login-view.fxml", event, "Login - GradAway");
    }
}
