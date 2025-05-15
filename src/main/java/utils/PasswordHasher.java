package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHasher {
    
    public static String hashPassword(String password) {
        try {
            // Générer un sel aléatoire
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Créer l'instance de MessageDigest avec SHA-256
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Ajouter le sel au mot de passe
            md.update(salt);
            
            // Hasher le mot de passe
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Combiner le sel et le mot de passe hashé
            byte[] combined = new byte[salt.length + hashedPassword.length];
            System.arraycopy(salt, 0, combined, 0, salt.length);
            System.arraycopy(hashedPassword, 0, combined, salt.length, hashedPassword.length);
            
            // Convertir en Base64 pour le stockage
            return Base64.getEncoder().encodeToString(combined);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }
    
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            // Décoder le mot de passe hashé
            byte[] combined = Base64.getDecoder().decode(hashedPassword);
            
            // Extraire le sel
            byte[] salt = new byte[16];
            System.arraycopy(combined, 0, salt, 0, salt.length);
            
            // Créer l'instance de MessageDigest
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // Ajouter le sel au mot de passe à vérifier
            md.update(salt);
            
            // Hasher le mot de passe à vérifier
            byte[] hashedInput = md.digest(password.getBytes());
            
            // Comparer les hashs
            return MessageDigest.isEqual(hashedInput, 
                java.util.Arrays.copyOfRange(combined, salt.length, combined.length));
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors de la vérification du mot de passe", e);
        }
    }
} 