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
import java.io.IOException;
import javafx.event.ActionEvent;

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
        demandesListView.setPlaceholder(new Label("Aucune demandes n'as été envoyer"));
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
                        new Label("Offre: " + demande.getOffre()),
                        new Label("Type d'entretien: " + demande.getTypeEntretien()),
                        new Label("Expert : " + demande.getExpert())
                    );
                    Button accepterBtn = new Button("Accepter");
                    accepterBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 4 12;");
                    accepterBtn.setOnAction(e -> {
                        if (demande.getIdExpert() <= 0) {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Vous devez ajouter un expert à cette demande");
                        } else {
                            accepterDemande(demande);
                        }
                    });
                    Button refuserBtn = new Button("Refuser");
                    refuserBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 4 12;");
                    refuserBtn.setOnAction(e -> refuserDemande(demande));
                    Button affecterExpertBtn = new Button("Affecter expert");
                    affecterExpertBtn.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 4 12;");
                    affecterExpertBtn.setOnAction(e -> affecterExpert(demande));
                    row.getChildren().addAll(infoBox, accepterBtn, refuserBtn, affecterExpertBtn);
                    setGraphic(row);
                    setText(null);
                }
            }
        });
        loadDemandes();
    }

    private void loadDemandes() {
        try {
            String query = "SELECT d.*, u.nom, u.prenom, e.nom_expert, e.prenom_expert " +
                           "FROM demandes_entretien d " +
                           "JOIN user u ON d.id_user = u.id " +
                           "LEFT JOIN expert e ON d.id_expert = e.id_expert " +
                           "ORDER BY date_demande DESC";
            try (Connection con = MyDatabase.getInstance().getCnx();
                 Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(query)) {
                ObservableList<DemandeEntretien> demandesList = FXCollections.observableArrayList();
                while (rs.next()) {
                    String expertName = "Pas selectionné";
                    if (rs.getString("nom_expert") != null) {
                        expertName = rs.getString("nom_expert") + " " + rs.getString("prenom_expert");
                    }
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
                        rs.getString("offre"),
                        rs.getInt("id_expert"),
                        expertName,
                        rs.getString("type_entretien")
                    ));
                }
                demandesListView.setItems(demandesList);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des demandes: " + e.getMessage());
        }
    }

    private String getExpertNameById(int idExpert) {
        String name = "Pas selectionné";
        String query = "SELECT nom_expert, prenom_expert FROM expert WHERE id_expert = ?";
        try (Connection con = MyDatabase.getInstance().getCnx();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idExpert);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("nom_expert") + " " + rs.getString("prenom_expert");
                }
            }
        } catch (SQLException e) {
            // ignore
        }
        return name;
    }

    private void accepterDemande(DemandeEntretien demande) {
        try {
            serviceDemandeEntretien.accepterDemande(demande.getIdDemande());
            // Send email notification
            String userEmail = getUserEmailById(demande.getIdUser());
            String userName = demande.getNomUser();
            String subject = "Confirmation de votre entretien avec Gradaway";
            String date = demande.getDateSouhaitee().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String heure = demande.getHeureSouhaitee().format(DateTimeFormatter.ofPattern("HH:mm"));
            try {
                sendStyledEmail(userEmail, subject, userName, date, heure, demande.getTypeEntretien());
            } catch (MessagingException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur Email", "L'email n'a pas pu être envoyé: " + e.getMessage());
            }
            showAlert(Alert.AlertType.INFORMATION, "Succès", "La demande a été acceptée et un email a été envoyé");
            loadDemandes();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'acceptation de la demande: " + e.getMessage());
        }
    }

    private void sendStyledEmail(String to, String subject, String userName, String date, String heure, String typeEntretien) throws javax.mail.MessagingException {
        String locationInfo = "";
        if ("Présentiel".equals(typeEntretien)) {
            String locationLink = utils.MailUtil.generateLocationLink();
            locationInfo = "<p>Localisation : <a href='" + locationLink + "'>Cliquez ici pour voir le lieu de l'entretien</a></p>";
        } else if ("En ligne".equals(typeEntretien)) {
            String zoomLink = utils.MailUtil.generateZoomLink();
            locationInfo = "<p>Lien Zoom : <a href='" + zoomLink + "'>Cliquez ici pour rejoindre la réunion</a></p>";
        }

        String htmlBody = "" +
                "<div style='font-family: Arial, sans-serif; background: #f8f9fa; padding: 24px;'>" +
                "  <div style='background: #3454d1; color: white; padding: 16px 24px; border-radius: 8px 8px 0 0; font-size: 22px; font-weight: bold;'>Confirmation de votre entretien</div>" +
                "  <div style='background: white; padding: 24px; border-radius: 0 0 8px 8px; box-shadow: 0 2px 8px #e0e0e0;'>" +
                "    <p>Bonjour <b>" + userName + "</b>,</p>" +
                "    <p>Votre demande d'entretien pour le <b>" + date + "</b> à <b>" + heure + "</b> a été <span style='color: #28a745; font-weight: bold;'>acceptée</span>.</p>" +
                "    <p>Type d'entretien : <b>" + typeEntretien + "</b></p>" +
                locationInfo +
                "    <p style='margin-top: 24px;'>Merci de vous présenter à l'heure prévue.<br/>Cordialement,<br/><b>L'équipe Gradaway</b></p>" +
                "  </div>" +
                "</div>";
        utils.MailUtil.sendMail(to, subject, htmlBody, true);
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

    private void affecterExpert(DemandeEntretien demande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffecterExpert.fxml"));
            Parent root = loader.load();
            AffecterExpertController controller = loader.getController();
            controller.setDemandeEntretien(demande);
            
            Stage stage = new Stage();
            stage.setTitle("Affecter un Expert");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Update the list when the expert assignment window is closed
            stage.setOnHidden(e -> loadDemandes());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire d'affectation: " + e.getMessage());
        }
    }

    @FXML
    private void DemandeEntretien() {
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Accueilvol.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Vols");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            // showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());


        }
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
        private final int idExpert;
        private final String expertName;
        private final String typeEntretien;

        public DemandeEntretien(int idDemande, int idUser, String nomUser, String domaine,
                               LocalDate dateDemande, LocalDate dateSouhaitee,
                               LocalTime heureSouhaitee, String objet, String statut, 
                               String offre, int idExpert, String expertName, String typeEntretien) {
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
            this.idExpert = idExpert;
            this.expertName = expertName;
            this.typeEntretien = typeEntretien;
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
        public int getIdExpert() { return idExpert; }
        public String getExpert() { return expertName; }
        public String getTypeEntretien() { return typeEntretien; }
    }
} 