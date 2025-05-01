package controllers;

import Services.ServiceRestaurant;
import entities.Restaurant;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ListRestaurantController {

    @FXML private TableView<Restaurant> restaurantTable;
    @FXML private TableColumn<Restaurant, Integer> idColumn;
    @FXML private TableColumn<Restaurant, String> nomColumn;
    @FXML private TableColumn<Restaurant, String> adresseColumn;
    @FXML private TableColumn<Restaurant, String> villeColumn;
    @FXML private TableColumn<Restaurant, String> paysColumn;
    @FXML private TableColumn<Restaurant, Integer> capaciteColumn;
    @FXML private TableColumn<Restaurant, String> ouvertureColumn;
    @FXML private TableColumn<Restaurant, String> fermetureColumn;
    @FXML private TableColumn<Restaurant, String> telephoneColumn;
    @FXML private TableColumn<Restaurant, Void> actionColumn;
    
    @FXML private TextField searchField;
    @FXML private Button refreshBtn;
    @FXML private Button addBtn;
    
    private ServiceRestaurant serviceRestaurant;
    private ObservableList<Restaurant> restaurantList;
    private FilteredList<Restaurant> filteredRestaurants;
    
    @FXML
    void initialize() {
        serviceRestaurant = new ServiceRestaurant();
        
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("idRestaurant"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        villeColumn.setCellValueFactory(new PropertyValueFactory<>("ville"));
        paysColumn.setCellValueFactory(new PropertyValueFactory<>("pays"));
        capaciteColumn.setCellValueFactory(new PropertyValueFactory<>("capaciteTotale"));
        ouvertureColumn.setCellValueFactory(new PropertyValueFactory<>("horaireOuverture"));
        fermetureColumn.setCellValueFactory(new PropertyValueFactory<>("horaireFermeture"));
        telephoneColumn.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        
        // Set up action column with buttons
        setupActionColumn();
        
        // Load data
        loadRestaurants();
        
        // Set up search functionality
        setupSearch();
    }
    
    /**
     * Configure the action column with edit and delete buttons
     */
    private void setupActionColumn() {
        Callback<TableColumn<Restaurant, Void>, TableCell<Restaurant, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Restaurant, Void> call(final TableColumn<Restaurant, Void> param) {
                return new TableCell<>() {
                    private final Button editBtn = new Button("Modifier");
                    private final Button deleteBtn = new Button("Supprimer");
                    private final HBox pane = new HBox(5, editBtn, deleteBtn);
                    
                    {
                        // Style buttons
                        editBtn.setStyle("-fx-background-color: #FFC107; -fx-text-fill: white; -fx-background-radius: 15px;");
                        deleteBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-background-radius: 15px;");
                        pane.setPadding(new Insets(2));
                        
                        // Set event handlers
                        editBtn.setOnAction(event -> {
                            Restaurant restaurant = getTableView().getItems().get(getIndex());
                            navigateToEdit(restaurant);
                        });
                        
                        deleteBtn.setOnAction(event -> {
                            Restaurant restaurant = getTableView().getItems().get(getIndex());
                            confirmDelete(restaurant);
                        });
                    }
                    
                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            }
        };
        
        actionColumn.setCellFactory(cellFactory);
    }
    
    /**
     * Load restaurants from the database
     */
    private void loadRestaurants() {
        try {
            List<Restaurant> restaurants = serviceRestaurant.recuperer();
            restaurantList = FXCollections.observableArrayList(restaurants);
            filteredRestaurants = new FilteredList<>(restaurantList);
            restaurantTable.setItems(filteredRestaurants);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des restaurants: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Set up search functionality
     */
    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredRestaurants.setPredicate(createPredicate(newValue));
        });
    }
    
    /**
     * Create a predicate for searching restaurants
     */
    private Predicate<Restaurant> createPredicate(String searchText) {
        return restaurant -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }
            
            String lowerCaseFilter = searchText.toLowerCase();
            
            return restaurant.getNom().toLowerCase().contains(lowerCaseFilter) ||
                   restaurant.getVille().toLowerCase().contains(lowerCaseFilter) ||
                   restaurant.getPays().toLowerCase().contains(lowerCaseFilter) ||
                   restaurant.getAdresse().toLowerCase().contains(lowerCaseFilter);
        };
    }
    
    /**
     * Navigate to the edit restaurant view
     */
    private void navigateToEdit(Restaurant restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierRestaurant.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the restaurant
            ModifierRestaurantController controller = loader.getController();
            controller.setRestaurant(restaurant);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) restaurantTable.getScene().getWindow();
            
            // Transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            stage.setScene(scene);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la vue: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Confirm and delete a restaurant
     */
    private void confirmDelete(Restaurant restaurant) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le restaurant " + restaurant.getNom());
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce restaurant ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceRestaurant.supprimer(restaurant);
                loadRestaurants(); // Refresh the table
                showAlert("Succès", "Restaurant supprimé avec succès !", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Refresh the restaurant table
     */
    @FXML
    void refreshTable(ActionEvent event) {
        loadRestaurants();
    }
    
    /**
     * Navigate to the add restaurant view
     */
    @FXML
    void navigateToAdd(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/AjouterRestaurant.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            stage.setScene(scene);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la vue: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Show an alert dialog
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 