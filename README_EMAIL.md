# Configuration de l'envoi d'emails pour l'application Foyer

Ce document explique comment configurer et utiliser le système d'envoi d'emails dans l'application de réservation de foyers.

## Configuration requise

Avant de pouvoir utiliser la fonctionnalité d'envoi d'emails, vous devez configurer les paramètres suivants :

### 1. Modifier les paramètres SMTP dans `EmailSender.java`

Ouvrez le fichier `src/main/java/utils/EmailSender.java` et modifiez les constantes suivantes :

```java
// SMTP server configuration
private static final String SMTP_HOST = "smtp.gmail.com";
private static final String SMTP_PORT = "587";

// Sender email credentials (replace with your actual email and app password)
private static final String SENDER_EMAIL = "your.application.email@gmail.com";
private static final String SENDER_PASSWORD = "your-app-password";
```

### 2. Créer un mot de passe d'application pour Gmail

Pour utiliser Gmail comme service d'envoi d'emails, suivez ces étapes :

1. Activez l'authentification à deux facteurs sur votre compte Google
   - Allez sur https://myaccount.google.com/security
   - Activez la "Validation en deux étapes"

2. Créez un mot de passe d'application
   - Allez sur https://myaccount.google.com/apppasswords
   - Sélectionnez "Mail" comme application
   - Sélectionnez "Autre (nom personnalisé)" comme appareil et nommez-le "Application Foyer"
   - Cliquez sur "Générer"
   - Copiez le mot de passe généré (sans espaces) et utilisez-le comme valeur pour `SENDER_PASSWORD`

## Tester l'envoi d'emails

Une classe de test est fournie pour vérifier que l'envoi d'emails fonctionne correctement :

1. Modifiez `src/main/java/tests/TestEmailSender.java` pour utiliser votre adresse email comme destinataire :
   ```java
   String recipientEmail = "votre.email@example.com";
   ```

2. Exécutez la classe `TestEmailSender` depuis votre IDE ou avec la commande :
   ```
   mvn exec:java -Dexec.mainClass="tests.TestEmailSender"
   ```

3. Vérifiez votre boîte de réception pour confirmer la réception de l'email de test.

## Fonctionnement dans l'application

Lorsqu'un utilisateur remplit le formulaire de réservation et clique sur le bouton "confirme" :

1. L'application vérifie que tous les champs sont correctement remplis
2. La réservation est enregistrée dans la base de données
3. Un email de confirmation est envoyé à l'adresse spécifiée dans le champ "Gmail"
4. Un message de confirmation est affiché à l'utilisateur

## Personnalisation du modèle d'email

Le modèle HTML de l'email peut être personnalisé en modifiant la méthode `generateReservationConfirmationEmail` dans la classe `EmailSender.java`.

## Dépannage

Si l'envoi d'emails échoue, vérifiez les points suivants :

1. **Connexion Internet** : Assurez-vous d'être connecté à Internet
2. **Paramètres SMTP** : Vérifiez que les paramètres SMTP sont corrects
3. **Identifiants** : Assurez-vous que l'adresse email et le mot de passe d'application sont corrects
4. **Logs** : Consultez les messages d'erreur dans la console pour plus de détails
5. **Pare-feu/Antivirus** : Vérifiez que votre pare-feu ou antivirus ne bloque pas les connexions SMTP

Si le problème persiste, contactez l'administrateur système. 