package Services;

import entities.Entretien;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEntretien implements IService<Entretien> {
    private Connection con;

    public ServiceEntretien() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Entretien entretien) throws SQLException {
        String req = "INSERT INTO entretien (id_expert, id_user, date_entretien, heure_entretien, etat_entretien, type_entretien, offre) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, entretien.getId_expert());
        ps.setInt(2, entretien.getId_user());
        ps.setDate(3, Date.valueOf(entretien.getDate_entretien()));
        ps.setTime(4, Time.valueOf(entretien.getHeure_entretien()));
        ps.setString(5, entretien.getEtat_entretien());
        ps.setString(6, entretien.getType_entretien());
        ps.setString(7, entretien.getOffre());
        ps.executeUpdate();
        System.out.println("Entretien ajouté");
    }

    @Override
    public void modifier(Entretien entretien) throws SQLException {
        String req = "UPDATE entretien SET id_expert=?, id_user=?, date_entretien=?, heure_entretien=?, etat_entretien=?, type_entretien=?, offre=? WHERE id_entretien=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, entretien.getId_expert());
        ps.setInt(2, entretien.getId_user());
        ps.setDate(3, Date.valueOf(entretien.getDate_entretien()));
        ps.setTime(4, Time.valueOf(entretien.getHeure_entretien()));
        ps.setString(5, entretien.getEtat_entretien());
        ps.setString(6, entretien.getType_entretien());
        ps.setString(7, entretien.getOffre());
        ps.setInt(8, entretien.getId_entretien());
        ps.executeUpdate();
        System.out.println("Entretien modifié");
    }

    @Override
    public boolean supprimer(Entretien entretien) throws SQLException {
        String req = "DELETE FROM entretien WHERE id_entretien=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, entretien.getId_entretien());
        ps.executeUpdate();
        System.out.println("Entretien supprimé");
        return false;
    }

    @Override
    public List<Entretien> recuperer() throws SQLException {
        List<Entretien> entretiens = new ArrayList<>();
        String req = "SELECT * FROM entretien";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            Entretien entretien = new Entretien(
                    rs.getInt("id_entretien"),
                    rs.getInt("id_expert"),
                    rs.getInt("id_user"),
                    rs.getDate("date_entretien").toLocalDate(),
                    rs.getTime("heure_entretien").toLocalTime(),
                    rs.getString("etat_entretien"),
                    rs.getString("type_entretien"),
                    rs.getString("offre")
            );
            entretiens.add(entretien);
        }
        return entretiens;
    }
}
