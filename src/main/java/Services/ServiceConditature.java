package Services;

import entities.Conditature;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceConditature implements IService<Conditature> {
    private Connection con = MyDatabase.getInstance().getCnx();

    public ServiceConditature() {
    }



    public void ajouter(Conditature conditature) throws SQLException {
        String req = "INSERT INTO conditature(id_dossier, user_id, id_universite, date_de_remise_C, Domaine) " +
                "VALUES (" + conditature.getId_dossier() + ", " + conditature.getUser_id() + ", " +
                conditature.getId_universite() + ", '" + conditature.getDate_de_remise_C() + "', '" +
                conditature.getDomaine() + "')";
        Statement st = this.con.createStatement();
        st.executeUpdate(req);
        System.out.println("Conditature ajoutée");
    }

    public void modifier(Conditature conditature) throws SQLException {
        String req = "UPDATE conditature SET id_dossier=?, user_id=?, id_universite=?, date_de_remise_C=?, Domaine=? " +
                "WHERE id_c=?";
        PreparedStatement ps = this.con.prepareStatement(req);
        ps.setInt(1, conditature.getId_dossier());
        ps.setInt(2, conditature.getUser_id());
        ps.setInt(3, conditature.getId_universite());
        ps.setString(4, conditature.getDate_de_remise_C());
        ps.setString(5, conditature.getDomaine());
        ps.setInt(6, conditature.getId_c());
        ps.executeUpdate();
        System.out.println("Conditature modifiée");
    }

    public boolean supprimer(Conditature conditature) throws SQLException {
        String req = "DELETE FROM conditature WHERE id_c=?";
        PreparedStatement ps = this.con.prepareStatement(req);
        ps.setInt(1, conditature.getId_c());
        ps.executeUpdate();
        System.out.println("Conditature supprimée");
        return false;
    }


    public List<Conditature> recuperer() throws SQLException {
        List<Conditature> conditatures = new ArrayList<>();
        String req = "SELECT * FROM conditature";
        Statement st = this.con.createStatement();
        ResultSet rs = st.executeQuery(req);

        while(rs.next()) {
            int id_c = rs.getInt("id_c");
            int id_dossier = rs.getInt("id_dossier");
            int user_id = rs.getInt("user_id");
            int id_universite = rs.getInt("id_universite");
            String date_de_remise_C = rs.getString("date_de_remise_C");
            String Domaine = rs.getString("Domaine");

            Conditature c = new Conditature(id_c, id_dossier, user_id, id_universite, date_de_remise_C, Domaine);
            conditatures.add(c);
        }

        return conditatures;
    }
}