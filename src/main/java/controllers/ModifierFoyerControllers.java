package controllers;

import Services.ServiceFoyer;
import entities.Foyer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ModifierFoyerControllers {

    @FXML private TextField tf_id;
    @FXML private TextField tf_nom;
    @FXML private TextField tf_adresse;
    @FXML private TextField tf_ville;
    @FXML private TextField tf_pays;
    @FXML private TextField tf_nombre_de_chambre;
    @FXML private TextField tf_capacite;
    @FXML private Button btn_modifier;
    @FXML private TextField tf_image;


    private final ServiceFoyer serviceFoyer = new ServiceFoyer();

    @FXML
    public void search(ActionEvent event) {
        try {
            String idText = tf_id.getText().trim();

            if (idText.isEmpty()) {
                showAlert("Erreur", "Veuillez entrer un ID", Alert.AlertType.ERROR);
                return;
            }

            int idFoyer = Integer.parseInt(idText);
            Foyer foyer = serviceFoyer.getFoyerById(idFoyer);

            if (foyer != null) {
                tf_nom.setText(foyer.getNom());
                tf_adresse.setText(foyer.getAdresse());
                tf_ville.setText(foyer.getVille());
                tf_pays.setText(foyer.getPays());
                tf_nombre_de_chambre.setText(String.valueOf(foyer.getNombreDeChambre()));
                tf_capacite.setText(String.valueOf(foyer.getCapacite()));

                setFieldsEditable(true);
                btn_modifier.setDisable(false);
            } else {
                showAlert("Information", "Aucun foyer trouvé avec cet ID", Alert.AlertType.INFORMATION);
                clearFields();
                setFieldsEditable(false);
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID doit être un nombre valide", Alert.AlertType.ERROR);
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la recherche: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void enregistrerModifications(ActionEvent event) {
        try {
            if (!validateFields()) {
                return;
            }

            Foyer foyer = new Foyer(
                    Integer.parseInt(tf_id.getText()),
                    tf_nom.getText().trim(),
                    tf_adresse.getText().trim(),
                    tf_ville.getText().trim(),
                    tf_pays.getText().trim(),
                    Integer.parseInt(tf_nombre_de_chambre.getText()),
                    Integer.parseInt(tf_capacite.getText()),
                    tf_image.getText().trim()  // <<< AJOUT pour l'image
            );


            serviceFoyer.modifier(foyer);
            showAlert("Succès", "Foyer modifié avec succès", Alert.AlertType.INFORMATION);
            setFieldsEditable(false);
            btn_modifier.setDisable(true);

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private boolean validateFields() {
        if (tf_nom.getText().trim().isEmpty() ||
                tf_adresse.getText().trim().isEmpty() ||
                tf_ville.getText().trim().isEmpty() ||
                tf_pays.getText().trim().isEmpty() ||
                tf_nombre_de_chambre.getText().trim().isEmpty() ||
                tf_capacite.getText().trim().isEmpty()) {

            showAlert("Erreur", "Tous les champs doivent être remplis", Alert.AlertType.ERROR);
            return false;
        }

        try {
            int chambres = Integer.parseInt(tf_nombre_de_chambre.getText());
            int capacite = Integer.parseInt(tf_capacite.getText());

            if (chambres <= 0 || capacite <= 0) {
                showAlert("Erreur", "Les valeurs numériques doivent être positives", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Valeurs numériques invalides", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void setFieldsEditable(boolean editable) {
        tf_nom.setEditable(editable);
        tf_adresse.setEditable(editable);
        tf_ville.setEditable(editable);
        tf_pays.setEditable(editable);
        tf_nombre_de_chambre.setEditable(editable);
        tf_capacite.setEditable(editable);
    }

    private void clearFields() {
        tf_nom.clear();
        tf_adresse.clear();
        tf_ville.clear();
        tf_pays.clear();
        tf_nombre_de_chambre.clear();
        tf_capacite.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void initialize() {
        setFieldsEditable(false);
        btn_modifier.setDisable(true);

        // Validation automatique des champs numériques
        tf_nombre_de_chambre.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                tf_nombre_de_chambre.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        tf_capacite.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                tf_capacite.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }
}