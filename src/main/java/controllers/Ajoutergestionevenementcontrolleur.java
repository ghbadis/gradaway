package controllers;

import entities.Evenement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import Services.ServiceEvenement;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.IOException;

public class Ajoutergestionevenementcontrolleur {
    @FXML
    private TextField domaine_txtf;
    @FXML
    private TextField description_txtf;
    @FXML
    private DatePicker date_picker;
    @FXML
    private TextField nom_txtf;
    @FXML
    private TextField place_disponible_txtf;
    @FXML
    private TextField lieu_txtf;
    @FXML
    private TextField chercher_txtf;
    @FXML
    private VBox evenements_container;
    @FXML
    private Button ajouter_evenement_button;
    @FXML
    private Button liste_reservation_button;
    @FXML
    private VBox formulaire_container;
    @FXML
    private Button valider_button;
    @FXML
    private Button annuler_button;
    @FXML
    private Button user_button;
    @FXML
    private TextField image_txtf;
    @FXML
    private Button choisir_image_button;

    private ServiceEvenement serviceEvenement;
    private ObservableList<Evenement> evenementsList;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Evenement selectedEvenement;

    private boolean isEditMode = false;

    @FXML
    public void initialize() {
        serviceEvenement = new ServiceEvenement();
        evenementsList = FXCollections.observableArrayList();
        
        // Charger les données
        loadData();
        
        ajouter_evenement_button.setOnAction(event -> showFormAjout());
        liste_reservation_button.setOnAction(event -> ouvrirListeReservations());
        valider_button.setOnAction(event -> validerFormulaire());
        annuler_button.setOnAction(event -> hideFormulaire());
        user_button.setOnAction(event -> ouvrirInterfaceUser());
        if (choisir_image_button != null) {
            choisir_image_button.setOnAction(event -> choisirImage());
        }
        // Ajouter un listener pour la recherche
        chercher_txtf.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEvenements(newValue);
        });
    }

    private void loadData() {
        try {
            List<Evenement> evenements = serviceEvenement.recuperer();
            evenementsList.clear();
            evenementsList.addAll(evenements);
            afficherEvenements(evenementsList);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des données", e.getMessage());
        }
    }

    private void afficherEvenements(ObservableList<Evenement> evenements) {
        evenements_container.getChildren().clear();
        
        for (Evenement evenement : evenements) {
            HBox eventCard = creerEventCard(evenement);
            evenements_container.getChildren().add(eventCard);
        }
    }

    private HBox creerEventCard(Evenement evenement) {
        HBox card = new HBox(10);
        card.getStyleClass().addAll("white-bg", "shadow");
        card.setPrefWidth(800);
        card.setPrefHeight(100);
        card.setPadding(new javafx.geometry.Insets(10));

        // Image de l'événement
        ImageView imageView = new ImageView();
        try {
            if (evenement.getImage() != null && !evenement.getImage().isEmpty()) {
                Image image = new Image(evenement.getImage());
                imageView.setImage(image);
            } else {
                // Use default image if no image is set
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default_event.png")));
            }
        } catch (Exception e) {
            // Use default image if image loading fails
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default_event.png")));
            } catch (Exception ex) {
                // If even the default image fails, create an empty ImageView
                imageView = new ImageView();
            }
        }
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);

        // Informations de l'événement
        VBox infoBox = new VBox(5);
        Label nomLabel = new Label(evenement.getNom());
        nomLabel.getStyleClass().add("event-title");
        Label dateLabel = new Label("Date: " + evenement.getDate());
        Label lieuLabel = new Label("Lieu: " + evenement.getLieu());
        Label placesLabel = new Label("Places disponibles: " + evenement.getPlaces_disponibles());
        infoBox.getChildren().addAll(nomLabel, dateLabel, lieuLabel, placesLabel);

        // Boutons
        HBox buttonBox = new HBox(10);
        Button modifierBtn = new Button("Modifier");
        Button supprimerBtn = new Button("Supprimer");
        
        modifierBtn.getStyleClass().add("update-btn");
        supprimerBtn.getStyleClass().add("delete-btn");

        modifierBtn.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifier_evenement.fxml"));
                Parent root = loader.load();
                ModifierEvenementController controller = loader.getController();
                controller.setEvenement(evenement);
                Stage stage = new Stage();
                stage.setTitle("Modifier l'événement");
                stage.setScene(new Scene(root));
                stage.showAndWait();
                loadData();
            } catch (Exception e) {
                showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification", e.getMessage());
            }
        });
        
        supprimerBtn.setOnAction(event -> {
            selectedEvenement = evenement;
            supprimerEvenement();
        });

        buttonBox.getChildren().addAll(modifierBtn, supprimerBtn);

        card.getChildren().addAll(imageView, infoBox, buttonBox);
        return card;
    }

    private void filterEvenements(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            afficherEvenements(evenementsList);
        } else {
            ObservableList<Evenement> filteredList = evenementsList.filtered(evenement ->
                evenement.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                evenement.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                evenement.getLieu().toLowerCase().contains(searchText.toLowerCase()) ||
                evenement.getDomaine().toLowerCase().contains(searchText.toLowerCase())
            );
            afficherEvenements(filteredList);
        }
    }

    private void showFormAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter_evenement.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un événement");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            // Recharger les données après l'ajout
            loadData();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout", e.getMessage());
        }
    }

    private void showFormModification(Evenement evenement) {
        isEditMode = true;
        afficherEvenementSelectionne(evenement);
        formulaire_container.setVisible(true);
        formulaire_container.setManaged(true);
    }

    private void hideFormulaire() {
        formulaire_container.setVisible(false);
        formulaire_container.setManaged(false);
        clearFields();
        selectedEvenement = null;
    }

    private void validerFormulaire() {
        if (isEditMode) {
            modifierEvenement();
        } else {
            ajouterEvenement();
        }
        hideFormulaire();
    }

    private void ajouterEvenement() {
        try {
            String nom = nom_txtf.getText();
            String description = description_txtf.getText();
            String date = date_picker.getValue().format(dateFormatter);
            String lieu = lieu_txtf.getText();
            String domaine = domaine_txtf.getText();
            int placesDisponibles = Integer.parseInt(place_disponible_txtf.getText());
            String image = image_txtf != null ? image_txtf.getText() : null;

            Evenement evenement = new Evenement(nom, description, date, lieu, domaine, placesDisponibles, image);
            serviceEvenement.ajouter(evenement);
            
            loadData();
            showAlert("Succès", "Événement ajouté avec succès", null);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout de l'événement", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format invalide", "Le nombre de places doit être un nombre entier");
        }
    }

    private void modifierEvenement() {
        if (selectedEvenement == null) {
            showAlert("Erreur", "Aucun événement sélectionné", "Veuillez sélectionner un événement à modifier");
            return;
        }

        try {
            selectedEvenement.setNom(nom_txtf.getText());
            selectedEvenement.setDescription(description_txtf.getText());
            selectedEvenement.setDate(date_picker.getValue().format(dateFormatter));
            selectedEvenement.setLieu(lieu_txtf.getText());
            selectedEvenement.setDomaine(domaine_txtf.getText());
            selectedEvenement.setPlaces_disponibles(Integer.parseInt(place_disponible_txtf.getText()));

            serviceEvenement.modifier(selectedEvenement);
            loadData();
            showAlert("Succès", "Événement modifié avec succès", null);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la modification de l'événement", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format invalide", "Le nombre de places doit être un nombre entier");
        }
    }

    private void supprimerEvenement() {
        if (selectedEvenement == null) {
            showAlert("Erreur", "Aucun événement sélectionné", "Veuillez sélectionner un événement à supprimer");
            return;
        }

        try {
            serviceEvenement.supprimer(selectedEvenement);
            loadData();
            showAlert("Succès", "Événement supprimé avec succès", null);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la suppression de l'événement", e.getMessage());
        }
    }

    private void afficherEvenementSelectionne(Evenement evenement) {
        nom_txtf.setText(evenement.getNom());
        description_txtf.setText(evenement.getDescription());
        date_picker.setValue(LocalDate.parse(evenement.getDate(), dateFormatter));
        lieu_txtf.setText(evenement.getLieu());
        domaine_txtf.setText(evenement.getDomaine());
        place_disponible_txtf.setText(String.valueOf(evenement.getPlaces_disponibles()));
        if (image_txtf != null) image_txtf.setText(evenement.getImage());
    }

    private void clearFields() {
        nom_txtf.clear();
        description_txtf.clear();
        date_picker.setValue(null);
        lieu_txtf.clear();
        domaine_txtf.clear();
        place_disponible_txtf.clear();
        if (image_txtf != null) image_txtf.clear();
        selectedEvenement = null;
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void ouvrirInterfaceUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affiche_evenement.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Affichage des Événements");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace(); // Affiche le stacktrace dans la console
            showAlert("Erreur", "Impossible d'ouvrir l'interface utilisateur", e.toString() + "\n" + (e.getCause() != null ? e.getCause().toString() : ""));
        }
    }

    private void choisirImage() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new javafx.stage.FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        java.io.File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            image_txtf.setText(selectedFile.toURI().toString());
        }
    }

    private void ouvrirListeReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/liste_reservation.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Liste des Réservations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la liste des réservations", e.getMessage());
        }
    }
}
