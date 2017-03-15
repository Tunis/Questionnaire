package vce.vues.controllers.salon;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
	}

	public void disconnect(ActionEvent actionEvent) {
		rootCtrl.goToLogin();
	}
}
