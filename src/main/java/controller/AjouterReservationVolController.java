package controller;

import entities.ReservationVol;
import entities.Vols;
import Services.serviceReservationVol;
import Services.serviceVols;
import utils.MyDatabase;
import utils.EmailUtils; // Importer la nouvelle classe
import utils.SessionManager; // Add SessionManager import
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public class AjouterReservationVolController {
    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> numeroVolComboBox;

    @FXML
    private Spinner<Integer> placesSpinner;

    @FXML
    private ComboBox<String> classeComboBox;

    @FXML
    private ComboBox<String> bagageComboBox;

    @FXML
    private TextArea commentairesArea;

    @FXML
    private TextField prixTotalField;

    @FXML
    private Button validerButton;

    @FXML
    private Button annulerButton;

    private serviceReservationVol serviceReservationVol;
    private serviceVols serviceVols;

    // Email de l'utilisateur - à définir lors de l'ouverture de la fenêtre
    private String userEmail = "utilisateur@example.com"; // Email par défaut

    public AjouterReservationVolController() {
        Connection connection = MyDatabase.getInstance().getCnx();
        serviceReservationVol = new serviceReservationVol(connection);
        serviceVols = new serviceVols(connection);
    }

    /**
     * Définir l'email de l'utilisateur
     * @param email Email de l'utilisateur
     */
    public void setUserEmail(String email) {
        if (email != null && !email.trim().isEmpty()) {
            this.userEmail = email;
        }
    }

    @FXML
    public void initialize() {
        // Auto-fill email field with logged-in user's email
        String loggedInEmail = SessionManager.getInstance().getUserEmail();
        if (loggedInEmail != null && !loggedInEmail.isEmpty()) {
            emailField.setText(loggedInEmail);
            emailField.setEditable(false);
        }

        // Initialiser les ComboBox
        initializeComboBoxes();

        // Configurer le Spinner
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        placesSpinner.setValueFactory(valueFactory);

        // Mettre à jour le prix total lorsque le nombre de places change
        placesSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            updatePrixTotal();
        });

        // Mettre à jour le prix total lorsque la classe change
        classeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            updatePrixTotal();
        });

        // Mettre à jour le prix total lorsque le type de bagage change
        bagageComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            updatePrixTotal();
        });

        // Mettre à jour le prix total lorsque le vol change
        numeroVolComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            updatePrixTotal();
        });
    }

    private void initializeComboBoxes() {
        // Remplir la ComboBox des numéros de vol
        List<String> numerosVol = serviceVols.getAllNumeroVols();
        numeroVolComboBox.setItems(FXCollections.observableArrayList(numerosVol));

        // Remplir la ComboBox des classes
        ObservableList<String> classes = FXCollections.observableArrayList(
                "Économique", "Affaires", "Première"
        );
        classeComboBox.setItems(classes);
        classeComboBox.setValue("Économique");

        // Remplir la ComboBox des types de bagage
        ObservableList<String> bagages = FXCollections.observableArrayList(
                "Cabine", "Soute", "Sans bagage"
        );
        bagageComboBox.setItems(bagages);
        bagageComboBox.setValue("Cabine");
    }

    private void updatePrixTotal() {
        try {
            // Vérifier si le numéro de vol est sélectionné
            if (numeroVolComboBox.getValue() == null) {
                prixTotalField.setText("0.00");
                return;
            }

            // Récupérer le prix unitaire du vol
            String numeroVol = numeroVolComboBox.getValue();
            int idVol = serviceVols.getIdVolByNumero(numeroVol);
            double prixUnitaire = serviceVols.getPrixVolById(idVol);

            // Récupérer le nombre de places
            int nombrePlaces = placesSpinner.getValue();

            // Calculer simplement le prix total (prix unitaire × nombre de places)
            double prixTotal = prixUnitaire * nombrePlaces;

            // Afficher le prix total formaté avec 2 décimales
            prixTotalField.setText(String.format("%.2f", prixTotal));
        } catch (Exception e) {
            // En cas d'erreur, afficher 0.00
            prixTotalField.setText("0.00");
        }
    }

    @FXML
    private void handleValider(ActionEvent event) {
        if (validateFields()) {
            try {
                // Récupérer les valeurs des champs
                String numeroVol = numeroVolComboBox.getValue();
                int idVol = serviceVols.getIdVolByNumero(numeroVol);
                int idEtudiant = 1; // À remplacer par l'ID de l'étudiant connecté
                int nombrePlaces = placesSpinner.getValue();

                // Récupérer le prix unitaire du vol
                double prixUnitaire = serviceVols.getPrixVolById(idVol);

                // Calculer simplement le prix total (prix unitaire × nombre de places)
                double prixTotal = prixUnitaire * nombrePlaces;

                String classe = classeComboBox.getValue();
                String typeBagage = bagageComboBox.getValue();
                String commentaires = commentairesArea.getText();

                // Générer une référence unique pour la réservation
                String reference = generateReference();

                // Créer l'objet ReservationVol
                ReservationVol reservation = new ReservationVol(
                        0, // ID sera généré par la base de données
                        idVol,
                        idEtudiant,
                        LocalDateTime.now(),
                        nombrePlaces,
                        prixTotal,
                        "En attente", // Statut par défaut
                        classe,
                        typeBagage,
                        commentaires,
                        reference
                );

                // Ajouter la réservation à la base de données
                serviceReservationVol.ajouterReservation(reservation);

                // Récupérer les détails du vol pour l'email
                Vols vol = serviceVols.getVolById(idVol);

                // Get email from the field
                String emailToUse = emailField.getText().trim();

                // Envoyer l'email de confirmation dans un thread séparé pour ne pas bloquer l'interface utilisateur
                new Thread(() -> {
                    boolean emailSent = EmailUtils.sendReservationConfirmation(emailToUse, reservation, vol);

                    // Mettre à jour l'interface utilisateur dans le thread JavaFX
                    javafx.application.Platform.runLater(() -> {
                        // Afficher un message de succès
                        if (emailSent) {
                            showAlert(Alert.AlertType.INFORMATION, "Succès",
                                    "La réservation a été ajoutée avec succès ! Un email de confirmation a été envoyé à " + emailToUse);
                        } else {
                            showAlert(Alert.AlertType.INFORMATION, "Succès",
                                    "La réservation a été ajoutée avec succès ! Cependant, l'email de confirmation n'a pas pu être envoyé.");
                        }

                        // Fermer la fenêtre
                        closeWindow();
                    });
                }).start();

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur",
                        "Une erreur s'est produite lors de l'ajout de la réservation : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    public void setVol(Vols vol) {
        if (vol != null) {
            // Sélectionner le numéro de vol dans la ComboBox
            numeroVolComboBox.setValue(vol.getNumeroVol());

            // Mettre à jour le prix total
            updatePrixTotal();
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();
        
        // Check if email is empty or invalid only if it's editable (not set from session)
        if (emailField.isEditable()) {
            if (emailField.getText() == null || emailField.getText().trim().isEmpty()) {
                errors.append("- Veuillez saisir un email pour la confirmation.\n");
            } else if (!emailField.getText().matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                errors.append("- L'adresse email saisie est invalide.\n");
            }
        }

        if (numeroVolComboBox.getValue() == null) {
            errors.append("- Veuillez sélectionner un numéro de vol.\n");
        }

        if (classeComboBox.getValue() == null) {
            errors.append("- Veuillez sélectionner une classe.\n");
        }

        if (bagageComboBox.getValue() == null) {
            errors.append("- Veuillez sélectionner un type de bagage.\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Champs invalides", errors.toString());
            return false;
        }

        return true;
    }

    private String generateReference() {
        // Générer une référence unique pour la réservation
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder reference = new StringBuilder("REF-");
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * chars.length());
            reference.append(chars.charAt(index));
        }
        return reference.toString();
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