package Services;

import entities.Foyer;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceFoyer implements IService<Foyer> {
    private Connection con;

    public ServiceFoyer() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Foyer foyer) throws SQLException {
        String req = "INSERT INTO foyer(nom, adresse, ville, pays, nombre_de_chambre, capacite, image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, foyer.getNom());
            ps.setString(2, foyer.getAdresse());
            ps.setString(3, foyer.getVille());
            ps.setString(4, foyer.getPays());
            ps.setInt(5, foyer.getNombreDeChambre());
            ps.setInt(6, foyer.getCapacite());
            ps.setString(7, foyer.getImage()); // ajouter l'image
            ps.executeUpdate();
            System.out.println("Foyer ajouté avec image");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifier(Foyer foyer) throws SQLException {
        String req = "UPDATE foyer SET nom = ?, adresse = ?, ville = ?, pays = ?, nombre_de_chambre = ?, capacite = ?, image = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, foyer.getNom());
            ps.setString(2, foyer.getAdresse());
            ps.setString(3, foyer.getVille());
            ps.setString(4, foyer.getPays());
            ps.setInt(5, foyer.getNombreDeChambre());
            ps.setInt(6, foyer.getCapacite());
            ps.setString(7, foyer.getImage()); // modification de l'image
            ps.setInt(8, foyer.getIdFoyer());
            ps.executeUpdate();
            System.out.println("Foyer modifié avec image");
        }
    }

    @Override
    public boolean supprimer(Foyer foyer) throws SQLException {
        String req = "DELETE FROM foyer WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, foyer.getIdFoyer());
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Foyer> recuperer() throws SQLException {
        List<Foyer> foyers = new ArrayList<>();
        String req = "SELECT * FROM foyer";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                int idF = rs.getInt("id");
                String nomF = rs.getString("nom");
                String adresseF = rs.getString("adresse");
                String villeF = rs.getString("ville");
                String paysF = rs.getString("pays");
                int chambres = rs.getInt("nombre_de_chambre");
                int capacite = rs.getInt("capacite");
                String image = rs.getString("image"); // récupérer l'image aussi

                Foyer foyer = new Foyer(idF, nomF, adresseF, villeF, paysF, chambres, capacite, image);
                foyers.add(foyer);
            }
        }
        return foyers;
    }

    public Foyer getFoyerById(int idFoyer) throws SQLException {
        String req = "SELECT * FROM foyer WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, idFoyer);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nom = rs.getString("nom");
                    String adresse = rs.getString("adresse");
                    String ville = rs.getString("ville");
                    String pays = rs.getString("pays");
                    int nombreDeChambre = rs.getInt("nombre_de_chambre");
                    int capacite = rs.getInt("capacite");
                    String image = rs.getString("image");

                    return new Foyer(idFoyer, nom, adresse, ville, pays, nombreDeChambre, capacite, image);
                }
            }
        }
        return null;
    }
}
