package Services;



import entities.ReservationRestaurant;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservationRestaurant implements IService<ReservationRestaurant> {

    private final Connection con;

    public ServiceReservationRestaurant() {
        con = MyDatabase.getInstance().getCnx();
    }
    
    /**
     * Retourne la connexion à la base de données
     * @return Connexion à la base de données
     */
    public Connection getCon() {
        return con;
    }

    @Override
    public void ajouter(ReservationRestaurant reservation) throws SQLException {
        String req = "INSERT INTO reservationrestaurant (idRestaurant, IdEtudient, dateReservation, nombrePersonne) " +
                "VALUES (?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getIdRestaurant());
        ps.setInt(2, reservation.getIdEtudiant());
        ps.setDate(3, Date.valueOf(reservation.getDateReservation()));
        ps.setInt(4, reservation.getNombrePersonnes());
        ps.executeUpdate();
        System.out.println("Réservation restaurant ajoutée !");
    }

    @Override
    public void modifier(ReservationRestaurant reservation) throws SQLException {
        String req = "UPDATE reservationrestaurant SET idRestaurant=?, IdEtudient=?, dateReservation=?, nombrePersonne=? " +
                "WHERE idReservation=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getIdRestaurant());
        ps.setInt(2, reservation.getIdEtudiant());
        ps.setDate(3, Date.valueOf(reservation.getDateReservation()));
        ps.setInt(4, reservation.getNombrePersonnes());
        ps.setInt(5, reservation.getIdReservation());
        ps.executeUpdate();
        System.out.println("Réservation restaurant modifiée !");
    }

    @Override
    public boolean supprimer(ReservationRestaurant reservation) throws SQLException {
        String req = "DELETE FROM reservationrestaurant WHERE idReservation=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, reservation.getIdReservation());
        ps.executeUpdate();
        System.out.println("Réservation restaurant supprimée !");
        return false;
    }

    @Override
    public List<ReservationRestaurant> recuperer() throws SQLException {
        List<ReservationRestaurant> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservationrestaurant";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int id = rs.getInt("idReservation");
            int idRestaurant = rs.getInt("idRestaurant");
            int idEtudiant = rs.getInt("IdEtudient");
            LocalDate dateReservation = rs.getDate("dateReservation").toLocalDate();
            int nombrePersonnes = rs.getInt("nombrePersonne");

            ReservationRestaurant r = new ReservationRestaurant(id, idRestaurant, idEtudiant, dateReservation, nombrePersonnes);
            reservations.add(r);
        }

        return reservations;
    }
    
    /**
     * Récupère les réservations d'un étudiant
     * @param idEtudiant ID de l'étudiant
     * @return Liste des réservations de l'étudiant
     * @throws SQLException En cas d'erreur SQL
     */
    /**
     * Récupère toutes les réservations de restaurant sans filtrer
     * @return Liste de toutes les réservations
     * @throws SQLException En cas d'erreur SQL
     */
    public List<ReservationRestaurant> getAllReservations() throws SQLException {
        List<ReservationRestaurant> reservations = new ArrayList<>();
        System.out.println("Récupération de toutes les réservations");
        
        String req = "SELECT * FROM reservationrestaurant";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int id = rs.getInt("idReservation");
            int idRestaurant = rs.getInt("idRestaurant");
            int idEtudiant = rs.getInt("IdEtudient");
            LocalDate dateReservation = rs.getDate("dateReservation").toLocalDate();
            int nombrePersonnes = rs.getInt("nombrePersonne");

            ReservationRestaurant r = new ReservationRestaurant(id, idRestaurant, idEtudiant, dateReservation, nombrePersonnes);
            reservations.add(r);
            System.out.println("Réservation trouvée - ID: " + id + ", Restaurant: " + idRestaurant + ", Etudiant: " + idEtudiant);
        }

        System.out.println("Total des réservations récupérées: " + reservations.size());
        return reservations;
    }
    
    public List<ReservationRestaurant> getReservationsByEtudiantId(int idEtudiant) throws SQLException {
        List<ReservationRestaurant> reservations = new ArrayList<>();
        System.out.println("Début de la recherche des réservations pour l'ID étudiant: " + idEtudiant);
        
        // Vérifier d'abord toutes les réservations pour débogage
        Statement st = con.createStatement();
        ResultSet allRs = st.executeQuery("SELECT * FROM reservationrestaurant");
        System.out.println("Toutes les réservations dans la base de données:");
        int count = 0;
        while (allRs.next()) {
            count++;
            System.out.println("ID: " + allRs.getInt("idReservation") + 
                             ", Restaurant: " + allRs.getInt("idRestaurant") + 
                             ", Etudiant: " + allRs.getInt("IdEtudient"));
        }
        System.out.println("Total des réservations dans la base: " + count);
        
        // Maintenant rechercher les réservations de l'étudiant spécifique
        String req = "SELECT * FROM reservationrestaurant WHERE IdEtudient=?";
        System.out.println("Exécution de la requête: " + req + " avec ID: " + idEtudiant);
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, idEtudiant);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("idReservation");
            int idRestaurant = rs.getInt("idRestaurant");
            LocalDate dateReservation = rs.getDate("dateReservation").toLocalDate();
            int nombrePersonnes = rs.getInt("nombrePersonne");

            ReservationRestaurant r = new ReservationRestaurant(id, idRestaurant, idEtudiant, dateReservation, nombrePersonnes);
            reservations.add(r);
        }

        return reservations;
    }
}
