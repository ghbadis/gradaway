package Services;

import entities.ReservationVol;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceReservationVol {
    private Connection connection;

    public serviceReservationVol(Connection connection) {
        this.connection = connection;
    }

    public List<ReservationVol> getAllReservations() {
        List<ReservationVol> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation_vol";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                ReservationVol reservation = new ReservationVol(
                        rs.getInt("id_reservation"),
                        rs.getInt("id_vol"),
                        rs.getInt("id_etudiant"),
                        rs.getTimestamp("date_reservation") != null ? rs.getTimestamp("date_reservation").toLocalDateTime() : null,
                        rs.getInt("nombre_places"),
                        rs.getDouble("prix_total"),
                        rs.getString("statut_paiement"),
                        rs.getString("classe"),
                        rs.getString("type_bagage"),
                        rs.getString("commentaires"),
                        rs.getString("reference_reservation")
                );
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public void ajouterReservation(ReservationVol reservation) {
        String sql = "INSERT INTO reservation_vol (id_vol, id_etudiant, date_reservation, nombre_places, prix_total, statut_paiement, classe, type_bagage, commentaires, reference_reservation) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservation.getIdVol());
            stmt.setInt(2, reservation.getIdEtudiant());
            if (reservation.getDateReservation() != null)
                stmt.setTimestamp(3, Timestamp.valueOf(reservation.getDateReservation()));
            else
                stmt.setNull(3, Types.TIMESTAMP);
            stmt.setInt(4, reservation.getNombrePlaces());
            stmt.setDouble(5, reservation.getPrixTotal());
            stmt.setString(6, reservation.getStatutPaiement());
            stmt.setString(7, reservation.getClasse());
            stmt.setString(8, reservation.getTypeBagage());
            stmt.setString(9, reservation.getCommentaires());
            stmt.setString(10, reservation.getReferenceReservation());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimerReservation(int idReservation) {
        String sql = "DELETE FROM reservation_vol WHERE id_reservation = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idReservation);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifierReservation(ReservationVol reservation) {
        String sql = "UPDATE reservation_vol SET id_vol=?, id_etudiant=?, date_reservation=?, nombre_places=?, prix_total=?, statut_paiement=?, classe=?, type_bagage=?, commentaires=?, reference_reservation=? WHERE id_reservation=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reservation.getIdVol());
            stmt.setInt(2, reservation.getIdEtudiant());
            if (reservation.getDateReservation() != null)
                stmt.setTimestamp(3, Timestamp.valueOf(reservation.getDateReservation()));
            else
                stmt.setNull(3, Types.TIMESTAMP);
            stmt.setInt(4, reservation.getNombrePlaces());
            stmt.setDouble(5, reservation.getPrixTotal());
            stmt.setString(6, reservation.getStatutPaiement());
            stmt.setString(7, reservation.getClasse());
            stmt.setString(8, reservation.getTypeBagage());
            stmt.setString(9, reservation.getCommentaires());
            stmt.setString(10, reservation.getReferenceReservation());
            stmt.setInt(11, reservation.getIdReservation());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}