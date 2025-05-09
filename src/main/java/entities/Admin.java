package entities;

import java.time.LocalDate;

public class Admin extends User {
    public Admin(int id, int age, int cin, int telephone, int moyennes, int annee_obtention_diplome, String nom, String prenom, String nationalite, String email, String domaine_etude, String universite_origine, String role, LocalDate dateNaissance, String mdp,String image ) {
        super(id, age, cin, telephone, moyennes, annee_obtention_diplome, nom, prenom, nationalite, email, domaine_etude, universite_origine, role, dateNaissance, mdp,image);
    }

    public Admin(int age, int cin, int telephone, int moyennes, int annee_obtention_diplome, String nom, String prenom, String nationalite, String email, String domaine_etude, String universite_origine, String role, LocalDate dateNaissance, String mdp,String image) {
        super(age, cin, telephone, moyennes, annee_obtention_diplome, nom, prenom, nationalite, email, domaine_etude, universite_origine, role, dateNaissance, mdp,image);
    }
}
//test