package entities;

import java.util.Date;

public class Vols {
    private int idVol;
    private String compagnie;
    private String numeroVol;
    private String aeroportDepart;
    private String aeroportArrivee;
    private String villeDepart;
    private String villeArrivee;
    private String paysDepart;
    private String paysArrivee;
    private Date dateDepart;
    private Date dateArrivee;
    private Integer duree; // en minutes
    private double prixStandard;
    private int placesDisponibles;
    private String statut;

    // Constructeur par défaut
    public Vols() {
        this.statut = "Confirmé";
    }

    // Constructeur pour l'ajout (sans idVol car auto-increment)
    public Vols(String compagnie, String numeroVol, String aeroportDepart, String aeroportArrivee,
               String villeDepart, String villeArrivee, String paysDepart, String paysArrivee,
               Date dateDepart, Date dateArrivee, Integer duree, double prixStandard,
               int placesDisponibles, String statut) {
        this.compagnie = compagnie;
        this.numeroVol = numeroVol;
        this.aeroportDepart = aeroportDepart;
        this.aeroportArrivee = aeroportArrivee;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.paysDepart = paysDepart;
        this.paysArrivee = paysArrivee;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.duree = duree;
        this.prixStandard = prixStandard;
        this.placesDisponibles = placesDisponibles;
        this.statut = statut != null ? statut : "Confirmé";
    }

    // Constructeur pour la modification (avec idVol)
    public Vols(int idVol, String compagnie, String numeroVol, String aeroportDepart, String aeroportArrivee,
               String villeDepart, String villeArrivee, String paysDepart, String paysArrivee,
               Date dateDepart, Date dateArrivee, Integer duree, double prixStandard,
               int placesDisponibles, String statut) {
        this.idVol = idVol;
        this.compagnie = compagnie;
        this.numeroVol = numeroVol;
        this.aeroportDepart = aeroportDepart;
        this.aeroportArrivee = aeroportArrivee;
        this.villeDepart = villeDepart;
        this.villeArrivee = villeArrivee;
        this.paysDepart = paysDepart;
        this.paysArrivee = paysArrivee;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.duree = duree;
        this.prixStandard = prixStandard;
        this.placesDisponibles = placesDisponibles;
        this.statut = statut != null ? statut : "Confirmé";
    }

    // Getters et Setters
    public int getIdVol() {
        return idVol;
    }

    public void setIdVol(int idVol) {
        this.idVol = idVol;
    }

    public String getCompagnie() {
        return compagnie;
    }

    public void setCompagnie(String compagnie) {
        this.compagnie = compagnie;
    }

    public String getNumeroVol() {
        return numeroVol;
    }

    public void setNumeroVol(String numeroVol) {
        this.numeroVol = numeroVol;
    }

    public String getAeroportDepart() {
        return aeroportDepart;
    }

    public void setAeroportDepart(String aeroportDepart) {
        this.aeroportDepart = aeroportDepart;
    }

    public String getAeroportArrivee() {
        return aeroportArrivee;
    }

    public void setAeroportArrivee(String aeroportArrivee) {
        this.aeroportArrivee = aeroportArrivee;
    }

    public String getVilleDepart() {
        return villeDepart;
    }

    public void setVilleDepart(String villeDepart) {
        this.villeDepart = villeDepart;
    }

    public String getVilleArrivee() {
        return villeArrivee;
    }

    public void setVilleArrivee(String villeArrivee) {
        this.villeArrivee = villeArrivee;
    }

    public String getPaysDepart() {
        return paysDepart;
    }

    public void setPaysDepart(String paysDepart) {
        this.paysDepart = paysDepart;
    }

    public String getPaysArrivee() {
        return paysArrivee;
    }

    public void setPaysArrivee(String paysArrivee) {
        this.paysArrivee = paysArrivee;
    }

    public Date getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(Date dateDepart) {
        this.dateDepart = dateDepart;
    }

    public Date getDateArrivee() {
        return dateArrivee;
    }

    public void setDateArrivee(Date dateArrivee) {
        this.dateArrivee = dateArrivee;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public double getPrixStandard() {
        return prixStandard;
    }

    public void setPrixStandard(double prixStandard) {
        this.prixStandard = prixStandard;
    }

    public int getPlacesDisponibles() {
        return placesDisponibles;
    }

    public void setPlacesDisponibles(int placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut != null ? statut : "Confirmé";
    }

    // Méthode toString()
    @Override
    public String toString() {
        return "Vol{" +
                "idVol=" + idVol +
                ", compagnie='" + compagnie + '\'' +
                ", numeroVol='" + numeroVol + '\'' +
                ", aeroportDepart='" + aeroportDepart + '\'' +
                ", aeroportArrivee='" + aeroportArrivee + '\'' +
                ", villeDepart='" + villeDepart + '\'' +
                ", villeArrivee='" + villeArrivee + '\'' +
                ", paysDepart='" + paysDepart + '\'' +
                ", paysArrivee='" + paysArrivee + '\'' +
                ", dateDepart=" + dateDepart +
                ", dateArrivee=" + dateArrivee +
                ", duree=" + duree +
                ", prixStandard=" + prixStandard +
                ", placesDisponibles=" + placesDisponibles +
                ", statut='" + statut + '\'' +
                '}';
    }
}