package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Services.ServiceDemandeEntretien;
import utils.MyDatabase;
import utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class DemanderEntretienController {
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<String> domaineComboBox;
    @FXML
    private DatePicker dateSouhaitee;
    @FXML
    private ComboBox<String> heureSouhaiteeComboBox;
    @FXML
    private ComboBox<String> typeEntretienComboBox;
    @FXML
    private TextArea objetTextArea;
    @FXML
    private Button soumettreButton;
    @FXML
    private Button annulerButton;
    @FXML
    private ComboBox<String> offreComboBox;
    @FXML
    private Button voirDemandesButton;

    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    private final ServiceDemandeEntretien serviceDemandeEntretien = new ServiceDemandeEntretien();

    @FXML
    public void initialize() {
        // Auto-fill email field with logged-in user's email
        String loggedInEmail = SessionManager.getInstance().getUserEmail();
        if (loggedInEmail != null && !loggedInEmail.isEmpty()) {
            emailField.setText(loggedInEmail);
            emailField.setEditable(false);
        }
        // Fill domain dropdown from candidature table
        try {
            java.util.List<String> domaines = serviceDemandeEntretien.recupererDomaines();
            domaineComboBox.getItems().addAll(domaines);
            if (!domaines.isEmpty()) {
                domaineComboBox.setValue(domaines.get(0));
            }
        } catch (SQLException e) {
            System.err.println("Error loading domaines: " + e.getMessage());
            // Fallback to default values if needed
            domaineComboBox.getItems().addAll(
                "Java", "Python", "C++", "JavaScript", "SQL",
                "Web Development", "Mobile Development", "Data Science",
                "Machine Learning", "DevOps", "Cloud Computing"
            );
        }
        // Set heureSouhaiteeComboBox values with consistent format
        heureSouhaiteeComboBox.getItems().addAll(
            "08h00-10h00",
            "14h00-16h00",
            "20h00-22h00"
        );
        // Set type d'entretien values
        typeEntretienComboBox.getItems().addAll("Présentiel", "En ligne");
        // Set today as minimum date
        dateSouhaitee.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) < 0);
            }
        });
        offreComboBox.getItems().addAll("licence", "master", "doctorat", "echange universitaire");
        // Add focus/blur listeners for blue border
        addFieldFocusListeners();
    }

    private void addFieldFocusListeners() {
        addFocusListener(emailField);
        addFocusListener(domaineComboBox);
        addFocusListener(dateSouhaitee);
        addFocusListener(heureSouhaiteeComboBox);
        addFocusListener(typeEntretienComboBox);
        addFocusListener(offreComboBox);
    }
    private void addFocusListener(Control field) {
        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                field.setStyle("-fx-border-color: #2196F3; -fx-border-width: 2px; -fx-background-color: white;");
            } else {
                field.setStyle("-fx-background-color: white;");
            }
        });
    }

    @FXML
    public void soumettreDemande() {
        if (!validateFields()) {
            return;
        }
        try {
            String email = emailField.getText().trim();
            int userId = getUserIdByEmail(email);
            if (userId == -1) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'utilisateur avec cet email n'existe pas");
                return;
            }
            if (hasPendingRequest(userId)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Vous avez déjà une demande en attente");
                return;
            }
            serviceDemandeEntretien.ajouter(
                userId,
                domaineComboBox.getValue(),
                dateSouhaitee.getValue(),
                parseHeureFromComboBox(),
                "", // No objet field
                offreComboBox.getValue(),
                typeEntretienComboBox.getValue()
            );
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre demande a été soumise avec succès");
            // Do not close the window
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la soumission de la demande: " + e.getMessage());
        }
    }

    private LocalTime parseHeureFromComboBox() {
        String selected = heureSouhaiteeComboBox.getValue();
        if (selected == null) return null;
        
        try {
            // Parse the start time from the range (e.g., "08h00-10h00" -> 08:00)
            String start = selected.split("-")[0].trim();
            // Convert "14h" to "14:00" and "14h00" to "14:00"
            start = start.replace("h", ":");
            if (!start.contains(":")) {
                start += ":00";
            }
            // Ensure HH:mm format
            if (start.length() == 4) {
                start = "0" + start;
            }
            return LocalTime.parse(start);
        } catch (Exception e) {
            System.err.println("Error parsing time: " + e.getMessage());
            return null;
        }
    }

    private boolean validateFields() {
        boolean valid = true;
        if (emailField.getText().trim().isEmpty()) {
            emailField.setStyle("-fx-border-color: #e53935; -fx-border-width: 2px; -fx-background-color: white;");
            valid = false;
        }
        if (domaineComboBox.getValue() == null) {
            domaineComboBox.setStyle("-fx-border-color: #e53935; -fx-border-width: 2px; -fx-background-color: white;");
            valid = false;
        }
        if (dateSouhaitee.getValue() == null) {
            dateSouhaitee.setStyle("-fx-border-color: #e53935; -fx-border-width: 2px; -fx-background-color: white;");
            valid = false;
        }
        if (heureSouhaiteeComboBox.getValue() == null) {
            heureSouhaiteeComboBox.setStyle("-fx-border-color: #e53935; -fx-border-width: 2px; -fx-background-color: white;");
            valid = false;
        }
        if (typeEntretienComboBox.getValue() == null) {
            typeEntretienComboBox.setStyle("-fx-border-color: #e53935; -fx-border-width: 2px; -fx-background-color: white;");
            valid = false;
        }
        if (offreComboBox.getValue() == null) {
            offreComboBox.setStyle("-fx-border-color: #e53935; -fx-border-width: 2px; -fx-background-color: white;");
            valid = false;
        }
        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            emailField.setStyle("-fx-border-color: #e53935; -fx-border-width: 2px; -fx-background-color: white;");
            valid = false;
        }
        return valid;
    }

    private int getUserIdByEmail(String email) throws SQLException {
        String query = "SELECT id FROM user WHERE email = ?";
        try (Connection con = MyDatabase.getInstance().getCnx();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1;
    }

    private boolean hasPendingRequest(int idUser) throws SQLException {
        String query = "SELECT COUNT(*) FROM demandes_entretien WHERE id_user = ? AND statut = 'en attente'";
        try (Connection con = MyDatabase.getInstance().getCnx();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @FXML
    public void annuler(javafx.event.ActionEvent event) {
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void voirDemandes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MesDemandesEntretien.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mes Demandes d'Entretien");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre des demandes: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 