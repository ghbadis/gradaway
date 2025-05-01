package controllers;

import Services.ServiceDossier;
import entities.Dossier;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AdminDossiercontroller implements Initializable {
    @FXML
    private TableView<Dossier> TvDossier;
    @FXML
    private TableColumn<Dossier, Integer> idColumn;
    @FXML
    private TableColumn<Dossier, Integer> idEtudiantColumn;
    @FXML
    private TableColumn<Dossier, String> cinColumn;
    @FXML
    private TableColumn<Dossier, String> photoColumn;
    @FXML
    private TableColumn<Dossier, String> diplomeBacColumn;
    @FXML
    private TableColumn<Dossier, String> releveNoteColumn;
    @FXML
    private TableColumn<Dossier, String> diplomeObtenusColumn;
    @FXML
    private TableColumn<Dossier, String> lettreMotivationsColumn;
    @FXML
    private TableColumn<Dossier, String> dossierSanteColumn;
    @FXML
    private TableColumn<Dossier, String> cvColumn;
    @FXML
    private TableColumn<Dossier, String> dateDepotColumn;

    private ServiceDossier serviceDossier;
    private ObservableList<Dossier> dossierList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceDossier = new ServiceDossier();
        dossierList = FXCollections.observableArrayList();
        
        // Configure table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id_dossier"));
        idEtudiantColumn.setCellValueFactory(new PropertyValueFactory<>("id_etudiant"));
        cinColumn.setCellValueFactory(new PropertyValueFactory<>("cin"));
        photoColumn.setCellValueFactory(new PropertyValueFactory<>("photo"));
        diplomeBacColumn.setCellValueFactory(new PropertyValueFactory<>("diplome_baccalauréat"));
        releveNoteColumn.setCellValueFactory(new PropertyValueFactory<>("releve_note"));
        diplomeObtenusColumn.setCellValueFactory(new PropertyValueFactory<>("diplome_obtenus"));
        lettreMotivationsColumn.setCellValueFactory(new PropertyValueFactory<>("lettre_motivations"));
        dossierSanteColumn.setCellValueFactory(new PropertyValueFactory<>("dossier_sante"));
        cvColumn.setCellValueFactory(new PropertyValueFactory<>("cv"));
        dateDepotColumn.setCellValueFactory(new PropertyValueFactory<>("datedepot"));
        
        // Load data
        loadDossierData();
    }

    private void loadDossierData() {
        try {
            dossierList.clear();
            dossierList.addAll(serviceDossier.recuperer());
            TvDossier.setItems(dossierList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




}
