package Services;

import entities.Universite;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

    public class ServiceUniversite implements IService<Universite> {
        private Connection con = MyDatabase.getInstance().getCnx();

        public ServiceUniversite() {
        }

        public void ajouter(Universite universite) throws SQLException {
            String req = "INSERT INTO universite(Nom, Ville, Adresse_universite, Domaine, Frais) " +
                    "VALUES ('" + universite.getNom() + "','" + universite.getVille() + "','" +
                    universite.getAdresse_universite() + "','" + universite.getDomaine() + "'," +
                    universite.getFrais() + ")";
            Statement st = this.con.createStatement();
            st.executeUpdate(req);
            System.out.println("Université ajoutée");
        }

        public void modifier(Universite universite) throws SQLException {
            String req = "UPDATE universite SET Nom=?, Ville=?, Adresse_universite=?, Domaine=?, Frais=? " +
                    "WHERE id_universite=?";
            PreparedStatement ps = this.con.prepareStatement(req);
            ps.setString(1, universite.getNom());
            ps.setString(2, universite.getVille());
            ps.setString(3, universite.getAdresse_universite());
            ps.setString(4, universite.getDomaine());
            ps.setDouble(5, universite.getFrais());
            ps.setInt(6, universite.getId_universite());
            ps.executeUpdate();
            System.out.println("Université modifiée");
        }

        public boolean supprimer(Universite universite) throws SQLException {
            String req = "DELETE FROM universite WHERE id_universite=?";
            PreparedStatement ps = this.con.prepareStatement(req);
            ps.setInt(1, universite.getId_universite());
            ps.executeUpdate();
            System.out.println("Université supprimée");
            return false;
        }

        public List<Universite> recuperer() throws SQLException {
            List<Universite> universites = new ArrayList<>();
            String req = "SELECT * FROM universite";
            Statement st = this.con.createStatement();
            ResultSet rs = st.executeQuery(req);

            while(rs.next()) {
                int id = rs.getInt("id_universite");
                String nom = rs.getString("Nom");
                String ville = rs.getString("Ville");
                String adresse = rs.getString("Adresse_universite");
                String domaine = rs.getString("Domaine");
                double frais = rs.getDouble("Frais");

                Universite u = new Universite(id, nom, ville, adresse, domaine, frais);
                universites.add(u);
            }

            return universites;
        }
    }

