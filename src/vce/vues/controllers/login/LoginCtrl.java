package vce.vues.controllers.login;

import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import vce.models.data.User;
import vce.vues.controllers.RootCtrl;

import java.io.File;
import java.sql.SQLException;

public class LoginCtrl {
	public TextField champPseudo;
	public PasswordField champMdp;


	private User user;

	private RootCtrl rootCtrl;

	public void tryLogin(ActionEvent actionEvent) {
		try {
			user = rootCtrl.getAuth().login(champPseudo.getText(), champMdp.getText());
		} catch (SQLException ignored) {
		} finally {
			if (user != null) {
				rootCtrl.setUser(user);
				champMdp.setText("");
				champPseudo.setText("");
                File tempFileJson = new File(user.getPseudo() + ".json");
                File tempFileXML = new File(user.getPseudo() + ".xml");
                if (tempFileJson.exists() || tempFileXML.exists()) {
                    rootCtrl.goToQuestionnaire();
                }

				rootCtrl.goToJoinSalon();
			} else {
				rootCtrl.error("Login échoué", "erreur de login");
			}
		}
	}

	public void goToInscription(ActionEvent actionEvent) {
		rootCtrl.goToInscription();
	}

	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
	}
}
