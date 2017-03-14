package vce.vues.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import vce.controllers.authentification.Authentification;
import vce.models.data.Questionnaire;
import vce.models.data.User;
import vce.models.salon.Salon;
import vce.models.session.RepondreQuestionnaire;
import vce.models.session.Session;
import vce.vues.Start;
import vce.vues.controllers.login.InscriptionCtrl;
import vce.vues.controllers.login.LoginCtrl;
import vce.vues.controllers.login.SqlConfigCtrl;
import vce.vues.controllers.questionnaire.QuestionnaireCtrl;
import vce.vues.controllers.resultat.ResultatsCtrl;
import vce.vues.controllers.salon.CreateSalonCtrl;
import vce.vues.controllers.salon.JoinSalonCtrl;
import vce.vues.controllers.salon.SalonCtrl;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.Socket;
import java.net.URL;
import java.sql.SQLException;
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

	private VBox sqlConfig;
	private SqlConfigCtrl sqlConfigCtrl;

	private VBox createSalonView;
	private CreateSalonCtrl createSalonCtrl;


	// variable d'application :
	private Authentification auth;
	private Session salon;
	private User user;

	// GETTERS :
	public Authentification getAuth() {
		return auth;
	}

	public Session getSalon() {
		return salon;
	}

	public User getUser() {
		return user;
	}

	// SETTERS :
	public void createSalon(Questionnaire questionnaire, int duree, boolean selected) {
		salon = new Salon(user, questionnaire, duree, selected, this);
		goToSalon();
	}

	public void createSession(Socket socket) {
		salon = new Session(user, socket, this);
		goToSalon();
	}

	public void setUser(User user) {
		this.user = user;
	}

	// premier chargement :
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		auth = new Authentification();
		tryConnectBDD();
	}

	// methodes globale :
	public void tryConnectBDD() {
		try {
			auth.getInstance();
			goToLogin();
		} catch (SQLException e) {
			goToSqlConfig();
		}
	}

	public void disconnect() {
		if (salon != null) {
			if (salon instanceof Salon) {
				Salon salonTemp = (Salon) salon;
				if (salonTemp.getServerSocket() != null) {
					salonTemp.closeAllInOut();
					salonTemp.closeServerCo();
				}
			} else {
				if (salon.getSocket() != null)
					salon.closeInOut();
			}
			salon = null;
		}
	}

	// methode changement de view :

	public void goToSqlConfig() {
		try {
			FXMLLoader loadLogin = new FXMLLoader(Start.class.getResource("/ihm/login/sqlConfig.fxml"));
			sqlConfig = loadLogin.load();
			sqlConfigCtrl = loadLogin.getController();
			sqlConfigCtrl.init(this);

			root.setCenter(sqlConfig);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
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
		disconnect();
		user = null;
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

	public void goToJoinSalon() {
		disconnect();
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

	public void goToCreateSalon() {
		try {
			FXMLLoader load = new FXMLLoader(Start.class.getResource("/ihm/salon/createSalon.fxml"));
			createSalonView = load.load();
			createSalonCtrl = load.getController();
			createSalonCtrl.init(this);

			root.setCenter(createSalonView);
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
		File tempFileJson = new File(user.getPseudo() + ".json");
		File tempFileXML = new File(user.getPseudo() + ".xml");
		if (tempFileJson.exists() || tempFileXML.exists()) {
			salon = new Salon(user, null, 1, false, this);
			Salon salonTemp = (Salon) salon;
			RepondreQuestionnaire jsonAvancement = loadJsonFile(tempFileJson);

			//RepondreQuestionnaire jsonAvancement = loadXMLFile(tempFileXML);
			salonTemp.setQuestionnaire(jsonAvancement.getQuestionnaire());
			salonTemp.setDuration(jsonAvancement.getQuestionnaire().getDurationMax());
			jsonAvancement.setSession(salonTemp);
			jsonAvancement.setRecup(true);
			salonTemp.setAvancement(jsonAvancement);

		}
		try {
			if (salon instanceof Salon) {
				Salon salonTemp = (Salon) salon;
				salonTemp.closeServerCo();
			}
			FXMLLoader load = new FXMLLoader(Start.class.getResource("/ihm/questionnaire/questionnaire.fxml"));
			questionnaire = load.load();
			questionnaireCtrl = load.getController();
			questionnaireCtrl.init(this);

			Platform.runLater(() -> root.setCenter(questionnaire));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

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

	// mettre a jour les listes :

	public void refreshList() {
		if (root.getCenter().equals(resultats)) {
			resultatsCtrl.update();
		} else if (root.getCenter().equals(questionnaire)) {
			questionnaireCtrl.update();
		}
	}

	// affichage des erreurs :

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


	private RepondreQuestionnaire loadJsonFile(File jsonFile) {

		RepondreQuestionnaire avancement = null;
				try (Reader reader = new FileReader(jsonFile)) {
					GsonBuilder builder = new GsonBuilder();
					builder.setPrettyPrinting();
					Gson gson = builder.create();
					avancement = gson.fromJson(reader, RepondreQuestionnaire.class);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return avancement;

			}

	private RepondreQuestionnaire loadXMLFile(File xmlFile) {
		JAXBContext context = null;
		try {
			context = JAXBContext
					.newInstance(RepondreQuestionnaire.class);

			Unmarshaller um = context.createUnmarshaller();

			// Reading XML from the file and unmarshalling.
			RepondreQuestionnaire wrapper = (RepondreQuestionnaire) um.unmarshal(xmlFile);

			System.out.println("recuperer : " + wrapper.getQuestionnaire());

			return wrapper;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}
}
