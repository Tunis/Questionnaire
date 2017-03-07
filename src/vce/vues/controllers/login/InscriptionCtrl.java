package vce.vues.controllers.login;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import vce.methodes.data.User;
import vce.vues.controllers.RootCtrl;

import java.sql.SQLException;

public class InscriptionCtrl {
	public TextField champNom;
	public TextField champPrenom;
	public TextField champPseudo;
	public TextField champMdp;


	private User user;


	private RootCtrl rootCtrl;

	public void tryInscription(ActionEvent actionEvent) {
		System.out.println("tentative d'inscription :");
		try {
			user = rootCtrl.getAuth().inscription(champNom.getText(),
					champPrenom.getText(),
					champPseudo.getText(),
					champMdp.getText());
		} catch (SQLException ignored) {
		} finally {
			if (user != null) {
				rootCtrl.setUser(user);
				rootCtrl.goToJoinSalon();
			} else {
				System.out.println("inscription echoué");
			}
		}

	}

	public void goToLogin(ActionEvent actionEvent) {
		rootCtrl.goToLogin();
	}

	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
	}
}
