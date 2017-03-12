package vce.models.data;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;

public class Reponse implements Serializable {
	private int id;
	private String reponse;
	private boolean correction;

	public void setId(int id) {
		this.id = id;
	}

	public void setReponse(String reponse) {
		this.reponse = reponse;
	}

	public void setCorrection(boolean correction) {
		this.correction = correction;
	}

	public Reponse() {
	}

	public Reponse(int id, String reponse, boolean correction) {
		this.id = id;
		this.reponse = reponse;
		this.correction = correction;
	}

	@XmlElement(name = "TextReponses")
	public String getReponse() {
		return reponse;
	}

	@XmlElement(name = "isCorrection")
	public boolean isCorrection() {
		return correction;
	}

	@XmlElement(name = "idReponse")
	public int getId() {
		return id;
	}
}
