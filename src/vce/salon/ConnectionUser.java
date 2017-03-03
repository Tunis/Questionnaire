package vce.salon;

import vce.data.SessionUser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionUser implements Runnable {

    private Socket socketUser;
    private Salon salon;
    private String commandeSend = "";
    private SessionUser sessionSend = null;

    //Construct
    //----------------------------------
    public ConnectionUser(Socket client, Salon salon) {
        this.socketUser = client;
        this.salon = salon;

        //Initialisation du Out
        Thread tOut = new Thread(new Out());
        tOut.start();

        //Initialisation du In
        Thread tIn = new Thread(new In());
        tIn.start();
    }

    //Setter
    //----------------------------------
    public void setSalonMapSocket(SessionUser session) {
        synchronized (salon.getMapSocket()) {
            this.salon.setMapSocket(session.getPseudo(), this);
        }
        synchronized (salon.getSessionList()) {
            salon.getSessionList().forEach(su -> send("SESSION", su));
	        salon.sendAll("SESSION", session);
        }
    }

    public SessionUser getSessionSend() {
        synchronized (sessionSend) {
            return this.sessionSend;
        }
    }

    public String getCommandeSend() {
        synchronized (commandeSend) {
            return commandeSend;
        }
    }

    //Method
    //----------------------------------
    //Commande : CURRENT_USER / QUESTIONNAIRE / SESSION / CLOSE
    public void send(String commande) {
        this.commandeSend = commande;
    }

    public void send(String commande, SessionUser session) {
        this.commandeSend = commande;
        this.sessionSend = session;
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

    //Inner Class
    //----------------------------------
    //Flux de sortie pour un client
    class Out implements Runnable {
        ObjectOutputStream oos = null;

        //Construct
        //----------------------------------
        public Out() {
            try {
                this.oos = new ObjectOutputStream(socketUser.getOutputStream());
                this.oos.flush();
            } catch (IOException e) {
                System.err.println("Erreur de flux Out : " + e.getMessage());
            }
        }

        @Override
        public void run() {
            while (!socketUser.isClosed()) {
                try {
                    //Selon la commande re�us on envoi l'objet correspondant, sinon on ne fait rien
                    if (getCommandeSend() != null) {
                        switch (getCommandeSend()) {
                            case "CURRENT_USER":
                                oos.writeObject(salon.getCurrentUser());
                                oos.flush();
                                oos.reset();
                                break;
                            case "SESSION":
                                oos.writeObject(getSessionSend());
                                oos.flush();
                                oos.reset();
                                break;
                            case "QUESTIONNAIRE":
                                oos.writeObject(salon.getQuestionnaire());
                                oos.flush();
                                oos.reset();
                                break;
                            case "CLOSE":
                                oos.close();
                                oos = null;
                                break;
                            default:
                                break;
                        }
                        commandeSend = "";
                    }
                } catch (IOException e) {
                    System.err.println("Erreur de flux Out Run : " + e.getMessage());
                }
            }
        }
    }

    //Flux d'entr� pour un client
    //Ne re�ois que des objets SessionUser => met � jour la list de SessionUser et la MapSocket.
    class In implements Runnable {
        ObjectInputStream ois = null;
        SessionUser session = null;
        boolean firstCo = true;

        //Construct
        //----------------------------------
        public In() {

        }

        @Override
        public void run() {
            try {
                ois = new ObjectInputStream(socketUser.getInputStream());
            } catch (IOException e) {
                System.err.println("Erreur de flux In : " + e.getMessage());
            }

            while (!socketUser.isClosed()) {
                try {
                    session = (SessionUser) ois.readObject();

	                if (firstCo) {
		                setSalonMapSocket(session);
		                firstCo = false;
	                } else {
		                salon.sendAll("SESSION", session);
	                }
                    salon.setSessionList(session);

                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Erreur de flux In Run : " + e.getMessage());
                }
            }
        }
    }
}
