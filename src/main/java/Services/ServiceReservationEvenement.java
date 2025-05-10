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
        String req = "INSERT INTO reservationevenement(id_etudiant, id_evenement, email, nom, prenom, date) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getId_etudiant());
        ps.setInt(2, reservation.getId_evenement());
        ps.setString(3, reservation.getEmail());
        ps.setString(4, reservation.getNom());
        ps.setString(5, reservation.getPrenom());
        ps.setString(6, reservation.getDate());
        ps.executeUpdate();
        System.out.println("Réservation ajoutée");
    }

    @Override
    public void modifier(ReservationEvenement reservation) throws SQLException {
        String req = "UPDATE reservationevenement SET id_etudiant=?, id_evenement=?, email=?, nom=?, prenom=?, date=? " +
                "WHERE id_reservation=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getId_etudiant());
        ps.setInt(2, reservation.getId_evenement());
        ps.setString(3, reservation.getEmail());
        ps.setString(4, reservation.getNom());
        ps.setString(5, reservation.getPrenom());
        ps.setString(6, reservation.getDate());
        ps.setInt(7, reservation.getId_reservation());
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
            String email = rs.getString("email");
            String nom = rs.getString("nom");
            String prenom = rs.getString("prenom");
            String date = rs.getString("date");
            ReservationEvenement r = new ReservationEvenement(id, idEtudiant, idEvenement, email, nom, prenom, date);
            reservations.add(r);
        }
        return reservations;
    }

    public List<ReservationEvenement> recupererParEmail(String email) throws SQLException {
        List<ReservationEvenement> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservationevenement WHERE email = ?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int id = rs.getInt("id_reservation");
            int idEtudiant = rs.getInt("id_etudiant");
            int idEvenement = rs.getInt("id_evenement");
            String emailReservation = rs.getString("email");
            String nom = rs.getString("nom");
            String prenom = rs.getString("prenom");
            String date = rs.getString("date");
            ReservationEvenement r = new ReservationEvenement(id, idEtudiant, idEvenement, emailReservation, nom, prenom, date);
            reservations.add(r);
        }
        return reservations;
    }
}
