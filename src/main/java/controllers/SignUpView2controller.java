package controllers;

import Services.ServiceUser;
import entities.User;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class SignUpView2controller {
    @FXML
    private TextField tftelephone;
    @FXML
    private TextField tfmoyennes;
    @FXML
    private TextField tfcin;
    @FXML
    private DatePicker tfdateNaissance;
    @FXML
    private TextField tfnationalite;
    @FXML
    private Group back;
    @FXML
    private TextField tfdomaine_etude;
    @FXML
    private TextField tfuniversite_origine;
    @FXML
    private TextField tfannee_obtention_diplome;
    @FXML
    private ComboBox<Integer> tfage;

    private String nom, prenom, email, mdp;
    private ServiceUser serviceUser = new ServiceUser();

    public void setUserData(String nom, String prenom, String email, String mdp) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.mdp = mdp;
    }

    @FXML
    public void initialize() {
        //  âges de 18 à 100 ans
        for (int i = 18; i <= 100; i++) {
            tfage.getItems().add(i);
        }
    }

    @FXML
    public void back(Event event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/SignUpView1.fxml"));
            Stage stage = (Stage) back.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page précédente");
        }
    }

    @FXML
    public void Ajouter(ActionEvent actionEvent) {
        if (validateFields()) {
            try {
                // Création de l'utilisateur avec les données du formulaire
                User user = new User(
                    tfage.getValue(),
                    Integer.parseInt(tfcin.getText()),
                    Integer.parseInt(tftelephone.getText()),
                    Integer.parseInt(tfmoyennes.getText()),
                    Integer.parseInt(tfannee_obtention_diplome.getText()),
                    nom,
                    prenom,
                    tfnationalite.getText(),
                    email,
                    tfdomaine_etude.getText(),
                    tfuniversite_origine.getText(),
                    "Etudiant",
                    tfdateNaissance.getValue(),
                    mdp,
                    "default.jpg"
                );

                // Tentative d'ajout dans la base de données
                serviceUser.ajouter(user);
                
                // Affichage du message de succès
                showSuccessAlert("Succès", "Inscription réussie ! Votre compte a été créé avec succès.");
                
                // Redirection vers la page de connexion
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/Login-View.fxml"));
                    Stage stage = (Stage) tfcin.getScene().getWindow();
                    stage.setScene(new Scene(root));
                } catch (IOException e) {
                    showAlert("Erreur", "Inscription réussie mais impossible de charger la page de connexion : " + e.getMessage());
                }
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de l'inscription : " + e.getMessage());
                e.printStackTrace();
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Format de nombre invalide : " + e.getMessage());
            } catch (Exception e) {
                showAlert("Erreur", "Une erreur inattendue s'est produite : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private boolean validateFields() {
        try {
            // Vérification des champs vides
            if (tfcin.getText().isEmpty() || tftelephone.getText().isEmpty() || 
                tfage.getValue() == null || tfdateNaissance.getValue() == null ||
                tfnationalite.getText().isEmpty() || tfdomaine_etude.getText().isEmpty() ||
                tfuniversite_origine.getText().isEmpty() || tfannee_obtention_diplome.getText().isEmpty() ||
                tfmoyennes.getText().isEmpty()) {
                showAlert("Erreur", "Tous les champs sont obligatoires");
                return false;
            }

            // Vérification des champs numériques
            int cin = Integer.parseInt(tfcin.getText());
            int telephone = Integer.parseInt(tftelephone.getText());
            int moyennes = Integer.parseInt(tfmoyennes.getText());
            int annee = Integer.parseInt(tfannee_obtention_diplome.getText());

            // Vérification des valeurs numériques
            if (cin <= 0 || telephone <= 0 || moyennes < 0 || moyennes > 20 || annee <= 0) {
                showAlert("Erreur", "Veuillez vérifier les valeurs numériques saisies");
                return false;
            }

            // Vérification de la date de naissance
            if (tfdateNaissance.getValue().isAfter(LocalDate.now())) {
                showAlert("Erreur", "La date de naissance ne peut pas être dans le futur");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Les champs CIN, Téléphone, Moyennes et Année doivent être des nombres valides");
            return false;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccessAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
