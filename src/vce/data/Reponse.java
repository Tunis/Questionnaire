package vce.data;

import java.io.Serializable;

public class Reponse implements Serializable {
    private String reponse;
    private boolean correction;

    public Reponse(String reponse, boolean correction) {
        this.reponse = reponse;
        this.correction = correction;
    }

    public String getReponse() {
        return reponse;
    }

    public boolean isCorrect() {
        return correction;
    }
}
