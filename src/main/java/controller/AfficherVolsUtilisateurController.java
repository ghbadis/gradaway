package controller;

import controller.AjouterReservationVolController;
import entities.Vols;
import Services.serviceVols;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import utils.MyDatabase;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.SnapshotParameters;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.geometry.VPos;
import java.io.File;
import java.net.URL;
public class AfficherVolsUtilisateurController {

    @FXML private GridPane gridVols;
    @FXML private Button reserverButton;
    @FXML private Button retourButton;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> destinationFilter;
    @FXML private ComboBox<String> dateFilter;
    @FXML private ComboBox<String> prixFilter;
    @FXML private ComboBox<String> paysDepart;
    @FXML private ComboBox<String> paysArrivee;

    private serviceVols serviceVols;
    private Vols selectedVol = null;
    private List<Vols> allVols = new ArrayList<>();
    private List<Vols> filteredVols = new ArrayList<>();

    public AfficherVolsUtilisateurController() {
        Connection connection = MyDatabase.getInstance().getCnx();
        serviceVols = new serviceVols(connection);
    }

    @FXML
    public void initialize() {
        // Initialiser les filtres
        initializeFilters();

        // Configurer la recherche
        setupSearch();

        // Afficher les vols
        loadVols();

        // Désactiver le bouton réserver jusqu'à ce qu'un vol soit sélectionné
        reserverButton.setDisable(true);

        // Ajouter des symboles Unicode aux boutons si les images ne sont pas disponibles
        reserverButton.setText("✈ Réserver le vol sélectionné");
        retourButton.setText("← Retour");
    }

    private void initializeFilters() {
        // Charger tous les vols pour initialiser les filtres
        allVols = serviceVols.getAllVols();

        // Initialiser le filtre de destination (ville d'arrivée)
        List<String> destinations = allVols.stream()
                .map(Vols::getVilleArrivee)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        destinationFilter.setItems(FXCollections.observableArrayList(destinations));
        destinationFilter.getItems().add(0, "Toutes les destinations");
        destinationFilter.setValue("Toutes les destinations");

        // Initialiser le filtre de pays de départ
        List<String> paysDeparts = allVols.stream()
                .map(Vols::getPaysDepart)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        paysDepart.setItems(FXCollections.observableArrayList(paysDeparts));
        paysDepart.getItems().add(0, "Tous les pays");
        paysDepart.setValue("Tous les pays");

        // Initialiser le filtre de pays d'arrivée
        List<String> paysArrivees = allVols.stream()
                .map(Vols::getPaysArrivee)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        paysArrivee.setItems(FXCollections.observableArrayList(paysArrivees));
        paysArrivee.getItems().add(0, "Tous les pays");
        paysArrivee.setValue("Tous les pays");

        // Initialiser le filtre de date
        List<String> dates = allVols.stream()
                .map(vol -> String.valueOf(vol.getDateDepart()))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        dateFilter.setItems(FXCollections.observableArrayList(dates));
        dateFilter.getItems().add(0, "Toutes les dates");
        dateFilter.setValue("Toutes les dates");

        // Initialiser le filtre de prix
        prixFilter.setItems(FXCollections.observableArrayList(
                "Toutes les gammes de prix",
                "Moins de 100€",
                "100€ - 300€",
                "300€ - 500€",
                "Plus de 500€"
        ));
        prixFilter.setValue("Toutes les gammes de prix");

        // Ajouter les listeners pour les filtres
        destinationFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        paysDepart.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        paysArrivee.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        dateFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        prixFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                applyFilters(); // Si la recherche est vide, appliquer seulement les filtres
            } else {
                // Filtrer par recherche et filtres
                String searchText = newVal.toLowerCase();
                filteredVols = allVols.stream()
                        .filter(vol -> matchesSearch(vol, searchText) && matchesFilters(vol))
                        .collect(Collectors.toList());
                displayVols(filteredVols);
            }
        });
    }

    private boolean matchesSearch(Vols vol, String searchText) {
        return vol.getNumeroVol().toLowerCase().contains(searchText) ||
                vol.getCompagnie().toLowerCase().contains(searchText) ||
                vol.getVilleDepart().toLowerCase().contains(searchText) ||
                vol.getVilleArrivee().toLowerCase().contains(searchText) ||
                vol.getPaysDepart().toLowerCase().contains(searchText) ||
                vol.getPaysArrivee().toLowerCase().contains(searchText) ||
                vol.getAeroportDepart().toLowerCase().contains(searchText) ||
                vol.getAeroportArrivee().toLowerCase().contains(searchText);
    }

    private boolean matchesFilters(Vols vol) {
        boolean matchesDestination = destinationFilter.getValue().equals("Toutes les destinations") ||
                vol.getVilleArrivee().equals(destinationFilter.getValue());

        boolean matchesPaysDepart = paysDepart.getValue().equals("Tous les pays") ||
                vol.getPaysDepart().equals(paysDepart.getValue());

        boolean matchesPaysArrivee = paysArrivee.getValue().equals("Tous les pays") ||
                vol.getPaysArrivee().equals(paysArrivee.getValue());

        boolean matchesDate = dateFilter.getValue().equals("Toutes les dates") ||
                String.valueOf(vol.getDateDepart()).equals(dateFilter.getValue());

        boolean matchesPrix = true;
        String prixRange = prixFilter.getValue();
        double prix = vol.getPrixStandard();

        if (!prixRange.equals("Toutes les gammes de prix")) {
            if (prixRange.equals("Moins de 100€")) {
                matchesPrix = prix < 100;
            } else if (prixRange.equals("100€ - 300€")) {
                matchesPrix = prix >= 100 && prix <= 300;
            } else if (prixRange.equals("300€ - 500€")) {
                matchesPrix = prix > 300 && prix <= 500;
            } else if (prixRange.equals("Plus de 500€")) {
                matchesPrix = prix > 500;
            }
        }

        return matchesDestination && matchesPaysDepart && matchesPaysArrivee && matchesDate && matchesPrix;
    }

    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();

        if (searchText.isEmpty()) {
            // Si pas de recherche, appliquer seulement les filtres
            filteredVols = allVols.stream()
                    .filter(this::matchesFilters)
                    .collect(Collectors.toList());
        } else {
            // Appliquer recherche et filtres
            filteredVols = allVols.stream()
                    .filter(vol -> matchesSearch(vol, searchText) && matchesFilters(vol))
                    .collect(Collectors.toList());
        }

        displayVols(filteredVols);
    }

    private void loadVols() {
        allVols = serviceVols.getAllVols();
        filteredVols = new ArrayList<>(allVols);
        displayVols(filteredVols);
    }

    private void displayVols(List<Vols> volsList) {
        gridVols.getChildren().clear();

        int row = 0;
        int col = 0;
        int maxCol = 2; // Afficher 2 cartes par ligne

        for (Vols vol : volsList) {
            VBox card = createVolCard(vol);

            // Ajouter la carte à la grille
            gridVols.add(card, col, row);

            // Passer à la colonne suivante ou à la ligne suivante
            col++;
            if (col >= maxCol) {
                col = 0;
                row++;
            }
        }

        // Si aucun vol n'est trouvé
        if (volsList.isEmpty()) {
            Label noResultsLabel = new Label("Aucun vol ne correspond à vos critères de recherche");
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-padding: 20;");
            gridVols.add(noResultsLabel, 0, 0, 2, 1);
        }
    }

    private VBox createVolCard(Vols vol) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setPrefWidth(400);

        // Style de base de la carte
        String baseStyle = "-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-border-radius: 10; -fx-border-color: #dcdde1; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);";

        // Si le vol est sélectionné, ajouter une bordure bleue
        if (selectedVol != null && vol.getIdVol() == selectedVol.getIdVol()) {
            card.setStyle(baseStyle + "-fx-border-color: #3498db; -fx-border-width: 2;");
        } else {
            card.setStyle(baseStyle);
        }

        // En-tête de la carte avec logo de compagnie, numéro de vol et prix
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        // Ajouter l'image de la compagnie
        ImageView companyLogo = new ImageView();
        companyLogo.setFitHeight(160);
        companyLogo.setFitWidth(160);
        companyLogo.setPreserveRatio(true);

        // Charger l'image depuis le chemin stocké dans l'objet vol
        if (vol.getImagePath() != null && !vol.getImagePath().isEmpty()) {
            try {
                // Vérifier si c'est une URL ou un chemin de fichier
                if (vol.getImagePath().startsWith("http://") || vol.getImagePath().startsWith("https://")) {
                    // C'est une URL
                    Image image = new Image(vol.getImagePath(), true); // true pour le chargement en arrière-plan
                    companyLogo.setImage(image);
                } else if (vol.getImagePath().startsWith("file:")) {
                    // C'est déjà une URI de fichier
                    Image image = new Image(vol.getImagePath());
                    companyLogo.setImage(image);
                } else {
                    // C'est un chemin de fichier normal
                    File imageFile = new File(vol.getImagePath());
                    if (imageFile.exists()) {
                        Image image = new Image(imageFile.toURI().toString());
                        companyLogo.setImage(image);
                    } else {
                        // Essayer comme ressource
                        URL imageUrl = getClass().getResource("/images/" + vol.getImagePath());
                        if (imageUrl != null) {
                            Image image = new Image(imageUrl.toExternalForm());
                            companyLogo.setImage(image);
                        } else {
                            // Image par défaut si l'image n'est pas trouvée
                            setDefaultImage(companyLogo, vol.getCompagnie());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'image: " + e.getMessage());
                e.printStackTrace();
                // Image par défaut en cas d'erreur
                setDefaultImage(companyLogo, vol.getCompagnie());
            }
        } else {
            // Image par défaut si aucun chemin d'image n'est spécifié
            setDefaultImage(companyLogo, vol.getCompagnie());
        }

        Label compagnieLabel = new Label(vol.getCompagnie());
        compagnieLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Label numeroLabel = new Label(vol.getNumeroVol());
        numeroLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #2c3e50;");

        VBox companyBox = new VBox(3);
        companyBox.getChildren().addAll(compagnieLabel, numeroLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label prixLabel = new Label(String.format("%.2f €", vol.getPrixStandard()));
        prixLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #27ae60;");

        header.getChildren().addAll(companyLogo, companyBox, spacer, prixLabel);

        // Ligne de séparation
        Separator separator = new Separator();
        separator.setPadding(new Insets(5, 0, 5, 0));

        // Informations sur le vol
        HBox infoBox = new HBox(20);
        infoBox.setAlignment(Pos.CENTER);

        // Boîte de départ avec ville, pays et aéroport
        VBox departBox = new VBox(5);
        departBox.setAlignment(Pos.CENTER);
        Label villeDepart = new Label(vol.getVilleDepart());
        villeDepart.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label paysDepart = new Label(vol.getPaysDepart());
        paysDepart.setStyle("-fx-font-style: italic; -fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        Label aeroportDepart = new Label(vol.getAeroportDepart());
        aeroportDepart.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        departBox.getChildren().addAll(villeDepart, paysDepart, aeroportDepart);

        // Flèche entre départ et arrivée
        Label arrowLabel = new Label("➔");
        arrowLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: #3498db;");

        // Boîte d'arrivée avec ville, pays et aéroport
        VBox arriveeBox = new VBox(5);
        arriveeBox.setAlignment(Pos.CENTER);
        Label villeArrivee = new Label(vol.getVilleArrivee());
        villeArrivee.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        Label paysArrivee = new Label(vol.getPaysArrivee());
        paysArrivee.setStyle("-fx-font-style: italic; -fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        Label aeroportArrivee = new Label(vol.getAeroportArrivee());
        aeroportArrivee.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        arriveeBox.getChildren().addAll(villeArrivee, paysArrivee, aeroportArrivee);

        infoBox.getChildren().addAll(departBox, arrowLabel, arriveeBox);

        // Dates et heures
        HBox datesBox = new HBox(20);
        datesBox.setAlignment(Pos.CENTER);

        Label dateDepart = new Label("Départ: " + vol.getDateDepart());
        dateDepart.setStyle("-fx-text-fill: #34495e;");

        Label dateArrivee = new Label("Arrivée: " + vol.getDateArrivee());
        dateArrivee.setStyle("-fx-text-fill: #34495e;");

        datesBox.getChildren().addAll(dateDepart, dateArrivee);

        // Détails supplémentaires
        HBox detailsBox = new HBox(20);
        detailsBox.setAlignment(Pos.CENTER);

        // Vérifier si la durée est null avant de l'afficher
        String dureeText = "Non précisée";
        if (vol.getDuree() != null) {
            dureeText = vol.getDuree() + " min";
        }

        Label dureeLabel = new Label("Durée: " + dureeText);
        dureeLabel.setStyle("-fx-text-fill: #7f8c8d;");

        Label placesLabel = new Label("Places: " + vol.getPlacesDisponibles());
        placesLabel.setStyle("-fx-text-fill: #7f8c8d;");

        detailsBox.getChildren().addAll(dureeLabel, placesLabel);

        // Statut du vol
        HBox statutBox = new HBox();
        statutBox.setAlignment(Pos.CENTER_RIGHT);

        // Vérifier si le statut est null avant de l'afficher
        String statut = vol.getStatut();
        if (statut == null) {
            statut = "Non défini";
        }

        Label statutLabel = new Label(statut);
        String statutStyle = "-fx-padding: 5 10; -fx-background-radius: 15; -fx-font-size: 12px; -fx-font-weight: bold;";

        if ("Confirmé".equals(statut)) {
            statutLabel.setStyle(statutStyle + "-fx-background-color: #e1f5fe; -fx-text-fill: #0288d1;");
        } else if ("Annulé".equals(statut)) {
            statutLabel.setStyle(statutStyle + "-fx-background-color: #ffebee; -fx-text-fill: #c62828;");
        } else if ("Retardé".equals(statut)) {
            statutLabel.setStyle(statutStyle + "-fx-background-color: #fff8e1; -fx-text-fill: #ffa000;");
        } else {
            statutLabel.setStyle(statutStyle + "-fx-background-color: #e8f5e9; -fx-text-fill: #388e3c;");
        }

        statutBox.getChildren().add(statutLabel);

        // Bouton de sélection
        Button selectButton = new Button("Sélectionner ce vol");
        selectButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 15;");
        selectButton.setPrefWidth(Double.MAX_VALUE);
        selectButton.setOnAction(e -> {
            selectedVol = vol;
            reserverButton.setDisable(false);
            displayVols(filteredVols); // Rafraîchir pour montrer la sélection
        });

        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(
                header, separator, infoBox,
                new Separator(), datesBox, detailsBox,
                statutBox, selectButton
        );

        // Ajouter un effet de survol
        card.setOnMouseEntered(e -> {
            if (selectedVol == null || vol.getIdVol() != selectedVol.getIdVol()) {
                card.setStyle(baseStyle + "-fx-border-color: #bdc3c7; -fx-border-width: 1;");
            }
        });

        card.setOnMouseExited(e -> {
            if (selectedVol == null || vol.getIdVol() != selectedVol.getIdVol()) {
                card.setStyle(baseStyle);
            }
        });

        return card;
    }

    // Méthode pour définir une image par défaut basée sur le nom de la compagnie
    private void setDefaultImage(ImageView imageView, String compagnie) {
        // Créer un cercle coloré avec les initiales de la compagnie comme image par défaut
        int size = 40;
        Canvas canvas = new Canvas(size, size);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Choisir une couleur basée sur le nom de la compagnie (pour avoir toujours la même couleur pour la même compagnie)
        int hash = compagnie.hashCode();
        String[] colors = {
                "#3498db", "#2ecc71", "#e74c3c", "#f39c12", "#9b59b6",
                "#1abc9c", "#d35400", "#c0392b", "#16a085", "#8e44ad"
        };
        String color = colors[Math.abs(hash % colors.length)];

        // Dessiner un cercle de couleur
        gc.setFill(Color.web(color));
        gc.fillOval(0, 0, size, size);

        // Ajouter les initiales
        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 16));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);

        // Obtenir les initiales (première lettre de chaque mot)
        String[] words = compagnie.split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                initials.append(word.charAt(0));
                if (initials.length() >= 2) break; // Maximum 2 lettres
            }
        }

        // Si nous n'avons qu'une lettre, utiliser la première lettre
        if (initials.length() == 0) {
            initials.append(compagnie.charAt(0));
        }

        gc.fillText(initials.toString().toUpperCase(), size/2, size/2);

        // Convertir le Canvas en Image
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage image = canvas.snapshot(params, null);

        imageView.setImage(image);
    }

    @FXML
    private void handleReserver(ActionEvent event) {
        if (selectedVol == null) {
            showAlert(Alert.AlertType.WARNING, "Aucun vol sélectionné",
                    "Veuillez sélectionner un vol à réserver.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/AjouterReservationVol.fxml"));

            if (loader.getLocation() == null) {
                // Essayer un chemin alternatif
                loader.setLocation(getClass().getResource("../views/AjouterReservationVol.fxml"));

                if (loader.getLocation() == null) {
                    showAlert(Alert.AlertType.ERROR, "Erreur",
                            "Le fichier FXML n'a pas été trouvé. Vérifiez le chemin: /views/AjouterReservationVol.fxml");
                    return;
                }
            }

            Parent root = loader.load();

            // Passer le vol sélectionné au contrôleur d'ajout de réservation
            AjouterReservationVolController controller = loader.getController();
            controller.setVol(selectedVol);

            Stage stage = new Stage();
            stage.setTitle("Nouvelle Réservation");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));

            // Ajouter une animation
            root.setScaleX(0.7);
            root.setScaleY(0.7);
            ScaleTransition st = new ScaleTransition(Duration.millis(300), root);
            st.setToX(1);
            st.setToY(1);
            st.play();

            stage.showAndWait();

            // Rafraîchir les vols après la réservation
            loadVols();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "Impossible d'ouvrir la fenêtre de réservation: " + e.getMessage());
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        // Ferme la fenêtre actuelle
        Stage stage = (Stage) retourButton.getScene().getWindow();
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