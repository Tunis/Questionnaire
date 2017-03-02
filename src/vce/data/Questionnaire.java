package vce.data;

import java.util.ArrayList;
import java.util.List;

public class Questionnaire {

    private List<Question> questions = new ArrayList<>();
    private int durationMax;

    public Questionnaire(int durationMax) {
        this.durationMax = durationMax;
    }


    /*
    private List<Question> getQuestionList ()
    {
        try
        {
            Statement getAllQuestions = db.createStatement();
            ResultSet questions = getAllQuestions.executeQuery("SELECT * FROM Question");
            List<Question> questionList= new ArrayList<>();
            List<Reponse> reponsesList = new ArrayList<>();

            while(questions.next())
            {
                int idQuestion = questions.getInt("idQuestion");
                String questionText = questions.getString("textQuestion");
                questionList.add(new Question(idQuestion,questionText));
            }

            return questionList;

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }
    */
}
