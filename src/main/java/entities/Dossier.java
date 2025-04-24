package entities;

import java.time.LocalDate;

public class Dossier {
    private int id_dossier;
    private int id_etudiant;
    private String cin;
    private String photo;
    private String diplome_baccalauréat;
    private String releve_note;
    private String diplome_obtenus;
    private String lettre_motivations;
    private String dossier_sante;
    private String cv;
    private LocalDate datedepot;


    public Dossier(int id_dossier, int id_etudiant, String cin, String photo,
                   String diplome_baccalauréat, String releve_note, String diplome_obtenus,
                   String lettre_motivations, String dossier_sante, String cv, LocalDate datedepot) {
        this.id_dossier = id_dossier;
        this.id_etudiant = id_etudiant;
        this.cin = cin;
        this.photo = photo;
        this.diplome_baccalauréat = diplome_baccalauréat;
        this.releve_note = releve_note;
        this.diplome_obtenus = diplome_obtenus;
        this.lettre_motivations = lettre_motivations;
        this.dossier_sante = dossier_sante;
        this.cv = cv;
        this.datedepot = datedepot;
    }
    public Dossier(int id_etudiant, String cin, String photo,
                   String diplome_baccalauréat, String releve_note, String diplome_obtenus,
                   String lettre_motivations, String dossier_sante, String cv, LocalDate datedepot) {
        this.id_etudiant = id_etudiant;
        this.cin = cin;
        this.photo = photo;
        this.diplome_baccalauréat = diplome_baccalauréat;
        this.releve_note = releve_note;
        this.diplome_obtenus = diplome_obtenus;
        this.lettre_motivations = lettre_motivations;
        this.dossier_sante = dossier_sante;
        this.cv = cv;
        this.datedepot = datedepot;
    }

    // Getter and setter for etape

    public int getId_dossier() {
        return id_dossier;
    }

    public void setId_dossier(int id_dossier) {
        this.id_dossier = id_dossier;
    }

    public int getId_etudiant() {
        return id_etudiant;
    }

    public void setId_etudiant(int id_etudiant) {
        this.id_etudiant = id_etudiant;
    }

    public String getCin() {
        return cin;
    }

    public void setCin(String cin) {
        this.cin = cin;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDiplome_baccalauréat() {
        return diplome_baccalauréat;
    }

    public void setDiplome_baccalauréat(String diplome_baccalauréat) {
        this.diplome_baccalauréat = diplome_baccalauréat;
    }

    public String getReleve_note() {
        return releve_note;
    }

    public void setReleve_note(String releve_note) {
        this.releve_note = releve_note;
    }

    public String getDiplome_obtenus() {
        return diplome_obtenus;
    }

    public void setDiplome_obtenus(String diplome_obtenus) {
        this.diplome_obtenus = diplome_obtenus;
    }

    public String getLettre_motivations() {
        return lettre_motivations;
    }

    public void setLettre_motivations(String lettre_motivations) {
        this.lettre_motivations = lettre_motivations;
    }

    public String getDossier_sante() {
        return dossier_sante;
    }

    public void setDossier_sante(String dossier_sante) {
        this.dossier_sante = dossier_sante;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public LocalDate getDatedepot() {
        return datedepot;
    }

    public void setDatedepot(LocalDate datedepot) {
        this.datedepot = datedepot;
    }

    @Override
    public String toString() {
        return "Dossier{" +
                "id_dossier=" + id_dossier +
                ", id_etudiant=" + id_etudiant +
                ", cin='" + cin + '\'' +
                ", photo='" + photo + '\'' +
                ", diplome_baccalauréat='" + diplome_baccalauréat + '\'' +
                ", releve_note='" + releve_note + '\'' +
                ", diplome_obtenus='" + diplome_obtenus + '\'' +
                ", lettre_motivations='" + lettre_motivations + '\'' +
                ", dossier_sante='" + dossier_sante + '\'' +
                ", cv='" + cv + '\'' +
                ", datedepot=" + datedepot +
                '}';
    }
}
