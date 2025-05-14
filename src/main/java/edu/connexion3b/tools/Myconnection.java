package edu.connexion3b.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Myconnection {

    private static Connection cnx; // 👉 type corrigé ici
    private final String url = "jdbc:mysql://localhost:3306/gym_management";
    private final String userName = "root";
    private final String pwd = "";
    private static Myconnection instance;

    private Myconnection() {
        try {
            cnx = DriverManager.getConnection(url, userName, pwd);
            System.out.println("Connexion établie...");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    }

    public static Connection getCnx() {
        return cnx;
    }

    public static Myconnection getInstance() {
        if (instance == null) {
            instance = new Myconnection();
        }
        return instance;
    }
}
