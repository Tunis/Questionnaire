package vce.models.data;

import javax.xml.bind.annotation.XmlElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Questionnaire implements Serializable {

	private String name;
	private int idQuestionnaire;

	private List<Question> questions = new ArrayList<>();
	private int durationMax;

	public Questionnaire() {
	}

	public Questionnaire(int idQuestionnaire, String name, List<Question> questions) {
		this.name = name;
		this.idQuestionnaire = idQuestionnaire;
		Collections.shuffle(questions);
		this.questions = questions;
	}


	// SETTERS :
	public void setName(String name) {
		this.name = name;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public void setDurationMax(int durationMax) {
		this.durationMax = durationMax;
	}

	// GETTERS :

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	@XmlElement(type = Question.class, name = "Question")
	public List<Question> getQuestionnaire() {
		return questions;
	}

	@XmlElement(name = "dureeMax")
	public int getDurationMax() {
		return durationMax;
	}
	
	public int getIdQuestionnaire(){
		return this.idQuestionnaire;
	}
}
