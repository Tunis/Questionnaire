package vce.session;

import vce.data.Question;
import vce.data.Questionnaire;
import vce.data.Reponse;

import java.util.ArrayList;
import java.util.List;

public class RepondreQuestionnaire {

    private Session session;
    private Questionnaire questionnaire;
    private List<Reponse> reponses = new ArrayList<>();
    private int indexMax;
    private int indexActuel;

    public RepondreQuestionnaire(Session session) {
        this.session = session;
        this.questionnaire = session.getQuestionnaire();
    }

    public Question nextQuestion() {
        Question question = questionnaire.getQuestions().get(indexActuel);
        if (indexActuel == indexMax) {
            indexMax++;
            session.setStatus(indexMax);
        }
        indexActuel++;

        return question;
    }

    public Question previousQuestion() {
        Question question = questionnaire.getQuestions().get(indexActuel - 1);

        indexActuel--;

        return question;
    }

    public void endQuestionnaire() {
        int[] score = new int[1];
        score[0] = 0;
        reponses.forEach(r -> {
            score[0] += r.isCorrect() ? 1 : 0;
        });

        session.setScore(score[0]);
    }

}
