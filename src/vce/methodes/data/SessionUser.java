package vce.methodes.data;

import java.io.Serializable;

public class SessionUser implements Serializable {

    private int status = 0;
    private int score = 0;
    private long tempsFin = 0;
    private String pseudo;

    //Construct
    //----------------------------------
    public SessionUser(User user) {
        pseudo = user.getPseudo();
    }

    //Setter
    //----------------------------------
    public void setStatus(int sta) {
        status = sta;
    }

    //Getter
    //----------------------------------
    public int getStatut() {
        return status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int sco) {
        score = sco;
    }

    public String getPseudo() {
        return pseudo;
    }

    public long getTempsFin() {
        return tempsFin;
    }

    public void setTempsFin(long tpsFin) {
        tempsFin = tpsFin;
    }


}
