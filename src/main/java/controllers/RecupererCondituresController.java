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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Candidature;
import Services.CandidatureService;
import entities.User;
import Services.ServiceUser;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RecupererCondituresController implements Initializable {

    @FXML
    private TableView<Candidature> candidaturesTable;
    
    @FXML
    private TableColumn<Candidature, Integer> userColumn;
    
    @FXML
    private TableColumn<Candidature, Integer> dossierColumn;
    
    @FXML
    private TableColumn<Candidature, Integer> universiteColumn;
    
    @FXML
    private TableColumn<Candidature, Date> dateColumn;
    
    @FXML
    private TableColumn<Candidature, String> domaineColumn;
    
    @FXML
    private TableColumn<Candidature, String> statusColumn;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private Button retourButton;
    
    @FXML
    private Button logoutButton;
    
    private CandidatureService candidatureService;
    private ObservableList<Candidature> candidaturesList = FXCollections.observableArrayList();
    private FilteredList<Candidature> filteredCandidatures;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Initializing RecupererCondituresController...");
        candidatureService = new CandidatureService();
        
        // Configuration des colonnes du TableView
        userColumn.setCellValueFactory(new PropertyValueFactory<>("user_id"));
        dossierColumn.setCellValueFactory(new PropertyValueFactory<>("id_dossier"));
        universiteColumn.setCellValueFactory(new PropertyValueFactory<>("id_universite"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date_de_remise_c"));
        domaineColumn.setCellValueFactory(new PropertyValueFactory<>("domaine"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Use simplified cell factories without custom styling
        userColumn.setCellFactory(column -> new TableCell<Candidature, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item != null ? String.valueOf(item) : "");
                }
            }
        });
        
        dossierColumn.setCellFactory(column -> new TableCell<Candidature, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item != null ? String.valueOf(item) : "");
                }
            }
        });
        
        universiteColumn.setCellFactory(column -> new TableCell<Candidature, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item != null ? String.valueOf(item) : "");
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
        
        // Add status column with color styling
        statusColumn.setCellFactory(column -> new TableCell<Candidature, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(item.toUpperCase());
                    
                    // Style based on status
                    switch (item.toLowerCase()) {
                        case "accepted":
                            setTextFill(Color.WHITE);
                            setStyle("-fx-background-color: #4CAF50; -fx-font-weight: bold; -fx-alignment: center;");
                            break;
                        case "rejected":
                            setTextFill(Color.WHITE);
                            setStyle("-fx-background-color: #F44336; -fx-font-weight: bold; -fx-alignment: center;");
                            break;
                        case "pending":
                        default:
                            setTextFill(Color.BLACK);
                            setStyle("-fx-background-color: #FFC107; -fx-font-weight: bold; -fx-alignment: center;");
                            break;
                    }
                }
            }
        });
        
        // Default row factory
        candidaturesTable.setRowFactory(tv -> new TableRow<>());
        
        // Load data
        loadCandidatures();
        
        // Setup the filtered list if searchField is available
        if (searchField != null) {
            filteredCandidatures = new FilteredList<>(candidaturesList, p -> true);
            candidaturesTable.setItems(filteredCandidatures);
            
            // Add search text listener
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) {
                    filteredCandidatures.setPredicate(p -> true);
                }
            });
        } else {
            candidaturesTable.setItems(candidaturesList);
        }
        
        System.out.println("RecupererCondituresController initialized successfully.");
    }
    
    @FXML
    private void handleSearchButton() {
        if (searchField != null) {
            String searchText = searchField.getText().toLowerCase();
            
            if (searchText == null || searchText.isEmpty()) {
                filteredCandidatures.setPredicate(p -> true);
            } else {
                filteredCandidatures.setPredicate(candidature -> 
                    candidature.getDomaine().toLowerCase().contains(searchText) ||
                    String.valueOf(candidature.getUser_id()).contains(searchText) ||
                    String.valueOf(candidature.getId_universite()).contains(searchText) ||
                    (candidature.getStatus() != null && candidature.getStatus().toLowerCase().contains(searchText))
                );
            }
        }
    }
    
    private void loadCandidatures() {
        try {
            List<Candidature> candidatures = candidatureService.getAllCandidatures();
            System.out.println("DEBUGGING: Retrieved " + candidatures.size() + " candidatures from service");
            
            // Clear our observable list
            candidaturesList.clear();
            
            // Manually add each candidature to ensure they're properly loaded
            for (Candidature c : candidatures) {
                // Ensure status is set for each candidature
                if (c.getStatus() == null) {
                    c.setStatus("pending");
                }
                candidaturesList.add(c);
                System.out.println("Added to list: User ID " + c.getUser_id() + 
                                  " - Domain " + c.getDomaine() + 
                                  " - Status " + c.getStatus());
            }
            
            // Force the TableView to refresh with the new data
            candidaturesTable.refresh();
            
            if (searchField != null) {
                // Set the filtered list as the TableView's items source
                filteredCandidatures = new FilteredList<>(candidaturesList, p -> true);
                
                // IMPORTANT - explicitly set items
                candidaturesTable.setItems(null); // Clear first
                candidaturesTable.setItems(filteredCandidatures);
            } else {
                candidaturesTable.setItems(null); // Clear first
                candidaturesTable.setItems(candidaturesList);
            }
            
            // Debug output - print TableView contents
            System.out.println("DEBUGGING TABLE: Item count: " + candidaturesTable.getItems().size());
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
            Parent root = FXMLLoader.load(getClass().getResource("/accueil.fxml"));
            Stage stage = (Stage) retourButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", "Impossible de retourner à l'écran précédent", 
                    e.getMessage());
        }
    }
    
    @FXML
    private void handleLogoutButton() {
        try {
            candidatureService.closeConnection();
            Parent root = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Déconnexion", "Impossible de se déconnecter", 
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