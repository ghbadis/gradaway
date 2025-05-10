package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import entities.Expert;
import Services.ServiceExpert;
import java.util.regex.Pattern;
import java.sql.SQLException;

public class ModifierExpertController {
    @FXML
    private TextField idField;
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField telephoneField;
    @FXML
    private ComboBox<String> specialiteComboBox;
    @FXML
    private TextField experienceField;
    @FXML
    private Button modifierButton;
    @FXML
    private Button annulerButton;

    private final ServiceExpert serviceExpert = new ServiceExpert();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");
    private Expert expertAModifier;

    @FXML
    public void initialize() {
        // Initialiser les spécialités
        specialiteComboBox.getItems().addAll(
            "Java",
            "Python",
            "JavaScript",
            "DevOps",
            "Base de données",
            "Web",
            "Mobile",
            "Cloud",
            "Sécurité"
        );

        // Configurer les boutons
        modifierButton.setOnAction(event -> modifierExpert());
        annulerButton.setOnAction(event -> fermerFenetre());
    }

    public void setExpert(Expert expert) {
        this.expertAModifier = expert;
        idField.setText(String.valueOf(expert.getId_expert()));
        nomField.setText(expert.getNom_expert());
        prenomField.setText(expert.getPrenom_expert());
        emailField.setText(expert.getEmail());
        telephoneField.setText(expert.getTelephone());
        specialiteComboBox.setValue(expert.getSpecialite());
        experienceField.setText(String.valueOf(expert.getAnneeExperience()));
    }

    private void modifierExpert() {
        if (!validateFields()) {
            return;
        }

        try {
            // Créer un nouvel expert avec le nouvel ID
            Expert expertModifie = new Expert();
            expertModifie.setId_expert(Integer.parseInt(idField.getText().trim()));
            expertModifie.setNom_expert(nomField.getText().trim());
            expertModifie.setPrenom_expert(prenomField.getText().trim());
            expertModifie.setEmail(emailField.getText().trim());
            expertModifie.setSpecialite(specialiteComboBox.getValue());
            
            String telephone = telephoneField.getText().trim();
            if (!telephone.isEmpty()) {
                expertModifie.setTelephone(telephone);
            }
            
            String experience = experienceField.getText().trim();
            if (!experience.isEmpty()) {
                expertModifie.setAnneeExperience(Integer.parseInt(experience));
            }

            // Supprimer l'ancien expert et ajouter le nouveau
            serviceExpert.supprimer(expertAModifier);
            serviceExpert.ajouter(expertModifie);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'expert a été modifié avec succès");
            fermerFenetre();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification de l'expert: " + e.getMessage());
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    private boolean validateFields() {
        // Vérifier que l'ID est un nombre valide
        try {
            int newId = Integer.parseInt(idField.getText().trim());
            if (newId <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID doit être un nombre positif");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'ID doit être un nombre valide");
            return false;
        }

        if (nomField.getText().trim().isEmpty() ||
            prenomField.getText().trim().isEmpty() ||
            emailField.getText().trim().isEmpty() ||
            specialiteComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs obligatoires (nom, prénom, email, spécialité)");
            return false;
        }

        if (!EMAIL_PATTERN.matcher(emailField.getText().trim()).matches()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format d'email invalide");
            return false;
        }

        String telephone = telephoneField.getText().trim();
        if (!telephone.isEmpty() && !PHONE_PATTERN.matcher(telephone).matches()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le numéro de téléphone doit contenir 10 chiffres");
            return false;
        }

        String experience = experienceField.getText().trim();
        if (!experience.isEmpty()) {
            try {
                int exp = Integer.parseInt(experience);
                if (exp < 0) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "L'année d'expérience doit être un nombre positif");
                    return false;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "L'année d'expérience doit être un nombre");
                return false;
            }
        }

        return true;
    }

    private void fermerFenetre() {
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 