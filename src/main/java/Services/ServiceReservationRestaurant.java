package Services;



import entities.ReservationRestaurant;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationRestaurant implements IService<ReservationRestaurant> {

    private final Connection con;

    public ServiceReservationRestaurant() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(ReservationRestaurant reservation) throws SQLException {
        String req = "INSERT INTO reservationrestaurant (idRestaurant, IdEtudient, dateReservation, nombrePersonne) " +
                "VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getIdRestaurant());
        ps.setInt(2, reservation.getIdEtudiant());
        ps.setDate(3, Date.valueOf(reservation.getDateReservation()));
        ps.setInt(4, reservation.getNombrePersonnes());
        ps.executeUpdate();
        System.out.println("Réservation restaurant ajoutée !");
    }

    @Override
    public void modifier(ReservationRestaurant reservation) throws SQLException {
        String req = "UPDATE reservationrestaurant SET idRestaurant=?, IdEtudient=?, dateReservation=?, nombrePersonne=? " +
                "WHERE idReservation=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getIdRestaurant());
        ps.setInt(2, reservation.getIdEtudiant());
        ps.setDate(3, Date.valueOf(reservation.getDateReservation()));
        ps.setInt(4, reservation.getNombrePersonnes());
        ps.setInt(5, reservation.getIdReservation());
        ps.executeUpdate();
        System.out.println("Réservation restaurant modifiée !");
    }

    @Override
    public void supprimer(ReservationRestaurant reservation) throws SQLException {
        String req = "DELETE FROM reservationrestaurant WHERE idReservation=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getIdReservation());
        ps.executeUpdate();
        System.out.println("Réservation restaurant supprimée !");
    }

    @Override
    public List<ReservationRestaurant> recuperer() throws SQLException {
        List<ReservationRestaurant> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservationrestaurant";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int id = rs.getInt("idReservation");
            int idRestaurant = rs.getInt("idRestaurant");
            int idEtudiant = rs.getInt("IdEtudient");
            LocalDate dateReservation = rs.getDate("dateReservation").toLocalDate();
            int nombrePersonnes = rs.getInt("nombrePersonne");

            ReservationRestaurant r = new ReservationRestaurant(id, idRestaurant, idEtudiant, dateReservation, nombrePersonnes);
            reservations.add(r);
        }

        return reservations;
    }

}
