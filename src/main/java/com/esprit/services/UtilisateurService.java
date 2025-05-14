package com.esprit.services;

import com.esprit.models.Utilisateur;
import com.esprit.utils.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurService implements IService<Utilisateur> {

    Connection connection = DataSource.getInstance().getConnection();

    @Override
    public void ajouter(Utilisateur utilisateur) {
        String req = "INSERT INTO utilisateur (Nom, Prenom, Sexe, Email, Telephone, Role, password, DateInscription, Salaire) VALUES (?,?,?,?,?,?,?,?,?)";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, utilisateur.getNom());
            pst.setString(2, utilisateur.getPrenom());
            pst.setString(3, utilisateur.getSexe());
            pst.setString(4, utilisateur.getEmail());
            pst.setString(5, utilisateur.getTelephone());
            pst.setInt(6, utilisateur.getRole());
            pst.setString(7, utilisateur.getPassword());
            pst.setString(8, utilisateur.getDateInscription());
            pst.setString(9, utilisateur.getSalaire());
            pst.executeUpdate();
            System.out.println("Utilisateur ajouté");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Utilisateur utilisateur) {
        String req = "UPDATE utilisateur SET Nom=?, Prenom=?, Sexe=?, Email=?, Telephone=?, Role=?, password=?, DateInscription=?, Salaire=? WHERE ID=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setString(1, utilisateur.getNom());
            pst.setString(2, utilisateur.getPrenom());
            pst.setString(3, utilisateur.getSexe());
            pst.setString(4, utilisateur.getEmail());
            pst.setString(5, utilisateur.getTelephone());
            pst.setInt(6, utilisateur.getRole());
            pst.setString(7, utilisateur.getPassword());
            pst.setString(8, utilisateur.getDateInscription());
            pst.setString(9, utilisateur.getSalaire());
            pst.setInt(10, utilisateur.getId());
            pst.executeUpdate();
            System.out.println("Utilisateur modifié");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(Utilisateur utilisateur) {
        String req = "DELETE FROM utilisateur WHERE ID=?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, utilisateur.getId());
            pst.executeUpdate();
            System.out.println("Utilisateur supprimé");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Utilisateur> rechercher() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String req = "SELECT * FROM utilisateur";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                utilisateurs.add(new Utilisateur(
                        rs.getInt("ID"),
                        rs.getString("Nom"),
                        rs.getString("Prenom"),
                        rs.getString("Sexe"),
                        rs.getString("Email"),
                        rs.getString("Telephone"),
                        rs.getInt("Role"),
                        rs.getString("password"),
                        rs.getString("DateInscription"),
                        rs.getString("Salaire")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return utilisateurs;
    }
} 