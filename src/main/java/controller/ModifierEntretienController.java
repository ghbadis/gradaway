package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import entities.Entretien;
import entities.Expert;
import entities.CandidatureDisplay;
import Services.ServiceEntretien;
import Services.ServiceExpert;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.regex.Pattern;
import utils.MyDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class ModifierEntretienController {
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
    private ComboBox<CandidatureDisplay> candidatureComboBox;
    @FXML
    private Button modifierButton;
    @FXML
    private Button annulerButton;

    private final ServiceEntretien serviceEntretien = new ServiceEntretien();
    private final ServiceExpert serviceExpert = new ServiceExpert();
    private Entretien entretienAModifier;

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
            typeEntretien.getItems().addAll("présentiel", "en ligne");
            offreComboBox.getItems().addAll("licence", "master", "doctorat", "echange universitaire");
            heureEntretienComboBox.getItems().addAll("08h00-10h00", "14h00-16h00", "20h00-22h00");
            modifierButton.setOnAction(event -> modifierEntretien());
            annulerButton.setOnAction(event -> fermerFenetre());

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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des experts: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des candidatures: " + e.getMessage());
        }
    }

    public void setEntretien(Entretien entretien) {
        this.entretienAModifier = entretien;
        for (Expert expert : expertComboBox.getItems()) {
            if (expert.getId_expert() == entretien.getId_expert()) {
                expertComboBox.setValue(expert);
                break;
            }
        }
        dateEntretien.setValue(entretien.getDate_entretien());
        String timeRange = getTimeRangeForHour(entretien.getHeure_entretien());
        heureEntretienComboBox.setValue(timeRange);
        typeEntretien.setValue(entretien.getType_entretien());
        offreComboBox.setValue(entretien.getOffre());
    }

    private void modifierEntretien() {
        if (!validateFields()) {
            return;
        }
        try {
            int idCandidature = candidatureComboBox.getValue() != null ? candidatureComboBox.getValue().getIdC() : -1;
            Entretien entretienModifie = new Entretien(
                entretienAModifier.getId_entretien(),
                expertComboBox.getValue().getId_expert(),
                entretienAModifier.getId_user(),
                idCandidature,
                dateEntretien.getValue(),
                parseHeureFromComboBox(),
                "en attente",
                typeEntretien.getValue(),
                offreComboBox.getValue()
            );
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
        if (expertComboBox.getValue() == null ||
            dateEntretien.getValue() == null ||
            heureEntretienComboBox.getValue() == null ||
            typeEntretien.getValue() == null ||
            offreComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires");
            return false;
        }
        return true;
    }

    private LocalTime parseHeureFromComboBox() {
        String selected = heureEntretienComboBox.getValue();
        if (selected == null) return null;
        String start = selected.split("-")[0].replace("h", ":");
        if (start.length() == 4) start = "0" + start;
        return LocalTime.parse(start);
    }

    private String getTimeRangeForHour(java.time.LocalTime heure) {
        if (heure.isAfter(LocalTime.of(7, 59)) && heure.isBefore(LocalTime.of(10, 1))) return "08h00-10h00";
        if (heure.isAfter(LocalTime.of(13, 59)) && heure.isBefore(LocalTime.of(16, 1))) return "14h00-16h00";
        if (heure.isAfter(LocalTime.of(19, 59)) && heure.isBefore(LocalTime.of(22, 1))) return "20h00-22h00";
        return "08h00-10h00";
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onvolsAdminButtonClick(ActionEvent actionEvent) {
        showAlert(Alert.AlertType.INFORMATION, "Information", "La fonctionnalité des vols sera bientôt disponible.");
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion: " + e.getMessage());
        }
    }
}
