package Services;

import entities.User;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser implements IService<User> {
    private Connection con;


    public ServiceUser() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(User user) throws SQLException {
        String req = "INSERT INTO user(nom, prenom, age, DateNaissance, nationalite, email, cin, telephone, " +
                "domaine_etude, universite_origine, role, moyennes, annee_obtention_diplome, mdp, image) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
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
            ps.setString(15, user.getImage());

            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Utilisateur ajouté avec succès ! ID: " + user.getId());
            } else {
                throw new SQLException("Échec de l'ajout de l'utilisateur, aucune ligne affectée.");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void modifier(User user) throws SQLException {
        String req = "update user set nom=?, prenom=?, age=?, DateNaissance=?, nationalite=?, email=?, " +
                "cin=?, telephone=?, domaine_etude=?, universite_origine=?, role=?, moyennes=?, " +
                "annee_obtention_diplome=?, mdp=?, image=? where id=?";
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
        ps.setString(15, user.getImage());
        ps.setInt(16, user.getId());
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
            String imageP = rs.getString("image");
            LocalDate dateNaissanceP = rs.getDate("dateNaissance").toLocalDate();

            User p = new User(idP, ageP, cinP, telephoneP, moyennesP, anneeDiplomeP,
                    nomP, prenomP, nationaliteP, emailP, domaineEtudeP,
                    universiteP, roleP, dateNaissanceP, mdpP, imageP);
            users.add(p);
        }

        return users;
    }

    public User getUserById(int id) throws SQLException {
        System.out.println("ServiceUser: Getting user by ID: " + id);
        String req = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                System.out.println("ServiceUser: User found with ID: " + id);
                User user = new User(
                    rs.getInt("id"),
                    rs.getInt("age"),
                    rs.getInt("cin"),
                    rs.getInt("telephone"),
                    rs.getInt("moyennes"),
                    rs.getInt("annee_obtention_diplome"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("nationalite"),
                    rs.getString("email"),
                    rs.getString("domaine_etude"),
                    rs.getString("universite_origine"),
                    rs.getString("role"),
                    rs.getDate("DateNaissance").toLocalDate(),
                    rs.getString("mdp"),
                    rs.getString("image")
                );
                System.out.println("ServiceUser: User data loaded - Name: " + user.getNom() + " " + user.getPrenom());
                return user;
            }
            System.out.println("ServiceUser: No user found with ID: " + id);
            return null;
        }
    }

    public boolean verifyPassword(int userId, String password) throws SQLException {
        String req = "SELECT mdp FROM user WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                String hashedPassword = rs.getString("mdp");
                return utils.PasswordHasher.verifyPassword(password, hashedPassword);
            }
            return false;
        }
    }

    public void updatePassword(int userId, String newPassword) throws SQLException {
        String hashedPassword = utils.PasswordHasher.hashPassword(newPassword);
        String req = "UPDATE user SET mdp = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public boolean emailExists(String email) throws SQLException {
        String req = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public User getUserByEmail(String email) throws SQLException {
        String req = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement ps = con.prepareStatement(req)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getInt("age"),
                    rs.getInt("cin"),
                    rs.getInt("telephone"),
                    rs.getInt("moyennes"),
                    rs.getInt("annee_obtention_diplome"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("nationalite"),
                    rs.getString("email"),
                    rs.getString("domaine_etude"),
                    rs.getString("universite_origine"),
                    rs.getString("role"),
                    rs.getDate("DateNaissance").toLocalDate(),
                    rs.getString("mdp"),
                    rs.getString("image")
                );
            }
        }
        return null;
    }
}
