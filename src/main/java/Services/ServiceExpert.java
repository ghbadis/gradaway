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
        // Vérifier que les champs obligatoires ne sont pas null
        if (expert.getNom_expert() == null || expert.getPrenom_expert() == null ||
                expert.getEmail() == null || expert.getSpecialite() == null) {
            throw new SQLException("Les champs nom, prénom, email et spécialité sont obligatoires");
        }

        try {
            // Insérer dans la table expert
            String expertReq = "INSERT INTO expert (nom_expert, prenom_expert, email, specialite, telephone, annee_experience, photo_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement expertPs = con.prepareStatement(expertReq, Statement.RETURN_GENERATED_KEYS);
            expertPs.setString(1, expert.getNom_expert());
            expertPs.setString(2, expert.getPrenom_expert());
            expertPs.setString(3, expert.getEmail());
            expertPs.setString(4, expert.getSpecialite());

            // Gestion des champs optionnels
            if (expert.getTelephone() != null && !expert.getTelephone().isEmpty()) {
                expertPs.setString(5, expert.getTelephone());
            } else {
                expertPs.setNull(5, Types.VARCHAR);
            }

            if (expert.getAnneeExperience() > 0) {
                expertPs.setInt(6, expert.getAnneeExperience());
            } else {
                expertPs.setNull(6, Types.INTEGER);
            }

            // Add photo_path
            if (expert.getPhotoPath() != null && !expert.getPhotoPath().isEmpty()) {
                expertPs.setString(7, expert.getPhotoPath());
            } else {
                expertPs.setNull(7, Types.VARCHAR);
            }

            expertPs.executeUpdate();

            // Récupérer l'id_expert généré
            ResultSet expertRs = expertPs.getGeneratedKeys();
            if (expertRs.next()) {
                expert.setId_expert(expertRs.getInt(1));
            }

            System.out.println("Expert ajouté avec succès");

        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void modifier(Expert expert) throws SQLException {
        String req = "UPDATE expert SET nom_expert = ?, prenom_expert = ?, email = ?, specialite = ?, telephone = ?, annee_experience = ?, photo_path = ? WHERE id_expert = ?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, expert.getNom_expert());
        ps.setString(2, expert.getPrenom_expert());
        ps.setString(3, expert.getEmail());
        ps.setString(4, expert.getSpecialite());

        if (expert.getTelephone() != null && !expert.getTelephone().isEmpty()) {
            ps.setString(5, expert.getTelephone());
        } else {
            ps.setNull(5, Types.VARCHAR);
        }

        if (expert.getAnneeExperience() > 0) {
            ps.setInt(6, expert.getAnneeExperience());
        } else {
            ps.setNull(6, Types.INTEGER);
        }

        // Set photo_path
        if (expert.getPhotoPath() != null && !expert.getPhotoPath().isEmpty()) {
            ps.setString(7, expert.getPhotoPath());
        } else {
            ps.setNull(7, Types.VARCHAR);
        }

        ps.setInt(8, expert.getId_expert());
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
            Expert e = new Expert();
            e.setId_expert(rs.getInt("id_expert"));
            e.setNom_expert(rs.getString("nom_expert"));
            e.setPrenom_expert(rs.getString("prenom_expert"));
            e.setEmail(rs.getString("email"));
            e.setSpecialite(rs.getString("specialite"));

            String telephone = rs.getString("telephone");
            if (!rs.wasNull()) {
                e.setTelephone(telephone);
            }

            int anneeExp = rs.getInt("annee_experience");
            if (!rs.wasNull()) {
                e.setAnneeExperience(anneeExp);
            }

            String photoPath = rs.getString("photo_path");
            if (!rs.wasNull()) {
                e.setPhotoPath(photoPath);
            }

            experts.add(e);
        }
        return experts;
    }

    public List<String> recupererDomaines() throws SQLException {
        List<String> domaines = new ArrayList<>();
        String req = "SELECT DISTINCT domaine FROM universite WHERE domaine IS NOT NULL ORDER BY domaine";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(req)) {
            while (rs.next()) {
                String domaine = rs.getString("domaine");
                if (domaine != null && !domaine.trim().isEmpty()) {
                    domaines.add(domaine);
                }
            }
        }
        return domaines;
    }
}
