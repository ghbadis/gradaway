package controllers;

import Services.ServiceRestaurant;
import Services.ServiceReservationRestaurant;
import entities.Restaurant;
import entities.ReservationRestaurant;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ListeReservationRestaurantControllers {

    @FXML private TableView<ReservationRestaurant> reservationTable;
    @FXML private TableColumn<ReservationRestaurant, Integer> idColumn;
    @FXML private TableColumn<ReservationRestaurant, String> restaurantColumn;
    @FXML private TableColumn<ReservationRestaurant, String> etudiantColumn;
    @FXML private TableColumn<ReservationRestaurant, String> dateDebutColumn;
    @FXML private TableColumn<ReservationRestaurant, String> dateFinColumn;
    @FXML private TableColumn<ReservationRestaurant, String> dateReservationColumn;
    @FXML private TableColumn<ReservationRestaurant, String> statusColumn;
    @FXML private TableColumn<ReservationRestaurant, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Button btnRetour;
    
    private ServiceReservationRestaurant serviceReservation = new ServiceReservationRestaurant();
    private ServiceRestaurant serviceRestaurant = new ServiceRestaurant();
    
    // Map to store reservation status (we'll simulate this since there's no status field in the database)
    private Map<Integer, String> reservationStatusMap = new HashMap<>();
    
    // Observable list to hold all reservations
    private ObservableList<ReservationRestaurant> allReservations = FXCollections.observableArrayList();
    // Filtered list for dynamic filtering
    private FilteredList<ReservationRestaurant> filteredReservations;
    
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        setupTableColumns();
        loadReservations();
        setupSearch();
    }
    
    private void setupTableColumns() {
        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idReservation"));
        
        // Pour le nom du restaurant, nous devons le récupérer en utilisant l'ID du restaurant
        restaurantColumn.setCellValueFactory(cellData -> {
            try {
                int restaurantId = cellData.getValue().getIdRestaurant();
                Restaurant restaurant = serviceRestaurant.recuperer().stream()
                    .filter(r -> r.getIdRestaurant() == restaurantId)
                    .findFirst()
                    .orElse(null);
                return new SimpleStringProperty(restaurant != null ? restaurant.getNom() : "Restaurant #" + restaurantId);
            } catch (SQLException e) {
                e.printStackTrace();
                return new SimpleStringProperty("Erreur");
            }
        });
        
        // Pour le nom de l'étudiant (nous afficherons simplement l'ID puisque nous n'avons pas d'entité étudiant)
        etudiantColumn.setCellValueFactory(cellData -> {
            int etudiantId = cellData.getValue().getIdEtudiant();
            return new SimpleStringProperty("Étudiant #" + etudiantId);
        });
        
        // Format date de réservation
        dateReservationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateReservation().format(dateFormatter)));
            
        // Masquer les colonnes dateDebut et dateFin car elles n'existent pas dans ReservationRestaurant
        dateDebutColumn.setVisible(false);
        dateFinColumn.setVisible(false);
        
        // Colonne de statut (simulée)
        statusColumn.setCellValueFactory(cellData -> {
            int reservationId = cellData.getValue().getIdReservation();
            String status = reservationStatusMap.getOrDefault(reservationId, "En attente");
            return new SimpleStringProperty(status);
        });
        
        // Configurer la colonne d'actions avec les boutons accepter/refuser
        setupActionsColumn();
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button acceptButton = new Button("Accepter");
            private final Button declineButton = new Button("Refuser");
            private final HBox buttonBox = new HBox(5, acceptButton, declineButton);
            
            {
                // Style des boutons
                acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                declineButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                
                // Actions des boutons
                acceptButton.setOnAction(event -> {
                    ReservationRestaurant reservation = getTableView().getItems().get(getIndex());
                    acceptReservation(reservation);
                });
                
                declineButton.setOnAction(event -> {
                    ReservationRestaurant reservation = getTableView().getItems().get(getIndex());
                    declineReservation(reservation);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    ReservationRestaurant reservation = getTableView().getItems().get(getIndex());
                    String status = reservationStatusMap.getOrDefault(reservation.getIdReservation(), "En attente");
                    
                    // Masquer les boutons si la réservation est déjà acceptée ou refusée
                    if (status.equals("Acceptée") || status.equals("Refusée")) {
                        setGraphic(null);
                    } else {
                        setGraphic(buttonBox);
                    }
                }
            }
        });
    }
    
    private void loadReservations() {
        try {
            // Effacer les données précédentes
            allReservations.clear();
            reservationStatusMap.clear();
            
            // Charger les réservations depuis la base de données
            allReservations.addAll(serviceReservation.recuperer());
            
            // Initialiser avec des statuts par défaut
            for (ReservationRestaurant reservation : allReservations) {
                // Par défaut, toutes sont en attente
                reservationStatusMap.put(reservation.getIdReservation(), "En attente");
            }
            
            // Créer la liste filtrée
            filteredReservations = new FilteredList<>(allReservations, p -> true);
            reservationTable.setItems(filteredReservations);
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des réservations: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredReservations.setPredicate(reservation -> {
                // Si le champ de recherche est vide, afficher toutes les réservations
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                try {
                    // Recherche par ID
                    if (String.valueOf(reservation.getIdReservation()).contains(lowerCaseFilter)) {
                        return true;
                    }
                    
                    // Recherche par nom de restaurant
                    Restaurant restaurant = serviceRestaurant.recuperer().stream()
                        .filter(r -> r.getIdRestaurant() == reservation.getIdRestaurant())
                        .findFirst()
                        .orElse(null);
                    if (restaurant != null && restaurant.getNom().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    
                    // Recherche par ID étudiant
                    if (String.valueOf(reservation.getIdEtudiant()).contains(lowerCaseFilter)) {
                        return true;
                    }
                    
                    // Recherche par date
                    if (reservation.getDateReservation().format(dateFormatter).contains(lowerCaseFilter)) {
                        return true;
                    }
                    
                    // Recherche par statut
                    String status = reservationStatusMap.getOrDefault(reservation.getIdReservation(), "En attente");
                    return status.toLowerCase().contains(lowerCaseFilter);
                    
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            });
        });
    }
    
    @FXML
    private void showAllReservations() {
        filteredReservations.setPredicate(p -> true);
    }
    
    @FXML
    private void filterAccepted() {
        filteredReservations.setPredicate(reservation -> {
            String status = reservationStatusMap.getOrDefault(reservation.getIdReservation(), "En attente");
            return status.equals("Acceptée");
        });
    }
    
    @FXML
    private void filterDeclined() {
        filteredReservations.setPredicate(reservation -> {
            String status = reservationStatusMap.getOrDefault(reservation.getIdReservation(), "En attente");
            return status.equals("Refusée");
        });
    }
    
    @FXML
    private void filterPending() {
        filteredReservations.setPredicate(reservation -> {
            String status = reservationStatusMap.getOrDefault(reservation.getIdReservation(), "En attente");
            return status.equals("En attente");
        });
    }
    
    private void acceptReservation(ReservationRestaurant reservation) {
        try {
            // Mettre à jour le statut dans notre map (dans une vraie application, vous le mettriez à jour dans la base de données)
            reservationStatusMap.put(reservation.getIdReservation(), "Acceptée");
            
            // Rafraîchir le tableau pour afficher le statut mis à jour
            reservationTable.refresh();
            
            showAlert("Succès", "Réservation #" + reservation.getIdReservation() + " acceptée avec succès", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'acceptation de la réservation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void declineReservation(ReservationRestaurant reservation) {
        try {
            // Mettre à jour le statut dans notre map (dans une vraie application, vous le mettriez à jour dans la base de données)
            reservationStatusMap.put(reservation.getIdReservation(), "Refusée");
            
            // Rafraîchir le tableau pour afficher le statut mis à jour
            reservationTable.refresh();
            
            showAlert("Succès", "Réservation #" + reservation.getIdReservation() + " refusée", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du refus de la réservation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void retourAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurant.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Ajouter une transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la navigation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
