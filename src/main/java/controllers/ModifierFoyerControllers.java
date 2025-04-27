package controllers;

import Services.ServiceFoyer;
import entities.Foyer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ModifierFoyerControllers {

    // Déclaration des champs de texte de l'interface
    @FXML
    private TextField tf_id;

    @FXML
    private TextField tf_nom;

    @FXML
    private TextField tf_adresse;

    @FXML
    private TextField tf_ville;

    @FXML
    private TextField tf_pays;

    @FXML
    private TextField tf_nombre_de_chambre;

    @FXML
    private TextField tf_capacite;

    // Instance du service pour effectuer des opérations CRUD
    private ServiceFoyer serviceFoyer = new ServiceFoyer();

    // Méthode pour récupérer les informations du foyer via l'ID et les remplir dans le formulaire
    // Méthode pour récupérer les informations du foyer via l'ID et les remplir dans le formulaire
    @FXML
    public void modifierfoyer(ActionEvent event) {
        try {
            // Lire l'ID du foyer à partir du champ de texte
            int idFoyer = Integer.parseInt(tf_id.getText());

            // Appeler la méthode du service pour obtenir le foyer par son ID
            Foyer foyer = serviceFoyer.getFoyerById(idFoyer);  // Utilisation de la méthode getFoyerById

            // Vérifier si le foyer a été trouvé
            if (foyer != null) {
                // Si foyer trouvé, remplir les champs avec les informations du foyer
                tf_nom.setText(foyer.getNom());
                tf_adresse.setText(foyer.getAdresse());
                tf_ville.setText(foyer.getVille());
                tf_pays.setText(foyer.getPays());
                tf_nombre_de_chambre.setText(String.valueOf(foyer.getNombreDeChambre()));
                tf_capacite.setText(String.valueOf(foyer.getCapacite()));
            } else {
                // Si aucun foyer n'est trouvé avec l'ID, afficher un message d'erreur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Foyer non trouvé !");
                alert.showAndWait();
            }

        } catch (NumberFormatException e) {
            // Si l'ID n'est pas un nombre valide, afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer un ID valide.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur inconnue");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue.");
            alert.showAndWait();
        }
    }


    // Méthode pour modifier un foyer dans la base de données
    @FXML
    void enregistrerModifications(ActionEvent event) {
        try {
            // Lire les valeurs des champs de texte
            int idFoyer = Integer.parseInt(tf_id.getText());
            String nom = tf_nom.getText();
            String adresse = tf_adresse.getText();
            String ville = tf_ville.getText();
            String pays = tf_pays.getText();
            int nombreDeChambre = Integer.parseInt(tf_nombre_de_chambre.getText());
            int capacite = Integer.parseInt(tf_capacite.getText());

            // Créer un objet Foyer avec les nouvelles valeurs
            Foyer foyer = new Foyer(idFoyer, nom, adresse, ville, pays, nombreDeChambre, capacite);

            // Appeler la méthode du service pour enregistrer les modifications
            ServiceFoyer serviceFoyer = new ServiceFoyer();

            // Afficher une alerte selon le résultat de l'opération
            boolean success = false;
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Foyer modifié avec succès !");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("La modification a échoué. Veuillez vérifier les informations.");
                alert.showAndWait();
            }

        } catch (NumberFormatException e) {
            // Gérer les erreurs de format des champs numériques
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer des nombres valides pour nombre de chambres et capacité.");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur inconnue");
            alert.setHeaderText(null);
            alert.setContentText("Une erreur est survenue.");
            alert.showAndWait();
        }
    }

    // Méthode d'initialisation, à appeler après l'injection des éléments FXML
    @FXML
    void initialize() {
        assert tf_id != null : "fx:id=\"tf_id\" was not injected: check your FXML file 'ModifierFoyer.fxml'.";
        assert tf_nom != null : "fx:id=\"tf_nom\" was not injected: check your FXML file 'ModifierFoyer.fxml'.";
        assert tf_adresse != null : "fx:id=\"tf_adresse\" was not injected: check your FXML file 'ModifierFoyer.fxml'.";
        assert tf_ville != null : "fx:id=\"tf_ville\" was not injected: check your FXML file 'ModifierFoyer.fxml'.";
        assert tf_pays != null : "fx:id=\"tf_pays\" was not injected: check your FXML file 'ModifierFoyer.fxml'.";
        assert tf_nombre_de_chambre != null : "fx:id=\"tf_nombre_de_chambre\" was not injected: check your FXML file 'ModifierFoyer.fxml'.";
        assert tf_capacite != null : "fx:id=\"tf_capacite\" was not injected: check your FXML file 'ModifierFoyer.fxml'.";
    }
}

