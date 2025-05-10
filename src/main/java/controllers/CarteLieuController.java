package controllers;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CarteLieuController {
    private static final String GOOGLE_MAPS_API_KEY = "AIzaSyA9kHY8jAE90YX5FvXyhxJ64_rYmfEPovA";
    
    @FXML private WebView carte_webview;
    @FXML private Button valider_button;
    @FXML private Button annuler_button;
    
    private TextField lieuTextField;
    private String selectedAddress;

    public void initialize() {
        WebEngine webEngine = carte_webview.getEngine();
        
        // Charger la carte Google Maps
        String htmlContent = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <title>Google Maps</title>
                <script src="https://maps.googleapis.com/maps/api/js?key=%s"></script>
                <style>
                    #map { height: 100%%; width: 100%%; }
                    html, body { height: 100%%; margin: 0; padding: 0; }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    var map;
                    var marker;
                    var geocoder;
                    
                    function initMap() {
                        map = new google.maps.Map(document.getElementById('map'), {
                            center: {lat: 36.8065, lng: 10.1815}, // Tunis
                            zoom: 13
                        });
                        
                        geocoder = new google.maps.Geocoder();
                        
                        map.addListener('click', function(e) {
                            placeMarker(e.latLng);
                            getAddress(e.latLng);
                        });
                    }
                    
                    function placeMarker(location) {
                        if (marker) {
                            marker.setPosition(location);
                        } else {
                            marker = new google.maps.Marker({
                                position: location,
                                map: map
                            });
                        }
                    }
                    
                    function getAddress(latLng) {
                        geocoder.geocode({'location': latLng}, function(results, status) {
                            if (status === 'OK') {
                                if (results[0]) {
                                    selectedAddress = results[0].formatted_address;
                                    // Envoyer l'adresse à Java
                                    window.location.href = "java-call:" + selectedAddress;
                                }
                            }
                        });
                    }
                    
                    window.onload = initMap;
                </script>
            </body>
            </html>
            """, GOOGLE_MAPS_API_KEY);
        
        webEngine.loadContent(htmlContent);
        
        // Écouter les changements d'URL pour récupérer l'adresse sélectionnée
        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.startsWith("java-call:")) {
                selectedAddress = newValue.substring(10);
                if (lieuTextField != null) {
                    lieuTextField.setText(selectedAddress);
                }
            }
        });
        
        valider_button.setOnAction(event -> fermerFenetre());
        annuler_button.setOnAction(event -> fermerFenetre());
    }
    
    public void setLieuTextField(TextField textField) {
        this.lieuTextField = textField;
    }
    
    private void fermerFenetre() {
        Stage stage = (Stage) valider_button.getScene().getWindow();
        stage.close();
    }
} 