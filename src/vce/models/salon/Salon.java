package vce.models.salon;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import vce.models.data.Questionnaire;
import vce.models.data.SessionUser;
import vce.models.data.User;
import vce.models.session.Session;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Salon extends Session {
	private final Map<String, ConnectionUser> mapSocket = new TreeMap<>();
    private final ObservableList<SessionUser> sessionListServer = FXCollections.observableArrayList();
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
    // pas reellement utile, seul moment d'utilité est pour la creation du questionnaire en interne?
    public int getDurationMax() {
        return this.durationMax;
    }

    public Map<String, ConnectionUser> getMapSocket() {
        return this.mapSocket;
    }

    public ObservableList<SessionUser> getSessionListServer() {
	    return this.sessionListServer;
    }

	public Questionnaire getQuestionnaire() {
		return this.questionnaire;
	}

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    //Setter
    //----------------------------------
    public void setSessionListServer(SessionUser session) {
	    boolean[] found = new boolean[1];
        found[0] = false;

	    //On v�rifie que l'objet n'existe pas d�j�
	    this.sessionListServer.forEach(su -> {
	    	// si l'object existe on le met a jour :
		    if (session.getPseudo().equals(su.getPseudo())) {
		    	su.setScore(su.getScore() == session.getScore() ? su.getScore() : session.getScore());
                su.setStatus(su.getStatut() == session.getStatut() ? su.getStatut() : session.getStatut());
                su.setTempsFin(su.getTempsFin() == session.getTempsFin() ? su.getTempsFin() : session.getTempsFin());
                found[0] = true;
            }
	    });

	    // si l'object n'existe pas on l'ajout simplement :
	    if (!found[0]) {
            Platform.runLater(() -> this.sessionListServer.add(session));
        }

        this.updateSessionUserList(session);
}

	public void setMapSocket(String key, ConnectionUser value) {
		this.mapSocket.put(key, value);
	}

    //Method
    //----------------------------------
    //Demande la génération du questionnaire, l'envoie à tous les clients = Début de la session
    public void startQuestionnaire() {

	    this.questionnaire = new Questionnaire(durationMax);
	    sendAll("QUESTIONNAIRE", null);
	    
	    //Début du test pour le currentUser
	    this.startTest();
    }

    void sendAll(String commande, SessionUser session) {
	        // si on a une sessionUser, on envoi la session à tous les clients
            if (session != null) {
                mapSocket.forEach((key, value) -> {
                    System.out.println("key : " + key);
                    System.out.println("pseudo : " + session.getPseudo());
                    if (!Objects.equals(session.getPseudo(), key)) {
                        System.out.println("envoi de " + session.getPseudo() + " a : " + currentUser.getPseudo());
	                    value.send(commande, session);
                    }
                });
            } else { // sinon il s'agit d'un questionnaire, on envoi la session à tous les clients
	            mapSocket.forEach((key, value) -> value.send(commande));
            }
    }

    //Inner Class
    //----------------------------------
    //Cr�er les connexions avec les clients
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
	                System.err.println("Hôte inconnu : " + e.getMessage());
                } catch (IOException e) {
                    port++;
                }
            }

            try {
                host = InetAddress.getLocalHost().getHostAddress().toString();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        //Method
        //----------------------------------
        // TODO : Gérer la boucle "infinie"
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
                    System.err.println("Erreur de flux ServerCo : " + e.getMessage());
                    isRunning = false;
                }
            }

            try {
                this.server.close();
            } catch (IOException e) {
                System.err.println("Impossible de fermer le server : " + e.getMessage());
            } finally {
            	this.server = null;
            }
        }
    }
}
