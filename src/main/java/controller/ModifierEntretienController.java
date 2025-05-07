package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import entities.Entretien;
import entities.Expert;
import Services.ServiceEntretien;
import Services.ServiceExpert;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

public class ModifierEntretienController {
    @FXML
    private TextField idEntretienField;
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
    private Button modifierButton;
    @FXML
    private Button annulerButton;

    private final ServiceEntretien serviceEntretien = new ServiceEntretien();
    private final ServiceExpert serviceExpert = new ServiceExpert();
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    private Entretien entretienAModifier;

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

            // Configurer les boutons
            modifierButton.setOnAction(event -> modifierEntretien());
            annulerButton.setOnAction(event -> fermerFenetre());
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des experts: " + e.getMessage());
        }
    }

    public void setEntretien(Entretien entretien) {
        this.entretienAModifier = entretien;
        idEntretienField.setText(String.valueOf(entretien.getId_entretien()));
        
        // Find and set the expert in the ComboBox
        for (Expert expert : expertComboBox.getItems()) {
            if (expert.getId_expert() == entretien.getId_expert()) {
                expertComboBox.setValue(expert);
                break;
            }
        }
        
        userIdField.setText(String.valueOf(entretien.getId_user()));
        dateEntretien.setValue(entretien.getDate_entretien());
        heureEntretien.setText(entretien.getHeure_entretien().format(DateTimeFormatter.ofPattern("HH:mm")));
        etatEntretien.setValue(entretien.getEtat_entretien());
        typeEntretien.setValue(entretien.getType_entretien());
        offreComboBox.setValue(entretien.getOffre());
    }

    private void modifierEntretien() {
        if (!validateFields()) {
            return;
        }

        try {
            // Créer un nouvel entretien avec les données modifiées
            String offre = offreComboBox.getValue();
            Entretien entretienModifie = new Entretien(
                Integer.parseInt(idEntretienField.getText().trim()),
                expertComboBox.getValue().getId_expert(),
                Integer.parseInt(userIdField.getText().trim()),
                dateEntretien.getValue(),
                LocalTime.parse(heureEntretien.getText().trim()),
                etatEntretien.getValue(),
                typeEntretien.getValue(),
                offre
            );

            // Mettre à jour l'entretien dans la base de données
            serviceEntretien.modifier(entretienModifie);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'entretien a été modifié avec succès");
            fermerFenetre();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification de l'entretien: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        // Vérifier que l'ID est un nombre valide
        try {
            int id = Integer.parseInt(idEntretienField.getText().trim());
            if (id <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID doit être un nombre positif");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID doit être un nombre valide");
            return false;
        }

        if (expertComboBox.getValue() == null ||
            userIdField.getText().trim().isEmpty() ||
            dateEntretien.getValue() == null ||
            heureEntretien.getText().trim().isEmpty() ||
            etatEntretien.getValue() == null ||
            typeEntretien.getValue() == null ||
            offreComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires");
            return false;
        }

        // Vérifier le format de l'heure
        if (!TIME_PATTERN.matcher(heureEntretien.getText().trim()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le format de l'heure doit être HH:mm");
            return false;
        }

        // Vérifier que l'ID utilisateur est un nombre valide
        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            if (userId <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID utilisateur doit être un nombre positif");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID utilisateur doit être un nombre valide");
            return false;
        }

        return true;
    }

    private void fermerFenetre() {
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
