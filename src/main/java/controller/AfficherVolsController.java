package controller;

import entities.Vols;
import Services.serviceVols;
import utils.MyDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class AfficherVolsController {
    @FXML private VBox detailsPane;
    @FXML private VBox detailsContent;
    @FXML
    private GridPane gridVols;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    private Label statusLabel;
    @FXML
    private Label totalVolsLabel;
    @FXML
    private Button ajouterButton, refreshButton, modifierButton, supprimerButton;

    private serviceVols serviceVols;
    private Vols selectedVol = null;
    private List<Vols> allVols;

    public AfficherVolsController() {
        Connection connection = MyDatabase.getInstance().getCnx();
        serviceVols = new serviceVols(connection);
    }

    @FXML
    public void initialize() {
        modifierButton.setDisable(true);
        supprimerButton.setDisable(true);
        detailsPane.setVisible(false);
        // Initialiser le ComboBox de filtrage
        filterComboBox.getItems().addAll(
                "Tous les vols",
                "Prix < 500€",
                "Prix 500€ - 800€",
                "Prix > 800€"
        );
        filterComboBox.setValue("Tous les vols");

        // Ajouter les listeners pour la recherche et le filtrage
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterVols();
        });

        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterVols();
        });

        // Charger les exemples si nécessaire et afficher les vols
        if (serviceVols.getAllVols().isEmpty()) {
            ajouterExemplesVols();
        }

        refreshVols();
    }

    private void updateDetailsPane(Vols vol) {
        if (vol == null) {
            detailsPane.setVisible(false);
            return;
        }

        detailsPane.setVisible(true);
        detailsContent.getChildren().clear();

        // Ajouter l'image de la compagnie en haut
        try {
            ImageView logoView = new ImageView();
            logoView.setFitWidth(200); // Augmenté de 100 à 200
            logoView.setFitHeight(120); // Augmenté de 60 à 120
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true); // Pour une meilleure qualité d'image
            logoView.setCache(true); // Pour de meilleures performances

            if (vol.getImagePath() != null && !vol.getImagePath().isEmpty()) {
                // Vérifier si c'est une URL externe ou un fichier local
                if (vol.getImagePath().startsWith("http://") ||
                        vol.getImagePath().startsWith("https://") ||
                        vol.getImagePath().startsWith("file:")) {
                    Image image = new Image(vol.getImagePath(), 200, 120, true, true); // Taille augmentée
                    logoView.setImage(image);
                } else {
                    // Essayer de charger depuis les ressources
                    URL imageUrl = getClass().getResource("/images/" + vol.getImagePath());
                    if (imageUrl != null) {
                        Image image = new Image(imageUrl.toExternalForm(), 200, 200, true, true); // Taille augmentée
                        logoView.setImage(image);
                    } else {
                        // Image par défaut
                        Image defaultImage = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Airplane_silhouette.svg/2048px-Airplane_silhouette.svg.png", 200, 200, true, true); // Taille augmentée
                        logoView.setImage(defaultImage);
                    }
                }
            } else {
                // Image par défaut si aucune n'est spécifiée
                Image defaultImage = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Airplane_silhouette.svg/2048px-Airplane_silhouette.svg.png", 200, 200, true, true); // Taille augmentée
                logoView.setImage(defaultImage);
            }

            // Ajouter un effet pour améliorer l'apparence de l'image
            DropShadow dropShadow = new DropShadow();
            dropShadow.setRadius(10.0);
            dropShadow.setOffsetX(6.0);
            dropShadow.setOffsetY(6.0);
            dropShadow.setColor(javafx.scene.paint.Color.color(0, 0, 0, 0.3));
            logoView.setEffect(dropShadow);

            VBox logoBox = new VBox(logoView);
            logoBox.setAlignment(Pos.CENTER);
            logoBox.setStyle("-fx-padding: 15 0 25 0;"); // Padding augmenté

            // Ajouter un fond pour mettre en valeur l'image
            logoBox.setStyle(logoBox.getStyle() + "-fx-background-color: #f8f9fa; -fx-background-radius: 8;");

            detailsContent.getChildren().add(logoBox);

        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du logo: " + e.getMessage());
        }

        // Création des sections de détails
        addDetailSection("Informations générales", new String[]{
                "Numéro de vol: " + vol.getNumeroVol(),
                "Compagnie: " + vol.getCompagnie(),
                "Statut: " + vol.getStatut()
        });

        addDetailSection("Départ", new String[]{
                "Pays: " + vol.getPaysDepart(),
                "Ville: " + vol.getVilleDepart(),
                "Aéroport: " + vol.getAeroportDepart(),
                "Date: " + formatDateTime(vol.getDateDepart())
        });

        addDetailSection("Arrivée", new String[]{
                "Pays: " + vol.getPaysArrivee(),
                "Ville: " + vol.getVilleArrivee(),
                "Aéroport: " + vol.getAeroportArrivee(),
                "Date: " + formatDateTime(vol.getDateArrivee())
        });

        addDetailSection("Détails du vol", new String[]{
                "Durée: " + vol.getDuree() + " minutes",
                "Prix: " + vol.getPrixStandard() + " €",
                "Places disponibles: " + vol.getPlacesDisponibles()
        });
    }

    private void addDetailSection(String title, String[] details) {
        VBox section = new VBox(5);
        section.setStyle("-fx-padding: 10 0;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        section.getChildren().add(titleLabel);

        for (String detail : details) {
            Label detailLabel = new Label(detail);
            detailLabel.setStyle("-fx-text-fill: #666;");
            section.getChildren().add(detailLabel);
        }

        Separator separator = new Separator();
        separator.setStyle("-fx-padding: 5 0;");

        detailsContent.getChildren().addAll(section, separator);
    }

    private void ajouterExemplesVols() {
        // Vols Air France
        serviceVols.ajouterVol(new Vols(0, "AF1234", "Air France", "CDG", "Paris", "France",
                "JFK", "New York", "États-Unis",
                LocalDateTime.of(2024, 7, 1, 10, 30),
                LocalDateTime.of(2024, 7, 1, 22, 45),
                495, 850.0, 180, "Confirmé", "https://upload.wikimedia.org/wikipedia/commons/4/44/Air_France_Logo.svg"));

        serviceVols.ajouterVol(new Vols(0, "AF2345", "Air France", "CDG", "Paris", "France",
                "DXB", "Dubai", "Émirats Arabes Unis",
                LocalDateTime.of(2024, 7, 2, 14, 15),
                LocalDateTime.of(2024, 7, 3, 00, 30),
                435, 720.0, 165, "Confirmé", "https://upload.wikimedia.org/wikipedia/commons/4/44/Air_France_Logo.svg"));

        // Vols Emirates
        serviceVols.ajouterVol(new Vols(0, "EK7890", "Emirates", "DXB", "Dubai", "Émirats Arabes Unis",
                "SYD", "Sydney", "Australie",
                LocalDateTime.of(2024, 7, 3, 23, 15),
                LocalDateTime.of(2024, 7, 4, 20, 30),
                855, 1150.0, 190, "Confirmé", "https://upload.wikimedia.org/wikipedia/commons/d/d0/Emirates_logo.svg"));

        // Vols Qatar Airways
        serviceVols.ajouterVol(new Vols(0, "QR7823", "Qatar Airways", "DOH", "Doha", "Qatar",
                "CDG", "Paris", "France",
                LocalDateTime.of(2024, 7, 5, 07, 30),
                LocalDateTime.of(2024, 7, 5, 13, 45),
                375, 750.0, 160, "Confirmé", "https://upload.wikimedia.org/wikipedia/commons/0/05/Qatar_Airways_Logo.svg"));

        // Vols Turkish Airlines
        serviceVols.ajouterVol(new Vols(0, "TK1876", "Turkish Airlines", "IST", "Istanbul", "Turquie",
                "CAI", "Le Caire", "Égypte",
                LocalDateTime.of(2024, 7, 6, 10, 15),
                LocalDateTime.of(2024, 7, 6, 11, 45),
                150, 320.0, 170, "Confirmé", "https://upload.wikimedia.org/wikipedia/commons/8/8b/Turkish_Airlines_logo_2019_compact.svg"));

        // Vols Lufthansa
        serviceVols.ajouterVol(new Vols(0, "LH4567", "Lufthansa", "FRA", "Francfort", "Allemagne",
                "JFK", "New York", "États-Unis",
                LocalDateTime.of(2024, 7, 7, 11, 30),
                LocalDateTime.of(2024, 7, 7, 23, 45),
                495, 780.0, 185, "Confirmé", "https://upload.wikimedia.org/wikipedia/commons/8/82/Lufthansa_Logo_2018.svg"));
    }

    private void refreshVols() {
        allVols = serviceVols.getAllVols();
        filterVols();
        updateStatusBar();
    }

    private void filterVols() {
        String searchText = searchField.getText().toLowerCase();
        String filterValue = filterComboBox.getValue();

        List<Vols> filteredVols = allVols.stream()
                .filter(vol -> {
                    // Filtre de recherche par pays
                    boolean matchesSearch = searchText.isEmpty() ||
                            vol.getPaysDepart().toLowerCase().contains(searchText) ||
                            vol.getPaysArrivee().toLowerCase().contains(searchText);

                    // Filtre par prix
                    boolean matchesPrice = true;
                    switch (filterValue) {
                        case "Prix < 500€":
                            matchesPrice = vol.getPrixStandard() < 500;
                            break;
                        case "Prix 500€ - 800€":
                            matchesPrice = vol.getPrixStandard() >= 500 && vol.getPrixStandard() <= 800;
                            break;
                        case "Prix > 800€":
                            matchesPrice = vol.getPrixStandard() > 800;
                            break;
                    }

                    return matchesSearch && matchesPrice;
                })
                .collect(Collectors.toList());

        afficherVolsFiltres(filteredVols);
        updateStatusBar();
    }

    private void afficherVolsFiltres(List<Vols> vols) {
        gridVols.getChildren().clear();
        int columns = 3;
        int row = 0;
        int col = 0;

        for (Vols vol : vols) {
            VBox card = createVolCard(vol);
            gridVols.add(card, col, row);

            col++;
            if (col == columns) {
                col = 0;
                row++;
            }
        }
    }

    private VBox createVolCard(Vols vol) {

        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #ddd; " +
                "-fx-border-radius: 5; -fx-background-radius: 5; -fx-min-width: 300; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");

        if (selectedVol != null && vol.getIdVol() == selectedVol.getIdVol()) {
            card.setStyle(card.getStyle() + "-fx-border-color: #2196F3; -fx-border-width: 2;");
        }

        // En-tête avec l'image et le titre
        StackPane imageContainer = new StackPane();
        imageContainer.setMinHeight(200);

        // Image plus grande et plus claire
        ImageView imageView = new ImageView();
        imageView.setFitWidth(300);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        // Chargement de l'image avec gestion d'erreur améliorée
        try {
            if (vol.getImagePath() != null && !vol.getImagePath().isEmpty()) {
                // Vérifier si c'est une URL externe (commence par http ou https)
                if (vol.getImagePath().startsWith("http://") || vol.getImagePath().startsWith("https://")) {
                    Image image = new Image(vol.getImagePath(), 300, 200, true, true);
                    imageView.setImage(image);
                    System.out.println("Image chargée depuis URL: " + vol.getImagePath());
                }
                // Vérifier si c'est une URL de fichier local (commence par file:)
                else if (vol.getImagePath().startsWith("file:")) {
                    Image image = new Image(vol.getImagePath(), 300, 200, true, true);
                    imageView.setImage(image);
                    System.out.println("Image chargée depuis fichier local: " + vol.getImagePath());
                }
                // Sinon, essayer de charger depuis les ressources
                else {
                    // Essayer de charger depuis le répertoire des ressources
                    URL imageUrl = getClass().getResource("/images/" + vol.getImagePath());
                    if (imageUrl != null) {
                        Image image = new Image(imageUrl.toExternalForm(), 300, 200, true, true);
                        imageView.setImage(image);
                        System.out.println("Image chargée depuis les ressources: " + vol.getImagePath());
                    } else {
                        // Charger une image par défaut
                        System.out.println("Image non trouvée, chargement de l'image par défaut");
                        Image defaultImage = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Airplane_silhouette.svg/2048px-Airplane_silhouette.svg.png", 300, 200, true, true);
                        imageView.setImage(defaultImage);
                    }
                }
            } else {
                // Si pas d'image spécifiée, utiliser une image par défaut
                Image defaultImage = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Airplane_silhouette.svg/2048px-Airplane_silhouette.svg.png", 300, 200, true, true);
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            System.err.println("Erreur de chargement de l'image: " + e.getMessage());
            e.printStackTrace();

            // En cas d'erreur, charger une image par défaut
            try {
                Image defaultImage = new Image("https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Airplane_silhouette.svg/2048px-Airplane_silhouette.svg.png", 300, 200, true, true);
                imageView.setImage(defaultImage);
            } catch (Exception ex) {
                System.err.println("Impossible de charger l'image par défaut: " + ex.getMessage());
            }
        }

        // Ajouter un effet pour améliorer l'apparence de l'image
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(10.0);
        dropShadow.setOffsetX(6.0);
        dropShadow.setOffsetY(6.0);
        dropShadow.setColor(javafx.scene.paint.Color.color(0, 0, 0, 0.3));
        imageView.setEffect(dropShadow);

        // Ajouter un overlay pour les informations sur l'image
        VBox overlay = new VBox();
        overlay.setAlignment(Pos.TOP_LEFT);
        overlay.setSpacing(5);
        overlay.setPrefWidth(300);

        // Bandeau supérieur avec le numéro de vol et la compagnie
        HBox topBanner = new HBox();
        topBanner.setStyle("-fx-background-color: rgba(33, 150, 243, 0.8); -fx-padding: 8 12; -fx-background-radius: 0 0 5 0;");
        topBanner.setAlignment(Pos.CENTER_LEFT);

        Label companyNumLabel = new Label(vol.getCompagnie() + " • " + vol.getNumeroVol());
        companyNumLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        topBanner.getChildren().add(companyNumLabel);

        // Bandeau de statut du vol
        HBox statusBanner = new HBox();
        statusBanner.setAlignment(Pos.CENTER_RIGHT);
        statusBanner.setStyle("-fx-padding: 5 12;");

        String statut = vol.getStatut() != null ? vol.getStatut() : "Non défini";
        Label statutLabel = new Label(statut);
        String statutStyle = "-fx-padding: 3 8; -fx-background-radius: 15; -fx-font-size: 11px; -fx-font-weight: bold;";

        if ("Confirmé".equals(statut)) {
            statutLabel.setStyle(statutStyle + "-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32;");
        } else if ("Annulé".equals(statut)) {
            statutLabel.setStyle(statutStyle + "-fx-background-color: #FFEBEE; -fx-text-fill: #C62828;");
        } else if ("Retardé".equals(statut)) {
            statutLabel.setStyle(statutStyle + "-fx-background-color: #FFF8E1; -fx-text-fill: #F57F17;");
        } else {
            statutLabel.setStyle(statutStyle + "-fx-background-color: #E3F2FD; -fx-text-fill: #1565C0;");
        }

        statusBanner.getChildren().add(statutLabel);

        // Bandeau inférieur avec les informations de départ et d'arrivée
        VBox bottomBanner = new VBox(3);
        bottomBanner.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 10; -fx-background-radius: 5 5 0 0;");
        bottomBanner.setAlignment(Pos.BOTTOM_CENTER);

        HBox routeInfo = new HBox(10);
        routeInfo.setAlignment(Pos.CENTER);

        VBox departInfo = new VBox(2);
        departInfo.setAlignment(Pos.CENTER);
        Label departLabel = new Label(vol.getVilleDepart());
        departLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label departAirportLabel = new Label(vol.getAeroportDepart());
        departAirportLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #BBDEFB;");
        departInfo.getChildren().addAll(departLabel, departAirportLabel);

        Label arrowLabel = new Label("✈");
        arrowLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #2196F3;");

        VBox arriveeInfo = new VBox(2);
        arriveeInfo.setAlignment(Pos.CENTER);
        Label arriveeLabel = new Label(vol.getVilleArrivee());
        arriveeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label arriveeAirportLabel = new Label(vol.getAeroportArrivee());
        arriveeAirportLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #BBDEFB;");
        arriveeInfo.getChildren().addAll(arriveeLabel, arriveeAirportLabel);

        routeInfo.getChildren().addAll(departInfo, arrowLabel, arriveeInfo);

        HBox dateTimeInfo = new HBox(20);
        dateTimeInfo.setAlignment(Pos.CENTER);

        Label dateLabel = new Label(formatDateTime(vol.getDateDepart()));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #BBDEFB;");

        String dureeText = vol.getDuree() != null ? vol.getDuree() + " min" : "Durée N/A";
        Label dureeLabel = new Label(dureeText);
        dureeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #BBDEFB;");

        dateTimeInfo.getChildren().addAll(dateLabel, dureeLabel);

        bottomBanner.getChildren().addAll(routeInfo, dateTimeInfo);

        // Positionner les bandeaux sur l'image
        imageContainer.getChildren().addAll(imageView);
        overlay.getChildren().addAll(topBanner, statusBanner);

        // Ajouter l'image et les overlays au conteneur
        StackPane.setAlignment(overlay, Pos.TOP_LEFT);
        StackPane.setAlignment(bottomBanner, Pos.BOTTOM_CENTER);
        imageContainer.getChildren().addAll(overlay, bottomBanner);

        // Informations du vol
        VBox details = new VBox(10);
        details.setStyle("-fx-padding: 15 0;");

        // Section prix avec mise en évidence
        HBox priceBox = new HBox();
        priceBox.setAlignment(Pos.CENTER);
        priceBox.setStyle("-fx-padding: 10; -fx-background-color: #E3F2FD; -fx-background-radius: 5;");

        Label prixLabel = new Label(String.format("%.2f €", vol.getPrixStandard()));
        prixLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1565C0;");

        priceBox.getChildren().add(prixLabel);

        // Détails supplémentaires
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(15);
        infoGrid.setVgap(5);
        infoGrid.setPadding(new javafx.geometry.Insets(10, 0, 10, 0));

        // Pays de départ et d'arrivée



        // Ajouter les éléments à la grille


        // Boutons d'action
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER);

        Button selectButton = new Button("Sélectionner");
        selectButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 5; -fx-padding: 8 15;");
        selectButton.setPrefWidth(150);

        Button detailsButton = new Button("Détails");
        detailsButton.setStyle("-fx-background-color: #78909C; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-padding: 8 15;");
        detailsButton.setPrefWidth(100);

        actionButtons.getChildren().addAll(selectButton, detailsButton);

        // Ajouter tous les éléments à la section détails
        details.getChildren().addAll(priceBox, infoGrid, actionButtons);

        // Ajouter l'image et les détails à la carte
        card.getChildren().addAll(imageContainer, details);

        // Ajouter l'événement de clic sur le bouton de sélection
        selectButton.setOnAction(e -> {
            selectedVol = vol;
            modifierButton.setDisable(false);
            supprimerButton.setDisable(false);
            updateDetailsPane(vol);
            refreshVols();
        });

        // Ajouter l'événement de clic sur le bouton de détails
        detailsButton.setOnAction(e -> {
            updateDetailsPane(vol);
        });

        // Ajouter l'événement de survol pour la carte
        card.setOnMouseEntered(e -> {
            if (selectedVol == null || vol.getIdVol() != selectedVol.getIdVol()) {
                card.setStyle(card.getStyle() + "-fx-border-color: #90CAF9; -fx-border-width: 1;");
            }
        });

        card.setOnMouseExited(e -> {
            if (selectedVol == null || vol.getIdVol() != selectedVol.getIdVol()) {
                card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #ddd; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5; -fx-min-width: 300; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            } else {
                card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #2196F3; " +
                        "-fx-border-radius: 5; -fx-background-radius: 5; -fx-min-width: 300; -fx-border-width: 2; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
            }
        });

        return card;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setWrapText(true);
        return label;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "Non défini";
        return dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private void updateStatusBar() {
        int totalVols = allVols.size();
        int volsFiltres = gridVols.getChildren().size();
        totalVolsLabel.setText(String.format("Total: %d vol(s) (%d filtrés)", totalVols, volsFiltres));
        statusLabel.setText("Dernière mise à jour: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AjouterVols.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un vol");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            refreshVols();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre d'ajout :\n" + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        refreshVols();
    }

    @FXML
    private void handleModifier() {
        if (selectedVol == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un vol à modifier.");
            return;
        }

        try {
            // Création du FXML à la volée pour ModifierVols
            String fxmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "\n" +
                    "<?import javafx.scene.control.*?>\n" +
                    "<?import javafx.scene.layout.*?>\n" +
                    "<?import javafx.geometry.Insets?>\n" +
                    "<?import javafx.scene.image.ImageView?>\n" +
                    "\n" +
                    "<VBox xmlns=\"http://javafx.com/javafx\" xmlns:fx=\"http://javafx.com/fxml\"\n" +
                    "      fx:controller=\"controller.ModiifierVolsController\"\n" +
                    "      spacing=\"15\" style=\"-fx-background-color: white; -fx-padding: 20;\">\n" +
                    "\n" +
                    "    <Label text=\"Modifier un vol\"\n" +
                    "           style=\"-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;\"/>\n" +
                    "\n" +
                    "    <HBox alignment=\"CENTER\" spacing=\"10\">\n" +
                    "        <ImageView fx:id=\"imagePreview\" fitWidth=\"150\" fitHeight=\"100\" preserveRatio=\"true\"/>\n" +
                    "        <Button fx:id=\"chooseImageButton\" text=\"Choisir une image\" onAction=\"#handleChooseImage\"\n" +
                    "                style=\"-fx-background-color: #6c757d; -fx-text-fill: white;\"/>\n" +
                    "        <Label fx:id=\"imagePathLabel\" text=\"Aucune image sélectionnée\" />\n" +
                    "    </HBox>\n" +
                    "\n" +
                    "    <GridPane hgap=\"15\" vgap=\"15\">\n" +
                    "        <padding><Insets top=\"20\" right=\"20\" bottom=\"20\" left=\"20\"/></padding>\n" +
                    "\n" +
                    "        <!-- Compagnie -->\n" +
                    "        <Label text=\"Compagnie:\" GridPane.columnIndex=\"0\" GridPane.rowIndex=\"0\"\n" +
                    "               style=\"-fx-text-fill: #2c3e50;\"/>\n" +
                    "        <ComboBox fx:id=\"compagnieComboBox\" GridPane.columnIndex=\"1\" GridPane.rowIndex=\"0\" />\n" +
                    "\n" +
                    "        <!-- Numero de vol -->\n" +
                    "        <Label text=\"Numéro de vol:\" GridPane.columnIndex=\"0\" GridPane.rowIndex=\"1\"\n" +
                    "               style=\"-fx-text-fill: #2c3e50;\"/>\n" +
                    "        <ComboBox fx:id=\"numeroVolComboBox\" GridPane.columnIndex=\"1\" GridPane.rowIndex=\"1\" />\n" +
                    "\n" +
                    "        <!-- Pays de depart -->\n" +
                    "        <Label text=\"Pays de départ:\" GridPane.columnIndex=\"0\" GridPane.rowIndex=\"2\"\n" +
                    "               style=\"-fx-text-fill: #2c3e50;\"/>\n" +
                    "        <ComboBox fx:id=\"paysDepartComboBox\" GridPane.columnIndex=\"1\" GridPane.rowIndex=\"2\" />\n" +
                    "\n" +
                    "        <!-- Ville de depart -->\n" +
                    "        <Label text=\"Ville de départ:\" GridPane.columnIndex=\"0\" GridPane.rowIndex=\"3\"\n" +
                    "               style=\"-fx-text-fill: #2c3e50;\"/>\n" +
                    "        <ComboBox fx:id=\"villeDepartComboBox\" GridPane.columnIndex=\"1\" GridPane.rowIndex=\"3\" />\n" +
                    "\n" +
                    "        <!-- Aeroport de depart -->\n" +
                    "        <Label text=\"Aéroport de départ:\" GridPane.columnIndex=\"0\" GridPane.rowIndex=\"4\"\n" +
                    "               style=\"-fx-text-fill: #2c3e50;\"/>\n" +
                    "        <ComboBox fx:id=\"aeroportDepartComboBox\" GridPane.columnIndex=\"1\" GridPane.rowIndex=\"4\" />\n" +
                    "\n" +
                    "        <!-- Pays d'arrivee -->\n" +
                    "        <Label text=\"Pays d'arrivée:\" GridPane.columnIndex=\"0\" GridPane.rowIndex=\"5\"\n" +
                    "               style=\"-fx-text-fill: #2c3e50;\"/>\n" +
                    "        <ComboBox fx:id=\"paysArriveeComboBox\" GridPane.columnIndex=\"1\" GridPane.rowIndex=\"5\" />\n" +
                    "\n" +
                    "        <!-- Ville d'arrivee -->\n" +
                    "        <Label text=\"Ville d'arrivée:\" GridPane.columnIndex=\"0\" GridPane.rowIndex=\"6\"\n" +
                    "               style=\"-fx-text-fill: #2c3e50;\"/>\n" +
                    "        <ComboBox fx:id=\"villeArriveeComboBox\" GridPane.columnIndex=\"1\" GridPane.rowIndex=\"6\" />\n" +
                    "\n" +
                    "        <!-- Aeroport d'arrivee -->\n" +
                    "        <Label text=\"Aéroport d'arrivée:\" GridPane.columnIndex=\"0\" GridPane.rowIndex=\"7\"\n" +
                    "               style=\"-fx-text-fill: #2c3e50;\"/>\n" +
                    "        <ComboBox fx:id=\"aeroportArriveeComboBox\" GridPane.columnIndex=\"1\" GridPane.rowIndex=\"7\" />\n" +
                    "\n" +
                    "        <!-- Date de depart -->\n" +
                    "        <Label text=\"Date de départ:\" GridPane.columnIndex=\"0\" GridPane.rowIndex=\"8\"\n" +
                    "               style=\"-fx-text-fill: #2c3e50;\"/>\n" +
                    "        <DatePicker fx:id=\"dateDepartPicker\" GridPane.columnIndex=\"1\" GridPane.rowIndex=\"8\" />\n" +
                    "\n" +
                    "        <!-- Date d'arrivee -->\n" +
                    "        <Label text=\"Date d'arrivée:\" GridPane.columnIndex=\"0\" GridPane.rowIndex=\"9\"\n" +
                    "               style=\"-fx-text-fill: #2c3e50;\"/>\n" +
                    "        <DatePicker fx:id=\"dateArriveePicker\" GridPane.columnIndex=\"1\" GridPane.rowIndex=\"9\" />\n" +
                    "\n" +
                    "        <!-- Duree -->\n" +
                    "        <Label text=\"Durée (minutes):\" GridPane.columnIndex=\"0\" GridPane.rowIndex=\"10\"\n" +
                    "               style=\"-fx-text-fill: #2c3e50;\"/>\n" +
                    "        <TextField fx:id=\"dureeField\" GridPane.columnIndex=\"1\" GridPane.rowIndex=\"10\" />\n" +
                    "\n" +
                    "        <!-- Prix -->\n" +
                    "        <Label text=\"Prix (€):\" GridPane.columnIndex=\"0\" GridPane.rowIndex=\"11\"\n" +
                    "               style=\"-fx-text-fill: #2c3e50;\"/>\n" +
                    "        <TextField fx:id=\"prixField\" GridPane.columnIndex=\"1\" GridPane.rowIndex=\"11\" />\n" +
                    "\n" +
                    "        <!-- Places disponibles -->\n" +
                    "        <Label text=\"Places disponibles:\" GridPane.columnIndex=\"0\" GridPane.rowIndex=\"12\"\n" +
                    "               style=\"-fx-text-fill: #2c3e50;\"/>\n" +
                    "        <TextField fx:id=\"placesField\" GridPane.columnIndex=\"1\" GridPane.rowIndex=\"12\" />\n" +
                    "    </GridPane>\n" +
                    "\n" +
                    "    <!-- Buttons -->\n" +
                    "    <HBox spacing=\"10\" alignment=\"CENTER_RIGHT\">\n" +
                    "        <Button fx:id=\"validerButton\" text=\"Valider\" onAction=\"#handleValider\"\n" +
                    "                style=\"-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;\n" +
                    "                       -fx-padding: 10 20; -fx-background-radius: 5;\"/>\n" +
                    "        <Button fx:id=\"annulerButton\" text=\"Annuler\" onAction=\"#handleAnnuler\"\n" +
                    "                style=\"-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;\n" +
                    "                       -fx-padding: 10 20; -fx-background-radius: 5;\"/>\n" +
                    "    </HBox>\n" +
                    "</VBox>";

            // Charger le FXML à partir de la chaîne
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/")); // Juste pour initialiser le loader

            // Utiliser la méthode setClassLoader pour s'assurer que le contrôleur est trouvé
            loader.setClassLoader(getClass().getClassLoader());

            // Charger le FXML à partir de la chaîne
            Parent root = loader.load(new java.io.ByteArrayInputStream(fxmlContent.getBytes()));

            // Récupérer le contrôleur
            ModiifierVolsController controller = loader.getController();

            // Obtenir une connexion de MyDatabase
            Connection conn = MyDatabase.getInstance().getCnx();
            controller.setConnection(conn);

            // Passer le vol sélectionné
            controller.setVol(selectedVol);

            // Configurer la nouvelle fenêtre
            Stage stage = new Stage();
            controller.setStage(stage);
            stage.setTitle("Modifier Vol");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(gridVols.getScene().getWindow());

            // Afficher la fenêtre et attendre qu'elle soit fermée
            stage.showAndWait();

            // Rafraîchir l'affichage après la modification
            refreshVols();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour afficher les erreurs
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Méthode utilitaire pour afficher les avertissements
    private void showWarning(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Avertissement");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        if (selectedVol == null) {
            showAlert(Alert.AlertType.INFORMATION, "Aucune sélection", "Veuillez sélectionner un vol à supprimer.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer ce vol ?");

        if (confirmDialog.showAndWait().get() == ButtonType.OK) {
            serviceVols.supprimerVol(selectedVol.getIdVol());
            selectedVol = null;
            updateDetailsPane(null);
            refreshVols();
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