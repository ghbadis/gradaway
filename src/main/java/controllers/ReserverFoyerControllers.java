package controllers;

import Services.ServiceReservationFoyer;
import entities.ReservationFoyer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.layout.Region;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.SQLException;

public class ReserverFoyerControllers {

    @FXML private DatePicker dp_date_debut;
    @FXML private DatePicker dp_date_fin;
    @FXML private DatePicker dp_date_reserver;

    private ServiceReservationFoyer serviceReservation = new ServiceReservationFoyer();

    @FXML
    public void initialize() {
        setupDatePickers();
        applyHoverEffects();
    }

    private void applyHoverEffects() {
        dp_date_debut.setOnMouseEntered(e -> {
            dp_date_debut.setStyle("-fx-background-color: #f0f7ff; -fx-background-radius: 8;");
        });
        dp_date_debut.setOnMouseExited(e -> {
            dp_date_debut.setStyle("-fx-background-radius: 8;");
        });

        dp_date_fin.setOnMouseEntered(e -> {
            dp_date_fin.setStyle("-fx-background-color: #f0f7ff; -fx-background-radius: 8;");
        });
        dp_date_fin.setOnMouseExited(e -> {
            dp_date_fin.setStyle("-fx-background-radius: 8;");
        });

        dp_date_reserver.setOnMouseEntered(e -> {
            dp_date_reserver.setStyle("-fx-background-color: #f0f7ff; -fx-background-radius: 8;");
        });
        dp_date_reserver.setOnMouseExited(e -> {
            dp_date_reserver.setStyle("-fx-background-radius: 8;");
        });
    }

    private void setupDatePickers() {
        java.time.LocalDate today = java.time.LocalDate.now();
        dp_date_debut.setValue(today);
        dp_date_fin.setValue(today.plusDays(7));
        dp_date_reserver.setValue(today);

        dp_date_debut.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(today) < 0);
                if (date.compareTo(today) == 0) {
                    setStyle("-fx-background-color: #c8e6c9;");
                } else if (date.getDayOfWeek().getValue() >= 6) {
                    setStyle("-fx-background-color: #e3f2fd;");
                }
            }
        });

        dp_date_fin.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(dp_date_debut.getValue()) < 0);
                if (dp_date_debut.getValue() != null && date.isEqual(dp_date_debut.getValue().plusDays(7))) {
                    setStyle("-fx-background-color: #c8e6c9;");
                } else if (date.getDayOfWeek().getValue() >= 6) {
                    setStyle("-fx-background-color: #e3f2fd;");
                }
            }
        });

        dp_date_debut.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (dp_date_fin.getValue() == null || dp_date_fin.getValue().compareTo(newVal) < 0) {
                    dp_date_fin.setValue(newVal.plusDays(7));
                }
                dp_date_fin.setDayCellFactory(dp_date_fin.getDayCellFactory());
            }
        });
    }

    @FXML
    private void confirme() {
        if (!validateFields()) {
            return;
        }

        // Récupérer l'ID de l'étudiant connecté (exemple: 31)
        int currentUserId = 31; // À remplacer par l'ID dynamique

        // Récupérer l'ID du foyer sélectionné (exemple: 68 pour "pnl")
        int selectedFoyerId = 68; // À remplacer par l'ID du foyer sélectionné

        try {
            // Vérifier que les IDs existent
            if (!serviceReservation.studentExists(currentUserId) ||
                    !serviceReservation.foyerExists(selectedFoyerId)) {
                showAlert("Erreur", "Étudiant ou foyer invalide", Alert.AlertType.ERROR);
                return;
            }

            ReservationFoyer reservation = new ReservationFoyer();
            reservation.setDateDebut(dp_date_debut.getValue());
            reservation.setDateFin(dp_date_fin.getValue());
            reservation.setDateReservation(dp_date_reserver.getValue());
            reservation.setIdEtudiant(currentUserId);
            reservation.setFoyerId(selectedFoyerId);

            serviceReservation.ajouter(reservation);

            // Création d'une alerte simple avec un seul bouton OK
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Succès");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Votre réservation a été ajoutée avec succès.");
            
            // Stylisation de l'alerte
            DialogPane dialogPane = successAlert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.color(0, 0, 0, 0.2));
            dialogPane.setEffect(shadow);
            
            // Personnalisation du bouton OK
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");
            okButton.setText("OK");
            
            // Afficher l'alerte et réinitialiser le formulaire après fermeture
            successAlert.showAndWait();
            resetForm();

        } catch (SQLException e) {
            e.printStackTrace(); // Afficher la trace complète dans la console
            showAlert("Erreur", "Échec de l'enregistrement: " + e.getMessage() + "\n\nVérifiez que l'ID étudiant " + currentUserId + " et l'ID foyer " + selectedFoyerId + " existent dans la base de données.", Alert.AlertType.ERROR);
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (dp_date_debut.getValue() == null) {
            errors.append("• Date de début requise\n");
        }
        if (dp_date_fin.getValue() == null) {
            errors.append("• Date de fin requise\n");
        }
        if (dp_date_reserver.getValue() == null) {
            errors.append("• Date de réservation requise\n");
        }
        if (dp_date_debut.getValue() != null && dp_date_fin.getValue() != null &&
                dp_date_fin.getValue().isBefore(dp_date_debut.getValue())) {
            errors.append("• La date de fin doit être après la date de début\n");
        }

        if (errors.length() > 0) {
            showAlert("Erreur", errors.toString(), Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }
    
    /**
     * Réinitialise le formulaire après une réservation réussie
     */
    private void resetForm() {
        // Réinitialiser les dates avec les valeurs par défaut
        java.time.LocalDate today = java.time.LocalDate.now();
        dp_date_debut.setValue(today);
        dp_date_fin.setValue(today.plusDays(7));
        dp_date_reserver.setValue(today);
        
        // Animation de succès pour indiquer que le formulaire a été réinitialisé
        javafx.animation.ScaleTransition scaleTransition = new javafx.animation.ScaleTransition(Duration.millis(200), dp_date_debut);
        scaleTransition.setFromX(1.0);
        scaleTransition.setFromY(1.0);
        scaleTransition.setToX(1.05);
        scaleTransition.setToY(1.05);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    @FXML
    private void annuler() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), dp_date_debut.getScene().getRoot());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.9);
        fadeOut.setOnFinished(e -> navigateToListFoyerClient());
        fadeOut.play();
    }

    private void navigateToListFoyer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyer.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dp_date_debut.getScene().getWindow();
            stage.setScene(new Scene(root));

            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Navigation impossible: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void navigateToListFoyerClient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) dp_date_debut.getScene().getWindow();
            stage.setScene(new Scene(root));

            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Navigation impossible: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.color(0, 0, 0, 0.2));
        dialogPane.setEffect(shadow);

        alert.showAndWait();
    }
}