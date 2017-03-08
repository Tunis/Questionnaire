package vce.models.data;

import java.io.Serializable;
import java.time.Duration;

public class SessionUser implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int status = 0;
    private int score = 0;
	private Duration tempsFin;
	private String pseudo;
	private boolean isDelete = false;

    //Construct
    //----------------------------------
    public SessionUser(User user) {
        pseudo = user.getPseudo();
        tempsFin = Duration.ZERO;
    }

    //Setter
    //----------------------------------
    public void setScore(int sco) {
        score = sco;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setTempsFin(Duration tpsFin) {
		tempsFin = tpsFin;
    }
    
    public void setIsDelete(boolean del){
    	this.isDelete = del;
    }
    
    //Getter
    //----------------------------------
    public int getStatus() {
        return status;
    }

    public int getScore() {
        return score;
    }
    
    public boolean isDelete() {
        return isDelete;
    }

    public String getPseudo() {
        return pseudo;
    }

	public Duration getTempsFin() {
		return tempsFin;
    }

	


}
