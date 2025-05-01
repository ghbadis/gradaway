package Services;

import models.Candidature;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidatureService {
    private Connection connection;
    private final int currentUserId = 31; // Example user ID
    private final int currentDossierId = 37; // Example dossier ID

    public CandidatureService() {
        try {
            // Database connection
            String url = "jdbc:mysql://localhost:3306/gradaway";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Candidature> getAllCandidatures() {
        List<Candidature> candidatures = new ArrayList<>();
        try {
            String query = "SELECT * FROM candidature";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Candidature candidature = new Candidature();
                candidature.setId_c(resultSet.getInt("id_c"));
                candidature.setId_dossier(resultSet.getInt("id_dossier"));
                candidature.setUser_id(resultSet.getInt("user_id"));
                candidature.setId_universite(resultSet.getInt("id_universite"));
                candidature.setDate_de_remise_c(resultSet.getDate("date_de_remise_c"));
                candidature.setDomaine(resultSet.getString("domaine"));
                
                candidatures.add(candidature);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidatures;
    }

    public Candidature getCandidatureById(int id) {
        try {
            String query = "SELECT * FROM candidature WHERE id_c = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Candidature candidature = new Candidature();
                candidature.setId_c(resultSet.getInt("id_c"));
                candidature.setId_dossier(resultSet.getInt("id_dossier"));
                candidature.setUser_id(resultSet.getInt("user_id"));
                candidature.setId_universite(resultSet.getInt("id_universite"));
                candidature.setDate_de_remise_c(resultSet.getDate("date_de_remise_c"));
                candidature.setDomaine(resultSet.getString("domaine"));
                
                return candidature;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deleteCandidature(int id) {
        try {
            String query = "DELETE FROM candidature WHERE id_c = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addCandidature(Candidature candidature) {
        try {
            // First check if the foreign keys exist
            if (!checkForeignKeyExists("user", "id", candidature.getUser_id()) ||
                !checkForeignKeyExists("dossier", "id_dossier", candidature.getId_dossier()) ||
                !checkForeignKeyExists("universite", "id_universite", candidature.getId_universite())) {
                System.out.println("Foreign key constraint violation - one or more referenced IDs don't exist");
                return false;
            }
            
            // If all foreign keys exist, proceed with insert
            String query = "INSERT INTO candidature (id_dossier, user_id, id_universite, date_de_remise_c, domaine) " +
                           "VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, candidature.getId_dossier());
            statement.setInt(2, candidature.getUser_id());
            statement.setInt(3, candidature.getId_universite());
            statement.setDate(4, new java.sql.Date(candidature.getDate_de_remise_c().getTime()));
            statement.setString(5, candidature.getDomaine());
            
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Helper method to check if a foreign key exists in its parent table
    private boolean checkForeignKeyExists(String tableName, String idColumnName, int id) {
        try {
            String query = "SELECT 1 FROM " + tableName + " WHERE " + idColumnName + " = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Returns true if the ID exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 