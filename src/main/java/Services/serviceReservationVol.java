package Services;

import entities.ReservationVol;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceReservationVol implements IService<ReservationVol> {
    private final Connection connection;

    public serviceReservationVol() {
        this.connection = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(ReservationVol reservation) throws SQLException {
        String sql = "INSERT INTO reservations_vol (id_vol, id_etudiant, date_reservation, nombre_places, " +
                "prix_total, statut_paiement, classe, type_bagage, commentaires, reference_reservation) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getIdVol());
            ps.setInt(2, reservation.getIdEtudiant());
            ps.setTimestamp(3, new Timestamp(reservation.getDateReservation().getTime()));
            ps.setInt(4, reservation.getNombrePlaces());
            ps.setDouble(5, reservation.getPrixTotal());
            ps.setString(6, reservation.getStatutPaiement());
            ps.setString(7, reservation.getClasse());
            ps.setString(8, reservation.getTypeBagage());
            ps.setString(9, reservation.getCommentaires());
            ps.setString(10, reservation.getReferenceReservation());

            ps.executeUpdate();

            // Récupérer l'ID auto-généré
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setIdReservation(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void modifier(ReservationVol reservation) throws SQLException {
        String sql = "UPDATE reservation_vol SET id_vol = ?, id_etudiant = ?, date_reservation = ?, " +
                "nombre_places = ?, prix_total = ?, statut_paiement = ?, classe = ?, " +
                "type_bagage = ?, commentaires = ?, reference_reservation = ? WHERE id_reservation = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservation.getIdVol());
            ps.setInt(2, reservation.getIdEtudiant());
            ps.setTimestamp(3, new Timestamp(reservation.getDateReservation().getTime()));
            ps.setInt(4, reservation.getNombrePlaces());
            ps.setDouble(5, reservation.getPrixTotal());
            ps.setString(6, reservation.getStatutPaiement());
            ps.setString(7, reservation.getClasse());
            ps.setString(8, reservation.getTypeBagage());
            ps.setString(9, reservation.getCommentaires());
            ps.setString(10, reservation.getReferenceReservation());
            ps.setInt(11, reservation.getIdReservation());

            ps.executeUpdate();
        }
    }

    @Override
    public boolean supprimer(ReservationVol reservation) throws SQLException {
        String sql = "DELETE FROM reservation_vol WHERE id_reservation = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservation.getIdReservation());
            ps.executeUpdate();
        }
        return false;
    }

    @Override
    public List<ReservationVol> recuperer() throws SQLException {
        List<ReservationVol> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation_vol";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                ReservationVol reservation = new ReservationVol(
                        rs.getInt("id_reservation"),
                        rs.getInt("id_vol"),
                        rs.getInt("id_etudiant"),
                        rs.getTimestamp("date_reservation"),
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
        }
        return reservations;
    }

    public ReservationVol recupererParId(int id) throws SQLException {
        String sql = "SELECT * FROM reservations_vol WHERE id_reservation = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ReservationVol(
                            rs.getInt("id_reservation"),
                            rs.getInt("id_vol"),
                            rs.getInt("id_etudiant"),
                            rs.getTimestamp("date_reservation"),
                            rs.getInt("nombre_places"),
                            rs.getDouble("prix_total"),
                            rs.getString("statut_paiement"),
                            rs.getString("classe"),
                            rs.getString("type_bagage"),
                            rs.getString("commentaires"),
                            rs.getString("reference_reservation")
                    );
                }
            }
        }
        return null;
    }

    public List<ReservationVol> recupererParEtudiant(int idEtudiant) throws SQLException {
        List<ReservationVol> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation_vol WHERE id_etudiant = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEtudiant);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReservationVol reservation = new ReservationVol(
                            rs.getInt("id_reservation"),
                            rs.getInt("id_vol"),
                            rs.getInt("id_etudiant"),
                            rs.getTimestamp("date_reservation"),
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
            }
        }
        return reservations;
    }

    // Méthode supplémentaire pour récupérer les réservations d'un vol spécifique
    public List<ReservationVol> recupererParVol(int idVol) throws SQLException {
        List<ReservationVol> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation_vol WHERE id_vol = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idVol);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReservationVol reservation = new ReservationVol(
                            rs.getInt("id_reservation"),
                            rs.getInt("id_vol"),
                            rs.getInt("id_etudiant"),
                            rs.getTimestamp("date_reservation"),
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
            }
        }
        return reservations;
    }
}