package questionnaire.models.donnees.questionnaire;

import questionnaire.models.donnees.question.Question;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;

public class Questionnaire implements Serializable {

    private List<Question> questionnaire;
    private Duration time;
    private int idquestion, nbQ;

    // Constructeur
    public Questionnaire() {

        questionnaire = Generatequestionnaire.generate();
        time = Duration.ofMinutes(10);
        nbQ  = questionnaire.size();

    }

    // Question suivante
    public Question nextQ() {

        if(idquestion < 20)
        {
            Question question;
            question = questionnaire.get(idquestion);
            idquestion++;
            return question;
        }
        else {
            return null;
        }

    }

    // Question précédente
    public Question prevQ(){

        if(idquestion >= 0) {
            Question question;
            idquestion--;
            question = questionnaire.get(idquestion-1);
            return question;
        }
        else {
            return null;
        }
    }

    // Envoie la liste des questions
    public List<Question> getQuestionnaire() {
        return questionnaire;
    }

    // Envoie le temps
    public Duration getTime() {
        return time;
    }

    // Envoie l'id de la question
    public int getIdquestion() {
        return idquestion;
    }

    // Envoie le nomvbe de question
    public int getNbQ() {
        return nbQ;
    }


}
