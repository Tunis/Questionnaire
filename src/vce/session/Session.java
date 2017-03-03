package vce.session;

import vce.data.Question;
import vce.data.Questionnaire;
import vce.data.SessionUser;
import vce.data.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
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



    /*
        on precise quand envoyé le current user (seul envoi effectué par le client)
     */

    public static void main(String[] args) throws IOException {
        new Thread(() -> {
            try {
                serv();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();

        Socket s;
        do {
            s = Session.connectServer("localhost", 1234);
            System.out.println("client socket : " + s);
        } while (s == null);
        Session session = new Session(new User(1, "fred", "fred", "fred"), s);
        session.send();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (session.avancement == null) {
        }

        session.sessionList.forEach(se -> System.out.println("status user " + se.getPseudo() + " : " + se.getStatut()));
        Question q;
        int i = 0;
        while (session.avancement != null && i <= 7) {
            i++;
            q = session.avancement.nextQuestion();
            System.out.println("status : " + session.currentUser.getStatut());
            System.out.println(q.getIdQuestion());
            System.out.println((session.avancement.getReponse() != null) ? session.avancement.getReponse().isCorrect() : "pas de reponse");
            if (i == 8) session.avancement.endQuestionnaire();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("resultat test :");
        System.out.println("score :" + session.currentUser.getScore());
        Duration duree = Duration.ofMillis(session.currentUser.getTempsFin());
        System.out.println("temp passé : " + duree.getSeconds() + " s");
    }

    /*
        mise a jour du current user :
     */

    public static void serv() throws IOException, ClassNotFoundException {
        ServerSocket ss = new ServerSocket(1234);
        Socket s = ss.accept();
        ObjectInputStream ois = new ObjectInputStream((s.getInputStream()));
        SessionUser user = (SessionUser) ois.readObject();

        System.out.println("serv recoit : " + user);
        System.out.println("de : " + user.getPseudo());
        System.out.println("status : " + user.getStatut());

        ObjectOutputStream oos = new ObjectOutputStream((s.getOutputStream()));

        // ajout de nouveau user :
        SessionUser s1 = new SessionUser(new User(2, "test1", "test1", "test1"));
        SessionUser s3 = new SessionUser(new User(2, "test3", "test3", "test3"));
        SessionUser s2 = new SessionUser(new User(2, "test2", "test2", "test2"));
        oos.writeObject(s1);
        oos.flush();
        oos.reset();
        oos.writeObject(s2);
        oos.flush();
        oos.reset();
        oos.writeObject(s3);
        oos.flush();
        oos.reset();


        s3.setStatus(10);

        oos.writeObject(s3);
        oos.flush();
        oos.reset();


        user = null;
        System.out.println("essai envoi questionnaire :");
        oos.writeObject(new Questionnaire(1000));
        oos.flush();
        System.out.println("envoi effectué");
        while (true) {
            System.out.println("boucle reception server");
            user = (SessionUser) ois.readObject();
            System.out.println("server a recu quelquechose");
            System.out.println("serv recoit : " + user);
            System.out.println("de : " + user.getPseudo());
            System.out.println("status : " + user.getStatut());
        }
    }

    public void startTest() {
        // cree un repondreQuestionnaire
        avancement = new RepondreQuestionnaire(this);
    }

    public void send() {
        out.setToSend();
    }

    /*
        mise a jour des autre user :
     */

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

    private synchronized void updateSessionUserList(SessionUser user) {
        boolean[] found = new boolean[1];
        found[0] = false;

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

    public void stopTest() {
        avancement = null;
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
