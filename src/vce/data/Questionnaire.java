package vce.data;

import vce.bdd.Bdd;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Questionnaire implements Serializable {

    private List<Question> questions = new ArrayList<>();
    private int durationMax;

    public Questionnaire(int durationMax) {
        this.durationMax = durationMax;
        questions = getQuestionListfromdb();
        Collections.shuffle(questions);
        questions = generateQuestionnaire();
    }

    public List<Question> getQuestionnaire() {
        return questions;
    }

    public int getDurationMax() {
        return durationMax;
    }


    // TODO: 02/03/2017 placeHolder :
    private List<Question> getQuestionListfromdb()
    {
        try
        {
            Statement getAllQuestions = Bdd.getInstance().createStatement();
            ResultSet questions = getAllQuestions.executeQuery("SELECT * FROM Question");
            List<Question> questionList= new ArrayList<>();

            while(questions.next())
            {
                int idQuestion = questions.getInt("idQuestion");
                String questionText = questions.getString("textQuestion");
                questionList.add(new Question(idQuestion, questionText));
            }

            return questionList;

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    private List<Question> generateQuestionnaire()
    {

        List<Question> questionnaire = new ArrayList<>();

        Collections.shuffle(questions);

        for (int i = 0; i < 20; i++) {
            questionnaire.add(questions.get(i));
        }

        Collections.shuffle(questionnaire);

        return questionnaire;
    }
}
