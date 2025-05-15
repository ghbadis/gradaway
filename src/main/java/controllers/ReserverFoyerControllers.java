package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.layout.Region;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;
import Services.ServiceReservationFoyer;
import Services.ServiceFoyer;
import entities.ReservationFoyer;
import entities.Foyer;
import utils.EmailSender;
import utils.MyDatabase;
import utils.QRCodeGenerator;
import utils.SessionManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import com.google.zxing.WriterException;
import java.io.IOException;
import java.nio.file.Paths;

public class ReserverFoyerControllers {

    @FXML private TextField tf_gmail;
    @FXML private DatePicker dp_date_debut;
    @FXML private DatePicker dp_date_fin;
    @FXML private DatePicker dp_date_reserver;
    @FXML private ComboBox<Foyer> cb_foyer;
    
    // Navigation buttons
    @FXML private Button accueilButton;
    @FXML private Button userButton;
    @FXML private Button dossierButton;
    @FXML private Button universiteButton;
    @FXML private Button entretienButton;
    @FXML private Button evenementButton;
    @FXML private Button hebergementButton;
    @FXML private Button restaurantButton;
    @FXML private Button volsButton;
    @FXML private Button logoutButton;

    private ServiceFoyer serviceFoyer = new ServiceFoyer();

    // Variable pour stocker le foyer sélectionné
    private Foyer selectedFoyer;
    
    /**
     * Méthode appelée par ListFoyerClientControllers pour définir le foyer sélectionné
     * @param foyer Le foyer sélectionné dans l'interface ListFoyerClient
     */
    public void setSelectedFoyer(Foyer foyer) {
        this.selectedFoyer = foyer;
        
        // Si l'interface est déjà initialisée, mettre à jour le ComboBox
        if (cb_foyer != null && foyer != null) {
            // Chercher le foyer correspondant dans la liste du ComboBox
            for (Foyer f : cb_foyer.getItems()) {
                if (f.getIdFoyer() == foyer.getIdFoyer()) {
                    cb_foyer.setValue(f);
                    break;
                }
            }
        }
    }

    @FXML
    public void initialize() {
        setupStyles();
        setupDatePickers();
        loadFoyers();
        setupNavigationButtons();
        
        try {
        // Récupérer l'email de l'utilisateur connecté depuis le SessionManager
        String userEmail = SessionManager.getInstance().getUserEmail();
        if (userEmail != null && !userEmail.isEmpty()) {
            // Afficher l'email dans le champ et le rendre non modifiable
            tf_gmail.setText(userEmail);
            tf_gmail.setEditable(false);
            tf_gmail.setDisable(true);
                tf_gmail.setStyle("-fx-background-color: #e9ecef; -fx-opacity: 0.8; -fx-text-fill: #666666;");
            System.out.println("ReserverFoyerControllers: Email utilisateur récupéré depuis SessionManager: " + userEmail);
        } else {
            System.out.println("ReserverFoyerControllers: Aucun email utilisateur trouvé dans SessionManager");
                showAlert("Erreur", "Vous devez être connecté pour faire une réservation", Alert.AlertType.ERROR);
                // Rediriger vers la page de connexion
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) tf_gmail.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.setTitle("Login - GradAway");
                    stage.centerOnScreen();
                } catch (IOException e) {
                    System.err.println("Erreur lors de la redirection vers la page de connexion: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'email utilisateur: " + e.getMessage());
            showAlert("Erreur", "Une erreur est survenue lors de la récupération de vos informations", Alert.AlertType.ERROR);
        }
    }
    
    private void loadFoyers() {
        try {
            // Récupérer tous les foyers disponibles
            List<Foyer> foyers = serviceFoyer.recuperer();
            ObservableList<Foyer> foyerList = FXCollections.observableArrayList(foyers);
            cb_foyer.setItems(foyerList);
            
            // Définir comment afficher les foyers dans le ComboBox
            cb_foyer.setCellFactory(param -> new ListCell<Foyer>() {
                @Override
                protected void updateItem(Foyer foyer, boolean empty) {
                    super.updateItem(foyer, empty);
                    if (empty || foyer == null) {
                        setText(null);
                    } else {
                        setText(foyer.getNom() + " (" + foyer.getVille() + ")");
                    }
                }
            });
            
            // Définir comment afficher le foyer sélectionné
            cb_foyer.setButtonCell(new ListCell<Foyer>() {
                @Override
                protected void updateItem(Foyer foyer, boolean empty) {
                    super.updateItem(foyer, empty);
                    if (empty || foyer == null) {
                        setText(null);
                    } else {
                        setText(foyer.getNom() + " (" + foyer.getVille() + ")");
                    }
                }
            });
            
            // Si un foyer a été présélectionné, l'utiliser
            if (selectedFoyer != null) {
                // Chercher le foyer correspondant dans la liste
                for (Foyer f : foyerList) {
                    if (f.getIdFoyer() == selectedFoyer.getIdFoyer()) {
                        cb_foyer.setValue(f);
                        break;
                    }
                }
            } 
            // Sinon, sélectionner le premier foyer par défaut s'il y en a
            else if (!foyerList.isEmpty()) {
                cb_foyer.setValue(foyerList.get(0));
            }
            
            System.out.println("Foyers chargés: " + foyers.size());
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la liste des foyers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupStyles() {
        String textFieldStyle = "-fx-background-color: #f8f9fa; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 8;";

        tf_gmail.setStyle(textFieldStyle);

        String datePickerStyle = "-fx-background-color: #f8f9fa; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 5; " +
                "-fx-background-radius: 5;";

        dp_date_debut.setStyle(datePickerStyle);
        dp_date_fin.setStyle(datePickerStyle);
        dp_date_reserver.setStyle(datePickerStyle);

        tf_gmail.setPrefWidth(250);
        dp_date_debut.setPrefWidth(250);
        dp_date_fin.setPrefWidth(250);
        dp_date_reserver.setPrefWidth(250);

        String focusStyle = "-fx-border-color: #2196F3; -fx-border-width: 2px;";
        tf_gmail.focusedProperty().addListener((obs, oldVal, newVal) -> {
            tf_gmail.setStyle(newVal ? textFieldStyle + focusStyle : textFieldStyle);
        });
    }

    private void setupDatePickers() {
        java.time.LocalDate today = java.time.LocalDate.now();
        dp_date_debut.setValue(today);
        dp_date_fin.setValue(today);
        dp_date_reserver.setValue(today);

        // Configuration du DatePicker de date de début pour désactiver les dates passées
        dp_date_debut.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.compareTo(today) < 0);
            }
        });

        // Configuration du DatePicker de date de fin pour désactiver les dates antérieures à la date de début
        updateDateFinCellFactory();

        // Mettre à jour les contraintes de date de fin lorsque la date de début change
        dp_date_debut.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Mettre à jour la factory du DatePicker de date de fin
            updateDateFinCellFactory();
            
            // Si la date de fin est antérieure à la nouvelle date de début, ajuster la date de fin
            if (dp_date_fin.getValue() != null && dp_date_fin.getValue().compareTo(newVal) < 0) {
                dp_date_fin.setValue(newVal);
            }
        });
    }
    
    /**
     * Met à jour la factory du DatePicker de date de fin pour désactiver les dates antérieures à la date de début
     */
    private void updateDateFinCellFactory() {
        dp_date_fin.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(java.time.LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                java.time.LocalDate dateDebut = dp_date_debut.getValue();
                if (dateDebut != null) {
                    // Désactiver les dates antérieures à la date de début
                    setDisable(empty || date.compareTo(dateDebut) < 0);
                    
                    // Ajouter un style visuel pour indiquer les dates désactivées
                    if (date.compareTo(dateDebut) < 0) {
                        setStyle("-fx-background-color: #f8d7da;");
                    }
                }
            }
        });
    }

    private void setupNavigationButtons() {
        accueilButton.setOnAction(this::onAccueilButtonClick);
        userButton.setOnAction(this::onProfileButtonClick);
        dossierButton.setOnAction(this::ondossierButtonClick);
        universiteButton.setOnAction(this::onuniversiteButtonClick);
        entretienButton.setOnAction(this::onentretienButtonClick);
        evenementButton.setOnAction(this::onevenementButtonClick);
        hebergementButton.setOnAction(this::onhebergementButtonClick);
        restaurantButton.setOnAction(this::onrestaurantButtonClick);
        volsButton.setOnAction(this::onvolsButtonClick);
        logoutButton.setOnAction(this::onlogoutButtonClick);
    }

    @FXML
    private void onAccueilButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/acceuil.fxml"));
            Parent root = loader.load();
            navigateToScene(root, event);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la navigation vers l'accueil: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onProfileButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EditProfile.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Modifier Mon Profil");
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture du profil: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void ondossierButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutDossier.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion du Dossier");
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture du dossier: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onuniversiteButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminconditature.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Candidatures");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des candidatures: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onentretienButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DemanderEntretien.fxml"));
            Parent root = loader.load();
            navigateToScene(root, event);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture de l'entretien: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onevenementButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/affiche_evenement.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Événements");
            stage.setMinWidth(1133);
            stage.setMinHeight(691);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des événements: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onhebergementButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            Parent root = loader.load();
            navigateToScene(root, event);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des foyers: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onrestaurantButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListRestaurantClient.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Restaurants");
            stage.setMinWidth(1200);
            stage.setMinHeight(800);
            stage.setResizable(true);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture des restaurants: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onvolsButtonClick(ActionEvent event) {
        // Implement when needed
    }

    @FXML
    private void onlogoutButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            Scene scene = new Scene(root);
            loginStage.setScene(scene);
            loginStage.setTitle("Login - GradAway");
            loginStage.setResizable(true);
            loginStage.centerOnScreen();
            loginStage.show();
            
            // Close current window
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de la déconnexion: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void navigateToScene(Parent root, ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
        // Add fade transition
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    private void confirme() {
        if (!validateFields()) {
            return;
        }

        try {
            // Récupérer l'email
            String email = tf_gmail.getText().trim();
            if (!isValidEmail(email)) {
                showAlert("Erreur", "Veuillez fournir une adresse email valide.", Alert.AlertType.ERROR);
                return;
            }
            
            // Vérifier si un foyer est sélectionné
            Foyer selectedFoyer = cb_foyer.getValue();
            if (selectedFoyer == null) {
                showAlert("Erreur", "Veuillez sélectionner un foyer.", Alert.AlertType.ERROR);
                return;
            }
            
            // Créer une instance du service de réservation
            ServiceReservationFoyer serviceReservation = new ServiceReservationFoyer();
            
            // Rechercher un utilisateur existant dans la base de données
            int idEtudiant = SessionManager.getInstance().getUserId();
            System.out.println("ID utilisé pour la réservation : " + idEtudiant);
            if (idEtudiant == -1) {
                showAlert("Erreur", "Aucun utilisateur connecté. Veuillez d'abord vous connecter.", Alert.AlertType.ERROR);
                return;
            }
            
            // Vérifier si l'étudiant existe
            if (!serviceReservation.studentExists(idEtudiant)) {
                showAlert("Erreur", "L'ID étudiant " + idEtudiant + " n'existe pas dans la base de données.", Alert.AlertType.ERROR);
                return;
            }
            
            // Créer une nouvelle réservation
            ReservationFoyer reservation = new ReservationFoyer();
            reservation.setIdEtudiant(idEtudiant);
            
            // Utiliser l'ID du foyer sélectionné
            reservation.setFoyerId(selectedFoyer.getIdFoyer());
            
            // Définir les dates
            reservation.setDateDebut(dp_date_debut.getValue());
            reservation.setDateFin(dp_date_fin.getValue());
            reservation.setDateReservation(dp_date_reserver.getValue());
            
            // Ajouter la réservation à la base de données
            serviceReservation.ajouter(reservation);
            
            // Envoyer un email de confirmation avec code QR
            sendConfirmationEmail(email, selectedFoyer, reservation);
            
            // Sauvegarder le code QR dans un fichier (uniquement pour référence, ne pas l'afficher)
            try {
                String userHome = System.getProperty("user.home");
                String qrFilePath = Paths.get(userHome, "Downloads", "reservation_" + idEtudiant + ".png").toString();
                
                // Afficher un message de confirmation avec option pour voir les réservations
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Réservation confirmée");
                alert.setHeaderText(null);
                alert.setContentText(
                    "Réservation confirmée avec succès!\n\n" +
                    "Votre numéro de réservation est associé à l'ID étudiant : " + idEtudiant + "\n" +
                    "Veuillez conserver ce numéro pour toute référence future.\n\n" +
                    "Un email de confirmation avec code QR a été envoyé à " + email
                );
                
                // Ajouter des boutons personnalisés
                ButtonType voirReservationsButton = new ButtonType("Voir mes réservations");
                ButtonType retourButton = new ButtonType("Retour à la liste");
                
                alert.getButtonTypes().setAll(voirReservationsButton, retourButton);
                
                // Réinitialiser les champs après la réservation
                resetFields();
                
                // Gérer la fermeture de la boîte de dialogue (croix rouge)
                Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
                stage.setOnCloseRequest(windowEvent -> {
                    // Retourner à la liste des foyers quand on clique sur la croix
                    navigateToListFoyerClient();
                });
                
                // Attendre la réponse de l'utilisateur
                final int finalIdEtudiant = idEtudiant; // Créer une copie finale pour le lambda
                alert.showAndWait().ifPresent(buttonType -> {
                    if (buttonType == voirReservationsButton) {
                        // Naviguer vers la page des réservations
                        naviguerVersMesReservations(finalIdEtudiant);
                    } else {
                        // Retourner à la liste des foyers
                        navigateToListFoyerClient();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                // En cas d'erreur, afficher un message normal
                showAlert("Succès", "Réservation confirmée avec succès!\n\nVotre numéro de réservation est associé à l'ID étudiant : " + idEtudiant + "\nVeuillez conserver ce numéro pour toute référence future.\n\nUn email de confirmation a été envoyé à " + email, Alert.AlertType.INFORMATION);
                
                // Retourner à la liste des foyers
                navigateToListFoyerClient();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la réservation: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    // Méthode pour réinitialiser les champs après une réservation réussie
    private void resetFields() {
        tf_gmail.clear();
        
        // Réinitialiser les dates à aujourd'hui
        java.time.LocalDate today = java.time.LocalDate.now();
        dp_date_debut.setValue(today);
        dp_date_fin.setValue(today);
        dp_date_reserver.setValue(today);
    }

    @FXML
    private void annuler() {
        navigateToListFoyerClient();
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (tf_gmail.getText().trim().isEmpty() || !isValidEmail(tf_gmail.getText())) {
            errors.append("Email invalide\n");
        }

        if (dp_date_debut.getValue() == null) {
            errors.append("La date de début est requise\n");
        }

        if (dp_date_fin.getValue() == null) {
            errors.append("La date de fin est requise\n");
        }

        if (dp_date_reserver.getValue() == null) {
            errors.append("La date de réservation est requise\n");
        }
        
        if (cb_foyer.getValue() == null) {
            errors.append("Veuillez sélectionner un foyer\n");
        }

        if (dp_date_debut.getValue() != null && dp_date_fin.getValue() != null &&
                dp_date_fin.getValue().isBefore(dp_date_debut.getValue())) {
            errors.append("La date de fin doit être après la date de début\n");
        }

        if (errors.length() > 0) {
            showAlert("Erreur de validation", errors.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(emailRegex);
    }
    
    /**
     * Récupère un ID d'utilisateur valide depuis la base de données
     * @return ID utilisateur existant ou -1 si aucun utilisateur n'est trouvé
     */
    private int getUserIdFromDatabase() {
        try {
            Connection con = MyDatabase.getInstance().getCnx();
            String query = "SELECT id FROM user LIMIT 1";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                return -1; // Aucun utilisateur trouvé
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void navigateToListFoyer() {
        try {
            // Assurez-vous que le chemin est correct et commence par un slash
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyer.fxml"));
            if (loader.getLocation() == null) {
                // Si le fichier n'est pas trouvé, essayez un autre chemin
                loader = new FXMLLoader(getClass().getResource("/main/resources/ListFoyer.fxml"));

                // Si toujours null, essayez sans le slash
                if (loader.getLocation() == null) {
                    loader = new FXMLLoader(getClass().getResource("ListFoyer.fxml"));
                }
            }
            
            if (loader.getLocation() == null) {
                throw new IOException("Impossible de trouver le fichier FXML ListFoyer.fxml");
            }
            
            Parent root = loader.load();
            Stage stage = (Stage) tf_gmail.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Ajouter une transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
        } catch (IOException e) {
            e.printStackTrace();
            // En cas d'erreur, essayez de naviguer vers ListFoyer.fxml
            navigateToListFoyer();
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
    
    /**
     * Navigue vers la liste des foyers pour les clients
     */
    private void navigateToListFoyerClient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListFoyerClient.fxml"));
            if (loader.getLocation() == null) {
                // Si le fichier n'est pas trouvé, essayez un autre chemin
                loader = new FXMLLoader(getClass().getResource("/main/resources/ListFoyerClient.fxml"));
                
                // Si toujours null, essayez sans le slash
                if (loader.getLocation() == null) {
                    loader = new FXMLLoader(getClass().getResource("ListFoyerClient.fxml"));
                }
            }
            
            if (loader.getLocation() == null) {
                throw new IOException("Impossible de trouver le fichier FXML ListFoyerClient.fxml");
            }
            
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) tf_gmail.getScene().getWindow();
            stage.setScene(scene);
            
            // Ajouter une transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement de la vue: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    /**
     * Navigue vers la page "Mes Réservations de Foyer" avec l'ID de l'étudiant
     * @param idEtudiant ID de l'étudiant
     */
    private void naviguerVersMesReservations(int idEtudiant) {
        try {
            // Charger la vue des réservations
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MesReservationsFoyer.fxml"));
            Parent root = loader.load();
            
            // Récupérer le contrôleur et définir l'ID de l'utilisateur
            MesReservationsFoyerController controller = loader.getController();
            controller.setUserId(idEtudiant);
            
            // Afficher la vue
            Scene scene = new Scene(root);
            Stage stage = (Stage) tf_gmail.getScene().getWindow();
            
            // Transition de fondu
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            
            stage.setScene(scene);
            fadeIn.play();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du chargement de la page des réservations: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    /**
     * Envoie un email de confirmation de réservation
     * 
     * @param email Adresse email du destinataire
     * @param foyer Foyer réservé
     * @param reservation Détails de la réservation
     */
    private void sendConfirmationEmail(String email, Foyer foyer, ReservationFoyer reservation) {
        try {
            // Formater les dates pour l'affichage
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String dateDebut = reservation.getDateDebut().format(formatter);
            String dateFin = reservation.getDateFin().format(formatter);
            String dateReservation = reservation.getDateReservation().format(formatter);
            
            // Générer le contenu HTML de l'email
            String emailContent = EmailSender.generateReservationConfirmationEmail(
                foyer.getNom(), 
                dateDebut, 
                dateFin, 
                dateReservation,
                reservation.getIdEtudiant(),
                foyer.getVille()
            );
            
            // Générer le contenu du code QR
            String qrContent = EmailSender.generateQRContent(
                foyer.getNom(),
                dateDebut,
                dateFin,
                reservation.getIdEtudiant(),
                foyer.getVille()
            );
            
            // Nom du fichier QR code
            String qrFileName = "reservation_" + reservation.getIdEtudiant() + ".png";
            
            // Envoyer l'email avec le code QR en pièce jointe
            boolean sent = EmailSender.sendEmail(
                email, 
                "Confirmation de réservation - " + foyer.getNom(), 
                emailContent,
                qrContent,
                qrFileName
            );
            
            if (sent) {
                System.out.println("Email de confirmation avec code QR envoyé avec succès à " + email);
            } else {
                System.err.println("Erreur lors de l'envoi de l'email de confirmation à " + email);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }
}
