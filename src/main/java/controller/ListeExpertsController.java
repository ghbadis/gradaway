package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import entities.Expert;
import Services.ServiceExpert;
import java.sql.SQLException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;

public class ListeExpertsController {
    @FXML
    private ListView<Expert> expertsList;
    @FXML
    private Button ajouterButton;
    @FXML
    private Button fermerButton;

    private final ServiceExpert serviceExpert = new ServiceExpert();
    private ObservableList<Expert> expertsData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurer les boutons
        ajouterButton.setOnAction(event -> ouvrirFenetreAjout());
        fermerButton.setOnAction(event -> fermerFenetre());

        // Configurer la ListView
        expertsList.setCellFactory(lv -> new ListCell<Expert>() {
            @Override
            protected void updateItem(Expert expert, boolean empty) {
                super.updateItem(expert, empty);
                
                if (empty || expert == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox row = new HBox(0);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setStyle("-fx-padding: 10 0 10 10; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-color: #fff;");
                    
                    Label nomLabel = new Label(expert.getNom_expert() + " " + expert.getPrenom_expert());
                    nomLabel.setMinWidth(180); nomLabel.setMaxWidth(180);
                    Label emailLabel = new Label(expert.getEmail());
                    emailLabel.setMinWidth(220); emailLabel.setMaxWidth(220);
                    Label specialiteLabel = new Label(expert.getSpecialite());
                    specialiteLabel.setMinWidth(140); specialiteLabel.setMaxWidth(140);
                    Label telephoneLabel = new Label(expert.getTelephone());
                    telephoneLabel.setMinWidth(120); telephoneLabel.setMaxWidth(120);
                    Label experienceLabel = new Label(String.valueOf(expert.getAnneeExperience()));
                    experienceLabel.setMinWidth(120); experienceLabel.setMaxWidth(120);
                    
                    // Spacer before actions
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                    
                    Button modifierBtn = new Button("Modifier");
                    modifierBtn.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #222; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 4 18;");
                    modifierBtn.setOnAction(event -> ouvrirFenetreModification(expert));
                    
                    Button supprimerBtn = new Button("Supprimer");
                    supprimerBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 4 18;");
                    supprimerBtn.setOnAction(event -> supprimerExpert(expert));
                    
                    row.getChildren().addAll(
                        nomLabel, emailLabel, specialiteLabel, telephoneLabel, experienceLabel, spacer, modifierBtn, supprimerBtn
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
            List<Expert> experts = serviceExpert.recuperer();
            expertsData.setAll(experts);
            expertsList.setItems(expertsData);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des experts: " + e.getMessage());
        }
    }

    private void ouvrirFenetreAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CreerExpert.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Expert");
            stage.setScene(new Scene(root));
            stage.show();
            
            stage.setOnHidden(event -> chargerDonnees());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre d'ajout: " + e.getMessage());
        }
    }

    private void ouvrirFenetreModification(Expert expert) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierExpert.fxml"));
            Parent root = loader.load();
            ModifierExpertController controller = loader.getController();
            controller.setExpert(expert);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier un Expert");
            stage.setScene(new Scene(root));
            stage.show();
            
            stage.setOnHidden(event -> chargerDonnees());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre de modification: " + e.getMessage());
        }
    }

    private void supprimerExpert(Expert expert) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer l'expert " + expert.getNom_expert() + " " + expert.getPrenom_expert() + " ?");
        
        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                serviceExpert.supprimer(expert);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "L'expert a été supprimé avec succès");
                chargerDonnees();
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression de l'expert: " + e.getMessage());
            }
        }
    }

    private void fermerFenetre() {
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
} 