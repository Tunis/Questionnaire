package vce.ihm.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import vce.authentification.Authentification;
import vce.ihm.Start;
import vce.ihm.controllers.login.InscriptionCtrl;
import vce.ihm.controllers.login.LoginCtrl;
import vce.ihm.controllers.questionnaire.QuestionnaireCtrl;
import vce.ihm.controllers.resultat.ResultatsCtrl;
import vce.ihm.controllers.salon.SalonCtrl;

import java.io.IOException;
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

	private VBox salon;
	private SalonCtrl salonCtrl;

	private BorderPane questionnaire;
	private QuestionnaireCtrl questionnaireCtrl;

	private VBox resultats;
	private ResultatsCtrl resultatsCtrl;


	// variable d'application :
	private Authentification auth;


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
}
