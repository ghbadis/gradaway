package controllers;

import entities.Evenement;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DateCell;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Services.ServiceEvenement;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ModifierEvenementController {
    @FXML private TextField nom_txtf;
    @FXML private TextField description_txtf;
    @FXML private TextField lieu_txtf;
    @FXML private TextField domaine_txtf;
    @FXML private DatePicker date_picker;
    @FXML private TextField place_disponible_txtf;
    @FXML private Button valider_button;
    @FXML private Button annuler_button;

    private Evenement evenement;
    private final ServiceEvenement serviceEvenement = new ServiceEvenement();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
        nom_txtf.setText(evenement.getNom());
        description_txtf.setText(evenement.getDescription());
        lieu_txtf.setText(evenement.getLieu());
        domaine_txtf.setText(evenement.getDomaine());
        date_picker.setValue(LocalDate.parse(evenement.getDate(), dateFormatter));
        place_disponible_txtf.setText(String.valueOf(evenement.getPlaces_disponibles()));
    }

    @FXML
    public void initialize() {
        // Empêcher la sélection de dates passées
        date_picker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.isBefore(today));
            }
        });

        valider_button.setOnAction(event -> validerModification());
        annuler_button.setOnAction(event -> fermerFenetre());
    }

    private void validerModification() {
        try {
            evenement.setNom(nom_txtf.getText());
            evenement.setDescription(description_txtf.getText());
            evenement.setLieu(lieu_txtf.getText());
            evenement.setDomaine(domaine_txtf.getText());
            evenement.setDate(date_picker.getValue().format(dateFormatter));
            evenement.setPlaces_disponibles(Integer.parseInt(place_disponible_txtf.getText()));
            serviceEvenement.modifier(evenement);
            showAlert("Succès", "Événement modifié avec succès", null);
            fermerFenetre();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la modification", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format invalide", "Le nombre de places doit être un nombre entier");
        }
    }

    private void fermerFenetre() {
        Stage stage = (Stage) valider_button.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 