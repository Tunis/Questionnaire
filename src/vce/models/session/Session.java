package vce.models.session;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import vce.models.data.Questionnaire;
import vce.models.data.SessionUser;
import vce.models.data.User;
import vce.vues.controllers.RootCtrl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Duration;

public class Session {

    private Socket socket;
    private Out out;
    protected final ObservableList<SessionUser> sessionList = FXCollections.observableArrayList();
    protected final SessionUser currentUser;
	protected Questionnaire questionnaire;

	protected RepondreQuestionnaire avancement;

    protected RootCtrl rootCtrl;
    //

    /*
        Constructeur, a besoin de l'user du client et de la socket de connection au server recuperer par connectServer() :
     */

    public Session(User user, Socket socket, RootCtrl rootCtrl) {
        this.rootCtrl = rootCtrl;
        currentUser = new SessionUser(user);
        this.socket = socket;
        new In();
        out = new Out();
        send();
    }

    /*
        Constructeur pour le salon
     */

    protected Session(User user, RootCtrl rootCtrl) {
        this.rootCtrl = rootCtrl;
        this.currentUser = new SessionUser(user);
    }

    /*
        methode lancant et soppant le test :
     */

	// lance le test, cree les objects necessaire :

    protected void startTest() {
        // cree un repondreQuestionnaire
        avancement = new RepondreQuestionnaire(this);
        rootCtrl.goToQuestionnaire();
    }

	// stop le test, supprime les object inutile :

	public void stopTest() {
		sessionList.add(currentUser);
		avancement = null;
	}

    /*
        on precise quand envoyé le current user (seul envoi effectué par le client)
     */

    public void send() {
        out.setToSend();
    }

    /*
        mise a jour des autre user lors de la reception d'un sessionUser par le server :
     */

	protected void updateSessionUserList(SessionUser user) {
            boolean[] found = new boolean[1];
            found[0] = false;
// TODO: pour gerer le cas du salon ajouter ici une simple verif que currentUser != user?
            if (!currentUser.getPseudo().equals(user.getPseudo())) {
            	System.out.println("----------------------------------------------------------");
                System.err.println(currentUser.getPseudo() + " : Serveur à envoyé => " + user.getPseudo());
                System.out.println("----------------------------------------------------------");
                // si trouver dans la liste on modifie les valeur actuel
                sessionList.forEach(s -> {
                    if (s.getPseudo().equals(user.getPseudo())) {
                    	System.err.println(currentUser.getPseudo() + " : modif list : " + user.getPseudo());
                        s.setScore(s.getScore() == user.getScore() ? s.getScore() : user.getScore());
                        s.setStatus(s.getStatus() == user.getStatus() ? s.getStatus() : user.getStatus());
                        s.setTempsFin(s.getTempsFin() == user.getTempsFin() ? s.getTempsFin() : user.getTempsFin());
                        found[0] = true;
                    }
                });
                
                
                // si on l'as pas trouver avant on l'ajoute.
                if (!found[0]) {
                	System.err.println(currentUser.getPseudo() + " : AddList : " + user.getPseudo());
                    Platform.runLater(() -> sessionList.add(user));
                }
            }

    }

    /*
        mise a jour du current user :
     */

	// retourne l'object gerant l'avancement du questionnaire :

	public RepondreQuestionnaire getAvancement() {
		return avancement;
	}

	// recupere le current user :

    public SessionUser getCurrentUser() {
        return currentUser;
    }

	// set le status du current user :

	public void setStatus(int indexMax) {
		this.currentUser.setStatus(indexMax);
	}

	// set le score du current user :

    public void setScore(int score) {
        this.currentUser.setScore(score);
    }

	// set le temps total du test du current user (mis a jour a la fin du test)

	public void setTime(Duration time) {
		this.currentUser.setTempsFin(time);
    }

    public ObservableList<SessionUser> getSessionList() {
            return sessionList;
    }

    /*
        on recupere le questionnaire :
     */

	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}

    public Socket getSocket() {
        return socket;
    }


    /*

        INNER CLASS :

     */

    private class Out implements Runnable {

        private ObjectOutputStream oos;
        private boolean toSend;

        public Out() {
            toSend = false;
	        new Thread(this).start();
        }

        /*
            simple toggle pour rentrer dans le traitement du thread.
         */

	    public void setToSend() {
		    this.toSend = true;
        }

	    private boolean getToSend() {
		    return toSend;
        }

        @Override
        public void run() {
            try {
                oos = new ObjectOutputStream((socket.getOutputStream()));
                oos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO: 02/03/2017 comme pour in tester la sortie de boucle
            while (true) {
                if (getToSend()) {
                    try {
                        oos.writeObject(currentUser);
                        oos.flush();
                        oos.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
	                    toSend = false;
                    }
                }
            }
        }
    }

    private class In implements Runnable {

        ObjectInputStream ois;
        Object received;

        public In() {
            new Thread(this).start();
        }

        @Override
        public void run() {
            // on cree l'imput stream au debut du thread
            try {
                ois = new ObjectInputStream((socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // TODO: 02/03/2017 test quoi mettre pour sortir de la boucle
            while (true) {
                // switch sur la classe recu, puis lancement d'un thread pour traiter l'info recu.
                try {
                    received = ois.readObject();
                    System.out.println("Class Reçus : " + received.getClass().getSimpleName());
                    switch (received.getClass().getSimpleName()) {
                        case "Questionnaire":
                            questionnaire = (Questionnaire) received;

                            startTest();
                            break;
                        case "SessionUser":
                            SessionUser user = (SessionUser) received;
	                        new Thread(() -> {
	                        	System.out.println("----------------------------------------------------------");
		                        System.err.println(currentUser.getPseudo() + " à reçu : " + user.getPseudo());
		                        updateSessionUserList(user);
	                        }).start();
	                        break;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                received = null;
            }
        }
    }

    /*
        tentative de connection au server :
     */

	public static Socket connectServer(String ip, int port) {
		Socket socket = null;
		try {
			socket = new Socket(ip, port);
			return socket;
		} catch (IOException ignored) {
		}
		return null;
	}
}
