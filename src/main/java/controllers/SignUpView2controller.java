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
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

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
    private ComboBox<String> tfdomaine_etude;
    @FXML
    private TextField tfuniversite_origine;
    @FXML
    private ComboBox<Integer> tfannee_obtention_diplome;
    @FXML
    private TextField tfage;
    @FXML
    private TextField newPassVisible;
    @FXML
    private TextField confNewPassVisible;
    @FXML
    private ImageView togglePasswordIcon;
    @FXML
    private ImageView togglePasswordIcon1;

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
        // Add listener for date of birth changes
        tfdateNaissance.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                calculateAndDisplayAge(newValue);
            }
        });

        // Initialize domaine_etude ComboBox
        ObservableList<String> domaines = FXCollections.observableArrayList(
            "Mathématiques",
            "Sciences expérimentales",
            "Économie et gestion",
            "Sciences techniques",
            "Lettres",
            "Sport",
            "Sciences de l'informatique"
        );
        tfdomaine_etude.setItems(domaines);

        // Initialize annee_obtention_diplome ComboBox
        List<Integer> annees = new ArrayList<>();
        for (int i = 1999; i <= 2025; i++) {
            annees.add(i);
        }
        tfannee_obtention_diplome.setItems(FXCollections.observableArrayList(annees));
    }

    private void calculateAndDisplayAge(LocalDate birthDate) {
        LocalDate referenceDate = LocalDate.of(2025, 1, 1);
        Period period = Period.between(birthDate, referenceDate);
        int age = period.getYears();
        tfage.setText(String.valueOf(age));
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
                // Convert comma to dot for database storage
                String moyenneStr = tfmoyennes.getText().replace(',', '.');
                double moyenne = Double.parseDouble(moyenneStr);

                // Création de l'utilisateur avec les données du formulaire
                User user = new User(
                    Integer.parseInt(tfage.getText()),
                    Integer.parseInt(tfcin.getText()),
                    Integer.parseInt(tftelephone.getText()),
                    moyenne,  // Changed to use double
                    tfannee_obtention_diplome.getValue(),
                    nom,
                    prenom,
                    tfnationalite.getText(),
                    email,
                    tfdomaine_etude.getValue(),
                    tfuniversite_origine.getText(),
                    "Etudiant",
                    tfdateNaissance.getValue(),
                    mdp,
                    "src/main/resources/images/profilee.jpg"
                );

                // Tentative d'ajout dans la base de données
                serviceUser.ajouter(user);
                
                // Affichage du message de succès
                showSuccessAlert("Succès", "Inscription réussie ! Votre compte a été créé avec succès.");
                
                // Redirection vers la page de connexion
                try {
                    Parent root = FXMLLoader.load(getClass().getResource("/login-view.fxml"));
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
                tfdateNaissance.getValue() == null ||
                tfnationalite.getText().isEmpty() || tfdomaine_etude.getValue() == null ||
                tfuniversite_origine.getText().isEmpty() || tfannee_obtention_diplome.getValue() == null ||
                tfmoyennes.getText().isEmpty()) {
                showAlert("Erreur", "Tous les champs sont obligatoires");
                return false;
            }

            // Vérification du format CIN (8 chiffres)
            String cin = tfcin.getText();
            if (!cin.matches("\\d{8}")) {
                showAlert("Erreur", "Le CIN doit contenir exactement 8 chiffres");
                return false;
            }

            // Vérification du format téléphone (8 chiffres)
            String telephone = tftelephone.getText();
            if (!telephone.matches("\\d{8}")) {
                showAlert("Erreur", "Le numéro de téléphone doit contenir exactement 8 chiffres");
                return false;
            }

            // Vérification de la date de naissance
            LocalDate birthDate = tfdateNaissance.getValue();
            LocalDate maxDate = LocalDate.of(2007, 1, 1);
            if (birthDate.isAfter(maxDate)) {
                showAlert("Erreur", "Vous devez être majeur (-18)");
                return false;
            }

            // Vérification de la moyenne avec virgule
            String moyenneStr = tfmoyennes.getText().replace(',', '.');
            try {
                double moyenne = Double.parseDouble(moyenneStr);
                if (moyenne < 9.0 || moyenne > 20.0) {
                    showAlert("Erreur", "La moyenne doit être comprise entre 9 et 20");
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Format de moyenne invalide. Utilisez une virgule comme séparateur décimal (ex: 10,25)");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Les champs Moyennes doivent être des nombres valides");
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
