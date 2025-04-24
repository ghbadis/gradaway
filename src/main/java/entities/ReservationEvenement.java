package entities;

public class ReservationEvenement {
    private int id_reservation;
    private int id_etudiant;
    private int id_evenement;
    private String domaine;
    private String statut;
    private String date;

    public ReservationEvenement(int id_reservation, int id_etudiant, int id_evenement, String domaine, String statut, String date) {
        this.id_reservation = id_reservation;
        this.id_etudiant = id_etudiant;
        this.id_evenement = id_evenement;
        this.domaine = domaine;
        this.statut = statut;
        this.date = date;
    }

    public ReservationEvenement(int id_etudiant, int id_evenement, String domaine, String statut, String date) {
        this.id_etudiant = id_etudiant;
        this.id_evenement = id_evenement;
        this.domaine = domaine;
        this.statut = statut;
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

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
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
                ", domaine='" + domaine + '\'' +
                ", statut='" + statut + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
