package Services;


import entities.Dossier;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceDossier implements IService<Dossier> {
    private Connection con;
    public ServiceDossier() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Dossier dossier) throws SQLException {
        String req = "INSERT INTO dossier (id_etudiant, cin, photo, diplome_baccalauréat, releve_note, " +
                "diplome_obtenus, lettre_motivations, dossier_sante, cv, datedepot) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, dossier.getId_etudiant());
        ps.setString(2, dossier.getCin());
        ps.setString(3, dossier.getPhoto());
        ps.setString(4, dossier.getDiplome_baccalauréat());
        ps.setString(5, dossier.getReleve_note());
        ps.setString(6, dossier.getDiplome_obtenus());
        ps.setString(7, dossier.getLettre_motivations());
        ps.setString(8, dossier.getDossier_sante());
        ps.setString(9, dossier.getCv());
        ps.setDate(10, Date.valueOf(dossier.getDatedepot()));
        ps.executeUpdate();
        System.out.println("Dossier ajouté");

    }

    @Override
    public void modifier(Dossier dossier) throws SQLException {
        String req = "UPDATE dossier SET id_etudiant=?, cin=?, photo=?, diplome_baccalauréat=?, releve_note=?, " +
                "diplome_obtenus=?, lettre_motivations=?, dossier_sante=?, cv=?, datedepot=? WHERE id_dossier=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, dossier.getId_etudiant());
        ps.setString(2, dossier.getCin());
        ps.setString(3, dossier.getPhoto());
        ps.setString(4, dossier.getDiplome_baccalauréat());
        ps.setString(5, dossier.getReleve_note());
        ps.setString(6, dossier.getDiplome_obtenus());
        ps.setString(7, dossier.getLettre_motivations());
        ps.setString(8, dossier.getDossier_sante());
        ps.setString(9, dossier.getCv());
        ps.setDate(10, Date.valueOf(dossier.getDatedepot()));
        ps.setInt(11, dossier.getId_dossier());
        ps.executeUpdate();
        System.out.println("Dossier modifié");

    }

    @Override
    public void supprimer(Dossier dossier) throws SQLException {
        String req = "DELETE FROM dossier WHERE id_dossier=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, dossier.getId_dossier());
        ps.executeUpdate();
        System.out.println("Dossier supprimé");

    }

    @Override
    public List<Dossier> recuperer() throws SQLException {
        List<Dossier> dossiers = new ArrayList<>();
        String req = "SELECT * FROM dossier";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            dossiers.add(mapResultSetToDossier(rs));
        }

        return dossiers;
    }

    // New method to get Dossier by id_etudiant
    public Dossier recupererParEtudiantId(int id_etudiant) throws SQLException {
        String req = "SELECT * FROM dossier WHERE id_etudiant = ? LIMIT 1"; // Limit 1 assuming one dossier per student
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, id_etudiant);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return mapResultSetToDossier(rs);
        }
        return null; // Return null if no dossier found for this student
    }

    // Helper method to map ResultSet to Dossier object
    private Dossier mapResultSetToDossier(ResultSet rs) throws SQLException {
        int id = rs.getInt("id_dossier");
        int idEtudiant = rs.getInt("id_etudiant");
        String cin = rs.getString("cin");
        String photo = rs.getString("photo");
        String bac = rs.getString("diplome_baccalauréat");
        String releve = rs.getString("releve_note");
        String diplomes = rs.getString("diplome_obtenus");
        String lettre = rs.getString("lettre_motivations");
        String sante = rs.getString("dossier_sante");
        String cv = rs.getString("cv");
        LocalDate dateDepot = rs.getDate("datedepot").toLocalDate();

        return new Dossier(id, idEtudiant, cin, photo, bac, releve, diplomes, lettre, sante, cv, dateDepot);
    }
}
