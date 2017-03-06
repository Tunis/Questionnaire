package vce.session;

import com.sun.org.apache.xpath.internal.SourceTree;
import vce.data.Questionnaire;
import vce.data.SessionUser;
import vce.data.User;
import vce.salon.Salon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Session {

    private Socket socket;
    private Out out;
	protected final List<SessionUser> sessionList = new ArrayList<>();
	protected final SessionUser currentUser;
	protected Questionnaire questionnaire;

	protected RepondreQuestionnaire avancement;

    /*
        Constructeur, a besoin de l'user du client et de la socket de connection au server recuperer par connectServer() :
     */

    public Session(User user, Socket socket) {
        currentUser = new SessionUser(user);
        this.socket = socket;
        new In();
        out = new Out();
        send();
    }

    /*
        Constructeur pour le salon
     */

	protected Session(User user) {
		this.currentUser = new SessionUser(user);
	}

    /*
        methode lancant et soppant le test :
     */

	// lance le test, cree les objects necessaire :

    protected void startTest() {
        // cree un repondreQuestionnaire
        System.out.println("on lance le test de " + currentUser.getPseudo());
        avancement = new RepondreQuestionnaire(this);
    }

	// stop le test, supprime les object inutile :

	public void stopTest() {
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
        synchronized (sessionList){
            if (!currentUser.getPseudo().equals(user.getPseudo())) {
                System.out.println("serveur a envoyé : " + user.getPseudo() + " => " + currentUser.getPseudo());
                System.out.println();
                // si trouver dans la liste on modifie les valeur actuel
                sessionList.forEach(s -> {
                    if (s.getPseudo().equals(user.getPseudo())) {
                        s.setScore(s.getScore() == user.getScore() ? s.getScore() : user.getScore());
                        s.setStatus(s.getStatut() == user.getStatut() ? s.getStatut() : user.getStatut());
                        s.setTempsFin(s.getTempsFin() == user.getTempsFin() ? s.getTempsFin() : user.getTempsFin());
                        found[0] = true;
                    }
                });

                // si on l'as pas trouver avant on l'ajoute.
                if (!found[0]) {
                    sessionList.add(user);
                }
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

    public void setTime(long time) {
        this.currentUser.setTempsFin(time);
    }


    /*
        on recupere le questionnaire :
     */

	public Questionnaire getQuestionnaire() {
		return questionnaire;
	}


    /*

        INNER CLASS :

     */

    private class Out implements Runnable {

        private ObjectOutputStream oos;
        private boolean toSend;

        public Out() {
            toSend = false;
            Thread t = new Thread(this);
            t.setName("out");
            t.start();
        }

        /*
            simple toggle pour rentrer dans le traitement du thread.
         */

        public void setToSend() {
            this.toSend = true;
        }

        private synchronized boolean getToSend() {
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
                    switch (received.getClass().getSimpleName()) {
                        case "Questionnaire":
                            questionnaire = (Questionnaire) received;
                            startTest();
                            break;
                        case "SessionUser":
                            SessionUser user = (SessionUser) received;
                            new Thread(() -> updateSessionUserList(user)).start();
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

    /*
        methode pour test :
     */

	public static void main(String[] args) throws IOException {
		Salon[] salon = new Salon[1];
		new Thread(() -> salon[0] = new Salon(new User(9, "salon", "salon", "salon"), 1)).start();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		new Thread(() -> client(salon[0], 1)).start();
		//new Thread(() -> client(salon[0], 2)).start();
        //new Thread(() -> client(salon[0], 3)).start();
        //new Thread(() -> client(salon[0], 4)).start();
        new Thread(() -> client(salon[0], 5)).start();


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        salon[0].startQuestionnaire();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("liste server :");
        salon[0].getSessionListServer().forEach((s)-> System.out.println(s.getPseudo()));
        System.out.println("liste session du serveur :");
        salon[0].sessionList.forEach((s)-> System.out.println(s.getPseudo()));
        System.out.println("current user :");
        System.out.println(salon[0].getCurrentUser().getPseudo());
    }

	public static void client(Salon salon, int id) {
		Socket s;
		do {
			s = Session.connectServer("localhost", 30000);
		} while (s == null);
		Session session = new Session(new User(1, "fred", "fred", "fred" + id), s);



		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// on a pas forcement tous les autres client ici c'est pas normal :/
		// TODO: a verifier ici les premier client semble ne pas recevoir les nouvelle connexion?
		System.out.println("-----client list :-----");
		session.sessionList.forEach(sS -> System.out.println("clientlist : " + session.currentUser.getPseudo() + " - " + sS.getPseudo()));
		System.out.println("------------------------");
		int i = 0;
		int max = ThreadLocalRandom.current().nextInt(5, 10);
		while (session.avancement != null && i < max) {
			i++;
			session.avancement.nextQuestion();
			if (i == max) session.avancement.endQuestionnaire();
			try {
				Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		try {
			Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println();
		System.out.println("propre resultat :" + session.getCurrentUser().getPseudo());
		System.out.println("score :" + session.currentUser.getScore());
		Duration duree = Duration.ofMillis(session.currentUser.getTempsFin());
		System.out.println("temp passé : " + duree.getSeconds() + " s");


		for (SessionUser user : session.sessionList) {
			System.out.println();
			System.out.println("resultat des autres :" + user.getPseudo());
			System.out.println("pas fini si temps = 0 ;)");
			System.out.println("score :" + user.getScore());
			Duration duree2 = Duration.ofMillis(user.getTempsFin());
			System.out.println("temp passé : " + duree2.getSeconds() + " s");
		}
		try {
			Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("-----client list fin :-----");
		session.sessionList.forEach(sS -> System.out.println("clientlist : " + session.currentUser.getPseudo() + " - " + sS.getPseudo()));

	}
}
