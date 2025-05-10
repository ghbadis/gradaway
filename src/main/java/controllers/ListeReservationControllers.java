package controllers;

import Services.ServiceFoyer;
import Services.ServiceReservationFoyer;
import entities.Foyer;
import entities.ReservationFoyer;
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

public class ListeReservationControllers {

    @FXML private TableView<ReservationFoyer> reservationTable;
    @FXML private TableColumn<ReservationFoyer, Integer> idColumn;
    @FXML private TableColumn<ReservationFoyer, String> foyerColumn;
    @FXML private TableColumn<ReservationFoyer, String> etudiantColumn;
    @FXML private TableColumn<ReservationFoyer, String> dateDebutColumn;
    @FXML private TableColumn<ReservationFoyer, String> dateFinColumn;
    @FXML private TableColumn<ReservationFoyer, String> dateReservationColumn;
    @FXML private TableColumn<ReservationFoyer, String> statusColumn;
    @FXML private TableColumn<ReservationFoyer, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private Button btnRetour;
    
    private ServiceReservationFoyer serviceReservation = new ServiceReservationFoyer();
    private ServiceFoyer serviceFoyer = new ServiceFoyer();
    
    // Map to store reservation status (we'll simulate this since there's no status field in the database)
    private Map<Integer, String> reservationStatusMap = new HashMap<>();
    
    // Observable list to hold all reservations
    private ObservableList<ReservationFoyer> allReservations = FXCollections.observableArrayList();
    // Filtered list for dynamic filtering
    private FilteredList<ReservationFoyer> filteredReservations;
    
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
        
        // For Foyer name, we need to fetch it using the foyerId
        foyerColumn.setCellValueFactory(cellData -> {
            try {
                int foyerId = cellData.getValue().getFoyerId();
                Foyer foyer = serviceFoyer.getFoyerById(foyerId);
                return new SimpleStringProperty(foyer != null ? foyer.getNom() : "Foyer #" + foyerId);
            } catch (SQLException e) {
                e.printStackTrace();
                return new SimpleStringProperty("Erreur");
            }
        });
        
        // For student name (we'll just show the ID since we don't have a student entity)
        etudiantColumn.setCellValueFactory(cellData -> {
            int etudiantId = cellData.getValue().getIdEtudiant();
            return new SimpleStringProperty("Étudiant #" + etudiantId);
        });
        
        // Format dates
        dateDebutColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateDebut().format(dateFormatter)));
            
        dateFinColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateFin().format(dateFormatter)));
            
        dateReservationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getDateReservation().format(dateFormatter)));
        
        // Status column (simulated)
        statusColumn.setCellValueFactory(cellData -> {
            int reservationId = cellData.getValue().getIdReservation();
            String status = reservationStatusMap.getOrDefault(reservationId, "En attente");
            return new SimpleStringProperty(status);
        });
        
        // Configure the action column with accept/decline buttons
        setupActionsColumn();
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button acceptButton = new Button("Accepter");
            private final Button declineButton = new Button("Refuser");
            private final HBox buttonBox = new HBox(5, acceptButton, declineButton);
            
            {
                // Style the buttons
                acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                declineButton.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
                
                // Set button actions
                acceptButton.setOnAction(event -> {
                    ReservationFoyer reservation = getTableView().getItems().get(getIndex());
                    acceptReservation(reservation);
                });
                
                declineButton.setOnAction(event -> {
                    ReservationFoyer reservation = getTableView().getItems().get(getIndex());
                    declineReservation(reservation);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    ReservationFoyer reservation = getTableView().getItems().get(getIndex());
                    String status = reservationStatusMap.getOrDefault(reservation.getIdReservation(), "En attente");
                    
                    // Hide buttons if reservation is already accepted or declined
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
            // Clear previous data
            allReservations.clear();
            reservationStatusMap.clear();
            
            // Load reservations from database
            allReservations.addAll(serviceReservation.recuperer());
            
            // Initialize with random statuses for demo
            for (ReservationFoyer reservation : allReservations) {
                // By default, all are pending
                reservationStatusMap.put(reservation.getIdReservation(), "En attente");
            }
            
            // Create filtered list
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
                // If search field is empty, show all reservations
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase();
                
                try {
                    // Search by ID
                    if (String.valueOf(reservation.getIdReservation()).contains(lowerCaseFilter)) {
                        return true;
                    }
                    
                    // Search by foyer name
                    Foyer foyer = serviceFoyer.getFoyerById(reservation.getFoyerId());
                    if (foyer != null && foyer.getNom().toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                    
                    // Search by student ID
                    if (String.valueOf(reservation.getIdEtudiant()).contains(lowerCaseFilter)) {
                        return true;
                    }
                    
                    // Search by dates
                    if (reservation.getDateDebut().format(dateFormatter).contains(lowerCaseFilter) ||
                        reservation.getDateFin().format(dateFormatter).contains(lowerCaseFilter) ||
                        reservation.getDateReservation().format(dateFormatter).contains(lowerCaseFilter)) {
                        return true;
                    }
                    
                    // Search by status
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
    
    private void acceptReservation(ReservationFoyer reservation) {
        try {
            // Update status in our map (in a real app, you would update this in the database)
            reservationStatusMap.put(reservation.getIdReservation(), "Acceptée");
            
            // Refresh table to show updated status
            reservationTable.refresh();
            
            showAlert("Succès", "Réservation #" + reservation.getIdReservation() + " acceptée avec succès", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'acceptation de la réservation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void declineReservation(ReservationFoyer reservation) {
        try {
            // Afficher une confirmation avant de supprimer
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Êtes-vous sûr de vouloir refuser et supprimer définitivement la réservation #" + reservation.getIdReservation() + " ?");
            
            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                // Supprimer la réservation de la base de données
                boolean deleted = serviceReservation.supprimer(reservation);
                
                if (deleted) {
                    // Supprimer de la map de statuts
                    reservationStatusMap.remove(reservation.getIdReservation());
                    
                    // Supprimer de la liste observable
                    allReservations.remove(reservation);
                    
                    // Rafraîchir la table
                    reservationTable.refresh();
                    
                    showAlert("Succès", "Réservation #" + reservation.getIdReservation() + " refusée et supprimée définitivement", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Erreur", "Impossible de supprimer la réservation de la base de données", Alert.AlertType.ERROR);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la suppression de la réservation: " + e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur inattendue: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void retourAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyer.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Add fade transition
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
