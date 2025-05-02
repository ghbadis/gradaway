package entities;

public class Reservation {
    private int idReservation;
    private int idRestaurant;
    private String nomClient;
    private String emailClient;
    private String telephoneClient;
    private String dateReservation;
    private String heureReservation;
    private int nombrePersonnes;
    private String commentaires;
    private String statut;

    public Reservation() {
    }

    public Reservation(int idRestaurant, String nomClient, String emailClient, String telephoneClient,
                      String dateReservation, String heureReservation, int nombrePersonnes, 
                      String commentaires, String statut) {
        this.idRestaurant = idRestaurant;
        this.nomClient = nomClient;
        this.emailClient = emailClient;
        this.telephoneClient = telephoneClient;
        this.dateReservation = dateReservation;
        this.heureReservation = heureReservation;
        this.nombrePersonnes = nombrePersonnes;
        this.commentaires = commentaires;
        this.statut = statut;
    }

    public Reservation(int idReservation, int idRestaurant, String nomClient, String emailClient, 
                      String telephoneClient, String dateReservation, String heureReservation, 
                      int nombrePersonnes, String commentaires, String statut) {
        this.idReservation = idReservation;
        this.idRestaurant = idRestaurant;
        this.nomClient = nomClient;
        this.emailClient = emailClient;
        this.telephoneClient = telephoneClient;
        this.dateReservation = dateReservation;
        this.heureReservation = heureReservation;
        this.nombrePersonnes = nombrePersonnes;
        this.commentaires = commentaires;
        this.statut = statut;
    }

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdRestaurant() {
        return idRestaurant;
    }

    public void setIdRestaurant(int idRestaurant) {
        this.idRestaurant = idRestaurant;
    }

    public String getNomClient() {
        return nomClient;
    }

    public void setNomClient(String nomClient) {
        this.nomClient = nomClient;
    }

    public String getEmailClient() {
        return emailClient;
    }

    public void setEmailClient(String emailClient) {
        this.emailClient = emailClient;
    }

    public String getTelephoneClient() {
        return telephoneClient;
    }

    public void setTelephoneClient(String telephoneClient) {
        this.telephoneClient = telephoneClient;
    }

    public String getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(String dateReservation) {
        this.dateReservation = dateReservation;
    }

    public String getHeureReservation() {
        return heureReservation;
    }

    public void setHeureReservation(String heureReservation) {
        this.heureReservation = heureReservation;
    }

    public int getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(int nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }

    public String getCommentaires() {
        return commentaires;
    }

    public void setCommentaires(String commentaires) {
        this.commentaires = commentaires;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "idReservation=" + idReservation +
                ", idRestaurant=" + idRestaurant +
                ", nomClient='" + nomClient + '\'' +
                ", dateReservation='" + dateReservation + '\'' +
                ", heureReservation='" + heureReservation + '\'' +
                ", nombrePersonnes=" + nombrePersonnes +
                ", statut='" + statut + '\'' +
                '}';
    }
}
