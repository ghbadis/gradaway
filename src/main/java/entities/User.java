package entities;

import java.time.LocalDate;

public class User {
    private int id, age, cin, telephone, annee_obtention_diplome;
    private double moyennes;
    private String nom, prenom, nationalite, email, domaine_etude, universite_origine, role, mdp, image;
    private LocalDate dateNaissance;

    public User(int id, int age, int cin, int telephone, double moyennes, int annee_obtention_diplome,
                String nom, String prenom, String nationalite, String email,
                String domaine_etude, String universite_origine, String role,
                LocalDate dateNaissance, String mdp, String image) {
        this.id = id;
        this.age = age;
        this.cin = cin;
        this.telephone = telephone;
        this.moyennes = moyennes;
        this.annee_obtention_diplome = annee_obtention_diplome;
        this.nom = nom;
        this.prenom = prenom;
        this.nationalite = nationalite;
        this.email = email;
        this.domaine_etude = domaine_etude;
        this.universite_origine = universite_origine;
        this.role = role;
        this.dateNaissance = dateNaissance;
        this.mdp = mdp;
        this.image = image;
    }

    public User(int age, int cin, int telephone, double moyennes, int annee_obtention_diplome,
                String nom, String prenom, String nationalite, String email,
                String domaine_etude, String universite_origine, String role,
                LocalDate dateNaissance, String mdp, String image) {
        this.age = age;
        this.cin = cin;
        this.telephone = telephone;
        this.moyennes = moyennes;
        this.annee_obtention_diplome = annee_obtention_diplome;
        this.nom = nom;
        this.prenom = prenom;
        this.nationalite = nationalite;
        this.email = email;
        this.domaine_etude = domaine_etude;
        this.universite_origine = universite_origine;
        this.role = role;
        this.dateNaissance = dateNaissance;
        this.mdp = mdp;
        this.image = image;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public int getCin() {
        return cin;
    }

    public void setCin(int cin) {
        this.cin = cin;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public double getMoyennes() {
        return moyennes;
    }

    public void setMoyennes(double moyennes) {
        this.moyennes = moyennes;
    }

    public int getAnnee_obtention_diplome() {
        return annee_obtention_diplome;
    }

    public void setAnnee_obtention_diplome(int annee_obtention_diplome) {
        this.annee_obtention_diplome = annee_obtention_diplome;
    }

    public String getDomaine_etude() {
        return domaine_etude;
    }

    public void setDomaine_etude(String domaine_etude) {
        this.domaine_etude = domaine_etude;
    }

    public String getUniversite_origine() {
        return universite_origine;
    }

    public void setUniversite_origine(String universite_origine) {
        this.universite_origine = universite_origine;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", age=" + age +
                ", cin=" + cin +
                ", telephone=" + telephone +
                ", moyennes=" + moyennes +
                ", annee_obtention_diplome=" + annee_obtention_diplome +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", nationalite='" + nationalite + '\'' +
                ", email='" + email + '\'' +
                ", domaine_etude='" + domaine_etude + '\'' +
                ", universite_origine='" + universite_origine + '\'' +
                ", role='" + role + '\'' +
                ", dateNaissance=" + dateNaissance +
                ", mdp='" + mdp + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
