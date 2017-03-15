package vce.vues.controllers.login;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import vce.models.data.User;
import vce.vues.controllers.RootCtrl;

import java.io.File;
import java.sql.SQLException;

public class LoginCtrl {
	public ComboBox<User> champPseudo;
	public PasswordField champMdp;
	public Button btnInscription;
	public Button btnLogin;


	private User user;

	private RootCtrl rootCtrl;

	public void tryLogin(ActionEvent actionEvent) {
		if (champPseudo.getSelectionModel().getSelectedItem() != null && !champMdp.getText().isEmpty()) {
			try {
				user = rootCtrl.getAuth().login(champPseudo.getSelectionModel().getSelectedItem().getPseudo(), champMdp.getText());
			} catch (SQLException ignored) {
			} finally {
				if (user != null) {
					rootCtrl.setUser(user);
					champMdp.setText("");
					champPseudo.getSelectionModel().clearSelection();
					File tempFileJson = new File(user.getPseudo() + ".json");
					File tempFileXML = new File(user.getPseudo() + ".xml");
					if (tempFileJson.exists() || tempFileXML.exists()) {
						rootCtrl.goToQuestionnaire();
					} else {
						rootCtrl.goToJoinSalon();
					}
				} else {
					rootCtrl.error("Login échoué", "erreur de login");
				}
			}
		} else {
			rootCtrl.error("Login échoué", "Merci de remplir tous les champs.");
		}
	}

	public void goToInscription(ActionEvent actionEvent) {
		rootCtrl.goToInscription();
	}

	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
		try {
			champPseudo.setItems(FXCollections.observableArrayList(rootCtrl.getAuth().getListUser()));
		} catch (SQLException ignored) {
			rootCtrl.goToSqlConfig();
		}
	}
}
