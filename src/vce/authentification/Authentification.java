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

        // TODO: 02/03/2017 test sans la premiere requete qui semble inutile
        // check if login is good :
        sttm = connectionBdd.prepareStatement("SELECT pseudo, mdp FROM Users WHERE pseudo = ? AND mdp = ?");
        sttm.setString(1, pseudo);
        sttm.setString(2, mdp);
        ResultSet resultSet = sttm.executeQuery();
        if (resultSet.next()) {
            // if login ok create user :
            sttm = connectionBdd.prepareStatement("SELECT id, nom, prenom, pseudo FROM Users WHERE pseudo = ?");
            sttm.setString(1, pseudo);
            ResultSet result = sttm.executeQuery();
            if (result.next()) {
                // create user
                user = new User(result.getInt(1), result.getString(2), result.getString(3), result.getString(4));
            }
        }

        // return user
        return user;
    }

    public User inscription(String nom, String prenom, String pseudo, String mdp) throws SQLException {
        User user = null;
        PreparedStatement sttm;
        // TODO: 02/03/2017 test sans la premiere requete qui semble inutile

        // check if pseudo already exist (pseudo is unique id) :
        sttm = connectionBdd.prepareStatement("SELECT pseudo FROM Users WHERE pseudo = ?");
        sttm.setString(1, pseudo);
        ResultSet resultSet = sttm.executeQuery();
        if (resultSet.isBeforeFirst()) {
            // if pseudo is unknow insert user in connectionBdd :
            sttm = connectionBdd.prepareStatement("INSERT INTO Users(nom, prenom, pseudo, mdp) VALUES (?, ?, ?, ?)", new String[]{"idUser"});
            sttm.setString(1, nom);
            sttm.setString(2, prenom);
            sttm.setString(3, pseudo);
            sttm.setString(4, mdp);
            ResultSet insert = sttm.getGeneratedKeys();
            if (insert.next()) {
                // create user
                // TODO: 02/03/2017 verifier ici si ok
                user = new User(insert.getInt(1), nom, prenom, pseudo);
            }
        }

        // return user
        return user;
    }
}
