package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import utils.UserSession;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Services.ServiceDossier;

public class Acceuilcontroller {

    private int userId;
    private ServiceDossier serviceDossier = new ServiceDossier();

    @FXML
    private Button scheduleEntretienButton;
    @FXML
    private Label registeredEventsLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Button checkMessagesButton;
    @FXML
    private Label activeDossiersLabel;
    @FXML
    private ListView appointmentsListView;
    @FXML
    private Label addressLabel;
    @FXML
    private Button viewEventsButton;
    @FXML
    private ListView notificationsListView;
    @FXML
    private Label emailLabel;
    @FXML
    private Button addDossierButton;
    @FXML
    private Label upcomingInterviewsLabel;

    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("Acceuilcontroller: Received user ID: " + this.userId);
    }

    @FXML
    public void onhebergementButtonClick(ActionEvent actionEvent) {
        try {
            System.out.println("AcceuilController: Opening ListFoyerClient view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Foyers");
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("AcceuilController: Error loading ListFoyerClient.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des foyers.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("AcceuilController: Unexpected error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue.");
            e.printStackTrace();
        }
    }



    @FXML
    public void onentretienButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemanderEntretien.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Display an alert if loading fails
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Loading View");
            alert.setContentText("Unable to load the entretien view: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void onuniversiteButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listcandidaturecards.fxml"));
            Parent root = loader.load();
            
            // Get the controller and set the user ID from UserSession
            CandidatureCardsController controller = loader.getController();
            controller.setUserId(UserSession.getInstance().getUserId());
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Mes Candidatures");
            stage.show();
        } catch (IOException e) {
            System.err.println("AcceuilController: Error loading listcandidaturecards.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des candidatures.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onvolsButtonClick(ActionEvent actionEvent) {
    }

    @FXML
    public void ondossierButtonClick(ActionEvent event) {
        if (this.userId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID utilisateur invalide. Impossible d'ouvrir la gestion des dossiers.");
            return;
        }

        try {
            System.out.println("AcceuilController: Opening AjoutDossier view for User ID: " + this.userId);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDossier.fxml"));
            Parent root = loader.load();

            Integer dossierId = serviceDossier.getDossierIdByUserId(this.userId);
            AjoutDossierController ajoutDossierController = loader.getController();
            ajoutDossierController.setEtudiantId(this.userId);
            if (dossierId != null) {
                ajoutDossierController.setDossierId(dossierId);
            }

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion du Dossier");
            stage.setMinWidth(1200); // Match the size from LoginViewcontroller or adjust
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

            // Optional: Close the accueil window if needed
            // Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            // currentStage.close();

        } catch (IOException e) {
            System.err.println("AcceuilController: Error loading AjoutDossier.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue du dossier.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("AcceuilController: Unexpected error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onlogoutButtonClick(ActionEvent actionEvent) {
        try {
            // Clear the user session
            UserSession.getInstance().clearSession();
            System.out.println("Acceuilcontroller: User session cleared on logout");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            Scene scene = new Scene(root);
            loginStage.setScene(scene);
            loginStage.setTitle("Login - GradAway");
            loginStage.setResizable(true);
            loginStage.centerOnScreen();
            loginStage.show();
            
            // Close current Accueil window
            Stage currentStage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            System.err.println("Acceuilcontroller: Error loading login view: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la déconnexion.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onProfileButtonClick(ActionEvent event) {
        if (this.userId <= 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "ID utilisateur invalide. Impossible d'ouvrir le profil.");
            return;
        }

        try {
            System.out.println("AcceuilController: Opening EditProfile view for User ID: " + this.userId);
            // Assuming the FXML file is named EditProfile.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();

            EditProfileController editProfileController = loader.getController();
            editProfileController.setUserId(this.userId); // Pass the user ID

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Mon Profil");
            // Adjust size as needed for EditProfile.fxml
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

            // Optional: Close the accueil window if needed
            // Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            // currentStage.close();

        } catch (IOException e) {
            System.err.println("AcceuilController: Error loading EditProfile.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue du profil.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("AcceuilController: Unexpected error opening profile: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onevenementButtonClick(ActionEvent actionEvent) {
        try {
            System.out.println("AcceuilController: Opening Affiche Evenement view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affiche_evenement.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Événements");
            stage.setMinWidth(1133); // Match the size from affiche_evenement.fxml
            stage.setMinHeight(691);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("AcceuilController: Error loading affiche_evenement.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des événements.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("AcceuilController: Unexpected error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onrestaurantButtonClick(ActionEvent actionEvent) {
        try {
            System.out.println("AcceuilController: Opening ListRestaurantClient view");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurantClient.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Restaurants");
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.err.println("AcceuilController: Error loading ListRestaurantClient.fxml: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la vue des restaurants.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("AcceuilController: Unexpected error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur Inattendue", "Une erreur inattendue est survenue.");
            e.printStackTrace();
        }
    }

    @FXML
    public void onAccueilButtonClick(ActionEvent actionEvent) {
    }

    // Helper method for showing alerts (can be reused)
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
