package tests;

import entities.ReservationVol;
import entities.Vols;
import Services.serviceReservationVol;
import Services.serviceVols;
import utils.MyDatabase;

import java.sql.SQLException;
import java.util.Date;

public class Maindhia {
    public static void main(String[] args) {
        System.out.println("=== Système de gestion des vols et réservations ===");

        // 1. Tester la connexion à la base de données
        testConnection();

        // 2. Tester l'ajout d'un vol
        int idVol = testAjoutVol();

        if(idVol > 0) {
            // 3. Tester l'ajout d'une réservation si le vol a été créé
            testAjoutReservation(idVol);
        }
    }

    private static void testConnection() {
        try {
            MyDatabase db = MyDatabase.getInstance();
            if(db.getCnx() != null && !db.getCnx().isClosed()) {
                System.out.println("\n✅ Connexion à la base de données réussie!");
            } else {
                System.out.println("\n❌ Échec de la connexion à la base de données");
            }
        } catch (SQLException e) {
            System.out.println("\n❌ Erreur de connexion: " + e.getMessage());
        }
    }

    private static int testAjoutVol() {
        serviceVols volService = new serviceVols();

        Vols nouveauVol = new Vols(

                "Air France",
                "AF123",
                "CDG",
                "JFK",
                "Paris",
                "New York",
                "France",
                "USA",
                new Date(), // date de départ
                new Date(System.currentTimeMillis() + 3600000 * 7), // date d'arrivée (7h plus tard)
                420, // durée en minutes
                599.99, // prix
                150, // places disponibles
                "Confirmé"
        );

        try {
            volService.ajouter(nouveauVol);
            System.out.println("\n✅ Vol ajouté avec succès!");
            afficherDetailsVol(nouveauVol);
            return nouveauVol.getIdVol();
        } catch (SQLException e) {
            System.out.println("\n❌ Erreur lors de l'ajout du vol: " + e.getMessage());
            return -1;
        }
    }

    private static void testAjoutReservation(int idVol) {
        serviceReservationVol reservationService = new serviceReservationVol();

        // Création d'une réservation de test
        ReservationVol nouvelleReservation = new ReservationVol(
                idVol,       // ID du vol créé précédemment
                1001,        // ID étudiant
                2,           // Nombre de places
                1199.98,     // Prix total (2 x 599.99)
                "RES-AF-001"  // Référence
        );

        // Définir des options supplémentaires
        nouvelleReservation.setClasse("Affaires");
        nouvelleReservation.setTypeBagage("Valise 25kg + Bagage à main");
        nouvelleReservation.setCommentaires("Sièges côte à côte si possible");

        try {
            reservationService.ajouter(nouvelleReservation);
            System.out.println("\n✅ Réservation ajoutée avec succès!");
            afficherDetailsReservation(nouvelleReservation);

            // Tester la récupération
            ReservationVol reservationRecuperee = reservationService.recupererParId(nouvelleReservation.getIdReservation());
            System.out.println("\n✅ Réservation récupérée:");
            afficherDetailsReservation(reservationRecuperee);

        } catch (SQLException e) {
            System.out.println("\n❌ Erreur lors de l'ajout de la réservation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void afficherDetailsVol(Vols vol) {
        System.out.println("Détails du vol:");
        System.out.println("ID: " + vol.getIdVol());
        System.out.println("Compagnie: " + vol.getCompagnie());
        System.out.println("Numéro: " + vol.getNumeroVol());
        System.out.println("Trajet: " + vol.getAeroportDepart() + " (" + vol.getVilleDepart() +
                ") → " + vol.getAeroportArrivee() + " (" + vol.getVilleArrivee() + ")");
        System.out.println("Dates: " + vol.getDateDepart() + " → " + vol.getDateArrivee());
        System.out.println("Prix: " + vol.getPrixStandard() + " €");
        System.out.println("Places disponibles: " + vol.getPlacesDisponibles());
        System.out.println("Statut: " + vol.getStatut());
    }

    private static void afficherDetailsReservation(ReservationVol reservation) {
        System.out.println("Détails de la réservation:");
        System.out.println("ID: " + reservation.getIdReservation());
        System.out.println("Référence: " + reservation.getReferenceReservation());
        System.out.println("ID Vol: " + reservation.getIdVol());
        System.out.println("ID Étudiant: " + reservation.getIdEtudiant());
        System.out.println("Places: " + reservation.getNombrePlaces());
        System.out.println("Classe: " + reservation.getClasse());
        System.out.println("Prix total: " + reservation.getPrixTotal() + " €");
        System.out.println("Statut paiement: " + reservation.getStatutPaiement());
        System.out.println("Type bagage: " + reservation.getTypeBagage());
        System.out.println("Commentaires: " + reservation.getCommentaires());
    }
}