package vce.vues.controllers.login;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import vce.vues.controllers.RootCtrl;

import java.sql.SQLException;

public class SqlConfigCtrl {
	public TextField champType;
	public TextField champIP;
	public TextField champPort;
	public TextField champDBName;
	public TextField champUser;
	public TextField champPass;
	public Button btnConnect;
	private RootCtrl rootCtrl;

	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
		Platform.runLater(() -> btnConnect.getParent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_ANY), this::tryConnect));
	}

	public void tryConnect() {
		if (!champType.getText().isEmpty() &&
				!champIP.getText().isEmpty() &&
				!champPort.getText().isEmpty() &&
				!champDBName.getText().isEmpty() &&
				!champUser.getText().isEmpty() &&
				!champPass.getText().isEmpty())
			try {
				rootCtrl.getAuth().getInstance(champType.getText(),
						champIP.getText(),
						champPort.getText(),
						champDBName.getText(),
						champUser.getText(),
						champPass.getText());
				rootCtrl.goToLogin();
			} catch (SQLException e) {
				rootCtrl.error("Connexion impossible", "verifié les informations de connexion.");
			}
	}
}
