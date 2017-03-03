package vce.salon;

import vce.data.Questionnaire;
import vce.data.SessionUser;
import vce.data.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

//TODO heritage session
public class Salon {
    private SessionUser currentUser;
    private Map<String, ConnectionUser> mapSocket = new TreeMap<String, ConnectionUser>();
    private List<SessionUser> sessionList = new ArrayList<SessionUser>();
    private int durationMax;
    private Questionnaire questionnaire;

    //TestUnit
    private Thread t = null;

    //Construct
    //----------------------------------
    public Salon(User user, int duration) {
        this.durationMax = duration;

        System.out.println("Initialisation ServerCo"); //TODO : A suppr
        t = new Thread(new ServerCo(this));
        t.start();
    }

    //Getter
    //----------------------------------
    public int getDurationMax() {
        return this.durationMax;
    }

    public SessionUser getCurrentUser() {
        return this.currentUser;
    }

    public Map<String, ConnectionUser> getMapSocket() {
        return this.mapSocket;
    }

    public List<SessionUser> getSessionList() {
        return this.sessionList;
    }

    //Setter
    //----------------------------------
    public void setSessionList(SessionUser session) {
        synchronized (sessionList) {
            String pseudo = "";
            boolean modifier = false;
            ListIterator<SessionUser> it = this.sessionList.listIterator();

            //On v�rifie que l'objet n'existe pas d�j�
            while (it.hasNext()) {
                pseudo = it.next().getPseudo();

                if (session.getPseudo().equals(pseudo)) {
                    it.set(session);
                    modifier = true;
                }
            }

            if (!modifier) {
                this.sessionList.add(session);
            }
        }
        //Envois la nouvel/maj SessionUser � tous les clients
        sendAll("SESSION", session);
    }

    public Questionnaire getQuestionnaire() {
        return this.questionnaire;
    }

    public Thread getT() {
        return this.t;
    }

    public synchronized void setMapSocket(String key, ConnectionUser value) {
        this.mapSocket.put(key, value);
    }

    //Method
    //----------------------------------
    //Demande la g�n�ration du questionnaire, l'envoie � tous les clients = D�but de la session
    public void startQuestionnaire() {

        this.questionnaire = new Questionnaire(100000); //TODO avec BDD
        sendAll("QUESTIONNAIRE", null);

        //TODO TRAITEMENT SESSION
    }

    void sendAll(String commande, SessionUser session) {
        synchronized (mapSocket) {
            //On envoi pour chaque clients
            if (session != null) {
                mapSocket.forEach((key, value) -> {
                    if (!Objects.equals(session.getPseudo(), key)) {
                        value.send(commande, session);
                    }
                });
            } else {
                mapSocket.forEach((key, value) -> {
                    value.send(commande);
                });
            }
        }
    }

    //Inner Class
    //----------------------------------
    //Cr�er les connexions avec les clients
    class ServerCo implements Runnable {
        private Salon salon;
        private int port = 30000;
        private ServerSocket server;
        private boolean isRunning = true;

        //Construct
        //----------------------------------
        public ServerCo(Salon salon) {
            this.salon = salon;

            while (this.port <= 65535) {
                try {
                    this.server = new ServerSocket(this.port, 100, InetAddress.getByName("127.0.0.1")); // TODO A modifier juste avec le port
                    System.out.println("Lancement du server sur le port : " + this.port); //TODO : A suppr
                    this.port = 65536;
                } catch (UnknownHostException e) {
                    System.err.println("H�te inconnu : " + e.getMessage());
                } catch (IOException e) {
                    //System.err.println("Erreur de flux : " + e.getMessage() + "\n port :" + this.port);
                    this.port++;
                }
            }
        }

        //Method
        //----------------------------------
        // TODO : G�rer la boucle "infinie"
        public void run() {
            while (isRunning == true) {
                try {
                    //On attend une connexion d'un client
                    Socket client = this.server.accept();
                    System.out.println("Connexion Client !"); //TODO : A suppr
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
