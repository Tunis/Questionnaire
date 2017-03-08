package vce.models.salon;

import vce.models.data.SessionUser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Objects;

public class ConnectionUser implements Runnable {

    private Socket socketUser;
    private Salon salon;
    //Pour les envois (send)
    private String commandeSend = "";
    private SessionUser sessionSend = null;
	private boolean sendDone = true;
	//Pour le Out et le IN
    ObjectInputStream ois = null;
    ObjectOutputStream oos = null;
    boolean isRunning = true;


    //
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

			System.out.println("Envoi de la session : " + su.getPseudo() + " vers " + session.getPseudo());
			send("SESSION", su);

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
        while (!sendDone) {
        }
        this.commandeSend = commande;
        sendDone = false;
    }

    public void send(String commande, SessionUser session) {
        while (!sendDone) {
        }
        this.commandeSend = commande;
        this.sessionSend = session;
        sendDone = false;
    }


    @Override
    public void run() {
    }

    //Inner Class
    //----------------------------------
    //Flux de sortie pour un client
    class Out implements Runnable {
    	
        //Construct
        //----------------------------------
        public Out() {
            
        }

        @Override
        public void run() {
        	try {
                oos = new ObjectOutputStream(socketUser.getOutputStream());
                oos.flush();
            } catch (IOException e) {
                System.err.println("Erreur de flux Out : " + e.getMessage());
                isRunning = false;
            }
        	
            while (isRunning) {
                try {
	                //Selon la commande reçus on envoi l'objet correspondant, sinon on ne fait rien
	                if (!Objects.equals(getCommandeSend(), "")) {
		                System.out.println("Commande : " + getCommandeSend() + " valeur session : " + getSessionSend().getPseudo());
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
                    isRunning = false;
                }
                
                //On gère la fermeture des flux et la fin de boucle
                allClose();
            }
        }
    }

    //Flux d'entré pour un client
    //Ne re�ois que des objets SessionUser => met à jour la list de SessionUser et la MapSocket.
    class In implements Runnable {
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
                System.err.println("Erreur de flux dans le In : " + e.getMessage());
                isRunning = false;
            }

            while (isRunning) {
                try {
                    session = (SessionUser) ois.readObject();
                    System.out.println("----------------------------------------------------------");
                    System.err.println(salon.getCurrentUser().getPseudo() + " à reçu : " + session.getPseudo());
	                if (firstCo) {
                		System.out.println("----------------------------------------------------------");
                        System.out.println("Premier envoi de : " + session.getPseudo());
                        //Ajoute la socket à la liste
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
                        //On met à jour la liste du serveur et on envoi la nouvelle session aux autres clients
                        salon.sendAll("SESSION", session);
                        System.out.println("----------------------------------------------------------");
                		System.out.println("Maj SessionServerList");
                        salon.setSessionListServer(session);
                    }
                } catch (SocketException e) {
                	if(e.getMessage().equalsIgnoreCase("Connection reset")){
                		System.err.println("Fermeture non prévue du client");
                	} else {
                		System.err.println("Erreur Socket : " + e.getMessage());
                	}
                	
                	isRunning = false;
                } catch (IOException e) {
                    System.err.println("Erreur de flux dans le In : " + e.getMessage());
                    isRunning = false;
                } catch (ClassNotFoundException e) {
                	System.err.println("Erreur Objet OIS : " + e.getMessage());
                	isRunning = false;
				}
                
                //On gère la fermeture des flux et la fin de boucle
                allClose();
            }
        }
    }
    
    private void allClose(){
    	//On gère la fermeture des flux et la fin de boucle
        if(!isRunning){
        	try {
        		oos.close();
				ois.close();
				socketUser.close();
			} catch (IOException e) {
				System.err.println("Erreur lors de la fermeture de la Socket : " + e.getMessage());
			} finally {
				oos = null;
				ois = null;
				socketUser = null;
			}
        }
    }
}
