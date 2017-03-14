package vce.vues.controllers.resultat;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import vce.models.data.ExportToPDF;
import vce.models.data.SessionUser;
import vce.vues.controllers.RootCtrl;

import java.sql.SQLException;
import java.util.List;

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

		try {
			List<SessionUser> bestResultat = rootCtrl.getAuth().getResultat(rootCtrl.getSalon().getQuestionnaire().getIdQuestionnaire());
			bestResultat.forEach(br -> {
				String format = String.format("%02dmin %02ds",
						(br.getTempsFin().getSeconds() % 3600) / 60,
						(br.getTempsFin().getSeconds() % 60));
				Label bestUser = new Label(br.getPseudo() + " à eu " + br.getScore() + " en " + format);
				bestResult.getChildren().add(bestUser);
			});
		} catch (SQLException ignored) {
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
			btnBack.setVisible(true);
		}
		try {
			bestResult.getChildren().clear();
			List<SessionUser> bestResultat = rootCtrl.getAuth().getResultat(rootCtrl.getSalon().getQuestionnaire().getIdQuestionnaire());
			bestResultat.forEach(br -> {
				String format = String.format("%02dmin %02ds",
						(br.getTempsFin().getSeconds() % 3600) / 60,
						(br.getTempsFin().getSeconds() % 60));
				Label bestUser = new Label(br.getPseudo() + " à eu " + br.getScore() + " en " + format);
				bestResult.getChildren().add(bestUser);
			});
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
