package vce.data;

import vce.bdd.Bdd;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Questionnaire implements Serializable {

    private List<Question> questions = new ArrayList<>();
    private int durationMax;

    public Questionnaire(int durationMax) {
        this.durationMax = durationMax;
        // TODO: 02/03/2017 doit etre changer pour recuperer seulement 20 random questions
        questions = getQuestionList();
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getDurationMax() {
        return durationMax;
    }


    // TODO: 02/03/2017 placeHolder :
    private List<Question> getQuestionList ()
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
}
