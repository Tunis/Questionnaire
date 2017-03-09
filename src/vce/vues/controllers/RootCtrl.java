package vce.vues.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import vce.controllers.authentification.Authentification;
import vce.models.data.User;
import vce.models.salon.Salon;
import vce.models.session.Session;
import vce.vues.Start;
import vce.vues.controllers.login.InscriptionCtrl;
import vce.vues.controllers.login.LoginCtrl;
import vce.vues.controllers.questionnaire.QuestionnaireCtrl;
import vce.vues.controllers.resultat.ResultatsCtrl;
import vce.vues.controllers.salon.JoinSalonCtrl;
import vce.vues.controllers.salon.SalonCtrl;

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
		user = null;
		salon = null;
		root.setCenter(login);
	}

	public void goToJoinSalon() {
		salon = null;
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

			Platform.runLater(() -> root.setCenter(questionnaire));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//

	public void goToResultats() {
		try {
			FXMLLoader load = new FXMLLoader(Start.class.getResource("/ihm/resultat/resultat.fxml"));
			resultats = load.load();
			resultatsCtrl = load.getController();
			resultatsCtrl.init(this);
			Platform.runLater(() -> root.setCenter(resultats));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public Session getSalon() {
		return salon;
	}

	public User getUser() {
		return user;
	}

	public void createSalon(int duree) {
		salon = new Salon(user, duree, this);
		goToSalon();
	}

	public void createSession(Socket socket) {
		salon = new Session(user, socket, this);
		goToSalon();
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void refreshList() {
		if (root.getCenter().equals(questionnaire)) {
			questionnaireCtrl.update();
		} else if (root.getCenter().equals(resultats)) {
			resultatsCtrl.update();
		} else if (root.getCenter().equals(salonView)) {
			salonCtrl.update();
		}
	}

	public void error(String errorType, String message) {
		Alert error = new Alert(Alert.AlertType.ERROR);
		error.setHeaderText(errorType);
		error.setContentText(message);
		error.initModality(Modality.APPLICATION_MODAL);
		error.show();
		if (errorType.equals("Erreur de Flux") || errorType.equals("Erreur de socket")) {
			goToJoinSalon();
		}
	}
}
