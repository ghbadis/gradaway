package controller;

import Services.serviceVols;
import entities.Vols;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.MyDatabase;

import java.io.File;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AjouterVolsController {
    @FXML
    private Button chooseImageButton;
    @FXML
    private ImageView imagePreview;
    @FXML
    private Label imagePathLabel;
    @FXML
    private ComboBox<String> compagnieComboBox;
    @FXML
    private ComboBox<String> numeroVolComboBox;
    @FXML
    private ComboBox<String> aeroportDepartComboBox;
    @FXML
    private ComboBox<String> aeroportArriveeComboBox;
    @FXML
    private ComboBox<String> villeDepartComboBox;
    @FXML
    private ComboBox<String> villeArriveeComboBox;
    @FXML
    private ComboBox<String> paysDepartComboBox;
    @FXML
    private ComboBox<String> paysArriveeComboBox;
    @FXML
    private DatePicker dateDepartPicker;
    @FXML
    private DatePicker dateArriveePicker;
    @FXML
    private TextField dureeField;
    @FXML
    private TextField prixField;
    @FXML
    private TextField placesField;
    @FXML
    private TextField imageFileNameField;
    @FXML
    private Button validerButton;
    @FXML
    private Button annulerButton;


    @FXML
    private void handleChooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Airline Logo Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Convert file to URL
                String fileUrl = selectedFile.toURI().toURL().toString();
                selectedImagePath = fileUrl;
                imagePathLabel.setText(selectedFile.getName());

                // Show preview
                Image image = new Image(fileUrl, 100, 100, true, true);
                imagePreview.setImage(image);
            } catch (MalformedURLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement de l'image: " + e.getMessage());
            }
        }
    }


    private String getCompanyLogoUrl(String compagnie) {
        if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
            return selectedImagePath;
        }

        // Default URLs if no image is selected
        switch (compagnie.toLowerCase()) {
            case "air france":
                return "https://upload.wikimedia.org/wikipedia/commons/4/44/Air_France_Logo.svg";
            case "emirates":
                return "https://upload.wikimedia.org/wikipedia/commons/d/d0/Emirates_logo.svg";
            case "qatar airways":
                return "https://upload.wikimedia.org/wikipedia/commons/0/05/Qatar_Airways_Logo.svg";
            case "turkish airlines":
                return "https://upload.wikimedia.org/wikipedia/commons/8/8b/Turkish_Airlines_logo_2019_compact.svg";
            case "lufthansa":
                return "https://upload.wikimedia.org/wikipedia/commons/8/82/Lufthansa_Logo_2018.svg";
            default:
                return "https://upload.wikimedia.org/wikipedia/commons/thumb/0/0c/Airplane_silhouette.svg/2048px-Airplane_silhouette.svg.png";
        }
    }

    private serviceVols serviceVols;
    private final Map<String, List<String>> paysVillesMap = new HashMap<>();
    private final Map<String, List<String>> villeAeroportsMap = new HashMap<>();
    private final Map<String, List<String>> compagnieVolsMap = new HashMap<>();
    private String selectedImagePath;

    public AjouterVolsController() {
        Connection connection = MyDatabase.getInstance().getCnx();
        serviceVols = new serviceVols(connection);
        initializeMockData();
    }
    private List<String> getAeroports() {
        return List.of(
                "CDG - Paris Charles de Gaulle, France",
                "JFK - John F. Kennedy, États-Unis",
                "DXB - Dubai International, Émirats Arabes Unis",
                "SYD - Sydney Kingsford Smith, Australie",
                "DOH - Hamad International, Qatar",
                "IST - Istanbul, Turquie",
                "FRA - Frankfurt, Allemagne",
                "LHR - London Heathrow, Royaume-Uni",
                "HND - Tokyo Haneda, Japon",
                "SIN - Singapore Changi, Singapour"
        );
    }
    private void initializeMockData() {
        // Données des compagnies aériennes et leurs numéros de vols
        Map<String, String[]> compagnieData = new HashMap<>();

        // Tunisair
        compagnieData.put("Tunisair", new String[]{
                "TU 101", "TU 102", "TU 201", "TU 202", "TU 207", "TU 208",
                "TU 711", "TU 712", "TU 721", "TU 722", "TU 815", "TU 816"
        });

        // Air France
        compagnieData.put("Air France", new String[]{
                "AF 1084", "AF 1085", "AF 1284", "AF 1285", "AF 1784", "AF 1785",
                "AF 2584", "AF 2585", "AF 3484", "AF 3485", "AF 4084", "AF 4085"
        });

        // Nouvelair
        compagnieData.put("Nouvelair", new String[]{
                "BJ 001", "BJ 002", "BJ 101", "BJ 102", "BJ 201", "BJ 202",
                "BJ 301", "BJ 302", "BJ 401", "BJ 402", "BJ 501", "BJ 502"
        });

        // Royal Air Maroc
        compagnieData.put("Royal Air Maroc", new String[]{
                "AT 500", "AT 501", "AT 502", "AT 503", "AT 504", "AT 505",
                "AT 600", "AT 601", "AT 602", "AT 603", "AT 604", "AT 605"
        });

        // Lufthansa
        compagnieData.put("Lufthansa", new String[]{
                "LH 1234", "LH 1235", "LH 1236", "LH 1237", "LH 1238", "LH 1239",
                "LH 2234", "LH 2235", "LH 2236", "LH 2237", "LH 2238", "LH 2239"
        });

        // British Airways
        compagnieData.put("British Airways", new String[]{
                "BA 800", "BA 801", "BA 802", "BA 803", "BA 804", "BA 805",
                "BA 810", "BA 811", "BA 812", "BA 813", "BA 814", "BA 815"
        });

        // Données des pays et leurs villes
        Map<String, String[]> paysData = new HashMap<>();

// Tunisie
        paysData.put("Tunisie", new String[]{
                "Tunis", "Sfax", "Sousse", "Monastir", "Djerba", "Tozeur", "Tabarka"
        });

// France
        paysData.put("France", new String[]{
                "Paris", "Lyon", "Marseille", "Nice", "Toulouse", "Bordeaux", "Nantes"
        });

// Espagne
        paysData.put("Espagne", new String[]{
                "Madrid", "Barcelone", "Valence", "Séville", "Malaga", "Palma de Majorque"
        });

// Italie
        paysData.put("Italie", new String[]{
                "Rome", "Milan", "Venise", "Florence", "Naples", "Turin", "Palerme"
        });

// Allemagne
        paysData.put("Allemagne", new String[]{
                "Berlin", "Munich", "Francfort", "Hambourg", "Düsseldorf", "Stuttgart"
        });

// Royaume-Uni
        paysData.put("Royaume-Uni", new String[]{
                "Londres", "Manchester", "Birmingham", "Édimbourg", "Glasgow", "Bristol"
        });

// Maroc
        paysData.put("Maroc", new String[]{
                "Casablanca", "Marrakech", "Rabat", "Tanger", "Fès", "Agadir"
        });

// Map des villes et leurs aéroports
        Map<String, String[]> villeAeroportsData = new HashMap<>();

// Aéroports en Tunisie
        villeAeroportsData.put("Tunis", new String[]{"Aéroport International de Tunis-Carthage"});
        villeAeroportsData.put("Sfax", new String[]{"Aéroport International de Sfax-Thyna"});
        villeAeroportsData.put("Sousse", new String[]{"Aéroport International de Sousse"});
        villeAeroportsData.put("Monastir", new String[]{"Aéroport International de Monastir Habib Bourguiba"});
        villeAeroportsData.put("Djerba", new String[]{"Aéroport International de Djerba-Zarzis"});
        villeAeroportsData.put("Tozeur", new String[]{"Aéroport International de Tozeur-Nefta"});
        villeAeroportsData.put("Tabarka", new String[]{"Aéroport International de Tabarka-Aïn Draham"});

// Aéroports en France
        villeAeroportsData.put("Paris", new String[]{"Charles de Gaulle (CDG)", "Orly (ORY)", "Paris-Beauvais (BVA)"});
        villeAeroportsData.put("Lyon", new String[]{"Saint-Exupéry (LYS)", "Lyon-Bron"});
        villeAeroportsData.put("Marseille", new String[]{"Marseille Provence (MRS)"});
        villeAeroportsData.put("Nice", new String[]{"Nice Côte d'Azur (NCE)"});
        villeAeroportsData.put("Toulouse", new String[]{"Toulouse-Blagnac (TLS)"});
        villeAeroportsData.put("Bordeaux", new String[]{"Aéroport de Bordeaux-Mérignac (BOD)"});
        villeAeroportsData.put("Nantes", new String[]{"Aéroport Nantes Atlantique (NTE)"});

// Aéroports en Espagne
        villeAeroportsData.put("Madrid", new String[]{"Adolfo Suárez Madrid–Barajas (MAD)"});
        villeAeroportsData.put("Barcelone", new String[]{"Josep Tarradellas Barcelona-El Prat (BCN)"});
        villeAeroportsData.put("Valence", new String[]{"Aéroport de Valence (VLC)"});
        villeAeroportsData.put("Séville", new String[]{"Aéroport de Séville-San Pablo (SVQ)"});
        villeAeroportsData.put("Malaga", new String[]{"Aéroport de Malaga-Costa del Sol (AGP)"});
        villeAeroportsData.put("Palma de Majorque", new String[]{"Aéroport de Palma de Majorque (PMI)"});

// Aéroports en Italie
        villeAeroportsData.put("Rome", new String[]{"Fiumicino (FCO)", "Ciampino (CIA)"});
        villeAeroportsData.put("Milan", new String[]{"Malpensa (MXP)", "Linate (LIN)", "Bergamo (BGY)"});
        villeAeroportsData.put("Venise", new String[]{"Marco Polo (VCE)", "Treviso (TSF)"});
        villeAeroportsData.put("Florence", new String[]{"Aéroport de Florence-Peretola (FLR)"});
        villeAeroportsData.put("Naples", new String[]{"Aéroport de Naples-Capodichino (NAP)"});
        villeAeroportsData.put("Turin", new String[]{"Aéroport de Turin-Caselle (TRN)"});
        villeAeroportsData.put("Palerme", new String[]{"Aéroport de Palerme-Punta Raisi (PMO)"});

// Aéroports en Allemagne
        villeAeroportsData.put("Berlin", new String[]{"Berlin Brandenburg (BER)"});
        villeAeroportsData.put("Munich", new String[]{"Aéroport de Munich-Franz-Josef Strauss (MUC)"});
        villeAeroportsData.put("Francfort", new String[]{"Aéroport de Francfort-sur-le-Main (FRA)"});
        villeAeroportsData.put("Hambourg", new String[]{"Aéroport de Hambourg (HAM)"});
        villeAeroportsData.put("Düsseldorf", new String[]{"Aéroport de Düsseldorf (DUS)"});
        villeAeroportsData.put("Stuttgart", new String[]{"Aéroport de Stuttgart (STR)"});

// Aéroports au Royaume-Uni
        villeAeroportsData.put("Londres", new String[]{"Heathrow (LHR)", "Gatwick (LGW)", "Stansted (STN)", "Luton (LTN)"});
        villeAeroportsData.put("Manchester", new String[]{"Manchester Airport (MAN)"});
        villeAeroportsData.put("Birmingham", new String[]{"Birmingham Airport (BHX)"});
        villeAeroportsData.put("Édimbourg", new String[]{"Edinburgh Airport (EDI)"});
        villeAeroportsData.put("Glasgow", new String[]{"Glasgow International Airport (GLA)"});
        villeAeroportsData.put("Bristol", new String[]{"Bristol Airport (BRS)"});

// Aéroports au Maroc
        villeAeroportsData.put("Casablanca", new String[]{"Mohammed V International (CMN)"});
        villeAeroportsData.put("Marrakech", new String[]{"Marrakech-Menara (RAK)"});
        villeAeroportsData.put("Rabat", new String[]{"Rabat–Salé (RBA)"});
        villeAeroportsData.put("Tanger", new String[]{"Ibn Battouta International (TNG)"});
        villeAeroportsData.put("Fès", new String[]{"Aéroport de Fès-Saïss (FEZ)"});
        villeAeroportsData.put("Agadir", new String[]{"Aéroport d'Agadir-Al Massira (AGA)"});

        // Conversion des données en Lists
        compagnieData.forEach((compagnie, vols) ->
                compagnieVolsMap.put(compagnie, Arrays.asList(vols))
        );

        paysData.forEach((pays, villes) ->
                paysVillesMap.put(pays, Arrays.asList(villes))
        );

        villeAeroportsData.forEach((ville, aeroports) ->
                villeAeroportsMap.put(ville, Arrays.asList(aeroports))
        );
    }

    @FXML
    public void initialize() {
        // Add this at the beginning of your initialize method
        imagePreview.setFitHeight(100);
        imagePreview.setFitWidth(100);
        imagePreview.setPreserveRatio(true);
        // Initialisation de la ComboBox des compagnies
        compagnieComboBox.setItems(FXCollections.observableArrayList(compagnieVolsMap.keySet()));

        // Remplir les ComboBox des pays
        paysDepartComboBox.setItems(FXCollections.observableArrayList(paysVillesMap.keySet()));
        paysArriveeComboBox.setItems(FXCollections.observableArrayList(paysVillesMap.keySet()));

        // Configuration des DatePickers
        LocalDate now = LocalDate.now();
        LocalDate oneYearFromNow = now.plusYears(1);

        // Configuration du DatePicker de départ
        dateDepartPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(now) < 0 || date.compareTo(oneYearFromNow) > 0);
            }
        });

        // Configuration du DatePicker d'arrivée
        dateArriveePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate minDate = dateDepartPicker.getValue();
                setDisable(empty || date.compareTo(minDate) < 0 || date.compareTo(oneYearFromNow) > 0);
            }
        });

        // Listener pour mettre à jour la date d'arrivée minimale quand la date de départ change
        dateDepartPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (dateArriveePicker.getValue() != null &&
                        dateArriveePicker.getValue().compareTo(newValue) < 0) {
                    dateArriveePicker.setValue(newValue);
                }
                dateArriveePicker.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(empty || date.compareTo(newValue) < 0 ||
                                date.compareTo(oneYearFromNow) > 0);
                    }
                });
            }
        });

        // Validation de la durée (entre 30 et 120 minutes)
        dureeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                dureeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (!newValue.isEmpty()) {
                try {
                    int duree = Integer.parseInt(newValue);
                    if (duree < 30 || duree > 120) {
                        dureeField.setStyle("-fx-border-color: red;");
                    } else {
                        dureeField.setStyle("-fx-border-color: #cccccc;");
                    }
                } catch (NumberFormatException e) {
                    dureeField.setStyle("-fx-border-color: red;");
                }
            }
        });

        // Validation du prix (entre 50 et 1200)
        prixField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                prixField.setText(oldValue);
            }
            if (!newValue.isEmpty()) {
                try {
                    double prix = Double.parseDouble(newValue);
                    if (prix < 50 || prix > 1200) {
                        prixField.setStyle("-fx-border-color: red;");
                    } else {
                        prixField.setStyle("-fx-border-color: #cccccc;");
                    }
                } catch (NumberFormatException e) {
                    prixField.setStyle("-fx-border-color: red;");
                }
            }
        });

        // Validation des places (entre 50 et 200)
        placesField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                placesField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (!newValue.isEmpty()) {
                try {
                    int places = Integer.parseInt(newValue);
                    if (places < 50 || places > 200) {
                        placesField.setStyle("-fx-border-color: red;");
                    } else {
                        placesField.setStyle("-fx-border-color: #cccccc;");
                    }
                } catch (NumberFormatException e) {
                    placesField.setStyle("-fx-border-color: red;");
                }
            }
        });

        // Listeners pour les changements des ComboBox
        compagnieComboBox.setOnAction(e -> {
            String compagnie = compagnieComboBox.getValue();
            if (compagnie != null) {
                updateNumeroVolComboBox(compagnie);
            }
        });

        paysDepartComboBox.setOnAction(e -> {
            String paysDepart = paysDepartComboBox.getValue();
            if (paysDepart != null) {
                updateVillesDepartComboBox(paysDepart);
                validatePaysDifferents();
            }
        });

        paysArriveeComboBox.setOnAction(e -> {
            String paysArrivee = paysArriveeComboBox.getValue();
            if (paysArrivee != null) {
                updateVillesArriveeComboBox(paysArrivee);
                validatePaysDifferents();
            }
        });

        villeDepartComboBox.setOnAction(e -> {
            String villeDepart = villeDepartComboBox.getValue();
            if (villeDepart != null) {
                updateAeroportsDepartComboBox(villeDepart);
            }
        });

        villeArriveeComboBox.setOnAction(e -> {
            String villeArrivee = villeArriveeComboBox.getValue();
            if (villeArrivee != null) {
                updateAeroportsArriveeComboBox(villeArrivee);
            }
        });

        // Initialiser les dates
        dateDepartPicker.setValue(now);
        dateArriveePicker.setValue(now);
    }

    private void updateNumeroVolComboBox(String compagnie) {
        List<String> numerosVol = compagnieVolsMap.get(compagnie);
        if (numerosVol != null) {
            numeroVolComboBox.setItems(FXCollections.observableArrayList(numerosVol));
        }
    }

    private void updateVillesDepartComboBox(String pays) {
        List<String> villes = paysVillesMap.get(pays);
        villeDepartComboBox.setItems(FXCollections.observableArrayList(villes));
        villeDepartComboBox.getSelectionModel().clearSelection();
        aeroportDepartComboBox.getItems().clear();
    }

    private void updateVillesArriveeComboBox(String pays) {
        List<String> villes = paysVillesMap.get(pays);
        villeArriveeComboBox.setItems(FXCollections.observableArrayList(villes));
        villeArriveeComboBox.getSelectionModel().clearSelection();
        aeroportArriveeComboBox.getItems().clear();
    }

    private void updateAeroportsDepartComboBox(String ville) {
        List<String> aeroports = villeAeroportsMap.get(ville);
        if (aeroports != null) {
            aeroportDepartComboBox.setItems(FXCollections.observableArrayList(aeroports));
        }
    }

    private void updateAeroportsArriveeComboBox(String ville) {
        List<String> aeroports = villeAeroportsMap.get(ville);
        if (aeroports != null) {
            aeroportArriveeComboBox.setItems(FXCollections.observableArrayList(aeroports));
        }
    }

    private boolean validatePaysDifferents() {
        String paysDepart = paysDepartComboBox.getValue();
        String paysArrivee = paysArriveeComboBox.getValue();

        if (paysDepart != null && paysArrivee != null && paysDepart.equals(paysArrivee)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le pays de départ et d'arrivée doivent être différents!");
            paysArriveeComboBox.setValue(null);
            return false;
        }
        return true;
    }

    private boolean validateRequiredFields() {
        StringBuilder errorMessage = new StringBuilder();
        LocalDate now = LocalDate.now();
        LocalDate oneYearFromNow = now.plusYears(1);

        if (compagnieComboBox.getValue() == null) errorMessage.append("- Compagnie aérienne requise\n");
        if (numeroVolComboBox.getValue() == null) errorMessage.append("- Numéro de vol requis\n");
        if (paysDepartComboBox.getValue() == null) errorMessage.append("- Pays de départ requis\n");
        if (paysArriveeComboBox.getValue() == null) errorMessage.append("- Pays d'arrivée requis\n");
        if (villeDepartComboBox.getValue() == null) errorMessage.append("- Ville de départ requise\n");
        if (villeArriveeComboBox.getValue() == null) errorMessage.append("- Ville d'arrivée requise\n");
        if (aeroportDepartComboBox.getValue() == null) errorMessage.append("- Aéroport de départ requis\n");
        if (aeroportArriveeComboBox.getValue() == null) errorMessage.append("- Aéroport d'arrivée requis\n");

        // Validation des dates
        if (dateDepartPicker.getValue() == null) {
            errorMessage.append("- Date de départ requise\n");
        } else {
            LocalDate dateDepart = dateDepartPicker.getValue();
            if (dateDepart.compareTo(now) < 0) {
                errorMessage.append("- La date de départ ne peut pas être antérieure à aujourd'hui\n");
            }
            if (dateDepart.compareTo(oneYearFromNow) > 0) {
                errorMessage.append("- La date de départ ne peut pas être supérieure à un an à partir d'aujourd'hui\n");
            }
        }

        if (dateArriveePicker.getValue() == null) {
            errorMessage.append("- Date d'arrivée requise\n");
        } else {
            LocalDate dateArrivee = dateArriveePicker.getValue();
            LocalDate dateDepart = dateDepartPicker.getValue();

            if (dateDepart != null && dateArrivee.compareTo(dateDepart) < 0) {
                errorMessage.append("- La date d'arrivée doit être égale ou postérieure à la date de départ\n");
            }
            if (dateArrivee.compareTo(oneYearFromNow) > 0) {
                errorMessage.append("- La date d'arrivée ne peut pas être supérieure à un an à partir d'aujourd'hui\n");
            }
        }

        // Validation de la durée
        if (dureeField.getText().trim().isEmpty()) {
            errorMessage.append("- Durée requise\n");
        } else {
            try {
                int duree = Integer.parseInt(dureeField.getText().trim());
                if (duree < 30 || duree > 120) {
                    errorMessage.append("- La durée doit être comprise entre 30 et 120 minutes\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("- La durée doit être un nombre valide\n");
            }
        }

        // Validation du prix
        if (prixField.getText().trim().isEmpty()) {
            errorMessage.append("- Prix requis\n");
        } else {
            try {
                double prix = Double.parseDouble(prixField.getText().trim());
                if (prix < 50 || prix > 1200) {
                    errorMessage.append("- Le prix doit être compris entre 50 et 1200\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("- Le prix doit être un nombre valide\n");
            }
        }

        // Validation des places
        if (placesField.getText().trim().isEmpty()) {
            errorMessage.append("- Nombre de places requis\n");
        } else {
            try {
                int places = Integer.parseInt(placesField.getText().trim());
                if (places < 50 || places > 200) {
                    errorMessage.append("- Le nombre de places doit être compris entre 50 et 200\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("- Le nombre de places doit être un nombre valide\n");
            }
        }

        if (errorMessage.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Champs invalides", errorMessage.toString());
            return false;
        }

        return true;
    }

    @FXML
    private void handleValider(ActionEvent event) {
        try {
            if (!validateRequiredFields()) {
                return;
            }

            String compagnie = compagnieComboBox.getValue();
            String numeroVol = numeroVolComboBox.getValue();
            String aeroportDepart = aeroportDepartComboBox.getValue();
            String aeroportArrivee = aeroportArriveeComboBox.getValue();
            String villeDepart = villeDepartComboBox.getValue();
            String villeArrivee = villeArriveeComboBox.getValue();
            String paysDepart = paysDepartComboBox.getValue();
            String paysArrivee = paysArriveeComboBox.getValue();
            LocalDate dateDepart = dateDepartPicker.getValue();
            LocalDate dateArrivee = dateArriveePicker.getValue();
            Integer duree = Integer.parseInt(dureeField.getText().trim());
            double prix = Double.parseDouble(prixField.getText().trim());
            int places = Integer.parseInt(placesField.getText().trim());

            // Use either selected image or default logo URL
            String imageUrl = selectedImagePath != null ? selectedImagePath : getCompanyLogoUrl(compagnie);

            LocalDateTime dateDepartDT = dateDepart.atTime(LocalTime.MIDNIGHT);
            LocalDateTime dateArriveeDT = dateArrivee.atTime(LocalTime.MIDNIGHT);

            Vols vol = new Vols(
                    0,
                    numeroVol,
                    compagnie,
                    aeroportDepart,
                    villeDepart,
                    paysDepart,
                    aeroportArrivee,
                    villeArrivee,
                    paysArrivee,
                    dateDepartDT,
                    dateArriveeDT,
                    duree,
                    prix,
                    places,
                    "Confirmé",
                    imageUrl
            );

            serviceVols.ajouterVol(vol);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le vol a été ajouté avec succès !");
            closeWindow();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez vérifier les champs saisis.\n" + e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) validerButton.getScene().getWindow();
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