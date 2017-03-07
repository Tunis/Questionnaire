package vce.methodes.session;

import vce.methodes.data.Question;
import vce.methodes.data.Questionnaire;
import vce.methodes.data.Reponse;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class RepondreQuestionnaire {

    private Session session;
    private Questionnaire questionnaire;
    private Map<Integer, Reponse> reponses = new HashMap<>();
    private int indexMax;
    private int indexActuel;

    private ScheduledExecutorService timer = new ScheduledThreadPoolExecutor(1);
    private Instant timeStart;

    /*
        Constructeur a besoin de la session actuelle :
     */

    public RepondreQuestionnaire(Session session) {
        this.session = session;
        this.questionnaire = session.getQuestionnaire();
        Collections.shuffle(questionnaire.getQuestionnaire());
        start();
    }

    /*
        gestion des reponses :
     */

	// ajouter une reponse a la liste de reponse :

	public void addReponse(Reponse reponse) {
		// est apeler avant previous/nextQuestion l'envoi est donc dans les autres methode
        reponses.put(indexActuel, reponse);
    }

    public int getIndexMax() {
        return indexMax;
    }

    public int getIndexActuel() {
        return indexActuel;
    }
    // recuperer la reponse de la question actuel choisi par l'user si elle existe sinon null :

	public Reponse getReponse() {
		return reponses.getOrDefault(questionnaire.getQuestionnaire().get(indexActuel), null);
    }

    /*
        methodes d'avancement du questionnaire :
     */

	// aller a la question suivante :

	public Question nextQuestion() {
		Question question;
        // on verifie qu'on ne sorte pas de la liste (outOfBoundException)
        if (indexActuel < questionnaire.getQuestionnaire().size() - 1) {
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
        // on envoi le tout
        addReponse(question.getReponses().get(ThreadLocalRandom.current().nextInt(0, 2)));
        System.out.println("client " + session.getCurrentUser().getPseudo() + " change de question");
        if (session.getSocket() != null) {
            session.send();
        }
        // on retourne la question suivante a l'ui
        return question;
    }

	// revenir a la question precedente :

	public Question previousQuestion() {
		Question question;
        // si on n'est pas au premier index alors :
        if (indexActuel > 0) {
            question = questionnaire.getQuestionnaire().get(indexActuel - 1);
            indexActuel--;
        } else { // sinon retourner la question actuelle
            question = questionnaire.getQuestionnaire().get(indexActuel);
        }
        addReponse(question.getReponses().get(ThreadLocalRandom.current().nextInt(0, 2)));
        // on envoie le tout (nouveau thread pour pas bloquer les autre traitement si l'envoi est long)
        if (session.getSocket() != null) {
            session.send();
        }
        // et on retourne la nouvelle question a l'ui
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
            score[0] += r.isCorrect() ? 1 : 0;
        });
        // on met a jour le score.
        session.setScore(score[0]);
        // on met a jour le temps ecoulé.
        session.setTime(Instant.now().toEpochMilli() - timeStart.toEpochMilli());
        // on envoi le tout.
        if (session.getSocket() != null) {
            session.send();
        }
        session.stopTest();
    }

    /*
        methode gerant la limite de temps pour le questionnaire :
     */

    private void start() {
        // definir le temp a l'instant de depart :
        timeStart = Instant.now();
        // lancer le thread qui forcera l'arret si on depasse le temps limite.
	    timer.schedule(this::endQuestionnaire, questionnaire.getDurationMax(), TimeUnit.MINUTES);
    }
}
