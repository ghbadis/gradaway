package tests;

import utils.EmailSender;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TestEmailSender {
    
    public static void main(String[] args) {
        // Email du destinataire (à remplacer par votre adresse email)
        String recipientEmail = "awaygrad@gmail.com";
        
        // Format de date pour l'affichage
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Données de test
        String nomFoyer = "Foyer Universitaire Central";
        String dateDebut = LocalDate.now().format(formatter);
        String dateFin = LocalDate.now().plusMonths(3).format(formatter);
        String dateReservation = LocalDate.now().format(formatter);
        
        // Générer le contenu HTML de l'email
        String emailContent = EmailSender.generateReservationConfirmationEmail(
            nomFoyer, dateDebut, dateFin, dateReservation);
        
        // Envoyer l'email de test
        boolean success = EmailSender.sendEmail(
            recipientEmail,
            "Test - Confirmation de réservation au foyer " + nomFoyer,
            emailContent
        );
        
        if (success) {
            System.out.println("✅ Email envoyé avec succès à " + recipientEmail);
        } else {
            System.err.println("❌ Échec de l'envoi de l'email à " + recipientEmail);
        }
        
        System.out.println("\n--- Instructions importantes ---");
        System.out.println("1. Avant d'utiliser ce test, modifiez la classe EmailSender.java pour configurer:");
        System.out.println("   - SENDER_EMAIL: Votre adresse Gmail");
        System.out.println("   - SENDER_PASSWORD: Votre mot de passe d'application (pas votre mot de passe Gmail)");
        System.out.println("2. Pour créer un mot de passe d'application pour Gmail:");
        System.out.println("   a. Activez l'authentification à deux facteurs sur votre compte Google");
        System.out.println("   b. Allez sur https://myaccount.google.com/apppasswords");
        System.out.println("   c. Créez un nouveau mot de passe d'application pour 'Mail' et 'Autre (nom personnalisé)'");
        System.out.println("3. Remplacez 'test@example.com' par votre adresse email pour recevoir le test");
    }
} 