package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.layout.Region;

import java.io.IOException;

public class ReserverFoyerControllers {

    @FXML private TextField tf_id;
    @FXML private TextField tf_gmail;
    @FXML private DatePicker dp_date_debut;
    @FXML private DatePicker dp_date_fin;
    @FXML private DatePicker dp_date_reserver;

    @FXML
    public void initialize() {
        setupStyles();
        setupDatePickers();
    }

    private void setupStyles() {
        String textFieldStyle = "-fx-background-color: #f8f9fa; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 8;";

        tf_id.setStyle(textFieldStyle);
        tf_gmail.setStyle(textFieldStyle);

        String datePickerStyle = "-fx-background-color: #f8f9fa; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5;";

        dp_date_debut.setStyle(datePickerStyle);
        dp_date_fin.setStyle(datePickerStyle);
        dp_date_reserver.setStyle(datePickerStyle);

        tf_id.setPrefWidth(250);
        tf_gmail.setPrefWidth(250);
        dp_date_debut.setPrefWidth(250);
        dp_date_fin.setPrefWidth(250);
        dp_date_reserver.setPrefWidth(250);

        String focusStyle = "-fx-border-color: #2196F3; -fx-border-width: 2px;";
        tf_id.focusedProperty().addListener((obs, oldVal, newVal) -> {
            tf_id.setStyle(newVal ? textFieldStyle + focusStyle : textFieldStyle);
        });

        tf_gmail.focusedProperty().addListener((obs, oldVal, newVal) -> {
            tf_gmail.setStyle(newVal ? textFieldStyle + focusStyle : textFieldStyle);
        });
    }

    private void setupDatePickers() {
        java.time.LocalDate today = java.time.LocalDate.now();
        dp_date_debut.setValue(today);
        dp_date_fin.setValue(today);
        dp_date_reserver.setValue(today);

        dp_date_debut.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(today) < 0);
            }
        });

        dp_date_fin.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(dp_date_debut.getValue()) < 0);
            }
        });

        dp_date_debut.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (dp_date_fin.getValue() != null && dp_date_fin.getValue().compareTo(newVal) < 0) {
                dp_date_fin.setValue(newVal);
            }
        });
    }

    @FXML
    private void confirme() {
        if (!validateFields()) {
            return;
        }

        // TODO: Add reservation to database
        showAlert("Succès", "Votre réservation a été enregistrée avec succès!", Alert.AlertType.INFORMATION);
        navigateToListFoyer();
    }

    @FXML
    private void annuler() {
        navigateToListFoyer();
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (tf_id.getText().trim().isEmpty()) {
            errors.append("L'ID est requis\n");
        }

        if (tf_gmail.getText().trim().isEmpty() || !isValidEmail(tf_gmail.getText())) {
            errors.append("Email invalide\n");
        }

        if (dp_date_debut.getValue() == null) {
            errors.append("La date de début est requise\n");
        }

        if (dp_date_fin.getValue() == null) {
            errors.append("La date de fin est requise\n");
        }

        if (dp_date_reserver.getValue() == null) {
            errors.append("La date de réservation est requise\n");
        }

        if (dp_date_debut.getValue() != null && dp_date_fin.getValue() != null &&
                dp_date_fin.getValue().isBefore(dp_date_debut.getValue())) {
            errors.append("La date de fin doit être après la date de début\n");
        }

        if (errors.length() > 0) {
            showAlert("Erreur de validation", errors.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }

    private void navigateToListFoyer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyer.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) tf_id.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);

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
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
}
