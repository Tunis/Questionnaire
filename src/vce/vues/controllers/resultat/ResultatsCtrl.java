package vce.vues.controllers.resultat;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import vce.models.data.SessionUser;
import vce.vues.controllers.RootCtrl;

public class ResultatsCtrl {
	public TableView<SessionUser> resultatView;
	public TableColumn<SessionUser, String> colPseudo;
	public TableColumn<SessionUser, Integer> colScore;
	public TableColumn<SessionUser, String> colTime;
	public Button btnDeco;
	public Button btnBack;


	private RootCtrl rootCtrl;


	public void init(RootCtrl rootCtrl) {


		this.rootCtrl = rootCtrl;


		resultatView.setItems(rootCtrl.getSalon().getSessionList());
		colPseudo.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getPseudo()));
		colScore.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getScore()));
		colTime.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(
				String.format("%02dmin %02ds",
						(cellData.getValue().getTempsFin().getSeconds() % 3600) / 60,
						(cellData.getValue().getTempsFin().getSeconds() % 60))));

		resultatView.getSortOrder().add(colScore);
		resultatView.getSortOrder().add(colTime);
		resultatView.getSortOrder().add(colPseudo);
		resultatView.sort();

        if (rootCtrl.getSalon().getSessionList().size() > 1) {
            btnBack.setVisible(false);
            btnDeco.setVisible(false);
        }
    }

	public void update() {
		resultatView.refresh();
		int[] notCompleted = new int[1];
		notCompleted[0] = 0;
		rootCtrl.getSalon().getSessionList().forEach(s -> {
			if (s.getTempsFin().isZero()) {
				notCompleted[0]++;
			}
		});
		if (notCompleted[0] == 0) {
			btnDeco.setVisible(true);
			btnBack.setVisible(true);
		}
	}

	public void back(ActionEvent actionEvent) {
		rootCtrl.goToJoinSalon();
	}

	public void disconnect(ActionEvent actionEvent) {
		rootCtrl.goToLogin();
	}
}
