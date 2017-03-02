package vce.session;

import vce.data.Questionnaire;
import vce.data.SessionUser;
import vce.data.User;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class Session implements Runnable {

    private Socket socket;
    private Out out;
    private List<SessionUser> sessionList = new ArrayList<>();
    private SessionUser currentUser;
    private Questionnaire questionnaire;
    private Timer durationActuel;
    private int durationMax;


    public Session(User user, Socket socket) {
        currentUser = new SessionUser(user);
        this.socket = socket;
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

    @Override
    public void run() {
        new In();
        out = new Out();

        // TODO: 02/03/2017 gné
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
