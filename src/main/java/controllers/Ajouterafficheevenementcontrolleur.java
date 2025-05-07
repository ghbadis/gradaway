package controllers;

import entities.Evenement;
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class Ajouterafficheevenementcontrolleur {
    @FXML
    private TextField chercher_txtf;
    @FXML
    private Button chercher_button;
    @FXML
    private ComboBox<String> domaine_comb;
    @FXML
    private GridPane affiche_even_grid;

    private ServiceEvenement serviceEvenement;
    private List<Evenement> evenements;

    @FXML
    public void initialize() {
        serviceEvenement = new ServiceEvenement();
        chargerEvenements();
        initialiserComboBox();
        setupListeners();
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
        if (evenement.getImage() != null) {
            try {
                Image image = new Image(evenement.getImage());
                imageView.setImage(image);
                imageView.setFitWidth(250);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
            } catch (Exception e) {
                // Image par défaut si l'image ne peut pas être chargée
                imageView.setImage(new Image("/image/default_event.png"));
            }
        }

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
            
            // Passer l'événement au contrôleur de réservation
            ReservationEvenementController controller = loader.getController();
            controller.setEvenement(evenement);
            
            Stage stage = new Stage();
            stage.setTitle("Réservation - " + evenement.getNom());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace(); // Affiche le stacktrace dans la console
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre de réservation", e.toString() + "\n" + (e.getCause() != null ? e.getCause().toString() : ""));
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

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
