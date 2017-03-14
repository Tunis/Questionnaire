package vce.vues.controllers.login;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import vce.models.data.User;
import vce.vues.controllers.RootCtrl;

import java.sql.SQLException;

public class InscriptionCtrl {
	public TextField champNom;
	public TextField champPrenom;
	public TextField champPseudo;
	public TextField champMdp;
	public Button btnInscription;
	public Button btnBack;


	private User user;


	private RootCtrl rootCtrl;

	public void tryInscription(ActionEvent actionEvent) {
		if (!champMdp.getText().isEmpty() && !champPseudo.getText().isEmpty() && !champNom.getText().isEmpty() && !champPrenom.getText().isEmpty()) {
			if (champMdp.getText().length() >= 4 && champPseudo.getText().length() >= 4) {
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
						rootCtrl.error("Inscription échoué", "Utilisateur deja connu");
					}
				}
			} else {
				rootCtrl.error("Inscription échoué", "Merci de remplir tout les champs.");
			}
		} else {
			rootCtrl.error("Inscription échoué", "Merci de remplir tout les champs.");
		}
	}

	public void goToLogin(ActionEvent actionEvent) {
		rootCtrl.goToLogin();
	}

	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;

		Platform.runLater(() -> btnBack.getParent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_ANY), () -> {
			goToLogin(null);
		}));

		Platform.runLater(() -> btnInscription.getParent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_ANY), () -> {
			tryInscription(null);
		}));
	}
}
