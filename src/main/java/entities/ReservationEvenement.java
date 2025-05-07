package entities;

public class ReservationEvenement {
    private int id_reservation;
    private int id_etudiant;
    private int id_evenement;
    private String email;
    private String nom;
    private String prenom;
    private String date;

    public ReservationEvenement(int id_reservation, int id_etudiant, int id_evenement, String email, String nom, String prenom, String date) {
        this.id_reservation = id_reservation;
        this.id_etudiant = id_etudiant;
        this.id_evenement = id_evenement;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.date = date;
    }

    public ReservationEvenement(int id_etudiant, int id_evenement, String email, String nom, String prenom, String date) {
        this.id_etudiant = id_etudiant;
        this.id_evenement = id_evenement;
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.date = date;
    }

    public int getId_reservation() {
        return id_reservation;
    }

    public void setId_reservation(int id_reservation) {
        this.id_reservation = id_reservation;
    }

    public int getId_etudiant() {
        return id_etudiant;
    }

    public void setId_etudiant(int id_etudiant) {
        this.id_etudiant = id_etudiant;
    }

    public int getId_evenement() {
        return id_evenement;
    }

    public void setId_evenement(int id_evenement) {
        this.id_evenement = id_evenement;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ReservationEvenement{" +
                "id_reservation=" + id_reservation +
                ", id_etudiant=" + id_etudiant +
                ", id_evenement=" + id_evenement +
                ", email='" + email + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
