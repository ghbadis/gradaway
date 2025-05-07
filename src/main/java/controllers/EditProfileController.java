package controllers;

import Services.ServiceUser;
import entities.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.event.ActionEvent;
import javafx.stage.FileChooser;
import java.io.File;
import java.sql.SQLException;

public class EditProfileController {
    @FXML
    private TextField tfnomprofil;
    @FXML
    private TextField tfprenomprofil;
    @FXML
    private TextField tfemailprofil;
    @FXML
    private TextField tfmdpprfil;
    @FXML
    private TextField tfcinprofil;
    @FXML
    private TextField tftelephoneprofil;
    @FXML
    private ComboBox<String> tfageprofil;
    @FXML
    private DatePicker tfdatenaissanceprofil;
    @FXML
    private TextField tfnationaliteprofil;
    @FXML
    private TextField tfdomaineprofil;
    @FXML
    private TextField tfdiplomeprofil;
    @FXML
    private TextField tfuniversiteprofil;
    @FXML
    private TextField tfmoyenneprofil;
    @FXML
    private Label messageLabel;
    @FXML
    private Button editProfileButton;

    private ServiceUser serviceUser;
    private User currentUser;
    private int userId;
    @FXML
    private ImageView ajouterimageprofil;

    public EditProfileController() {
        serviceUser = new ServiceUser();
    }

    @FXML
    public void initialize() {
        System.out.println("EditProfileController: initialize() called");
        
        // Initialize age ComboBox with values from 18 to 100
        if (tfageprofil != null) {
            System.out.println("EditProfileController: Initializing age ComboBox");
            for (int i = 18; i <= 100; i++) {
                tfageprofil.getItems().add(String.valueOf(i));
            }
        } else {
            System.err.println("EditProfileController: tfageprofil is null");
        }

        // If userId was set before initialize, load the user data
        if (userId > 0) {
            System.out.println("EditProfileController: Loading user data in initialize()");
            loadUserData();
        }
    }

    public void setUserId(int userId) {
        System.out.println("EditProfileController: Setting user ID: " + userId);
        this.userId = userId;
        
        // If fields are already initialized, load the user data
        if (tfageprofil != null) {
            System.out.println("EditProfileController: Loading user data in setUserId()");
            loadUserData();
        }
    }

    private void loadUserData() {
        System.out.println("EditProfileController: loadUserData() called");
        try {
            currentUser = serviceUser.getUserById(userId);
            if (currentUser != null) {
                System.out.println("EditProfileController: User loaded successfully - " + 
                    currentUser.getNom() + " " + currentUser.getPrenom());
                populateFields();
            } else {
                setMessage("Utilisateur non trouvé");
                System.err.println("EditProfileController: User not found with ID: " + userId);
            }
        } catch (SQLException e) {
            setMessage("Erreur lors du chargement des données: " + e.getMessage());
            System.err.println("EditProfileController: Error loading user data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void populateFields() {
        System.out.println("EditProfileController: populateFields() called");
        if (currentUser != null) {
            try {
                System.out.println("EditProfileController: Populating fields for user: " + currentUser.getNom());
                
                // Basic Information
                tfnomprofil.setText(currentUser.getNom());
                tfprenomprofil.setText(currentUser.getPrenom());
                tfemailprofil.setText(currentUser.getEmail());
                tfmdpprfil.setText(currentUser.getMdp());
                tfnationaliteprofil.setText(String.valueOf(currentUser.getCin()));
                tfcinprofil.setText(currentUser.getNationalite());
                tftelephoneprofil.setText(String.valueOf(currentUser.getTelephone()));
                
                // Load profile image if exists
                if (currentUser.getImage() != null && !currentUser.getImage().isEmpty()) {
                    try {
                        File imageFile = new File(currentUser.getImage());
                        if (imageFile.exists()) {
                            Image image = new Image(imageFile.toURI().toString());
                            ajouterimageprofil.setImage(image);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading profile image: " + e.getMessage());
                    }
                }
                
                // Age and Date of Birth
                if (tfageprofil != null) {
                    tfageprofil.getSelectionModel().select(String.valueOf(currentUser.getAge()));
                }
                if (tfdatenaissanceprofil != null) {
                    tfdatenaissanceprofil.setValue(currentUser.getDateNaissance());
                }
                
                // Additional Information
                tfdomaineprofil.setText(currentUser.getDomaine_etude());
                tfdiplomeprofil.setText(String.valueOf(currentUser.getAnnee_obtention_diplome()));
                tfuniversiteprofil.setText(currentUser.getUniversite_origine());
                tfmoyenneprofil.setText(String.valueOf(currentUser.getMoyennes()));
                
                // Welcome message
                setMessage("Bienvenue " + currentUser.getPrenom() + " " + currentUser.getNom());
                System.out.println("EditProfileController: Fields populated successfully");
            } catch (Exception e) {
                setMessage("Erreur lors du remplissage des champs: " + e.getMessage());
                System.err.println("EditProfileController: Error populating fields: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("EditProfileController: Cannot populate fields - currentUser is null");
        }
    }

    private void setMessage(String message) {
        if (messageLabel != null) {
            messageLabel.setText(message);
        } else {
            System.err.println("Message Label is not available: " + message);
        }
    }

    @FXML
    private void handleEditProfile() {
        try {
            System.out.println("Edit profile button clicked");
            
            // Validate required fields
            if (tfnomprofil.getText().isEmpty() || tfprenomprofil.getText().isEmpty() || 
                tfemailprofil.getText().isEmpty() || tfmdpprfil.getText().isEmpty() ||
                tfcinprofil.getText().isEmpty() || tftelephoneprofil.getText().isEmpty() ||
                tfageprofil.getValue() == null || tfdatenaissanceprofil.getValue() == null ||
                tfnationaliteprofil.getText().isEmpty() || tfdomaineprofil.getText().isEmpty() ||
                tfdiplomeprofil.getText().isEmpty() || tfuniversiteprofil.getText().isEmpty() ||
                tfmoyenneprofil.getText().isEmpty()) {
                setMessage("Veuillez remplir tous les champs obligatoires");
                return;
            }

            // Update the currentUser object with new values
            currentUser.setNom(tfnomprofil.getText());
            currentUser.setPrenom(tfprenomprofil.getText());
            currentUser.setEmail(tfemailprofil.getText());
            currentUser.setMdp(tfmdpprfil.getText());
            currentUser.setCin(Integer.parseInt(tfnationaliteprofil.getText()));
            currentUser.setNationalite(tfcinprofil.getText());
            currentUser.setTelephone(Integer.parseInt(tftelephoneprofil.getText()));
            currentUser.setAge(Integer.parseInt(tfageprofil.getValue()));
            currentUser.setDateNaissance(tfdatenaissanceprofil.getValue());
            currentUser.setDomaine_etude(tfdomaineprofil.getText());
            currentUser.setAnnee_obtention_diplome(Integer.parseInt(tfdiplomeprofil.getText()));
            currentUser.setUniversite_origine(tfuniversiteprofil.getText());
            currentUser.setMoyennes(Integer.parseInt(tfmoyenneprofil.getText()));


            //
            // Update the user in the database
            serviceUser.modifier(currentUser);
            setMessage("Profil mis à jour avec succès!");
            System.out.println("Profile updated successfully for user: " + currentUser.getNom());
        } catch (NumberFormatException e) {
            setMessage("Erreur: Veuillez entrer des nombres valides pour les champs numériques");
            System.err.println("Number format error: " + e.getMessage());
        } catch (Exception e) {
            setMessage("Erreur lors de la mise à jour du profil: " + e.getMessage());
            System.err.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void changephoto(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Profile Picture");
        
        // Set extension filters
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        
        // Show open file dialog
        File selectedFile = fileChooser.showOpenDialog(null);
        
        if (selectedFile != null) {
            try {
                // Create an image from the selected file
                Image image = new Image(selectedFile.toURI().toString());
                
                // Set the image to the ImageView
                ajouterimageprofil.setImage(image);
                
                // Save the image path to the database
                if (currentUser != null) {
                    // Create a copy of the image in the application's resources directory
                    String destinationPath = "src/main/resources/images/profiles/" + selectedFile.getName();
                    File destinationFile = new File(destinationPath);
                    
                    // Create directories if they don't exist
                    destinationFile.getParentFile().mkdirs();
                    
                    // Copy the file
                    java.nio.file.Files.copy(
                        selectedFile.toPath(),
                        destinationFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                    );
                    
                    // Update user with the new image path
                    currentUser.setImage(destinationPath);
                    serviceUser.modifier(currentUser);
                    setMessage("Photo de profil mise à jour avec succès!");
                    System.out.println("Profile picture updated successfully for user: " + currentUser.getNom());
                } else {
                    setMessage("Erreur: Aucun utilisateur connecté");
                }
            } catch (Exception e) {
                setMessage("Erreur lors du chargement de l'image: " + e.getMessage());
                System.err.println("Error loading image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
