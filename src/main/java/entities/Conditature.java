
package entities;

public class Conditature {
    private int id_c;
    private int id_dossier;
    private int user_id;
    private int id_universite;
    private String date_de_remise_C;
    private String Domaine;

    // Constructor with all fields (including ID)
    public Conditature(int id_c, int id_dossier, int user_id, int id_universite, String date_de_remise_C, String Domaine) {
        this.id_c = id_c;
        this.id_dossier = id_dossier;
        this.user_id = user_id;
        this.id_universite = id_universite;
        this.date_de_remise_C = date_de_remise_C;
        this.Domaine = Domaine;
    }

    // Constructor without ID field
    public Conditature(int id_dossier, int user_id, int id_universite, String date_de_remise_C, String Domaine) {
        this.id_dossier = id_dossier;
        this.user_id = user_id;
        this.id_universite = id_universite;
        this.date_de_remise_C = date_de_remise_C;
        this.Domaine = Domaine;
    }

    // Getters and Setters
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

    public String getDate_de_remise_C() {
        return date_de_remise_C;
    }

    public void setDate_de_remise_C(String date_de_remise_C) {
        this.date_de_remise_C = date_de_remise_C;
    }

    public String getDomaine() {
        return Domaine;
    }

    public void setDomaine(String Domaine) {
        this.Domaine = Domaine;
    }

    @Override
    public String toString() {
        return "Conditature{" +
                "id_c=" + id_c +
                ", id_dossier=" + id_dossier +
                ", user_id=" + user_id +
                ", id_universite=" + id_universite +
                ", date_de_remise_C='" + date_de_remise_C + '\'' +
                ", Domaine='" + Domaine + '\'' +
                '}';
    }
}