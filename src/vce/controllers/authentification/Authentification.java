package vce.controllers.authentification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import vce.models.data.Question;
import vce.models.data.Questionnaire;
import vce.models.data.Reponse;
import vce.models.data.SessionUser;
import vce.models.data.User;

public class Authentification {

	private String url;

	private Connection instance;

	private void getConfig() {
		try {
			// dossier actuel
			System.getProperty("user.dir");
			File bdd_config = new File(System.getProperty("user.dir") + "/bdd_config.txt");
			if (bdd_config.exists()) {
				// fichier existe on lis la config
				Map<String, String> bdd_info = new HashMap<>();
				String line;
				BufferedReader read = new BufferedReader(new FileReader(bdd_config));
				while ((line = read.readLine()) != null) {
					String[] info = line.split("=");
					if (info.length == 2) {
						bdd_info.put(info[0], info[1]);
					}
				}
				url = "jdbc:" + bdd_info.get("bdd.type") + "://" + bdd_info.get("bdd.address") + ":" + bdd_info.get("bdd.port") + "/" + bdd_info.get("bdd.name") + "?user=" + bdd_info.get("bdd.login") + "&password=" + ((bdd_info.get("bdd.password") != null) ? bdd_info.get("bdd.password") : "") + "";
			} else {
				// fichier absent on le crée
				PrintWriter out;
				out = new PrintWriter(new FileWriter(bdd_config));
				out.write("bdd.type=mysql");
				out.println();
				out.write("bdd.address=127.0.0.1");
				out.println();
				out.write("bdd.port=3306");
				out.println();
				out.write("bdd.name=vce");
				out.println();
				out.write("bdd.login=root");
				out.println();
				out.write("bdd.password=root");
				out.close();
				url = "jdbc:mysql://127.0.0.1:3306/vce?user=root&password=root";
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setConfig(String type, String ip, String port, String dbName, String user, String pass) {
		try {
			// dossier actuel
			System.getProperty("user.dir");
			File bdd_config = new File(System.getProperty("user.dir") + "/bdd_config.txt");
			PrintWriter out;
			out = new PrintWriter(new FileWriter(bdd_config));
			out.write("bdd.type=" + type);
			out.println();
			out.write("bdd.address=" + ip);
			out.println();
			out.write("bdd.port=" + port);
			out.println();
			out.write("bdd.name=" + dbName);
			out.println();
			out.write("bdd.login=" + user);
			out.println();
			out.write("bdd.password=" + pass);
			out.close();
			url = "jdbc:" + type + "://" + ip + ":" + port + "/" + dbName + "?user=" + user + "&password=" + pass;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// methode de connexion a la bdd
	public Connection getInstance() throws SQLException {

		if (instance == null) {
			getConfig();
			// les derniere version du mysql Connector on plus besoins du forClass :)
			instance = DriverManager.getConnection(url);
			// si erreur de connexion a la bdd va throw une exception.
		}
		return instance;
	}

	public Connection getInstance(String type, String ip, String port, String dbName, String user, String pass) throws SQLException {

		if (instance == null) {
			setConfig(type, ip, port, dbName, user, pass);
			getConfig();
			// les derniere version du mysql Connector on plus besoins du forClass :)
			instance = DriverManager.getConnection(url);
			// si erreur de connexion a la bdd va throw une exception.
		}
		return instance;
	}

	/*
		methode pour le login, retourne null si echoue ou une exception :
	 */
	public User login(String pseudo, String mdp) throws SQLException {
		User user = null;
		PreparedStatement sttm;

		// on recupere les infos du membre :
		sttm = getInstance().prepareStatement("SELECT idUser, nameUser, firstnameUser, pseudoUser FROM Users WHERE pseudoUser = ? AND passwordUser = sha1(?)");
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
		sttm = getInstance().prepareStatement("INSERT INTO Users(nameUser, firstnameUser, pseudoUser, passwordUser) VALUES (?, ?, ?, sha1(?))", new String[]{"idUser"});
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

	public List<User> getListUser() throws SQLException {
		// methode pour afficher la liste des user connu sur la page login
		List<User> userList = new ArrayList<>();
		PreparedStatement sttm;
		User user;
		sttm = getInstance().prepareStatement("SELECT idUser, nameUser, firstnameUser, pseudoUser FROM Users");
		ResultSet result = sttm.executeQuery();
		while (result.next()) {
			// create user
			user = new User(result.getInt(1), result.getString(2), result.getString(3), result.getString(4));
			userList.add(user);
		}
		return userList;
	}
	
	//Que les 5 meilleurs
	public List<SessionUser> getResultat(){
		return null;
	}
	
	//Pour ajouter le réslutat à la base
	public void updateResultatToDB(int score, Duration timeScore, User user, Questionnaire questionnaire) throws SQLException{
		PreparedStatement sttm;
		
		sttm = getInstance().prepareStatement("INSERT INTO score(idUserScore, timeScore, scoreScore, idquestionnaireScore) VALUES (?, ?, ?, ?)");
		sttm.setInt(1, user.getId());
		sttm.setDouble(2, timeScore.toMillis());
		sttm.setInt(3, score);
		sttm.setInt(4, questionnaire.getIdQuestionnaire());
		
		sttm.executeUpdate();
	}

	public List<Questionnaire> getListQuestionnaire() throws SQLException {
		// methode pour lister les questionnaire de la bdd
		List<Questionnaire> questionnaireList = new ArrayList<>();
		PreparedStatement sttm;
		Questionnaire questionnaire;
		sttm = getInstance().prepareStatement("SELECT idQuestionnaire, name FROM questionnaire");
		ResultSet result = sttm.executeQuery();

		while (result.next()) {
			// recuperation des question du questionnaire actuel :
			// TODO: 11/03/2017 get question of questionnaire and reponse of question
			PreparedStatement sttmQ = getInstance().prepareStatement("SELECT idQuestion, textQuestion FROM liaison\n" +
					"LEFT JOIN question ON liaison.idquestionLiaison = question.idQuestion\n" +
					"WHERE idquestionnaireLiaison = ?");
			sttmQ.setInt(1, result.getInt(1));
			ResultSet question = sttmQ.executeQuery();
			List<Question> questions = new ArrayList<>();

			while (question.next()) {
				// recuperation des reponse de la question actuelle :
				PreparedStatement sttmR = getInstance().prepareStatement("SELECT idReponse, textReponse, verifReponse FROM reponse" +
						" WHERE idquestionReponse = ?");
				sttmR.setInt(1, question.getInt(1));
				ResultSet reponse = sttmR.executeQuery();
				List<Reponse> reponses = new ArrayList<>();

				while (reponse.next()) {
					// enregistrement des reponses :
					reponses.add(new Reponse(reponse.getInt(1), reponse.getString(2), reponse.getBoolean(3)));
				}
				// enregistrement des questions :
				questions.add(new Question(question.getInt(1), question.getString(2), reponses));
			}
			// enregistrement du questionnaire :
			questionnaire = new Questionnaire(result.getInt(1), result.getString(2), questions);
			questionnaireList.add(questionnaire);
		}
		// retourne la liste des questionnaire connu :
		return questionnaireList;
	}
}
