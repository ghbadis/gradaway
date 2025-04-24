package entities;

public class Foyer {
    private int idFoyer;
    private String nom;
    private String adresse;
    private String ville;
    private String pays;
    private int nombreDeChambre;
    private int capacite;

    // Constructeur complet avec tous les paramètres
    public Foyer(int idFoyer, String nom, String adresse, String ville, String pays, int nombreDeChambre, int capacite) {
        this.idFoyer = idFoyer;
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.pays = pays;
        this.nombreDeChambre = nombreDeChambre;
        this.capacite = capacite;
    }

    public Foyer(String nom, String adresse, String ville, String pays, int nombreDeChambre, int capacite) {
        this.nom = nom;
        this.adresse = adresse;
        this.ville = ville;
        this.pays = pays;
        this.nombreDeChambre = nombreDeChambre;
        this.capacite = capacite;
    }

    // Constructeur sans paramètre
    public Foyer() {}

    // Getters et setters
    public int getIdFoyer() {
        return idFoyer;
    }

    public void setIdFoyer(int idFoyer) {
        this.idFoyer = idFoyer;
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

    public int getNombreDeChambre() {
        return nombreDeChambre;
    }

    public void setNombreDeChambre(int nombreDeChambre) {
        this.nombreDeChambre = nombreDeChambre;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    @Override
    public String toString() {
        return "Foyer{" +
                "idFoyer=" + idFoyer +
                ", nom='" + nom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", ville='" + ville + '\'' +
                ", pays='" + pays + '\'' +
                ", nombreDeChambre=" + nombreDeChambre +
                ", capacite=" + capacite +
                '}';
    }
}
