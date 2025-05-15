package controllers;

import entities.ReservationEvenement;
import entities.Evenement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Services.ServiceReservationEvenement;
import Services.ServiceEvenement;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import utils.PDFGenerator;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.net.URI;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ListeReservationController {
    @FXML
    private VBox reservations_container;
    @FXML
    private Button supprimer_button;
    @FXML
    private Button modifier_button;
    @FXML
    private Button retour_button;

    private ServiceReservationEvenement serviceReservation;
    private ServiceEvenement serviceEvenement;
    private ObservableList<ReservationEvenement> reservationsList;
    private ReservationEvenement selectedReservation = null;
    private int currentUserId;

    @FXML
    public void initialize() {
        serviceReservation = new ServiceReservationEvenement();
        serviceEvenement = new ServiceEvenement();
        reservationsList = FXCollections.observableArrayList();

        // Charger les données
        loadData();

        // Ajouter les listeners pour les boutons
        supprimer_button.setOnAction(event -> supprimerReservation());
        modifier_button.setOnAction(event -> modifierReservation());
        retour_button.setOnAction(event -> retourInterfacePrecedente());
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadData(); // Recharger les données avec le nouvel ID utilisateur
    }

    public void refreshData() {
        loadData();
    }

    private void loadData() {
        try {
            List<ReservationEvenement> reservations;
            if (currentUserId > 0) {
                reservations = serviceReservation.recupererParIdEtudiant(currentUserId);
            } else {
                reservations = serviceReservation.recuperer();
            }
            afficherReservations(reservations);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des réservations", e.getMessage());
        }
    }

    private void afficherReservations(List<ReservationEvenement> reservations) {
        reservations_container.getChildren().clear();
        for (ReservationEvenement reservation : reservations) {
            HBox card = new HBox(15);
            card.getStyleClass().addAll("white-bg", "shadow", "card");
            card.setPadding(new Insets(16));
            card.setAlignment(Pos.CENTER_LEFT);

            // Image de l'événement
            Evenement evenement = null;
            try {
                for (Evenement ev : serviceEvenement.recuperer()) {
                    if (ev.getId_evenement() == reservation.getId_evenement()) {
                        evenement = ev;
                        break;
                    }
                }
            } catch (Exception e) {}

            ImageView imageView = new ImageView();
            if (evenement != null && evenement.getImage() != null && !evenement.getImage().isEmpty()) {
                try {
                    imageView.setImage(new Image(evenement.getImage()));
                } catch (Exception e) {}
            }
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setStyle("-fx-background-radius: 10px; -fx-border-radius: 10px;");

            VBox infoBox = new VBox(5);
            Label nomLabel = new Label("Nom : " + reservation.getNom());
            nomLabel.getStyleClass().add("form-label");
            Label prenomLabel = new Label("Prénom : " + reservation.getPrenom());
            prenomLabel.getStyleClass().add("form-label");
            Label emailLabel = new Label("Email : " + reservation.getEmail());
            emailLabel.setStyle("-fx-text-fill: #666;");
            Label dateLabel = new Label("Date : " + reservation.getDate());
            dateLabel.setStyle("-fx-text-fill: #666;");
            infoBox.getChildren().addAll(nomLabel, prenomLabel, emailLabel, dateLabel);

            // Bouton Billet
            Button billetButton = new Button("Billet");
            billetButton.getStyleClass().add("add-btn");
            billetButton.setOnAction(event -> afficherQRCode(reservation));

            card.getChildren().addAll(imageView, infoBox, billetButton);

            // Style de sélection
            if (reservation == selectedReservation) {
                card.setStyle("-fx-border-color: #1976D2; -fx-border-width: 2px; -fx-background-radius: 15px;");
            } else {
                card.setStyle("-fx-background-radius: 15px;");
            }

            card.setOnMouseClicked(event -> {
                selectedReservation = reservation;
                afficherReservations(reservations);
            });

            reservations_container.getChildren().add(card);
        }
    }

    @FXML
    private void afficherQRCode(ReservationEvenement reservation) {
        try {
            // Générer le PDF et obtenir l'URL
            String pdfUrl = PDFGenerator.generateBilletPDF(reservation);

            // Créer une nouvelle fenêtre pour afficher le QR code
            Stage qrStage = new Stage();
            VBox root = new VBox(20);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(20));

            // Générer le QR code avec l'URL du PDF
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(pdfUrl, BarcodeFormat.QR_CODE, 300, 300);

            // Convertir le QR code en image
            BufferedImage qrImage = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < 300; x++) {
                for (int y = 0; y < 300; y++) {
                    qrImage.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }

            // Afficher le QR code
            ImageView qrImageView = new ImageView(SwingFXUtils.toFXImage(qrImage, null));
            qrImageView.setFitWidth(300);
            qrImageView.setFitHeight(300);

            // Ajouter un message explicatif
            Label messageLabel = new Label("Scannez ce QR code pour accéder à votre billet");
            messageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            // Ajouter un bouton pour ouvrir le PDF directement
            Button openPdfButton = new Button("Ouvrir le PDF");
            openPdfButton.setOnAction(e -> {
                try {
                    Desktop.getDesktop().browse(new URI(pdfUrl));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            root.getChildren().addAll(messageLabel, qrImageView, openPdfButton);

            Scene scene = new Scene(root);
            qrStage.setTitle("QR Code du Billet");
            qrStage.setScene(scene);
            qrStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la génération du QR code");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void supprimerReservation() {
        if (selectedReservation == null) {
            showAlert("Erreur", "Aucune réservation sélectionnée", "Veuillez sélectionner une réservation à supprimer");
            return;
        }
        try {
            serviceReservation.supprimer(selectedReservation);
            selectedReservation = null;
            loadData();
            showAlert("Succès", "Réservation supprimée", "La réservation a été supprimée avec succès");
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la suppression", e.getMessage());
        }
    }

    private void modifierReservation() {
        if (selectedReservation == null) {
            showAlert("Erreur", "Aucune réservation sélectionnée", "Veuillez sélectionner une réservation à modifier");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("modifier_reservation.fxml"));
            Parent root = loader.load();
            ModifierReservationController controller = loader.getController();
            controller.setReservation(selectedReservation);
            controller.setParentController(this);
            Stage stage = new Stage();
            stage.setTitle("Modifier la réservation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre de modification", e.getMessage());
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
    public void acceuilbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
            Parent root = loader.load();

            Acceuilcontroller controller = loader.getController();
            controller.setUserId(currentUserId);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil - GradAway");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la page d'accueil", e.getMessage());
        }
    }

    @FXML
    public void userbutton(ActionEvent actionEvent) {
        if (currentUserId <= 0) {
            showAlert("Erreur", "ID utilisateur invalide", "Impossible d'ouvrir le profil.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();

            EditProfileController controller = loader.getController();
            controller.setUserId(currentUserId);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Mon Profil");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture du profil", e.getMessage());
        }
    }

    @FXML
    public void dossierbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDossier.fxml"));
            Parent root = loader.load();

            AjoutDossierController controller = loader.getController();
            controller.setEtudiantId(currentUserId);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Ajout Dossier");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture du dossier", e.getMessage());
        }
    }

    @FXML
    public void universitébutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminconditature.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Candidatures");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des candidatures", e.getMessage());
        }
    }

    @FXML
    public void entretienbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemanderEntretien.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Demander Entretien");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des entretiens", e.getMessage());
        }
    }

    @FXML
    public void evenementbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affiche_evenement.fxml"));
            Parent root = loader.load();

            Ajouterafficheevenementcontrolleur controller = loader.getController();
            controller.setCurrentUserId(currentUserId);

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Événements");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des événements", e.getMessage());
        }
    }

    @FXML
    public void hebergementbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Foyers");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des foyers", e.getMessage());
        }
    }

    @FXML
    public void restaurantbutton(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurantClient.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Restaurants");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture des restaurants", e.getMessage());
        }
    }

    @FXML
    public void volsbutton(ActionEvent actionEvent) {
        showAlert("Information", "Information", "La fonctionnalité des vols sera bientôt disponible.");
    }

    @FXML
    public void logoutbutton(ActionEvent actionEvent) {
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
            showAlert("Erreur", "Erreur lors de la déconnexion", e.getMessage());
        }
    }

    @FXML
    private void retourInterfacePrecedente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_evenement.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) retour_button.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Événements");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur", "Erreur lors du retour à l'interface précédente");
        }
    }
}