package vce.session;

import vce.data.Question;
import vce.data.Questionnaire;
import vce.data.Reponse;

import java.util.HashMap;
import java.util.Map;

public class RepondreQuestionnaire {

    private Session session;
    private Questionnaire questionnaire;
    private Map<Integer, Reponse> reponses = new HashMap<>();
    private int indexMax;
    private int indexActuel;

    public RepondreQuestionnaire(Session session) {
        this.session = session;
        this.questionnaire = session.getQuestionnaire();
    }

    public synchronized void addReponse(Reponse reponse) {
        // TODO: 02/03/2017 doit etre appeler en meme temps que nextQuestion et previousQuestion par l'ihm
        reponses.put(indexActuel, reponse);
    }

    public Question nextQuestion() {
        // on recupere la question de l'index actuel (actuel car 0 est le premier)
        Question question;
        if (indexActuel < questionnaire.getQuestions().size() - 1) {
            question = questionnaire.getQuestions().get(indexActuel);
            if (indexActuel == indexMax) {
                indexMax++;
                session.setStatus(indexMax);
            }
            indexActuel++;
        } else {
            question = questionnaire.getQuestions().get(indexActuel);
        }
        return question;
    }

    public Question previousQuestion() {
        Question question;
        // si on est pas au premier index alors :
        if (indexActuel > 0) {
            question = questionnaire.getQuestions().get(indexActuel - 1);
            indexActuel--;
        } else { // sinon retourner la question actuelle
            question = questionnaire.getQuestions().get(indexActuel);
        }
        return question;
    }

    public synchronized void endQuestionnaire() {
        int[] score = new int[1];
        score[0] = 0;
        // boucle sur chaque entrer de la map pour additionner les reponses bonne
        reponses.forEach((q, r) -> {
            score[0] += r.isCorrect() ? 1 : 0;
        });
        System.out.println("score : " + score[0]);
        session.setScore(score[0]);
    }
}
