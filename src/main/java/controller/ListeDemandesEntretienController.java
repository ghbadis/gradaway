package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import Services.ServiceDemandeEntretien;
import utils.MyDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListCell;
import utils.MailUtil;
import javax.mail.MessagingException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ListeDemandesEntretienController {
    @FXML
    private TableView<DemandeEntretien> demandesTable;
    @FXML
    private TableColumn<DemandeEntretien, Integer> idDemandeColumn;
    @FXML
    private TableColumn<DemandeEntretien, Integer> idUserColumn;
    @FXML
    private TableColumn<DemandeEntretien, String> nomUserColumn;
    @FXML
    private TableColumn<DemandeEntretien, String> domaineColumn;
    @FXML
    private TableColumn<DemandeEntretien, String> dateDemandeColumn;
    @FXML
    private TableColumn<DemandeEntretien, String> dateSouhaiteeColumn;
    @FXML
    private TableColumn<DemandeEntretien, String> heureSouhaiteeColumn;
    @FXML
    private TableColumn<DemandeEntretien, String> statutColumn;
    @FXML
    private TextArea objetTextArea;
    @FXML
    private Button accepterButton;
    @FXML
    private Button refuserButton;
    @FXML
    private Button planifierButton;
    @FXML
    private Button fermerButton;
    @FXML
    private ListView<DemandeEntretien> demandesListView;

    private final ServiceDemandeEntretien serviceDemandeEntretien = new ServiceDemandeEntretien();
    private final ObservableList<DemandeEntretien> demandesList = FXCollections.observableArrayList();

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
                    HBox row = new HBox(10);
                    row.setStyle("-fx-padding: 10 0 10 10; -fx-background-color: #fff;");
                    VBox infoBox = new VBox(2);
                    infoBox.getChildren().addAll(
                        new Label("Nom: " + demande.getNomUser()),
                        new Label("Domaine: " + demande.getDomaine()),
                        new Label("Date Demande: " + demande.getDateDemande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                        new Label("Date Souhaitée: " + demande.getDateSouhaitee().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                        new Label("Heure: " + demande.getHeureSouhaitee().format(DateTimeFormatter.ofPattern("HH:mm"))),
                        new Label("Statut: " + demande.getStatut()),
                        new Label("Offre: " + demande.getOffre())
                    );
                    Button accepterBtn = new Button("Accepter");
                    accepterBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 4 12;");
                    accepterBtn.setOnAction(e -> accepterDemande(demande));
                    Button refuserBtn = new Button("Refuser");
                    refuserBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 4 12;");
                    refuserBtn.setOnAction(e -> refuserDemande(demande));
                    row.getChildren().addAll(infoBox, accepterBtn, refuserBtn);
                    setGraphic(row);
                    setText(null);
                }
            }
        });
        loadDemandes();
        demandesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                objetTextArea.setText(newSelection.getObjet());
            }
        });
    }

    private void loadDemandes() {
        try {
            String query = "SELECT d.*, u.nom, u.prenom FROM demandes_entretien d JOIN user u ON d.id_user = u.id ORDER BY date_demande DESC";
            try (Connection con = MyDatabase.getInstance().getCnx();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(query)) {
                ObservableList<DemandeEntretien> demandesList = FXCollections.observableArrayList();
                while (rs.next()) {
                    demandesList.add(new DemandeEntretien(
                        rs.getInt("id_demande"),
                        rs.getInt("id_user"),
                        rs.getString("nom") + " " + rs.getString("prenom"),
                        rs.getString("domaine"),
                        rs.getDate("date_demande").toLocalDate(),
                        rs.getDate("date_souhaitee").toLocalDate(),
                        rs.getTime("heure_souhaitee").toLocalTime(),
                        rs.getString("objet"),
                        rs.getString("statut"),
                        rs.getString("offre")
                    ));
                }
                demandesListView.setItems(demandesList);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des demandes: " + e.getMessage());
        }
    }

    private void accepterDemande(DemandeEntretien demande) {
        try {
            serviceDemandeEntretien.accepterDemande(demande.getIdDemande());
            // Send email notification
            String userEmail = getUserEmailById(demande.getIdUser());
            String userName = demande.getNomUser();
            String subject = "Confirmation de votre entretien avec Gradaway";
            String body = "Bonjour " + userName + ",\n\n" +
                "Votre demande d'entretien pour le " + demande.getDateSouhaitee().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " à " + demande.getHeureSouhaitee().format(DateTimeFormatter.ofPattern("HH:mm")) +
                " a été acceptée.\n\nMerci de vous présenter à l'heure prévue.\n\nCordialement,\nL'équipe Gradaway";
            try {
                MailUtil.sendMail(userEmail, subject, body);
            } catch (MessagingException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur Email", "L'email n'a pas pu être envoyé: " + e.getMessage());
            }
            showAlert(Alert.AlertType.INFORMATION, "Succès", "La demande a été acceptée et un email a été envoyé");
            loadDemandes();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'acceptation de la demande: " + e.getMessage());
        }
    }

    private String getUserEmailById(int idUser) {
        String email = null;
        try (Connection con = MyDatabase.getInstance().getCnx();
             PreparedStatement ps = con.prepareStatement("SELECT email FROM user WHERE id = ?")) {
            ps.setInt(1, idUser);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    email = rs.getString("email");
                }
            }
        } catch (SQLException e) {
            // Optionally log error
        }
        return email;
    }

    private void refuserDemande(DemandeEntretien demande) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette demande ?");
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                serviceDemandeEntretien.supprimerDemande(demande.getIdDemande());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "La demande a été supprimée");
                loadDemandes();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression de la demande: " + e.getMessage());
            }
        }
    }

    @FXML
    private void planifierEntretien() {
        DemandeEntretien demande = demandesTable.getSelectionModel().getSelectedItem();
        if (demande != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreerEntretien.fxml"));
                Parent root = loader.load();
                CreerEntretienController controller = loader.getController();
                // Pre-fill the form with request data
                controller.preRemplirDemande(demande);
                
                Stage stage = new Stage();
                stage.setTitle("Planifier l'Entretien");
                stage.setScene(new Scene(root));
                stage.show();
                
                // Update the list when the planning window is closed
                stage.setOnHidden(e -> loadDemandes());
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire: " + e.getMessage());
            }
        }
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

    public static class DemandeEntretien {
        private final int idDemande;
        private final int idUser;
        private final String nomUser;
        private final String domaine;
        private final LocalDate dateDemande;
        private final LocalDate dateSouhaitee;
        private final LocalTime heureSouhaitee;
        private final String objet;
        private final String statut;
        private final String offre;

        public DemandeEntretien(int idDemande, int idUser, String nomUser, String domaine,
                               LocalDate dateDemande, LocalDate dateSouhaitee,
                               LocalTime heureSouhaitee, String objet, String statut, String offre) {
            this.idDemande = idDemande;
            this.idUser = idUser;
            this.nomUser = nomUser;
            this.domaine = domaine;
            this.dateDemande = dateDemande;
            this.dateSouhaitee = dateSouhaitee;
            this.heureSouhaitee = heureSouhaitee;
            this.objet = objet;
            this.statut = statut;
            this.offre = offre;
        }

        public int getIdDemande() { return idDemande; }
        public int getIdUser() { return idUser; }
        public String getNomUser() { return nomUser; }
        public String getDomaine() { return domaine; }
        public LocalDate getDateDemande() { return dateDemande; }
        public LocalDate getDateSouhaitee() { return dateSouhaitee; }
        public LocalTime getHeureSouhaitee() { return heureSouhaitee; }
        public String getObjet() { return objet; }
        public String getStatut() { return statut; }
        public String getOffre() { return offre; }
    }
} 