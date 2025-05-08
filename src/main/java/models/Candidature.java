package models;

import java.util.Date;

public class Candidature {
    private int id_c;
    private int id_dossier;
    private int user_id;
    private int id_universite;
    private Date date_de_remise_c;
    private String domaine;

    public Candidature() {
    }

    public Candidature(int id_c, int id_dossier, int user_id, int id_universite, Date date_de_remise_c, String domaine) {
        this.id_c = id_c;
        this.id_dossier = id_dossier;
        this.user_id = user_id;
        this.id_universite = id_universite;
        this.date_de_remise_c = date_de_remise_c;
        this.domaine = domaine;
    }

    public int getId_c() {
        return id_c;
    }

    public void setId_c(int id_c) {
        this.id_c = id_c;
    }

    public int getId_dossier() {
        return id_dossier;
    }

    public void setId_dossier(int id_dossier) {
        this.id_dossier = id_dossier;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getId_universite() {
        return id_universite;
    }

    public void setId_universite(int id_universite) {
        this.id_universite = id_universite;
    }

    public Date getDate_de_remise_c() {
        return date_de_remise_c;
    }

    public void setDate_de_remise_c(Date date_de_remise_c) {
        this.date_de_remise_c = date_de_remise_c;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    @Override
    public String toString() {
        return "Candidature{" +
                "id_c=" + id_c +
                ", id_dossier=" + id_dossier +
                ", user_id=" + user_id +
                ", id_universite=" + id_universite +
                ", date_de_remise_c=" + date_de_remise_c +
                ", domaine='" + domaine + '\'' +
                '}';
    }
} 