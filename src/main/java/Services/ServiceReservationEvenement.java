package Services;

import entities.ReservationEvenement;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationEvenement implements IService<ReservationEvenement> {
    private Connection con;

    public ServiceReservationEvenement() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(ReservationEvenement reservation) throws SQLException {
        String req = "INSERT INTO reservationevenement(id_etudiant, id_evenement, domaine, statut, date) " +
                "VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getId_etudiant());
        ps.setInt(2, reservation.getId_evenement());
        ps.setString(3, reservation.getDomaine());
        ps.setString(4, reservation.getStatut());
        ps.setString(5, reservation.getDate());
        ps.executeUpdate();
        System.out.println("Réservation ajoutée");
    }

    @Override
    public void modifier(ReservationEvenement reservation) throws SQLException {
        String req = "UPDATE reservationevenement SET id_etudiant=?, id_evenement=?, domaine=?, statut=?, date=? " +
                "WHERE id_reservation=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getId_etudiant());
        ps.setInt(2, reservation.getId_evenement());
        ps.setString(3, reservation.getDomaine());
        ps.setString(4, reservation.getStatut());
        ps.setString(5, reservation.getDate());
        ps.setInt(6, reservation.getId_reservation());
        ps.executeUpdate();
        System.out.println("Réservation modifiée");
    }

    @Override
    public boolean supprimer(ReservationEvenement reservation) throws SQLException {
        String req = "DELETE FROM reservationevenement WHERE id_reservation=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getId_reservation());
        ps.executeUpdate();
        System.out.println("Réservation supprimée");
        return false;
    }

    @Override
    public List<ReservationEvenement> recuperer() throws SQLException {
        List<ReservationEvenement> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservationevenement";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            int id = rs.getInt("id_reservation");
            int idEtudiant = rs.getInt("id_etudiant");
            int idEvenement = rs.getInt("id_evenement");
            String domaine = rs.getString("domaine");
            String statut = rs.getString("statut");
            String date = rs.getString("date");
            ReservationEvenement r = new ReservationEvenement(id, idEtudiant, idEvenement, domaine, statut, date);
            reservations.add(r);
        }
        return reservations;
    }
}
