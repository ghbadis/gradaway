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

public class ListeEntretiensController {
    @FXML
    private ListView<Entretien> entretiensList;
    @FXML
    private Button ajouterButton;
    @FXML
    private Button fermerButton;
    @FXML
    private Button voirDemandesButton;

    private final ServiceEntretien serviceEntretien = new ServiceEntretien();
    private final ServiceExpert serviceExpert = new ServiceExpert();
    private ObservableList<Entretien> entretiensData = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    public void initialize() {
        // Configurer les boutons
        ajouterButton.setOnAction(event -> ouvrirFenetreAjout());
        fermerButton.setOnAction(event -> fermerFenetre());
        voirDemandesButton.setOnAction(event -> ouvrirDemandesEntretien());

        // Configurer la ListView
        entretiensList.setCellFactory(lv -> new ListCell<Entretien>() {
            @Override
            protected void updateItem(Entretien entretien, boolean empty) {
                super.updateItem(entretien, empty);
                if (empty || entretien == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox row = new HBox(0);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setStyle("-fx-padding: 10 0 10 10; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-color: #fff;");

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
                    Label heureLabel = new Label(entretien.getHeure_entretien().format(timeFormatter));
                    heureLabel.setMinWidth(100); heureLabel.setMaxWidth(100);
                    Label etatLabel = new Label(entretien.getEtat_entretien());
                    etatLabel.setMinWidth(100); etatLabel.setMaxWidth(100);
                    Label typeLabel = new Label(entretien.getType_entretien());
                    typeLabel.setMinWidth(120); typeLabel.setMaxWidth(120);
                    Label offreLabel = new Label(entretien.getOffre());
                    offreLabel.setMinWidth(160); offreLabel.setMaxWidth(160);

                    // Spacer before actions
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                    Button modifierBtn = new Button("Modifier");
                    modifierBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #222; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 4 18;");
                    modifierBtn.setOnAction(event -> ouvrirFenetreModification(entretien));

                    Button supprimerBtn = new Button("Supprimer");
                    supprimerBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 4 18;");
                    supprimerBtn.setOnAction(event -> supprimerEntretien(entretien));

                    row.getChildren().addAll(
                        expertLabel, dateLabel, heureLabel, etatLabel, typeLabel, offreLabel, spacer, modifierBtn, supprimerBtn
                    );
                    setGraphic(row);
                    setText(null);
                }
            }
        });

        // Charger les données
        chargerDonnees();
    }

    private void chargerDonnees() {
        try {
            List<Entretien> entretiens = serviceEntretien.recuperer();
            entretiensData.setAll(entretiens);
            entretiensList.setItems(entretiensData);
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
} 