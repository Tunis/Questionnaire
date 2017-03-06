package vce.salon;

import vce.data.Questionnaire;
import vce.data.SessionUser;
import vce.data.User;
import vce.session.Session;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Salon extends Session {
	private final Map<String, ConnectionUser> mapSocket = new TreeMap<>();
	private final List<SessionUser> sessionListServer = new ArrayList<>();
	private int durationMax;
    private Thread t = null;
    private String host;
    private int port = 30000;

    //Construct
    //----------------------------------
    public Salon(User user, int duration) {
	    // on initialise le currentUser de session ;)
	    super(user);
	    this.setSessionListServer(this.currentUser);
	    this.durationMax = duration;

	    // creation du thread gerant les connexion entrante :
	    t = new Thread(new ServerCo(this));
        t.start();
    }

    //Getter
    //----------------------------------
    // pas reellement utile, seul moment d'utilitÃ© est pour la creation du questionnaire en interne?
    public int getDurationMax() {
        return this.durationMax;
    }

    public Map<String, ConnectionUser> getMapSocket() {
        return this.mapSocket;
    }

	public List<SessionUser> getSessionListServer() {
		return this.sessionListServer;
	}

	public Questionnaire getQuestionnaire() {
		return this.questionnaire;
	}

    public String getHost() {
        System.out.println("host retourner :");
        return this.host;
    }

    public int getPort() {
        System.out.println("port retourner : " + port);
        return this.port;
    }

    //Setter
    //----------------------------------
    public void setSessionListServer(SessionUser session) {
	    synchronized (sessionListServer) {
		    String pseudo;
		    boolean modifier = false;
		    ListIterator<SessionUser> itSS = this.sessionListServer.listIterator();

		    //On vï¿½rifie que l'objet n'existe pas dï¿½jï¿½
		    while (itSS.hasNext()) {
                pseudo = itSS.next().getPseudo();

	            // si l'object existe on le met a jour :
			    if (session.getPseudo().equals(pseudo)) {
			    	itSS.set(session);
                    modifier = true;
                }
            }

		    // si l'object n'existe pas on l'ajout simplement :
		    if (!modifier) {
	            this.sessionListServer.add(session);
		    }

            this.updateSessionUserList(session);
        }
    }

	public void setMapSocket(String key, ConnectionUser value) {
		synchronized (mapSocket) {
			this.mapSocket.put(key, value);
		}
	}

    //Method
    //----------------------------------
    //Demande la gï¿½nï¿½ration du questionnaire, l'envoie à tous les clients = Début de la session
    public void startQuestionnaire() {

	    this.questionnaire = new Questionnaire(durationMax);
	    sendAll("QUESTIONNAIRE", null);
	    
	    //Dï¿½but du test pour le currentUser
	    this.startTest();
    }

    void sendAll(String commande, SessionUser session) {
        synchronized (mapSocket) {
	        // si on a une sessionUser, on envoi la session à tous les clients
	        if (session != null) {
                mapSocket.forEach((key, value) -> {
                    if (!Objects.equals(session.getPseudo(), key)) {
                        value.send(commande, session);
                    }
                });
            } else { // sinon il s'agit d'un questionnaire, on envoi la session à tous les clients
	            mapSocket.forEach((key, value) -> value.send(commande));
	        }
        }
    }

    //Inner Class
    //----------------------------------
    //Créer les connexions avec les clients
    class ServerCo implements Runnable {
        private Salon salon;
        private ServerSocket server;
        private boolean isRunning = true;

        //Construct
        //----------------------------------
        public ServerCo(Salon salon) {
            this.salon = salon;

            while (port <= 65535) {
                try {
                    this.server = new ServerSocket(port);
                    break;
                } catch (UnknownHostException e) {
	                System.err.println("HÃ´te inconnu : " + e.getMessage());
                } catch (IOException e) {
                    //System.err.println("Erreur de flux : " + e.getMessage() + "\n port :" + this.port);
                    port++;
                }
            }

            //host = this.server.getLocalSocketAddress().toString();
            try {
                host = InetAddress.getLocalHost().getHostAddress().toString();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        //Method
        //----------------------------------
        // TODO : GÃ©rer la boucle "infinie"
        public void run() {
	        // on change isRunning lorsque tous les utilisateur on fini le test et que le server leur a envoyer tous les status.
	        while (isRunning) {
		        try {
                    //On attend une connexion d'un client
                    Socket client = this.server.accept();
                    //Ouverture d'un thread pour traiter le client, puis on attend de nouveau les connexion
                    Thread t = new Thread(new ConnectionUser(client, this.salon));
                    t.start();
                } catch (IOException e) {
                    System.err.println("Erreur de flux : " + e.getMessage());
                }
            }

            try {
                this.server.close();
            } catch (IOException e) {
                System.err.println("Impossible de fermer le server : " + e.getMessage());
                this.server = null;
            }
        }
    }
}
