package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import entities.User;
import Services.ServiceUser;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AdminUsercontroller implements Initializable {

    @FXML
    private ListView<HBox> userListView;

    private ServiceUser serviceUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceUser = new ServiceUser();
        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> users = serviceUser.recuperer();
            userListView.getItems().clear();

            for (User user : users) {
                HBox userBox = createUserBox(user);
                userListView.getItems().add(userBox);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HBox createUserBox(User user) {
        // Main container
        HBox mainBox = new HBox(20);
        mainBox.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 10; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");
        mainBox.setPadding(new Insets(15));

        // Left section with photo
        VBox photoBox = new VBox(10);
        photoBox.setAlignment(javafx.geometry.Pos.CENTER);

        ImageView photoView = new ImageView();
        try {
            if (user.getImage() != null && !user.getImage().isEmpty()) {
                File imageFile = new File(user.getImage());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    photoView.setImage(image);
                } else {
                    loadDefaultImage(photoView);
                }
            } else {
                loadDefaultImage(photoView);
            }
        } catch (Exception e) {
            loadDefaultImage(photoView);
        }

        photoView.setFitHeight(120);
        photoView.setFitWidth(120);
        photoView.setPreserveRatio(true);
        photoView.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 60; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 60; -fx-border-width: 2;");

        // Add user name under photo
        Text nameText = new Text(user.getNom() + " " + user.getPrenom());
        nameText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        photoBox.getChildren().addAll(photoView, nameText);

        // Middle section with user info
        GridPane infoGrid = new GridPane();
        infoGrid.setHgap(15);
        infoGrid.setVgap(8);
        infoGrid.setPadding(new Insets(10));

        // Personal Information Section
        VBox personalInfoBox = createInfoSection("Informations Personnelles");
        GridPane personalGrid = new GridPane();
        personalGrid.setHgap(10);
        personalGrid.setVgap(5);

        addInfoRow(personalGrid, 0, "Email:", user.getEmail());
        addInfoRow(personalGrid, 1, "Téléphone:", String.valueOf(user.getTelephone()));
        addInfoRow(personalGrid, 2, "CIN:", String.valueOf(user.getCin()));
        addInfoRow(personalGrid, 3, "Âge:", String.valueOf(user.getAge()));
        addInfoRow(personalGrid, 4, "Nationalité:", user.getNationalite());
        addInfoRow(personalGrid, 5, "Date de naissance:", user.getDateNaissance().toString());

        personalInfoBox.getChildren().add(personalGrid);

        // Academic Information Section
        VBox academicInfoBox = createInfoSection("Informations Académiques");
        GridPane academicGrid = new GridPane();
        academicGrid.setHgap(10);
        academicGrid.setVgap(5);

        addInfoRow(academicGrid, 0, "Domaine d'étude:", user.getDomaine_etude());
        addInfoRow(academicGrid, 1, "Université d'origine:", user.getUniversite_origine());
        addInfoRow(academicGrid, 2, "Moyenne:", String.valueOf(user.getMoyennes()));
        addInfoRow(academicGrid, 3, "Année d'obtention:", String.valueOf(user.getAnnee_obtention_diplome()));
        addInfoRow(academicGrid, 4, "Rôle:", user.getRole());

        academicInfoBox.getChildren().add(academicGrid);

        // Right section with action buttons
        VBox actionBox = new VBox(10);
        actionBox.setAlignment(javafx.geometry.Pos.CENTER);
        actionBox.setPadding(new Insets(10));

        Button editButton = createActionButton("Modifier", "#4CAF50");
        Button deleteButton = createActionButton("Supprimer", "#f44336");

        actionBox.getChildren().addAll(editButton, deleteButton);

        // Add all sections to main box
        mainBox.getChildren().addAll(photoBox, personalInfoBox, academicInfoBox, actionBox);
        return mainBox;
    }

    private VBox createInfoSection(String title) {
        VBox section = new VBox(10);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-border-width: 1;");

        Text titleText = new Text(title);
        titleText.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-fill: #333333;");

        section.getChildren().add(titleText);
        return section;
    }

    private Button createActionButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 8 15; -fx-background-radius: 5;");
        button.setMinWidth(100);
        return button;
    }

    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Text labelText = new Text(label);
        labelText.setStyle("-fx-font-weight: bold; -fx-fill: #666666;");

        Text valueText = new Text(value);
        valueText.setStyle("-fx-fill: #333333;");

        grid.add(labelText, 0, row);
        grid.add(valueText, 1, row);
    }

    private void loadDefaultImage(ImageView imageView) {
        try {
            URL defaultImageUrl = getClass().getResource("/images/default-user.png");
            if (defaultImageUrl != null) {
                imageView.setImage(new Image(defaultImageUrl.toString()));
            } else {
                System.err.println("Image par défaut non trouvée");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image par défaut: " + e.getMessage());
        }
    }
}