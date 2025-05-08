package entities;

public class Evenement {
    private int id_evenement;
    private String nom;
    private String description;
    private String date;
    private String lieu;
    private String domaine;
    private int places_disponibles;
    private String image;

    public Evenement(int id_evenement, String nom, String description, String date, String lieu, String domaine, int places_disponibles, String image) {
        this.id_evenement = id_evenement;
        this.nom = nom;
        this.description = description;
        this.date = date;
        this.lieu = lieu;
        this.domaine = domaine;
        this.places_disponibles = places_disponibles;
        this.image = image;
    }

    public Evenement(String nom, String description, String date, String lieu, String domaine, int places_disponibles, String image) {
        this.nom = nom;
        this.description = description;
        this.date = date;
        this.lieu = lieu;
        this.domaine = domaine;
        this.places_disponibles = places_disponibles;
        this.image = image;
    }

    public int getId_evenement() {
        return id_evenement;
    }

    public void setId_evenement(int id_evenement) {
        this.id_evenement = id_evenement;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public int getPlaces_disponibles() {
        return places_disponibles;
    }

    public void setPlaces_disponibles(int places_disponibles) {
        this.places_disponibles = places_disponibles;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Evenement{" +
                "id_evenement=" + id_evenement +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", date='" + date + '\'' +
                ", lieu='" + lieu + '\'' +
                ", domaine='" + domaine + '\'' +
                ", places_disponibles=" + places_disponibles +
                ", image='" + image + '\'' +
                '}';
    }
}
