package controller;

import entities.Entretien;
import entities.Expert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import Services.ServiceEntretien;
import Services.ServiceExpert;
import javafx.stage.Stage;
import entities.CandidatureDisplay;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.time.format.DateTimeFormatter;
import utils.MyDatabase;
import java.io.IOException;

public class CreerEntretienController {

    @FXML
    private ComboBox<Expert> expertComboBox;

    @FXML
    private DatePicker dateEntretien;

    @FXML
    private ComboBox<String> heureEntretienComboBox;

    @FXML
    private ComboBox<String> typeEntretien;

    @FXML
    private ComboBox<String> offreComboBox;

    @FXML
    private Button creerButton;

    @FXML
    private Button annulerButton;

    @FXML
    private ComboBox<CandidatureDisplay> candidatureComboBox;

    private final ServiceEntretien serviceEntretien = new ServiceEntretien();
    private final ServiceExpert serviceExpert = new ServiceExpert();

    @FXML
    public void initialize() {
        try {
            // Set date restrictions
            LocalDate today = LocalDate.now();
            LocalDate maxDate = today.plusYears(1);
            dateEntretien.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(empty || date.isBefore(today) || date.isAfter(maxDate));
                }
            });

            List<Expert> experts = serviceExpert.recuperer();
            expertComboBox.getItems().addAll(experts);
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
            // Fill type and offre dropdowns
            typeEntretien.getItems().addAll("présentiel", "en ligne");
            offreComboBox.getItems().addAll("licence", "master", "doctorat", "echange universitaire");
            // Fill heureEntretienComboBox
            heureEntretienComboBox.getItems().addAll("08h00-10h00", "14h00-16h00", "20h00-22h00");

            // Fetch candidatures and users
            Connection con = MyDatabase.getInstance().getCnx();
            String sql = "SELECT c.id_c, u.nom, u.prenom, c.domaine FROM candidature c JOIN user u ON c.user_id = u.id";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                candidatureComboBox.getItems().add(new CandidatureDisplay(
                    rs.getInt("id_c"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("domaine")
                ));
            }
        } catch (SQLException e) {
            showError("Erreur de chargement", e.getMessage());
        }
    }

    @FXML
    public void creerEntretien(ActionEvent event) {
        try {
            if (expertComboBox.getValue() == null ||
                dateEntretien.getValue() == null ||
                heureEntretienComboBox.getValue() == null ||
                typeEntretien.getValue() == null ||
                offreComboBox.getValue() == null ||
                candidatureComboBox.getValue() == null) {
                showError("Champs manquants", "Veuillez remplir tous les champs.");
                return;
            }
            int idExpert = expertComboBox.getValue().getId_expert();
            int idUser = getCurrentUserId(); // Implement this method as needed
            int idCandidature = candidatureComboBox.getValue().getIdC();
            LocalDate date = dateEntretien.getValue();
            String selectedTimeRange = heureEntretienComboBox.getValue();
            String start = selectedTimeRange.split("-")[0].replace("h", ":");
            if (start.length() == 4) start = "0" + start;
            LocalTime heure = LocalTime.parse(start);
            String type = typeEntretien.getValue();
            String offre = offreComboBox.getValue();
            Entretien entretien = new Entretien(idExpert, idUser, idCandidature, date, heure, "en attente", type, offre);
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

    // Dummy method for user ID, replace with actual logic
    private int getCurrentUserId() {
        // TODO: Replace with actual logic to get the current user ID
        return 1;
    }

    public void preRemplirDemande(ListeDemandesEntretienController.DemandeEntretien demande) {
        // Set the date and time from the request
        dateEntretien.setValue(demande.getDateSouhaitee());
        String timeRange = getTimeRangeForHour(demande.getHeureSouhaitee());
        heureEntretienComboBox.setValue(timeRange);
        // Set default values for other fields
        typeEntretien.setValue("présentiel");
        // The expert will need to be selected manually as it's not part of the request
    }

    private String getTimeRangeForHour(java.time.LocalTime heure) {
        if (heure.isAfter(LocalTime.of(7, 59)) && heure.isBefore(LocalTime.of(10, 1))) return "08h00-10h00";
        if (heure.isAfter(LocalTime.of(13, 59)) && heure.isBefore(LocalTime.of(16, 1))) return "14h00-16h00";
        if (heure.isAfter(LocalTime.of(19, 59)) && heure.isBefore(LocalTime.of(22, 1))) return "20h00-22h00";
        return "08h00-10h00";
    }

    @FXML
    public void onAccueilAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AcceuilAdmin.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil Admin");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onUserAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminUser.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Utilisateurs");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void ondossierAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDossier.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Dossiers");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onuniversiteAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recupereruniversite.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Universités");
            stage.setMinWidth(1256);
            stage.setMinHeight(702);
            stage.setResizable(true);
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onentretienAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gestionnaire.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Entretiens");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onevenementAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_evenement.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Événements");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onhebergementAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFoyer.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Foyers");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onrestaurantAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterRestaurant.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Restaurants");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onvolsAdminButtonClick(ActionEvent actionEvent) {
        showInfo("Information", "La fonctionnalité des vols sera bientôt disponible.");
    }

    @FXML
    public void onlogoutAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - GradAway");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur", "Erreur lors de la déconnexion: " + e.getMessage());
        }
    }
}
