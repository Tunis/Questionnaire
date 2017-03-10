package vce.models.data;

import vce.models.bdd.Bdd;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable {

    private int idQuestion;
    private String question;
    private List<Reponse> reponses = new ArrayList<>();

    public Question() {
    }

    public void setIdQuestion(int idQuestion) {
        this.idQuestion = idQuestion;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setReponses(List<Reponse> reponses) {
        this.reponses = reponses;
    }

    public Question(int id, String question) {
        this.idQuestion = id;
        this.question = question;
        recupReponse();
    }

    @XmlElement(name = "idQuestion")
    public int getIdQuestion() {
        return idQuestion;
    }

    @XmlElement(name = "textQuestion")
    public String getQuestion() {
        return question;
    }

    @XmlElement(type = Reponse.class, name = "Reponse")
    public List<Reponse> getReponses() {
        return reponses;
    }


    private void recupReponse() {
        try
        {
            PreparedStatement getReponses = Bdd.getInstance().prepareStatement("SELECT textReponse, verifReponse FROM Reponse WHERE idquestionReponse = ?");
            getReponses.setInt(1,idQuestion);
            ResultSet reponse = getReponses.executeQuery();

            while (reponse.next())
            {
                String reponseText = reponse.getString("textReponse");
                boolean reponseVerif = reponse.getBoolean("verifReponse");
                reponses.add(new Reponse(reponseText, reponseVerif));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

}
