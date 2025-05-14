package Entities;

public class Salle {
    private int id;
    private String nom;
    private int nbr_max;
    public Salle() {}
    public Salle(int id, String nom, int nbr_max) {
        this.id = id;
        this.nom = nom;
        this.nbr_max = nbr_max;
    }
    public Salle(String nom, int nbr_max) {
        this.nom = nom;
        this.nbr_max = nbr_max;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNom() {
        return nom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public int getNbrMax() {
        return nbr_max;
    }
    public void setNbrMax(int nbr_max) {
        this.nbr_max = nbr_max;
    }
    @Override
    public String toString() {
        return  " Id : "+id+
                " Nom : " +nom+
                " Nbr max : "+nbr_max;
    }
}
