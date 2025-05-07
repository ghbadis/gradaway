package Services;

import entities.Universite;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceUniversite {

    private Connection con = MyDatabase.getInstance().getCnx();

    public List<Universite> getAllUniversites() throws SQLException {
        List<Universite> universites = new ArrayList<>();
        String query = "SELECT * FROM universite";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Universite u = new Universite(
                    rs.getInt("id_universite"),
                    rs.getString("Nom"),
                    rs.getString("Ville"),
                    rs.getString("Adresse_universite"),
                    rs.getString("Domaine"),
                    rs.getDouble("Frais"),
                    rs.getString("photo_path")
                );
                universites.add(u);
            }
        }
        return universites;
    }

    public List<Universite> searchUniversites(String searchText) throws SQLException {
        List<Universite> universites = new ArrayList<>();
        String query = "SELECT * FROM universite WHERE LOWER(Nom) LIKE ?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, "%" + searchText.toLowerCase() + "%");
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    Universite u = new Universite(
                        rs.getInt("id_universite"),
                        rs.getString("Nom"),
                        rs.getString("Ville"),
                        rs.getString("Adresse_universite"),
                        rs.getString("Domaine"),
                        rs.getDouble("Frais"),
                        rs.getString("photo_path")
                    );
                    universites.add(u);
                }
            }
        }
        return universites;
    }

    public void ajouter(Universite universite) {
        String query = "INSERT INTO universite (Nom, Ville, Adresse_universite, Domaine, Frais, photo_path) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, universite.getNom());
            st.setString(2, universite.getVille());
            st.setString(3, universite.getAdresse_universite());
            st.setString(4, universite.getDomaine());
            st.setDouble(5, universite.getFrais());
            st.setString(6, universite.getPhotoPath());
            
            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("L'ajout de l'université a échoué, aucune ligne insérée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout de l'université: " + e.getMessage());
        }
    }

    public void modifier(Universite universite) {
        String query = "UPDATE universite SET Nom = ?, Ville = ?, Adresse_universite = ?, Domaine = ?, Frais = ?, photo_path = ? WHERE id_universite = ?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, universite.getNom());
            st.setString(2, universite.getVille());
            st.setString(3, universite.getAdresse_universite());
            st.setString(4, universite.getDomaine());
            st.setDouble(5, universite.getFrais());
            st.setString(6, universite.getPhotoPath());
            st.setInt(7, universite.getId_universite());
            
            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("La modification de l'université a échoué, aucune ligne mise à jour.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la modification de l'université: " + e.getMessage());
        }
    }

    public void supprimer(Universite universite) {
        String query = "DELETE FROM universite WHERE id_universite = ?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setInt(1, universite.getId_universite());
            
            int rowsAffected = st.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("La suppression de l'université a échoué, aucune ligne supprimée.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression de l'université: " + e.getMessage());
        }
    }

    public Universite recuperer(int id) throws SQLException {
        String query = "SELECT * FROM universite WHERE id_universite = ?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return new Universite(
                        rs.getInt("id_universite"),
                        rs.getString("Nom"),
                        rs.getString("Ville"),
                        rs.getString("Adresse_universite"),
                        rs.getString("Domaine"),
                        rs.getDouble("Frais"),
                        rs.getString("photo_path")
                    );
                }
            }
        }
        return null; // Return null if no university is found with the given ID
    }
}
