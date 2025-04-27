package tests;

import Services.ServiceFoyer;
import Services.ServiceReservationFoyer;
import Services.ServiceReservationRestaurant;
import Services.ServiceRestaurant;
import entities.Foyer;
import entities.ReservationFoyer;
import entities.ReservationRestaurant;
import entities.Restaurant;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Mainwajdi extends Application {
    public static void main(String[] args) {
        ServiceFoyer serviceFoyer = new ServiceFoyer();
        ServiceRestaurant serviceRestaurant = new ServiceRestaurant();
        ServiceReservationFoyer serviceReservationFoyer = new ServiceReservationFoyer();
        ServiceReservationRestaurant serviceReservationRestaurant = new ServiceReservationRestaurant();


        try {
            // Test Foyer CRUD
            System.out.println("=== Testing Foyer CRUD ===");
            testFoyerCRUD(serviceFoyer);

            // Test Restaurant CRUD
            System.out.println("\n=== Testing Restaurant CRUD ===");
            testRestaurantCRUD(serviceRestaurant);

            // Test ReservationFoyer CRUD
            System.out.println("\n=== Testing ReservationFoyer CRUD ===");
            testReservationFoyerCRUD(serviceFoyer, serviceReservationFoyer);

            // Test ReservationRestaurant CRUD
            System.out.println("\n=== Testing ReservationRestaurant CRUD ===");
            testReservationRestaurantCRUD(serviceRestaurant, serviceReservationRestaurant);

        } catch (SQLException e) {
            System.err.println("Error during CRUD operations: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testFoyerCRUD(ServiceFoyer service) throws SQLException {
        // Create
        System.out.println("Testing Create (Ajouter)");
        Foyer newFoyer = new Foyer("Foyer Test", "123 Test Street", "Test City", "Test Country", 10, 50);
        service.ajouter(newFoyer);
        System.out.println("New foyer created successfully");

        // Read
        System.out.println("\nTesting Read (Recuperer)");
        List<Foyer> foyers = service.recuperer();
        System.out.println("All foyers:");
        for (Foyer f : foyers) {
            System.out.println(f);
        }

        // Update
        System.out.println("\nTesting Update (Modifier)");
        if (!foyers.isEmpty()) {
            Foyer foyerToUpdate = foyers.get(0);
            foyerToUpdate.setNom("Updated Foyer Name");
            foyerToUpdate.setCapacite(75);
            service.modifier(foyerToUpdate);
            System.out.println("Foyer updated successfully");
        }

        // Delete
        System.out.println("\nTesting Delete (Supprimer)");
        if (!foyers.isEmpty()) {
            Foyer foyerToDelete = foyers.get(0);
            service.supprimer(foyerToDelete);
            System.out.println("Foyer deleted successfully");
        }
    }

    private static void testRestaurantCRUD(ServiceRestaurant service) throws SQLException {
        // Create
        System.out.println("Testing Create (Ajouter)");
        Restaurant newRestaurant = new Restaurant(
                "Test Restaurant",
                "456 Food Street",
                "Food City",
                "Food Country",
                100,
                "09:00",
                "22:00",
                "1234567890",
                "test@restaurant.com"
        );
        service.ajouter(newRestaurant);
        System.out.println("New restaurant created successfully");

        // Read
        System.out.println("\nTesting Read (Recuperer)");
        List<Restaurant> restaurants = service.recuperer();
        System.out.println("All restaurants:");
        for (Restaurant r : restaurants) {
            System.out.println(r);
        }

        // Update
        System.out.println("\nTesting Update (Modifier)");
        if (!restaurants.isEmpty()) {
            Restaurant restaurantToUpdate = restaurants.get(0);
            restaurantToUpdate.setNom("Updated Restaurant Name");
            restaurantToUpdate.setCapaciteTotale(150);
            service.modifier(restaurantToUpdate);
            System.out.println("Restaurant updated successfully");
        }

        // Delete
        System.out.println("\nTesting Delete (Supprimer)");
        if (!restaurants.isEmpty()) {
            Restaurant restaurantToDelete = restaurants.get(0);
            service.supprimer(restaurantToDelete);
            System.out.println("Restaurant deleted successfully");
        }
    }

    private static void testReservationFoyerCRUD(ServiceFoyer foyerService, ServiceReservationFoyer reservationService) throws SQLException {
        // First create a foyer to use for the reservation
        System.out.println("Creating a foyer for the reservation");
        Foyer newFoyer = new Foyer("Reservation Foyer", "456 Reservation St", "Reservation City", "Reservation Country", 20, 100);
        foyerService.ajouter(newFoyer);

        // Get the created foyer's ID
        List<Foyer> foyers = foyerService.recuperer();
        if (foyers.isEmpty()) {
            throw new SQLException("Failed to create foyer for reservation");
        }
        int foyerId = foyers.get(0).getIdFoyer();

        // Create
        System.out.println("Testing Create (Ajouter)");
        ReservationFoyer newReservation = new ReservationFoyer(
                foyerId, // Use the actual foyer ID
                31, // idEtudiant
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                LocalDate.now()
        );
        reservationService.ajouter(newReservation);
        System.out.println("New foyer reservation created successfully");

        // Read
        System.out.println("\nTesting Read (Recuperer)");
        List<ReservationFoyer> reservations = reservationService.recuperer();
        System.out.println("All foyer reservations:");
        for (ReservationFoyer r : reservations) {
            System.out.println(r);
        }

        // Update
        System.out.println("\nTesting Update (Modifier)");
        if (!reservations.isEmpty()) {
            ReservationFoyer reservationToUpdate = reservations.get(0);
            reservationToUpdate.setDateFin(LocalDate.now().plusDays(14));
            reservationService.modifier(reservationToUpdate);
            System.out.println("Foyer reservation updated successfully");
        }

        // Delete
        System.out.println("\nTesting Delete (Supprimer)");
        if (!reservations.isEmpty()) {
            ReservationFoyer reservationToDelete = reservations.get(0);
            reservationService.supprimer(reservationToDelete);
            System.out.println("Foyer reservation deleted successfully");
        }

        // Clean up - delete the test foyer
        foyerService.supprimer(foyers.get(0));
    }

    private static void testReservationRestaurantCRUD(ServiceRestaurant restaurantService, ServiceReservationRestaurant reservationService) throws SQLException {
        // First create a restaurant to use for the reservation
        System.out.println("Creating a restaurant for the reservation");
        Restaurant newRestaurant = new Restaurant(
                "Test Restaurant",
                "456 Food Street",
                "Food City",
                "Food Country",
                100,
                "09:00",
                "22:00",
                "1234567890",
                "test@restaurant.com"
        );
        restaurantService.ajouter(newRestaurant);

        // Get the created restaurant's ID
        List<Restaurant> restaurants = restaurantService.recuperer();
        if (restaurants.isEmpty()) {
            throw new SQLException("Failed to create restaurant for reservation");
        }
        int restaurantId = restaurants.get(0).getIdRestaurant();

        // Create
        System.out.println("Testing Create (Ajouter)");
        ReservationRestaurant newReservation = new ReservationRestaurant(
                restaurantId, // Use the actual restaurant ID
                31, // idEtudiant
                LocalDate.now(),
                4 // number of people
        );
        reservationService.ajouter(newReservation);
        System.out.println("New restaurant reservation created successfully");

        // Read
        System.out.println("\nTesting Read (Recuperer)");
        List<ReservationRestaurant> reservations = reservationService.recuperer();
        System.out.println("All restaurant reservations:");
        for (ReservationRestaurant r : reservations) {
            System.out.println(r);
        }

        // Update
        System.out.println("\nTesting Update (Modifier)");
        if (!reservations.isEmpty()) {
            ReservationRestaurant reservationToUpdate = reservations.get(0);
            reservationToUpdate.setNombrePersonnes(6);
            reservationService.modifier(reservationToUpdate);
            System.out.println("Restaurant reservation updated successfully");
        }

        // Delete
        System.out.println("\nTesting Delete (Supprimer)");
        if (!reservations.isEmpty()) {
            ReservationRestaurant reservationToDelete = reservations.get(0);
            reservationService.supprimer(reservationToDelete);
            System.out.println("Restaurant reservation deleted successfully");
        }

        // Clean up - delete the test restaurant
        restaurantService.supprimer(restaurants.get(0));
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}
