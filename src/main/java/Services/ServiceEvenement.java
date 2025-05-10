package Services;

import entities.Evenement;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvenement implements IService<Evenement> {
    private Connection con;

    public ServiceEvenement() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Evenement evenement) throws SQLException {
        String req = "INSERT INTO evenement(nom, description, date, lieu, domaine, places_disponibles, image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, evenement.getNom());
        ps.setString(2, evenement.getDescription());
        ps.setString(3, evenement.getDate());
        ps.setString(4, evenement.getLieu());
        ps.setString(5, evenement.getDomaine());
        ps.setInt(6, evenement.getPlaces_disponibles());
        ps.setString(7, evenement.getImage());
        ps.executeUpdate();
        System.out.println("Événement ajouté");
    }

    @Override
    public void modifier(Evenement evenement) throws SQLException {
        String req = "UPDATE evenement SET nom=?, description=?, date=?, lieu=?, domaine=?, places_disponibles=?, image=? WHERE id_evenement=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, evenement.getNom());
        ps.setString(2, evenement.getDescription());
        ps.setString(3, evenement.getDate());
        ps.setString(4, evenement.getLieu());
        ps.setString(5, evenement.getDomaine());
        ps.setInt(6, evenement.getPlaces_disponibles());
        ps.setString(7, evenement.getImage());
        ps.setInt(8, evenement.getId_evenement());
        ps.executeUpdate();
        System.out.println("Événement modifié");
    }

    @Override
    public boolean supprimer(Evenement evenement) throws SQLException {
        String req = "DELETE FROM evenement WHERE id_evenement=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, evenement.getId_evenement());
        ps.executeUpdate();
        System.out.println("Événement supprimé");
        return false;
    }

    @Override
    public List<Evenement> recuperer() throws SQLException {
        List<Evenement> evenements = new ArrayList<>();
        String req = "SELECT * FROM evenement";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            int id = rs.getInt("id_evenement");
            String nom = rs.getString("nom");
            String description = rs.getString("description");
            String date = rs.getString("date");
            String lieu = rs.getString("lieu");
            String domaine = rs.getString("domaine");
            int places = rs.getInt("places_disponibles");
            String image = rs.getString("image");
            Evenement e = new Evenement(id, nom, description, date, lieu, domaine, places, image);
            evenements.add(e);
        }
        return evenements;
    }
}
