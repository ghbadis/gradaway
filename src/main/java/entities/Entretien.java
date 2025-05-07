package entities;

import java.time.LocalDate;
import java.time.LocalTime;

public class Entretien {
    private int id_entretien;
    private int id_expert;
    private int id_user;
    private LocalDate date_entretien;
    private LocalTime heure_entretien;
    private String etat_entretien;
    private String type_entretien;

    public Entretien(int id_entretien, int id_expert, int id_user, LocalDate date_entretien, LocalTime heure_entretien, String etat_entretien, String type_entretien) {
        this.id_entretien = id_entretien;
        this.id_expert = id_expert;
        this.id_user = id_user;
        this.date_entretien = date_entretien;
        this.heure_entretien = heure_entretien;
        this.etat_entretien = etat_entretien;
        this.type_entretien = type_entretien;
    }

    public Entretien(int id_expert, int id_user, LocalDate date_entretien, LocalTime heure_entretien, String etat_entretien, String type_entretien) {
        this.id_expert = id_expert;
        this.id_user = id_user;
        this.date_entretien = date_entretien;
        this.heure_entretien = heure_entretien;
        this.etat_entretien = etat_entretien;
        this.type_entretien = type_entretien;
    }

    public String getType_entretien() {
        return type_entretien;
    }

    public void setType_entretien(String type_entretien) {
        this.type_entretien = type_entretien;
    }

    public int getId_entretien() {
        return id_entretien;
    }

    public void setId_entretien(int id_entretien) {
        this.id_entretien = id_entretien;
    }

    public int getId_expert() {
        return id_expert;
    }

    public void setId_expert(int id_expert) {
        this.id_expert = id_expert;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public LocalDate getDate_entretien() {
        return date_entretien;
    }

    public void setDate_entretien(LocalDate date_entretien) {
        this.date_entretien = date_entretien;
    }

    public LocalTime getHeure_entretien() {
        return heure_entretien;
    }

    public void setHeure_entretien(LocalTime heure_entretien) {
        this.heure_entretien = heure_entretien;
    }

    public String getEtat_entretien() {
        return etat_entretien;
    }

    public void setEtat_entretien(String etat_entretien) {
        this.etat_entretien = etat_entretien;
    }

    @Override
    public String toString() {
        return "Entretien{" +
                "id_entretien=" + id_entretien +
                ", id_expert=" + id_expert +
                ", id_user=" + id_user +
                ", date_entretien=" + date_entretien +
                ", heure_entretien=" + heure_entretien +
                ", etat_entretien='" + etat_entretien +
                ", type_entretien='" + type_entretien +
                '}';
    }
}
