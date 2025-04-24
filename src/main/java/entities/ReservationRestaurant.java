package entities;

import java.time.LocalDate;

public class ReservationRestaurant {
    private int idReservation;
    private int idRestaurant;
    private int idEtudiant;
    private LocalDate dateReservation;
    private int nombrePersonnes;

    // ✅ Constructeur vide
    public ReservationRestaurant() {
    }

    // ✅ Constructeur avec tous les champs
    public ReservationRestaurant(int idReservation, int idRestaurant, int idEtudiant,
                                 LocalDate dateReservation, int nombrePersonnes) {
        this.idReservation = idReservation;
        this.idRestaurant = idRestaurant;
        this.idEtudiant = idEtudiant;
        this.dateReservation = dateReservation;
        this.nombrePersonnes = nombrePersonnes;
    }

    // ✅ Constructeur sans idReservation (utilisé lors de l'ajout)
    public ReservationRestaurant(int idRestaurant, int idEtudiant,
                                 LocalDate dateReservation, int nombrePersonnes) {
        this.idRestaurant = idRestaurant;
        this.idEtudiant = idEtudiant;
        this.dateReservation = dateReservation;
        this.nombrePersonnes = nombrePersonnes;
    }

    // ✅ Getters & Setters
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

    public int getIdEtudiant() {
        return idEtudiant;
    }

    public void setIdEtudiant(int idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    public int getNombrePersonnes() {
        return nombrePersonnes;
    }

    public void setNombrePersonnes(int nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }

    // ✅ toString pour affichage ou debug
    @Override
    public String toString() {
        return "ReservationRestaurant{" +
                "idReservation=" + idReservation +
                ", idRestaurant=" + idRestaurant +
                ", idEtudiant=" + idEtudiant +
                ", dateReservation=" + dateReservation +
                ", nombrePersonnes=" + nombrePersonnes +
                '}';
    }
}
