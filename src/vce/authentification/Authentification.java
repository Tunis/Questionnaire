package vce.authentification;

import vce.bdd.Bdd;
import vce.data.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Authentification {

    private Connection connectionBdd;

	/*
		Constructeur, recupere la connection a la bdd
	 */
	public Authentification() {
		this.connectionBdd = Bdd.getInstance();
	}

	/*
		methode pour le login, retourne null si echoue ou une exception :
	 */
	public User login(String pseudo, String mdp) throws SQLException {
		User user = null;
		PreparedStatement sttm;

		// on recupere les infos du membre :
		sttm = connectionBdd.prepareStatement("SELECT idUser, nameUser, firstnameUser, pseudoUser FROM Users WHERE pseudoUser = ? AND passwordUser = sha1(?)");
		sttm.setString(1, pseudo);
		sttm.setString(2, mdp);
		ResultSet result = sttm.executeQuery();
		// si login et mdp bon :
		if (result.next()) {
			// create user
			user = new User(result.getInt(1), result.getString(2), result.getString(3), result.getString(4));
		}

		// return user
		return user;
	}

	/*
		gere l'inscription a la bdd, retourne null ou exception si erreur :
	 */
	public User inscription(String nom, String prenom, String pseudo, String mdp) throws SQLException {
		User user = null;
		PreparedStatement sttm;
		// on essaie d'inserer le nouveau membre :
		sttm = connectionBdd.prepareStatement("INSERT INTO Users(nameUser, firstnameUser, pseudoUser, passwordUser) VALUES (?, ?, ?, sha1(?))", new String[]{"idUser"});
		sttm.setString(1, nom);
		sttm.setString(2, prenom);
		sttm.setString(3, pseudo);
		sttm.setString(4, mdp);
		sttm.executeUpdate();
		ResultSet insert = sttm.getGeneratedKeys();

		// si l'insertion est ok :
		if (insert.next()) {
			// create user
			user = new User(insert.getInt(1), nom, prenom, pseudo);
		}

		// return user
		return user;

		//truc a lacon

	}
}
