package controllers;

import entities.Evenement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import Services.ServiceEvenement;


import java.sql.SQLException;
import java.util.List;

public class Ajoutergestionevenementcontrolleur {
    @FXML
    private TableColumn<Evenement, Integer> domaine_col;
    @FXML
    private TextField domaine_txtf;
    @FXML
    private Button modifier_button;
    @FXML
    private TextField description_txtf;
    @FXML
    private Button affichier_button;
    @FXML
    private TableColumn<Evenement, String> nom_col;
    @FXML
    private TableColumn<Evenement, String> lieu_col;
    @FXML
    private Button ajouter_button;
    @FXML
    private TableColumn<Evenement, Integer> place_disponible_col;
    @FXML
    private TextField date_txtf;
    @FXML
    private TableColumn<Evenement, String> date_col;
    @FXML
    private TextField nom_txtf;
    @FXML
    private TextField place_disponible_txtf;
    @FXML
    private ComboBox<String> lieu_comb;
    @FXML
    private TextField chercher_txtf;
    @FXML
    private TableColumn<Evenement, Integer> evenement_id_col;
    @FXML
    private TextField evenement_id_txtf;
    @FXML
    private TableColumn<Evenement, String> description_col;
    @FXML
    private TableView<Evenement> gestion_evenement_tableview;
    @FXML
    private Button supprimer_button;

    private ServiceEvenement serviceEvenement;
    private ObservableList<Evenement> evenementsList;

    @FXML
    public void initialize() {
        serviceEvenement = new ServiceEvenement();
        evenementsList = FXCollections.observableArrayList();

        // Initialiser les colonnes du TableView
        evenement_id_col.setCellValueFactory(new PropertyValueFactory<>("id_evenement"));
        nom_col.setCellValueFactory(new PropertyValueFactory<>("nom"));
        description_col.setCellValueFactory(new PropertyValueFactory<>("description"));
        date_col.setCellValueFactory(new PropertyValueFactory<>("date"));
        lieu_col.setCellValueFactory(new PropertyValueFactory<>("lieu"));
        domaine_col.setCellValueFactory(new PropertyValueFactory<>("domaine"));
        place_disponible_col.setCellValueFactory(new PropertyValueFactory<>("places_disponibles"));

        // Ajouter les lieux au ComboBox
        lieu_comb.getItems().addAll("Salle Polyvalente", "Amphithéâtre", "Salle de conférence", "Espace extérieur");

        // Charger les données
        loadData();

        // Ajouter les listeners pour les boutons
        ajouter_button.setOnAction(event -> ajouterEvenement());
        modifier_button.setOnAction(event -> modifierEvenement());
        supprimer_button.setOnAction(event -> supprimerEvenement());
        affichier_button.setOnAction(event -> afficherEvenement());

        // Ajouter un listener pour la sélection dans le TableView
        gestion_evenement_tableview.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                afficherEvenementSelectionne(newSelection);
            }
        });
    }

    private void loadData() {
        try {
            List<Evenement> evenements = serviceEvenement.recuperer();
            evenementsList.clear();
            evenementsList.addAll(evenements);
            gestion_evenement_tableview.setItems(evenementsList);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des données", e.getMessage());
        }
    }

    private void ajouterEvenement() {
        try {
            String nom = nom_txtf.getText();
            String description = description_txtf.getText();
            String date = date_txtf.getText();
            String lieu = lieu_comb.getValue();
            String domaine = domaine_txtf.getText();
            int placesDisponibles = Integer.parseInt(place_disponible_txtf.getText());

            Evenement evenement = new Evenement(nom, description, date, lieu, domaine, placesDisponibles);
            serviceEvenement.ajouter(evenement);

            clearFields();
            loadData();
            showAlert("Succès", "Événement ajouté avec succès", null);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout de l'événement", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format invalide", "Le nombre de places doit être un nombre entier");
        }
    }

    private void modifierEvenement() {
        Evenement selectedEvenement = gestion_evenement_tableview.getSelectionModel().getSelectedItem();
        if (selectedEvenement == null) {
            showAlert("Erreur", "Aucun événement sélectionné", "Veuillez sélectionner un événement à modifier");
            return;
        }

        try {
            selectedEvenement.setNom(nom_txtf.getText());
            selectedEvenement.setDescription(description_txtf.getText());
            selectedEvenement.setDate(date_txtf.getText());
            selectedEvenement.setLieu(lieu_comb.getValue());
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
        Evenement selectedEvenement = gestion_evenement_tableview.getSelectionModel().getSelectedItem();
        if (selectedEvenement == null) {
            showAlert("Erreur", "Aucun événement sélectionné", "Veuillez sélectionner un événement à supprimer");
            return;
        }

        try {
            serviceEvenement.supprimer(selectedEvenement);
            clearFields();
            loadData();
            showAlert("Succès", "Événement supprimé avec succès", null);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la suppression de l'événement", e.getMessage());
        }
    }

    private void afficherEvenement() {
        Evenement selectedEvenement = gestion_evenement_tableview.getSelectionModel().getSelectedItem();
        if (selectedEvenement != null) {
            afficherEvenementSelectionne(selectedEvenement);
        }
    }

    private void afficherEvenementSelectionne(Evenement evenement) {
        evenement_id_txtf.setText(String.valueOf(evenement.getId_evenement()));
        nom_txtf.setText(evenement.getNom());
        description_txtf.setText(evenement.getDescription());
        date_txtf.setText(evenement.getDate());
        lieu_comb.setValue(evenement.getLieu());
        domaine_txtf.setText(evenement.getDomaine());
        place_disponible_txtf.setText(String.valueOf(evenement.getPlaces_disponibles()));
    }

    private void clearFields() {
        evenement_id_txtf.clear();
        nom_txtf.clear();
        description_txtf.clear();
        date_txtf.clear();
        lieu_comb.setValue(null);
        domaine_txtf.clear();
        place_disponible_txtf.clear();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
