package vce.ihm.controllers.questionnaire;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import vce.data.Question;
import vce.data.Reponse;
import vce.ihm.controllers.RootCtrl;

import java.util.List;

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


	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
		prevBTN.setVisible(false);
		endBTN.setVisible(false);
		nextQuestion(null);
	}

	public void changeQuestion() {
		slotQuestion.getChildren().clear();
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
	}

	public void prevQuestion(ActionEvent event) {
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
}
