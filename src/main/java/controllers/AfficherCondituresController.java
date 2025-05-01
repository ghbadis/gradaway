package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Candidature;
import Services.CandidatureService;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherCondituresController implements Initializable {

    @FXML
    private TableView<Candidature> candidatureTable;
    
    @FXML
    private TableColumn<Candidature, String> nomColumn;
    
    @FXML
    private TableColumn<Candidature, String> prenomColumn;
    
    @FXML
    private TableColumn<Candidature, String> emailColumn;
    
    @FXML
    private TableColumn<Candidature, Date> dateColumn;
    
    @FXML
    private TableColumn<Candidature, String> universiteColumn;
    
    @FXML
    private TableColumn<Candidature, String> domaineColumn;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private Button retourButton;
    
    private CandidatureService candidatureService;
    private ObservableList<Candidature> candidatureList = FXCollections.observableArrayList();
    private FilteredList<Candidature> filteredCandidatures;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Initializing AfficherCondituresController...");
        candidatureService = new CandidatureService();
        
        // Configuration des colonnes du TableView
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date_de_remise_c"));
        universiteColumn.setCellValueFactory(new PropertyValueFactory<>("universite"));
        domaineColumn.setCellValueFactory(new PropertyValueFactory<>("domaine"));
        
        // Use simplified cell factories without custom styling
        nomColumn.setCellFactory(column -> new TableCell<Candidature, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item != null ? item : "");
                }
            }
        });
        
        prenomColumn.setCellFactory(column -> new TableCell<Candidature, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item != null ? item : "");
                }
            }
        });
        
        emailColumn.setCellFactory(column -> new TableCell<Candidature, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item != null ? item : "");
                }
            }
        });
        
        dateColumn.setCellFactory(column -> new TableCell<Candidature, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item != null ? item.toString() : "");
                }
            }
        });
        
        universiteColumn.setCellFactory(column -> new TableCell<Candidature, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item != null ? item : "");
                }
            }
        });
        
        domaineColumn.setCellFactory(column -> new TableCell<Candidature, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item != null ? item : "");
                }
            }
        });
        
        // Default row factory
        candidatureTable.setRowFactory(tv -> new TableRow<>());
        
        // Load data
        loadCandidatures();
        
        // Setup the filtered list if searchField is available
        if (searchField != null) {
            filteredCandidatures = new FilteredList<>(candidatureList, p -> true);
            candidatureTable.setItems(filteredCandidatures);
            
            // Add search text listener
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) {
                    filteredCandidatures.setPredicate(p -> true);
                }
            });
        } else {
            candidatureTable.setItems(candidatureList);
        }
        
        System.out.println("AfficherCondituresController initialized successfully.");
    }
    
    @FXML
    private void handleSearchButton() {
        if (searchField != null) {
            String searchText = searchField.getText().toLowerCase();
            
            if (searchText == null || searchText.isEmpty()) {
                filteredCandidatures.setPredicate(p -> true);
            } else {
                filteredCandidatures.setPredicate(candidature -> 
                    // Use the available properties from Candidature class
                    candidature.getDomaine().toLowerCase().contains(searchText) ||
                    String.valueOf(candidature.getUser_id()).contains(searchText) ||
                    String.valueOf(candidature.getId_universite()).contains(searchText) ||
                    String.valueOf(candidature.getId_dossier()).contains(searchText)
                );
            }
        }
    }
    
    private void loadCandidatures() {
        try {
            List<Candidature> candidatures = candidatureService.getAllCandidatures();
            System.out.println("DEBUGGING: Retrieved " + candidatures.size() + " candidatures from service");
            
            // Clear our observable list
            candidatureList.clear();
            
            // Manually add each candidature to ensure they're properly loaded
            for (Candidature c : candidatures) {
                candidatureList.add(c);
                System.out.println("Added to list: User ID " + c.getUser_id() + " - Domain " + c.getDomaine());
            }
            
            // Force the TableView to refresh with the new data
            candidatureTable.refresh();
            
            if (searchField != null) {
                // Set the filtered list as the TableView's items source
                filteredCandidatures = new FilteredList<>(candidatureList, p -> true);
                
                // IMPORTANT - explicitly set items
                candidatureTable.setItems(null); // Clear first
                candidatureTable.setItems(filteredCandidatures);
            } else {
                candidatureTable.setItems(null); // Clear first
                candidatureTable.setItems(candidatureList);
            }
            
            // Debug output - print TableView contents
            System.out.println("DEBUGGING TABLE: Item count: " + candidatureTable.getItems().size());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les candidatures", e.getMessage());
        }
    }
    
    @FXML
    private void handleRefreshButton() {
        loadCandidatures();
    }
    
    @FXML
    private void handleRetourButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/adminconditature.fxml"));
            Stage stage = (Stage) retourButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible de retourner à l'écran précédent", 
                    e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 