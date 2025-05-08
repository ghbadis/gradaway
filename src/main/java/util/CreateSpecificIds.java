package util;

import java.sql.*;

/**
 * Utility to create specific user and dossier IDs in the database
 */
public class CreateSpecificIds {
    private Connection connection;
    
    public CreateSpecificIds() {
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
     * Creates a user with ID 52 if it doesn't exist
     */
    public void createUser52() {
        try {
            // First check if the user table exists
            if (!tableExists("user")) {
                System.out.println("User table does not exist. Creating it...");
                createUserTable();
            }
            
            // Check if ID 52 already exists
            if (idExists("user", "id", 52)) {
                System.out.println("User with ID 52 already exists.");
                return;
            }
            
            // Find the current auto-increment value
            int currentAutoIncrement = getAutoIncrement("user");
            System.out.println("Current auto_increment value for user table: " + currentAutoIncrement);
            
            // Try to directly set ID 52
            if (currentAutoIncrement <= 52) {
                System.out.println("Inserting user with ID 52 directly...");
                String insertSQL = "INSERT INTO user (id, nom, prenom, email, password) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(insertSQL);
                stmt.setInt(1, 52);
                stmt.setString(2, "User");
                stmt.setString(3, "FiftyTwo");
                stmt.setString(4, "user52@example.com");
                stmt.setString(5, "password");
                int rows = stmt.executeUpdate();
                
                if (rows > 0) {
                    System.out.println("Successfully created user with ID 52");
                    
                    // Reset auto_increment if needed
                    if (currentAutoIncrement > 0) {
                        setAutoIncrement("user", Math.max(53, currentAutoIncrement));
                    }
                } else {
                    System.out.println("Failed to create user with ID 52");
                }
            } else {
                System.out.println("Auto-increment value is too high, need to reset and recreate the table");
                recreateUserTable();
            }
        } catch (SQLException e) {
            System.err.println("Error creating user with ID 52: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a dossier with ID 44 if it doesn't exist
     */
    public void createDossier44() {
        try {
            // First check if the dossier table exists
            if (!tableExists("dossier")) {
                System.out.println("Dossier table does not exist. Creating it...");
                createDossierTable();
            }
            
            // Check if ID 44 already exists
            if (idExists("dossier", "id_dossier", 44)) {
                System.out.println("Dossier with ID 44 already exists.");
                return;
            }
            
            // Find the current auto-increment value
            int currentAutoIncrement = getAutoIncrement("dossier");
            System.out.println("Current auto_increment value for dossier table: " + currentAutoIncrement);
            
            // Try to directly set ID 44
            if (currentAutoIncrement <= 44) {
                System.out.println("Inserting dossier with ID 44 directly...");
                String insertSQL = "INSERT INTO dossier (id_dossier, nom_dossier) VALUES (?, ?)";
                PreparedStatement stmt = connection.prepareStatement(insertSQL);
                stmt.setInt(1, 44);
                stmt.setString(2, "Dossier Forty-Four");
                int rows = stmt.executeUpdate();
                
                if (rows > 0) {
                    System.out.println("Successfully created dossier with ID 44");
                    
                    // Reset auto_increment if needed
                    if (currentAutoIncrement > 0) {
                        setAutoIncrement("dossier", Math.max(45, currentAutoIncrement));
                    }
                } else {
                    System.out.println("Failed to create dossier with ID 44");
                }
            } else {
                System.out.println("Auto-increment value is too high, need to reset and recreate the table");
                recreateDossierTable();
            }
        } catch (SQLException e) {
            System.err.println("Error creating dossier with ID 44: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Re-creates the user table with auto-increment starting from 1
     */
    private void recreateUserTable() {
        try {
            // Get existing users first
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM user");
            
            // Temporarily store users
            Statement statement = connection.createStatement();
            statement.execute("CREATE TEMPORARY TABLE temp_users SELECT * FROM user");
            
            // Drop and recreate table
            statement.execute("DROP TABLE user");
            createUserTable();
            
            // Insert special user with ID 52
            String insertSQL = "INSERT INTO user (id, nom, prenom, email, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(insertSQL);
            stmt.setInt(1, 52);
            stmt.setString(2, "User");
            stmt.setString(3, "FiftyTwo");
            stmt.setString(4, "user52@example.com");
            stmt.setString(5, "password");
            stmt.executeUpdate();
            
            // Restore other users (excluding ID 52)
            statement.execute("INSERT INTO user SELECT * FROM temp_users WHERE id != 52");
            statement.execute("DROP TEMPORARY TABLE temp_users");
            
            System.out.println("User table recreated with ID 52");
        } catch (SQLException e) {
            System.err.println("Error recreating user table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Re-creates the dossier table with auto-increment starting from 1
     */
    private void recreateDossierTable() {
        try {
            // Get existing dossiers first
            ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM dossier");
            
            // Temporarily store dossiers
            Statement statement = connection.createStatement();
            statement.execute("CREATE TEMPORARY TABLE temp_dossiers SELECT * FROM dossier");
            
            // Drop and recreate table
            statement.execute("DROP TABLE dossier");
            createDossierTable();
            
            // Insert special dossier with ID 44
            String insertSQL = "INSERT INTO dossier (id_dossier, nom_dossier) VALUES (?, ?)";
            PreparedStatement stmt = connection.prepareStatement(insertSQL);
            stmt.setInt(1, 44);
            stmt.setString(2, "Dossier Forty-Four");
            stmt.executeUpdate();
            
            // Restore other dossiers (excluding ID 44)
            statement.execute("INSERT INTO dossier SELECT * FROM temp_dossiers WHERE id_dossier != 44");
            statement.execute("DROP TEMPORARY TABLE temp_dossiers");
            
            System.out.println("Dossier table recreated with ID 44");
        } catch (SQLException e) {
            System.err.println("Error recreating dossier table: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates the user table
     */
    private void createUserTable() throws SQLException {
        String createUserTable = 
            "CREATE TABLE user (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "nom VARCHAR(50), " +
            "prenom VARCHAR(50), " +
            "email VARCHAR(100), " +
            "password VARCHAR(100)" +
            ")";
        Statement stmt = connection.createStatement();
        stmt.execute(createUserTable);
        System.out.println("User table created successfully");
    }
    
    /**
     * Creates the dossier table
     */
    private void createDossierTable() throws SQLException {
        String createDossierTable = 
            "CREATE TABLE dossier (" +
            "id_dossier INT AUTO_INCREMENT PRIMARY KEY, " +
            "nom_dossier VARCHAR(100)" +
            ")";
        Statement stmt = connection.createStatement();
        stmt.execute(createDossierTable);
        System.out.println("Dossier table created successfully");
    }
    
    /**
     * Gets the current auto_increment value for a table
     */
    private int getAutoIncrement(String tableName) {
        try {
            String query = "SELECT `AUTO_INCREMENT` FROM INFORMATION_SCHEMA.TABLES WHERE " +
                          "TABLE_SCHEMA = 'gradaway' AND TABLE_NAME = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, tableName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting auto_increment value: " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Sets the auto_increment value for a table
     */
    private void setAutoIncrement(String tableName, int value) {
        try {
            String query = "ALTER TABLE " + tableName + " AUTO_INCREMENT = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, value);
            stmt.executeUpdate();
            System.out.println("Set auto_increment value for " + tableName + " to " + value);
        } catch (SQLException e) {
            System.err.println("Error setting auto_increment value: " + e.getMessage());
        }
    }
    
    /**
     * Checks if a specific ID exists in a table
     */
    private boolean idExists(String tableName, String idColumn, int id) {
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
     * Checks if a table exists
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
     * Close database connection
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
        CreateSpecificIds creator = new CreateSpecificIds();
        
        try {
            creator.createUser52();
            creator.createDossier44();
            
            System.out.println("\nCreation of specific IDs complete!");
            System.out.println("User ID 52 and Dossier ID 44 should now exist in the database.");
        } finally {
            creator.closeConnection();
        }
    }
} 