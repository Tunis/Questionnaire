package questionnaire.models.donnees.question;

import questionnaire.models.donnees.database.Db;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Question implements Serializable{

    private int id;
    private String text;
    private List<Reponses> reponseslist;

    public Question(int id, String text) {
        this.id = id;
        this.text = text;
        setReponses();
    }

    private void setReponses() {
        this.reponseslist = Reponses.getAllreponses(this.id);
    }

    public static List<Question> getAllquestion () {

        Connection db = Db.getDb();

        List<Question> questionlist = new ArrayList<>();

        if(db!=null)
        {
            try {

                // Connexion a la base de donnée
                Statement statement = db.createStatement();

                // Requêtes récupération des questions
                ResultSet questions = statement.executeQuery("SELECT * FROM Question");

                // On boucle sur le résultat des questions
                while (questions.next()) {

                    // On récupere les informations de la table question
                    int idQ = questions.getInt("idQuestion");
                    String textQ = questions.getString("textQuestion");

                    // On crée un objet question
                    Question question = new Question(idQ,textQ);

                    // On map les questions / réponses ensemble
                    questionlist.add(question);

                } // fin while question

                // on libère la query question
                questions.close();
                statement.close();

            } // fin try
            catch (SQLException e) {
                e.printStackTrace();
            } // fin catch

            return questionlist;
        }
        else
        {
            return null;
        }

    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public List<Reponses> getReponseslist() {
        return reponseslist;
    }
}
