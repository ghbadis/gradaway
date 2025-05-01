package controllers;

import entities.Universite;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import Services.ServiceUniversite;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ModifierUniversiteController implements Initializable {

    @FXML
    private TextField idField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField villeField;
    @FXML
    private TextField adresseField;
    @FXML
    private TextField domaineField;
    @FXML
    private TextField fraisField;
    
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
    private TextField searchFieldTable;
    @FXML
    private Button searchTableButton;
    @FXML
    private Button refreshTableButton;
    @FXML
    private Button modifierButton;
    @FXML
    private Button cancelButton;
    
    private final ServiceUniversite serviceUniversite = new ServiceUniversite();
    private Universite currentUniversite;
    private ObservableList<Universite> universiteList = FXCollections.observableArrayList();
    private FilteredList<Universite> filteredUniversites;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize controller
        modifierButton.setDisable(true); // Disable modifier button until a university is found
        
        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_universite"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("Nom"));
        villeColumn.setCellValueFactory(new PropertyValueFactory<>("Ville"));
        adresseColumn.setCellValueFactory(new PropertyValueFactory<>("Adresse_universite"));
        domaineColumn.setCellValueFactory(new PropertyValueFactory<>("Domaine"));
        fraisColumn.setCellValueFactory(new PropertyValueFactory<>("Frais"));
        
        // Debug column mapping
        System.out.println("INITIALIZE - Mapping columns to properties:");
        System.out.println("ID Column -> id_universite");
        System.out.println("Nom Column -> Nom");
        System.out.println("Ville Column -> Ville");
        System.out.println("Adresse Column -> Adresse_universite");
        System.out.println("Domaine Column -> Domaine");
        System.out.println("Frais Column -> Frais");
        
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
        
        // Setup the filtered list
        filteredUniversites = new FilteredList<>(universiteList, p -> true);
        universiteTable.setItems(filteredUniversites);
        
        // Add search text listener for real-time filtering
        searchFieldTable.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                filteredUniversites.setPredicate(p -> true);
            }
        });
        
        // Setup table selection listener
        universiteTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> handleTableSelection(newValue));
    }
    
    private void handleTableSelection(Universite selectedUniversite) {
        if (selectedUniversite != null) {
            // Fill the form with university data
            currentUniversite = selectedUniversite;
            idField.setText(String.valueOf(selectedUniversite.getId_universite()));
            nomField.setText(selectedUniversite.getNom());
            villeField.setText(selectedUniversite.getVille());
            adresseField.setText(selectedUniversite.getAdresse_universite());
            domaineField.setText(selectedUniversite.getDomaine());
            fraisField.setText(String.valueOf(selectedUniversite.getFrais()));
            
            // Enable the modifier button
            modifierButton.setDisable(false);
        }
    }
    
    @FXML
    private void handleSearchTableButton() {
        String searchText = searchFieldTable.getText().toLowerCase();
        
        if (searchText == null || searchText.isEmpty()) {
            filteredUniversites.setPredicate(p -> true);
        } else {
            filteredUniversites.setPredicate(universite -> 
                universite.getNom().toLowerCase().contains(searchText) ||
                universite.getDomaine().toLowerCase().contains(searchText) ||
                universite.getVille().toLowerCase().contains(searchText)
            );
        }
    }
    
    @FXML
    private void handleRefreshTableButton() {
        searchFieldTable.clear();
        loadUniversites();
        filteredUniversites.setPredicate(p -> true);
    }
    
    private void loadUniversites() {
        try {
            List<Universite> universities = serviceUniversite.recuperer();
            System.out.println("DEBUGGING: Retrieved " + universities.size() + " universities from service");
            
            // Clear our observable list
            universiteList.clear();
            
            // Manually add each university to ensure they're properly loaded
            for (Universite u : universities) {
                universiteList.add(u);
                System.out.println("Added to list: " + u.getId_universite() + " - " + u.getNom());
            }
            
            // Force the TableView to refresh with the new data
            universiteTable.refresh();
            
            // Set the filtered list as the TableView's items source
            filteredUniversites = new FilteredList<>(universiteList, p -> true);
            
            // IMPORTANT - explicitly set items
            universiteTable.setItems(null); // Clear first
            universiteTable.setItems(filteredUniversites);
            
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
    
    @FXML
    private void handleModifierButton() {
        if (currentUniversite == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez d'abord sélectionner une université");
            return;
        }
        
        if (validateFields()) {
            try {
                String nom = nomField.getText();
                String ville = villeField.getText();
                String adresse = adresseField.getText();
                String domaine = domaineField.getText();
                double frais = Double.parseDouble(fraisField.getText());
                
                // Update current university
                currentUniversite.setNom(nom);
                currentUniversite.setVille(ville);
                currentUniversite.setAdresse_universite(adresse);
                currentUniversite.setDomaine(domaine);
                currentUniversite.setFrais(frais);
                
                // Call service to modify university
                serviceUniversite.modifier(currentUniversite);
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Université modifiée avec succès!");
                
                // Refresh the table
                loadUniversites();
                
                // Clear the form and reset state
                clearFields();
                currentUniversite = null;
                modifierButton.setDisable(true);
                
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de format", "Les frais doivent être un nombre valide");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de base de données", e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleCancelButton() {
        clearFields();
        currentUniversite = null;
        modifierButton.setDisable(true);
        universiteTable.getSelectionModel().clearSelection();
    }
    
    private boolean validateFields() {
        if (nomField.getText().isEmpty() || villeField.getText().isEmpty() ||
                adresseField.getText().isEmpty() || domaineField.getText().isEmpty() ||
                fraisField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs manquants", "Veuillez remplir tous les champs");
            return false;
        }
        
        try {
            Double.parseDouble(fraisField.getText());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Format invalide", "Les frais doivent être un nombre valide");
            return false;
        }
        
        return true;
    }
    
    private void clearFields() {
        idField.clear();
        nomField.clear();
        villeField.clear();
        adresseField.clear();
        domaineField.clear();
        fraisField.clear();
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 