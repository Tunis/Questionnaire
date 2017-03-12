package vce.vues.controllers.login;

import javafx.scene.control.TextField;
import vce.vues.controllers.RootCtrl;

import java.sql.SQLException;

public class SqlConfigCtrl {
	public TextField champType;
	public TextField champIP;
	public TextField champPort;
	public TextField champDBName;
	public TextField champUser;
	public TextField champPass;
	private RootCtrl rootCtrl;

	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
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
