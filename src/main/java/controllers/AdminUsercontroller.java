package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import Services.ServiceUser;
import entities.User;
import java.util.List;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import Services.ServiceDossier;
import entities.Dossier;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.util.Pair;
import javafx.geometry.Insets;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminUsercontroller implements Initializable {
    @FXML
    private TableView<User> TvUser;

    @FXML
    private TableColumn<User, Integer> colId;
    @FXML
    private TableColumn<User, String> colNom;
    @FXML
    private TableColumn<User, String> colPrenom;
    @FXML
    private TableColumn<User, String> colEmail;
    @FXML
    private TableColumn<User, Integer> colTelephone;
    @FXML
    private TableColumn<User, Integer> colCin;
    @FXML
    private TableColumn<User, Integer> colAge;
    @FXML
    private TableColumn<User, java.time.LocalDate> colDateNaissance;
    @FXML
    private TableColumn<User, String> colNationalite;
    @FXML
    private TableColumn<User, String> colDomaineEtude;
    @FXML
    private TableColumn<User, String> colUniversiteOrigine;
    @FXML
    private TableColumn<User, String> colRole;
    @FXML
    private TableColumn<User, Integer> colMoyennes;
    @FXML
    private TableColumn<User, Integer> colAnneeObtentionDiplome;
    @FXML
    private TableColumn<User, String> colImage;

    @FXML
    private Button deleteButton;

    @FXML
    private Button modifyButton;

    private ServiceUser serviceUser = new ServiceUser();
    private ServiceDossier serviceDossier = new ServiceDossier();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set up the table columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colTelephone.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colCin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        colAge.setCellValueFactory(new PropertyValueFactory<>("age"));
        colDateNaissance.setCellValueFactory(new PropertyValueFactory<>("date_naissance"));
        colNationalite.setCellValueFactory(new PropertyValueFactory<>("nationalite"));
        colDomaineEtude.setCellValueFactory(new PropertyValueFactory<>("domaine_etude"));
        colUniversiteOrigine.setCellValueFactory(new PropertyValueFactory<>("universite_origine"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colMoyennes.setCellValueFactory(new PropertyValueFactory<>("moyennes"));
        colAnneeObtentionDiplome.setCellValueFactory(new PropertyValueFactory<>("annee_obtention_diplome"));
        colImage.setCellValueFactory(new PropertyValueFactory<>("image"));

        // Load and display users
        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> users = serviceUser.recuperer();
            TvUser.getItems().setAll(users);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du chargement des utilisateurs.");
            alert.showAndWait();
        }
    }

    @FXML
    public void onDeleteButtonClick(ActionEvent actionEvent) {
        User selectedUser = TvUser.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un utilisateur à supprimer.");
            alert.showAndWait();
            return;
        }
        try {
            // First, delete related dossiers
            Dossier dossier = serviceDossier.recupererParEtudiantId(selectedUser.getId());
            if (dossier != null) {
                serviceDossier.supprimer(dossier);
            }
            // Then delete the user
            serviceUser.supprimer(selectedUser);
            TvUser.getItems().remove(selectedUser);
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText(null);
            alert.setContentText("L'utilisateur a été supprimé avec succès.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de la suppression de l'utilisateur.");
            alert.showAndWait();
        }
    }

    @FXML
    public void onModifyButtonClick(ActionEvent actionEvent) {
        User selectedUser = TvUser.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un utilisateur à modifier.");
            alert.showAndWait();
            return;
        }

        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Modifier l'utilisateur");
        dialog.setHeaderText("Modifier les informations de l'utilisateur");

        ButtonType saveButtonType = new ButtonType("Sauvegarder", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomField = new TextField(selectedUser.getNom());
        TextField prenomField = new TextField(selectedUser.getPrenom());
        TextField emailField = new TextField(selectedUser.getEmail());
        TextField telephoneField = new TextField(String.valueOf(selectedUser.getTelephone()));
        TextField cinField = new TextField(String.valueOf(selectedUser.getCin()));
        TextField ageField = new TextField(String.valueOf(selectedUser.getAge()));
        TextField nationaliteField = new TextField(selectedUser.getNationalite());
        TextField domaineEtudeField = new TextField(selectedUser.getDomaine_etude());
        TextField universiteOrigineField = new TextField(selectedUser.getUniversite_origine());
        TextField roleField = new TextField(selectedUser.getRole());
        TextField moyennesField = new TextField(String.valueOf(selectedUser.getMoyennes()));
        TextField anneeObtentionDiplomeField = new TextField(String.valueOf(selectedUser.getAnnee_obtention_diplome()));
        TextField imageField = new TextField(selectedUser.getImage());

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prenom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(new Label("Telephone:"), 0, 3);
        grid.add(telephoneField, 1, 3);
        grid.add(new Label("CIN:"), 0, 4);
        grid.add(cinField, 1, 4);
        grid.add(new Label("Age:"), 0, 5);
        grid.add(ageField, 1, 5);
        grid.add(new Label("Nationalite:"), 0, 6);
        grid.add(nationaliteField, 1, 6);
        grid.add(new Label("Domaine Etude:"), 0, 7);
        grid.add(domaineEtudeField, 1, 7);
        grid.add(new Label("Universite Origine:"), 0, 8);
        grid.add(universiteOrigineField, 1, 8);
        grid.add(new Label("Role:"), 0, 9);
        grid.add(roleField, 1, 9);
        grid.add(new Label("Moyennes:"), 0, 10);
        grid.add(moyennesField, 1, 10);
        grid.add(new Label("Annee Obtention Diplome:"), 0, 11);
        grid.add(anneeObtentionDiplomeField, 1, 11);
        grid.add(new Label("Image:"), 0, 12);
        grid.add(imageField, 1, 12);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Pair<>(nomField.getText(), prenomField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            try {
                selectedUser.setNom(nomField.getText());
                selectedUser.setPrenom(prenomField.getText());
                selectedUser.setEmail(emailField.getText());
                selectedUser.setTelephone(Integer.parseInt(telephoneField.getText()));
                selectedUser.setCin(Integer.parseInt(cinField.getText()));
                selectedUser.setAge(Integer.parseInt(ageField.getText()));
                selectedUser.setNationalite(nationaliteField.getText());
                selectedUser.setDomaine_etude(domaineEtudeField.getText());
                selectedUser.setUniversite_origine(universiteOrigineField.getText());
                selectedUser.setRole(roleField.getText());
                selectedUser.setMoyennes(Integer.parseInt(moyennesField.getText()));
                selectedUser.setAnnee_obtention_diplome(Integer.parseInt(anneeObtentionDiplomeField.getText()));
                selectedUser.setImage(imageField.getText());

                serviceUser.modifier(selectedUser);
                TvUser.refresh();
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("L'utilisateur a été modifié avec succès.");
                alert.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Erreur lors de la modification de l'utilisateur.");
                alert.showAndWait();
            }
        });
    }
}
