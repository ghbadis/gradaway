package entities;

public class Universite {

        private int id_universite;
        private String Nom;
        private String Ville;
        private String Adresse_universite;
        private String Domaine;
        private double Frais;

        public Universite(int id_universite, String Nom, String Ville, String Adresse_universite, String Domaine, double Frais) {
            this.id_universite = id_universite;
            this.Nom = Nom;
            this.Ville = Ville;
            this.Adresse_universite = Adresse_universite;
            this.Domaine = Domaine;
            this.Frais = Frais;
        }

        public Universite(String Nom, String Ville, String Adresse_universite, String Domaine, double Frais) {
            this.Nom = Nom;
            this.Ville = Ville;
            this.Adresse_universite = Adresse_universite;
            this.Domaine = Domaine;
            this.Frais = Frais;
        }

        public int getId_universite() {
            return this.id_universite;
        }

        public void setId_universite(int id_universite) {
            this.id_universite = id_universite;
        }

        public String getNom() {
            return this.Nom;
        }

        public void setNom(String Nom) {
            this.Nom = Nom;
        }

        public String getVille() {
            return this.Ville;
        }

        public void setVille(String Ville) {
            this.Ville = Ville;
        }

        public String getAdresse_universite() {
            return this.Adresse_universite;
        }

        public void setAdresse_universite(String Adresse_universite) {
            this.Adresse_universite = Adresse_universite;
        }

        public String getDomaine() {
            return this.Domaine;
        }

        public void setDomaine(String Domaine) {
            this.Domaine = Domaine;
        }

        public double getFrais() {
            return this.Frais;
        }

        public void setFrais(double Frais) {
            this.Frais = Frais;
        }

        @Override
        public String toString() {
            return "Universite{" +
                    "id_universite=" + id_universite +
                    ", Nom='" + Nom + '\'' +
                    ", Ville='" + Ville + '\'' +
                    ", Adresse_universite='" + Adresse_universite + '\'' +
                    ", Domaine='" + Domaine + '\'' +
                    ", Frais=" + Frais +
                    '}';
        }
    }

