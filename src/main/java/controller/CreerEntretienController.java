package controller;

import entities.Entretien;
import entities.Expert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import Services.ServiceEntretien;
import Services.ServiceExpert;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class CreerEntretienController {

    @FXML
    private ComboBox<Expert> expertComboBox;

    @FXML
    private TextField userIdField;

    @FXML
    private DatePicker dateEntretien;

    @FXML
    private TextField heureEntretien;

    @FXML
    private ComboBox<String> etatEntretien;

    @FXML
    private ComboBox<String> typeEntretien;

    @FXML
    private ComboBox<String> offreComboBox;

    @FXML
    private Button creerButton;

    @FXML
    private Button annulerButton;

    private final ServiceEntretien serviceEntretien = new ServiceEntretien();
    private final ServiceExpert serviceExpert = new ServiceExpert();

    @FXML
    public void initialize() {
        try {
            List<Expert> experts = serviceExpert.recuperer();
            expertComboBox.getItems().addAll(experts);

            // Setup how Expert appears in ComboBox
            expertComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Expert item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : (item.getNom_expert() + " " + item.getPrenom_expert()));
                }
            });
            expertComboBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Expert item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : (item.getNom_expert() + " " + item.getPrenom_expert()));
                }
            });

            // Fill état and type dropdowns
            etatEntretien.getItems().addAll("en attente", "confirmé", "annulé");
            typeEntretien.getItems().addAll("présentiel", "en ligne");

            offreComboBox.getItems().addAll("licence", "master", "doctorat", "echange universitaire");

        } catch (SQLException e) {
            showError("Erreur de chargement", e.getMessage());
        }
    }

    @FXML
    public void creerEntretien(ActionEvent event) {
        try {
            if (expertComboBox.getValue() == null || userIdField.getText().isEmpty()
                    || dateEntretien.getValue() == null || heureEntretien.getText().isEmpty()
                    || etatEntretien.getValue() == null || typeEntretien.getValue() == null
                    || offreComboBox.getValue() == null) {
                showError("Champs manquants", "Veuillez remplir tous les champs.");
                return;
            }

            int idUser = Integer.parseInt(userIdField.getText());
            int idExpert = expertComboBox.getValue().getId_expert();
            LocalDate date = dateEntretien.getValue();
            LocalTime heure = LocalTime.parse(heureEntretien.getText());
            String etat = etatEntretien.getValue();
            String type = typeEntretien.getValue();
            String offre = offreComboBox.getValue();

            Entretien entretien = new Entretien(idExpert, idUser, date, heure, etat, type, offre);

            serviceEntretien.ajouter(entretien);
            showInfo("Succès", "Entretien ajouté avec succès.");
            annuler(null); // Reset form

        } catch (NumberFormatException e) {
            showError("Format invalide", "ID utilisateur doit être un nombre.");
        } catch (Exception e) {
            showError("Erreur", e.getMessage());
        }
    }

    @FXML
    public void annuler(ActionEvent event) {
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur - " + title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void preRemplirDemande(ListeDemandesEntretienController.DemandeEntretien demande) {
        // Set the user ID from the request
        userIdField.setText(String.valueOf(demande.getIdUser()));
        
        // Set the date and time from the request
        dateEntretien.setValue(demande.getDateSouhaitee());
        heureEntretien.setText(demande.getHeureSouhaitee().format(DateTimeFormatter.ofPattern("HH:mm")));
        
        // Set default values for other fields
        etatEntretien.setValue("en attente");
        typeEntretien.setValue("présentiel");
        
        // The expert will need to be selected manually as it's not part of the request
    }
}
