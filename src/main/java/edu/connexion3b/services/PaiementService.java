package edu.connexion3b.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import edu.connexion3b.entities.Abonnement;

import java.util.HashMap;
import java.util.Map;

public class PaiementService {
    
    // Clé API Stripe (à remplacer par votre clé réelle en production)
    private static final String API_KEY = "sk_test_51ROOIHFyWmW1Kne3qDAgvXbdWOQlXkXCd9OTCSaw0BOvrcFveiuShFlmLTTK3pTOt96EgkyfZSVbbZVoqdMGZAGL00wTxOYlNy";
    
    public PaiementService() {
        // Initialisation de l'API Stripe avec la clé
        Stripe.apiKey = API_KEY;
    }
    
    /**
     * Crée une intention de paiement pour un abonnement
     * @param abonnement L'abonnement à payer
     * @param email Email du client
     * @return Le client secret pour finaliser le paiement côté client
     */
    public String creerIntentionPaiement(Abonnement abonnement, String email) throws StripeException {
        // Conversion du prix en centimes (Stripe utilise les centimes)
        long montantEnCentimes = Math.round(abonnement.getPrix() * 100);
        
        // Création des paramètres pour l'intention de paiement
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(montantEnCentimes)
                .setCurrency("eur")
                .setDescription("Abonnement " + abonnement.getType() + " - " + abonnement.getNom())
                .setReceiptEmail(email)
                .putMetadata("id_abonnement", String.valueOf(abonnement.getId()))
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();
        
        // Création de l'intention de paiement
        PaymentIntent paymentIntent = PaymentIntent.create(params);
        
        // Retourne le client secret nécessaire pour finaliser le paiement côté client
        return paymentIntent.getClientSecret();
    }
    
    /**
     * Vérifie le statut d'un paiement
     * @param paymentIntentId ID de l'intention de paiement
     * @return true si le paiement est réussi, false sinon
     */
    public boolean verifierPaiement(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return "succeeded".equals(paymentIntent.getStatus());
    }
    
    /**
     * Annule une intention de paiement
     * @param paymentIntentId ID de l'intention de paiement
     * @return true si l'annulation est réussie
     */
    public boolean annulerPaiement(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        PaymentIntent canceledIntent = paymentIntent.cancel();
        return "canceled".equals(canceledIntent.getStatus());
    }
}