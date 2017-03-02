package vce.authentification;

import vce.bdd.Bdd;
import vce.data.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Authentification {

    private Connection connectionBdd;

    public Authentification(Bdd connectionBdd) {
        this.connectionBdd = connectionBdd.getInstance();
    }

    public User login(String pseudo, String mdp) throws SQLException {
        User user = null;
        PreparedStatement sttm;

        // check if login is good :
        sttm = connectionBdd.prepareStatement("SELECT pseudo, mdp FROM Users WHERE pseudo = ? AND mdp = ?");
        sttm.setString(1, pseudo);
        sttm.setString(2, mdp);
        ResultSet resultSet = sttm.executeQuery();
        if (resultSet.next()) {
            // if login ok create user :
            sttm = connectionBdd.prepareStatement("SELECT nom, prenom, pseudo FROM Users WHERE pseudo = ?");
            sttm.setString(1, pseudo);
            ResultSet result = sttm.executeQuery();
            if (result.next()) {
                // create user
                user = new User(result.getString(1), result.getString(2), result.getString(3));
            }
        }

        // return user
        return user;
    }

    public User inscription(String nom, String prenom, String pseudo, String mdp) throws SQLException {
        User user = null;
        PreparedStatement sttm;

        // check if pseudo already exist (pseudo is unique id) :
        sttm = connectionBdd.prepareStatement("SELECT pseudo FROM Users WHERE pseudo = ?");
        sttm.setString(1, pseudo);
        ResultSet resultSet = sttm.executeQuery();
        if (resultSet.isBeforeFirst()) {
            // if pseudo is unknow insert user in connectionBdd :
            sttm = connectionBdd.prepareStatement("INSERT INTO Users(nom, prenom, pseudo, mdp) VALUES ?, ?, ?, ?");
            sttm.setString(1, nom);
            sttm.setString(1, prenom);
            sttm.setString(1, pseudo);
            sttm.setString(1, mdp);

            if (sttm.executeUpdate() > 0) {
                // create user
                user = new User(nom, prenom, pseudo);
            }
        }

        // return user
        return user;
    }

}
