package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Services.ServiceDemandeEntretien;
import utils.MyDatabase;

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
    private TextField heureSouhaitee;
    @FXML
    private TextArea objetTextArea;
    @FXML
    private Button soumettreButton;
    @FXML
    private Button annulerButton;
    @FXML
    private ComboBox<String> offreComboBox;

    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    private final ServiceDemandeEntretien serviceDemandeEntretien = new ServiceDemandeEntretien();

    @FXML
    public void initialize() {
        // Fill domain dropdown with common programming domains
        domaineComboBox.getItems().addAll(
            "Java", "Python", "C++", "JavaScript", "SQL",
            "Web Development", "Mobile Development", "Data Science",
            "Machine Learning", "DevOps", "Cloud Computing"
        );

        // Set today as minimum date
        dateSouhaitee.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(LocalDate.now()) < 0);
            }
        });

        offreComboBox.getItems().addAll("licence", "master", "doctorat", "echange universitaire");
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
                LocalTime.parse(heureSouhaitee.getText().trim()),
                objetTextArea.getText().trim(),
                offreComboBox.getValue()
            );
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Votre demande a été soumise avec succès");
            annuler(null);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la soumission de la demande: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        if (emailField.getText().trim().isEmpty() ||
            domaineComboBox.getValue() == null ||
            dateSouhaitee.getValue() == null ||
            heureSouhaitee.getText().trim().isEmpty() ||
            objetTextArea.getText().trim().isEmpty() ||
            offreComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs");
            return false;
        }
        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez entrer un email valide");
            return false;
        }

        if (!TIME_PATTERN.matcher(heureSouhaitee.getText().trim()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le format de l'heure doit être HH:mm");
            return false;
        }

        return true;
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 