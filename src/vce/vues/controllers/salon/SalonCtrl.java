package vce.vues.controllers.salon;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import vce.models.data.SessionUser;
import vce.models.salon.Salon;
import vce.vues.controllers.RootCtrl;

public class SalonCtrl {
	public Label ipSalon;
	public Label portSalon;
    public ListView<SessionUser> listSalon;
    public Button btnLaunch;
	private RootCtrl rootCtrl;

	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
		listSalon.setCellFactory(new Callback<ListView<SessionUser>, ListCell<SessionUser>>() {

			@Override
			public ListCell<SessionUser> call(ListView<SessionUser> p) {

				return new ListCell<SessionUser>() {

					@Override
					protected void updateItem(SessionUser t, boolean bln) {
						super.updateItem(t, bln);
						if (t != null) {
							setText(t.getPseudo());
						}
					}
				};
			}
		});

		if (rootCtrl.getSalon() instanceof Salon) {
			btnLaunch.setVisible(true);
			Salon salon = (Salon) rootCtrl.getSalon();
            ipSalon.setText(salon.getHost());
            portSalon.setText(String.valueOf(salon.getPort()));
            listSalon.setItems(salon.getSessionList());
        } else {
			ipSalon.setText(rootCtrl.getSalon().getSocket().getInetAddress().getHostAddress());
			portSalon.setText(String.valueOf(rootCtrl.getSalon().getSocket().getPort()));
			listSalon.setItems(rootCtrl.getSalon().getSessionList());
			btnLaunch.setVisible(false);
		}
	}

	public void showList(ActionEvent actionEvent) {
		System.out.println("liste actuel : ");
		rootCtrl.getSalon().getSessionList().forEach(s -> System.out.println(s.getPseudo()));
	}


	public void launch(ActionEvent event) {
		Salon salon = (Salon) rootCtrl.getSalon();
		salon.startQuestionnaire();
	}
}
