package vce.vues.controllers.questionnaire;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import vce.models.data.Question;
import vce.models.data.Reponse;
import vce.models.data.SessionUser;
import vce.vues.controllers.RootCtrl;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class QuestionnaireCtrl {
	public ListView<SessionUser> statusOther;
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

		// listener pour la progressBar afin de la mettre a jour :
		rootCtrl.getSalon().getCurrentUser().statusProperty().addListener((ov, old_val, new_val) -> {
			progressBar.setProgress(new_val.doubleValue() / 20);
		});


		// cellFactory de la liste des autre user afin d'afficher leur status :
		statusOther.setCellFactory(new Callback<ListView<SessionUser>, ListCell<SessionUser>>() {

			@Override
			public ListCell<SessionUser> call(ListView<SessionUser> p) {

				return new ListCell<SessionUser>() {

					@Override
					protected void updateItem(SessionUser t, boolean bln) {
						super.updateItem(t, bln);
						if (t != null) {
							setText(t.getPseudo() + " : " + t.getStatus());
						}
					}
				};
			}
		});

		// set la liste dans la listView des autre user :
		statusOther.setItems(rootCtrl.getSalon().getSessionList());

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
			if (rootCtrl.getSalon().getAvancement().getReponse() != null && rootCtrl.getSalon().getAvancement().getReponse().equals(r)) {
				reponseSlot.setSelected(true);
			}
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
		saveReponse();
		reponsesGroup.getToggles().clear();
		questionActual = rootCtrl.getSalon().getAvancement().previousQuestion();
		changeQuestion();
	}

	public void endQuestionnaire(ActionEvent event) {
		saveReponse();
		timer.cancel();
		rootCtrl.getSalon().getAvancement().endQuestionnaire();
		rootCtrl.goToResultats();
	}

	public void nextQuestion(ActionEvent event) {
		saveReponse();
		reponsesGroup.getToggles().clear();
		questionActual = rootCtrl.getSalon().getAvancement().nextQuestion();
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
		rootCtrl.goToResultats();
		rootCtrl.getSalon().getAvancement().endQuestionnaire();
	}
}
