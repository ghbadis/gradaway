package controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import Services.ServiceEvenement;
import entities.Evenement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatistiquesEvenementsController {
    @FXML
    private BarChart<String, Number> evenementsChart;
    
    @FXML
    private PieChart reservationsChart;
    
    private ServiceEvenement serviceEvenement;
    
    @FXML
    public void initialize() {
        serviceEvenement = new ServiceEvenement();
        loadData();
    }
    
    private void loadData() {
        try {
            List<Evenement> evenements = serviceEvenement.recuperer();
            
            // Préparer les données pour le graphique en barres
            Map<String, Integer> domainesCount = new HashMap<>();
            for (Evenement evenement : evenements) {
                domainesCount.merge(evenement.getDomaine(), 1, Integer::sum);
            }
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Nombre d'événements");
            
            for (Map.Entry<String, Integer> entry : domainesCount.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            
            evenementsChart.getData().add(series);
            
            // Préparer les données pour le graphique en camembert
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Evenement evenement : evenements) {
                pieChartData.add(new PieChart.Data(
                    evenement.getNom(),
                    evenement.getPlaces_disponibles()
                ));
            }
            
            reservationsChart.setData(pieChartData);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 