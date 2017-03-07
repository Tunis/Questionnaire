package vce.ihm.controllers.salon;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import vce.data.SessionUser;
import vce.ihm.controllers.RootCtrl;

public class SalonCtrl {
	public Label ipSalon;
	public Label portSalon;
    public ListView<SessionUser> listSalon;
    public Button btnLaunch;
	private RootCtrl rootCtrl;

	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
		if (rootCtrl.getSalon() == null) {
			ipSalon.setText(rootCtrl.getSession().getSocket().getInetAddress().getHostAddress());
			portSalon.setText(String.valueOf(rootCtrl.getSession().getSocket().getPort()));
			btnLaunch.setVisible(false);
            listSalon.setItems(rootCtrl.getSession().getSessionList());
            listSalon.setCellFactory(new Callback<ListView<SessionUser>, ListCell<SessionUser>>() {

                @Override
                public ListCell<SessionUser> call(ListView<SessionUser> p) {

                    ListCell<SessionUser> cell = new ListCell<SessionUser>() {

                        @Override
                        protected void updateItem(SessionUser t, boolean bln) {
                            super.updateItem(t, bln);
                            if (t != null) {
                                setText(t.getPseudo());
                            }
                        }

                    };

                    return cell;
                }
            });

        } else {
			ipSalon.setText(rootCtrl.getSalon().getHost());
			portSalon.setText(String.valueOf(rootCtrl.getSalon().getPort()));
			btnLaunch.setVisible(true);
            listSalon.setItems(rootCtrl.getSalon().getSessionListServer());
            listSalon.setCellFactory(new Callback<ListView<SessionUser>, ListCell<SessionUser>>() {

                @Override
                public ListCell<SessionUser> call(ListView<SessionUser> p) {

                    ListCell<SessionUser> cell = new ListCell<SessionUser>() {

                        @Override
                        protected void updateItem(SessionUser t, boolean bln) {
                            super.updateItem(t, bln);
                            if (t != null) {
                                setText(t.getPseudo());
                            }
                        }

                    };

                    return cell;
                }
            });
        }
	}

	public void showList(ActionEvent actionEvent) {
		System.out.println("-----------------------------------------------");
		if (rootCtrl.getSalon() == null) {
			rootCtrl.getSession().getSessionList().forEach(s -> System.out.println(s.getPseudo()));
		} else {
			System.out.println("-------- Session : ----------------");
			rootCtrl.getSalon().getSessionList().forEach(s -> System.out.println(s.getPseudo()));
			System.out.println("-------- Salon : ----------------");
			rootCtrl.getSalon().getSessionListServer().forEach(s -> System.out.println(s.getPseudo()));
			System.out.println("-------- socketList : ----------------");
			rootCtrl.getSalon().getMapSocket().forEach((k, s) -> System.out.println("Client : " + k + " socket : " + s));
		}
	}
}
