package Services;

import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class ServiceDemandeEntretien {

    public void ajouter(int idUser, String domaine, LocalDate dateSouhaitee, 
                       LocalTime heureSouhaitee, String objet, String offre) throws SQLException {
        String query = "INSERT INTO demandes_entretien (id_user, domaine, date_souhaitee, " +
                      "heure_souhaitee, objet, offre, statut) VALUES (?, ?, ?, ?, ?, ?, 'en attente')";
        
        try (Connection con = MyDatabase.getInstance().getCnx();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idUser);
            ps.setString(2, domaine);
            ps.setDate(3, Date.valueOf(dateSouhaitee));
            ps.setTime(4, Time.valueOf(heureSouhaitee));
            ps.setString(5, objet);
            ps.setString(6, offre);
            ps.executeUpdate();
        }
    }

    public void accepterDemande(int idDemande) throws SQLException {
        String query = "UPDATE demandes_entretien SET statut = 'acceptée' WHERE id_demande = ?";
        try (Connection con = MyDatabase.getInstance().getCnx();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idDemande);
            ps.executeUpdate();
        }
    }

    public void refuserDemande(int idDemande) throws SQLException {
        String query = "UPDATE demandes_entretien SET statut = 'refusée' WHERE id_demande = ?";
        try (Connection con = MyDatabase.getInstance().getCnx();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idDemande);
            ps.executeUpdate();
        }
    }

    public String getEmailUser(int idDemande) throws SQLException {
        String query = "SELECT u.email FROM user u " +
                      "JOIN demandes_entretien d ON u.id = d.id_user " +
                      "WHERE d.id_demande = ?";
        
        try (Connection con = MyDatabase.getInstance().getCnx();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idDemande);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("email");
                }
            }
        }
        return null;
    }

    public void supprimerDemande(int idDemande) throws SQLException {
        String query = "DELETE FROM demandes_entretien WHERE id_demande = ?";
        try (Connection con = MyDatabase.getInstance().getCnx();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, idDemande);
            ps.executeUpdate();
        }
    }
} 