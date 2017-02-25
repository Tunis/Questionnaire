package questionnaire.models.donnees.question;

import questionnaire.models.donnees.database.Db;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Reponses implements Serializable{

    private boolean correct;
    private String text;

    public Reponses(boolean correct, String text) {
        this.correct = correct;
        this.text = text;
    }

    public static List<Reponses> getAllreponses(int idQ) {

        Connection db = Db.getDb();
        List<Reponses> reponseslist = new ArrayList<>();
        if (db != null) {

            try {

                // Grace a l'id quesiotn on récupère les réponses associée
                PreparedStatement statement1 = null;

                statement1 = db.prepareStatement("SELECT * FROM Reponse WHERE idquestionReponse = ?");
                statement1.setInt(1, idQ);
                ResultSet reponses = statement1.executeQuery();

                // On boucle sur les retour de la requête des réponses
                while (reponses.next()) {
                    // On récupere les informations des réponses
                    boolean corectReponse = reponses.getBoolean("verifReponse");
                    String textReponse = reponses.getString("textReponse");

                    // On ajoute a la liste de réponses la réponse
                    reponseslist.add(new Reponses(corectReponse, textReponse));
                } //fin while reponse

                // On libère laquery reponses
                reponses.close();
                statement1.close();

                return reponseslist;


            }
            catch (SQLException e) {
                return null;
            }

        } // Fin if null
        return null;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return correct;
    }

}
