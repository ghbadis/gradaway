package entities;

import java.util.Date;

public class ReservationVol {
    private int idReservation;
    private int idVol;
    private int idEtudiant;
    private Date dateReservation;
    private int nombrePlaces;
    private double prixTotal;
    private String statutPaiement;
    private String classe;
    private String typeBagage;
    private String commentaires;
    private String referenceReservation;

    // Constructeur par défaut
    public ReservationVol() {
        this.dateReservation = new Date(); // Valeur par défaut = date/heure actuelle
        this.nombrePlaces = 1; // Valeur par défaut = 1
        this.statutPaiement = "En attente"; // Valeur par défaut
        this.classe = "Économique"; // Valeur par défaut
    }

    // Constructeur pour l'ajout (sans idReservation car auto-increment)
    public ReservationVol(int idVol, int idEtudiant, int nombrePlaces,
                       double prixTotal, String referenceReservation) {
        this();
        this.idVol = idVol;
        this.idEtudiant = idEtudiant;
        this.nombrePlaces = nombrePlaces;
        this.prixTotal = prixTotal;
        this.referenceReservation = referenceReservation;
    }

    // Constructeur complet pour la modification
    public ReservationVol(int idReservation, int idVol, int idEtudiant, Date dateReservation,
                       int nombrePlaces, double prixTotal, String statutPaiement,
                       String classe, String typeBagage, String commentaires,
                       String referenceReservation) {
        this.idReservation = idReservation;
        this.idVol = idVol;
        this.idEtudiant = idEtudiant;
        this.dateReservation = dateReservation != null ? dateReservation : new Date();
        this.nombrePlaces = nombrePlaces > 0 ? nombrePlaces : 1;
        this.prixTotal = prixTotal;
        this.statutPaiement = statutPaiement != null ? statutPaiement : "En attente";
        this.classe = classe != null ? classe : "Économique";
        this.typeBagage = typeBagage;
        this.commentaires = commentaires;
        this.referenceReservation = referenceReservation;
    }

    // Getters et Setters
    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdVol() {
        return idVol;
    }

    public void setIdVol(int idVol) {
        this.idVol = idVol;
    }

    public int getIdEtudiant() {
        return idEtudiant;
    }

    public void setIdEtudiant(int idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    public Date getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(Date dateReservation) {
        this.dateReservation = dateReservation != null ? dateReservation : new Date();
    }

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces > 0 ? nombrePlaces : 1;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public String getStatutPaiement() {
        return statutPaiement;
    }

    public void setStatutPaiement(String statutPaiement) {
        this.statutPaiement = statutPaiement != null ? statutPaiement : "En attente";
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe != null ? classe : "Économique";
    }

    public String getTypeBagage() {
        return typeBagage;
    }

    public void setTypeBagage(String typeBagage) {
        this.typeBagage = typeBagage;
    }

    public String getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
    }

    public String getReferenceReservation() {
        return referenceReservation;
    }

    public void setReferenceReservation(String referenceReservation) {
        this.referenceReservation = referenceReservation;
    }

    // Méthode toString()
    @Override
    public String toString() {
        return "Reservation{" +
                "idReservation=" + idReservation +
                ", idVol=" + idVol +
                ", idEtudiant=" + idEtudiant +
                ", dateReservation=" + dateReservation +
                ", nombrePlaces=" + nombrePlaces +
                ", prixTotal=" + prixTotal +
                ", statutPaiement='" + statutPaiement + '\'' +
                ", classe='" + classe + '\'' +
                ", typeBagage='" + typeBagage + '\'' +
                ", commentaires='" + commentaires + '\'' +
                ", referenceReservation='" + referenceReservation + '\'' +
                '}';
    }
}