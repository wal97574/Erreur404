package com.esprit.models;

public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String sexe;
    private String email;
    private String telephone;
    private int role;
    private String password;
    private String dateInscription;
    private String salaire;

    public Utilisateur() {
    }

    public Utilisateur(int id, String nom, String prenom, String sexe, String email, String telephone, int role, String password, String dateInscription, String salaire) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.email = email;
        this.telephone = telephone;
        this.role = role;
        this.password = password;
        this.dateInscription = dateInscription;
        this.salaire = salaire;
    }

    public Utilisateur(String nom, String prenom, String sexe, String email, String telephone, int role, String password, String dateInscription, String salaire) {
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.email = email;
        this.telephone = telephone;
        this.role = role;
        this.password = password;
        this.dateInscription = dateInscription;
        this.salaire = salaire;
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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(String dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getSalaire() {
        return salaire;
    }

    public void setSalaire(String salaire) {
        this.salaire = salaire;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", sexe='" + sexe + '\'' +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", role=" + role +
                ", password='" + password + '\'' +
                ", dateInscription='" + dateInscription + '\'' +
                ", salaire='" + salaire + '\'' +
                '}';
    }
} 