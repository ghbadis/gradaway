package Services;

import entities.Expert;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceExpert implements IService<Expert> {
    private Connection con;

    public ServiceExpert() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Expert expert) throws SQLException {
        String req = "INSERT INTO expert (id_user, nom_expert, prenom_expert, email, specialite) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, expert.getId_user());
        ps.setString(2, expert.getNom_expert());
        ps.setString(3, expert.getPrenom_expert());
        ps.setString(4, expert.getEmail());
        ps.setString(5, expert.getSpecialite());
        ps.executeUpdate();
        System.out.println("Expert ajouté avec succès");
    }

    @Override
    public void modifier(Expert expert) throws SQLException {
        String req = "UPDATE expert SET id_user = ?, nom_expert = ?, prenom_expert = ?, email = ?, specialite = ? WHERE id_expert = ?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, expert.getId_user());
        ps.setString(2, expert.getNom_expert());
        ps.setString(3, expert.getPrenom_expert());
        ps.setString(4, expert.getEmail());
        ps.setString(5, expert.getSpecialite());
        ps.setInt(6, expert.getId_expert());
        ps.executeUpdate();
        System.out.println("Expert modifié avec succès");
    }

    @Override
    public boolean supprimer(Expert expert) throws SQLException {
        String req = "DELETE FROM expert WHERE id_expert = ?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, expert.getId_expert());
        ps.executeUpdate();
        System.out.println("Expert supprimé avec succès");
        return false;
    }

    @Override
    public List<Expert> recuperer() throws SQLException {
        List<Expert> experts = new ArrayList<>();
        String req = "SELECT * FROM expert";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            Expert e = new Expert(
                    rs.getInt("id_expert"),
                    rs.getString("nom_expert"),
                    rs.getString("prenom_expert"),
                    rs.getString("email"),
                    rs.getString("specialite")
            );
            experts.add(e);
        }
        return experts;
    }
}
