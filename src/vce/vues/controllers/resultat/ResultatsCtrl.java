package vce.vues.controllers.resultat;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import vce.models.data.ExportToPDF;
import vce.models.data.SessionUser;
import vce.vues.controllers.RootCtrl;

import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ResultatsCtrl {
	public TableView<SessionUser> resultatView;
	public TableColumn<SessionUser, String> colPseudo;
	public TableColumn<SessionUser, Integer> colScore;
	public TableColumn<SessionUser, String> colTime;
	public Button btnBack;
	public Button btnCertificat;
	public VBox bestResult;


	private RootCtrl rootCtrl;


	public void init(RootCtrl rootCtrl) {

		this.rootCtrl = rootCtrl;
		if (rootCtrl.getSalon().getCurrentUser().getScore() < 10) {
			btnCertificat.setVisible(false);
		}

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
			rootCtrl.getSalon().getSessionList().forEach(s -> {
				if (s.getTempsFin().isZero()) {
					btnBack.setVisible(false);
				}
			});
		}

		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				majBest();
			}
		}, 0, 1000);

		Platform.runLater(() -> btnBack.getParent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.B, KeyCombination.SHORTCUT_ANY), () -> {
			back(null);
		}));

		Platform.runLater(() -> btnCertificat.getParent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_ANY), () -> {
			createCertificat(null);
		}));
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
			btnBack.setVisible(true);
		}
	}

	private void majBest() {
		try {
			List<SessionUser> bestResultat = rootCtrl.getAuth().getResultat(rootCtrl.getSalon().getQuestionnaire().getIdQuestionnaire());
			for (int i = 0; i < bestResultat.size(); i++) {
				String format = String.format("%02dmin %02ds",
						(bestResultat.get(i).getTempsFin().getSeconds() % 3600) / 60,
						(bestResultat.get(i).getTempsFin().getSeconds() % 60));
				int e = i;
				Label text = (Label) bestResult.getChildren().get(i);
				Platform.runLater(() -> text.setText(bestResultat.get(e).getPseudo() + " à eu " + bestResultat.get(e).getScore() + " en " + format));
			}
		} catch (SQLException ignored) {
		}
	}


	public void back(ActionEvent actionEvent) {
		rootCtrl.goToLogin();
	}

	public void createCertificat(ActionEvent event) {
		ExportToPDF certificat = new ExportToPDF();
		certificat.createCertificate(rootCtrl.getSalon().getQuestionnaire().getName(),
				rootCtrl.getSalon().getUser().getNom(), rootCtrl.getSalon().getUser().getPrenom(),
				rootCtrl.getSalon().getCurrentUser().getScore(),
				rootCtrl.getSalon().getQuestionnaire().getQuestionnaire().size());
	}
}
