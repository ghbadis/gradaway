package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.MyDatabase;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class AffecterExpertController {
    @FXML private Label nomCandidatLabel;
    @FXML private Label domaineLabel;
    @FXML private Label dateSouhaiteeLabel;
    @FXML private ComboBox<ExpertItem> expertsComboBox;
    
    private ListeDemandesEntretienController.DemandeEntretien demandeEntretien;
    
    public void setDemandeEntretien(ListeDemandesEntretienController.DemandeEntretien demande) {
        this.demandeEntretien = demande;
        updateLabels();
        loadExperts();
    }
    
    private void updateLabels() {
        nomCandidatLabel.setText(demandeEntretien.getNomUser());
        domaineLabel.setText(demandeEntretien.getDomaine());
        dateSouhaiteeLabel.setText(demandeEntretien.getDateSouhaitee().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }
    
    private void loadExperts() {
        try {
            String query = "SELECT id_expert, nom_expert, prenom_expert FROM expert WHERE specialite = ?";
            try (Connection con = MyDatabase.getInstance().getCnx();
                 PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, demandeEntretien.getDomaine());
                ResultSet rs = ps.executeQuery();
                ObservableList<ExpertItem> experts = FXCollections.observableArrayList();
                while (rs.next()) {
                    experts.add(new ExpertItem(rs.getInt("id_expert"), rs.getString("nom_expert"), rs.getString("prenom_expert")));
                }
                expertsComboBox.setItems(experts);
                if (!experts.isEmpty()) {
                    expertsComboBox.getSelectionModel().selectFirst();
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des experts: " + e.getMessage());
        }
    }
    
    @FXML
    private void confirmerAffectation() {
        ExpertItem selected = expertsComboBox.getValue();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez sélectionner un expert");
            return;
        }
        try {
            String query = "UPDATE demandes_entretien SET id_expert = ? WHERE id_demande = ?";
            try (Connection con = MyDatabase.getInstance().getCnx();
                 PreparedStatement ps = con.prepareStatement(query)) {
                ps.setInt(1, selected.getIdExpert());
                ps.setInt(2, demandeEntretien.getIdDemande());
                ps.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Expert affecté avec succès");
                fermer();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'affectation de l'expert: " + e.getMessage());
        }
    }
    
    @FXML
    private void annuler() {
        fermer();
    }
    
    private void fermer() {
        Stage stage = (Stage) expertsComboBox.getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Helper class for ComboBox
    private static class ExpertItem {
        private final int idExpert;
        private final String nom;
        private final String prenom;
        public ExpertItem(int idExpert, String nom, String prenom) {
            this.idExpert = idExpert;
            this.nom = nom;
            this.prenom = prenom;
        }
        public int getIdExpert() { return idExpert; }
        @Override
        public String toString() { return nom + " " + prenom; }
    }
} 