package vce.vues.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import vce.controllers.authentification.Authentification;
import vce.methodes.data.User;
import vce.vues.Start;
import vce.vues.controllers.login.InscriptionCtrl;
import vce.vues.controllers.login.LoginCtrl;
import vce.vues.controllers.questionnaire.QuestionnaireCtrl;
import vce.vues.controllers.resultat.ResultatsCtrl;
import vce.vues.controllers.salon.JoinSalonCtrl;
import vce.vues.controllers.salon.SalonCtrl;
import vce.methodes.salon.Salon;
import vce.methodes.session.Session;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class RootCtrl implements Initializable {

	@FXML
	private BorderPane root;


	// liste des views et controllers :
	private VBox login;
	private LoginCtrl loginCtrl;

	private VBox inscription;
	private InscriptionCtrl inscriptionCtrl;

	private VBox joinSalon;
	private JoinSalonCtrl joinSalonCtrl;

	private VBox salonView;
	private SalonCtrl salonCtrl;

	private BorderPane questionnaire;
	private QuestionnaireCtrl questionnaireCtrl;

	private VBox resultats;
	private ResultatsCtrl resultatsCtrl;


	// variable d'application :
	private Authentification auth;
	private Session salon;
	private User user;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		auth = new Authentification();
		try {
			FXMLLoader loadLogin = new FXMLLoader(Start.class.getResource("/ihm/login/login.fxml"));
			login = loadLogin.load();
			loginCtrl = loadLogin.getController();
			loginCtrl.init(this);

			root.setCenter(login);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Authentification getAuth() {
		return auth;
	}

	public void goToInscription() {
		if (inscription == null) {
			try {
				FXMLLoader loadInscription = new FXMLLoader(Start.class.getResource("/ihm/login/inscription.fxml"));
				inscription = loadInscription.load();
				inscriptionCtrl = loadInscription.getController();
				inscriptionCtrl.init(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		root.setCenter(inscription);
	}

	public void goToLogin() {
		root.setCenter(login);
	}

	public void goToJoinSalon() {
		try {
			FXMLLoader load = new FXMLLoader(Start.class.getResource("/ihm/salon/joinSalon.fxml"));
			joinSalon = load.load();
			joinSalonCtrl = load.getController();
			joinSalonCtrl.init(this);

			root.setCenter(joinSalon);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void goToSalon() {
		try {
			FXMLLoader load = new FXMLLoader(Start.class.getResource("/ihm/salon/salon.fxml"));
			salonView = load.load();
			salonCtrl = load.getController();
			salonCtrl.init(this);

			root.setCenter(salonView);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void goToQuestionnaire() {
		try {
			FXMLLoader load = new FXMLLoader(Start.class.getResource("/ihm/questionnaire/questionnaire.fxml"));
			questionnaire = load.load();
			questionnaireCtrl = load.getController();
			questionnaireCtrl.init(this);

			root.setCenter(questionnaire);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void goToResultats() {
		root.setCenter(resultats);
	}


	public Session getSalon() {
		return salon;
	}

	public User getUser() {
		return user;
	}

	public void createSalon(int duree) {
		salon = new Salon(user, duree);
		goToSalon();
	}

	public void createSession(Socket socket) {
		salon = new Session(user, socket);
		System.out.println("session créée : " + salon);
		goToSalon();
	}

	public void setUser(User user) {
		this.user = user;
	}
}
