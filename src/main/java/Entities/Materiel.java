package Entities;

import java.util.Date;

public class Materiel {
    private int id;
    private String nom;
    private java.util.Date dateMaintenance;
    public Materiel() {}
    public Materiel(int id, String nom, java.util.Date date_maintenance) {
        this.id = id;
        this.nom = nom;
        this.dateMaintenance = date_maintenance;
    }
    public Materiel(String nom, java.util.Date date_maintenance) {
        this.nom = nom;
        this.dateMaintenance = date_maintenance;
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
    public Date getDateMaintenance() {
        return dateMaintenance;
    }
    public void setDateMaintenance(java.util.Date date_maintenance) {
        this.dateMaintenance = date_maintenance;
    }
    @Override
    public String toString(){
        return  " Id : "+id+
                " Nom : " +nom+
                " Date maintenance : "+dateMaintenance;
    }
}
