package vce.salon;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import vce.data.SessionUser;

public class ConnectionUser implements Runnable {

	private Lock verrou = new ReentrantLock();
    private Socket socketUser;
    private Salon salon;
    private String commandeSend = "";
    private SessionUser sessionSend = null;
	private boolean sendDone = true;

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
		System.out.println("----------------------------------------------------------");
		System.out.println("Met à jour la list Socket");
        // ajout a la map des socket du salon :
		this.salon.setMapSocket(session.getPseudo(), this);
		
		System.out.println("Nombre de Session dans ListServer : " + salon.getSessionListServer().size());
		// pour chaque session du server on l'envoi a la nouvelle connexion :
		salon.getSessionListServer().forEach(su -> {
			while(!sendDone){}
			System.err.println("Envoi de la session : " + su.getPseudo() + " vers " + session.getPseudo());
			send("SESSION", su);
			sendDone = false;
		});
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
	                if (!Objects.equals(getCommandeSend(), "")) {
		                System.out.println("----------------------------------------------------------");
		                System.err.println("Commande envoyée : " + getCommandeSend() + " valeur session : " + getSessionSend().getPseudo());
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
                    System.out.println("----------------------------------------------------------");
                    System.err.println(salon.getCurrentUser().getPseudo() + " à reçu : " + session.getPseudo());
	                if (firstCo) {
                		System.out.println("----------------------------------------------------------");
                        System.err.println("Premier envoi de : " + session.getPseudo());
                        //Ajoute la socket � la liste
                        setSalonMapSocket(session);
                        // on envoi la nouvelle session a tout les autres :
                		System.out.println("----------------------------------------------------------");
                		System.out.println("Envoi à tous les clients de la nouvelle session : " + session.getPseudo());
                		salon.sendAll("SESSION", session);
                		salon.setSessionListServer(session);
		                firstCo = false;
                    } else {
                    	System.out.println("----------------------------------------------------------");
                		System.out.println("Envoi à tous les clients de la nouvelle session : " + session.getPseudo());
                        //On met � jour la liste du serveur et on envoi la nouvelle session aux autre clients
                        salon.sendAll("SESSION", session);
                        System.out.println("----------------------------------------------------------");
                		System.out.println("Maj SessionServerList");
                        salon.setSessionListServer(session);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Erreur de flux In Run : " + e.getMessage());
                }
            }
        }
    }
}
