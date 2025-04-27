package Services;



import entities.Restaurant;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRestaurant implements IService<Restaurant> {

    private Connection con;

    public ServiceRestaurant() {
        con = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Restaurant restaurant) throws SQLException {
        String req = "INSERT INTO restaurant(nom, adresse, ville, pays, capacitetotal, horaireouverture, horairefermeture, telephone, email) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, restaurant.getNom());
        ps.setString(2, restaurant.getAdresse());
        ps.setString(3, restaurant.getVille());
        ps.setString(4, restaurant.getPays());
        ps.setInt(5, restaurant.getCapaciteTotale());
        ps.setString(6, restaurant.getHoraireOuverture());
        ps.setString(7, restaurant.getHoraireFermeture());
        ps.setString(8, restaurant.getTelephone());
        ps.setString(9, restaurant.getEmail());
        ps.executeUpdate();
        System.out.println("Restaurant ajouté !");
    }

    @Override
    public void modifier(Restaurant restaurant) throws SQLException {
        String req = "UPDATE restaurant SET nom=?, adresse=?, ville=?, pays=?, capacitetotal=?, horaireouverture=?, horairefermeture=?, telephone=?, email=? WHERE idRestaurant=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setString(1, restaurant.getNom());
        ps.setString(2, restaurant.getAdresse());
        ps.setString(3, restaurant.getVille());
        ps.setString(4, restaurant.getPays());
        ps.setInt(5, restaurant.getCapaciteTotale());
        ps.setString(6, restaurant.getHoraireOuverture());
        ps.setString(7, restaurant.getHoraireFermeture());
        ps.setString(8, restaurant.getTelephone());
        ps.setString(9, restaurant.getEmail());
        ps.setInt(10, restaurant.getIdRestaurant());
        ps.executeUpdate();
        System.out.println("Restaurant modifié !");
    }

    @Override
    public boolean supprimer(Restaurant restaurant) throws SQLException {
        String req = "DELETE FROM restaurant WHERE idRestaurant=?";
        PreparedStatement ps = con.prepareStatement(req);
        ps.setInt(1, restaurant.getIdRestaurant());
        ps.executeUpdate();
        System.out.println("Restaurant supprimé !");
        return false;
    }

    @Override
    public List<Restaurant> recuperer() throws SQLException {
        List<Restaurant> restaurants = new ArrayList<>();
        String req = "SELECT * FROM restaurant";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(req);

        while (rs.next()) {
            int id = rs.getInt("idRestaurant");
            String nom = rs.getString("nom");
            String adresse = rs.getString("adresse");
            String ville = rs.getString("ville");
            String pays = rs.getString("pays");
            int capacite = rs.getInt("capacitetotal");
            String ouverture = rs.getString("horaireouverture");
            String fermeture = rs.getString("horairefermeture");
            String tel = rs.getString("telephone");
            String email = rs.getString("email");

            Restaurant r = new Restaurant(id, nom, adresse, ville, pays, capacite, ouverture, fermeture, tel, email);
            restaurants.add(r);
        }

        return restaurants;
    }

    }

