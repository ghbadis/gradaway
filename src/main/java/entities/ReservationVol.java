package entities;

import java.time.LocalDateTime;

public class ReservationVol {
    private int idReservation;
    private int idVol;
    private int idEtudiant;
    private LocalDateTime dateReservation;
    private int nombrePlaces;
    private double prixTotal;
    private String statutPaiement;
    private String classe;
    private String typeBagage;
    private String commentaires;
    private String referenceReservation;

    // Constructeur
    public ReservationVol(int idReservation, int idVol, int idEtudiant, LocalDateTime dateReservation,
                          int nombrePlaces, double prixTotal, String statutPaiement, String classe,
                          String typeBagage, String commentaires, String referenceReservation) {
        this.idReservation = idReservation;
        this.idVol = idVol;
        this.idEtudiant = idEtudiant;
        this.dateReservation = dateReservation;
        this.nombrePlaces = nombrePlaces;
        this.prixTotal = prixTotal;
        this.statutPaiement = statutPaiement;
        this.classe = classe;
        this.typeBagage = typeBagage;
        this.commentaires = commentaires;
        this.referenceReservation = referenceReservation;
    }

    // Getters et setters
    public int getIdReservation() { return idReservation; }
    public void setIdReservation(int idReservation) { this.idReservation = idReservation; }

    public int getIdVol() { return idVol; }
    public void setIdVol(int idVol) { this.idVol = idVol; }

    public int getIdEtudiant() { return idEtudiant; }
    public void setIdEtudiant(int idEtudiant) { this.idEtudiant = idEtudiant; }

    public LocalDateTime getDateReservation() { return dateReservation; }
    public void setDateReservation(LocalDateTime dateReservation) { this.dateReservation = dateReservation; }

    public int getNombrePlaces() { return nombrePlaces; }
    public void setNombrePlaces(int nombrePlaces) { this.nombrePlaces = nombrePlaces; }

    public double getPrixTotal() { return prixTotal; }
    public void setPrixTotal(double prixTotal) { this.prixTotal = prixTotal; }

    public String getStatutPaiement() { return statutPaiement; }
    public void setStatutPaiement(String statutPaiement) { this.statutPaiement = statutPaiement; }

    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    public String getTypeBagage() { return typeBagage; }
    public void setTypeBagage(String typeBagage) { this.typeBagage = typeBagage; }

    public String getCommentaires() { return commentaires; }
    public void setCommentaires(String commentaires) { this.commentaires = commentaires; }

    public String getReferenceReservation() { return referenceReservation; }
    public void setReferenceReservation(String referenceReservation) { this.referenceReservation = referenceReservation; }
}