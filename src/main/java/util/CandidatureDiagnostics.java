package util;

import java.sql.*;
import java.util.*;

/**
 * Direct diagnostic tool for candidature insertion problems
 */
public class CandidatureDiagnostics {
    private Connection connection;
    
    public CandidatureDiagnostics() {
        try {
            // Database connection
            String url = "jdbc:mysql://localhost:3306/gradaway";
            String user = "root";
            String password = "";
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to database successfully");
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Verifies tables and their relationships
     */
    public void verifyTables() {
        System.out.println("\n=== VERIFYING DATABASE TABLES ===");
        
        // Check table existence
        String[] requiredTables = {"user", "dossier", "universite", "candidature"};
        for (String table : requiredTables) {
            System.out.printf("Table %-15s: %s%n", table, tableExists(table) ? "EXISTS" : "MISSING");
        }
        
        // Check record counts
        System.out.println("\n=== RECORD COUNTS ===");
        for (String table : requiredTables) {
            if (tableExists(table)) {
                System.out.printf("Table %-15s: %d records%n", table, countRecords(table));
            }
        }
        
        // Check primary keys and foreign keys
        verifyTableStructure("user", "id");
        verifyTableStructure("dossier", "id_dossier");
        verifyTableStructure("universite", "id_universite");
        verifyForeignKeys();
    }
    
    /**
     * Checks the structure of a specific table
     */
    private void verifyTableStructure(String tableName, String primaryKeyColumn) {
        if (!tableExists(tableName)) return;
        
        System.out.println("\n=== TABLE STRUCTURE: " + tableName.toUpperCase() + " ===");
        
        try {
            // Check if primary key exists
            DatabaseMetaData dbMeta = connection.getMetaData();
            ResultSet primaryKeys = dbMeta.getPrimaryKeys(null, null, tableName);
            boolean hasPrimaryKey = false;
            
            while (primaryKeys.next()) {
                String columnName = primaryKeys.getString("COLUMN_NAME");
                System.out.println("Primary Key: " + columnName);
                if (columnName.equals(primaryKeyColumn)) {
                    hasPrimaryKey = true;
                }
            }
            
            if (!hasPrimaryKey) {
                System.out.println("WARNING: Expected primary key '" + primaryKeyColumn + "' not found!");
            }
            
            // List all columns
            ResultSet columns = dbMeta.getColumns(null, null, tableName, null);
            System.out.println("Columns:");
            
            while (columns.next()) {
                String name = columns.getString("COLUMN_NAME");
                String type = columns.getString("TYPE_NAME");
                System.out.printf("  - %-20s (Type: %s)%n", name, type);
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking table structure: " + e.getMessage());
        }
    }
    
    /**
     * Verifies foreign key relationships for the candidature table
     */
    private void verifyForeignKeys() {
        if (!tableExists("candidature")) return;
        
        System.out.println("\n=== FOREIGN KEYS FOR CANDIDATURE ===");
        
        try {
            DatabaseMetaData dbMeta = connection.getMetaData();
            ResultSet foreignKeys = dbMeta.getImportedKeys(null, null, "candidature");
            boolean hasUserFK = false;
            boolean hasDossierFK = false;
            boolean hasUniversiteFK = false;
            
            while (foreignKeys.next()) {
                String pkTable = foreignKeys.getString("PKTABLE_NAME");
                String pkColumn = foreignKeys.getString("PKCOLUMN_NAME");
                String fkColumn = foreignKeys.getString("FKCOLUMN_NAME");
                
                System.out.printf("FK: %s.%s references %s.%s%n", 
                        "candidature", fkColumn, pkTable, pkColumn);
                
                if (pkTable.equals("user") && pkColumn.equals("id") && fkColumn.equals("user_id")) {
                    hasUserFK = true;
                }
                if (pkTable.equals("dossier") && pkColumn.equals("id_dossier") && fkColumn.equals("id_dossier")) {
                    hasDossierFK = true;
                }
                if (pkTable.equals("universite") && pkColumn.equals("id_universite") && fkColumn.equals("id_universite")) {
                    hasUniversiteFK = true;
                }
            }
            
            if (!hasUserFK) {
                System.out.println("WARNING: Foreign key relationship to user table not found!");
            }
            if (!hasDossierFK) {
                System.out.println("WARNING: Foreign key relationship to dossier table not found!");
            }
            if (!hasUniversiteFK) {
                System.out.println("WARNING: Foreign key relationship to universite table not found!");
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking foreign keys: " + e.getMessage());
        }
    }
    
    /**
     * Tries to directly insert a candidature using existing IDs
     */
    public void testInsertCandidature() {
        System.out.println("\n=== TESTING CANDIDATURE INSERTION ===");
        
        // Test with first IDs from database
        System.out.println("Test 1: Using first IDs from database");
        int userId = getFirstId("user", "id");
        int dossierId = getFirstId("dossier", "id_dossier");
        int universiteId = getFirstId("universite", "id_universite");
        
        if (userId > 0 && dossierId > 0 && universiteId > 0) {
            attemptInsertion(userId, dossierId, universiteId);
        } else {
            if (userId <= 0) System.out.println("No user found in the database.");
            if (dossierId <= 0) System.out.println("No dossier found in the database.");
            if (universiteId <= 0) System.out.println("No universite found in the database.");
        }
        
        // Test with specific IDs requested by the user
        System.out.println("\nTest 2: Using specific IDs requested by user");
        int specificUserId = 52;
        int specificDossierId = 44;
        // Use first university ID as that wasn't specified
        
        // Check if these IDs exist
        boolean userExists = checkIfIdExists("user", "id", specificUserId);
        boolean dossierExists = checkIfIdExists("dossier", "id_dossier", specificDossierId);
        
        System.out.println("Checking if requested IDs exist:");
        System.out.println("User ID " + specificUserId + " exists: " + userExists);
        System.out.println("Dossier ID " + specificDossierId + " exists: " + dossierExists);
        
        if (universiteId > 0) {
            attemptInsertion(specificUserId, specificDossierId, universiteId);
        } else {
            System.out.println("Cannot test with specific IDs because no university was found.");
        }
    }
    
    /**
     * Attempts to insert a candidature with specific IDs
     */
    private void attemptInsertion(int userId, int dossierId, int universiteId) {
        System.out.println("Using IDs for test insertion:");
        System.out.println("User ID: " + userId);
        System.out.println("Dossier ID: " + dossierId);
        System.out.println("Universite ID: " + universiteId);
        
        try {
            // Begin transaction
            connection.setAutoCommit(false);
            
            // Prepare statement
            String insertSQL = "INSERT INTO candidature (id_dossier, user_id, id_universite, date_de_remise_c, domaine) " +
                              "VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement stmt = connection.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, dossierId);
            stmt.setInt(2, userId);
            stmt.setInt(3, universiteId);
            stmt.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            stmt.setString(5, "Test Domain");
            
            // Execute and get result
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Insertion test result: " + (rowsAffected > 0 ? "SUCCESS" : "FAILED"));
            
            if (rowsAffected > 0) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    System.out.println("Created candidature with ID: " + keys.getInt(1));
                }
                
                // Commit the transaction
                connection.commit();
                System.out.println("Test candidature committed to database.");
            } else {
                // Rollback if no rows affected
                connection.rollback();
            }
            
            // Restore auto-commit
            connection.setAutoCommit(true);
            
        } catch (SQLException e) {
            System.err.println("ERROR DURING INSERTION TEST: " + e.getMessage());
            e.printStackTrace();
            
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Checks if a specific ID exists in a table
     */
    private boolean checkIfIdExists(String tableName, String idColumn, int id) {
        try {
            String query = "SELECT 1 FROM " + tableName + " WHERE " + idColumn + " = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error checking if ID exists: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets the first ID from a table
     */
    private int getFirstId(String tableName, String idColumn) {
        try {
            String query = "SELECT " + idColumn + " FROM " + tableName + " LIMIT 1";
            PreparedStatement stmt = connection.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting first ID: " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Checks if a table exists in the database
     */
    private boolean tableExists(String tableName) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, tableName, null);
            return tables.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Counts records in a table
     */
    private int countRecords(String tableName) {
        try {
            String query = "SELECT COUNT(*) FROM " + tableName;
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Close the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        CandidatureDiagnostics diagnostics = new CandidatureDiagnostics();
        
        try {
            // Run diagnostics
            diagnostics.verifyTables();
            diagnostics.testInsertCandidature();
            
            System.out.println("\n=== DIAGNOSTICS COMPLETE ===");
            System.out.println("Check the messages above for any errors or warnings.");
            
        } finally {
            diagnostics.closeConnection();
        }
    }
} 