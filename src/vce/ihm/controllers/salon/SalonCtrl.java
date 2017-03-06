package vce.ihm.controllers.salon;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import vce.ihm.controllers.RootCtrl;

public class SalonCtrl {
	public Label ipSalon;
	public Label portSalon;
	public ListView listSalon;
	private RootCtrl rootCtrl;

	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
		System.out.println("valeur de root : " + rootCtrl.getSalon());
		if (rootCtrl.getSalon() == null) {
			ipSalon.setText(rootCtrl.getSession().getSocket().getInetAddress().getHostName());
			portSalon.setText(String.valueOf(rootCtrl.getSession().getSocket().getPort()));
		} else {
			ipSalon.setText(rootCtrl.getSalon().getHost());
			//portSalon.setText(String.valueOf(rootCtrl.getSalon().getPort()));
		}
	}
}
