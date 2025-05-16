package controller;

import Services.serviceReservationVol;
import entities.ReservationVol;
import Services.serviceVols;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import utils.MyDatabase;

import java.io.IOException;
import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AfficherReservationVolController {

    @FXML
    private GridPane gridReservations;

    @FXML
    private Button ajouterButton, refreshButton, modifierButton, supprimerButton;

    private serviceReservationVol serviceReservationVol;
    private serviceVols serviceVols;
    private ReservationVol selectedReservation = null;

    // Style constants
    private static final String CARD_STYLE = """
            -fx-background-color: white;
            -fx-padding: 15;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);
            """;

    private static final String CARD_STYLE_SELECTED = """
            -fx-background-color: white;
            -fx-padding: 15;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-effect: dropshadow(three-pass-box, rgba(33,150,243,0.3), 10, 0, 0, 0);
            -fx-border-color: #2196F3;
            -fx-border-width: 2;
            """;

    private static final String TITLE_LABEL_STYLE = """
            -fx-font-size: 13px;
            -fx-text-fill: #95a5a6;
            -fx-font-weight: normal;
            """;

    private static final String VALUE_LABEL_STYLE = """
            -fx-font-size: 14px;
            -fx-text-fill: #2c3e50;
            -fx-font-weight: bold;
            """;

    public AfficherReservationVolController() {
        Connection connection = MyDatabase.getInstance().getCnx();
        serviceReservationVol = new serviceReservationVol(connection);
        serviceVols = new serviceVols(connection);
    }

    @FXML
    public void initialize() {
        setupButtons();
        if (serviceReservationVol.getAllReservations().isEmpty()) {
            ajouterExemplesReservations();
        }
        afficherReservations();
    }

    private void setupButtons() {
        // Style des boutons
        setupButton(ajouterButton, "#4CAF50", "Ajouter une nouvelle réservation");
        setupButton(refreshButton, "#2196F3", "Actualiser la liste des réservations");
        setupButton(modifierButton, "#FFC107", "Modifier la réservation sélectionnée");
        setupButton(supprimerButton, "#f44336", "Supprimer la réservation sélectionnée");
    }

    private void setupButton(Button button, String color, String tooltip) {
        button.setStyle(String.format("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-weight: bold;
                -fx-padding: 10 20;
                -fx-background-radius: 5;
                -fx-cursor: hand;
                """, color));

        button.setTooltip(new javafx.scene.control.Tooltip(tooltip));

        // Effet de survol
        button.setOnMouseEntered(e -> {
            button.setEffect(new DropShadow(10, Color.web(color)));
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setToX(1.05);
            st.setToY(1.05);
            st.play();
        });

        button.setOnMouseExited(e -> {
            button.setEffect(null);
            ScaleTransition st = new ScaleTransition(Duration.millis(100), button);
            st.setToX(1);
            st.setToY(1);
            st.play();
        });
    }

    private void ajouterExemplesReservations() {
        serviceReservationVol.ajouterReservation(new ReservationVol(
                0,
                1,
                1,
                java.time.LocalDateTime.of(2024, 7, 1, 9, 0),
                2,
                1300.0,
                "Payé",
                "Affaires",
                "Soute",
                "Aucun commentaire",
                "REF-001"
        ));

        serviceReservationVol.ajouterReservation(new ReservationVol(
                0,
                2,
                2,
                java.time.LocalDateTime.of(2024, 7, 5, 15, 30),
                1,
                800.0,
                "En attente",
                "Économique",
                "Cabine",
                "Bagage cabine uniquement",
                "REF-002"
        ));

        serviceReservationVol.ajouterReservation(new ReservationVol(
                0,
                3,
                3,
                java.time.LocalDateTime.of(2024, 7, 10, 8, 0),
                3,
                900.0,
                "Payé",
                "Première",
                null,
                null,
                "REF-003"
        ));
    }

    private void afficherReservations() {
        gridReservations.getChildren().clear();
        List<ReservationVol> reservations = serviceReservationVol.getAllReservations();

        int row = 0;
        for (ReservationVol reservation : reservations) {
            VBox card = createReservationCard(reservation);
            GridPane.setFillWidth(card, true);
            gridReservations.add(card, 0, row++);
        }
    }

    private VBox createReservationCard(ReservationVol reservation) {
        VBox card = new VBox(15);
        card.setStyle(selectedReservation != null &&
                reservation.getIdReservation() == selectedReservation.getIdReservation() ?
                CARD_STYLE_SELECTED : CARD_STYLE);

        // En-tête de la carte
        HBox header = createCardHeader(reservation);

        // Contenu principal
        VBox content = createCardContent(reservation);

        // Pied de la carte
        HBox footer = createCardFooter(reservation);

        card.getChildren().addAll(header, content, footer);

        // Gestionnaire d'événements
        setupCardInteraction(card, reservation);

        return card;
    }

    private HBox createCardHeader(ReservationVol reservation) {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        // Référence de réservation
        VBox refBox = createInfoSection("Référence", reservation.getReferenceReservation());

        // Date de réservation
        String dateStr = reservation.getDateReservation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        VBox dateBox = createInfoSection("Date de réservation", dateStr);

        header.getChildren().addAll(refBox, dateBox);
        return header;
    }

    private VBox createCardContent(ReservationVol reservation) {
        VBox content = new VBox(10);

        // Informations du vol
        HBox flightInfo = new HBox(20);
        flightInfo.setAlignment(Pos.CENTER_LEFT);

        String numeroVol = serviceVols.getNumeroVolById(reservation.getIdVol());
        VBox volBox = createInfoSection("Numéro de vol", numeroVol != null ? numeroVol : "Vol " + reservation.getIdVol());
        VBox classeBox = createInfoSection("Classe", reservation.getClasse());
        VBox placesBox = createInfoSection("Places", String.valueOf(reservation.getNombrePlaces()));

        flightInfo.getChildren().addAll(volBox, classeBox, placesBox);

        // Informations des bagages et prix
        HBox extraInfo = new HBox(20);
        extraInfo.setAlignment(Pos.CENTER_LEFT);

        VBox bagageBox = createInfoSection("Type de bagage",
                reservation.getTypeBagage() != null ? reservation.getTypeBagage() : "Non spécifié");
        VBox prixBox = createInfoSection("Prix total", String.format("%.2f €", reservation.getPrixTotal()));

        String statusStyle = reservation.getStatutPaiement().equals("Payé") ?
                "-fx-text-fill: #27ae60;" : "-fx-text-fill: #f39c12;";
        VBox statutBox = createInfoSection("Statut", reservation.getStatutPaiement(), statusStyle);

        extraInfo.getChildren().addAll(bagageBox, prixBox, statutBox);

        content.getChildren().addAll(flightInfo, extraInfo);
        return content;
    }

    private HBox createCardFooter(ReservationVol reservation) {
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_LEFT);

        Label commentLabel = new Label("Commentaires: " +
                (reservation.getCommentaires() != null ? reservation.getCommentaires() : "Aucun"));
        commentLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #95a5a6;");

        footer.getChildren().add(commentLabel);
        return footer;
    }

    private VBox createInfoSection(String title, String value) {
        return createInfoSection(title, value, null);
    }

    private VBox createInfoSection(String title, String value, String additionalValueStyle) {
        VBox section = new VBox(5);

        Label titleLabel = new Label(title);
        titleLabel.setStyle(TITLE_LABEL_STYLE);

        Label valueLabel = new Label(value);
        String valueStyle = VALUE_LABEL_STYLE;
        if (additionalValueStyle != null) {
            valueStyle += additionalValueStyle;
        }
        valueLabel.setStyle(valueStyle);

        section.getChildren().addAll(titleLabel, valueLabel);
        return section;
    }

    private void setupCardInteraction(VBox card, ReservationVol reservation) {
        // Animation de survol
        card.setOnMouseEntered(e -> {
            if (selectedReservation == null ||
                    reservation.getIdReservation() != selectedReservation.getIdReservation()) {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), card);
                st.setToX(1.02);
                st.setToY(1.02);
                st.play();
            }
        });

        card.setOnMouseExited(e -> {
            if (selectedReservation == null ||
                    reservation.getIdReservation() != selectedReservation.getIdReservation()) {
                ScaleTransition st = new ScaleTransition(Duration.millis(100), card);
                st.setToX(1);
                st.setToY(1);
                st.play();
            }
        });

        // Sélection
        card.setOnMouseClicked(e -> {
            selectedReservation = reservation;
            afficherReservations();
        });
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            // Create the FXML loader directly with the file path
            FXMLLoader loader = new FXMLLoader();
            // Set the location explicitly
            loader.setLocation(AfficherReservationVolController.class.getResource("/views/AjouterReservationVol.fxml"));

            if (loader.getLocation() == null) {
                // Try alternative path
                loader.setLocation(AfficherReservationVolController.class.getResource("../views/AjouterReservationVol.fxml"));

                if (loader.getLocation() == null) {
                    showAlert(AlertType.ERROR, "Erreur",
                            "Le fichier FXML n'a pas été trouvé. Vérifiez le chemin: /views/AjouterReservationVol.fxml");
                    return;
                }
            }

            Parent root = loader.load();

            // Récupérer le contrôleur pour passer des données si nécessaire
            // Pas besoin de passer un vol spécifique ici car c'est un nouvel ajout
            // AjouterReservationVolController controller = loader.getController();

            Stage stage = new Stage();
            stage.setTitle("Ajouter une réservation");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            // Add animation
            root.setScaleX(0.7);
            root.setScaleY(0.7);
            ScaleTransition st = new ScaleTransition(Duration.millis(300), root);
            st.setToX(1);
            st.setToY(1);
            st.play();

            stage.showAndWait();
            afficherReservations();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur",
                    "Erreur lors de l'ouverture du formulaire: " + e.getMessage());
        }
    }


    @FXML
    private void handleRefresh(ActionEvent event) {
        selectedReservation = null;
        afficherReservations();
    }

    @FXML
    private void handleModifier(ActionEvent event) {
        if (selectedReservation == null) {
            showAlert(AlertType.WARNING, "Aucune sélection",
                    "Veuillez sélectionner une réservation à modifier.");
            return;
        }

        try {
            // Create the FXML loader directly with the file path
            FXMLLoader loader = new FXMLLoader();
            // Set the location explicitly
            loader.setLocation(AfficherReservationVolController.class.getResource("/views/ModifierReservationVol.fxml"));

            if (loader.getLocation() == null) {
                // Try alternative path
                loader.setLocation(AfficherReservationVolController.class.getResource("../views/ModifierReservationVol.fxml"));

                if (loader.getLocation() == null) {
                    showAlert(AlertType.ERROR, "Erreur",
                            "Le fichier FXML n'a pas été trouvé. Vérifiez le chemin: /views/ModifierReservationVol.fxml");
                    return;
                }
            }

            Parent root = loader.load();

            ModifierReservationVolController controller = loader.getController();
            controller.setReservation(selectedReservation);

            Stage stage = new Stage();
            stage.setTitle("Modifier la réservation");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            // Add animation
            root.setScaleX(0.7);
            root.setScaleY(0.7);
            ScaleTransition st = new ScaleTransition(Duration.millis(300), root);
            st.setToX(1);
            st.setToY(1);
            st.play();

            stage.showAndWait();
            afficherReservations();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur",
                    "Erreur lors de l'ouverture du formulaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        if (selectedReservation == null) {
            showAlert(AlertType.WARNING, "Aucune sélection",
                    "Veuillez sélectionner une réservation à supprimer.");
            return;
        }

        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réservation ?");
        confirmAlert.setContentText("Cette action est irréversible.");

        if (confirmAlert.showAndWait().get().getButtonData().isDefaultButton()) {
            try {
                serviceReservationVol.supprimerReservation(selectedReservation.getIdReservation());
                selectedReservation = null;
                afficherReservations();
                showAlert(AlertType.INFORMATION, "Succès",
                        "La réservation a été supprimée avec succès.");
            } catch (Exception e) {
                showAlert(AlertType.ERROR, "Erreur",
                        "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }}