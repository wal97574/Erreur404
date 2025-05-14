package edu.connexion3b.entities;

public class Abonnement {
    private int id;
    private String nom;
    private String type;
    private String duree_jours;
    private float prix;
    private boolean estEtudiant;
    
    public Abonnement() {
    }
    
    public Abonnement(String nom, String type, String duree_jours, float prix) {
        this.nom = nom;
        this.type = type;
        this.duree_jours = duree_jours;
        this.prix = prix;
    }
    
    public Abonnement(String nom, String type, String duree_jours, float prix, boolean estEtudiant) {
        this.nom = nom;
        this.type = type;
        this.duree_jours = duree_jours;
        this.prix = prix;
        this.estEtudiant = estEtudiant;
    }
    
    public Abonnement(int id, String nom, String type, String duree_jours, float prix, boolean estEtudiant) {
        this.id = id;
        this.nom = nom;
        this.type = type;
        this.duree_jours = duree_jours;
        this.prix = prix;
        this.estEtudiant = estEtudiant;
    }
    
    // Getters and Setters
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getDuree_jours() {
        return duree_jours;
    }
    
    public void setDuree_jours(String duree_jours) {
        this.duree_jours = duree_jours;
    }
    
    public float getPrix() {
        return prix;
    }
    
    public void setPrix(float prix) {
        this.prix = prix;
    }
    
    public boolean isEstEtudiant() {
        return estEtudiant;
    }
    
    public void setEstEtudiant(boolean estEtudiant) {
        this.estEtudiant = estEtudiant;
    }
    
    @Override
    public String toString() {
        return "Abonnement{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", type='" + type + '\'' +
                ", duree_jours='" + duree_jours + '\'' +
                ", prix=" + prix +
                ", estEtudiant=" + estEtudiant +
                '}';
    }
}