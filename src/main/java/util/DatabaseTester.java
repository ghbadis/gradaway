package util;

import java.sql.*;
import java.util.*;

/**
 * Utility class to test database connectivity and view table structure
 */
public class DatabaseTester {
    private Connection connection;
    
    public DatabaseTester() {
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
     * Lists all tables in the database
     */
    public List<String> listTables() {
        List<String> tables = new ArrayList<>();
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }
    
    /**
     * Describes a specific table's structure
     */
    public void describeTable(String tableName) {
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            
            System.out.println("Table structure for: " + tableName);
            System.out.println("----------------------------");
            System.out.printf("%-20s %-15s %-10s %-10s%n", "COLUMN_NAME", "TYPE_NAME", "COLUMN_SIZE", "NULLABLE");
            System.out.println("----------------------------");
            
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                String typeName = columns.getString("TYPE_NAME");
                int columnSize = columns.getInt("COLUMN_SIZE");
                String isNullable = columns.getString("IS_NULLABLE");
                
                System.out.printf("%-20s %-15s %-10d %-10s%n", columnName, typeName, columnSize, isNullable);
            }
            
            // Get primary keys
            ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
            List<String> pkColumns = new ArrayList<>();
            
            while (primaryKeys.next()) {
                pkColumns.add(primaryKeys.getString("COLUMN_NAME"));
            }
            
            if (!pkColumns.isEmpty()) {
                System.out.println("\nPrimary Key(s): " + String.join(", ", pkColumns));
            }
            
            System.out.println("----------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Shows a sample of data from the specified table
     */
    public void showTableData(String tableName, int limit) {
        try {
            String query = "SELECT * FROM " + tableName + " LIMIT " + limit;
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            System.out.println("\nSample data from table: " + tableName);
            System.out.println("----------------------------");
            
            // Print column headers
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();
            
            // Print separator
            for (int i = 1; i <= columnCount; i++) {
                for (int j = 0; j < metaData.getColumnName(i).length(); j++) {
                    System.out.print("-");
                }
                System.out.print("\t");
            }
            System.out.println();
            
            // Print data
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getString(i) + "\t");
                }
                System.out.println();
            }
            
            System.out.println("----------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Reports the count of records in a table
     */
    public int countRecords(String tableName) {
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
     * Checks if the table exists
     */
    public boolean tableExists(String tableName) {
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
     * Creates a basic user and dossier for testing if none exist
     */
    public void createTestDataIfNeeded() {
        try {
            // Check if user table is empty
            if (countRecords("user") == 0) {
                System.out.println("Creating test user...");
                String createUserQuery = "INSERT INTO user (nom, prenom, email, password) VALUES (?, ?, ?, ?)";
                PreparedStatement userStatement = connection.prepareStatement(createUserQuery, Statement.RETURN_GENERATED_KEYS);
                userStatement.setString(1, "Test");
                userStatement.setString(2, "User");
                userStatement.setString(3, "test@example.com");
                userStatement.setString(4, "password");
                userStatement.executeUpdate();
                
                ResultSet keys = userStatement.getGeneratedKeys();
                if (keys.next()) {
                    System.out.println("Created test user with ID: " + keys.getInt(1));
                }
            }
            
            // Check if dossier table is empty
            if (countRecords("dossier") == 0) {
                System.out.println("Creating test dossier...");
                String createDossierQuery = "INSERT INTO dossier (nom_dossier) VALUES (?)";
                PreparedStatement dossierStatement = connection.prepareStatement(createDossierQuery, Statement.RETURN_GENERATED_KEYS);
                dossierStatement.setString(1, "Test Dossier");
                dossierStatement.executeUpdate();
                
                ResultSet keys = dossierStatement.getGeneratedKeys();
                if (keys.next()) {
                    System.out.println("Created test dossier with ID: " + keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating test data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Main method to run tests directly
     */
    public static void main(String[] args) {
        DatabaseTester tester = new DatabaseTester();
        
        System.out.println("Database tables:");
        List<String> tables = tester.listTables();
        for (String table : tables) {
            System.out.println("- " + table);
        }
        
        if (tables.contains("user")) {
            tester.describeTable("user");
            tester.showTableData("user", 5);
            System.out.println("Total user records: " + tester.countRecords("user"));
        } else {
            System.out.println("User table does not exist!");
        }
        
        if (tables.contains("dossier")) {
            tester.describeTable("dossier");
            tester.showTableData("dossier", 5);
            System.out.println("Total dossier records: " + tester.countRecords("dossier"));
        } else {
            System.out.println("Dossier table does not exist!");
        }
        
        // Create test data if needed
        tester.createTestDataIfNeeded();
    }
} 