package Services;

import entities.Reservation;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation implements IService<Reservation> {

    private Connection conn;
    private PreparedStatement pst;

    public ServiceReservation() {
        conn = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Reservation reservation) throws SQLException {
        String req = "INSERT INTO reservation (id_restaurant, nom_client, email_client, telephone_client, date_reservation, heure_reservation, nombre_personnes, commentaires, statut) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try {
            pst = conn.prepareStatement(req);
            pst.setInt(1, reservation.getIdRestaurant());
            pst.setString(2, reservation.getNomClient());
            pst.setString(3, reservation.getEmailClient());
            pst.setString(4, reservation.getTelephoneClient());
            pst.setString(5, reservation.getDateReservation());
            pst.setString(6, reservation.getHeureReservation());
            pst.setInt(7, reservation.getNombrePersonnes());
            pst.setString(8, reservation.getCommentaires());
            pst.setString(9, reservation.getStatut());
            
            pst.executeUpdate();
            System.out.println("Réservation ajoutée avec succès");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de l'ajout de la réservation: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void modifier(Reservation reservation) throws SQLException {
        String req = "UPDATE reservation SET id_restaurant = ?, nom_client = ?, email_client = ?, telephone_client = ?, " +
                     "date_reservation = ?, heure_reservation = ?, nombre_personnes = ?, commentaires = ?, statut = ? " +
                     "WHERE id_reservation = ?";
        
        try {
            pst = conn.prepareStatement(req);
            pst.setInt(1, reservation.getIdRestaurant());
            pst.setString(2, reservation.getNomClient());
            pst.setString(3, reservation.getEmailClient());
            pst.setString(4, reservation.getTelephoneClient());
            pst.setString(5, reservation.getDateReservation());
            pst.setString(6, reservation.getHeureReservation());
            pst.setInt(7, reservation.getNombrePersonnes());
            pst.setString(8, reservation.getCommentaires());
            pst.setString(9, reservation.getStatut());
            pst.setInt(10, reservation.getIdReservation());
            
            pst.executeUpdate();
            System.out.println("Réservation modifiée avec succès");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la modification de la réservation: " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public boolean supprimer(Reservation reservation) throws SQLException {
        String req = "DELETE FROM reservation WHERE id_reservation = ?";
        
        try {
            pst = conn.prepareStatement(req);
            pst.setInt(1, reservation.getIdReservation());
            
            pst.executeUpdate();
            System.out.println("Réservation supprimée avec succès");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la suppression de la réservation: " + ex.getMessage());
            throw ex;
        }
        return false;
    }

    @Override
    public List<Reservation> recuperer() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation";
        
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(req);
            
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setIdReservation(rs.getInt("id_reservation"));
                reservation.setIdRestaurant(rs.getInt("id_restaurant"));
                reservation.setNomClient(rs.getString("nom_client"));
                reservation.setEmailClient(rs.getString("email_client"));
                reservation.setTelephoneClient(rs.getString("telephone_client"));
                reservation.setDateReservation(rs.getString("date_reservation"));
                reservation.setHeureReservation(rs.getString("heure_reservation"));
                reservation.setNombrePersonnes(rs.getInt("nombre_personnes"));
                reservation.setCommentaires(rs.getString("commentaires"));
                reservation.setStatut(rs.getString("statut"));
                
                reservations.add(reservation);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération des réservations: " + ex.getMessage());
            throw ex;
        }
        
        return reservations;
    }
    
    /**
     * Récupère les réservations pour un restaurant spécifique
     */
    public List<Reservation> recupererParRestaurant(int idRestaurant) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation WHERE id_restaurant = ?";
        
        try {
            pst = conn.prepareStatement(req);
            pst.setInt(1, idRestaurant);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setIdReservation(rs.getInt("id_reservation"));
                reservation.setIdRestaurant(rs.getInt("id_restaurant"));
                reservation.setNomClient(rs.getString("nom_client"));
                reservation.setEmailClient(rs.getString("email_client"));
                reservation.setTelephoneClient(rs.getString("telephone_client"));
                reservation.setDateReservation(rs.getString("date_reservation"));
                reservation.setHeureReservation(rs.getString("heure_reservation"));
                reservation.setNombrePersonnes(rs.getInt("nombre_personnes"));
                reservation.setCommentaires(rs.getString("commentaires"));
                reservation.setStatut(rs.getString("statut"));
                
                reservations.add(reservation);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération des réservations par restaurant: " + ex.getMessage());
            throw ex;
        }
        
        return reservations;
    }
    
    /**
     * Récupère les réservations pour une date spécifique
     */
    public List<Reservation> recupererParDate(String date) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String req = "SELECT * FROM reservation WHERE date_reservation = ?";
        
        try {
            pst = conn.prepareStatement(req);
            pst.setString(1, date);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Reservation reservation = new Reservation();
                reservation.setIdReservation(rs.getInt("id_reservation"));
                reservation.setIdRestaurant(rs.getInt("id_restaurant"));
                reservation.setNomClient(rs.getString("nom_client"));
                reservation.setEmailClient(rs.getString("email_client"));
                reservation.setTelephoneClient(rs.getString("telephone_client"));
                reservation.setDateReservation(rs.getString("date_reservation"));
                reservation.setHeureReservation(rs.getString("heure_reservation"));
                reservation.setNombrePersonnes(rs.getInt("nombre_personnes"));
                reservation.setCommentaires(rs.getString("commentaires"));
                reservation.setStatut(rs.getString("statut"));
                
                reservations.add(reservation);
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération des réservations par date: " + ex.getMessage());
            throw ex;
        }
        
        return reservations;
    }
    
    /**
     * Met à jour le statut d'une réservation
     */
    public void mettreAJourStatut(int idReservation, String nouveauStatut) throws SQLException {
        String req = "UPDATE reservation SET statut = ? WHERE id_reservation = ?";
        
        try {
            pst = conn.prepareStatement(req);
            pst.setString(1, nouveauStatut);
            pst.setInt(2, idReservation);
            
            pst.executeUpdate();
            System.out.println("Statut de la réservation mis à jour avec succès");
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la mise à jour du statut de la réservation: " + ex.getMessage());
            throw ex;
        }
    }
}
