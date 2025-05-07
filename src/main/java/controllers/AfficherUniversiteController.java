package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.layout.FlowPane;
import entities.Universite;
import Services.ServiceUniversite;

import java.sql.SQLException;
import java.util.List;

public class AfficherUniversiteController {

    @FXML
    private TextField searchField;
    @FXML
    private FlowPane universiteListView;
    @FXML
    private Button searchButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Button closeButton;

    private ServiceUniversite serviceUniversite = new ServiceUniversite();

    @FXML
    public void initialize() {
        loadUniversites();
    }

    @FXML
    private void handleSearchButton() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            loadUniversites();
        } else {
            try {
                List<Universite> filteredUniversites = serviceUniversite.searchUniversites(searchText);
                displayUniversites(filteredUniversites);
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur de recherche", "Une erreur est survenue lors de la recherche", e.getMessage());
            }
        }
    }

    @FXML
    private void handleRefreshButton() {
        searchField.clear();
        loadUniversites();
    }

    @FXML
    private void handleCloseButton() {
        // Logic to close the window or navigate back
    }

    private void loadUniversites() {
        try {
            List<Universite> universites = serviceUniversite.getAllUniversites();
            displayUniversites(universites);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement", "Une erreur est survenue lors du chargement des universités", e.getMessage());
        }
    }

    private void displayUniversites(List<Universite> universites) {
        universiteListView.getChildren().clear();
        for (Universite universite : universites) {
            // Create and add UI components for each universite
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}