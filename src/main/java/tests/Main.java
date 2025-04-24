package tests;


import Services.*;
import entities.*;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ServiceUser serviceUser = new ServiceUser();
        ServiceDossier serviceDossier = new ServiceDossier();


        ServiceUniversite serviceUniversite = new ServiceUniversite();
        ServiceConditature serviceConditature = new ServiceConditature();

        ServiceExpert serviceExpert = new ServiceExpert();
        ServiceEntretien serviceEntretien = new ServiceEntretien();


        ServiceEvenement serviceEvenement = new ServiceEvenement();
        ServiceReservationEvenement serviceReservation = new ServiceReservationEvenement();


        try {

            //✅✅✅✅✅✅✅✅ //userrrrrr

            //Ajouter une user
            serviceUser.ajouter(new User(
                    20,                    // age
                    12345678,             // cin
                    98765432,             // telephone
                    15,                   // moyennes
                    2023,                 // annee_obtention_diplome
                    "badis",              // nom
                    "ghaoui",             // prenom
                    "tunisien",           // nationalite
                    "badis10@gmail.com",  // email
                    "Informatique",       // domaine_etude
                    "ISET Nabeul",        // universite_origine
                    "Etudiant",           // role
                    LocalDate.of(2000, 1, 1),  // dateNaissance
                    "password123"         // mdp
            ));

            //Modifier user
            /*serviceUser.modifier(new User(
                    1,                    // id
                    20,                   // age
                    12345678,             // cin
                    98765432,             // telephone
                    15,                   // moyennes
                    2023,                 // annee_obtention_diplome
                    "iyed",               // nom
                    "ghaoui",             // prenom
                    "tunisien",           // nationalite
                    "badis10@gmail.com",  // email
                    "Informatique",       // domaine_etude
                    "ISET Nabeul",        // universite_origine
                    "Etudiant",           // role
                    LocalDate.of(2000, 1, 1),  // dateNaissance
                    "newpassword123"      // mdp
            ));*/

            // Supprimer user
            /*serviceUser.supprimer(new User(
                    1,                    // id
                    20,                   // age
                    12345678,             // cin
                    98765432,             // telephone
                    15,                   // moyennes
                    2023,                 // annee_obtention_diplome
                    "iyed",               // nom
                    "ghaoui",             // prenom
                    "tunisien",           // nationalite
                    "badis10@gmail.com",  // email
                    "Informatique",       // domaine_etude
                    "ISET Nabeul",        // universite_origine
                    "Etudiant",           // role
                    LocalDate.of(2000, 1, 1),  // dateNaissance
                    "password123"         // mdp
            ));*/

            //ajouter Dossier
            serviceDossier.ajouter(new Dossier(
                    31,
                    "cin",
                    "photo",
                    "diplome baccalaureat",
                    "relevenote",
                    "Diplome_obtenus",
                    "Letttre_motivation",
                    "Dossier_sante",
                    "Cv",
                    LocalDate.of(2000, 1, 1)
            ));

            //Modifier Dossier
           /* serviceDossier.modifier(new Dossier(
                    1,
                     20,
                    "cin",
                    "photo",
                    "diplome baccalaureat",
                    "relevenote",
                    "Diplome_obtenus",
                    "Letttre_motivation",
                    "Dossier_sante",
                    "Cv",
                    LocalDate.of(2000, 1, 1)
             ));*/

        // Supprimer Dossier
            /* serviceDossier.supprimer(new Dossier(
                1,
                     20,
                    "cin",
                    "photo",
                    "diplome baccalaureat",
                    "relevenote",
                    "Diplome_obtenus",
                    "Letttre_motivation",
                    "Dossier_sante",
                    "Cv",
                    LocalDate.of(2000, 1, 1)
             ));*/



            // Afficher toutes les users
            System.out.println(serviceUser.recuperer());
            System.out.println(serviceDossier.recuperer());






//✅✅✅✅✅✅✅✅ //universite et conditature

            Universite u1 = new Universite("MIT", "Boston", "77 Mass Ave", "Engineering", 60000.0);
            serviceUniversite.ajouter(u1);

            Universite u2 = new Universite("Oxford", "Oxford", "University Offices", "Humanities", 35000.0);
            serviceUniversite.ajouter(u2);

            System.out.println("Liste initiale des universités:");
            serviceUniversite.recuperer().forEach(System.out::println);

            u1.setFrais(65000.0);
            serviceUniversite.modifier(u1);

            System.out.println("\nAprès modification:");
            serviceUniversite.recuperer().forEach(System.out::println);

            serviceUniversite.supprimer(u2);

            System.out.println("\nAprès suppression:");
            serviceUniversite.recuperer().forEach(System.out::println);


            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Conditature c1 = new Conditature(35, 31, 50, today, "Computer Science");
            serviceConditature.ajouter(c1);

           Conditature c2 = new Conditature(35, 31, 50, today, "Mathematics");
            serviceConditature.ajouter(c2);

            System.out.println("\nListe initiale des conditatures:");
            serviceConditature.recuperer().forEach(System.out::println);

            c1.setDomaine("Artificial Intelligence");
            serviceConditature.modifier(c1);

            System.out.println("\nAprès modification:");
            serviceConditature.recuperer().forEach(System.out::println);

            serviceConditature.supprimer(c2);

            System.out.println("\nAprès suppression:");
            serviceConditature.recuperer().forEach(System.out::println);


           // ✅✅✅✅✅✅✅✅ //expert et entretien

            serviceExpert.ajouter(new Expert(31,"Chaabene", "Med Dhia", "dhia.expert@example.com", "Intelligence Artificielle"));

            System.out.println(serviceExpert.recuperer());

            serviceEntretien.ajouter(new Entretien(24, 31, LocalDate.of(2025, 5, 20), LocalTime.of(14, 30), "En attente"));
            //serviceEntretien.modifier(new Entretien(11, 2, 3, LocalDate.of(2025, 5, 21), LocalTime.of(15, 30), "Confirmé"));
            //serviceEntretien.supprimer(new Entretien(12, 2, 3, LocalDate.of(2025, 5, 21), LocalTime.of(15, 30), "Confirmé"));

            System.out.println(serviceEntretien.recuperer());


         //   ✅✅✅✅✅✅✅✅ //evenement

// Ajouter un événement
            serviceEvenement.ajouter(new Evenement(
                    "Conférence Java",
                    "Introduction à Java et Spring Boot",
                    "2024-03-20",
                    "Salle A101",
                    "Informatique",
                    50
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
            serviceReservation.ajouter(new ReservationEvenement(
                    1, // id_etudiant
                    2, // id_evenement
                    "Informatique",
                    "Confirmée",
                    "2024-03-19"
            ));

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


            //✅✅✅✅✅✅✅✅ //vols





        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}

