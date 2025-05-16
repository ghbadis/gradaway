package Services;

import entities.Vols;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceVols {
    private Connection connection;

    public serviceVols(Connection connection) {
        this.connection = connection;
    }

    public List<Vols> getAllVols() {
        List<Vols> volsList = new ArrayList<>();
        String sql = "SELECT * FROM vols";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Vols vol = new Vols(
                        rs.getInt("id_vol"),
                        rs.getString("numero_vol"),
                        rs.getString("compagnie"),
                        rs.getString("aeroport_depart"),
                        rs.getString("ville_depart"),
                        rs.getString("pays_depart"),
                        rs.getString("aeroport_arrivee"),
                        rs.getString("ville_arrivee"),
                        rs.getString("pays_arrivee"),
                        rs.getTimestamp("date_depart") != null ? rs.getTimestamp("date_depart").toLocalDateTime() : null,
                        rs.getTimestamp("date_arrivee") != null ? rs.getTimestamp("date_arrivee").toLocalDateTime() : null,
                        rs.getObject("duree") != null ? rs.getInt("duree") : null,
                        rs.getDouble("prix_standard"),
                        rs.getInt("places_disponibles"),
                        rs.getString("statut"),
                        rs.getString("image_path")
                );
                volsList.add(vol);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return volsList;
    }


    // Add these methods to your existing serviceVols class

    public Vols getVolById(int idVol) {
        String sql = "SELECT * FROM vols WHERE id_vol = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Vols(
                        rs.getInt("id_vol"),
                        rs.getString("numero_vol"),
                        rs.getString("compagnie"),
                        rs.getString("aeroport_depart"),
                        rs.getString("ville_depart"),
                        rs.getString("pays_depart"),
                        rs.getString("aeroport_arrivee"),
                        rs.getString("ville_arrivee"),
                        rs.getString("pays_arrivee"),
                        rs.getTimestamp("date_depart") != null ? rs.getTimestamp("date_depart").toLocalDateTime() : null,
                        rs.getTimestamp("date_arrivee") != null ? rs.getTimestamp("date_arrivee").toLocalDateTime() : null,
                        rs.getObject("duree") != null ? rs.getInt("duree") : null,
                        rs.getDouble("prix_standard"),
                        rs.getInt("places_disponibles"),
                        rs.getString("statut"),
                        rs.getString("image_path")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Vols getVolByNumero(String numeroVol) {
        String sql = "SELECT * FROM vols WHERE numero_vol = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numeroVol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Vols(
                        rs.getInt("id_vol"),
                        rs.getString("numero_vol"),
                        rs.getString("compagnie"),
                        rs.getString("aeroport_depart"),
                        rs.getString("ville_depart"),
                        rs.getString("pays_depart"),
                        rs.getString("aeroport_arrivee"),
                        rs.getString("ville_arrivee"),
                        rs.getString("pays_arrivee"),
                        rs.getTimestamp("date_depart") != null ? rs.getTimestamp("date_depart").toLocalDateTime() : null,
                        rs.getTimestamp("date_arrivee") != null ? rs.getTimestamp("date_arrivee").toLocalDateTime() : null,
                        rs.getObject("duree") != null ? rs.getInt("duree") : null,
                        rs.getDouble("prix_standard"),
                        rs.getInt("places_disponibles"),
                        rs.getString("statut"),
                        rs.getString("image_path")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public void ajouterVol(Vols vol) {
        String sql = "INSERT INTO vols (numero_vol, compagnie, aeroport_depart, ville_depart, pays_depart, aeroport_arrivee, ville_arrivee, pays_arrivee, date_depart, date_arrivee, duree, prix_standard, places_disponibles, statut, image_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vol.getNumeroVol());
            stmt.setString(2, vol.getCompagnie());
            stmt.setString(3, vol.getAeroportDepart());
            stmt.setString(4, vol.getVilleDepart());
            stmt.setString(5, vol.getPaysDepart());
            stmt.setString(6, vol.getAeroportArrivee());
            stmt.setString(7, vol.getVilleArrivee());
            stmt.setString(8, vol.getPaysArrivee());
            if (vol.getDateDepart() != null)
                stmt.setTimestamp(9, Timestamp.valueOf(vol.getDateDepart()));
            else
                stmt.setNull(9, Types.TIMESTAMP);
            if (vol.getDateArrivee() != null)
                stmt.setTimestamp(10, Timestamp.valueOf(vol.getDateArrivee()));
            else
                stmt.setNull(10, Types.TIMESTAMP);
            if (vol.getDuree() != null)
                stmt.setInt(11, vol.getDuree());
            else
                stmt.setNull(11, Types.INTEGER);
            stmt.setDouble(12, vol.getPrixStandard());
            stmt.setInt(13, vol.getPlacesDisponibles());
            stmt.setString(14, vol.getStatut());
            stmt.setString(15, vol.getImagePath());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifierVol(Vols vol) {
        String sql = "UPDATE vols SET numero_vol=?, compagnie=?, aeroport_depart=?, ville_depart=?, pays_depart=?, aeroport_arrivee=?, ville_arrivee=?, pays_arrivee=?, date_depart=?, date_arrivee=?, duree=?, prix_standard=?, places_disponibles=?, statut=?, image_path=? WHERE id_vol=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, vol.getNumeroVol());
            stmt.setString(2, vol.getCompagnie());
            stmt.setString(3, vol.getAeroportDepart());
            stmt.setString(4, vol.getVilleDepart());
            stmt.setString(5, vol.getPaysDepart());
            stmt.setString(6, vol.getAeroportArrivee());
            stmt.setString(7, vol.getVilleArrivee());
            stmt.setString(8, vol.getPaysArrivee());
            if (vol.getDateDepart() != null)
                stmt.setTimestamp(9, Timestamp.valueOf(vol.getDateDepart()));
            else
                stmt.setNull(9, Types.TIMESTAMP);
            if (vol.getDateArrivee() != null)
                stmt.setTimestamp(10, Timestamp.valueOf(vol.getDateArrivee()));
            else
                stmt.setNull(10, Types.TIMESTAMP);
            if (vol.getDuree() != null)
                stmt.setInt(11, vol.getDuree());
            else
                stmt.setNull(11, Types.INTEGER);
            stmt.setDouble(12, vol.getPrixStandard());
            stmt.setInt(13, vol.getPlacesDisponibles());
            stmt.setString(14, vol.getStatut());
            stmt.setString(15, vol.getImagePath());
            stmt.setInt(16, vol.getIdVol());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimerVol(int idVol) {
        String sql = "DELETE FROM vols WHERE id_vol=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVol);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour récupérer tous les numéros de vols
    public List<String> getAllNumeroVols() {
        List<String> numerosVol = new ArrayList<>();
        String sql = "SELECT numero_vol FROM vols";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                numerosVol.add(rs.getString("numero_vol"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return numerosVol;
    }

    // Méthode pour récupérer l'ID d'un vol par son numéro
    public int getIdVolByNumero(String numeroVol) {
        String sql = "SELECT id_vol FROM vols WHERE numero_vol = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, numeroVol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_vol");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retourne -1 si le vol n'est pas trouvé
    }

    // Méthode pour récupérer le prix d'un vol par son ID
    public double getPrixVolById(int idVol) {
        String sql = "SELECT prix_standard FROM vols WHERE id_vol = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("prix_standard");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Retourne 0.0 si le vol n'est pas trouvé
    }

    public String getNumeroVolById(int idVol) {
        String sql = "SELECT numero_vol FROM vols WHERE id_vol = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idVol);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("numero_vol");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}