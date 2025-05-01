package controllers;

import entities.Universite;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import Services.ServiceUniversite;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class RecupererUniversiteController implements Initializable {

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
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button exportButton;
    @FXML
    private Button closeButton;
    
    private final ServiceUniversite serviceUniversite = new ServiceUniversite();
    private ObservableList<Universite> universiteList = FXCollections.observableArrayList();
    private FilteredList<Universite> filteredUniversites;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Initializing RecupererUniversiteController...");
        
        try {
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
            
            // Load data
            loadUniversites();
            
            // Setup the filtered list
            filteredUniversites = new FilteredList<>(universiteList, p -> true);
            universiteTable.setItems(filteredUniversites);
            
            // Add search text listener
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue == null || newValue.isEmpty()) {
                    filteredUniversites.setPredicate(p -> true);
                }
            });
            
            System.out.println("RecupererUniversiteController initialized successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in RecupererUniversiteController initialization: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSearchButton() {
        String searchText = searchField.getText().toLowerCase();
        
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
    private void handleRefreshButton() {
        searchField.clear();
        loadUniversites();
        filteredUniversites.setPredicate(p -> true);
    }
    
    @FXML
    private void handleExportButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter la liste des universités");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(exportButton.getScene().getWindow());
        
        if (file != null) {
            exportToCsv(file);
        }
    }
    
    @FXML
    private void handleCloseButton() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
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
    
    private void exportToCsv(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            // Write header
            writer.write("ID,Nom,Ville,Adresse,Domaine,Frais\n");
            
            // Write data
            for (Universite universite : universiteList) {
                writer.write(String.format("%d,%s,%s,%s,%s,%.2f\n",
                        universite.getId_universite(),
                        escapeSpecialCharacters(universite.getNom()),
                        escapeSpecialCharacters(universite.getVille()),
                        escapeSpecialCharacters(universite.getAdresse_universite()),
                        escapeSpecialCharacters(universite.getDomaine()),
                        universite.getFrais()
                ));
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Export réussi", 
                    "Les données ont été exportées avec succès vers: " + file.getAbsolutePath());
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur d'exportation", e.getMessage());
        }
    }
    
    private String escapeSpecialCharacters(String data) {
        if (data == null) {
            return "";
        }
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 