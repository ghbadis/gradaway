package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {

    private final String URL = "jdbc:mysql://localhost:3306/gradaway";
    private final String USERNAME = "root";
    private final String PASSWORD = "";
    private Connection cnx;
    private static MyDatabase instance;

    private MyDatabase() {
        try {
            // Load MySQL driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading MySQL driver: " + e.getMessage());
        }
        connect();
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    private void connect() {
        try {
            cnx = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connected to database");
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    public Connection getCnx() {
        try {
            // Check if connection is closed or invalid
            if (cnx == null || cnx.isClosed() || !cnx.isValid(2)) {
                connect();
            }
        } catch (SQLException e) {
            System.out.println("Connection validation failed: " + e.getMessage());
            connect();
        }
        return cnx;
    }
}
