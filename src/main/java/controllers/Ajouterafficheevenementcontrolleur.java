package controllers;

import entities.Evenement;
import entities.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Services.ServiceEvenement;
import Services.ServiceUser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;

public class Ajouterafficheevenementcontrolleur {
    @FXML
    private TextField chercher_txtf;
    @FXML
    private Button chercher_button;
    @FXML
    private ComboBox<String> domaine_comb;
    @FXML
    private GridPane affiche_even_grid;
    @FXML
    private Button liste_reservation_button;

    private final ServiceEvenement serviceEvenement = new ServiceEvenement();
    private final ServiceUser serviceUser = new ServiceUser();
    private int currentUserId; // Pour stocker l'ID de l'utilisateur connecté
    private List<Evenement> evenements;
//
    @FXML
    public void initialize() {
        chargerEvenements();
        initialiserComboBox();
        setupListeners();
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    private void chargerEvenements() {
        try {
            evenements = serviceEvenement.recuperer();
            afficherEvenements(evenements);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des événements", e.getMessage());
        }
    }

    private void initialiserComboBox() {
        try {
            List<String> domaines = evenements.stream()
                    .map(Evenement::getDomaine)
                    .distinct()
                    .collect(Collectors.toList());
            domaine_comb.getItems().addAll(domaines);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'initialisation des domaines", e.getMessage());
        }
    }

    private void setupListeners() {
        domaine_comb.setOnAction(event -> filtrerParDomaine());
        chercher_button.setOnAction(event -> chercherEvenements());
        liste_reservation_button.setOnAction(event -> ouvrirListeReservations());
    }

    private void filtrerParDomaine() {
        String domaineSelectionne = domaine_comb.getValue();
        if (domaineSelectionne != null && !domaineSelectionne.isEmpty()) {
            List<Evenement> evenementsFiltres = evenements.stream()
                    .filter(e -> e.getDomaine().equals(domaineSelectionne))
                    .collect(Collectors.toList());
            afficherEvenements(evenementsFiltres);
        } else {
            afficherEvenements(evenements);
        }
    }

    private void chercherEvenements() {
        String recherche = chercher_txtf.getText().toLowerCase();
        if (!recherche.isEmpty()) {
            List<Evenement> evenementsFiltres = evenements.stream()
                    .filter(e -> e.getNom().toLowerCase().contains(recherche) ||
                            e.getDescription().toLowerCase().contains(recherche))
                    .collect(Collectors.toList());
            afficherEvenements(evenementsFiltres);
        } else {
            afficherEvenements(evenements);
        }
    }

    private void afficherEvenements(List<Evenement> evenements) {
        affiche_even_grid.getChildren().clear();
        int colonne = 0;
        int ligne = 0;
        int maxColonnes = 3;

        for (Evenement evenement : evenements) {
            VBox eventBox = creerEventBox(evenement);
            affiche_even_grid.add(eventBox, colonne, ligne);

            colonne++;
            if (colonne >= maxColonnes) {
                colonne = 0;
                ligne++;
            }
        }
    }

    private VBox creerEventBox(Evenement evenement) {
        VBox eventBox = new VBox(10);
        eventBox.getStyleClass().addAll("white-bg", "shadow");
        eventBox.setPrefWidth(250);
        eventBox.setPrefHeight(300);

        // Image de l'événement
        ImageView imageView = new ImageView();
        try {
            if (evenement.getImage() != null && !evenement.getImage().isEmpty()) {
                Image image = new Image(evenement.getImage());
                imageView.setImage(image);
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default_event.png")));
            }
        } catch (Exception e) {
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default_event.png")));
            } catch (Exception ex) {
                imageView = new ImageView();
            }
        }
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Informations de l'événement
        Label nomLabel = new Label(evenement.getNom());
        nomLabel.getStyleClass().add("event-title");

        Label dateLabel = new Label("Date: " + evenement.getDate());
        Label lieuLabel = new Label("Lieu: " + evenement.getLieu());
        Label placesLabel = new Label("Places disponibles: " + evenement.getPlaces_disponibles());

        // Boutons
        HBox buttonBox = new HBox(10);
        Button reserverButton = new Button("Réserver");
        Button detailButton = new Button("Détail");

        reserverButton.getStyleClass().add("update-btn");
        detailButton.getStyleClass().add("clear-btn");

        reserverButton.setOnAction(event -> reserverEvenement(evenement));
        detailButton.setOnAction(event -> afficherDetails(evenement));

        buttonBox.getChildren().addAll(reserverButton, detailButton);

        eventBox.getChildren().addAll(imageView, nomLabel, dateLabel, lieuLabel, placesLabel, buttonBox);
        return eventBox;
    }

    private void reserverEvenement(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reservation_evenement.fxml"));
            Parent root = loader.load();

            // Récupérer l'utilisateur connecté
            User currentUser = serviceUser.getUserById(currentUserId);

            // Passer l'événement et l'utilisateur au contrôleur de réservation
            ReservationEvenementController controller = loader.getController();
            controller.setEvenement(evenement);
            controller.setCurrentUser(currentUser);

            // Get the current stage and update its scene
            Stage stage = (Stage) affiche_even_grid.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Réservation - " + evenement.getNom());
            stage.centerOnScreen();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre de réservation", e.getMessage());
        }
    }

    private void afficherDetails(Evenement evenement) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de l'événement");
        alert.setHeaderText(evenement.getNom());
        alert.setContentText(
                "Description: " + evenement.getDescription() + "\n" +
                        "Date: " + evenement.getDate() + "\n" +
                        "Lieu: " + evenement.getLieu() + "\n" +
                        "Domaine: " + evenement.getDomaine() + "\n" +
                        "Places disponibles: " + evenement.getPlaces_disponibles()
        );
        alert.showAndWait();
    }

    private void ouvrirListeReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/liste_reservation.fxml"));
            Parent root = loader.load();
            
            // Passer l'ID de l'utilisateur au contrôleur de la liste des réservations
            ListeReservationController controller = loader.getController();
            controller.setCurrentUserId(currentUserId);
            
            // Get the current stage and update its scene
            Stage stage = (Stage) liste_reservation_button.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste de mes réservations");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la liste des réservations", e.getMessage());
        }
    }

    @FXML
    public void acceuilbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
            Parent root = loader.load();

            Acceuilcontroller controller = loader.getController();
            controller.setUserId(currentUserId);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil - GradAway");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la page d'accueil", e.getMessage());
        }
    }

    @FXML
    public void userbutton(ActionEvent actionEvent) {
        if (currentUserId <= 0) {
            showAlert("Erreur", "ID utilisateur invalide", "Impossible d'ouvrir le profil.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();

            EditProfileController controller = loader.getController();
            controller.setUserId(currentUserId);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Mon Profil");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture du profil", e.getMessage());
        }
    }

    @FXML
    public void dossierbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDossier.fxml"));
            Parent root = loader.load();

            AjoutDossierController controller = loader.getController();
            controller.setEtudiantId(currentUserId);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajout Dossier");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture du dossier", e.getMessage());
        }
    }

    @FXML
    public void universitébutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminconditature.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Candidatures");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des candidatures", e.getMessage());
        }
    }

    @FXML
    public void entretienbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemanderEntretien.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Demander Entretien");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des entretiens", e.getMessage());
        }
    }

    @FXML
    public void evenementbutton(ActionEvent actionEvent) {
        // Already in events view, no need to navigate
    }

    @FXML
    public void hebergementbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Foyers");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des foyers", e.getMessage());
        }
    }

    @FXML
    public void restaurantbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurantClient.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Restaurants");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des restaurants", e.getMessage());
        }
    }

    @FXML
    public void volsbutton(ActionEvent actionEvent) {
        showAlert("Information", "Information", "La fonctionnalité des vols sera bientôt disponible.");
    }

    @FXML
    public void logoutbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - GradAway");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la déconnexion", e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
