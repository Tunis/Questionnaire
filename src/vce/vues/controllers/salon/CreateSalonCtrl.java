package vce.vues.controllers.salon;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import vce.models.data.Questionnaire;
import vce.vues.controllers.RootCtrl;

import java.sql.SQLException;

public class CreateSalonCtrl {

	public TextField champDuree;
	public ComboBox<Questionnaire> champQuestionnaire;
	public CheckBox servDed;
	public Button btnDisconnect;
	private RootCtrl rootCtrl;

	public void init(RootCtrl rootCtrl) {

		champQuestionnaire.setCellFactory(cell -> createSheetCell());
		champQuestionnaire.setButtonCell(createSheetCell());

		this.rootCtrl = rootCtrl;
		try {
			champQuestionnaire.setItems(FXCollections.observableArrayList(rootCtrl.getAuth().getListQuestionnaire()));
		} catch (SQLException ignored) {
			rootCtrl.goToSqlConfig();
		}

		Platform.runLater(() -> btnDisconnect.getParent().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_ANY), () -> {
			Disconnect(null);
		}));
	}

	public void createSalon(ActionEvent actionEvent) {
		if (champQuestionnaire.getSelectionModel().getSelectedItem() != null) {
			int duree = 20;
			if (!champDuree.getText().isEmpty()) {
				duree = Integer.valueOf(champDuree.getText());
			}
			Questionnaire questionnaire = champQuestionnaire.getSelectionModel().getSelectedItem();
			questionnaire.setDurationMax(duree);

			rootCtrl.createSalon(questionnaire, duree, servDed.isSelected());
			rootCtrl.goToSalon();
		} else {
			rootCtrl.error("Pas de questionnaire", "Veuillez choisir un questionnaire dans la liste.");
		}
	}

	public void Disconnect(ActionEvent actionEvent) {
		rootCtrl.goToLogin();
	}

	private ListCell<Questionnaire> createSheetCell() {
		return new ListCell<Questionnaire>() {
			@Override
			protected void updateItem(Questionnaire item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setText(null);
					setGraphic(null);
				} else {
					setText(item.getName());
				}
			}
		};
	}
}
