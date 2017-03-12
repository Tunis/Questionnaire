package vce.models.session;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import vce.models.data.Question;
import vce.models.data.Questionnaire;
import vce.models.salon.Salon;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@XmlRootElement(name = "status")
public class RepondreQuestionnaire {
	private transient Session session;
	private Questionnaire questionnaire;
	private Map<Integer, Integer> reponses = new HashMap<>();
	private int indexMax = 0;
	private int indexActuel = 0;
	private transient Instant timeStart;

	private transient File tempFileJson;
	private transient File tempFileXML;

	private boolean recup = false;

	public void setQuestionnaire(Questionnaire questionnaire) {
		this.questionnaire = questionnaire;
	}

	public void setReponses(Map<Integer, Integer> reponses) {
		this.reponses = reponses;
	}

	public void setSession(Salon session) {
		this.session = session;
	}
	/*
	    Constructeur a besoin de la session actuelle :
     */

	public void setRecup(boolean recup) {
		this.recup = recup;
	}

	public RepondreQuestionnaire() {
	}

	public RepondreQuestionnaire(Session session) {
		this.session = session;
		this.questionnaire = session.getQuestionnaire();
		Collections.shuffle(questionnaire.getQuestionnaire());
		timeStart = Instant.now();
	}

    /*
        gestion des reponses :
     */

	@XmlElement(type = Questionnaire.class)
	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

	@XmlElementWrapper(name = "Reponses")
	public Map<Integer, Integer> getReponses() {
		return reponses;
	}

	// ajouter une reponse a la liste de reponse :

	public void addReponse(int indexReponse) {
		// est apeler avant previous/nextQuestion l'envoi est donc dans les autres methode
		reponses.put(indexActuel,
				indexReponse);
	}

	public int getIndexMax() {
		return indexMax;
	}

	public int getIndexActuel() {
		return indexActuel;
	}
	// recuperer la reponse de la question actuel choisi par l'user si elle existe sinon null :

	public int getReponse() {
		return reponses.getOrDefault(indexActuel, -1);
	}

    /*
        models d'avancement du questionnaire :
     */

	// aller a la question suivante :

	public Question nextQuestion() {
		Question question;
		// on verifie qu'on ne sorte pas de la liste (outOfBoundException)
		if (indexActuel < questionnaire.getQuestionnaire().size()) {
			// on recupere la question de l'index actuel (actuel car 0 est le premier)
			question = questionnaire.getQuestionnaire().get(indexActuel);
			if (indexActuel == indexMax) {
				// on passe a l'index suivant et on update
				indexMax++;
			}

			indexActuel++;
			// y'a surement une couille ici a cause du start a 0 a verifier.
			session.setStatus(indexActuel);
		} else {
			question = questionnaire.getQuestionnaire().get(indexActuel);
		}
		//
		// on envoi le tout
		update();
		// on retourne la question suivante a l'ui
		return question;
	}

	// revenir a la question precedente :

	public Question previousQuestion() {
		Question question;
		// si on n'est pas au premier index alors :
		if (indexActuel > 0) {
			--indexActuel;
			question = questionnaire.getQuestionnaire().get(indexActuel - 1);
		} else { // sinon retourner la question actuelle
			question = questionnaire.getQuestionnaire().get(indexActuel);
		}
		session.setStatus(indexActuel);
		update();
		// et on retourne la nouvelle question a l'ui
		return question;
	}

	// aller a la question x :

	public Question goToQuestion(int index) {
		Question question;
		// on recupere la question de l'index
		indexActuel = index - 1;
		question = questionnaire.getQuestionnaire().get(indexActuel);
		if (indexActuel == indexMax) {
			// on passe a l'index suivant et on update
			indexMax++;
		}

		indexActuel++;
		// y'a surement une couille ici a cause du start a 0 a verifier.
		session.setStatus(indexActuel);
		update();
		return question;
	}

    /*
        methode de fin de questionnaire, calcul le resultat de l'user et envoi le tout au server :
     */

	public void endQuestionnaire() {
		// obliger d'utiliser un tableau ici, dans les lambda les variable doivent etre final ou similaire.
		int[] score = new int[1];
		score[0] = 0;
		// boucle sur chaque entrer de la map pour additionner les reponses bonne.
		reponses.forEach((q, r) -> {
			System.out.println("question " + q + " : " + questionnaire.getQuestionnaire().get(q - 1).getIdQuestion());
			System.out.println("reponse " + r + " : " + questionnaire.getQuestionnaire().get(q - 1).getReponses().get(r).getReponse());
			System.out.println("correct : " + questionnaire.getQuestionnaire().get(q - 1).getReponses().get(r).isCorrection());
		});
		reponses.forEach((q, r) -> {
			score[0] += questionnaire.getQuestionnaire().get(q - 1).getReponses().get(r).isCorrection() ? 1 : 0;
		});
		// on met a jour le score.
		session.setScore(score[0]);
		// on met a jour le temps ecoulé.
		Duration timeFin;
		if (!recup) {
			timeFin = Duration.between(timeStart, Instant.now());
		} else {
			timeFin = Duration.ofMinutes(questionnaire.getDurationMax());
		}
		session.setTime(timeFin);
		// on envoi le tout.
		update();

		// TODO: 10/03/2017 methode de sauvegarde en bdd

		tempFileJson.delete();
		tempFileXML.delete();

		createCertificate();

		session.stopTest();
	}

	private void createCertificate() {
		PDDocument document = new PDDocument();
		PDPage page = new PDPage(PDRectangle.A4);

		PDFont font = PDType1Font.HELVETICA;
		float fontSize = 12.0f;

		String imgPath = "img/fond-certificat.jpg";

		try {
			File pdfDocument = new File("certificat " + session.user.getNom() + " " + session.user.getPrenom() + ".pdf");

			PDPageContentStream contentStream = null;

			try {

				PDRectangle pageSize = page.getMediaBox();
				float pageWidth = pageSize.getWidth();

				//Tourne à 90°
				page.setRotation(90);

				//Applique la rotation via une transformation par Matrice
				contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.OVERWRITE, true);
				contentStream.transform(new Matrix(0, 1, -1, 0, pageWidth, 0));

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					contentStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			try {
				//Récupère l'image
				PDImageXObject pdImage = PDImageXObject.createFromFile(imgPath, document);
				contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);

				// reduce this value if the image is too large
				float scale = 0.61f;

				//Ajout l'image au pdf
				contentStream.drawImage(pdImage, 20, 20, pdImage.getWidth() * scale, pdImage.getHeight() * scale);

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					contentStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}


			PDPageContentStream contents = null;

			try {
				contents = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
				contents.beginText();
				contents.setFont(font, fontSize);
				contents.newLineAtOffset(190, 150);
				DateFormat dateTimeFormatter = SimpleDateFormat.getInstance();
				contents.showText(dateTimeFormatter.format(Date.from(Instant.now())));
				contents.newLineAtOffset(390, 0);
				contents.showText("Afip formations");
				contents.newLineAtOffset(-340, 120);
				StringBuilder sb = new StringBuilder();
				sb.append("Mr ")
						.append(session.user.getNom())
						.append(" ")
						.append(session.user.getPrenom())
						.append(" a passer le test ")
						.append(questionnaire.getName())
						.append(" avec la note de ")
						.append(session.currentUser.getScore())
						.append("/")
						.append(questionnaire.getQuestionnaire().size())
						.append(".");
				contents.showText(sb.toString());
				contents.endText();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					contents.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			document.addPage(page);
			document.save(pdfDocument);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				document.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	private void update() {
		if (!recup) {
			if (session.getSocket() != null) {
				session.send();
			} else {
				Salon salon = (Salon) session;
				salon.sendAll("SESSION", session.getCurrentUser());
			}
		}
	}


	public void saveToFile() {
		tempFileJson = new File(session.getCurrentUser().getPseudo() + ".json");
		tempFileXML = new File(session.getCurrentUser().getPseudo() + ".xml");

		Object[] save = new Object[2];
		save[0] = questionnaire;
		save[1] = reponses;

		if (!tempFileJson.exists()) {
			try {
				tempFileJson.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try (Writer writer = new FileWriter(tempFileJson)) {
			GsonBuilder builder = new GsonBuilder();
			builder.setPrettyPrinting();
			Gson gson = builder.create();
			gson.toJson(this, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}


		/**
		 *  save to XML file.
		 */


		if (!tempFileXML.exists()) {
			try {
				tempFileXML.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			JAXBContext context = JAXBContext
					.newInstance(RepondreQuestionnaire.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

			// Marshalling and saving XML to the file.
			m.marshal(this, tempFileXML);

		} catch (Exception e) { // catches ANY exception
			e.printStackTrace();
		}

	}

}
