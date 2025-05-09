package controllers;

import entities.Dossier;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import Services.ServiceDossier;
import Services.ServiceUser;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.io.File;

public class AdminDossiercontroller implements Initializable {

    @FXML
    private TableView<Dossier> dossierTable;
    
    @FXML
    private TableColumn<Dossier, Integer> idColumn;
    
    @FXML
    private TableColumn<Dossier, String> nomColumn;
    
    @FXML
    private TableColumn<Dossier, String> prenomColumn;
    
    @FXML
    private TableColumn<Dossier, String> cinColumn;
    
    @FXML
    private TableColumn<Dossier, String> photoColumn;
    
    @FXML
    private TableColumn<Dossier, String> diplomeBacColumn;
    
    @FXML
    private TableColumn<Dossier, String> releveNoteColumn;
    
    @FXML
    private TableColumn<Dossier, String> diplomeObtenusColumn;
    
    @FXML
    private TableColumn<Dossier, String> lettreMotivationsColumn;
    
    @FXML
    private TableColumn<Dossier, String> dossierSanteColumn;
    
    @FXML
    private TableColumn<Dossier, String> cvColumn;
    
    @FXML
    private TableColumn<Dossier, String> dateDepotColumn;

    @FXML
    private TableColumn<Dossier, Void> actionColumn;

    private ServiceDossier serviceDossier;
    private ServiceUser serviceUser;
    private ObservableList<Dossier> dossiers;
    private Map<Integer, User> userCache;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceDossier = new ServiceDossier();
        serviceUser = new ServiceUser();
        dossiers = FXCollections.observableArrayList();
        userCache = new HashMap<>();

        // Hide ID column
        idColumn.setVisible(false);
        
        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_dossier"));
        
        // Configuration des colonnes nom et prénom
        nomColumn.setCellValueFactory(cellData -> {
            Dossier dossier = cellData.getValue();
            if (dossier != null) {
                try {
                    int userId = dossier.getId_etudiant();
                    System.out.println("Tentative de récupération de l'utilisateur avec l'ID: " + userId);
                    User user = getUserFromCache(userId);
                    return new SimpleStringProperty(user != null ? user.getNom() : "N/A");
                } catch (Exception e) {
                    System.err.println("Erreur lors de la récupération du nom: " + e.getMessage());
                    return new SimpleStringProperty("N/A");
                }
            }
            return new SimpleStringProperty("N/A");
        });
        
        prenomColumn.setCellValueFactory(cellData -> {
            Dossier dossier = cellData.getValue();
            if (dossier != null) {
                try {
                    int userId = dossier.getId_etudiant();
                    System.out.println("Tentative de récupération de l'utilisateur avec l'ID: " + userId);
                    User user = getUserFromCache(userId);
                    return new SimpleStringProperty(user != null ? user.getPrenom() : "N/A");
                } catch (Exception e) {
                    System.err.println("Erreur lors de la récupération du prénom: " + e.getMessage());
                    return new SimpleStringProperty("N/A");
                }
            }
            return new SimpleStringProperty("N/A");
        });

        // Set cell value factories for image columns..
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));
        cinColumn.setCellValueFactory(new PropertyValueFactory<>("cin"));
        diplomeBacColumn.setCellValueFactory(new PropertyValueFactory<>("diplome_baccalauréat"));
        releveNoteColumn.setCellValueFactory(new PropertyValueFactory<>("releve_note"));
        diplomeObtenusColumn.setCellValueFactory(new PropertyValueFactory<>("diplome_obtenus"));
        lettreMotivationsColumn.setCellValueFactory(new PropertyValueFactory<>("lettre_motivations"));
        dossierSanteColumn.setCellValueFactory(new PropertyValueFactory<>("dossier_sante"));
        cvColumn.setCellValueFactory(new PropertyValueFactory<>("cv"));
        dateDepotColumn.setCellValueFactory(new PropertyValueFactory<>("datedepot"));

        // Configuration de la colonne d'action (boutons modifier et supprimer)
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button modifyButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttonBox = new HBox(10); // 10 pixels spacing between buttons

            {
                modifyButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");
                
                modifyButton.setOnAction(event -> {
                    Dossier dossier = getTableView().getItems().get(getIndex());
                    showModifyDialog(dossier);
                });
                
                deleteButton.setOnAction(event -> {
                    Dossier dossier = getTableView().getItems().get(getIndex());
                    try {
                        serviceDossier.supprimer(dossier);
                        dossiers.remove(dossier);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        System.err.println("Erreur lors de la suppression du dossier: " + e.getMessage());
                    }
                });

                buttonBox.getChildren().addAll(modifyButton, deleteButton);
                buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonBox);
            }
        });

        // Configuration des colonnes d'images
        configureImageColumn(photoColumn, "Photo");
        configureImageColumn(cinColumn, "CIN");
        configureImageColumn(diplomeBacColumn, "Diplôme Bac");
        configureImageColumn(releveNoteColumn, "Relevé Notes");
        configureImageColumn(diplomeObtenusColumn, "Diplômes Obtenus");
        configureImageColumn(lettreMotivationsColumn, "Lettre Motivation");
        configureImageColumn(dossierSanteColumn, "Dossier Santé");
        configureImageColumn(cvColumn, "CV");

        // Load data
        loadDossiers();
    }

    private User getUserFromCache(int userId) {
        try {
            if (userId <= 0) {
                System.err.println("ID utilisateur invalide: " + userId);
                return null;
            }

            if (userCache.containsKey(userId)) {
                return userCache.get(userId);
            }

            User user = serviceUser.getUserById(userId);
            if (user != null) {
                userCache.put(userId, user);
                System.out.println("Utilisateur chargé: " + user.getNom() + " " + user.getPrenom());
            } else {
                System.err.println("Aucun utilisateur trouvé pour l'ID: " + userId);
            }
            return user;
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération de l'utilisateur " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Erreur inattendue lors de la récupération de l'utilisateur " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void configureImageColumn(TableColumn<Dossier, String> column, String labelText) {
        column.setCellFactory(tc -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();
            private final VBox vbox = new VBox(5);

            {
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                imageView.setPreserveRatio(true);
                vbox.setAlignment(javafx.geometry.Pos.CENTER);
                vbox.getChildren().addAll(imageView, label);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);
                if (empty || imagePath == null) {
                    setGraphic(null);
                } else {
                    try {
                        String fullPath = imagePath.startsWith("file:") ? imagePath : "file:" + imagePath;
                        System.out.println("Chargement de l'image: " + fullPath);
                        Image image = new Image(fullPath);
                        imageView.setImage(image);
                        label.setText(labelText);
                        setGraphic(vbox);
                    } catch (Exception e) {
                        System.err.println("Erreur lors du chargement de l'image " + labelText + ": " + imagePath);
                        System.err.println("Message d'erreur: " + e.getMessage());
                        label.setText("Image non disponible");
                        imageView.setImage(null);
                        setGraphic(vbox);
                    }
                }
            }
        });
    }

    private void loadDossiers() {
        try {
            dossiers.clear();
            userCache.clear();
            
            // Charger les dossiers
            dossiers.addAll(serviceDossier.recuperer());
            System.out.println("Nombre de dossiers chargés: " + dossiers.size());
            
            // Préchargement des utilisateurs
            for (Dossier dossier : dossiers) {
                try {
                    int userId = dossier.getId_etudiant();
                    System.out.println("Préchargement de l'utilisateur avec l'ID: " + userId);
                    User user = getUserFromCache(userId);
                    if (user != null) {
                        System.out.println("Utilisateur préchargé: " + user.getNom() + " " + user.getPrenom());
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du préchargement de l'utilisateur: " + e.getMessage());
                }
            }
            
            dossierTable.setItems(dossiers);
            
            // Log des données pour le débogage
            for (Dossier dossier : dossiers) {
                System.out.println("\nDossier ID: " + dossier.getId_dossier());
                System.out.println("User ID: " + dossier.getId_etudiant());
                User user = getUserFromCache(dossier.getId_etudiant());
                System.out.println("User: " + (user != null ? user.getNom() + " " + user.getPrenom() : "N/A"));
                System.out.println("Photo: " + dossier.getPhoto());
                System.out.println("CIN: " + dossier.getCin());
                System.out.println("Diplôme Bac: " + dossier.getDiplome_baccalauréat());
                System.out.println("Relevé Notes: " + dossier.getReleve_note());
                System.out.println("Diplômes Obtenus: " + dossier.getDiplome_obtenus());
                System.out.println("Lettre Motivation: " + dossier.getLettre_motivations());
                System.out.println("Dossier Santé: " + dossier.getDossier_sante());
                System.out.println("CV: " + dossier.getCv());
                System.out.println("------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des dossiers: " + e.getMessage());
        }
    }

    private void showModifyDialog(Dossier dossier) {
        Dialog<Dossier> dialog = new Dialog<>();
        dialog.setTitle("Modifier le Dossier");
        dialog.setHeaderText("Modifier les informations du dossier");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the custom dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Create text fields for each document
        javafx.scene.control.TextField photoField = new javafx.scene.control.TextField(dossier.getPhoto());
        javafx.scene.control.TextField cinField = new javafx.scene.control.TextField(dossier.getCin());
        javafx.scene.control.TextField diplomeBacField = new javafx.scene.control.TextField(dossier.getDiplome_baccalauréat());
        javafx.scene.control.TextField releveNoteField = new javafx.scene.control.TextField(dossier.getReleve_note());
        javafx.scene.control.TextField diplomeObtenusField = new javafx.scene.control.TextField(dossier.getDiplome_obtenus());
        javafx.scene.control.TextField lettreMotivationsField = new javafx.scene.control.TextField(dossier.getLettre_motivations());
        javafx.scene.control.TextField dossierSanteField = new javafx.scene.control.TextField(dossier.getDossier_sante());
        javafx.scene.control.TextField cvField = new javafx.scene.control.TextField(dossier.getCv());

        // Add fields to grid
        grid.add(new Label("Photo:"), 0, 0);
        grid.add(photoField, 1, 0);
        grid.add(new Label("CIN:"), 0, 1);
        grid.add(cinField, 1, 1);
        grid.add(new Label("Diplôme Bac:"), 0, 2);
        grid.add(diplomeBacField, 1, 2);
        grid.add(new Label("Relevé Notes:"), 0, 3);
        grid.add(releveNoteField, 1, 3);
        grid.add(new Label("Diplômes Obtenus:"), 0, 4);
        grid.add(diplomeObtenusField, 1, 4);
        grid.add(new Label("Lettre Motivation:"), 0, 5);
        grid.add(lettreMotivationsField, 1, 5);
        grid.add(new Label("Dossier Santé:"), 0, 6);
        grid.add(dossierSanteField, 1, 6);
        grid.add(new Label("CV:"), 0, 7);
        grid.add(cvField, 1, 7);

        // Add file chooser buttons
        Button photoButton = new Button("Parcourir");
        Button cinButton = new Button("Parcourir");
        Button diplomeBacButton = new Button("Parcourir");
        Button releveNoteButton = new Button("Parcourir");
        Button diplomeObtenusButton = new Button("Parcourir");
        Button lettreMotivationsButton = new Button("Parcourir");
        Button dossierSanteButton = new Button("Parcourir");
        Button cvButton = new Button("Parcourir");

        // Add browse buttons to grid
        grid.add(photoButton, 2, 0);
        grid.add(cinButton, 2, 1);
        grid.add(diplomeBacButton, 2, 2);
        grid.add(releveNoteButton, 2, 3);
        grid.add(diplomeObtenusButton, 2, 4);
        grid.add(lettreMotivationsButton, 2, 5);
        grid.add(dossierSanteButton, 2, 6);
        grid.add(cvButton, 2, 7);

        // Configure file choosers
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("PDF", "*.pdf"),
            new FileChooser.ExtensionFilter("Tous les fichiers", "*.*")
        );

        // Set up file chooser actions
        photoButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                photoField.setText(file.getAbsolutePath());
            }
        });

        cinButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                cinField.setText(file.getAbsolutePath());
            }
        });

        diplomeBacButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                diplomeBacField.setText(file.getAbsolutePath());
            }
        });

        releveNoteButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                releveNoteField.setText(file.getAbsolutePath());
            }
        });

        diplomeObtenusButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                diplomeObtenusField.setText(file.getAbsolutePath());
            }
        });

        lettreMotivationsButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                lettreMotivationsField.setText(file.getAbsolutePath());
            }
        });

        dossierSanteButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                dossierSanteField.setText(file.getAbsolutePath());
            }
        });

        cvButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(dialog.getDialogPane().getScene().getWindow());
            if (file != null) {
                cvField.setText(file.getAbsolutePath());
            }
        });

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a Dossier when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                dossier.setPhoto(photoField.getText());
                dossier.setCin(cinField.getText());
                dossier.setDiplome_baccalauréat(diplomeBacField.getText());
                dossier.setReleve_note(releveNoteField.getText());
                dossier.setDiplome_obtenus(diplomeObtenusField.getText());
                dossier.setLettre_motivations(lettreMotivationsField.getText());
                dossier.setDossier_sante(dossierSanteField.getText());
                dossier.setCv(cvField.getText());
                return dossier;
            }
            return null;
        });

        Optional<Dossier> result = dialog.showAndWait();

        result.ifPresent(modifiedDossier -> {
            try {
                serviceDossier.modifier(modifiedDossier);
                // Refresh the table
                loadDossiers();
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur lors de la modification");
                alert.setContentText("Une erreur est survenue lors de la modification du dossier: " + e.getMessage());
                alert.showAndWait();
            }
        });
    }
}
