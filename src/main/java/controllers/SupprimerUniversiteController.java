package controllers;

import entities.Universite;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import Services.ServiceUniversite;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SupprimerUniversiteController implements Initializable {

    @FXML
    private TableView<Universite> universiteTable;
    @FXML
    private TableColumn<Universite, Integer> idColumn;
    @FXML
    private TableColumn<Universite, String> nomColumn;
    @FXML
    private TableColumn<Universite, String> villeColumn;
    @FXML
    private TableColumn<Universite, String> adresseColumn;
    @FXML
    private TableColumn<Universite, String> domaineColumn;
    @FXML
    private TableColumn<Universite, Double> fraisColumn;
    
    @FXML
    private VBox detailsBox;
    @FXML
    private Label selectedUniversiteLabel;
    @FXML
    private Label universiteInfoLabel;
    
    @FXML
    private Button supprimerButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button closeButton;
    
    private final ServiceUniversite serviceUniversite = new ServiceUniversite();
    private ObservableList<Universite> universiteList = FXCollections.observableArrayList();
    private Universite selectedUniversite;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_universite"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("Nom"));
        villeColumn.setCellValueFactory(new PropertyValueFactory<>("Ville"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("Adresse_universite"));
        domaineColumn.setCellValueFactory(new PropertyValueFactory<>("Domaine"));
        fraisColumn.setCellValueFactory(new PropertyValueFactory<>("Frais"));
        
        // Use simplified cell factories without custom styling
        idColumn.setCellFactory(column -> new TableCell<Universite, Integer>() {
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
        
        nomColumn.setCellFactory(column -> new TableCell<Universite, String>() {
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
        
        villeColumn.setCellFactory(column -> new TableCell<Universite, String>() {
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
        
        adresseColumn.setCellFactory(column -> new TableCell<Universite, String>() {
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
        
        domaineColumn.setCellFactory(column -> new TableCell<Universite, String>() {
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
        
        fraisColumn.setCellFactory(column -> new TableCell<Universite, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item != null ? String.valueOf(item) : "");
                }
            }
        });
        
        // Default row factory
        universiteTable.setRowFactory(tv -> new TableRow<>());
        
        // Load universities
        loadUniversites();
        
        // Setup table selection listener
        universiteTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedUniversite = newSelection;
                updateSelectionDetails();
                supprimerButton.setDisable(false);
            } else {
                selectedUniversite = null;
                detailsBox.setVisible(false);
                supprimerButton.setDisable(true);
            }
        });
    }
    
    @FXML
    private void handleSupprimerButton() {
        if (selectedUniversite == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une université à supprimer");
            return;
        }
        
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmer la suppression");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'université '" + 
                                    selectedUniversite.getNom() + "' ?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Delete the university
            serviceUniversite.supprimer(selectedUniversite);

            // Update the UI
            universiteList.remove(selectedUniversite);
            selectedUniversite = null;
            detailsBox.setVisible(false);
            supprimerButton.setDisable(true);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Université supprimée avec succès");
        }
    }
    
    @FXML
    private void handleRefreshButton() {
        loadUniversites();
        selectedUniversite = null;
        detailsBox.setVisible(false);
        supprimerButton.setDisable(true);
    }
    
    @FXML
    private void handleCloseButton() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    private void loadUniversites() {
        try {
            List<Universite> universities = serviceUniversite.getAllUniversites();
            
            // Clear our observable list
            universiteList.clear();
            
            // Manually add each university to ensure they're properly loaded
            for (Universite u : universities) {
                universiteList.add(u);
                System.out.println("Added to list: " + u.getId_universite() + " - " + u.getNom());
            }
            
            // Force the TableView to refresh with the new data
            universiteTable.refresh();
            
            // IMPORTANT - explicitly set items
            universiteTable.setItems(null); // Clear first
            universiteTable.setItems(universiteList);
            
            // Debug output - print TableView contents
            System.out.println("DEBUGGING TABLE: Item count: " + universiteTable.getItems().size());
            
            // If table is empty, add a test university directly
            if (universiteTable.getItems().isEmpty() && !universities.isEmpty()) {
                System.out.println("WARNING: TableView empty despite data in list. Adding test university directly...");
                // Create a test university
                Universite testUniv = new Universite(999, "Test University", "Test City", 
                                      "Test Address", "Test Domain", 1000.0);
                
                // Create new list with test university
                ObservableList<Universite> testList = FXCollections.observableArrayList();
                testList.add(testUniv);
                
                // Set the test list to the TableView
                universiteTable.setItems(testList);
                
                System.out.println("Test university added directly to TableView");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de base de données", e.getMessage());
        }
    }
    
    private void updateSelectionDetails() {
        if (selectedUniversite != null) {
            String info = String.format("ID: %d\nNom: %s\nVille: %s\nAdresse: %s\nDomaine: %s\nFrais: %.2f",
                    selectedUniversite.getId_universite(),
                    selectedUniversite.getNom(),
                    selectedUniversite.getVille(),
                    selectedUniversite.getAdresse_universite(),
                    selectedUniversite.getDomaine(),
                    selectedUniversite.getFrais());
            
            universiteInfoLabel.setText(info);
            detailsBox.setVisible(true);
        } else {
            detailsBox.setVisible(false);
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 