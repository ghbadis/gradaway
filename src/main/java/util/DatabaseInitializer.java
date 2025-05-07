package util;

import java.sql.*;

/**
 * This class creates necessary database tables and sample data
 * to help with testing the application.
 */
public class DatabaseInitializer {
    private Connection connection;
    
    public DatabaseInitializer() {
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
     * Creates required tables if they don't exist
     */
    public void createTablesIfNeeded() {
        try {
            // Check if user table exists
            if (!tableExists("user")) {
                System.out.println("Creating user table...");
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
            
            // Check if dossier table exists
            if (!tableExists("dossier")) {
                System.out.println("Creating dossier table...");
                String createDossierTable = 
                    "CREATE TABLE dossier (" +
                    "id_dossier INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nom_dossier VARCHAR(100)" +
                    ")";
                Statement stmt = connection.createStatement();
                stmt.execute(createDossierTable);
                System.out.println("Dossier table created successfully");
            }
            
            // Check if universite table exists
            if (!tableExists("universite")) {
                System.out.println("Creating universite table...");
                String createUniversiteTable = 
                    "CREATE TABLE universite (" +
                    "id_universite INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nom VARCHAR(100), " +
                    "pays VARCHAR(50), " +
                    "ville VARCHAR(50), " +
                    "adresse_universite VARCHAR(200), " +
                    "domaine VARCHAR(100), " +
                    "frais DOUBLE, " +
                    "photo_path VARCHAR(255)" +
                    ")";
                Statement stmt = connection.createStatement();
                stmt.execute(createUniversiteTable);
                System.out.println("Universite table created successfully");
            } else {
                // Check if photo_path column exists and add it if it doesn't
                try {
                    DatabaseMetaData dbm = connection.getMetaData();
                    ResultSet columns = dbm.getColumns(null, null, "universite", "photo_path");
                    if (!columns.next()) {
                        System.out.println("Adding photo_path column to universite table...");
                        String addPhotoPathColumn = "ALTER TABLE universite ADD COLUMN photo_path VARCHAR(255)";
                        Statement stmt = connection.createStatement();
                        stmt.execute(addPhotoPathColumn);
                        System.out.println("Added photo_path column to universite table");
                    }
                } catch (SQLException e) {
                    System.err.println("Error checking or adding photo_path column: " + e.getMessage());
                }
            }
            
            // Check if candidature table exists
            if (!tableExists("candidature")) {
                System.out.println("Creating candidature table...");
                String createCandidatureTable = 
                    "CREATE TABLE candidature (" +
                    "id_c INT AUTO_INCREMENT PRIMARY KEY, " +
                    "id_dossier INT, " +
                    "user_id INT, " +
                    "id_universite INT, " +
                    "date_de_remise_c DATE, " +
                    "domaine VARCHAR(100), " +
                    "FOREIGN KEY (id_dossier) REFERENCES dossier(id_dossier), " +
                    "FOREIGN KEY (user_id) REFERENCES user(id), " +
                    "FOREIGN KEY (id_universite) REFERENCES universite(id_universite)" +
                    ")";
                Statement stmt = connection.createStatement();
                stmt.execute(createCandidatureTable);
                System.out.println("Candidature table created successfully");
            }
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Insert sample data into tables
     */
    public void insertSampleData() {
        try {
            // Insert test user if none exists
            if (countRecords("user") == 0) {
                System.out.println("Inserting test user...");
                String insertUserSQL = "INSERT INTO user (nom, prenom, email, password) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(insertUserSQL, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, "Test");
                stmt.setString(2, "User");
                stmt.setString(3, "test@example.com");
                stmt.setString(4, "password");
                stmt.executeUpdate();
                
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    System.out.println("Created user with ID: " + rs.getInt(1));
                }
            }
            
            // Insert test dossier if none exists
            if (countRecords("dossier") == 0) {
                System.out.println("Inserting test dossier...");
                String insertDossierSQL = "INSERT INTO dossier (nom_dossier) VALUES (?)";
                PreparedStatement stmt = connection.prepareStatement(insertDossierSQL, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, "Dossier Test");
                stmt.executeUpdate();
                
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    System.out.println("Created dossier with ID: " + rs.getInt(1));
                }
            }
            
            // Insert test universite if none exists
            if (countRecords("universite") == 0) {
                System.out.println("Inserting test universite...");
                String insertUnivSQL = "INSERT INTO universite (nom, pays, ville, frais) VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(insertUnivSQL, Statement.RETURN_GENERATED_KEYS);
                stmt.setString(1, "Université Test");
                stmt.setString(2, "France");
                stmt.setString(3, "Paris");
                stmt.setDouble(4, 5000.0);
                stmt.executeUpdate();
                
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    System.out.println("Created universite with ID: " + rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Check if a table exists
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
     * Count records in a table
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
     * Run database initializer
     */
    public static void main(String[] args) {
        DatabaseInitializer initializer = new DatabaseInitializer();
        initializer.createTablesIfNeeded();
        initializer.insertSampleData();
        
        System.out.println("\nDatabase setup complete!");
        
        // Verify data
        DatabaseTester tester = new DatabaseTester();
        System.out.println("\nVerifying user table:");
        tester.showTableData("user", 5);
        
        System.out.println("\nVerifying dossier table:");
        tester.showTableData("dossier", 5);
        
        System.out.println("\nVerifying universite table:");
        tester.showTableData("universite", 5);
    }
} 