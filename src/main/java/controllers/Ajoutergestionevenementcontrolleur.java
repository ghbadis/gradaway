package controllers;

import entities.Evenement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import Services.ServiceEvenement;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Ajoutergestionevenementcontrolleur implements Initializable {
    @FXML
    private TextField domaine_txtf;
    @FXML
    private TextField description_txtf;
    @FXML
    private DatePicker date_picker;
    @FXML
    private TextField nom_txtf;
    @FXML
    private TextField place_disponible_txtf;
    @FXML
    private TextField lieu_txtf;
    @FXML
    private TextField chercher_txtf;
    @FXML
    private VBox evenements_container;
    @FXML
    private Button ajouter_evenement_button;
    @FXML
    private Button liste_reservation_button;
    @FXML
    private VBox formulaire_container;
    @FXML
    private Button valider_button;
    @FXML
    private Button annuler_button;
    @FXML
    private Button user_button;
    @FXML
    private TextField image_txtf;
    @FXML
    private Button choisir_image_button;
    @FXML
    private Button statistiques_button;
    @FXML
    private Button admin_button;
    @FXML
    private Button dossier_button;
    @FXML
    private Button universite_button;
    @FXML
    private Button entretien_button;
    @FXML
    private Button evenement_button;
    @FXML
    private Button hebergement_button;
    @FXML
    private Button restaurant_button;
    @FXML
    private Button vols_button;
    @FXML
    private Button logout_button;

    private ServiceEvenement serviceEvenement;
    private ObservableList<Evenement> evenementsList;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Evenement selectedEvenement;
    private String selectedImagePath;

    private boolean isEditMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceEvenement = new ServiceEvenement();
        evenementsList = FXCollections.observableArrayList();
        
        // Charger les données
        loadData();
        
        if (ajouter_evenement_button != null) {
            ajouter_evenement_button.setOnAction(event -> showFormAjout());
        }
        if (liste_reservation_button != null) {
            liste_reservation_button.setOnAction(event -> ouvrirListeReservations());
        }
        if (valider_button != null) {
            valider_button.setOnAction(event -> validerFormulaire());
        }
        if (annuler_button != null) {
            annuler_button.setOnAction(event -> hideFormulaire());
        }
        if (user_button != null) {
            user_button.setOnAction(event -> onUserAdminButtonClick(event));
        }
        if (choisir_image_button != null) {
            choisir_image_button.setOnAction(event -> choisirImage());
        }
        if (statistiques_button != null) {
            statistiques_button.setOnAction(event -> ouvrirStatistiques());
        }
        
        // Ajouter un listener pour la recherche
        if (chercher_txtf != null) {
            chercher_txtf.textProperty().addListener((observable, oldValue, newValue) -> {
                filterEvenements(newValue);
            });
        }
    }

    private void loadData() {
        try {
            List<Evenement> evenements = serviceEvenement.recuperer();
            evenementsList.clear();
            evenementsList.addAll(evenements);
            afficherEvenements(evenementsList);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des données", e.getMessage());
        }
    }

    private void afficherEvenements(ObservableList<Evenement> evenements) {
        evenements_container.getChildren().clear();
        
        for (Evenement evenement : evenements) {
            HBox eventCard = creerEventCard(evenement);
            evenements_container.getChildren().add(eventCard);
        }
    }

    private HBox creerEventCard(Evenement evenement) {
        HBox card = new HBox(10);
        card.getStyleClass().addAll("white-bg", "shadow");
        card.setPrefWidth(800);
        card.setPrefHeight(100);
        card.setPadding(new javafx.geometry.Insets(10));

        // Image de l'événement
        ImageView imageView = new ImageView();
        try {
            if (evenement.getImage() != null && !evenement.getImage().isEmpty()) {
                Image image = new Image(evenement.getImage());
                imageView.setImage(image);
            } else {
                // Use default image if no image is set
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default_event.png")));
            }
        } catch (Exception e) {
            // Use default image if image loading fails
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default_event.png")));
            } catch (Exception ex) {
                // If even the default image fails, create an empty ImageView
                imageView = new ImageView();
            }
        }
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);

        // Informations de l'événement
        VBox infoBox = new VBox(5);
        Label nomLabel = new Label(evenement.getNom());
        nomLabel.getStyleClass().add("event-title");
        Label dateLabel = new Label("Date: " + evenement.getDate());
        Label lieuLabel = new Label("Lieu: " + evenement.getLieu());
        Label placesLabel = new Label("Places disponibles: " + evenement.getPlaces_disponibles());
        infoBox.getChildren().addAll(nomLabel, dateLabel, lieuLabel, placesLabel);

        // Boutons
        HBox buttonBox = new HBox(10);
        Button modifierBtn = new Button("Modifier");
        Button supprimerBtn = new Button("Supprimer");
        
        modifierBtn.getStyleClass().add("update-btn");
        supprimerBtn.getStyleClass().add("delete-btn");

        modifierBtn.setOnAction(event -> {
            try {
                // Fermer la fenêtre actuelle
                Stage currentStage = (Stage) modifierBtn.getScene().getWindow();
                currentStage.close();

                // Ouvrir la fenêtre de modification
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifier_evenement.fxml"));
                Parent root = loader.load();
                ModifierEvenementController controller = loader.getController();
                controller.setEvenement(evenement);
                Stage stage = new Stage();
                stage.setTitle("Modifier l'événement");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification", e.getMessage());
            }
        });
        
        supprimerBtn.setOnAction(event -> {
            selectedEvenement = evenement;
            supprimerEvenement();
        });

        buttonBox.getChildren().addAll(modifierBtn, supprimerBtn);

        card.getChildren().addAll(imageView, infoBox, buttonBox);
        return card;
    }

    private void filterEvenements(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            afficherEvenements(evenementsList);
        } else {
            ObservableList<Evenement> filteredList = evenementsList.filtered(evenement ->
                evenement.getNom().toLowerCase().contains(searchText.toLowerCase()) ||
                evenement.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                evenement.getLieu().toLowerCase().contains(searchText.toLowerCase()) ||
                evenement.getDomaine().toLowerCase().contains(searchText.toLowerCase())
            );
            afficherEvenements(filteredList);
        }
    }

    private void showFormAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter_evenement.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ajouter_evenement_button.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Ajouter un événement");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre d'ajout", e.getMessage());
        }
    }

    private void showFormModification(Evenement evenement) {
        isEditMode = true;
        afficherEvenementSelectionne(evenement);
        formulaire_container.setVisible(true);
        formulaire_container.setManaged(true);
    }

    private void hideFormulaire() {
        formulaire_container.setVisible(false);
        formulaire_container.setManaged(false);
        clearFields();
        selectedEvenement = null;
    }

    private void validerFormulaire() {
        if (isEditMode) {
            modifierEvenement();
        } else {
            ajouterEvenement();
        }
        hideFormulaire();
    }

    private void ajouterEvenement() {
        try {
            String nom = nom_txtf.getText();
            String description = description_txtf.getText();
            String date = date_picker.getValue().format(dateFormatter);
            String lieu = lieu_txtf.getText();
            String domaine = domaine_txtf.getText();
            int placesDisponibles = Integer.parseInt(place_disponible_txtf.getText());
            String image = image_txtf != null ? image_txtf.getText() : null;

            Evenement evenement = new Evenement(nom, description, date, lieu, domaine, placesDisponibles, image);
            serviceEvenement.ajouter(evenement);
            
            loadData();
            showAlert("Succès", "Événement ajouté avec succès", null);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout de l'événement", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format invalide", "Le nombre de places doit être un nombre entier");
        }
    }

    private void modifierEvenement() {
        if (selectedEvenement == null) {
            showAlert("Erreur", "Aucun événement sélectionné", "Veuillez sélectionner un événement à modifier");
            return;
        }

        try {
            selectedEvenement.setNom(nom_txtf.getText());
            selectedEvenement.setDescription(description_txtf.getText());
            selectedEvenement.setDate(date_picker.getValue().format(dateFormatter));
            selectedEvenement.setLieu(lieu_txtf.getText());
            selectedEvenement.setDomaine(domaine_txtf.getText());
            selectedEvenement.setPlaces_disponibles(Integer.parseInt(place_disponible_txtf.getText()));

            serviceEvenement.modifier(selectedEvenement);
            loadData();
            showAlert("Succès", "Événement modifié avec succès", null);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la modification de l'événement", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format invalide", "Le nombre de places doit être un nombre entier");
        }
    }

    private void supprimerEvenement() {
        if (selectedEvenement == null) {
            showAlert("Erreur", "Aucun événement sélectionné", "Veuillez sélectionner un événement à supprimer");
            return;
        }

        try {
            serviceEvenement.supprimer(selectedEvenement);
            loadData();
            showAlert("Succès", "Événement supprimé avec succès", null);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la suppression de l'événement", e.getMessage());
        }
    }

    private void afficherEvenementSelectionne(Evenement evenement) {
        nom_txtf.setText(evenement.getNom());
        description_txtf.setText(evenement.getDescription());
        date_picker.setValue(LocalDate.parse(evenement.getDate(), dateFormatter));
        lieu_txtf.setText(evenement.getLieu());
        domaine_txtf.setText(evenement.getDomaine());
        place_disponible_txtf.setText(String.valueOf(evenement.getPlaces_disponibles()));
        if (image_txtf != null) image_txtf.setText(evenement.getImage());
    }

    private void clearFields() {
        nom_txtf.clear();
        description_txtf.clear();
        date_picker.setValue(null);
        lieu_txtf.clear();
        domaine_txtf.clear();
        place_disponible_txtf.clear();
        if (image_txtf != null) image_txtf.clear();
        selectedEvenement = null;
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void ouvrirInterfaceUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affiche_evenement.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Affichage des Événements");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace(); // Affiche le stacktrace dans la console
            showAlert("Erreur", "Impossible d'ouvrir l'interface utilisateur", e.toString() + "\n" + (e.getCause() != null ? e.getCause().toString() : ""));
        }
    }

    private void choisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        java.io.File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath();
            image_txtf.setText(selectedImagePath);
        }
    }

    private void ouvrirListeReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/liste_reservation.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) liste_reservation_button.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des Réservations");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la liste des réservations", e.getMessage());
        }
    }

    private void ouvrirStatistiques() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/statistiques_evenements.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) statistiques_button.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            stage.centerOnScreen();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de la déconnexion", e.getMessage());
        }
    }
}
