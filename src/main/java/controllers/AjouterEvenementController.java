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
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AjouterEvenementController implements Initializable {
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

    private ServiceEvenement serviceEvenement;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceEvenement = new ServiceEvenement();
        
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
        annuler_button.setOnAction(event -> annulerFormulaire());
        choisir_image_button.setOnAction(event -> choisirImage());
        choisir_lieu_button.setOnAction(event -> showMapDialog());
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void validerFormulaire() {
        try {
            // Validation des champs
            if (nom_txtf.getText().isEmpty() || description_txtf.getText().isEmpty() ||
                date_picker.getValue() == null || lieu_txtf.getText().isEmpty() ||
                domaine_txtf.getText().isEmpty() || place_disponible_txtf.getText().isEmpty()) {
                showAlert("Erreur", "Erreur", "Veuillez remplir tous les champs");
                return;
            }

            // Vérification de l'image
            String imagePath = image_txtf.getText();
            if (imagePath == null || imagePath.isEmpty()) {
                showAlert("Erreur", "Erreur", "Veuillez sélectionner une image pour l'événement");
                return;
            }

            // Création de l'événement
            Evenement evenement = new Evenement(
                nom_txtf.getText(),
                description_txtf.getText(),
                date_picker.getValue().format(dateFormatter),
                lieu_txtf.getText(),
                domaine_txtf.getText(),
                Integer.parseInt(place_disponible_txtf.getText()),
                imagePath
            );

            // Ajout de l'événement
            serviceEvenement.ajouter(evenement);
            showAlert("Succès", "Succès", "Événement ajouté avec succès");
            
            // Redirection vers la page de gestion des événements
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_evenement.fxml"));
                Parent root = loader.load();
                
                Stage stage = (Stage) valider_button.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Gestion des Événements");
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur", "Erreur lors du retour à la gestion des événements");
            }

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur", "Erreur lors de l'ajout de l'événement");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Erreur", "Le nombre de places doit être un nombre entier");
        }
    }

    @FXML
    private void annulerFormulaire() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_evenement.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) annuler_button.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Événements");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur", "Erreur lors du retour à la gestion des événements");
        }
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
                        try {
                            window.java.setCoords(lat + ', ' + lng);
                        } catch (e) {
                            console.error('Error sending coordinates to Java:', e);
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

                    // Test the Java bridge
                    window.onload = function() {
                        console.log('Window loaded, testing Java bridge...');
                        try {
                            if (window.java) {
                                console.log('Java bridge is available');
                            } else {
                                console.error('Java bridge is not available');
                            }
                        } catch (e) {
                            console.error('Error testing Java bridge:', e);
                        }
                    };
                </script>
            </body>
            </html>
            """;

        webEngine.loadContent(htmlContent);
        System.out.println("WebView content loaded");

        // Bridge Java <-> JS pour remplir le champ dès le clic
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == javafx.concurrent.Worker.State.SUCCEEDED) {
                System.out.println("WebView loaded successfully");
                try {
                    JSObject window = (JSObject) webEngine.executeScript("window");
                    window.setMember("java", new Object() {
                        @SuppressWarnings("unused")
                        public void setCoords(String coords) {
                            System.out.println("Received coordinates from JavaScript: " + coords);
                            String[] parts = coords.split(", ");
                            if (parts.length == 2) {
                                String lat = parts[0];
                                String lng = parts[1];
                                // Appel à LocationIQ en tâche de fond
                                new Thread(() -> {
                                    String address = getAddressFromLocationIQ(lat, lng);
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
                    System.out.println("Java bridge initialized successfully");
                } catch (Exception e) {
                    System.err.println("Error initializing Java bridge: " + e.getMessage());
                    e.printStackTrace();
                }
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
        mapStage.showAndWait();
    }

    // Méthode pour obtenir l'adresse à partir des coordonnées avec LocationIQ
    private String getAddressFromLocationIQ(String lat, String lng) {
        try {
            System.out.println("Requesting address for coordinates: " + lat + ", " + lng);
            String apiKey = "pk.f837395c000e92811358ab1b5e5485d6";
            String url = String.format("https://us1.locationiq.com/v1/reverse?key=%s&lat=%s&lon=%s&format=json", 
                apiKey, lat, lng);
            
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "JavaFXApp");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            
            System.out.println("Sending request to LocationIQ...");
            if (conn.getResponseCode() == 200) {
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();
                
                System.out.println("Response received from LocationIQ");
                org.json.JSONObject json = new org.json.JSONObject(response.toString());
                if (json.has("display_name")) {
                    String address = json.getString("display_name");
                    System.out.println("Address found: " + address);
                    return address;
                }
            } else {
                System.out.println("Error response from LocationIQ: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            System.err.println("Error getting address from LocationIQ: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Returning coordinates as fallback");
        return lat + ", " + lng; // Retourne les coordonnées si l'adresse n'a pas pu être récupérée
    }

    @FXML
    public void onAccueilButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AcceuilAdmin.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Accueil Admin");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
        }
    }

    @FXML
    public void onUserButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminUser.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Utilisateurs");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
        }
    }

    @FXML
    public void onDossierButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminDossier.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Dossiers");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
        }
    }

    @FXML
    public void onUniversiteButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminuniversite.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Universités");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
        }
    }

    @FXML
    public void onEntretienButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Gestionnaire.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Entretiens");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
        }
    }

    @FXML
    public void onEvenementButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion_evenement.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Événements");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
        }
    }

    @FXML
    public void onHebergementButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterFoyer.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Foyers");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
        }
    }

    @FXML
    public void onRestaurantButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterRestaurant.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Restaurants");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur", "Erreur lors de l'ouverture");
        }
    }

    @FXML
    public void onVolsButtonClick(ActionEvent actionEvent) {
        showAlert("Information", "Information", "La fonctionnalité des vols sera bientôt disponible.");
    }

    @FXML
    public void onLogoutButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - GradAway");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur", "Erreur lors de la déconnexion");
        }
    }
} 