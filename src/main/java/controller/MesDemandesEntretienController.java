package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import utils.MyDatabase;
import utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListCell;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.LocalTime;
import utils.MailUtil;

public class MesDemandesEntretienController {
    @FXML private ListView<DemandeEntretien> demandesListView;
    @FXML private Button fermerButton;

    @FXML
    public void initialize() {
        demandesListView.setCellFactory(lv -> new ListCell<DemandeEntretien>() {
            @Override
            protected void updateItem(DemandeEntretien demande, boolean empty) {
                super.updateItem(demande, empty);
                if (empty || demande == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox infoBox = new VBox(2);
                    infoBox.getChildren().addAll(
                        new Label("Domaine: " + demande.getDomaine()),
                        new Label("Date Demande: " + demande.getDateDemande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                        new Label("Date Souhaitée: " + demande.getDateSouhaitee().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                        new Label("Heure: " + demande.getHeureSouhaitee().format(DateTimeFormatter.ofPattern("HH:mm"))),
                        new Label("Statut: " + demande.getStatut()),
                        new Label("Offre: " + demande.getOffre()),
                        new Label("Type d'entretien: " + demande.getTypeEntretien()),
                        new Label("Expert : " + demande.getExpertName())
                    );
                    setGraphic(infoBox);
                    setText(null);
                }
            }
        });
        loadDemandes();
        demandesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            // No action needed, objet removed
        });
    }

    private void loadDemandes() {
        try {
            String email = SessionManager.getInstance().getUserEmail();
            int userId = getUserIdByEmail(email);
            String query = "SELECT d.*, e.nom_expert, e.prenom_expert FROM demandes_entretien d " +
                           "LEFT JOIN expert e ON d.id_expert = e.id_expert WHERE d.id_user = ? ORDER BY date_demande DESC";
            try (Connection con = MyDatabase.getInstance().getCnx();
                 PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    ObservableList<DemandeEntretien> demandesList = FXCollections.observableArrayList();
                    while (rs.next()) {
                        String expertName = "Pas selectionné";
                        if (rs.getString("nom_expert") != null) {
                            expertName = rs.getString("nom_expert") + " " + rs.getString("prenom_expert");
                        }
                        demandesList.add(new DemandeEntretien(
                            rs.getInt("id_demande"),
                            rs.getInt("id_user"),
                            rs.getString("domaine"),
                            rs.getDate("date_demande").toLocalDate(),
                            rs.getDate("date_souhaitee").toLocalDate(),
                            rs.getTime("heure_souhaitee").toLocalTime(),
                            rs.getString("objet"),
                            rs.getString("statut"),
                            rs.getString("offre"),
                            rs.getInt("id_expert"),
                            expertName,
                            rs.getString("type_entretien")
                        ));
                    }
                    demandesListView.setItems(demandesList);
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des demandes: " + e.getMessage());
        }
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

    @FXML
    private void fermer() {
        Stage stage = (Stage) fermerButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void sendStyledEmail(String to, String subject, String userName, String date, String heure) throws javax.mail.MessagingException {
        String htmlBody = "" +
                "<div style='font-family: Arial, sans-serif; background: #f8f9fa; padding: 24px;'>" +
                "  <div style='background: #3454d1; color: white; padding: 16px 24px; border-radius: 8px 8px 0 0; font-size: 22px; font-weight: bold;'>Confirmation de votre entretien</div>" +
                "  <div style='background: white; padding: 24px; border-radius: 0 0 8px 8px; box-shadow: 0 2px 8px #e0e0e0;'>" +
                "    <p>Bonjour <b>" + userName + "</b>,</p>" +
                "    <p>Votre demande d'entretien pour le <b>" + date + "</b> à <b>" + heure + "</b> a été <span style='color: #28a745; font-weight: bold;'>acceptée</span>.</p>" +
                "    <p style='margin-top: 24px;'>Merci de vous présenter à l'heure prévue.<br/>Cordialement,<br/><b>L'équipe Gradaway</b></p>" +
                "  </div>" +
                "</div>";
        utils.MailUtil.sendMail(to, subject, htmlBody, true);
    }

    public static class DemandeEntretien {
        private final int idDemande;
        private final int idUser;
        private final String domaine;
        private final LocalDate dateDemande;
        private final LocalDate dateSouhaitee;
        private final LocalTime heureSouhaitee;
        private final String objet;
        private final String statut;
        private final String offre;
        private final int idExpert;
        private final String expertName;
        private final String typeEntretien;

        public DemandeEntretien(int idDemande, int idUser, String domaine,
                               LocalDate dateDemande, LocalDate dateSouhaitee,
                               LocalTime heureSouhaitee, String objet, String statut, 
                               String offre, int idExpert, String expertName, String typeEntretien) {
            this.idDemande = idDemande;
            this.idUser = idUser;
            this.domaine = domaine;
            this.dateDemande = dateDemande;
            this.dateSouhaitee = dateSouhaitee;
            this.heureSouhaitee = heureSouhaitee;
            this.objet = objet;
            this.statut = statut;
            this.offre = offre;
            this.idExpert = idExpert;
            this.expertName = expertName;
            this.typeEntretien = typeEntretien;
        }

        public int getIdDemande() { return idDemande; }
        public int getIdUser() { return idUser; }
        public String getDomaine() { return domaine; }
        public LocalDate getDateDemande() { return dateDemande; }
        public LocalDate getDateSouhaitee() { return dateSouhaitee; }
        public LocalTime getHeureSouhaitee() { return heureSouhaitee; }
        public String getObjet() { return objet; }
        public String getStatut() { return statut; }
        public String getOffre() { return offre; }
        public int getIdExpert() { return idExpert; }
        public String getExpertName() { return expertName; }
        public String getTypeEntretien() { return typeEntretien; }
    }
} 