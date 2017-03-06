package vce.ihm.controllers.login;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import vce.data.User;
import vce.ihm.controllers.RootCtrl;

import java.sql.SQLException;

public class LoginCtrl {
	public TextField champPseudo;
	public TextField champMdp;


	private User user;

	private RootCtrl rootCtrl;

	public void tryLogin(ActionEvent actionEvent) {
		System.out.println("on tente le login :");
		try {
			user = rootCtrl.getAuth().login(champPseudo.getText(), champMdp.getText());
		} catch (SQLException ignored) {
		} finally {
			if (user != null) {
				System.out.println("login réussit : " + user.getPseudo());
			} else {
				System.out.println("login echoué");
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
