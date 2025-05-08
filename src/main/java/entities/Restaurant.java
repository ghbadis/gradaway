package entities;

public class Restaurant {

    private int idRestaurant;
    private String nom;
    private String adresse;
    private String ville;
    private String pays;
    private int capaciteTotale;
    private String horaireOuverture;
    private String horaireFermeture;
    private String telephone;
    private String email;
    private String image;

    // Constructeur complet
    public Restaurant(int idRestaurant, String nom, String adresse, String ville, String pays,
                      int capaciteTotale, String horaireOuverture, String horaireFermeture,
                      String telephone, String email, String image) {
        this.idRestaurant = idRestaurant;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.pays = pays;
        this.capaciteTotale = capaciteTotale;
        this.horaireOuverture = horaireOuverture;
        this.horaireFermeture = horaireFermeture;
        this.telephone = telephone;
        this.email = email;
        this.image = image;
    }
    
    // Constructeur complet sans image
    public Restaurant(int idRestaurant, String nom, String adresse, String ville, String pays,
                      int capaciteTotale, String horaireOuverture, String horaireFermeture,
                      String telephone, String email) {
        this.idRestaurant = idRestaurant;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.pays = pays;
        this.capaciteTotale = capaciteTotale;
        this.horaireOuverture = horaireOuverture;
        this.horaireFermeture = horaireFermeture;
        this.telephone = telephone;
        this.email = email;
    }

    // Constructeur sans ID (utile pour l'ajout)
    public Restaurant(String nom, String adresse, String ville, String pays,
                      int capaciteTotale, String horaireOuverture, String horaireFermeture,
                      String telephone, String email, String image) {
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.pays = pays;
        this.capaciteTotale = capaciteTotale;
        this.horaireOuverture = horaireOuverture;
        this.horaireFermeture = horaireFermeture;
        this.telephone = telephone;
        this.email = email;
        this.image = image;
    }
    
    // Constructeur sans ID et sans image
    public Restaurant(String nom, String adresse, String ville, String pays,
                      int capaciteTotale, String horaireOuverture, String horaireFermeture,
                      String telephone, String email) {
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.pays = pays;
        this.capaciteTotale = capaciteTotale;
        this.horaireOuverture = horaireOuverture;
        this.horaireFermeture = horaireFermeture;
        this.telephone = telephone;
        this.email = email;
    }

    public Restaurant() {

    }

    // Getters & Setters
    public int getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(int idRestaurant) {
        this.idRestaurant = idRestaurant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getPays() {
        return pays;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public int getCapaciteTotale() {
        return capaciteTotale;
    }

    public void setCapaciteTotale(int capaciteTotale) {
        this.capaciteTotale = capaciteTotale;
    }

    public String getHoraireOuverture() {
        return horaireOuverture;
    }

    public void setHoraireOuverture(String horaireOuverture) {
        this.horaireOuverture = horaireOuverture;
    }

    public String getHoraireFermeture() {
        return horaireFermeture;
    }

    public void setHoraireFermeture(String horaireFermeture) {
        this.horaireFermeture = horaireFermeture;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "idRestaurant=" + idRestaurant +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", ville='" + ville + '\'' +
                ", pays='" + pays + '\'' +
                ", capaciteTotale=" + capaciteTotale +
                ", horaireOuverture='" + horaireOuverture + '\'' +
                ", horaireFermeture='" + horaireFermeture + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
