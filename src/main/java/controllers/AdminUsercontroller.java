package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import entities.User;
import Services.ServiceUser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;

public class AdminUsercontroller implements Initializable {

    @FXML
    private ListView<HBox> userListView;

    private ServiceUser serviceUser;
    @FXML
    private TextField rechercheruser;

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

        // Add event handlers for buttons
        editButton.setOnAction(event -> handleEditUser(user));
        deleteButton.setOnAction(event -> handleDeleteUser(user));

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

    private void handleEditUser(User user) {
        try {
            // Load the EditProfile FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();

            // Get the controller and set the user ID
            EditProfileController editController = loader.getController();
            editController.setUserId(user.getId());

            // Create and show the stage
            Stage stage = new Stage();
            stage.setTitle("Modifier le profil - " + user.getNom() + " " + user.getPrenom());
            stage.setScene(new Scene(root));
            stage.show();

            // Add listener for when the window is closed to refresh the user list
            stage.setOnHidden(event -> loadUsers());
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleDeleteUser(User user) {
        try {
            Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmation de suppression");
            confirmDialog.setHeaderText("Supprimer l'utilisateur");
            confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer l'utilisateur " + user.getNom() + " " + user.getPrenom() + " ?");

            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    try {
                        // Create a new User object with just the ID
                        User userToDelete = new User(
                            user.getId(),  // id
                            user.getAge(),
                            user.getCin(),
                            user.getTelephone(),
                            user.getMoyennes(),
                            user.getAnnee_obtention_diplome(),
                            user.getNom(),
                            user.getPrenom(),
                            user.getNationalite(),
                            user.getEmail(),
                            user.getDomaine_etude(),
                            user.getUniversite_origine(),
                            user.getRole(),
                            user.getDateNaissance(),
                            user.getMdp(),
                            user.getImage()
                        );
                        serviceUser.supprimer(userToDelete);
                        loadUsers(); // Refresh the list
                        showSuccess("Suppression réussie", "L'utilisateur a été supprimé avec succès.");
                    } catch (Exception e) {
                        showError("Erreur lors de la suppression", e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            showError("Erreur", e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    public void rechercheruser(ActionEvent actionEvent) {
        String searchText = rechercheruser.getText().trim();
        
        if (searchText.isEmpty()) {
            // If search field is empty, show all users
            loadUsers();
            return;
        }

        try {
            List<User> allUsers = serviceUser.recuperer();
            userListView.getItems().clear();

            // Filter users by email or CIN
            for (User user : allUsers) {
                String userEmail = user.getEmail().toLowerCase();
                String userCin = String.valueOf(user.getCin()).toLowerCase();
                String searchLower = searchText.toLowerCase();

                if (userEmail.contains(searchLower) || userCin.contains(searchLower)) {
                    HBox userBox = createUserBox(user);
                    userListView.getItems().add(userBox);
                }
            }

            // Show message if no users found
            if (userListView.getItems().isEmpty()) {
                showError("Recherche", "Aucun utilisateur trouvé avec cet email ou CIN.");
            }
        } catch (Exception e) {
            showError("Erreur de recherche", e.getMessage());
        }
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
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
            showAlert("Erreur", "Erreur lors de l'ouverture", e.getMessage());
        }
    }

    @FXML
    public void onvolsAdminButtonClick(ActionEvent actionEvent) {
        showAlert("Information", "Information", "La fonctionnalité des vols sera bientôt disponible.");
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
            showAlert("Erreur", "Erreur lors de la déconnexion", e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}