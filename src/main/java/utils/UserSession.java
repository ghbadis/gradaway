package utils;

public class UserSession {
    private static UserSession instance;
    private int userId;

    private UserSession() {
        // Private constructor to prevent instantiation
        userId = 0; // Default value
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
        System.out.println("[DEBUG] UserSession: User ID set to " + userId);
    }

    public void clearSession() {
        this.userId = 0;
        System.out.println("[DEBUG] UserSession: Session cleared");
    }
} 