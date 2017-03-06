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
    // a besoin du socket et du salon :
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

	//Setters
	//----------------------------------
	private void setSalonMapSocket(SessionUser session) {
		synchronized (salon.getMapSocket()) {
	        // ajout a la map des socket du salon :
			this.salon.setMapSocket(session.getPseudo(), this);
        }
		synchronized (salon.getSessionListServer()) {
			// pour chaque session du server on l'envoi a la nouvelle connexion :
			salon.getSessionListServer().forEach(su -> send("SESSION", su));
			// on envoi la nouvelle session a tout les autres :
			salon.sendAll("SESSION", session);
        }
    }

	//Getters
	//----------------------------------

	private SessionUser getSessionSend() {
		synchronized (sessionSend) {
            return this.sessionSend;
        }
    }

	private String getCommandeSend() {
		synchronized (commandeSend) {
            return commandeSend;
        }
    }

	//Methods
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
	                //Selon la commande reçus on envoi l'objet correspondant, sinon on ne fait rien
	                if (getCommandeSend() != null) {
                        switch (getCommandeSend()) {
	                        // mise a jour de la session du server :
	                        case "CURRENT_USER":
                                oos.writeObject(salon.getCurrentUser());
                                oos.flush();
                                oos.reset();
                                break;
	                        // mise a jour de la session reçu par le server :
	                        case "SESSION":
                                oos.writeObject(getSessionSend());
                                oos.flush();
                                oos.reset();
                                break;
	                        // envoi du questionnaire pour demarer le test :
	                        case "QUESTIONNAIRE":
                                oos.writeObject(salon.getQuestionnaire());
                                oos.flush();
                                oos.reset();
                                break;
	                        // le client a couper la connection :
	                        case "CLOSE":
                                oos.close();
                                oos = null;
                                break;
                            default:
                                break;
                        }
	                    // envoi effectué on remet en attente :
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
	                // premier envoi de l'user :
	                if (firstCo) {
		                // on l'ajoute a la liste des socket avec sont pseudo :
		                new Thread(()->{
		                    setSalonMapSocket(session);
                            send("SESSION", salon.getCurrentUser());
                        }).start();

		                firstCo = false;
	                }
	                // on met a jour la liste du server :
                    new Thread(()->{
	                    salon.sendAll("SESSION", session);
                        salon.setSessionListServer(session);
                    }).start();

                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Erreur de flux In Run : " + e.getMessage());
                }
            }
        }
    }
}
