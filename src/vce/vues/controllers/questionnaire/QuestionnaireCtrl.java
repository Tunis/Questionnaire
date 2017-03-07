package vce.vues.controllers.questionnaire;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import vce.models.data.Question;
import vce.models.data.Reponse;
import vce.vues.controllers.RootCtrl;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class QuestionnaireCtrl {
	public VBox statusOther;
	public ProgressBar progressBar;
	public VBox slotQuestion;
	public ToggleGroup reponsesGroup;
	public Button prevBTN;
	public Button endBTN;
	public Button suivBTN;
	public Button btnActual;
	public Button btnMax;


	private RootCtrl rootCtrl;
	private Question questionActual;
	private ScheduledExecutorService timer = new ScheduledThreadPoolExecutor(1);


	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
		prevBTN.setVisible(false);
		endBTN.setVisible(false);
		nextQuestion(null);
		start();
	}

	public void changeQuestion() {
		slotQuestion.getChildren().remove(0, slotQuestion.getChildren().size());

		btnActual.setText(String.valueOf(rootCtrl.getSalon().getAvancement().getIndexActuel()));
		btnMax.setText(String.valueOf(questionActual.getIdQuestion()));


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

	private void start() {
		// lancer le thread qui forcera l'arret si on depasse le temps limite.
		System.out.println("lancement timer");
		timer.schedule(this::stopQuestionnaire, rootCtrl.getSalon().getQuestionnaire().getDurationMax(), TimeUnit.SECONDS);
	}

	private void stopQuestionnaire() {
		System.out.println("fin timer");
		rootCtrl.getSalon().getAvancement().endQuestionnaire();
		System.out.println("allons sur resultat :");
		rootCtrl.goToResultats();
	}
}
