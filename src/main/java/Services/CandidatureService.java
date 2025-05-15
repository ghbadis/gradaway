package Services;

import models.Candidature;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidatureService {
    private Connection connection;
    private int currentUserId;
    private int currentDossierId;

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

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }
    public void setCurrentDossierId(int dossierId) {
        this.currentDossierId = dossierId;
    }

    public List<Candidature> getAllCandidatures() {
        List<Candidature> candidatures = new ArrayList<>();
        try {
            String query;
            PreparedStatement statement;
            
            if (currentUserId > 0) {
                // If we have a current user ID, filter by it
                query = "SELECT * FROM candidature WHERE user_id = ?";
                statement = connection.prepareStatement(query);
                statement.setInt(1, currentUserId);
                System.out.println("[DEBUG] CandidatureService: Filtering candidatures for user ID: " + currentUserId);
            } else {
                // Otherwise get all candidatures
                query = "SELECT * FROM candidature";
                statement = connection.prepareStatement(query);
                System.out.println("[DEBUG] CandidatureService: Getting all candidatures (no user filter)");
            }
            
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Candidature candidature = new Candidature();
                candidature.setId_c(resultSet.getInt("id_c"));
                candidature.setId_dossier(resultSet.getInt("id_dossier"));
                candidature.setUser_id(resultSet.getInt("user_id"));
                candidature.setId_universite(resultSet.getInt("id_universite"));
                candidature.setDate_de_remise_c(resultSet.getDate("date_de_remise_c"));
                candidature.setDomaine(resultSet.getString("domaine"));
                
                // Get status if available
                try {
                    String status = resultSet.getString("status");
                    candidature.setStatus(status != null ? status : "pending");
                } catch (SQLException e) {
                    // Column might not exist in older database schemas
                    candidature.setStatus("pending");
                }
                
                // Get acceptance date if available
                try {
                    java.sql.Date acceptationDate = resultSet.getDate("date_acceptation");
                    candidature.setDate_acceptation(acceptationDate);
                } catch (SQLException e) {
                    // Column might not exist in older database schemas
                }
                
                candidatures.add(candidature);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return candidatures;
    }

    public List<Candidature> getCandidaturesForUser(int userId) {
        List<Candidature> candidatures = new ArrayList<>();
        try {
            String query = "SELECT * FROM candidature WHERE user_id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Candidature candidature = new Candidature();
                candidature.setId_c(resultSet.getInt("id_c"));
                candidature.setId_dossier(resultSet.getInt("id_dossier"));
                candidature.setUser_id(resultSet.getInt("user_id"));
                candidature.setId_universite(resultSet.getInt("id_universite"));
                candidature.setDate_de_remise_c(resultSet.getDate("date_de_remise_c"));
                candidature.setDomaine(resultSet.getString("domaine"));
                
                // Get status if available
                try {
                    String status = resultSet.getString("status");
                    candidature.setStatus(status != null ? status : "pending");
                } catch (SQLException e) {
                    // Column might not exist in older database schemas
                    candidature.setStatus("pending");
                }
                
                // Get acceptance date if available
                try {
                    java.sql.Date acceptationDate = resultSet.getDate("date_acceptation");
                    candidature.setDate_acceptation(acceptationDate);
                } catch (SQLException e) {
                    // Column might not exist in older database schemas
                }
                
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
                
                // Get status if available
                try {
                    String status = resultSet.getString("status");
                    candidature.setStatus(status != null ? status : "pending");
                } catch (SQLException e) {
                    // Column might not exist in older database schemas
                    candidature.setStatus("pending");
                }
                
                // Get acceptance date if available
                try {
                    java.sql.Date acceptationDate = resultSet.getDate("date_acceptation");
                    candidature.setDate_acceptation(acceptationDate);
                } catch (SQLException e) {
                    // Column might not exist in older database schemas
                }
                
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
            System.out.println("Attempting to add candidature with:");
            System.out.println("User ID: " + candidature.getUser_id());
            System.out.println("Dossier ID: " + candidature.getId_dossier());
            System.out.println("Universite ID: " + candidature.getId_universite());
            
            // Improved error reporting for foreign key validation
            boolean userExists = checkForeignKeyExists("user", "id", candidature.getUser_id());
            boolean dossierExists = checkForeignKeyExists("dossier", "id_dossier", candidature.getId_dossier());
            boolean universiteExists = checkForeignKeyExists("universite", "id_universite", candidature.getId_universite());
            
            System.out.println("Validation results:");
            System.out.println("User exists: " + userExists);
            System.out.println("Dossier exists: " + dossierExists);
            System.out.println("Universite exists: " + universiteExists);
            
            if (!userExists) {
                System.out.println("Error: User ID " + candidature.getUser_id() + " does not exist");
                return false;
            }
            
            if (!dossierExists) {
                System.out.println("Error: Dossier ID " + candidature.getId_dossier() + " does not exist");
                return false;
            }
            
            if (!universiteExists) {
                System.out.println("Error: Universite ID " + candidature.getId_universite() + " does not exist");
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
            // First check if the table exists
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, tableName, null);
            
            if (!tables.next()) {
                System.out.println("ERROR: Table '" + tableName + "' does not exist!");
                return false;
            }
            
            // Check if the column exists
            ResultSet columns = metaData.getColumns(null, null, tableName, idColumnName);
            if (!columns.next()) {
                System.out.println("ERROR: Column '" + idColumnName + "' does not exist in table '" + tableName + "'!");
                
                // Show available columns
                System.out.println("Available columns in '" + tableName + "':");
                columns = metaData.getColumns(null, null, tableName, null);
                while (columns.next()) {
                    System.out.println("- " + columns.getString("COLUMN_NAME"));
                }
                
                return false;
            }
            
            // Check for the ID
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
    
    // New method to get a list of existing user IDs
    public List<Integer> getExistingUserIds() {
        List<Integer> userIds = new ArrayList<>();
        try {
            // First check if the table exists
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "user", null);
            
            if (!tables.next()) {
                // Try variations of the table name
                String[] possibleTableNames = {"user", "users", "utilisateur", "utilisateurs"};
                String actualTableName = null;
                
                for (String tableName : possibleTableNames) {
                    tables = metaData.getTables(null, null, tableName, null);
                    if (tables.next()) {
                        actualTableName = tableName;
                        System.out.println("Found user table with name: " + actualTableName);
                        break;
                    }
                }
                
                if (actualTableName == null) {
                    System.out.println("ERROR: User table not found!");
                    return userIds;
                }
                
                // Get ID column name
                ResultSet columns = metaData.getColumns(null, null, actualTableName, null);
                String idColumnName = "id"; // default
                
                while (columns.next()) {
                    String colName = columns.getString("COLUMN_NAME");
                    if (colName.equalsIgnoreCase("id") || colName.contains("id") || 
                        colName.equalsIgnoreCase("user_id") || colName.equalsIgnoreCase("utilisateur_id")) {
                        idColumnName = colName;
                        break;
                    }
                }
                
                String query = "SELECT " + idColumnName + " FROM " + actualTableName + " LIMIT 10";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                
                while (resultSet.next()) {
                    userIds.add(resultSet.getInt(idColumnName));
                }
            } else {
                // Original code
                String query = "SELECT id FROM user LIMIT 10";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                
                while (resultSet.next()) {
                    userIds.add(resultSet.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userIds;
    }
    
    // New method to get a list of existing dossier IDs
    public List<Integer> getExistingDossierIds() {
        List<Integer> dossierIds = new ArrayList<>();
        try {
            // First check if the table exists
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "dossier", null);
            
            if (!tables.next()) {
                // Try variations of the table name
                String[] possibleTableNames = {"dossier", "dossiers", "folder", "folders"};
                String actualTableName = null;
                
                for (String tableName : possibleTableNames) {
                    tables = metaData.getTables(null, null, tableName, null);
                    if (tables.next()) {
                        actualTableName = tableName;
                        System.out.println("Found dossier table with name: " + actualTableName);
                        break;
                    }
                }
                
                if (actualTableName == null) {
                    System.out.println("ERROR: Dossier table not found!");
                    return dossierIds;
                }
                
                // Get ID column name
                ResultSet columns = metaData.getColumns(null, null, actualTableName, null);
                String idColumnName = "id_dossier"; // default
                
                while (columns.next()) {
                    String colName = columns.getString("COLUMN_NAME");
                    if (colName.equalsIgnoreCase("id_dossier") || colName.contains("dossier") || 
                        colName.equalsIgnoreCase("id") || colName.contains("_id")) {
                        idColumnName = colName;
                        break;
                    }
                }
                
                String query = "SELECT " + idColumnName + " FROM " + actualTableName + " LIMIT 10";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                
                while (resultSet.next()) {
                    dossierIds.add(resultSet.getInt(idColumnName));
                }
            } else {
                // Original code
                String query = "SELECT id_dossier FROM dossier LIMIT 10";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                
                while (resultSet.next()) {
                    dossierIds.add(resultSet.getInt("id_dossier"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dossierIds;
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

    public boolean candidatureExists(int userId, int universiteId) {
        try {
            String query = "SELECT COUNT(*) FROM candidature WHERE user_id = ? AND id_universite = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, userId);
            statement.setInt(2, universiteId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Add new method to accept a candidature
    public boolean acceptCandidature(int candidatureId) {
        try {
            // First check if candidature exists and if it's not already accepted
            String checkQuery = "SELECT id_c, status FROM candidature WHERE id_c = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setInt(1, candidatureId);
            
            ResultSet rs = checkStatement.executeQuery();
            if (!rs.next() || "accepted".equals(rs.getString("status"))) {
                System.out.println("Cannot accept candidature: ID " + candidatureId + 
                                   " not found or already accepted");
                return false;
            }
            
            // Update the candidature status and set acceptance date
            String query = "UPDATE candidature SET status = 'accepted', date_acceptation = ? WHERE id_c = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            
            // Set today's date as acceptance date
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            statement.setDate(1, today);
            statement.setInt(2, candidatureId);
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Add new method to reject a candidature
    public boolean rejectCandidature(int candidatureId) {
        try {
            // First check if candidature exists and if it's not already rejected
            String checkQuery = "SELECT id_c, status FROM candidature WHERE id_c = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setInt(1, candidatureId);
            
            ResultSet rs = checkStatement.executeQuery();
            if (!rs.next() || "rejected".equals(rs.getString("status"))) {
                System.out.println("Cannot reject candidature: ID " + candidatureId + 
                                   " not found or already rejected");
                return false;
            }
            
            // Update the candidature status
            String query = "UPDATE candidature SET status = 'rejected' WHERE id_c = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, candidatureId);
            
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
} 