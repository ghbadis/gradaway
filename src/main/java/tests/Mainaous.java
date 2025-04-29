package tests;

import entities.Evenement;
import entities.ReservationEvenement;
import Services.ServiceEvenement;
import Services.ServiceReservationEvenement;
import utils.MyDatabase;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        ServiceEvenement serviceEvenement = new ServiceEvenement();
        ServiceReservationEvenement serviceReservation = new ServiceReservationEvenement();

        try {
            // Ajouter un événement
            serviceEvenement.ajouter(new Evenement(
                    "Tournoi Universitaire de eSport",
                    "Compétition inter-facultés sur League of Legends,FIFA et Rocket League.",
                    "2025-06-3",
                    "Salle Polyvalente, Campus Est",
                    "Gaming",
                    100
            ));

            // Modifier un événement
           /* serviceEvenement.modifier(new Evenement(
                1,
                "Conférence Java Avancée",
                "Programmation avancée en Java",
                "2024-03-25",
                "Salle B203",
                "Informatique",
                30
            ));*/

            // Afficher tous les événements
            System.out.println("Liste des événements :");
            System.out.println(serviceEvenement.recuperer());

            // Supprimer un événement
            //serviceEvenement.supprimer(new Evenement(1, "", "", "", "", "", 0));

            // Ajouter une réservation
            /*serviceReservation.ajouter(new ReservationEvenement(
                1, // id_etudiant
                2, // id_evenement
                "Informatique",
                "Confirmée",
                "2024-03-19"
            )); */

            // Modifier une réservation
            /*serviceReservation.modifier(new ReservationEvenement(
                1, // id_reservation
                1, // id_etudiant
                1, // id_evenement
                "Informatique",
                "Annulée",
                "2024-03-19"
            ));*/

            // Afficher toutes les réservations
            System.out.println("\nListe des réservations :");
            System.out.println(serviceReservation.recuperer());

            // Supprimer une réservation
            //serviceReservation.supprimer(new ReservationEvenement(1, 0, 0, "", "", ""));

        } catch (SQLException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
