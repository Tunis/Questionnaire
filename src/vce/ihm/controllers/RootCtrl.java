package vce.ihm.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import vce.ihm.controllers.login.InscriptionCtrl;
import vce.ihm.controllers.login.LoginCtrl;
import vce.ihm.controllers.questionnaire.QuestionnaireCtrl;
import vce.ihm.controllers.resultat.ResultatsCtrl;
import vce.ihm.controllers.salon.SalonCtrl;

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


	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
}
