package Services;

import entities.User;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements Services.IService<User> {
    private Connection con;

    public ServiceUser() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(User user) throws SQLException {
        String req = "INSERT INTO user(nom, prenom, age, DateNaissance, nationalite, email, cin, telephone, " +
                "domaine_etude, universite_origine, role, moyennes, annee_obtention_diplome, mdp) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, user.getNom());
        ps.setString(2, user.getPrenom());
        ps.setInt(3, user.getAge());
        ps.setDate(4, Date.valueOf(user.getDateNaissance()));
        ps.setString(5, user.getNationalite());
        ps.setString(6, user.getEmail());
        ps.setInt(7, user.getCin());
        ps.setInt(8, user.getTelephone());
        ps.setString(9, user.getDomaine_etude());
        ps.setString(10, user.getUniversite_origine());
        ps.setString(11, user.getRole());
        ps.setInt(12, user.getMoyennes());
        ps.setInt(13, user.getAnnee_obtention_diplome());
        ps.setString(14, user.getMdp());
        ps.executeUpdate();
        System.out.println("user ajouté");
    }

    @Override
    public void modifier(User user) throws SQLException {
        String req = "update user set nom=?, prenom=?, age=?, DateNaissance=?, nationalite=?, email=?, " +
                "cin=?, telephone=?, domaine_etude=?, universite_origine=?, role=?, moyennes=?, " +
                "annee_obtention_diplome=?, mdp=? where id=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, user.getNom());
        ps.setString(2, user.getPrenom());
        ps.setInt(3, user.getAge());
        ps.setDate(4, Date.valueOf(user.getDateNaissance()));
        ps.setString(5, user.getNationalite());
        ps.setString(6, user.getEmail());
        ps.setInt(7, user.getCin());
        ps.setInt(8, user.getTelephone());
        ps.setString(9, user.getDomaine_etude());
        ps.setString(10, user.getUniversite_origine());
        ps.setString(11, user.getRole());
        ps.setInt(12, user.getMoyennes());
        ps.setInt(13, user.getAnnee_obtention_diplome());
        ps.setString(14, user.getMdp());
        ps.setInt(15, user.getId());
        ps.executeUpdate();
        System.out.println("user modifié");
    }

    @Override
    public boolean supprimer(User user) throws SQLException {
        String req = "delete from user where id=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, user.getId());
        ps.executeUpdate();
        System.out.println("user supprimé");
        return false;
    }

    @Override
    public List<User> recuperer() throws SQLException {
        List<User> users = new ArrayList<>();
        String req = "select * from user";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            int idP = rs.getInt("id");
            int ageP = rs.getInt("age");
            int cinP = rs.getInt("cin");
            int telephoneP = rs.getInt("telephone");
            int moyennesP = rs.getInt("moyennes");
            int anneeDiplomeP = rs.getInt("annee_obtention_diplome");
            String nomP = rs.getString("nom");
            String prenomP = rs.getString("prenom");
            String nationaliteP = rs.getString("nationalite");
            String emailP = rs.getString("email");
            String domaineEtudeP = rs.getString("domaine_etude");
            String universiteP = rs.getString("universite_origine");
            String roleP = rs.getString("role");
            String mdpP = rs.getString("mdp");
            LocalDate dateNaissanceP = rs.getDate("dateNaissance").toLocalDate();

            User p = new User(idP, ageP, cinP, telephoneP, moyennesP, anneeDiplomeP,
                    nomP, prenomP, nationaliteP, emailP, domaineEtudeP,
                    universiteP, roleP, dateNaissanceP, mdpP);
            users.add(p);
        }
        return users;
    }
}
