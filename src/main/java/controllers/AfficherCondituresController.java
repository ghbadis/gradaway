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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.Candidature;
import entities.Universite;
import Services.CandidatureService;
import Services.ServiceUniversite;

import java.io.File;
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
    private TableColumn<Candidature, Integer> universiteImageColumn;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private Button retourButton;
    
    @FXML
    private Button cardsButton;
    
    private CandidatureService candidatureService;
    private ServiceUniversite serviceUniversite;
    private ObservableList<Candidature> candidatureList = FXCollections.observableArrayList();
    private FilteredList<Candidature> filteredCandidatures;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Initializing AfficherCondituresController...");
        candidatureService = new CandidatureService();
        serviceUniversite = new ServiceUniversite();
        
        // Configuration des colonnes du TableView
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date_de_remise_c"));
        universiteColumn.setCellValueFactory(new PropertyValueFactory<>("universite"));
        domaineColumn.setCellValueFactory(new PropertyValueFactory<>("domaine"));
        
        // Configure university image column - use id_universite as the cell value
        universiteImageColumn.setCellValueFactory(new PropertyValueFactory<>("id_universite"));
        
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
        
        // Custom cell factory for the university image column
        universiteImageColumn.setCellFactory(column -> new TableCell<Candidature, Integer>() {
            private final ImageView imageView = new ImageView();
            
            {
                // Configure the image view
                imageView.setFitHeight(80);
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);
                
                // Center the image in the cell
                setGraphic(imageView);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                setAlignment(javafx.geometry.Pos.CENTER);
            }
            
            @Override
            protected void updateItem(Integer universiteId, boolean empty) {
                super.updateItem(universiteId, empty);
                
                if (empty || universiteId == null) {
                    imageView.setImage(null);
                    setGraphic(null);
                } else {
                    try {
                        // Get the university info
                        Universite universite = serviceUniversite.recuperer(universiteId);
                        
                        if (universite != null && universite.getPhotoPath() != null && !universite.getPhotoPath().isEmpty()) {
                            // Try to load from resources folder
                            String defaultImagePath = "/images/default_university.png";
                            try {
                                // Try to load from resources
                                URL photoUrl = getClass().getResource("/" + universite.getPhotoPath());
                                if (photoUrl != null) {
                                    Image universityImage = new Image(photoUrl.toExternalForm());
                                    imageView.setImage(universityImage);
                                } else {
                                    // Try as a file path
                                    File photoFile = new File("src/main/resources/" + universite.getPhotoPath());
                                    if (photoFile.exists()) {
                                        Image universityImage = new Image(photoFile.toURI().toString());
                                        imageView.setImage(universityImage);
                                    } else {
                                        // If file not found, use default image
                                        URL defaultUrl = getClass().getResource(defaultImagePath);
                                        if (defaultUrl != null) {
                                            Image defaultImage = new Image(defaultUrl.toExternalForm());
                                            imageView.setImage(defaultImage);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                // Load default image on error
                                URL defaultUrl = getClass().getResource(defaultImagePath);
                                if (defaultUrl != null) {
                                    Image defaultImage = new Image(defaultUrl.toExternalForm());
                                    imageView.setImage(defaultImage);
                                }
                                System.err.println("Error loading image: " + e.getMessage());
                            }
                        } else {
                            // Load default image if no photo path
                            URL defaultUrl = getClass().getResource("/images/default_university.png");
                            if (defaultUrl != null) {
                                Image defaultImage = new Image(defaultUrl.toExternalForm());
                                imageView.setImage(defaultImage);
                            }
                        }
                        
                        setGraphic(imageView);
                    } catch (Exception e) {
                        e.printStackTrace();
                        setGraphic(null);
                    }
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
    
    @FXML
    private void handleCardsButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listcandidaturecards.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) cardsButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de Navigation", 
                    "Impossible de charger la vue en cartes", e.getMessage());
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