package controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.concurrent.Worker.State;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import netscape.javascript.JSObject;
import javafx.application.HostServices;

public class    AjouterUniversiteController {

    @FXML private Button retourButton;
    @FXML private Button selectPhotoButton;
    @FXML private Button ajouterButton;
    @FXML private Button fermerButton;
    @FXML private Button openMapButton;

    @FXML private TextField nomField;
    @FXML private TextField domaineField;
    @FXML private TextField fraisField;
    @FXML private TextField villeField;

    @FXML private Label nomErrorLabel;
    @FXML private Label villeErrorLabel;
    @FXML private Label domaineErrorLabel;
    @FXML private Label fraisErrorLabel;
    @FXML private Label photoPathLabel;
    @FXML private Label selectedCityLabel;
    @FXML private TextField selectedAddressField;

    @FXML private ImageView photoPreview;
    @FXML private WebView mapView;

    private String selectedCity;
    private double selectedLat;
    private double selectedLng;
    private HostServices hostServices;

    @FXML
    public void initialize() {
        setupMap();
    }

    private void setupMap() {
        WebEngine webEngine = mapView.getEngine();
        
        // Load Google Maps with Google Maps JavaScript API
        String mapHtml = """
<!DOCTYPE html>
<html>
<head>
    <title>Google Maps Picker</title>
    <style>
        #map { height: 300px; width: 100%; }
        body { margin: 0; }
    </style>
</head>
<body>
    <div id=\"map\"></div>
    <script>
      let marker;
      function initMap() {
        const map = new google.maps.Map(document.getElementById(\"map\"), {
          center: { lat: 36.8065, lng: 10.1815 },
          zoom: 7,
        });

        map.addListener(\"click\", (e) => {
          if (marker) marker.setMap(null);
          marker = new google.maps.Marker({
            position: e.latLng,
            map: map,
            draggable: true
          });
          // Reverse geocode
          fetch('https://nominatim.openstreetmap.org/reverse?format=json&lat=' + e.latLng.lat() + '&lon=' + e.latLng.lng())
            .then(response => response.json())
            .then(data => {
              var city = (data.address && (data.address.city || data.address.town || data.address.village)) || 'Unknown';
              var address = data.display_name || '';
              if (window.java && window.java.setSelectedCity)
                window.java.setSelectedCity(city, address, e.latLng.lat(), e.latLng.lng());
            });
        });
      }
    </script>
    <script src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyB60t3liU2ZOMtoqL5AZCrJD573ZE2cAEw&callback=initMap\" async defer></script>
</body>
</html>
""";

        // Create a bridge between JavaScript and Java
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("java", new Object() {
                    public void setSelectedCity(String city, String address, double lat, double lng) {
                        System.out.println("Java bridge called: " + city + " (" + lat + "," + lng + ")");
                        selectedCity = city;
                        selectedLat = lat;
                        selectedLng = lng;
                        selectedCityLabel.setText("Ville sélectionnée: " + city);
                        villeField.setText(city);
                        selectedAddressField.setText(address);
                        villeErrorLabel.setVisible(false);
                    }
                });
            }
        });

        webEngine.loadContent(mapHtml);
    }

    @FXML
    private void handleRetourButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/recupereruniversite.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) retourButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Universités");
            stage.show();
        } catch (IOException e) {
            // Handle error
        }
    }

    @FXML
    private void handleSelectPhotoButton() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une photo");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Stage stage = (Stage) selectPhotoButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            photoPathLabel.setText(selectedFile.getAbsolutePath());
            Image image = new Image(selectedFile.toURI().toString());
            photoPreview.setImage(image);
        }
    }

    @FXML
    private void handleAjouterButton() {
        if (selectedCity == null || selectedCity.equals("Unknown") || selectedCity.trim().isEmpty()) {
            villeErrorLabel.setText("Veuillez sélectionner une ville sur la carte");
            villeErrorLabel.setVisible(true);
            return;
        }
        // TODO: Implement logic to add a university with the selected city
    }

    @FXML
    private void handleFermerButton() {
        // TODO: Implement logic to close the window or clear the form
    }

    @FXML
    private void handleOpenMapButton() {
        if (hostServices != null) {
            hostServices.showDocument("https://www.google.com/maps");
        }
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }
} 