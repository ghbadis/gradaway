package entities;

import java.time.LocalDateTime;

public class Vols {
    private int idVol;
    private String numeroVol;
    private String compagnie;
    private String aeroportDepart;
    private String villeDepart;
    private String paysDepart;
    private String aeroportArrivee;
    private String villeArrivee;
    private String paysArrivee;
    private LocalDateTime dateDepart;
    private LocalDateTime dateArrivee;
    private Integer duree;
    private double prixStandard;
    private int placesDisponibles;
    private String statut;
    private String imagePath;

    public Vols(int idVol, String numeroVol, String compagnie, String aeroportDepart,
                String villeDepart, String paysDepart, String aeroportArrivee,
                String villeArrivee, String paysArrivee, LocalDateTime dateDepart,
                LocalDateTime dateArrivee, Integer duree, double prixStandard,
                int placesDisponibles, String statut, String imagePath) {
        this.idVol = idVol;
        this.numeroVol = numeroVol;
        this.compagnie = compagnie;
        this.aeroportDepart = aeroportDepart;
        this.villeDepart = villeDepart;
        this.paysDepart = paysDepart;
        this.aeroportArrivee = aeroportArrivee;
        this.villeArrivee = villeArrivee;
        this.paysArrivee = paysArrivee;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.duree = duree;
        this.prixStandard = prixStandard;
        this.placesDisponibles = placesDisponibles;
        this.statut = statut;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public int getIdVol() { return idVol; }
    public void setIdVol(int idVol) { this.idVol = idVol; }

    public String getNumeroVol() { return numeroVol; }
    public void setNumeroVol(String numeroVol) { this.numeroVol = numeroVol; }

    public String getCompagnie() { return compagnie; }
    public void setCompagnie(String compagnie) { this.compagnie = compagnie; }

    public String getAeroportDepart() { return aeroportDepart; }
    public void setAeroportDepart(String aeroportDepart) { this.aeroportDepart = aeroportDepart; }

    public String getVilleDepart() { return villeDepart; }
    public void setVilleDepart(String villeDepart) { this.villeDepart = villeDepart; }

    public String getPaysDepart() { return paysDepart; }
    public void setPaysDepart(String paysDepart) { this.paysDepart = paysDepart; }

    public String getAeroportArrivee() { return aeroportArrivee; }
    public void setAeroportArrivee(String aeroportArrivee) { this.aeroportArrivee = aeroportArrivee; }

    public String getVilleArrivee() { return villeArrivee; }
    public void setVilleArrivee(String villeArrivee) { this.villeArrivee = villeArrivee; }

    public String getPaysArrivee() { return paysArrivee; }
    public void setPaysArrivee(String paysArrivee) { this.paysArrivee = paysArrivee; }

    public LocalDateTime getDateDepart() { return dateDepart; }
    public void setDateDepart(LocalDateTime dateDepart) { this.dateDepart = dateDepart; }

    public LocalDateTime getDateArrivee() { return dateArrivee; }
    public void setDateArrivee(LocalDateTime dateArrivee) { this.dateArrivee = dateArrivee; }

    public Integer getDuree() { return duree; }
    public void setDuree(Integer duree) { this.duree = duree; }

    public double getPrixStandard() { return prixStandard; }
    public void setPrixStandard(double prixStandard) { this.prixStandard = prixStandard; }

    public int getPlacesDisponibles() { return placesDisponibles; }
    public void setPlacesDisponibles(int placesDisponibles) { this.placesDisponibles = placesDisponibles; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    @Override
    public String toString() {
        return "Vol{" +
                "idVol=" + idVol +
                ", numeroVol='" + numeroVol + '\'' +
                ", compagnie='" + compagnie + '\'' +
                ", aeroportDepart='" + aeroportDepart + '\'' +
                ", villeDepart='" + villeDepart + '\'' +
                ", paysDepart='" + paysDepart + '\'' +
                ", aeroportArrivee='" + aeroportArrivee + '\'' +
                ", villeArrivee='" + villeArrivee + '\'' +
                ", paysArrivee='" + paysArrivee + '\'' +
                ", dateDepart=" + dateDepart +
                ", dateArrivee=" + dateArrivee +
                ", duree=" + duree +
                ", prixStandard=" + prixStandard +
                ", placesDisponibles=" + placesDisponibles +
                ", statut='" + statut + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}