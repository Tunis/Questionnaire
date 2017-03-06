package vce.salon;

import vce.data.SessionUser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class ConnectionUser implements Runnable {

    private Socket socketUser;
    private Salon salon;
    private String commandeSend = "";
    private SessionUser sessionSend = null;
	private boolean sendDone = false;

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
	        // ajout a la map des socket du salon :
			this.salon.setMapSocket(session.getPseudo(), this);

			// pour chaque session du server on l'envoi a la nouvelle connexion :
		salon.getSessionListServer().forEach(su -> {
			System.out.println("send session " + su.getPseudo() + " to " + session.getPseudo());
			send("SESSION", su);
		});
			// on envoi la nouvelle session a tout les autres :
		System.out.println("puis appel a sendAll avec en param : " + session.getPseudo());
		salon.sendAll("SESSION", session);
		salon.setSessionListServer(session);

    }

	//Getters
	//----------------------------------

	private SessionUser getSessionSend() {
		return this.sessionSend;
	}

	private String getCommandeSend() {
		return commandeSend;
	}

	//Methods
	//----------------------------------
    //Commande : CURRENT_USER / QUESTIONNAIRE / SESSION / CLOSE
    public void send(String commande) {
	    this.commandeSend = commande;
	    while (!sendDone) {
	    }
    }

    public void send(String commande, SessionUser session) {
            this.commandeSend = commande;
            this.sessionSend = session;
	    while (!sendDone) {
	    }
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
	                if (!Objects.equals(getCommandeSend(), "")) {
		                sendDone = false;
		                System.out.println("commande : " + getCommandeSend() + " valeur session : " + getSessionSend());
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
		                sendDone = true;
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
                            System.out.println("premiere connextion envoi :");
                            //Ajoute la socket � la liste
                            setSalonMapSocket(session);

		                firstCo = false;
                    } else {
                        //On met � jour la liste du serveur et on envoi la nouvelle session aux autre clients
                            salon.sendAll("SESSION", session);
                            salon.setSessionListServer(session);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Erreur de flux In Run : " + e.getMessage());
                }
            }
        }
    }
}
