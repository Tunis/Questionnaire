package vce.session;

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


public class Session {

    private Socket socket;
    private Out out;
    private List<SessionUser> sessionList = new ArrayList<>();
    private SessionUser currentUser;
    private Questionnaire questionnaire;

    private RepondreQuestionnaire avancement;


    public Session(User user, Socket socket) {
        currentUser = new SessionUser(user);
        this.socket = socket;
        new In();
        out = new Out();
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
        signal de depart du test :
     */

    public static void main(String[] args) throws IOException {
        Salon[] salon = new Salon[1];
        new Thread(() -> {
            salon[0] = new Salon(new User(9, "salon", "salon", "salon"), 1000);
        }).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new Thread(() -> client(salon[0], 1)).start();
        new Thread(() -> client(salon[0], 2)).start();
        new Thread(() -> client(salon[0], 3)).start();
        new Thread(() -> client(salon[0], 4)).start();
        new Thread(() -> client(salon[0], 5)).start();

    }

    /*
        on precise quand envoyé le current user (seul envoi effectué par le client)
     */

    public static void client(Salon salon, int id) {
        System.out.println("debut client" + id);
        Socket s;
        do {
            s = Session.connectServer("localhost", 30000);
        } while (s == null);
        Session session = new Session(new User(1, "fred", "fred", "fred" + id), s);
        session.send();


        salon.startQuestionnaire();

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        int i = 0;
        while (session.avancement != null && i <= 7) {
            i++;
            session.avancement.nextQuestion();
            if (i == 8) session.avancement.endQuestionnaire();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println();
        System.out.println("client resultat :");
        System.out.println("score :" + session.currentUser.getScore());
        Duration duree = Duration.ofMillis(session.currentUser.getTempsFin());
        System.out.println("temp passé : " + duree.getSeconds() + " s");

        System.out.println();
        System.out.println("server a recu :");
        System.out.println(salon.getSessionList().get(0).getPseudo());
        System.out.println(salon.getSessionList().get(0).getStatut());
        System.out.println(salon.getSessionList().get(0).getScore());
        Duration duree2 = Duration.ofMillis(salon.getSessionList().get(0).getTempsFin());
        System.out.println("temp passé : " + duree2.getSeconds() + " s");

        System.out.println("-----salon list :-----");
        salon.getSessionList().forEach(sS -> System.out.println(sS.getPseudo()));

        System.out.println("-----client list :-----");
        System.out.println("client : " + session.currentUser.getPseudo());
        session.sessionList.forEach(sS -> System.out.println(sS.getPseudo()));

    }

    /*
        mise a jour des autre user :
     */

    public void startTest() {
        // cree un repondreQuestionnaire
        System.out.println("on lance le test");
        avancement = new RepondreQuestionnaire(this);
    }

    /*
        mise a jour du current user :
     */

    public void send() {
        out.setToSend();
    }

    private synchronized void updateSessionUserList(SessionUser user) {
        boolean[] found = new boolean[1];
        found[0] = false;

        System.out.println("serveur a envoyé : ");
        System.out.println(user.getPseudo());

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

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setStatus(int indexMax) {
        this.currentUser.setStatus(indexMax);
    }


    /*

        INNER CLASS :

     */

    public void setScore(int score) {
        this.currentUser.setScore(score);
    }

    public void setTime(long time) {
        this.currentUser.setTempsFin(time);
    }

    public void stopTest() {
        avancement = null;
    }

    public RepondreQuestionnaire getAvancement() {
        return avancement;
    }

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
                            new Thread(Session.this::startTest).start();
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
}
