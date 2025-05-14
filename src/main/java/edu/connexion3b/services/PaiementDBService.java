package edu.connexion3b.services;

import edu.connexion3b.entities.Paiement;
import edu.connexion3b.tools.Myconnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PaiementDBService {
    private Connection connection;
    
    public PaiementDBService() {
        connection = Myconnection.getInstance().getCnx();
    }
    
    public void ajouter(Paiement paiement) {
        String sql = "INSERT INTO paiement (id_abonnement, email, montant, devise, statut, payment_intent_id, date_creation, date_modification) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, paiement.getIdAbonnement());
            pst.setString(2, paiement.getEmail());
            pst.setFloat(3, paiement.getMontant());
            pst.setString(4, paiement.getDevise());
            pst.setString(5, paiement.getStatut());
            pst.setString(6, paiement.getPaymentIntentId());
            pst.setTimestamp(7, Timestamp.valueOf(paiement.getDateCreation()));
            pst.setTimestamp(8, Timestamp.valueOf(paiement.getDateModification()));
            pst.executeUpdate();
            
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                paiement.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de l'ajout du paiement: " + e.getMessage());
        }
    }
    
    public void mettreAJourStatut(String paymentIntentId, String nouveauStatut) {
        String sql = "UPDATE paiement SET statut = ?, date_modification = ? WHERE payment_intent_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, nouveauStatut);
            pst.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pst.setString(3, paymentIntentId);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du statut: " + e.getMessage());
        }
    }
    
    public List<Paiement> getPaiementsParAbonnement(int idAbonnement) {
        List<Paiement> paiements = new ArrayList<>();
        String sql = "SELECT * FROM paiement WHERE id_abonnement = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, idAbonnement);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Paiement p = new Paiement();
                p.setId(rs.getInt("id"));
                p.setIdAbonnement(rs.getInt("id_abonnement"));
                p.setEmail(rs.getString("email"));
                p.setMontant(rs.getFloat("montant"));
                p.setDevise(rs.getString("devise"));
                p.setStatut(rs.getString("statut"));
                p.setPaymentIntentId(rs.getString("payment_intent_id"));
                p.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                p.setDateModification(rs.getTimestamp("date_modification").toLocalDateTime());
                paiements.add(p);
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération des paiements: " + e.getMessage());
        }
        return paiements;
    }
    
    public Paiement getParPaymentIntentId(String paymentIntentId) {
        String sql = "SELECT * FROM paiement WHERE payment_intent_id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, paymentIntentId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Paiement p = new Paiement();
                p.setId(rs.getInt("id"));
                p.setIdAbonnement(rs.getInt("id_abonnement"));
                p.setEmail(rs.getString("email"));
                p.setMontant(rs.getFloat("montant"));
                p.setDevise(rs.getString("devise"));
                p.setStatut(rs.getString("statut"));
                p.setPaymentIntentId(rs.getString("payment_intent_id"));
                p.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
                p.setDateModification(rs.getTimestamp("date_modification").toLocalDateTime());
                return p;
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la récupération du paiement: " + e.getMessage());
        }
        return null;
    }
}