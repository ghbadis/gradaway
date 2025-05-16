package controllers;

import Services.ServiceRestaurant;
import Services.ServiceReservationRestaurant;
import entities.Restaurant;
import entities.ReservationRestaurant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatistiqueReservationRestaurantController {

    @FXML
    private BarChart<String, Number> reservationChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    @FXML
    private ComboBox<Restaurant> restaurantComboBox;

    @FXML
    private Button btnRefresh;

    @FXML
    private Button btnRetour;
    
    @FXML
    private ScrollPane chartScrollPane;
    
    @FXML
    private AnchorPane chartContainer;

    @FXML private Button accueilButton;
    @FXML private Button userButton;
    @FXML private Button dossierButton;
    @FXML private Button universiteButton;
    @FXML private Button entretienButton;
    @FXML private Button evenementButton;
    @FXML private Button hebergementButton;
    @FXML private Button restaurantButton;
    @FXML private Button volsButton;
    @FXML private Button logoutButton;

    private ServiceReservationRestaurant serviceReservationRestaurant;
    private ServiceRestaurant serviceRestaurant;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        serviceReservationRestaurant = new ServiceReservationRestaurant();
        serviceRestaurant = new ServiceRestaurant();

        // Configuration du style du graphique
        reservationChart.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10;");
        reservationChart.setAnimated(false);

        // Charger les restaurants dans le ComboBox
        loadRestaurants();

        // Charger les données initiales
        loadChartData();
    }

    private void loadRestaurants() {
        try {
            // Ajouter une option "Tous les restaurants"
            Restaurant allRestaurants = new Restaurant();
            allRestaurants.setIdRestaurant(-1);
            allRestaurants.setNom("Tous les restaurants");

            // Récupérer tous les restaurants
            List<Restaurant> restaurants = serviceRestaurant.recuperer();
            
            // Créer une liste observable avec l'option "Tous les restaurants" en premier
            ObservableList<Restaurant> restaurantList = FXCollections.observableArrayList();
            restaurantList.add(allRestaurants);
            restaurantList.addAll(restaurants);
            
            // Configurer le ComboBox
            restaurantComboBox.setItems(restaurantList);
            restaurantComboBox.setValue(allRestaurants);
            
            // Définir comment afficher les restaurants dans le ComboBox
            restaurantComboBox.setCellFactory(param -> new javafx.scene.control.ListCell<Restaurant>() {
                @Override
                protected void updateItem(Restaurant restaurant, boolean empty) {
                    super.updateItem(restaurant, empty);
                    if (empty || restaurant == null) {
                        setText(null);
                    } else {
                        setText(restaurant.getNom());
                    }
                }
            });
            
            // Définir comment afficher le restaurant sélectionné
            restaurantComboBox.setButtonCell(new javafx.scene.control.ListCell<Restaurant>() {
                @Override
                protected void updateItem(Restaurant restaurant, boolean empty) {
                    super.updateItem(restaurant, empty);
                    if (empty || restaurant == null) {
                        setText(null);
                    } else {
                        setText(restaurant.getNom());
                    }
                }
            });
            
            // Ajouter un écouteur pour mettre à jour le graphique lorsqu'un restaurant est sélectionné
            restaurantComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    loadChartData();
                }
            });
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la liste des restaurants: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void refreshChart(ActionEvent event) {
        loadChartData();
    }

    private void loadChartData() {
        try {
            // Effacer les données précédentes
            reservationChart.getData().clear();
            
            // Récupérer toutes les réservations
            List<ReservationRestaurant> allReservations = serviceReservationRestaurant.getAllReservations();
            
            // Récupérer tous les restaurants pour avoir accès aux noms
            List<Restaurant> allRestaurants = serviceRestaurant.recuperer();
            Map<Integer, String> restaurantNames = new HashMap<>();
            for (Restaurant restaurant : allRestaurants) {
                restaurantNames.put(restaurant.getIdRestaurant(), restaurant.getNom());
            }
            
            // Filtrer les réservations si un restaurant spécifique est sélectionné
            Restaurant selectedRestaurant = restaurantComboBox.getValue();
            List<ReservationRestaurant> filteredReservations = allReservations;
            
            if (selectedRestaurant != null && selectedRestaurant.getIdRestaurant() != -1) {
                int restaurantId = selectedRestaurant.getIdRestaurant();
                filteredReservations = allReservations.stream()
                        .filter(r -> r.getIdRestaurant() == restaurantId)
                        .collect(Collectors.toList());
                
                // Mettre à jour le titre du graphique
                reservationChart.setTitle("Nombre de réservations par jour pour " + selectedRestaurant.getNom());
            } else {
                // Titre par défaut pour tous les restaurants
                reservationChart.setTitle("Nombre de réservations par jour pour tous les restaurants");
            }
            
            // Structure de données pour stocker les réservations par date et par restaurant
            Map<LocalDate, Map<Integer, Integer>> reservationsByDateAndRestaurant = new HashMap<>();
            
            // Remplir la structure de données
            for (ReservationRestaurant reservation : filteredReservations) {
                LocalDate date = reservation.getDateReservation();
                int restaurantId = reservation.getIdRestaurant();
                
                // Initialiser la map pour cette date si elle n'existe pas encore
                if (!reservationsByDateAndRestaurant.containsKey(date)) {
                    reservationsByDateAndRestaurant.put(date, new HashMap<>());
                }
                
                // Incrémenter le compteur pour ce restaurant à cette date
                Map<Integer, Integer> restaurantCounts = reservationsByDateAndRestaurant.get(date);
                restaurantCounts.put(restaurantId, restaurantCounts.getOrDefault(restaurantId, 0) + 1);
            }
            
            // Créer une série par restaurant
            Map<Integer, XYChart.Series<String, Number>> seriesByRestaurant = new HashMap<>();
            
            // Si un restaurant spécifique est sélectionné, créer une seule série
            if (selectedRestaurant != null && selectedRestaurant.getIdRestaurant() != -1) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName(selectedRestaurant.getNom());
                seriesByRestaurant.put(selectedRestaurant.getIdRestaurant(), series);
            } else {
                // Sinon, créer une série pour chaque restaurant
                for (Restaurant restaurant : allRestaurants) {
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName(restaurant.getNom());
                    seriesByRestaurant.put(restaurant.getIdRestaurant(), series);
                }
            }
            
            // Trier les dates
            List<LocalDate> sortedDates = new ArrayList<>(reservationsByDateAndRestaurant.keySet());
            sortedDates.sort(LocalDate::compareTo);
            
            // Pour chaque date, ajouter les données à la série correspondante
            for (LocalDate date : sortedDates) {
                String dateStr = date.format(formatter);
                Map<Integer, Integer> restaurantCounts = reservationsByDateAndRestaurant.get(date);
                
                for (Map.Entry<Integer, Integer> entry : restaurantCounts.entrySet()) {
                    int restaurantId = entry.getKey();
                    int count = entry.getValue();
                    
                    // Vérifier si nous avons une série pour ce restaurant
                    if (seriesByRestaurant.containsKey(restaurantId)) {
                        XYChart.Series<String, Number> series = seriesByRestaurant.get(restaurantId);
                        series.getData().add(new XYChart.Data<>(dateStr, count));
                    }
                }
            }
            
            // Ajouter toutes les séries au graphique
            for (XYChart.Series<String, Number> series : seriesByRestaurant.values()) {
                if (!series.getData().isEmpty()) {
                    reservationChart.getData().add(series);
                }
            }
            
            // Ajuster la largeur du graphique en fonction du nombre de dates
            int numDates = sortedDates.size();
            int seriesCount = reservationChart.getData().size();
            
            // Calculer la largeur nécessaire pour le graphique
            // Plus il y a de dates et de séries, plus le graphique doit être large
            int minWidth = 980; // Largeur minimale
            int dateWidth = 150; // Largeur par date
            int calculatedWidth = Math.max(minWidth, numDates * dateWidth);
            
            // Définir la largeur du graphique
            reservationChart.setPrefWidth(calculatedWidth);
            chartContainer.setPrefWidth(calculatedWidth);
            
            // Configurer le défilement horizontal
            chartScrollPane.setHvalue(0); // Réinitialiser la position de défilement
            
            // Ajuster l'espacement des barres en fonction du nombre de dates
            if (numDates > 10) {
                // Réduire l'espacement pour les grands ensembles de données
                reservationChart.setCategoryGap(5);
                reservationChart.setBarGap(2);
            } else {
                // Espacement normal pour les petits ensembles de données
                reservationChart.setCategoryGap(10);
                reservationChart.setBarGap(4);
            }
            
            // Personnaliser l'apparence des barres et ajouter les noms des restaurants
            for (int i = 0; i < reservationChart.getData().size(); i++) {
                XYChart.Series<String, Number> series = reservationChart.getData().get(i);
                String restaurantName = series.getName();
                
                // Attribuer une couleur différente à chaque série
                String color = getColorForSeries(i);
                
                for (XYChart.Data<String, Number> data : series.getData()) {
                    // Appliquer la couleur à la barre
                    data.getNode().setStyle("-fx-bar-fill: " + color + ";");
                    
                    // Créer un label pour afficher le nom du restaurant sur la barre
                    StackPane node = (StackPane) data.getNode();
                    
                    // Créer un VBox pour contenir le texte
                    VBox labelBox = new VBox();
                    labelBox.setAlignment(Pos.CENTER);
                    
                    // Créer un label pour le nom du restaurant
                    Label nameLabel = new Label(restaurantName);
                    nameLabel.setTextFill(Color.WHITE);
                    nameLabel.setFont(Font.font("System", FontWeight.BOLD, 10));
                    nameLabel.setWrapText(true);
                    nameLabel.setTextAlignment(TextAlignment.CENTER);
                    nameLabel.setMaxWidth(80);
                    
                    // Créer un label pour le nombre de réservations
                    Label countLabel = new Label(data.getYValue().toString());
                    countLabel.setTextFill(Color.WHITE);
                    countLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
                    
                    // Ajouter les labels au VBox
                    labelBox.getChildren().addAll(nameLabel, countLabel);
                    
                    // Ajouter le VBox à la barre
                    node.getChildren().add(labelBox);
                    
                    // Ajouter un effet de survol
                    final String finalColor = color;
                    data.getNode().setOnMouseEntered(e -> {
                        // Éclaircir la couleur pour l'effet de survol
                        data.getNode().setStyle("-fx-bar-fill: " + getLighterColor(finalColor) + ";");
                    });
                    
                    data.getNode().setOnMouseExited(e -> {
                        data.getNode().setStyle("-fx-bar-fill: " + finalColor + ";");
                    });
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les données des réservations: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Retourne une couleur pour une série donnée
     * @param index Index de la série
     * @return Code couleur hexadécimal
     */
    private String getColorForSeries(int index) {
        // Tableau de couleurs pour les différentes séries
        String[] colors = {
            "#1a365d", // Bleu foncé
            "#2c7fb8", // Bleu
            "#41b6c4", // Bleu-vert
            "#7fcdbb", // Vert clair
            "#c7e9b4", // Vert très clair
            "#edf8b1", // Jaune clair
            "#f7fcb9", // Jaune très clair
            "#d95f0e", // Orange
            "#993404", // Orange foncé
            "#e41a1c"  // Rouge
        };
        
        return colors[index % colors.length];
    }
    
    /**
     * Retourne une version plus claire de la couleur donnée
     * @param color Couleur hexadécimale
     * @return Version plus claire de la couleur
     */
    private String getLighterColor(String color) {
        // Simplement retourner une couleur plus claire prédéfinie
        if (color.equals("#1a365d")) return "#2c4c7c";
        if (color.equals("#2c7fb8")) return "#4a9fd6";
        if (color.equals("#41b6c4")) return "#65d6e4";
        if (color.equals("#7fcdbb")) return "#9feddb";
        if (color.equals("#c7e9b4")) return "#e7ffd4";
        if (color.equals("#edf8b1")) return "#ffffd1";
        if (color.equals("#f7fcb9")) return "#ffffd9";
        if (color.equals("#d95f0e")) return "#f97f2e";
        if (color.equals("#993404")) return "#b95424";
        if (color.equals("#e41a1c")) return "#ff3a3c";
        
        return color; // Retourner la couleur d'origine si non trouvée
    }

    @FXML
    private void retourListe(ActionEvent event) {
        try {
            // Charger l'interface ListRestaurant.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurant.fxml"));
            Parent root = loader.load();
            
            // Récupérer la scène actuelle
            Scene scene = btnRetour.getScene();
            Stage stage = (Stage) scene.getWindow();
            
            // Remplacer le contenu de la scène
            scene.setRoot(root);
            
            // Configurer la fenêtre
            stage.setTitle("Liste des Restaurants");
            stage.setMaximized(true);
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger l'interface de liste des restaurants: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // === NAVIGATION DASHBOARD ===
    @FXML private void navigateToAccueil() { navigateTo("/AcceuilAdmin.fxml", "Accueil Admin"); }
    @FXML private void navigateToUser() { navigateTo("/AdminUser.fxml", "Gestion des Utilisateurs"); }
    @FXML private void navigateToDossier() { navigateTo("/AdminDossier.fxml", "Gestion des Dossiers"); }
    @FXML private void navigateToUniversite() { navigateTo("/adminuniversite.fxml", "Gestion des Universités"); }
    @FXML private void navigateToEntretien() { navigateTo("/Gestionnaire.fxml", "Gestion des Entretiens"); }
    @FXML private void navigateToEvenement() { navigateTo("/gestion_evenement.fxml", "Gestion des Événements"); }
    @FXML private void navigateToHebergement() { navigateTo("/AjouterFoyer.fxml", "Gestion des Foyers"); }
    @FXML private void navigateToRestaurant() { navigateTo("/ListRestaurant.fxml", "Liste des Restaurants"); }
    @FXML private void navigateToVols() {   try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Accueilvol.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) volsButton.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Gestion des Vols");
        stage.centerOnScreen();
    } catch (IOException e) {
        e.printStackTrace();
        //showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture", e.getMessage());
    }}
    @FXML private void logout() { navigateTo("/login-view.fxml", "Login - GradAway"); }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = accueilButton.getScene(); // ou n'importe quel bouton du menu
            Stage stage = (Stage) scene.getWindow();
            scene.setRoot(root); // remplace tout le contenu de la fenêtre
            stage.setTitle(title);
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
