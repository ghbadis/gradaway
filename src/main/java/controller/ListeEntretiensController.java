package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import entities.Entretien;
import Services.ServiceEntretien;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import Services.ServiceExpert;
import entities.Expert;
import java.util.Comparator;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import utils.MyDatabase;
import java.io.IOException;
import javafx.event.ActionEvent;

public class ListeEntretiensController {
    @FXML
    private ListView<Entretien> entretiensList;
    @FXML
    private Button ajouterButton;
    @FXML
    private Button fermerButton;
    @FXML
    private Button voirDemandesButton;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> dateFilterComboBox;

    private final ServiceEntretien serviceEntretien = new ServiceEntretien();
    private final ServiceExpert serviceExpert = new ServiceExpert();
    private ObservableList<Entretien> entretiensData = FXCollections.observableArrayList();
    private ObservableList<Entretien> allEntretiens = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        // Configurer les boutons
        ajouterButton.setOnAction(event -> ouvrirFenetreAjout());
        fermerButton.setOnAction(event -> fermerFenetre());
        voirDemandesButton.setOnAction(event -> ouvrirDemandesEntretien());

        // Configure date filter combobox
        dateFilterComboBox.getItems().addAll(
            "Ordre croissant",
            "Ordre décroissant"
        );
        dateFilterComboBox.setValue("Ordre croissant");
        dateFilterComboBox.setOnAction(e -> filterEntretiens());

        // Add search field listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterEntretiens());

        // Configurer la ListView
        entretiensList.setCellFactory(lv -> new ListCell<Entretien>() {
            @Override
            protected void updateItem(Entretien entretien, boolean empty) {
                super.updateItem(entretien, empty);
                if (empty || entretien == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    HBox row = new HBox(0);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setStyle("-fx-padding: 10 0 10 10; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-color: #fff;");

                    // Fetch candidature name and domaine
                    String candidatureName = "";
                    String candidatureDomaine = "";
                    try {
                        Connection con = MyDatabase.getInstance().getCnx();
                        String sql = "SELECT u.nom, u.prenom, c.domaine FROM candidature c JOIN user u ON c.user_id = u.id WHERE c.id_c = ?";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setInt(1, entretien.getId_candidature());
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            candidatureName = rs.getString("nom") + " " + rs.getString("prenom");
                            candidatureDomaine = rs.getString("domaine");
                        } else {
                            candidatureName = "-";
                            candidatureDomaine = "-";
                        }
                    } catch (Exception e) {
                        candidatureName = "-";
                        candidatureDomaine = "-";
                    }
                    Label candidatureLabel = new Label(candidatureName);
                    candidatureLabel.setMinWidth(180); candidatureLabel.setMaxWidth(180);
                    Label domaineLabel = new Label(candidatureDomaine);
                    domaineLabel.setMinWidth(160); domaineLabel.setMaxWidth(160);

                    // Get expert's full name
                    String expertName = "";
                    try {
                        Expert expert = serviceExpert.recuperer().stream()
                            .filter(e -> e.getId_expert() == entretien.getId_expert())
                            .findFirst().orElse(null);
                        if (expert != null) {
                            expertName = expert.getNom_expert() + " " + expert.getPrenom_expert();
                        }
                    } catch (Exception e) {
                        expertName = "Expert inconnu";
                    }
                    Label expertLabel = new Label(expertName);
                    expertLabel.setMinWidth(220); expertLabel.setMaxWidth(220);
                    Label dateLabel = new Label(entretien.getDate_entretien().format(dateFormatter));
                    dateLabel.setMinWidth(120); dateLabel.setMaxWidth(120);
                    Label plageHoraireLabel = new Label(getTimeRangeForHour(entretien.getHeure_entretien()));
                    plageHoraireLabel.setMinWidth(140); plageHoraireLabel.setMaxWidth(140);
                    Label typeLabel = new Label(entretien.getType_entretien());
                    typeLabel.setMinWidth(120); typeLabel.setMaxWidth(120);
                    Label offreLabel = new Label(entretien.getOffre());
                    offreLabel.setMinWidth(160); offreLabel.setMaxWidth(160);

                    // Spacer before actions
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Button modifierBtn = new Button("Modifier");
                    modifierBtn.setStyle("-fx-background-color: #0d47a1; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 4 18;");
                    modifierBtn.setOnAction(event -> ouvrirFenetreModification(entretien));

                    Button supprimerBtn = new Button("Supprimer");
                    supprimerBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 4 18;");
                    supprimerBtn.setOnAction(event -> supprimerEntretien(entretien));

                    row.getChildren().addAll(
                        candidatureLabel, domaineLabel, expertLabel, dateLabel, plageHoraireLabel, typeLabel, offreLabel, spacer, modifierBtn, supprimerBtn
                    );
                    setGraphic(row);
                    setText(null);

                    // Highlight on hover
                    row.setOnMouseEntered(e -> row.setStyle("-fx-padding: 10 0 10 10; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-color: #e3f0fa;"));
                    row.setOnMouseExited(e -> row.setStyle("-fx-padding: 10 0 10 10; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-color: #fff;"));
                }
            }
        });

        // Prevent selection from causing blanking
        entretiensList.setOnMousePressed(event -> entretiensList.getSelectionModel().clearSelection());

        // Charger les données
        chargerDonnees();
    }

    private void filterEntretiens() {
        String search = searchField.getText() == null ? "" : searchField.getText().toLowerCase();
        String dateFilter = dateFilterComboBox.getValue();

        // Filter by expert name OR candidature name
        ObservableList<Entretien> filteredList = allEntretiens.filtered(entretien -> {
            boolean match = false;
            try {
                // Expert name
                Expert expert = serviceExpert.recuperer().stream()
                    .filter(e -> e.getId_expert() == entretien.getId_expert())
                    .findFirst().orElse(null);
                String expertName = expert != null ? (expert.getNom_expert() + " " + expert.getPrenom_expert()).toLowerCase() : "";

                // Candidature name
                String candidatureName = "";
                try {
                    Connection con = MyDatabase.getInstance().getCnx();
                    String sql = "SELECT u.nom, u.prenom FROM candidature c JOIN user u ON c.user_id = u.id WHERE c.id_c = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, entretien.getId_candidature());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        candidatureName = (rs.getString("nom") + " " + rs.getString("prenom")).toLowerCase();
                    }
                } catch (Exception e) { /* ignore */ }

                match = expertName.contains(search) || candidatureName.contains(search);
            } catch (Exception e) {
                // ignore
            }
            return match;
        });

        // Sort by date
        if (dateFilter != null) {
            Comparator<Entretien> dateComparator = Comparator.comparing(Entretien::getDate_entretien);
            if (dateFilter.equals("Ordre décroissant")) {
                dateComparator = dateComparator.reversed();
            }
            // Create a new modifiable list for sorting
            List<Entretien> sortedList = new ArrayList<>(filteredList);
            sortedList.sort(dateComparator);
            filteredList = FXCollections.observableArrayList(sortedList);
        }

        entretiensData.setAll(filteredList);
        entretiensList.setItems(entretiensData);
    }

    private void chargerDonnees() {
        try {
            List<Entretien> entretiens = serviceEntretien.recuperer();
            allEntretiens.setAll(entretiens);
            entretiensData.setAll(allEntretiens);
            entretiensList.setItems(entretiensData);
            filterEntretiens(); // Apply initial sorting
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des entretiens: " + e.getMessage());
        }
    }

    private void ouvrirFenetreAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreerEntretien.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Entretien");
            stage.setScene(new Scene(root));
            stage.show();
            
            stage.setOnHidden(event -> chargerDonnees());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre d'ajout: " + e.getMessage());
        }
    }

    private void ouvrirFenetreModification(Entretien entretien) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierEntretien.fxml"));
            Parent root = loader.load();
            ModifierEntretienController controller = loader.getController();
            controller.setEntretien(entretien);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier un Entretien");
            stage.setScene(new Scene(root));
            stage.show();
            
            stage.setOnHidden(event -> chargerDonnees());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre de modification: " + e.getMessage());
        }
    }

    private void supprimerEntretien(Entretien entretien) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer l'entretien ID " + entretien.getId_entretien() + " ?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                serviceEntretien.supprimer(entretien);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "L'entretien a été supprimé avec succès");
                chargerDonnees();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression de l'entretien: " + e.getMessage());
            }
        }
    }

    private void fermerFenetre() {
        Stage stage = (Stage) fermerButton.getScene().getWindow();
        stage.close();
    }

    private void ouvrirDemandesEntretien() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeDemandesEntretien.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestion des Demandes d'Entretien");
            stage.setScene(new Scene(root));
            stage.show();
            
            stage.setOnHidden(e -> chargerDonnees());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Erreur lors de l'ouverture de la fenêtre des demandes: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private String getTimeRangeForHour(java.time.LocalTime heure) {
        if (heure.isAfter(java.time.LocalTime.of(7, 59)) && heure.isBefore(java.time.LocalTime.of(10, 1))) return "08h00-10h00";
        if (heure.isAfter(java.time.LocalTime.of(13, 59)) && heure.isBefore(java.time.LocalTime.of(16, 1))) return "14h00-16h00";
        if (heure.isAfter(java.time.LocalTime.of(19, 59)) && heure.isBefore(java.time.LocalTime.of(22, 1))) return "20h00-22h00";
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
} 