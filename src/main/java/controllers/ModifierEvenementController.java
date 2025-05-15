package controllers;

import entities.Evenement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Services.ServiceEvenement;
import javafx.scene.web.WebView;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import javafx.geometry.Pos;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ModifierEvenementController implements Initializable {
    @FXML private TextField nom_txtf;
    @FXML private TextField description_txtf;
    @FXML private DatePicker date_picker;
    @FXML private TextField lieu_txtf;
    @FXML private TextField domaine_txtf;
    @FXML private TextField place_disponible_txtf;
    @FXML private TextField image_txtf;
    @FXML private Button valider_button;
    @FXML private Button annuler_button;
    @FXML private Button choisir_image_button;

    private ServiceEvenement serviceEvenement;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Evenement evenement;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceEvenement = new ServiceEvenement();
        
        // Définir la date minimale à aujourd'hui
        date_picker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.isBefore(today));
            }
        });
        
        // Définir la date par défaut à aujourd'hui
        date_picker.setValue(LocalDate.now());

        valider_button.setOnAction(event -> validerModification());
        annuler_button.setOnAction(event -> annulerModification());
        choisir_image_button.setOnAction(event -> choisirImage());
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
        // Remplir les champs avec les données de l'événement
        nom_txtf.setText(evenement.getNom());
        description_txtf.setText(evenement.getDescription());
        date_picker.setValue(LocalDate.parse(evenement.getDate(), dateFormatter));
        lieu_txtf.setText(evenement.getLieu());
        domaine_txtf.setText(evenement.getDomaine());
        place_disponible_txtf.setText(String.valueOf(evenement.getPlaces_disponibles()));
        if (evenement.getImage() != null) {
            image_txtf.setText(evenement.getImage());
        }
    }

    private void validerModification() {
        try {
            // Validation des champs
            if (nom_txtf.getText().isEmpty() || description_txtf.getText().isEmpty() ||
                date_picker.getValue() == null || lieu_txtf.getText().isEmpty() ||
                domaine_txtf.getText().isEmpty() || place_disponible_txtf.getText().isEmpty()) {
                showAlert("Erreur", "Erreur", "Veuillez remplir tous les champs");
                return;
            }

            // Mise à jour de l'événement
            evenement.setNom(nom_txtf.getText());
            evenement.setDescription(description_txtf.getText());
            evenement.setDate(date_picker.getValue().format(dateFormatter));
            evenement.setLieu(lieu_txtf.getText());
            evenement.setDomaine(domaine_txtf.getText());
            evenement.setPlaces_disponibles(Integer.parseInt(place_disponible_txtf.getText()));
            if (!image_txtf.getText().isEmpty()) {
                evenement.setImage(image_txtf.getText());
            }

            // Mise à jour dans la base de données
            serviceEvenement.modifier(evenement);
            showAlert("Succès", "Succès", "Événement modifié avec succès");
            
            // Fermer la fenêtre
            Stage stage = (Stage) valider_button.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur", "Erreur lors de la modification de l'événement");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Erreur", "Le nombre de places doit être un nombre entier");
        }
    }

    private void annulerModification() {
        Stage stage = (Stage) annuler_button.getScene().getWindow();
        stage.close();
    }

    private void choisirImage() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new javafx.stage.FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        java.io.File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            image_txtf.setText(selectedFile.toURI().toString());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
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
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
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
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
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
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
        }
    }

    @FXML
    public void onuniversiteAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminuniversite.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Universités");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
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
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
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
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
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
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
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
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
        }
    }

    @FXML
    public void onvolsAdminButtonClick(ActionEvent actionEvent) {
        showAlert("Information", "Information", "La fonctionnalité des vols sera bientôt disponible.");
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
            showAlert("Erreur", "Erreur", "Erreur lors de la déconnexion");
        }
    }
} 