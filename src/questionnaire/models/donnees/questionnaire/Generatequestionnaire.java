package questionnaire.models.donnees.questionnaire;


import questionnaire.models.donnees.question.Question;

import java.util.*;

public class Generatequestionnaire {

    public static List<Question> generate () {

        List<Question> questionnaire = new ArrayList<>();
        List<Question> allquestions = Question.getAllquestion();

        Collections.shuffle(allquestions);

        for (int i = 0; i < 20 ; i++) {
            questionnaire.add(allquestions.get(i));
        }

        return questionnaire;

    }

}
