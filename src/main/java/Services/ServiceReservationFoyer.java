package Services;


import entities.ReservationFoyer;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationFoyer implements IService<ReservationFoyer> {
    private Connection con;
    ServiceUser us = new ServiceUser();

    public ServiceReservationFoyer() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(ReservationFoyer reservation) throws SQLException {
        String req = "INSERT INTO reservationfoyer (idfoyer, idetudient, datedebut, DateFin, dateReservation) " +
                "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getFoyerId());
       ps.setInt(2, reservation.getIdEtudiant());
      // ps.setInt(2, user.getUserById());

        ps.setDate(3, Date.valueOf(reservation.getDateDebut()));
        ps.setDate(4, Date.valueOf(reservation.getDateFin()));
        ps.setDate(5, Date.valueOf(reservation.getDateReservation()));
        ps.executeUpdate();
        System.out.println("Réservation ajoutée !");
    }

    @Override
    public void modifier(ReservationFoyer reservation) throws SQLException {
        String req = "UPDATE reservationfoyer SET idfoyer=?, idetudient=?, datedebut=?, DateFin=?, dateReservation=? " +
                "WHERE IdReservation=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getFoyerId());
        ps.setInt(2, reservation.getIdEtudiant());
        ps.setDate(3, Date.valueOf(reservation.getDateDebut()));
        ps.setDate(4, Date.valueOf(reservation.getDateFin()));
        ps.setDate(5, Date.valueOf(reservation.getDateReservation()));
        ps.setInt(6, reservation.getIdReservation());
        ps.executeUpdate();
        System.out.println("Réservation modifiée !");
    }

    @Override
    public boolean supprimer(ReservationFoyer reservation) throws SQLException {
        String req = "DELETE FROM reservationfoyer WHERE IdReservation=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getIdReservation());
        int rowsAffected = ps.executeUpdate();
        System.out.println("Réservation supprimée !");
        return rowsAffected > 0;
    }


    public boolean studentExists(int studentId) throws SQLException {
        String sql = "SELECT id FROM user WHERE id = ?";
        Connection connection;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    public boolean foyerExists(int foyerId) throws SQLException {
        String sql = "SELECT id FROM foyer WHERE id = ?";
        Connection connection;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, foyerId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    @Override
    public List<ReservationFoyer> recuperer() throws SQLException {
        List<ReservationFoyer> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservationfoyer";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int id = rs.getInt("IdReservation");
            int foyerId = rs.getInt("idfoyer");
            int idEtudiant = rs.getInt("idetudient");
            LocalDate dateDebut = rs.getDate("datedebut").toLocalDate();
            LocalDate DateFin = rs.getDate("DateFin").toLocalDate();
            LocalDate dateReservation = rs.getDate("dateReservation").toLocalDate();

            ReservationFoyer reservation = new ReservationFoyer(id, foyerId, idEtudiant, dateDebut, DateFin, dateReservation);
            reservations.add(reservation);
        }

        return reservations;
    }
    
    /**
     * Récupère toutes les réservations d'un étudiant spécifique
     * @param idEtudiant ID de l'étudiant
     * @return Liste des réservations de l'étudiant
     * @throws SQLException En cas d'erreur SQL
     */
    public List<ReservationFoyer> getReservationsByEtudiantId(int idEtudiant) throws SQLException {
        List<ReservationFoyer> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservationfoyer WHERE idetudient=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, idEtudiant);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("IdReservation");
            int foyerId = rs.getInt("idfoyer");
            LocalDate dateDebut = rs.getDate("datedebut").toLocalDate();
            LocalDate dateFin = rs.getDate("DateFin").toLocalDate();
            LocalDate dateReservation = rs.getDate("dateReservation").toLocalDate();

            ReservationFoyer reservation = new ReservationFoyer(id, foyerId, idEtudiant, dateDebut, dateFin, dateReservation);
            reservations.add(reservation);
        }

        return reservations;
    }
    
    /**
     * Récupère toutes les réservations sans filtrer
     * @return Liste de toutes les réservations
     * @throws SQLException En cas d'erreur SQL
     */
    public List<ReservationFoyer> getAllReservations() throws SQLException {
        return recuperer();
    }

}
