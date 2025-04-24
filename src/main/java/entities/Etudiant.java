package entities;

import java.time.LocalDate;

public class Etudiant  extends User {
    public Etudiant(int id, int age, int cin, int telephone, int moyennes, int annee_obtention_diplome, String nom, String prenom, String nationalite, String email, String domaine_etude, String universite_origine, String role, LocalDate dateNaissance, String mdp) {
        super(id, age, cin, telephone, moyennes, annee_obtention_diplome, nom, prenom, nationalite, email, domaine_etude, universite_origine, role, dateNaissance, mdp);
    }

    public Etudiant(int age, int cin, int telephone, int moyennes, int annee_obtention_diplome, String nom, String prenom, String nationalite, String email, String domaine_etude, String universite_origine, String role, LocalDate dateNaissance, String mdp) {
        super(age, cin, telephone, moyennes, annee_obtention_diplome, nom, prenom, nationalite, email, domaine_etude, universite_origine, role, dateNaissance, mdp);
    }

}
