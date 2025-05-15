package utils;

/**
 * Classe singleton pour gérer les informations de session de l'utilisateur connecté
 */
public class SessionManager {
    
    private static SessionManager instance;
    private int userId;
    private String userEmail;
    private String userRole;
    
    // Constructeur privé pour le pattern Singleton
    private SessionManager() {
        // Initialisation par défaut
        this.userId = -1;
        this.userEmail = "";
        this.userRole = "";
    }
    
    /**
     * Obtenir l'instance unique de SessionManager
     * @return L'instance de SessionManager
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Définir les informations de l'utilisateur connecté
     * @param userId ID de l'utilisateur
     * @param userEmail Email de l'utilisateur
     * @param userRole Rôle de l'utilisateur
     */
    public void setUserInfo(int userId, String userEmail, String userRole) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userRole = userRole;
        
        System.out.println("SessionManager: Informations utilisateur définies - ID: " + userId + ", Email: " + userEmail + ", Rôle: " + userRole);
    }
    
    /**
     * Effacer les informations de session (déconnexion)
     */
    public void clearSession() {
        this.userId = -1;
        this.userEmail = "";
        this.userRole = "";
        
        System.out.println("SessionManager: Session effacée");
    }
    
    /**
     * Obtenir l'ID de l'utilisateur connecté
     * @return ID de l'utilisateur
     */
    public int getUserId() {
        return userId;
    }
    
    /**
     * Obtenir l'email de l'utilisateur connecté
     * @return Email de l'utilisateur
     */
    public String getUserEmail() {
        return userEmail;
    }
    
    /**
     * Obtenir le rôle de l'utilisateur connecté
     * @return Rôle de l'utilisateur
     */
    public String getUserRole() {
        return userRole;
    }
    
    /**
     * Vérifier si un utilisateur est connecté
     * @return true si un utilisateur est connecté, false sinon
     */
    public boolean isLoggedIn() {
        return userId > 0;
    }
}
