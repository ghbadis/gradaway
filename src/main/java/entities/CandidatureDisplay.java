package entities;

public class CandidatureDisplay {
    private final int idC;
    private final String nom;
    private final String prenom;
    private final String domaine;

    public CandidatureDisplay(int idC, String nom, String prenom, String domaine) {
        this.idC = idC;
        this.nom = nom;
        this.prenom = prenom;
        this.domaine = domaine;
    }

    public int getIdC() { return idC; }
    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getDomaine() { return domaine; }

    @Override
    public String toString() {
        return nom + " " + prenom + " - " + domaine;
    }
} 