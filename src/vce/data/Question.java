package vce.data;

import java.util.ArrayList;
import java.util.List;

public class Question {

    private int idQuestion;
    private String question;
    private List<Reponse> reponses = new ArrayList<>();

    public Question(int id, String question) {
        this.idQuestion = id;
        this.question = question;
    }

    // TODO: 02/03/2017 recuperation des reponse de cette question

    public int getIdQuestion() {
        return idQuestion;
    }

    public String getQuestion() {
        return question;
    }

    public List<Reponse> getReponses() {
        return reponses;
    }

    /*
    private List<Reponse> recupReponse ()
    {

        List<Reponse> reponsesList = new ArrayList<>();

        try
        {
            PreparedStatement getReponses = db.prepareStatement("SELECT textReponse, verifReponse FROM Reponse WHERE idquestionReponse = ?");
            getReponses.setInt(1,idQuestion);
            ResultSet reponse = getReponses.executeQuery();

            while (reponse.next())
            {
                String reponseText = reponse.getString("textReponse");
                boolean reponseVerif = reponse.getBoolean("verifReponse");
                reponsesList.add(new Reponse(reponseText,reponseVerif));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return reponsesList;
    }
    */
}
