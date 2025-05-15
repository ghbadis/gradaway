package controllers;

import entities.Dossier;
import entities.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public class AdminDossiercontroller implements Initializable {

    @FXML
    private VBox dossierContainer;

    private ServiceDossier serviceDossier;
    private ServiceUser serviceUser;
    private ObservableList<Dossier> dossiers;
    private Map<Integer, User> userCache;
    @FXML
    private TextField rechercherdossier;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceDossier = new ServiceDossier();
        serviceUser = new ServiceUser();
        dossiers = FXCollections.observableArrayList();
        userCache = new HashMap<>();
        loadDossiers();
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
            
            dossierContainer.getChildren().clear();
            for (Dossier dossier : dossiers) {
                HBox card = createDossierCard(dossier);
                dossierContainer.getChildren().add(card);
            }
            
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

    private HBox createDossierCard(Dossier dossier) {
        // Main card container
        HBox card = new HBox(20);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");
        card.setPadding(new Insets(15));
        card.setPrefWidth(680);

        // Get user information
        User user = getUserFromCache(dossier.getId_etudiant());
        String userName = user != null ? user.getNom() + " " + user.getPrenom() : "Utilisateur inconnu";

        // Middle section with dossier info
        VBox infoBox = new VBox(15);
        infoBox.setPadding(new Insets(10));

        // Nouvelle section Informations Personnelles : nom, prénom, email
        VBox personalInfo = createInfoSection("Informations Personnelles");
        GridPane personalGrid = new GridPane();
        personalGrid.setHgap(10);
        personalGrid.setVgap(5);

        String nom = user != null ? user.getNom() : "-";
        String prenom = user != null ? user.getPrenom() : "-";
        String email = user != null ? user.getEmail() : "-";

        addInfoRow(personalGrid, 0, "Nom :", nom);
        addInfoRow(personalGrid, 1, "Prénom :", prenom);
        addInfoRow(personalGrid, 2, "Email :", email);
        personalInfo.getChildren().clear(); // On retire le titre ajouté par createInfoSection
        Label titleLabel = new Label("Informations Personnelles");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");
        personalInfo.getChildren().addAll(titleLabel, personalGrid);

        // Documents Information with thumbnails
        VBox documentsInfo = createInfoSection("Documents");
        
        // Create HBox for first 4 images
        HBox thumbnailsRow = new HBox(15);
        thumbnailsRow.setPadding(new Insets(10));
        thumbnailsRow.setAlignment(javafx.geometry.Pos.CENTER);

        // Create list of all document thumbnails
        List<DocumentThumbnail> allThumbnails = new ArrayList<>();
        allThumbnails.add(new DocumentThumbnail("Photo", dossier.getPhoto()));
        allThumbnails.add(new DocumentThumbnail("CIN", dossier.getCin()));
        allThumbnails.add(new DocumentThumbnail("Diplôme Bac", dossier.getDiplome_baccalauréat()));
        allThumbnails.add(new DocumentThumbnail("Relevé Notes", dossier.getReleve_note()));
        allThumbnails.add(new DocumentThumbnail("Diplômes Obtenus", dossier.getDiplome_obtenus()));
        allThumbnails.add(new DocumentThumbnail("Lettre Motivation", dossier.getLettre_motivations()));
        allThumbnails.add(new DocumentThumbnail("Dossier Santé", dossier.getDossier_sante()));
        allThumbnails.add(new DocumentThumbnail("CV", dossier.getCv()));

        // Add first 4 thumbnails to row
        for (int i = 0; i < Math.min(4, allThumbnails.size()); i++) {
            DocumentThumbnail doc = allThumbnails.get(i);
            VBox thumbnailBox = createThumbnailBox(doc.title, doc.imagePath);
            thumbnailsRow.getChildren().add(thumbnailBox);
        }

        // Add "See More" button if there are more than 4 documents
        if (allThumbnails.size() > 4) {
            Button seeMoreButton = new Button("Voir plus de documents");
            seeMoreButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
            seeMoreButton.setOnAction(event -> showAllDocuments(allThumbnails));
            
            HBox buttonBox = new HBox(seeMoreButton);
            buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
            buttonBox.setPadding(new Insets(10, 0, 0, 0));
            
            documentsInfo.getChildren().addAll(thumbnailsRow, buttonBox);
        } else {
            documentsInfo.getChildren().add(thumbnailsRow);
        }

        infoBox.getChildren().clear();
        infoBox.getChildren().addAll(personalInfo, documentsInfo);

        // Right section with action buttons
        VBox actionBox = new VBox(10);
        actionBox.setAlignment(javafx.geometry.Pos.CENTER);
        actionBox.setPadding(new Insets(10));

        Button editButton = createActionButton("Modifier le dossier", "#4CAF50", "✏️");
        Button deleteButton = createActionButton("Supprimer le dossier", "#f44336", "🗑️");

        // Add event handlers for buttons
        editButton.setOnAction(event -> handleEditDossier(dossier));
        deleteButton.setOnAction(event -> handleDeleteDossierWithConfirmation(dossier));

        actionBox.getChildren().addAll(editButton, deleteButton);

        // Add only infoBox and actionBox to card (plus rien à gauche)
        card.getChildren().addAll(infoBox, actionBox);
        return card;
    }

    private VBox createInfoSection(String title) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-border-width: 1;");

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333333;");

        section.getChildren().add(titleLabel);
        return section;
    }

    private Button createActionButton(String text, String color, String icon) {
        Button button = new Button(icon + " " + text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;" +
                "-fx-cursor: hand;");
        button.setMinWidth(150);
        // Effet hover
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: derive(" + color + ", 20%); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5; -fx-cursor: hand;"));
        return button;
    }

    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label labelText = new Label(label);
        labelText.setStyle("-fx-font-weight: bold; -fx-text-fill: #666666;");

        Label valueText = new Label(value);
        valueText.setStyle("-fx-text-fill: #333333;");

        grid.add(labelText, 0, row);
        grid.add(valueText, 1, row);
    }

    private void handleViewDossier(Dossier dossier) {
        // Implement view dossier functionality
    }

    private void handleEditDossier(Dossier dossier) {
        try {
            // Créer une boîte de dialogue pour la modification
            Dialog<Dossier> dialog = new Dialog<>();
            dialog.setTitle("Modifier le dossier");
            dialog.setHeaderText("Modifier les informations du dossier");

            // Configuration des boutons
            ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Création du formulaire
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            // Champ de modification pour la photo avec bouton choisir
            TextField photoField = new TextField(dossier.getPhoto());
            Button photoChooseButton = new Button("Choisir");
            photoChooseButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir une image");
                fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
                );
                File selectedFile = fileChooser.showOpenDialog(null);
                if (selectedFile != null) {
                    photoField.setText(selectedFile.getAbsolutePath());
                }
            });
            HBox photoBox = new HBox(5, photoField, photoChooseButton);

            // Champ de modification pour CIN avec bouton choisir
            TextField cinField = new TextField(dossier.getCin());
            Button cinChooseButton = new Button("Choisir");
            cinChooseButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir un fichier CIN");
                File selectedFile = fileChooser.showOpenDialog(null);
                if (selectedFile != null) {
                    cinField.setText(selectedFile.getAbsolutePath());
                }
            });
            HBox cinBox = new HBox(5, cinField, cinChooseButton);

            // Champ de modification pour Diplôme Bac avec bouton choisir
            TextField diplomeBacField = new TextField(dossier.getDiplome_baccalauréat());
            Button diplomeBacChooseButton = new Button("Choisir");
            diplomeBacChooseButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir un fichier Diplôme Bac");
                File selectedFile = fileChooser.showOpenDialog(null);
                if (selectedFile != null) {
                    diplomeBacField.setText(selectedFile.getAbsolutePath());
                }
            });
            HBox diplomeBacBox = new HBox(5, diplomeBacField, diplomeBacChooseButton);

            // Champ de modification pour Relevé Notes avec bouton choisir
            TextField releveNoteField = new TextField(dossier.getReleve_note());
            Button releveNoteChooseButton = new Button("Choisir");
            releveNoteChooseButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir un fichier Relevé Notes");
                File selectedFile = fileChooser.showOpenDialog(null);
                if (selectedFile != null) {
                    releveNoteField.setText(selectedFile.getAbsolutePath());
                }
            });
            HBox releveNoteBox = new HBox(5, releveNoteField, releveNoteChooseButton);

            // Champ de modification pour Diplômes Obtenus avec bouton choisir
            TextField diplomeObtenusField = new TextField(dossier.getDiplome_obtenus());
            Button diplomeObtenusChooseButton = new Button("Choisir");
            diplomeObtenusChooseButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir un fichier Diplômes Obtenus");
                File selectedFile = fileChooser.showOpenDialog(null);
                if (selectedFile != null) {
                    diplomeObtenusField.setText(selectedFile.getAbsolutePath());
                }
            });
            HBox diplomeObtenusBox = new HBox(5, diplomeObtenusField, diplomeObtenusChooseButton);

            // Champ de modification pour Lettre Motivation avec bouton choisir
            TextField lettreMotivationsField = new TextField(dossier.getLettre_motivations());
            Button lettreMotivationsChooseButton = new Button("Choisir");
            lettreMotivationsChooseButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir un fichier Lettre Motivation");
                File selectedFile = fileChooser.showOpenDialog(null);
                if (selectedFile != null) {
                    lettreMotivationsField.setText(selectedFile.getAbsolutePath());
                }
            });
            HBox lettreMotivationsBox = new HBox(5, lettreMotivationsField, lettreMotivationsChooseButton);

            // Champ de modification pour Dossier Santé avec bouton choisir
            TextField dossierSanteField = new TextField(dossier.getDossier_sante());
            Button dossierSanteChooseButton = new Button("Choisir");
            dossierSanteChooseButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir un fichier Dossier Santé");
                File selectedFile = fileChooser.showOpenDialog(null);
                if (selectedFile != null) {
                    dossierSanteField.setText(selectedFile.getAbsolutePath());
                }
            });
            HBox dossierSanteBox = new HBox(5, dossierSanteField, dossierSanteChooseButton);

            // Champ de modification pour CV avec bouton choisir
            TextField cvField = new TextField(dossier.getCv());
            Button cvChooseButton = new Button("Choisir");
            cvChooseButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Choisir un fichier CV");
                File selectedFile = fileChooser.showOpenDialog(null);
                if (selectedFile != null) {
                    cvField.setText(selectedFile.getAbsolutePath());
                }
            });
            HBox cvBox = new HBox(5, cvField, cvChooseButton);

            // Ajout des champs au formulaire
            grid.add(new Label("Photo:"), 0, 0);
            grid.add(photoBox, 1, 0);
            grid.add(new Label("CIN:"), 0, 1);
            grid.add(cinBox, 1, 1);
            grid.add(new Label("Diplôme Bac:"), 0, 2);
            grid.add(diplomeBacBox, 1, 2);
            grid.add(new Label("Relevé Notes:"), 0, 3);
            grid.add(releveNoteBox, 1, 3);
            grid.add(new Label("Diplômes Obtenus:"), 0, 4);
            grid.add(diplomeObtenusBox, 1, 4);
            grid.add(new Label("Lettre Motivation:"), 0, 5);
            grid.add(lettreMotivationsBox, 1, 5);
            grid.add(new Label("Dossier Santé:"), 0, 6);
            grid.add(dossierSanteBox, 1, 6);
            grid.add(new Label("CV:"), 0, 7);
            grid.add(cvBox, 1, 7);

            dialog.getDialogPane().setContent(grid);

            // Conversion du résultat
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

            // Affichage de la boîte de dialogue et traitement du résultat
            Optional<Dossier> result = dialog.showAndWait();
            result.ifPresent(updatedDossier -> {
                try {
                    serviceDossier.modifier(updatedDossier);
                    loadDossiers(); // Recharger la liste des dossiers
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Le dossier a été modifié avec succès.");
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification du dossier: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    private void handleDeleteDossier(Dossier dossier) {
        try {
            serviceDossier.supprimer(dossier);
            loadDossiers(); // Recharger la liste des dossiers
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le dossier a été supprimé avec succès.");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du dossier: " + e.getMessage());
        }
    }

    private void handleDeleteDossierWithConfirmation(Dossier dossier) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer ce dossier ?");
        alert.setContentText("Cette action est irréversible.");

        ButtonType buttonOui = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType buttonNon = new ButtonType("Non", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(buttonOui, buttonNon);

        alert.showAndWait().ifPresent(type -> {
            if (type == buttonOui) {
                handleDeleteDossier(dossier);
            }
        });
    }

    private void loadDefaultImage(ImageView imageView) {
        try {
            URL defaultImageUrl = getClass().getResource("/images/default-user.png");
            if (defaultImageUrl != null) {
                imageView.setImage(new Image(defaultImageUrl.toString()));
            } else {
                // If default image is not found, create a colored rectangle
                imageView.setImage(null);
                imageView.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 60; " +
                        "-fx-border-color: #cccccc; -fx-border-radius: 60; -fx-border-width: 2;");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image par défaut: " + e.getMessage());
            // If there's an error, create a colored rectangle
            imageView.setImage(null);
            imageView.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 60; " +
                    "-fx-border-color: #cccccc; -fx-border-radius: 60; -fx-border-width: 2;");
        }
    }

    @FXML
    public void rechercherdossier(ActionEvent actionEvent) {
        String email = rechercherdossier.getText().trim();
        dossierContainer.getChildren().clear();

        if (email.isEmpty()) {
            // Si le champ est vide, recharger tous les dossiers
            loadDossiers();
            return;
        }

        try {
            // Trouver l'utilisateur par email
            User user = serviceUser.getUserByEmail(email);
            if (user == null) {
                showAlert(Alert.AlertType.WARNING, "Aucun résultat", "Aucun utilisateur trouvé avec cet email.");
                return;
            }

            // Récupérer le(s) dossier(s) de cet utilisateur
            List<Dossier> dossiersTrouves = new ArrayList<>();
            for (Dossier dossier : serviceDossier.recuperer()) {
                if (dossier.getId_etudiant() == user.getId()) {
                    dossiersTrouves.add(dossier);
                }
            }

            if (dossiersTrouves.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Aucun résultat", "Aucun dossier trouvé pour cet utilisateur.");
                return;
            }

            // Afficher les dossiers trouvés
            for (Dossier dossier : dossiersTrouves) {
                HBox card = createDossierCard(dossier);
                dossierContainer.getChildren().add(card);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static class DocumentThumbnail {
        String title;
        String imagePath;

        DocumentThumbnail(String title, String imagePath) {
            this.title = title;
            this.imagePath = imagePath;
        }
    }

    private VBox createThumbnailBox(String title, String imagePath) {
        VBox thumbnailBox = new VBox(5);
        thumbnailBox.setAlignment(javafx.geometry.Pos.CENTER);
        thumbnailBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-border-width: 1;");
        thumbnailBox.setPadding(new Insets(5));
        thumbnailBox.setPrefWidth(150);  // Reduced width to fit 4 images in one row
        thumbnailBox.setPrefHeight(180); // Adjusted height for better proportions

        ImageView thumbnailView = new ImageView();
        thumbnailView.setFitHeight(120);  // Adjusted image size
        thumbnailView.setFitWidth(120);   // Adjusted image size
        thumbnailView.setPreserveRatio(true);
        thumbnailView.setStyle("-fx-background-color: white; -fx-background-radius: 5; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-border-width: 1;");

        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                if (imagePath.startsWith("file:")) {
                    thumbnailView.setImage(new Image(imagePath));
                } else {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        thumbnailView.setImage(new Image(imageFile.toURI().toString()));
                    } else {
                        loadDefaultImage(thumbnailView);
                    }
                }
            } else {
                loadDefaultImage(thumbnailView);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la miniature " + title + ": " + e.getMessage());
            loadDefaultImage(thumbnailView);
        }

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(javafx.geometry.Pos.CENTER);

        thumbnailBox.getChildren().addAll(thumbnailView, titleLabel);

        // Add click handler to view full image
        thumbnailBox.setOnMouseClicked(event -> {
            if (imagePath != null && !imagePath.isEmpty()) {
                showFullImage(title, imagePath);
            }
        });

        return thumbnailBox;
    }

    private void showAllDocuments(List<DocumentThumbnail> documents) {
        Stage stage = new Stage();
        VBox root = new VBox(20);
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setStyle("-fx-background-color: white; -fx-padding: 20;");

        Label titleLabel = new Label("Tous les documents");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        FlowPane documentsPane = new FlowPane();
        documentsPane.setHgap(20);
        documentsPane.setVgap(20);
        documentsPane.setPadding(new Insets(20));
        documentsPane.setPrefWrapLength(800);

        for (DocumentThumbnail doc : documents) {
            VBox thumbnailBox = createThumbnailBox(doc.title, doc.imagePath);
            documentsPane.getChildren().add(thumbnailBox);
        }

        Button closeButton = new Button("Fermer");
        closeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 10 20; -fx-background-radius: 5;");
        closeButton.setOnAction(e -> stage.close());

        root.getChildren().addAll(titleLabel, documentsPane, closeButton);

        Scene scene = new Scene(root);
        stage.setTitle("Tous les documents");
        stage.setScene(scene);
        stage.show();
    }

    private void showFullImage(String title, String imagePath) {
        try {
            Stage stage = new Stage();
            VBox root = new VBox(10);
            root.setAlignment(javafx.geometry.Pos.CENTER);
            root.setStyle("-fx-background-color: white; -fx-padding: 20;");

            ImageView fullImageView = new ImageView();
            if (imagePath.startsWith("file:")) {
                fullImageView.setImage(new Image(imagePath));
            } else {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    fullImageView.setImage(new Image(imageFile.toURI().toString()));
                }
            }

            fullImageView.setFitHeight(800);
            fullImageView.setFitWidth(1000);
            fullImageView.setPreserveRatio(true);

            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333333;");

            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-padding: 10 20; -fx-background-radius: 5;");
            closeButton.setOnAction(e -> stage.close());

            root.getChildren().addAll(titleLabel, fullImageView, closeButton);

            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage de l'image complète: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void onAccueilAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AcceuilAdmin.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil Admin");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onUserAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminUser.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Utilisateurs");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void ondossierAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDossier.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Dossiers");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onuniversiteAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminuniversite.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Universités");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onentretienAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gestionnaire.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Entretiens");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onevenementAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_evenement.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Événements");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onhebergementAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFoyer.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Foyers");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onrestaurantAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterRestaurant.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Restaurants");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    public void onvolsAdminButtonClick(ActionEvent actionEvent) {
        showAlert(Alert.AlertType.INFORMATION, "Information", "La fonctionnalité des vols sera bientôt disponible.");
    }

    @FXML
    public void onlogoutAdminButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - GradAway");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion: " + e.getMessage());
        }
    }
}
