package entities;

public class Expert {
    private int id_user,id_expert, anneeExperience;
    private String nom_expert,prenom_expert,email, specialite, telephone;

    // Constructeur par défaut
    public Expert() {
    }

    public Expert(int id_user, int id_expert, String nom_expert, String prenom_expert, String specialite, String email, String telephone, int anneeExperience) {
        this.id_user = id_user;
        this.id_expert = id_expert;
        this.nom_expert = nom_expert;
        this.prenom_expert = prenom_expert;
        this.email = email;
        this.specialite = specialite;
        this.telephone = telephone;
        this.anneeExperience = anneeExperience;
    }

    public Expert(String nom_expert, String prenom_expert, String email, String specialite, String telephone, int anneeExperience) {
        this.nom_expert = nom_expert;
        this.prenom_expert = prenom_expert;
        this.email = email;
        this.specialite = specialite;
        this.telephone = telephone;
        this.anneeExperience = anneeExperience;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public int getId_expert() {
        return id_expert;
    }

    public void setId_expert(int id_expert) {
        this.id_expert = id_expert;
    }

    public String getNom_expert() {
        return nom_expert;
    }

    public void setNom_expert(String nom_expert) {
        this.nom_expert = nom_expert;
    }

    public String getPrenom_expert() {
        return prenom_expert;
    }

    public void setPrenom_expert(String prenom_expert) {
        this.prenom_expert = prenom_expert;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getAnneeExperience() {
        return anneeExperience;
    }

    public void setAnneeExperience(int anneeExperience) {
        this.anneeExperience = anneeExperience;
    }

    @Override
    public String toString() {
        return "Expert{" +
                "id_user=" + id_user +
                ", id_expert=" + id_expert +
                ", nom_expert='" + nom_expert + '\'' +
                ", prenom_expert='" + prenom_expert + '\'' +
                ", email='" + email + '\'' +
                ", specialite='" + specialite + '\'' +
                ", telephone='" + telephone + '\'' +
                ", anneeExperience=" + anneeExperience +
                '}';
    }
}

