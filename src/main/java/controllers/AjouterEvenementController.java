package controllers;

import entities.Evenement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import Services.ServiceEvenement;
import javafx.scene.web.WebView;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import javafx.geometry.Pos;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AjouterEvenementController {
    @FXML private TextField nom_txtf;
    @FXML private TextField description_txtf;
    @FXML private DatePicker date_picker;
    @FXML private TextField lieu_txtf;
    @FXML private TextField domaine_txtf;
    @FXML private TextField place_disponible_txtf;
    @FXML private TextField image_txtf;
    @FXML private Button valider_button;
    @FXML private Button annuler_button;
    @FXML private Button choisir_image_button;
    @FXML private Button choisir_lieu_button;

    private final ServiceEvenement serviceEvenement = new ServiceEvenement();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        // Définir la date minimale à aujourd'hui
        date_picker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.isBefore(today));
            }
        });
        
        // Définir la date par défaut à aujourd'hui
        date_picker.setValue(LocalDate.now());

        valider_button.setOnAction(event -> validerFormulaire());
        annuler_button.setOnAction(event -> fermerFenetre());
        choisir_image_button.setOnAction(event -> choisirImage());
        choisir_lieu_button.setOnAction(event -> showMapDialog());
    }

    private void validerFormulaire() {
        try {
            String nom = nom_txtf.getText();
            String description = description_txtf.getText();
            LocalDate dateValue = date_picker.getValue();
            String lieu = lieu_txtf.getText();
            String domaine = domaine_txtf.getText();
            String placesStr = place_disponible_txtf.getText();
            String image = image_txtf != null ? image_txtf.getText() : null;

            // Contrôle de saisie : tous les champs doivent être remplis
            if (nom.isEmpty() || description.isEmpty() || dateValue == null || lieu.isEmpty() || domaine.isEmpty() || placesStr.isEmpty() || image == null || image.isEmpty()) {
                showAlert("Erreur", "Champs obligatoires", "Veuillez remplir tous les champs avant d'ajouter l'événement.");
                return;
            }

            int placesDisponibles = Integer.parseInt(placesStr);
            String date = dateValue.format(dateFormatter);

            Evenement evenement = new Evenement(nom, description, date, lieu, domaine, placesDisponibles, image);
            serviceEvenement.ajouter(evenement);
            
            showAlert("Succès", "Événement ajouté avec succès", null);
            fermerFenetre();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout de l'événement", e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Format invalide", "Le nombre de places doit être un nombre entier");
        }
    }

    private void fermerFenetre() {
        Stage stage = (Stage) annuler_button.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void choisirImage() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
            new javafx.stage.FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        java.io.File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            image_txtf.setText(selectedFile.toURI().toString());
        }
    }

    @FXML
    public void showMapDialog() {
        Stage mapStage = new Stage();
        mapStage.setTitle("Sélectionner un lieu");

        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Leaflet Map</title>
                <link rel="stylesheet" href="https://unpkg.com/leaflet@0.7.7/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@0.7.7/dist/leaflet.js"></script>
                <style>
                    #map { height: 500px; width: 100%; background: #eaeaea; }
                    #selectedCoords {
                        margin: 10px;
                        padding: 10px;
                        background-color: #f8f9fa;
                        border: 1px solid #dee2e6;
                        border-radius: 4px;
                        min-height: 20px;
                    }
                </style>
            </head>
            <body>
                <div id="map">Carte non chargée</div>
                <div id="selectedCoords"></div>
                <script>
                    var marker = null;
                    var selectedLat = '';
                    var selectedLng = '';
                    var map = L.map('map').setView([36.8065, 10.1815], 13);
                    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                        attribution: '© OpenStreetMap contributors'
                    }).addTo(map);

                    function sendCoordsToJava(lat, lng) {
                        console.log('Sending coordinates to Java:', lat, lng);
                        if (window.java && typeof window.java.setCoords === 'function') {
                            window.java.setCoords(lat + ', ' + lng);
                        } else {
                            console.error('Java bridge not available');
                        }
                    }

                    map.on('click', function(e) {
                        if (marker) {
                            map.removeLayer(marker);
                        }
                        marker = L.marker(e.latlng).addTo(map);
                        selectedLat = e.latlng.lat;
                        selectedLng = e.latlng.lng;
                        document.getElementById('selectedCoords').innerHTML = '<strong>Coordonnées sélectionnées :</strong> ' + selectedLat + ', ' + selectedLng;
                        sendCoordsToJava(selectedLat, selectedLng);
                    });
                </script>
            </body>
            </html>
            """;

        webEngine.loadContent(htmlContent);

        // Bridge Java <-> JS pour remplir le champ dès le clic
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == javafx.concurrent.Worker.State.SUCCEEDED) {
                System.out.println("WebView loaded successfully");
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("java", new Object() {
                    @SuppressWarnings("unused")
                    public void setCoords(String coords) {
                        System.out.println("Received coordinates from JavaScript: " + coords);
                        String[] parts = coords.split(", ");
                        if (parts.length == 2) {
                            String lat = parts[0];
                            String lng = parts[1];
                            // Appel à Nominatim en tâche de fond
                            new Thread(() -> {
                                String address = getAddressFromNominatim(lat, lng);
                                javafx.application.Platform.runLater(() -> {
                                    if (address != null && !address.isEmpty()) {
                                        lieu_txtf.setText(address);
                                        System.out.println("Address set in text field: " + address);
                                    } else {
                                        lieu_txtf.setText(coords);
                                        System.out.println("Failed to get address, fallback to coords");
                                        Alert alert = new Alert(Alert.AlertType.WARNING, "Impossible de récupérer l'adresse. Les coordonnées sont utilisées à la place.");
                                        alert.showAndWait();
                                    }
                                });
                            }).start();
                        }
                    }
                });
            }
        });

        Button validerButton = new Button("Valider");
        validerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        validerButton.setOnAction(e -> {
            System.out.println("Validating selected location...");
            mapStage.close();
        });

        VBox root = new VBox(10, webView, validerButton);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 800, 600);
        mapStage.setScene(scene);
        mapStage.show();
    }

    // Méthode pour obtenir l'adresse à partir des coordonnées avec Nominatim
    private String getAddressFromNominatim(String lat, String lng) {
        try {
            System.out.println("Requesting address for coordinates: " + lat + ", " + lng);
            String url = String.format("https://nominatim.openstreetmap.org/reverse?format=json&lat=%s&lon=%s&zoom=18&addressdetails=1", lat, lng);
            
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "JavaFXApp"); // Important pour respecter les conditions d'utilisation
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            System.out.println("Sending request to Nominatim...");
            if (conn.getResponseCode() == 200) {
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();
                
                System.out.println("Response received from Nominatim");
                org.json.JSONObject json = new org.json.JSONObject(response.toString());
                if (json.has("display_name")) {
                    String address = json.getString("display_name");
                    System.out.println("Address found: " + address);
                    return address;
                }
            } else {
                System.out.println("Error response from Nominatim: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            System.err.println("Error getting address from Nominatim: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Returning coordinates as fallback");
        return lat + ", " + lng; // Retourne les coordonnées si l'adresse n'a pas pu être récupérée
    }
} 