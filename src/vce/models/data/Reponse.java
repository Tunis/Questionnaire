package vce.models.data;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

public class Reponse implements Serializable {
    private String reponse;
    private boolean correction;

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public void setCorrection(boolean correction) {
        this.correction = correction;
    }

    public Reponse() {
    }

    public Reponse(String reponse, boolean correction) {
        this.reponse = reponse;
        this.correction = correction;
    }

    @XmlElement(name = "TextReponses")
    public String getReponse() {
        return reponse;
    }

    @XmlElement(name = "isCorrect")
    public boolean isCorrect() {
        return correction;
    }
}
