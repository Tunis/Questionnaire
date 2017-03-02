package vce.session;

import vce.data.Questionnaire;
import vce.data.SessionUser;
import vce.data.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Session {

    private Socket socket;
    private Out out;
    private List<SessionUser> sessionList = new ArrayList<>();
    private SessionUser currentUser;
    private Questionnaire questionnaire;
    private Timer durationActuel;
    private int durationMax; // semble necessiter un temp en milliseconde !


    public Session(User user, Socket socket) {
        currentUser = new SessionUser(user);
        this.socket = socket;
        new In();
        out = new Out();
    }

    private boolean connectServer(String ip, int port) {
        // TODO: 02/03/2017 a deplacer dans l'ui
        try {
            socket = new Socket(ip, port);
            return true;
        } catch (IOException ignored) {
        }
        return false;
    }

    public void start() {
        // TODO: 02/03/2017 methode pour changer l'affichage de l'ihm du salon au question (l'ihm cree le RepondreQuestionnaire)
        durationActuel.schedule(new TimerTask() {
            @Override
            public void run() {
                stop();
            }
        }, durationMax);
    }

    private void stop() {
        // pas a session de gerer ca finalement, session a pas acces a repondreQuestionnaire pour appeler endQuestionnaire().
    }

    public void send() {
        out.setToSend();
    }

    public Questionnaire getQuestionnaire() {
        return questionnaire;
    }

    public void setStatus(int indexMax) {
        this.currentUser.setStatus(indexMax);
    }

    public void setScore(int score) {
        this.currentUser.setScore(score);
    }

    public void setDurationMax() {
        durationMax = questionnaire.getDurationMax();
    }


    /*

        INNER CLASS :

     */

    private class Out implements Runnable {

        ObjectOutputStream oos;
        boolean toSend;

        public Out() {
            toSend = false;
            new Thread(this).start();
        }

        public void setToSend() {
            this.toSend = true;
        }

        @Override
        public void run() {
            try {
                oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO: 02/03/2017 comme pour in tester la sortie de boucle
            while (true) {
                if (toSend) {
                    try {
                        oos.writeObject(currentUser);
                        oos.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    toSend = false;
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
            try {
                ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO: 02/03/2017 test quoi mettre pour sortir de la boucle
            while (true) {
                try {
                    received = ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                switch (received.getClass().getSimpleName()) {
                    case "Questionnaire":
                        questionnaire = (Questionnaire) received;
                        break;
                    case "SessionUser":
                        break;
                }
                received = null;
            }
        }
    }

}
