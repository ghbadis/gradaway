package Services;

import entities.Vols;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceVols implements IService<Vols> {
    private final Connection connection;

    public serviceVols() {
        this.connection = MyDatabase.getInstance().getCnx();    }

    @Override
    public void ajouter(Vols vol) throws SQLException {
        String sql = "INSERT INTO vols (compagnie, numero_vol, aeroport_depart, aeroport_arrivee, " +
                "ville_depart, ville_arrivee, pays_depart, pays_arrivee, date_depart, " +
                "date_arrivee, duree, prix_standard, places_disponibles, statut) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, vol.getCompagnie());
            ps.setString(2, vol.getNumeroVol());
            ps.setString(3, vol.getAeroportDepart());
            ps.setString(4, vol.getAeroportArrivee());
            ps.setString(5, vol.getVilleDepart());
            ps.setString(6, vol.getVilleArrivee());
            ps.setString(7, vol.getPaysDepart());
            ps.setString(8, vol.getPaysArrivee());
            ps.setTimestamp(9, new Timestamp(vol.getDateDepart().getTime()));
            ps.setTimestamp(10, new Timestamp(vol.getDateArrivee().getTime()));
            ps.setObject(11, vol.getDuree(), Types.INTEGER);
            ps.setDouble(12, vol.getPrixStandard());
            ps.setInt(13, vol.getPlacesDisponibles());
            ps.setString(14, vol.getStatut());

            ps.executeUpdate();

            // Récupérer l'ID auto-généré
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vol.setIdVol(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void modifier(Vols vol) throws SQLException {
        String sql = "UPDATE vols SET compagnie = ?, numero_vol = ?, aeroport_depart = ?, " +
                "aeroport_arrivee = ?, ville_depart = ?, ville_arrivee = ?, pays_depart = ?, " +
                "pays_arrivee = ?, date_depart = ?, date_arrivee = ?, duree = ?, " +
                "prix_standard = ?, places_disponibles = ?, statut = ? WHERE id_vol = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, vol.getCompagnie());
            ps.setString(2, vol.getNumeroVol());
            ps.setString(3, vol.getAeroportDepart());
            ps.setString(4, vol.getAeroportArrivee());
            ps.setString(5, vol.getVilleDepart());
            ps.setString(6, vol.getVilleArrivee());
            ps.setString(7, vol.getPaysDepart());
            ps.setString(8, vol.getPaysArrivee());
            ps.setTimestamp(9, new Timestamp(vol.getDateDepart().getTime()));
            ps.setTimestamp(10, new Timestamp(vol.getDateArrivee().getTime()));
            ps.setObject(11, vol.getDuree(), Types.INTEGER);
            ps.setDouble(12, vol.getPrixStandard());
            ps.setInt(13, vol.getPlacesDisponibles());
            ps.setString(14, vol.getStatut());
            ps.setInt(15, vol.getIdVol());

            ps.executeUpdate();
        }
    }

    @Override
    public void supprimer(Vols vol) throws SQLException {
        String sql = "DELETE FROM vols WHERE id_vol = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, vol.getIdVol());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Vols> recuperer() throws SQLException {
        List<Vols> vols = new ArrayList<>();
        String sql = "SELECT * FROM vols";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Vols vol = new Vols(
                        rs.getInt("id_vol"),
                        rs.getString("compagnie"),
                        rs.getString("numero_vol"),
                        rs.getString("aeroport_depart"),
                        rs.getString("aeroport_arrivee"),
                        rs.getString("ville_depart"),
                        rs.getString("ville_arrivee"),
                        rs.getString("pays_depart"),
                        rs.getString("pays_arrivee"),
                        rs.getTimestamp("date_depart"),
                        rs.getTimestamp("date_arrivee"),
                        rs.getObject("duree", Integer.class),
                        rs.getDouble("prix_standard"),
                        rs.getInt("places_disponibles"),
                        rs.getString("statut")
                );
                vols.add(vol);
            }
        }

        return vols;
    }

    // Méthode supplémentaire pour rechercher un vol par ID
    public Vols recupererParId(int id) throws SQLException {
        String sql = "SELECT * FROM vols WHERE id_vol = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Vols(
                            rs.getInt("id_vol"),
                            rs.getString("compagnie"),
                            rs.getString("numero_vol"),
                            rs.getString("aeroport_depart"),
                            rs.getString("aeroport_arrivee"),
                            rs.getString("ville_depart"),
                            rs.getString("ville_arrivee"),
                            rs.getString("pays_depart"),
                            rs.getString("pays_arrivee"),
                            rs.getTimestamp("date_depart"),
                            rs.getTimestamp("date_arrivee"),
                            rs.getObject("duree", Integer.class),
                            rs.getDouble("prix_standard"),
                            rs.getInt("places_disponibles"),
                            rs.getString("statut")
                    );
                }
            }
        }

        return null;
    }
}