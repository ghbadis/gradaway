package entities;

import java.time.LocalDate;

public class ReservationFoyer {
    private int idReservation;
    private int foyerId;
    private int idEtudiant;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateReservation;



    public ReservationFoyer(int idReservation, int foyerId, int idEtudiant, LocalDate dateDebut, LocalDate dateFin, LocalDate dateReservation) {
        this.idReservation = idReservation;
        this.foyerId = foyerId;
        this.idEtudiant = idEtudiant;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.dateReservation = dateReservation;
    }

    public ReservationFoyer( int foyerId, int idEtudiant, LocalDate dateDebut, LocalDate dateFin, LocalDate dateReservation) {
        this.foyerId = foyerId;
        this.idEtudiant = idEtudiant;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.dateReservation = dateReservation;
    }

    public ReservationFoyer() {

    }


    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getFoyerId() {
        return foyerId;
    }

    public void setFoyerId(int foyerId) {
        this.foyerId = foyerId;
    }

    public int getIdEtudiant() {
        return idEtudiant;
    }

    public void setIdEtudiant(int idEtudiant) {
        this.idEtudiant = idEtudiant;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    @Override
    public String toString() {
        return "ReservationFoyer{" +
                "idReservation=" + idReservation +
                ", foyerId=" + foyerId +
                ", idEtudiant=" + idEtudiant +
                ", dateDebut=" + dateDebut +
                ", dateFin=" + dateFin +
                ", dateReservation=" + dateReservation +
                '}';
    }

    public void setIdEtudient(int currentUserId) {
    }

    public void setIdFoyer(int selectedFoyerId) {
    }
}
