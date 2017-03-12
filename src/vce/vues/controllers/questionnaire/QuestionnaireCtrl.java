package vce.vues.controllers.questionnaire;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Callback;
import javafx.util.Duration;
import vce.models.data.Question;
import vce.models.data.Reponse;
import vce.models.data.SessionUser;
import vce.vues.controllers.RootCtrl;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class QuestionnaireCtrl {
	public ListView<SessionUser> statusOther;
	public VBox slotQuestion;
	public ToggleGroup reponsesGroup;
	public Button prevBTN;
	public Button endBTN;
	public Button suivBTN;
	public ProgressBar progressBarTime;
	public ButtonBar buttonBar;

	private Timeline timeline;
	private IntegerProperty timeSeconds;
	private int TIMESTART;
	private Tooltip ttTimer;


	private RootCtrl rootCtrl;
	private Question questionActual;
	private Timer timer = new Timer();

	private IntegerProperty status = new SimpleIntegerProperty(0);


	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
		ttTimer = new Tooltip();
		int nbBtn = rootCtrl.getSalon().getQuestionnaire().getQuestionnaire().size();
		for (int i = 0; i < nbBtn; i++) {
			Button newBtn = new Button((i + 1) + "");
			newBtn.setOnAction(this::goToQuestion);
			ButtonBar.setButtonData(newBtn, ButtonBar.ButtonData.LEFT);
			buttonBar.getButtons().add(newBtn);
		}
		checkBtn();

		TIMESTART = rootCtrl.getSalon().getQuestionnaire().getDurationMax() * 60;
		timeSeconds = new SimpleIntegerProperty(TIMESTART * 100);

		// listener pour la progressBar afin de la mettre a jour :

		progressBarTime.progressProperty().bind(timeSeconds.divide(TIMESTART * 100.0));

		timeline = new Timeline();
		timeline.getKeyFrames().add(
				new KeyFrame(Duration.seconds(TIMESTART + 1),
						new KeyValue(timeSeconds, 0)));
		timeline.playFromStart();

		status.addListener((ov, old_val, new_val) -> {
			// changement de question
		});

		timeSeconds.addListener((observable, oldValue, newValue) -> {
			ttTimer.setText(String.format("%02dmin %02ds",
					((timeSeconds.get() / 100) % 360000) / 60,
					((timeSeconds.get() / 100) % 360000 % 60)));
		});

		progressBarTime.setTooltip(ttTimer);

		// cellFactory de la liste des autre user afin d'afficher leur status :
		statusOther.setCellFactory(new Callback<ListView<SessionUser>, ListCell<SessionUser>>() {

			@Override
			public ListCell<SessionUser> call(ListView<SessionUser> p) {

				return new ListCell<SessionUser>() {

					@Override
					protected void updateItem(SessionUser t, boolean bln) {
						super.updateItem(t, bln);
						if (t != null) {
							Platform.runLater(() -> setText(t.getPseudo() + " : " + t.getStatus()));
						} else {
							Platform.runLater(() -> setText(""));
						}
					}
				};
			}
		});

		// set la liste dans la listView des autre user :
		statusOther.setItems(rootCtrl.getSalon().getSessionList());

		reponsesGroup = new ToggleGroup();
		prevBTN.setVisible(false);
		nextQuestion(null);
		start();
	}

	public void changeQuestion() {
		slotQuestion.getChildren().remove(0, slotQuestion.getChildren().size());

		TextFlow questionText = new TextFlow();
		questionText.getChildren().add(new Text(questionActual.getQuestion()));
		slotQuestion.getChildren().add(questionText);
		List<Reponse> reponses = questionActual.getReponses();
		for (int i = 0; i < reponses.size(); i++) {
			RadioButton reponseSlot = new RadioButton(reponses.get(i).getReponse());
			reponseSlot.setWrapText(true);
			reponseSlot.setToggleGroup(reponsesGroup);
			slotQuestion.getChildren().add(reponseSlot);
			if (rootCtrl.getSalon().getAvancement().getReponse() > -1 && rootCtrl.getSalon().getAvancement().getReponse() == i) {
				reponseSlot.setSelected(true);
			}
		}
		checkBtn();
		if (rootCtrl.getSalon().getAvancement().getIndexActuel() > 1) {
			prevBTN.setVisible(true);
		} else {
			prevBTN.setVisible(false);
		}
		if (rootCtrl.getSalon().getAvancement().getIndexActuel() < 20) {
			suivBTN.setVisible(true);
		} else {
			suivBTN.setVisible(false);
		}
	}

	public void prevQuestion(ActionEvent event) {
		saveReponse();
		reponsesGroup.getToggles().clear();
		questionActual = rootCtrl.getSalon().getAvancement().previousQuestion();
		status.setValue(rootCtrl.getSalon().getAvancement().getIndexActuel() - 1);
		changeQuestion();

		rootCtrl.getSalon().getAvancement().saveToFile();
	}

	public void endQuestionnaire(ActionEvent event) {
		saveReponse();

		rootCtrl.getSalon().getAvancement().saveToFile();
		timer.cancel();
		rootCtrl.getSalon().getAvancement().endQuestionnaire();
		rootCtrl.goToResultats();
	}

	public void nextQuestion(ActionEvent event) {
		saveReponse();
		reponsesGroup.getToggles().clear();
		questionActual = rootCtrl.getSalon().getAvancement().nextQuestion();
		status.setValue(rootCtrl.getSalon().getAvancement().getIndexActuel() - 1);

		rootCtrl.getSalon().getAvancement().saveToFile();

		changeQuestion();
	}

	public void goToQuestion(ActionEvent event) {
		saveReponse();
		Button btn = (Button) event.getSource();
		reponsesGroup.getToggles().clear();
		questionActual = rootCtrl.getSalon().getAvancement().goToQuestion(buttonBar.getButtons().indexOf(btn) + 1);
		status.setValue(rootCtrl.getSalon().getAvancement().getIndexActuel() - 1);

		rootCtrl.getSalon().getAvancement().saveToFile();

		changeQuestion();
	}

	private void saveReponse() {
		// save methode
		RadioButton rep = (RadioButton) reponsesGroup.getSelectedToggle();
		int indexRep = reponsesGroup.getToggles().indexOf(rep);
		if (indexRep > -1) {
			rootCtrl.getSalon().getAvancement().addReponse(indexRep);
		}
	}

	private void checkBtn() {
		for (int i = 0; i < buttonBar.getButtons().size(); i++) {
			Button newBtn = (Button) buttonBar.getButtons().get(i);
			int isSolved = rootCtrl.getSalon().getAvancement().getReponses().getOrDefault(i + 1, -1);
			if (isSolved != -1) {
				newBtn.setBackground(new Background(new BackgroundFill(
						Color.DARKGREEN,
						CornerRadii.EMPTY,
						Insets.EMPTY)));

			} else {
				newBtn.setBackground(new Background(new BackgroundFill(
						Color.CHOCOLATE,
						CornerRadii.EMPTY,
						Insets.EMPTY)));
			}
			if (status.get() == i) {
				newBtn.setDisable(true);
			} else {
				newBtn.setDisable(false);
			}
		}
	}

	private void start() {
		//timer.schedule(this::test, rootCtrl.getSalon().getQuestionnaire().getDurationMax(), TimeUnit.SECONDS);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				stopQuestionnaire();
			}
		}, rootCtrl.getSalon().getQuestionnaire().getDurationMax() * 60000);

	}

	private void stopQuestionnaire() {
		saveReponse();

		rootCtrl.getSalon().getAvancement().saveToFile();
		timer.cancel();
		rootCtrl.goToResultats();
		rootCtrl.getSalon().getAvancement().endQuestionnaire();
	}

	public void update() {
		statusOther.refresh();
	}
}
