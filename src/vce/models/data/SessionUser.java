package vce.models.data;

import java.io.Serializable;
import java.time.Duration;

public class SessionUser implements Serializable {

    private int status = 0;
    private int score = 0;
	private Duration tempsFin;
	private String pseudo;
    //

    //Construct
    //----------------------------------
    public SessionUser(User user) {
        pseudo = user.getPseudo();
    }

    //Setter
    //----------------------------------


    public void setStatus(int status) {
        this.status = status;
    }

    //Getter
    //----------------------------------
    public int getStatus() {
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

	public Duration getTempsFin() {
		return tempsFin;
    }

	public void setTempsFin(Duration tpsFin) {
		tempsFin = tpsFin;
    }


}
