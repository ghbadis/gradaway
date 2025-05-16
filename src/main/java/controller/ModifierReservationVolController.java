package controller;

import Services.serviceReservationVol;
import Services.serviceVols;
import entities.ReservationVol;
import entities.Vols;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.MyDatabase;

import java.sql.Connection;
import java.util.List;

public class ModifierReservationVolController {
    @FXML private TextField referenceField;
    @FXML private ComboBox<String> numeroVolComboBox;
    @FXML private Spinner<Integer> placesSpinner;
    @FXML private ComboBox<String> classeComboBox;
    @FXML private ComboBox<String> bagageComboBox;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private TextArea commentairesArea;
    @FXML private TextField prixTotalField;
    @FXML private Button validerButton;
    @FXML private Button annulerButton;

    private serviceReservationVol serviceReservation;
    private serviceVols serviceVols;
    private Connection connection;
    private ReservationVol reservationActuelle;

    @FXML
    public void initialize() {
        setupConnection();
        setupComboBoxes();
        setupListeners();
    }

    private void setupConnection() {
        try {
            connection = MyDatabase.getInstance().getCnx();
            serviceReservation = new serviceReservationVol(connection);
            serviceVols = new serviceVols(connection);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion",
                    "Impossible de se connecter à la base de données: " + e.getMessage());
        }
    }

    private void setupComboBoxes() {
        // Configuration des vols disponibles
        List<Vols> vols = serviceVols.getAllVols();
        numeroVolComboBox.setItems(FXCollections.observableArrayList(
                vols.stream().map(Vols::getNumeroVol).toList()
        ));

        // Configuration des classes
        classeComboBox.setItems(FXCollections.observableArrayList(
                "Économique", "Affaires", "Première"
        ));

        // Configuration des types de bagages
        bagageComboBox.setItems(FXCollections.observableArrayList(
                "Cabine", "Soute", "Cabine + Soute"
        ));

        // Configuration des statuts
        statutComboBox.setItems(FXCollections.observableArrayList(
                "En attente", "Payé", "Annulé"
        ));

        // Configuration du spinner de places
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        placesSpinner.setValueFactory(valueFactory);
    }

    private void setupListeners() {
        // Mise à jour du prix total lors des changements
        numeroVolComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updatePrixTotal());
        placesSpinner.valueProperty().addListener((obs, oldVal, newVal) -> updatePrixTotal());
        classeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> updatePrixTotal());
    }

    public void setReservation(ReservationVol reservation) {
        this.reservationActuelle = reservation;
        populateFields();
    }

    private void populateFields() {
        if (reservationActuelle == null) return;

        referenceField.setText(reservationActuelle.getReferenceReservation());
        Vols vol = serviceVols.getVolById(reservationActuelle.getIdVol());
        if (vol != null) {
            numeroVolComboBox.setValue(vol.getNumeroVol());
        }
        placesSpinner.getValueFactory().setValue(reservationActuelle.getNombrePlaces());
        classeComboBox.setValue(reservationActuelle.getClasse());
        bagageComboBox.setValue(reservationActuelle.getTypeBagage());
        statutComboBox.setValue(reservationActuelle.getStatutPaiement());
        commentairesArea.setText(reservationActuelle.getCommentaires());
        // Afficher le prix sans le symbole €
        prixTotalField.setText(String.format("%.2f", reservationActuelle.getPrixTotal()));
    }

    private void updatePrixTotal() {
        try {
            String numeroVol = numeroVolComboBox.getValue();
            if (numeroVol == null) {
                prixTotalField.setText("0.00");
                return;
            }

            Vols vol = serviceVols.getVolByNumero(numeroVol);
            if (vol == null) {
                prixTotalField.setText("0.00");
                return;
            }

            int places = placesSpinner.getValue();
            String classe = classeComboBox.getValue();
            if (classe == null) {
                classe = "Économique"; // Valeur par défaut
            }

            double prixBase = vol.getPrixStandard();
            double multiplicateur = switch (classe) {
                case "Affaires" -> 1.5;
                case "Première" -> 2.0;
                default -> 1.0;
            };

            double prixTotal = prixBase * places * multiplicateur;
            // Stocker le prix sans le symbole €
            prixTotalField.setText(String.format("%.2f", prixTotal));
        } catch (Exception e) {
            prixTotalField.setText("0.00");
        }
    }

    @FXML
    private void handleValider(ActionEvent event) {
        if (!validateFields()) return;

        try {
            updateReservation();
            serviceReservation.modifierReservation(reservationActuelle);

            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "La réservation a été modifiée avec succès.");
            closeWindow();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de la modification de la réservation: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (numeroVolComboBox.getValue() == null)
            errors.append("- Veuillez sélectionner un vol\n");
        if (classeComboBox.getValue() == null)
            errors.append("- Veuillez sélectionner une classe\n");
        if (bagageComboBox.getValue() == null)
            errors.append("- Veuillez sélectionner un type de bagage\n");
        if (statutComboBox.getValue() == null)
            errors.append("- Veuillez sélectionner un statut\n");

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errors.toString());
            return false;
        }
        return true;
    }

    private void updateReservation() {
        try {
            Vols vol = serviceVols.getVolByNumero(numeroVolComboBox.getValue());

            reservationActuelle.setIdVol(vol.getIdVol());
            reservationActuelle.setNombrePlaces(placesSpinner.getValue());

            // Correction pour le prix total
            String prixText = prixTotalField.getText();
            // Supprimer le symbole € et les espaces
            prixText = prixText.replace(" €", "").replace("€", "").trim();
            // Remplacer la virgule par un point si nécessaire
            prixText = prixText.replace(",", ".");

            try {
                double prixTotal = Double.parseDouble(prixText);
                reservationActuelle.setPrixTotal(prixTotal);
            } catch (NumberFormatException e) {
                // En cas d'erreur, calculer le prix directement
                Vols selectedVol = serviceVols.getVolByNumero(numeroVolComboBox.getValue());
                int places = placesSpinner.getValue();
                String classe = classeComboBox.getValue();

                double prixBase = selectedVol.getPrixStandard();
                double multiplicateur = 1.0;
                if (classe.equals("Affaires")) {
                    multiplicateur = 1.5;
                } else if (classe.equals("Première")) {
                    multiplicateur = 2.0;
                }

                double prixTotal = prixBase * places * multiplicateur;
                reservationActuelle.setPrixTotal(prixTotal);
            }

            reservationActuelle.setStatutPaiement(statutComboBox.getValue());
            reservationActuelle.setClasse(classeComboBox.getValue());
            reservationActuelle.setTypeBagage(bagageComboBox.getValue());
            reservationActuelle.setCommentaires(commentairesArea.getText());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la réservation: " + e.getMessage(), e);
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
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