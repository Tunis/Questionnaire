package vce.vues.controllers.salon;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import vce.models.session.Session;
import vce.vues.controllers.RootCtrl;

import java.net.Socket;

public class JoinSalonCtrl {


	public TextField champIp;
	public TextField champPort;
	public Button btnJoinSalon;
	public Button btnDisconnect;
	private RootCtrl rootCtrl;

	public void createSalon(ActionEvent actionEvent) {
		// TODO: 09/03/2017 a verifier
		rootCtrl.goToCreateSalon();
	}

	public void joinSalon(ActionEvent actionEvent) {
		Socket socket = Session.connectServer(champIp.getText(), Integer.parseInt(champPort.getText()));
		if (socket != null) {
			rootCtrl.createSession(socket);
		} else {
			rootCtrl.error("Connexion serveur", "Serveur inconnu, verifié l'ip ou le port");
		}
	}

	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
		Platform.runLater(() -> btnDisconnect.getParent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_ANY), () -> {
			disconnect(null);
		}));

		Platform.runLater(() -> btnJoinSalon.getParent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.J, KeyCombination.SHORTCUT_ANY), () -> {
			joinSalon(null);
		}));
	}

	public void disconnect(ActionEvent actionEvent) {
		rootCtrl.goToLogin();
	}
}
