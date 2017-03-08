package vce.vues.controllers.login;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import vce.models.data.User;
import vce.vues.controllers.RootCtrl;

import java.sql.SQLException;

public class LoginCtrl {
	public TextField champPseudo;
	public TextField champMdp;


	private User user;

	private RootCtrl rootCtrl;

	public void tryLogin(ActionEvent actionEvent) {
		try {
			user = rootCtrl.getAuth().login(champPseudo.getText(), champMdp.getText());
		} catch (SQLException ignored) {
		} finally {
			if (user != null) {
				rootCtrl.setUser(user);
				rootCtrl.goToJoinSalon();
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
