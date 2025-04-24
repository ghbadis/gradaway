package Services;




import entities.Foyer;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceFoyer implements IService<Foyer> {
    private Connection con;

    public ServiceFoyer() {
        con = MyDatabase.getInstance().getCnx();  // ✅ initialisation de la connexion
    }

    @Override
    public void ajouter(Foyer foyer) throws SQLException {

        String req = "INSERT INTO foyer(nom, adresse, ville, pays, nombre_de_chambre, capacite) " +
                "VALUES ('" + foyer.getNom() + "', '" + foyer.getAdresse() + "', '" + foyer.getVille() +
                "', '" + foyer.getPays() + "', " + foyer.getNombreDeChambre() + ", " + foyer.getCapacite() + ")";
        try {Statement st = con.createStatement();
            st.executeUpdate(req);
            System.out.println("Foyer ajouté");
        } catch (SQLException e) {
            e.printStackTrace();
        }
       /* Statement st = con.createStatement();
        st.executeUpdate(req);
        System.out.println("Foyer ajouté");*/
    }


    @Override
    public void modifier(Foyer foyer) throws SQLException {
        String req = "UPDATE foyer SET nom = ?, adresse = ?, ville = ?, pays = ?, nombre_de_chambre = ?, capacite = ? WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, foyer.getNom());
        ps.setString(2, foyer.getAdresse());
        ps.setString(3, foyer.getVille());
        ps.setString(4, foyer.getPays());
        ps.setInt(5, foyer.getNombreDeChambre());
        ps.setInt(6, foyer.getCapacite());
        ps.setInt(7, foyer.getIdFoyer());
        ps.executeUpdate();
        System.out.println("Foyer modifié");
    }

    @Override
    public void supprimer(Foyer foyer) throws SQLException {
        String req = "DELETE FROM foyer WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, foyer.getIdFoyer());
        ps.executeUpdate();
        System.out.println("Foyer supprimé");

    }
    @Override
    public List<Foyer> recuperer() throws SQLException {
        List<Foyer> foyers = new ArrayList<>();
        String req = "SELECT * FROM foyer";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int idF = rs.getInt("id");
            String nomF = rs.getString("nom");
            String adresseF = rs.getString("adresse");
            String villeF = rs.getString("ville");
            String paysF = rs.getString("pays");
            int chambres = rs.getInt("nombre_de_chambre");
            int capacite = rs.getInt("capacite");

            Foyer foyer = new Foyer(idF, nomF, adresseF, villeF, paysF, chambres, capacite);
            foyers.add(foyer);
        }

        return foyers;
    }

    }
