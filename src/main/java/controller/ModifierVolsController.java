package controller;

import Services.serviceVols;
import entities.Vols;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.MyDatabase;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


public class ModifierVolsController {
    @FXML private ComboBox<String> compagnieComboBox;
    @FXML private ComboBox<String> numeroVolComboBox;
    @FXML private ComboBox<String> paysDepartComboBox;
    @FXML private ComboBox<String> paysArriveeComboBox;
    @FXML private ComboBox<String> villeDepartComboBox;
    @FXML private ComboBox<String> villeArriveeComboBox;
    @FXML private ComboBox<String> aeroportDepartComboBox;
    @FXML private ComboBox<String> aeroportArriveeComboBox;
    @FXML private DatePicker dateDepartPicker;
    @FXML private DatePicker dateArriveePicker;
    @FXML private TextField dureeField;
    @FXML private TextField prixField;
    @FXML private TextField placesField;
    @FXML private ImageView imagePreview;
    @FXML private Label imagePathLabel;
    @FXML private Button chooseImageButton;
    @FXML private Button validerButton;
    @FXML private Button annulerButton;

    private serviceVols serviceVols;
    private Stage stage;
    private Vols volAModifier;
    private String selectedImagePath;
    private Connection connection; // Your database connection

    // Maps for storing relationships between countries, cities, and airports
    private Map<String, List<String>> paysVillesMap;
    private Map<String, List<String>> villeAeroportsMap;
    private Map<String, List<String>> compagnieNumerosMap;

    @FXML
    public void initialize() {
        // Ne pas initialiser serviceVols ici
        initializeMaps();
        setupComboBoxes();
        setupComboBoxListeners();
        setupValidation();
        setupDateValidation();
    }

    // Ajoutez cette nouvelle méthode
    private void setupDateValidation() {
        // Définir les limites de dates
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusYears(1);

        // Restreindre la sélection de la date de départ
        dateDepartPicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(today) < 0 || date.compareTo(maxDate) > 0);
                if (date.compareTo(today) < 0 || date.compareTo(maxDate) > 0) {
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Restreindre la sélection de la date d'arrivée
        dateArriveePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate dateDepart = dateDepartPicker.getValue();
                boolean isBeforeDepartDate = dateDepart != null && date.compareTo(dateDepart) < 0;
                setDisable(empty || date.compareTo(today) < 0 || date.compareTo(maxDate) > 0 || isBeforeDepartDate);
                if (date.compareTo(today) < 0 || date.compareTo(maxDate) > 0 || isBeforeDepartDate) {
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // Mettre à jour la validation de la date d'arrivée quand la date de départ change
        dateDepartPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                dateArriveePicker.setValue(null);
                validateDates();
            }
        });
    }

    private void initializeMaps() {
        paysVillesMap = new HashMap<>();
        villeAeroportsMap = new HashMap<>();
        compagnieNumerosMap = new HashMap<>();

        // Pays et villes
        paysVillesMap.put("France", Arrays.asList("Paris", "Lyon", "Nice", "Marseille", "Toulouse", "Bordeaux"));
        paysVillesMap.put("Espagne", Arrays.asList("Madrid", "Barcelone", "Valence", "Séville", "Malaga", "Palma de Majorque"));
        paysVillesMap.put("Italie", Arrays.asList("Rome", "Milan", "Venise", "Florence", "Naples", "Turin"));
        paysVillesMap.put("Allemagne", Arrays.asList("Berlin", "Munich", "Francfort", "Hambourg", "Düsseldorf", "Stuttgart"));
        paysVillesMap.put("Royaume-Uni", Arrays.asList("Londres", "Manchester", "Birmingham", "Edinburgh", "Glasgow", "Bristol"));
        paysVillesMap.put("Portugal", Arrays.asList("Lisbonne", "Porto", "Faro", "Funchal", "Lagos"));
        paysVillesMap.put("Maroc", Arrays.asList("Casablanca", "Marrakech", "Rabat", "Fès", "Tanger", "Agadir"));
        paysVillesMap.put("Tunisie", Arrays.asList("Tunis", "Djerba", "Monastir", "Sfax", "Tozeur"));

        // Dans la méthode initializeMaps(), remplacez la partie villeAeroportsMap par ceci :
// France
        villeAeroportsMap.put("Paris", Arrays.asList(
                "Charles de Gaulle (CDG)",
                "Orly (ORY)",
                "Paris-Beauvais (BVA)"
        ));
        villeAeroportsMap.put("Lyon", Arrays.asList(
                "Saint-Exupéry (LYS)",
                "Lyon-Bron (LYN)"
        ));
        villeAeroportsMap.put("Nice", Arrays.asList(
                "Nice Côte d'Azur (NCE)",
                "Cannes-Mandelieu (CEQ)"
        ));
        villeAeroportsMap.put("Marseille", Arrays.asList(
                "Marseille Provence (MRS)",
                "Aéroport Marseille-Marignane (MRS)"
        ));
        villeAeroportsMap.put("Toulouse", Arrays.asList(
                "Toulouse-Blagnac (TLS)",
                "Toulouse-Francazal (LFBF)"
        ));
        villeAeroportsMap.put("Bordeaux", Arrays.asList(
                "Bordeaux-Mérignac (BOD)",
                "Bordeaux-Léognan (LFCB)"
        ));

// Espagne
        villeAeroportsMap.put("Madrid", Arrays.asList(
                "Adolfo Suárez Madrid-Barajas (MAD)",
                "Madrid-Cuatro Vientos (LECU)",
                "Madrid-Torrejón (TOJ)"
        ));
        villeAeroportsMap.put("Barcelone", Arrays.asList(
                "El Prat Josep Tarradellas (BCN)",
                "Sabadell (QSA)",
                "Girona-Costa Brava (GRO)"
        ));
        villeAeroportsMap.put("Valence", Arrays.asList(
                "Valencia Airport (VLC)",
                "Castellón-Costa Azahar (CDT)"
        ));
        villeAeroportsMap.put("Séville", Arrays.asList(
                "Sevilla Airport (SVQ)",
                "Jerez Airport (XRY)"
        ));
        villeAeroportsMap.put("Malaga", Arrays.asList(
                "Málaga-Costa del Sol (AGP)",
                "Granada (GRX)"
        ));

// Italie
        villeAeroportsMap.put("Rome", Arrays.asList(
                "Fiumicino-Leonardo da Vinci (FCO)",
                "Ciampino (CIA)",
                "Rome Urbe (LIRU)"
        ));
        villeAeroportsMap.put("Milan", Arrays.asList(
                "Malpensa (MXP)",
                "Linate (LIN)",
                "Bergamo Orio al Serio (BGY)"
        ));
        villeAeroportsMap.put("Venise", Arrays.asList(
                "Marco Polo (VCE)",
                "Treviso-Sant'Angelo (TSF)",
                "Venezia-Lido (LIPV)"
        ));

// Royaume-Uni
        villeAeroportsMap.put("Londres", Arrays.asList(
                "Heathrow (LHR)",
                "Gatwick (LGW)",
                "Stansted (STN)",
                "Luton (LTN)",
                "London City (LCY)"
        ));
        villeAeroportsMap.put("Manchester", Arrays.asList(
                "Manchester Airport (MAN)",
                "Liverpool John Lennon (LPL)",
                "Manchester City (EGCB)"
        ));
        villeAeroportsMap.put("Birmingham", Arrays.asList(
                "Birmingham Airport (BHX)",
                "East Midlands (EMA)"
        ));

// Allemagne
        villeAeroportsMap.put("Berlin", Arrays.asList(
                "Brandenburg Willy Brandt (BER)",
                "Berlin Schönefeld (SXF)"
        ));
        villeAeroportsMap.put("Munich", Arrays.asList(
                "Franz Josef Strauss (MUC)",
                "Augsburg (AGB)"
        ));
        villeAeroportsMap.put("Francfort", Arrays.asList(
                "Frankfurt am Main (FRA)",
                "Frankfurt-Hahn (HHN)",
                "Egelsbach (QEF)"
        ));

// Maroc
        villeAeroportsMap.put("Casablanca", Arrays.asList(
                "Mohammed V International (CMN)",
                "Casablanca-Anfa (CAS)",
                "Casablanca-Tit Mellil (GMMN)"
        ));
        villeAeroportsMap.put("Marrakech", Arrays.asList(
                "Marrakech Menara (RAK)",
                "Marrakech-Atlas (GMMX)"
        ));
        villeAeroportsMap.put("Rabat", Arrays.asList(
                "Rabat-Salé (RBA)",
                "Kénitra (NNA)"
        ));
        villeAeroportsMap.put("Fès", Arrays.asList(
                "Fès-Saïs (FEZ)",
                "Meknès-Bassatine (MEK)"
        ));
        villeAeroportsMap.put("Tanger", Arrays.asList(
                "Ibn Battouta (TNG)",
                "Tétouan-Sania R'mel (TTU)"
        ));

// Tunisie
        villeAeroportsMap.put("Tunis", Arrays.asList(
                "Carthage International (TUN)",
                "Tunis-Aouina (DTTA)"
        ));
        villeAeroportsMap.put("Djerba", Arrays.asList(
                "Djerba-Zarzis (DJE)",
                "Gabès-Matmata (GAE)"
        ));
        villeAeroportsMap.put("Monastir", Arrays.asList(
                "Habib Bourguiba International (MIR)",
                "Enfidha-Hammamet (NBE)"
        ));
        villeAeroportsMap.put("Sfax", Arrays.asList(
                "Sfax-Thyna (SFA)",
                "Gafsa (GAF)"
        ));

// Portugal
        villeAeroportsMap.put("Lisbonne", Arrays.asList(
                "Humberto Delgado (LIS)",
                "Montijo (LPMR)",
                "Cascais (CAT)"
        ));
        villeAeroportsMap.put("Porto", Arrays.asList(
                "Francisco Sá Carneiro (OPO)",
                "Braga (BGZ)"
        ));
        villeAeroportsMap.put("Faro", Arrays.asList(
                "Faro Airport (FAO)",
                "Portimão (PRM)"
        ));

        // Compagnies aériennes et numéros de vols
        compagnieNumerosMap.put("Air France", Arrays.asList(
                "AF1234", "AF5678", "AF9012", "AF3456", "AF7890"
        ));
        compagnieNumerosMap.put("Lufthansa", Arrays.asList(
                "LH1234", "LH5678", "LH9012", "LH3456", "LH7890"
        ));
        compagnieNumerosMap.put("British Airways", Arrays.asList(
                "BA1234", "BA5678", "BA9012", "BA3456", "BA7890"
        ));
        compagnieNumerosMap.put("Iberia", Arrays.asList(
                "IB1234", "IB5678", "IB9012", "IB3456", "IB7890"
        ));
        compagnieNumerosMap.put("Alitalia", Arrays.asList(
                "AZ1234", "AZ5678", "AZ9012", "AZ3456", "AZ7890"
        ));
        compagnieNumerosMap.put("Royal Air Maroc", Arrays.asList(
                "AT1234", "AT5678", "AT9012", "AT3456", "AT7890"
        ));
        compagnieNumerosMap.put("Tunisair", Arrays.asList(
                "TU1234", "TU5678", "TU9012", "TU3456", "TU7890"
        ));
        compagnieNumerosMap.put("TAP Air Portugal", Arrays.asList(
                "TP1234", "TP5678", "TP9012", "TP3456", "TP7890"
        ));
    }

    private void setupComboBoxes() {
        compagnieComboBox.setItems(FXCollections.observableArrayList(compagnieNumerosMap.keySet()));
        paysDepartComboBox.setItems(FXCollections.observableArrayList(paysVillesMap.keySet()));
        paysArriveeComboBox.setItems(FXCollections.observableArrayList(paysVillesMap.keySet()));
    }


    private void setupComboBoxListeners() {
        compagnieComboBox.setOnAction(e -> {
            String compagnie = compagnieComboBox.getValue();
            if (compagnie != null) {
                List<String> numeros = compagnieNumerosMap.get(compagnie);
                numeroVolComboBox.setItems(FXCollections.observableArrayList(numeros));
            }
        });

        paysDepartComboBox.setOnAction(e -> {
            String pays = paysDepartComboBox.getValue();
            if (pays != null) {
                List<String> villes = paysVillesMap.get(pays);
                villeDepartComboBox.setItems(FXCollections.observableArrayList(villes));
                villeDepartComboBox.setValue(null);
                aeroportDepartComboBox.getItems().clear();
                validatePaysDifferents();
            }
        });

        villeDepartComboBox.setOnAction(e -> {
            String ville = villeDepartComboBox.getValue();
            if (ville != null) {
                List<String> aeroports = villeAeroportsMap.get(ville);
                aeroportDepartComboBox.setItems(FXCollections.observableArrayList(aeroports));
            }
        });

        paysArriveeComboBox.setOnAction(e -> {
            String pays = paysArriveeComboBox.getValue();
            if (pays != null) {
                List<String> villes = paysVillesMap.get(pays);
                villeArriveeComboBox.setItems(FXCollections.observableArrayList(villes));
                villeArriveeComboBox.setValue(null);
                aeroportArriveeComboBox.getItems().clear();
                validatePaysDifferents();
            }
        });

        villeArriveeComboBox.setOnAction(e -> {
            String ville = villeArriveeComboBox.getValue();
            if (ville != null) {
                List<String> aeroports = villeAeroportsMap.get(ville);
                aeroportArriveeComboBox.setItems(FXCollections.observableArrayList(aeroports));
            }
        });

        dateDepartPicker.setOnAction(e -> validateDates());
        dateArriveePicker.setOnAction(e -> validateDates());
    }

    private void setupValidation() {
        dureeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                dureeField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });

        prixField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                prixField.setText(oldVal);
            }
        });

        placesField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                placesField.setText(newVal.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void validatePaysDifferents() {
        String paysDepart = paysDepartComboBox.getValue();
        String paysArrivee = paysArriveeComboBox.getValue();

        if (paysDepart != null && paysArrivee != null && paysDepart.equals(paysArrivee)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation");
            alert.setHeaderText("Pays identiques");
            alert.setContentText("Le pays de départ et d'arrivée doivent être différents");
            alert.showAndWait();
            paysArriveeComboBox.setValue(null);
        }
    }

    private void validateDates() {
        LocalDate dateDepart = dateDepartPicker.getValue();
        LocalDate dateArrivee = dateArriveePicker.getValue();
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusYears(1);

        StringBuilder errorMessage = new StringBuilder();

        if (dateDepart != null && dateArrivee != null) {
            // Vérifier uniquement si les deux dates sont sélectionnées
            if (dateArrivee.isBefore(dateDepart)) {
                errorMessage.append("La date d'arrivée doit être égale ou après la date de départ\n");
                dateArriveePicker.setValue(dateDepart); // Met la date d'arrivée égale à la date de départ au lieu de la vider
            }
        }

        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation des dates");
            alert.setHeaderText("Erreur de dates");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        // Ajouter une option pour entrer une URL
        TextInputDialog urlDialog = new TextInputDialog();
        urlDialog.setTitle("Image URL");
        urlDialog.setHeaderText("Entrer une URL d'image");
        urlDialog.setContentText("URL:");

        // Créer des boutons pour choisir entre fichier local et URL
        Alert choiceAlert = new Alert(Alert.AlertType.CONFIRMATION);
        choiceAlert.setTitle("Source de l'image");
        choiceAlert.setHeaderText("Choisir la source de l'image");
        choiceAlert.setContentText("D'où voulez-vous charger l'image?");

        ButtonType buttonTypeFile = new ButtonType("Fichier local");
        ButtonType buttonTypeURL = new ButtonType("URL");
        ButtonType buttonTypeCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);

        choiceAlert.getButtonTypes().setAll(buttonTypeFile, buttonTypeURL, buttonTypeCancel);

        Optional<ButtonType> result = choiceAlert.showAndWait();
        if (result.get() == buttonTypeFile) {
            // Choisir un fichier local
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                selectedImagePath = selectedFile.toURI().toString(); // Utiliser l'URI pour compatibilité
                imagePathLabel.setText(selectedFile.getName());
                Image image = new Image(selectedImagePath);
                imagePreview.setImage(image);
            }
        } else if (result.get() == buttonTypeURL) {
            // Entrer une URL
            Optional<String> urlResult = urlDialog.showAndWait();
            if (urlResult.isPresent() && !urlResult.get().isEmpty()) {
                String url = urlResult.get();
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url; // Ajouter le protocole si manquant
                }
                selectedImagePath = url;
                imagePathLabel.setText(url);
                try {
                    Image image = new Image(url, true); // true pour le chargement en arrière-plan
                    imagePreview.setImage(image);
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Impossible de charger l'image");
                    alert.setContentText("L'URL fournie n'est pas valide ou l'image n'est pas accessible.");
                    alert.showAndWait();
                }
            }
        }
    }

    @FXML
    private void handleValider() {
        if (connection == null || serviceVols == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de connexion");
            alert.setContentText("La connexion à la base de données n'est pas initialisée.");
            alert.showAndWait();
            return;
        }

        try {
            if (validateFields()) {
                updateVol();
                serviceVols.modifierVol(volAModifier);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Modification réussie");
                alert.setContentText("Le vol a été modifié avec succès.");
                alert.showAndWait();

                closeWindow();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de la modification");
            alert.setContentText("Une erreur est survenue : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private boolean validateFields() {
        StringBuilder errorMessage = new StringBuilder();

        if (compagnieComboBox.getValue() == null) errorMessage.append("Sélectionnez une compagnie\n");
        if (numeroVolComboBox.getValue() == null) errorMessage.append("Sélectionnez un numéro de vol\n");
        if (paysDepartComboBox.getValue() == null) errorMessage.append("Sélectionnez un pays de départ\n");
        if (paysArriveeComboBox.getValue() == null) errorMessage.append("Sélectionnez un pays d'arrivée\n");
        if (villeDepartComboBox.getValue() == null) errorMessage.append("Sélectionnez une ville de départ\n");
        if (villeArriveeComboBox.getValue() == null) errorMessage.append("Sélectionnez une ville d'arrivée\n");
        if (aeroportDepartComboBox.getValue() == null) errorMessage.append("Sélectionnez un aéroport de départ\n");
        if (aeroportArriveeComboBox.getValue() == null) errorMessage.append("Sélectionnez un aéroport d'arrivée\n");
        if (dateDepartPicker.getValue() == null) errorMessage.append("Sélectionnez une date de départ\n");
        if (dateArriveePicker.getValue() == null) errorMessage.append("Sélectionnez une date d'arrivée\n");

        // Vérification de la durée
        try {
            int duree = Integer.parseInt(dureeField.getText());
            if (duree <= 0) errorMessage.append("La durée doit être supérieure à 0\n");
        } catch (NumberFormatException e) {
            errorMessage.append("La durée doit être un nombre valide\n");
        }

        // Vérification du prix
        try {
            double prix = Double.parseDouble(prixField.getText());
            if (prix <= 0) errorMessage.append("Le prix doit être supérieur à 0\n");
        } catch (NumberFormatException e) {
            errorMessage.append("Le prix doit être un nombre valide\n");
        }

        // Vérification des places
        try {
            int places = Integer.parseInt(placesField.getText());
            if (places <= 0) errorMessage.append("Le nombre de places doit être supérieur à 0\n");
        } catch (NumberFormatException e) {
            errorMessage.append("Le nombre de places doit être un nombre valide\n");
        }

        // Vérification des dates
        LocalDate dateDepart = dateDepartPicker.getValue();
        LocalDate dateArrivee = dateArriveePicker.getValue();
        if (dateDepart != null && dateArrivee != null && dateArrivee.isBefore(dateDepart)) {
            errorMessage.append("La date d'arrivée doit être égale ou après la date de départ\n");
        }

        if (errorMessage.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de validation");
            alert.setHeaderText("Veuillez corriger les erreurs suivantes :");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    private void updateVol() {
        try {
            if (volAModifier == null) {
                throw new Exception("Aucun vol à modifier n'a été sélectionné");
            }

            // Vérification des valeurs nulles
            if (compagnieComboBox.getValue() == null || numeroVolComboBox.getValue() == null ||
                    paysDepartComboBox.getValue() == null || paysArriveeComboBox.getValue() == null ||
                    villeDepartComboBox.getValue() == null || villeArriveeComboBox.getValue() == null ||
                    aeroportDepartComboBox.getValue() == null || aeroportArriveeComboBox.getValue() == null ||
                    dateDepartPicker.getValue() == null || dateArriveePicker.getValue() == null ||
                    dureeField.getText().isEmpty() || prixField.getText().isEmpty() ||
                    placesField.getText().isEmpty()) {

                throw new Exception("Tous les champs doivent être remplis");
            }

            volAModifier.setCompagnie(compagnieComboBox.getValue());
            volAModifier.setNumeroVol(numeroVolComboBox.getValue());
            volAModifier.setPaysDepart(paysDepartComboBox.getValue());
            volAModifier.setPaysArrivee(paysArriveeComboBox.getValue());
            volAModifier.setVilleDepart(villeDepartComboBox.getValue());
            volAModifier.setVilleArrivee(villeArriveeComboBox.getValue());
            volAModifier.setAeroportDepart(aeroportDepartComboBox.getValue());
            volAModifier.setAeroportArrivee(aeroportArriveeComboBox.getValue());

            // Gestion des dates
            LocalDate dateDepart = dateDepartPicker.getValue();
            LocalDate dateArrivee = dateArriveePicker.getValue();
            LocalTime heureDepart = LocalTime.of(12, 0); // Midi par défaut

            LocalDateTime dateTimeDepart = LocalDateTime.of(dateDepart, heureDepart);
            LocalDateTime dateTimeArrivee = dateTimeDepart.plusMinutes(Integer.parseInt(dureeField.getText()));

            volAModifier.setDateDepart(dateTimeDepart);
            volAModifier.setDateArrivee(dateTimeArrivee);

            // Conversion et validation des valeurs numériques
            try {
                int duree = Integer.parseInt(dureeField.getText());
                if (duree <= 0) throw new Exception("La durée doit être supérieure à 0");
                volAModifier.setDuree(duree);
            } catch (NumberFormatException e) {
                throw new Exception("La durée doit être un nombre valide");
            }

            try {
                double prix = Double.parseDouble(prixField.getText());
                if (prix <= 0) throw new Exception("Le prix doit être supérieur à 0");
                volAModifier.setPrixStandard(prix);
            } catch (NumberFormatException e) {
                throw new Exception("Le prix doit être un nombre valide");
            }

            try {
                int places = Integer.parseInt(placesField.getText());
                if (places <= 0) throw new Exception("Le nombre de places doit être supérieur à 0");
                volAModifier.setPlacesDisponibles(places);
            } catch (NumberFormatException e) {
                throw new Exception("Le nombre de places doit être un nombre valide");
            }

            if (selectedImagePath != null) {
                volAModifier.setImagePath(selectedImagePath);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la mise à jour du vol : " + e.getMessage());
        }
    }

    public void setVol(Vols vol) {
        if (vol == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur d'initialisation");
            alert.setContentText("Aucun vol n'a été sélectionné pour modification");
            alert.showAndWait();
            return;
        }
        this.volAModifier = vol;
        populateFields();
    }

    private void populateFields() {
        if (volAModifier != null) {
            try {
                // Remplir les ComboBox
                compagnieComboBox.setValue(volAModifier.getCompagnie());
                numeroVolComboBox.setValue(volAModifier.getNumeroVol());
                paysDepartComboBox.setValue(volAModifier.getPaysDepart());
                paysArriveeComboBox.setValue(volAModifier.getPaysArrivee());
                villeDepartComboBox.setValue(volAModifier.getVilleDepart());
                villeArriveeComboBox.setValue(volAModifier.getVilleArrivee());
                aeroportDepartComboBox.setValue(volAModifier.getAeroportDepart());
                aeroportArriveeComboBox.setValue(volAModifier.getAeroportArrivee());

                // Remplir les DatePicker
                if (volAModifier.getDateDepart() != null) {
                    dateDepartPicker.setValue(volAModifier.getDateDepart().toLocalDate());
                }
                if (volAModifier.getDateArrivee() != null) {
                    dateArriveePicker.setValue(volAModifier.getDateArrivee().toLocalDate());
                }

                // Remplir les champs texte
                dureeField.setText(String.valueOf(volAModifier.getDuree()));
                prixField.setText(String.valueOf(volAModifier.getPrixStandard()));
                placesField.setText(String.valueOf(volAModifier.getPlacesDisponibles()));

                // Gérer l'image
                if (volAModifier.getImagePath() != null && !volAModifier.getImagePath().isEmpty()) {
                    selectedImagePath = volAModifier.getImagePath();
                    imagePathLabel.setText(selectedImagePath);

                    try {
                        // Vérifier si c'est une URL ou un chemin de fichier
                        if (selectedImagePath.startsWith("http://") || selectedImagePath.startsWith("https://")) {
                            // C'est une URL
                            Image image = new Image(selectedImagePath, true); // true pour le chargement en arrière-plan
                            imagePreview.setImage(image);
                        } else if (selectedImagePath.startsWith("file:")) {
                            // C'est déjà une URI de fichier
                            Image image = new Image(selectedImagePath);
                            imagePreview.setImage(image);
                        } else {
                            // C'est un chemin de fichier normal
                            File imageFile = new File(selectedImagePath);
                            if (imageFile.exists()) {
                                Image image = new Image(imageFile.toURI().toString());
                                imagePreview.setImage(image);
                            } else {
                                // Essayer comme ressource
                                URL imageUrl = getClass().getResource("/images/" + selectedImagePath);
                                if (imageUrl != null) {
                                    Image image = new Image(imageUrl.toExternalForm());
                                    imagePreview.setImage(image);
                                } else {
                                    imagePreview.setImage(null);
                                    imagePathLabel.setText("Image non trouvée: " + selectedImagePath);
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur de chargement de l'image: " + e.getMessage());
                        e.printStackTrace();
                        imagePreview.setImage(null);
                        imagePathLabel.setText("Erreur: " + selectedImagePath);
                    }
                }

                // Mettre à jour les listes dépendantes
                if (volAModifier.getCompagnie() != null) {
                    List<String> numeros = compagnieNumerosMap.get(volAModifier.getCompagnie());
                    if (numeros != null) {
                        numeroVolComboBox.setItems(FXCollections.observableArrayList(numeros));
                    }
                }

                if (volAModifier.getPaysDepart() != null) {
                    List<String> villesDepart = paysVillesMap.get(volAModifier.getPaysDepart());
                    if (villesDepart != null) {
                        villeDepartComboBox.setItems(FXCollections.observableArrayList(villesDepart));
                    }
                }

                if (volAModifier.getPaysArrivee() != null) {
                    List<String> villesArrivee = paysVillesMap.get(volAModifier.getPaysArrivee());
                    if (villesArrivee != null) {
                        villeArriveeComboBox.setItems(FXCollections.observableArrayList(villesArrivee));
                    }
                }

                if (volAModifier.getVilleDepart() != null) {
                    List<String> aeroportsDepart = villeAeroportsMap.get(volAModifier.getVilleDepart());
                    if (aeroportsDepart != null) {
                        aeroportDepartComboBox.setItems(FXCollections.observableArrayList(aeroportsDepart));
                    }
                }

                if (volAModifier.getVilleArrivee() != null) {
                    List<String> aeroportsArrivee = villeAeroportsMap.get(volAModifier.getVilleArrivee());
                    if (aeroportsArrivee != null) {
                        aeroportArriveeComboBox.setItems(FXCollections.observableArrayList(aeroportsArrivee));
                    }
                }

            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur lors du remplissage des champs");
                alert.setContentText("Une erreur est survenue lors du chargement des données du vol : " + e.getMessage());
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) annulerButton.getScene().getWindow();
        stage.close();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setConnection(Connection connection) {
        try {
            // Si la connexion est null, essayer d'en obtenir une nouvelle
            if (connection == null) {
                MyDatabase db = MyDatabase.getInstance();
                connection = db.getCnx();
            }

            // Vérifier si la connexion est valide
            if (connection == null || connection.isClosed()) {
                throw new SQLException("La connexion n'est pas valide");
            }

            this.connection = connection;
            this.serviceVols = new serviceVols(this.connection);
            System.out.println("Connexion établie dans ModifierVolsController");

        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de connexion");
            alert.setHeaderText("La connexion à la base de données a échoué");
            alert.setContentText("Détails : " + e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }
}