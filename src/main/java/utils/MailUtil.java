package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Random;

public class MailUtil {
    private static final String USERNAME = "awaygrad@gmail.com";
    private static final String PASSWORD = "dmzq fnaw uglm glyn"; // <-- Replace with your Gmail App Password
    private static final Random random = new Random();

    public static void sendMail(String to, String subject, String body, boolean isHtml) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(USERNAME));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        if (isHtml) {
            message.setContent(body, "text/html; charset=utf-8");
        } else {
            message.setText(body);
        }

        Transport.send(message);
    }

    // For backward compatibility
    public static void sendMail(String to, String subject, String body) throws MessagingException {
        sendMail(to, subject, body, false);
    }

    // Generate a random location link
    public static String generateLocationLink() {
        String[] locations = {
            "https://maps.google.com/?q=48.8566,2.3522", // Paris
            "https://maps.google.com/?q=45.7640,4.8357", // Lyon
            "https://maps.google.com/?q=43.7102,7.2620", // Nice
            "https://maps.google.com/?q=48.5734,7.7521", // Strasbourg
            "https://maps.google.com/?q=43.2965,5.3698"  // Marseille
        };
        return locations[random.nextInt(locations.length)];
    }

    // Generate a random Zoom link
    public static String generateZoomLink() {
        String[] zoomLinks = {
            "https://zoom.us/j/123456789",
            "https://zoom.us/j/987654321",
            "https://zoom.us/j/456789123",
            "https://zoom.us/j/789123456",
            "https://zoom.us/j/321654987"
        };
        return zoomLinks[random.nextInt(zoomLinks.length)];
    }
} 