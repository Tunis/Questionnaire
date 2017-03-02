package vce.data;

public class Reponse {
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
