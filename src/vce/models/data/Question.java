package vce.models.data;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
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

	public Question(int id, String question, List<Reponse> reponses) {
		this.idQuestion = id;
		this.question = question;
		this.reponses = reponses;
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

}
