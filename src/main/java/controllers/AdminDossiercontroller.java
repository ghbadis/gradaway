package controllers;

import entities.Dossier;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import Services.ServiceDossier;
import Services.ServiceUser;
import javafx.scene.control.TableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminDossiercontroller implements Initializable {

    @FXML
    private TableView<Dossier> dossierTable;
    
    @FXML
    private TableColumn<Dossier, Integer> idColumn;
    
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

    private ServiceDossier serviceDossier;
    private ServiceUser serviceUser;
    private ObservableList<Dossier> dossiers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceDossier = new ServiceDossier();
        serviceUser = new ServiceUser();
        dossiers = FXCollections.observableArrayList();

        // Initialize table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_dossier"));
        dateDepotColumn.setCellValueFactory(new PropertyValueFactory<>("datedepot"));

        // Custom cell factory for photo column
        photoColumn.setCellFactory(tc -> new TableCell<>() {
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
            protected void updateItem(String photoPath, boolean empty) {
                super.updateItem(photoPath, empty);
                if (empty || photoPath == null) {
                    setGraphic(null);
                } else {
                    try {
                        // Vérifier si le chemin est valide
                        if (photoPath.startsWith("file:")) {
                            Image image = new Image(photoPath);
                            imageView.setImage(image);
                            label.setText("Photo");
                            setGraphic(vbox);
                        } else {
                            // Essayer de charger l'image depuis le chemin relatif
                            String fullPath = "file:" + photoPath;
                            Image image = new Image(fullPath);
                            imageView.setImage(image);
                            label.setText("Photo");
                            setGraphic(vbox);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors du chargement de l'image: " + photoPath);
                        System.err.println("Message d'erreur: " + e.getMessage());
                        label.setText("Image non disponible");
                        imageView.setImage(null);
                        setGraphic(vbox);
                    }
                }
            }
        });

        // Custom cell factory for CIN column
        cinColumn.setCellFactory(tc -> new TableCell<>() {
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
            protected void updateItem(String cinPath, boolean empty) {
                super.updateItem(cinPath, empty);
                if (empty || cinPath == null) {
                    setGraphic(null);
                } else {
                    try {
                        if (cinPath.startsWith("file:")) {
                            Image image = new Image(cinPath);
                            imageView.setImage(image);
                            label.setText("CIN");
                            setGraphic(vbox);
                        } else {
                            String fullPath = "file:" + cinPath;
                            Image image = new Image(fullPath);
                            imageView.setImage(image);
                            label.setText("CIN");
                            setGraphic(vbox);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors du chargement de l'image CIN: " + cinPath);
                        System.err.println("Message d'erreur: " + e.getMessage());
                        label.setText("Image non disponible");
                        imageView.setImage(null);
                        setGraphic(vbox);
                    }
                }
            }
        });

        // Custom cell factory for diplome bac column
        diplomeBacColumn.setCellFactory(tc -> new TableCell<>() {
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
            protected void updateItem(String diplomePath, boolean empty) {
                super.updateItem(diplomePath, empty);
                if (empty || diplomePath == null) {
                    setGraphic(null);
                } else {
                    try {
                        if (diplomePath.startsWith("file:")) {
                            Image image = new Image(diplomePath);
                            imageView.setImage(image);
                            label.setText("Diplôme Bac");
                            setGraphic(vbox);
                        } else {
                            String fullPath = "file:" + diplomePath;
                            Image image = new Image(fullPath);
                            imageView.setImage(image);
                            label.setText("Diplôme Bac");
                            setGraphic(vbox);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors du chargement de l'image Diplôme Bac: " + diplomePath);
                        System.err.println("Message d'erreur: " + e.getMessage());
                        label.setText("Image non disponible");
                        imageView.setImage(null);
                        setGraphic(vbox);
                    }
                }
            }
        });

        // Custom cell factory for releve note column
        releveNoteColumn.setCellFactory(tc -> new TableCell<>() {
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
            protected void updateItem(String relevePath, boolean empty) {
                super.updateItem(relevePath, empty);
                if (empty || relevePath == null) {
                    setGraphic(null);
                } else {
                    try {
                        if (relevePath.startsWith("file:")) {
                            Image image = new Image(relevePath);
                            imageView.setImage(image);
                            label.setText("Relevé Notes");
                            setGraphic(vbox);
                        } else {
                            String fullPath = "file:" + relevePath;
                            Image image = new Image(fullPath);
                            imageView.setImage(image);
                            label.setText("Relevé Notes");
                            setGraphic(vbox);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors du chargement de l'image Relevé Notes: " + relevePath);
                        System.err.println("Message d'erreur: " + e.getMessage());
                        label.setText("Image non disponible");
                        imageView.setImage(null);
                        setGraphic(vbox);
                    }
                }
            }
        });

        // Custom cell factory for diplome obtenus column
        diplomeObtenusColumn.setCellFactory(tc -> new TableCell<>() {
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
            protected void updateItem(String diplomePath, boolean empty) {
                super.updateItem(diplomePath, empty);
                if (empty || diplomePath == null) {
                    setGraphic(null);
                } else {
                    try {
                        if (diplomePath.startsWith("file:")) {
                            Image image = new Image(diplomePath);
                            imageView.setImage(image);
                            label.setText("Diplômes Obtenus");
                            setGraphic(vbox);
                        } else {
                            String fullPath = "file:" + diplomePath;
                            Image image = new Image(fullPath);
                            imageView.setImage(image);
                            label.setText("Diplômes Obtenus");
                            setGraphic(vbox);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors du chargement de l'image Diplômes Obtenus: " + diplomePath);
                        System.err.println("Message d'erreur: " + e.getMessage());
                        label.setText("Image non disponible");
                        imageView.setImage(null);
                        setGraphic(vbox);
                    }
                }
            }
        });

        // Custom cell factory for lettre motivations column
        lettreMotivationsColumn.setCellFactory(tc -> new TableCell<>() {
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
            protected void updateItem(String lettrePath, boolean empty) {
                super.updateItem(lettrePath, empty);
                if (empty || lettrePath == null) {
                    setGraphic(null);
                } else {
                    try {
                        if (lettrePath.startsWith("file:")) {
                            Image image = new Image(lettrePath);
                            imageView.setImage(image);
                            label.setText("Lettre Motivation");
                            setGraphic(vbox);
                        } else {
                            String fullPath = "file:" + lettrePath;
                            Image image = new Image(fullPath);
                            imageView.setImage(image);
                            label.setText("Lettre Motivation");
                            setGraphic(vbox);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors du chargement de l'image Lettre Motivation: " + lettrePath);
                        System.err.println("Message d'erreur: " + e.getMessage());
                        label.setText("Image non disponible");
                        imageView.setImage(null);
                        setGraphic(vbox);
                    }
                }
            }
        });

        // Custom cell factory for dossier sante column
        dossierSanteColumn.setCellFactory(tc -> new TableCell<>() {
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
            protected void updateItem(String santePath, boolean empty) {
                super.updateItem(santePath, empty);
                if (empty || santePath == null) {
                    setGraphic(null);
                } else {
                    try {
                        if (santePath.startsWith("file:")) {
                            Image image = new Image(santePath);
                            imageView.setImage(image);
                            label.setText("Dossier Santé");
                            setGraphic(vbox);
                        } else {
                            String fullPath = "file:" + santePath;
                            Image image = new Image(fullPath);
                            imageView.setImage(image);
                            label.setText("Dossier Santé");
                            setGraphic(vbox);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors du chargement de l'image Dossier Santé: " + santePath);
                        System.err.println("Message d'erreur: " + e.getMessage());
                        label.setText("Image non disponible");
                        imageView.setImage(null);
                        setGraphic(vbox);
                    }
                }
            }
        });

        // Custom cell factory for CV column
        cvColumn.setCellFactory(tc -> new TableCell<>() {
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
            protected void updateItem(String cvPath, boolean empty) {
                super.updateItem(cvPath, empty);
                if (empty || cvPath == null) {
                    setGraphic(null);
                } else {
                    try {
                        if (cvPath.startsWith("file:")) {
                            Image image = new Image(cvPath);
                            imageView.setImage(image);
                            label.setText("CV");
                            setGraphic(vbox);
                        } else {
                            String fullPath = "file:" + cvPath;
                            Image image = new Image(fullPath);
                            imageView.setImage(image);
                            label.setText("CV");
                            setGraphic(vbox);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors du chargement de l'image CV: " + cvPath);
                        System.err.println("Message d'erreur: " + e.getMessage());
                        label.setText("Image non disponible");
                        imageView.setImage(null);
                        setGraphic(vbox);
                    }
                }
            }
        });

        // Set cell value factories for all columns
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));
        cinColumn.setCellValueFactory(new PropertyValueFactory<>("cin"));
        diplomeBacColumn.setCellValueFactory(new PropertyValueFactory<>("diplome_baccalauréat"));
        releveNoteColumn.setCellValueFactory(new PropertyValueFactory<>("releve_note"));
        diplomeObtenusColumn.setCellValueFactory(new PropertyValueFactory<>("diplome_obtenus"));
        lettreMotivationsColumn.setCellValueFactory(new PropertyValueFactory<>("lettre_motivations"));
        dossierSanteColumn.setCellValueFactory(new PropertyValueFactory<>("dossier_sante"));
        cvColumn.setCellValueFactory(new PropertyValueFactory<>("cv"));

        // Load data
        loadDossiers();
    }

    private void loadDossiers() {
        try {
            dossiers.clear();
            dossiers.addAll(serviceDossier.recuperer());
            dossierTable.setItems(dossiers);
            
            // Afficher les chemins des images pour le débogage
            for (Dossier dossier : dossiers) {
                System.out.println("Photo: " + dossier.getPhoto());
                System.out.println("CIN: " + dossier.getCin());
                System.out.println("Diplôme Bac: " + dossier.getDiplome_baccalauréat());
                System.out.println("Relevé Notes: " + dossier.getReleve_note());
                System.out.println("Diplômes Obtenus: " + dossier.getDiplome_obtenus());
                System.out.println("Lettre Motivation: " + dossier.getLettre_motivations());
                System.out.println("Dossier Santé: " + dossier.getDossier_sante());
                System.out.println("CV: " + dossier.getCv());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Afficher un message d'erreur à l'utilisateur
            System.err.println("Erreur lors du chargement des dossiers: " + e.getMessage());
        }
    }
}
