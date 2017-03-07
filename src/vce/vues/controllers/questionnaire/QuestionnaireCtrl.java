package vce.vues.controllers.questionnaire;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import vce.models.data.Question;
import vce.models.data.Reponse;
import vce.vues.controllers.RootCtrl;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class QuestionnaireCtrl {
	public VBox statusOther;
	public ProgressBar progressBar;
	public VBox slotQuestion;
	public ToggleGroup reponsesGroup;
	public Button prevBTN;
	public Button endBTN;
	public Button suivBTN;


	private RootCtrl rootCtrl;
	private Question questionActual;
	private Timer timer = new Timer();


	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
		reponsesGroup = new ToggleGroup();
		prevBTN.setVisible(false);
		endBTN.setVisible(false);
		nextQuestion(null);
		start();
	}

	public void changeQuestion() {
		slotQuestion.getChildren().remove(0, slotQuestion.getChildren().size());

		FlowPane questionText = new FlowPane(new Label(questionActual.getQuestion()));
		slotQuestion.getChildren().add(questionText);
		List<Reponse> reponses = questionActual.getReponses();
		reponses.forEach(r -> {
			RadioButton reponseSlot = new RadioButton(r.getReponse());
			reponseSlot.setToggleGroup(reponsesGroup);
			slotQuestion.getChildren().add(reponseSlot);
		});
		if (rootCtrl.getSalon().getAvancement().getIndexActuel() > 1) {
			prevBTN.setVisible(true);
		} else {
			prevBTN.setVisible(false);
		}
		if (rootCtrl.getSalon().getAvancement().getIndexMax() == 20) {
			endBTN.setVisible(true);
		} else {
			endBTN.setVisible(false);
		}
		if (rootCtrl.getSalon().getAvancement().getIndexActuel() < 20) {
			suivBTN.setVisible(true);
		} else {
			suivBTN.setVisible(false);
		}
		if (rootCtrl.getSalon().getAvancement().getReponse() != null) {
			System.out.println("reponse connu");
		}
	}

	public void prevQuestion(ActionEvent event) {
		RadioButton rep = (RadioButton) reponsesGroup.getSelectedToggle();
		rep.getText();
		questionActual = rootCtrl.getSalon().getAvancement().previousQuestion();
		changeQuestion();
	}

	public void endQuestionnaire(ActionEvent event) {
		rootCtrl.getSalon().getAvancement().endQuestionnaire();
		rootCtrl.goToResultats();
	}

	public void nextQuestion(ActionEvent event) {
		questionActual = rootCtrl.getSalon().getAvancement().nextQuestion();
		changeQuestion();
	}

	private void start() {
		//timer.schedule(this::test, rootCtrl.getSalon().getQuestionnaire().getDurationMax(), TimeUnit.SECONDS);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				stopQuestionnaire();
			}
		}, rootCtrl.getSalon().getQuestionnaire().getDurationMax() * 1000);

	}

	private void stopQuestionnaire() {
		rootCtrl.goToResultats();
		rootCtrl.getSalon().getAvancement().endQuestionnaire();
	}
}
