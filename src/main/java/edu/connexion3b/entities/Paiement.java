package edu.connexion3b.entities;

import java.time.LocalDateTime;

public class Paiement {
    private int id;
    private int idAbonnement;
    private String email;
    private float montant;
    private String devise;
    private String statut;
    private String paymentIntentId;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    
    public Paiement() {
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }
    
    public Paiement(int idAbonnement, String email, float montant, String devise, String statut, String paymentIntentId) {
        this.idAbonnement = idAbonnement;
        this.email = email;
        this.montant = montant;
        this.devise = devise;
        this.statut = statut;
        this.paymentIntentId = paymentIntentId;
        this.dateCreation = LocalDateTime.now();
        this.dateModification = LocalDateTime.now();
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getIdAbonnement() {
        return idAbonnement;
    }
    
    public void setIdAbonnement(int idAbonnement) {
        this.idAbonnement = idAbonnement;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public float getMontant() {
        return montant;
    }
    
    public void setMontant(float montant) {
        this.montant = montant;
    }
    
    public String getDevise() {
        return devise;
    }
    
    public void setDevise(String devise) {
        this.devise = devise;
    }
    
    public String getStatut() {
        return statut;
    }
    
    public void setStatut(String statut) {
        this.statut = statut;
        this.dateModification = LocalDateTime.now();
    }
    
    public String getPaymentIntentId() {
        return paymentIntentId;
    }
    
    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDateTime getDateModification() {
        return dateModification;
    }
    
    public void setDateModification(LocalDateTime dateModification) {
        this.dateModification = dateModification;
    }
    
    @Override
    public String toString() {
        return "Paiement{" +
                "id=" + id +
                ", idAbonnement=" + idAbonnement +
                ", email='" + email + '\'' +
                ", montant=" + montant +
                ", devise='" + devise + '\'' +
                ", statut='" + statut + '\'' +
                ", paymentIntentId='" + paymentIntentId + '\'' +
                ", dateCreation=" + dateCreation +
                ", dateModification=" + dateModification +
                '}';
    }
}