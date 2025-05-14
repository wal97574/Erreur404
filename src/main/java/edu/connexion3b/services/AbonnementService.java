package edu.connexion3b.services;

import edu.connexion3b.entities.Abonnement;
import edu.connexion3b.tools.Myconnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AbonnementService {
    private Connection connection;
    
    public AbonnementService() {
        connection = Myconnection.getInstance().getCnx();
    }
    
    public void ajouter(Abonnement abonnement) {
        String sql = "INSERT INTO abonnements (nom, type, duree_jours, prix, est_etudiant) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, abonnement.getNom());
            pst.setString(2, abonnement.getType());
            pst.setString(3, abonnement.getDuree_jours());
            pst.setFloat(4, abonnement.getPrix());
            pst.setBoolean(5, abonnement.isEstEtudiant());
            pst.executeUpdate();
            
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                abonnement.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout: " + e.getMessage());
        }
    }
    
    public List<Abonnement> afficherTous() {
        List<Abonnement> abonnements = new ArrayList<>();
        String sql = "SELECT * FROM abonnements";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Abonnement a = new Abonnement();
                a.setId(rs.getInt("id"));
                a.setNom(rs.getString("nom"));
                a.setType(rs.getString("type"));
                a.setDuree_jours(rs.getString("duree_jours"));
                a.setPrix(rs.getFloat("prix"));
                a.setEstEtudiant(rs.getBoolean("est_etudiant"));
                abonnements.add(a);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'affichage: " + e.getMessage());
        }
        return abonnements;
    }
    
    public void modifier(Abonnement abonnement) {
        String sql = "UPDATE abonnements SET nom=?, type=?, duree_jours=?, prix=?, est_etudiant=? WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, abonnement.getNom());
            pst.setString(2, abonnement.getType());
            pst.setString(3, abonnement.getDuree_jours());
            pst.setFloat(4, abonnement.getPrix());
            pst.setBoolean(5, abonnement.isEstEtudiant());
            pst.setInt(6, abonnement.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la modification: " + e.getMessage());
        }
    }
    
    public void supprimer(int id) {
        String sql = "DELETE FROM abonnements WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la suppression: " + e.getMessage());
        }
    }
    
    public Abonnement getById(int id) {
        String sql = "SELECT * FROM abonnements WHERE id=?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Abonnement a = new Abonnement();
                a.setId(rs.getInt("id"));
                a.setNom(rs.getString("nom"));
                a.setType(rs.getString("type"));
                a.setDuree_jours(rs.getString("duree_jours"));
                a.setPrix(rs.getFloat("prix"));
                return a;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche: " + e.getMessage());
        }
        return null;
    }
}