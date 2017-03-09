package vce.models.session;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import vce.models.data.Questionnaire;
import vce.models.data.SessionUser;
import vce.models.data.User;
import vce.models.salon.Salon;
import vce.vues.controllers.RootCtrl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Duration;

public class Session {

    private Salon salon = null;
    private Socket socket = null;
    private Out out;
    protected final ObservableList<SessionUser> sessionList = FXCollections.observableArrayList();
    protected final SessionUser currentUser;
	protected Questionnaire questionnaire;
	protected RepondreQuestionnaire avancement;

    protected RootCtrl rootCtrl;
    private boolean sendDone = true;
    private boolean toSend = false;
    
    //Pour le Out et le IN
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    private boolean isRunning = true;

        
    //Construct
    //----------------------------------
    //A besoin de l'user du client et de la socket de connection au server recuperer par connectServer()
    public Session(User user, Socket socket, RootCtrl rootCtrl) {
        this.rootCtrl = rootCtrl;
        currentUser = new SessionUser(user);
        this.socket = socket;
        new In();
        out = new Out();
        send();
    }

    //Constructeur pour le salon
    protected Session(User user, RootCtrl rootCtrl) {
        this.rootCtrl = rootCtrl;
        this.currentUser = new SessionUser(user);
    }
    
    //Getter
    //----------------------------------
    private boolean getToSend() {return toSend;}
    private boolean getSendDone() {return this.sendDone;}
	public Questionnaire getQuestionnaire() {return questionnaire;}
	public Socket getSocket() {return socket;}
    
    //Setter
    //----------------------------------
    // set le status du current user :
 	public void setStatus(int indexMax) {
 		this.currentUser.setStatus(indexMax);
 	}

 	// set le score du current user :
     public void setScore(int score) {
         this.currentUser.setScore(score);
     }

 	// set le temps total du test du current user (mis a jour a la fin du test)
 	public void setTime(Duration time) {
 		this.currentUser.setTempsFin(time);
     }
 	
 	//Méthodes
    //----------------------------------
 	//Tentative de connection au serveur
	public static Socket connectServer(String ip, int port) {
		Socket socket = null;
		try {
			socket = new Socket(ip, port);
			return socket;
		} catch (IOException ignored) {
		}
		return null;
	}
	
	public void launchError(String handler, String message){
        Platform.runLater(() -> this.rootCtrl.error(handler, message));
    }

    //Methode lançant et stoppant le test
    //----------------------------------
	// lance le test, cree les objects necessaire :
    protected void startTest() {
        // cree un repondreQuestionnaire
        avancement = new RepondreQuestionnaire(this);
        rootCtrl.goToQuestionnaire();
    }

	// stop le test, supprime les object inutile :
	public void stopTest() {
		sessionList.add(currentUser);
		avancement = null;
	}

    /*
        on precise quand envoyé le current user (seul envoi effectué par le client)
     */

    public void send() {
        while (!getSendDone()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        toSend = true;
        sendDone = false;
    }


    // Permet de mettre à jour la liste User lorsqu'un client ce déconnecte
    protected void deleteSessionList(SessionUser user){
    	int[] compteur = new int[1];
    	compteur[0] = 0;
    	boolean[] found = new boolean[1];
        found[0] = false;
    	
    	this.sessionList.forEach(s -> {
    		if(!user.getPseudo().equals(s.getPseudo()) && found[0] == false){
    			compteur[0]++;
    		} else {
    			found[0] = true;
    		}
    	});
        System.out.println("a ete trouvé : " + found[0]);
        System.out.println("a l'id : " + compteur[0]);

        Platform.runLater(() -> {
            sessionList.remove(compteur[0]);
            rootCtrl.refreshList();
        });
    }
    
    /*
        mise a jour des autre user lors de la reception d'un sessionUser par le server :
     */
	protected void updateSessionUserList(SessionUser user) {
        boolean[] found = new boolean[1];
        found[0] = false;
        
        if (!currentUser.getPseudo().equals(user.getPseudo())) {
        	System.out.println("----------------------------------------------------------");
            System.err.println(currentUser.getPseudo() + " : Serveur à envoyé => " + user.getPseudo());
            System.out.println("----------------------------------------------------------");
            // si trouver dans la liste on modifie les valeur actuel
            sessionList.forEach(s -> {
                if (s.getPseudo().equals(user.getPseudo())) {
                	System.err.println(currentUser.getPseudo() + " : modif list : " + user.getPseudo());
                    Platform.runLater(() -> s.setScore(s.getScore() == user.getScore() ? s.getScore() : user.getScore()));
                    Platform.runLater(() -> s.setStatus(s.getStatus() == user.getStatus() ? s.getStatus() : user.getStatus()));
                    Platform.runLater(() -> s.setTempsFin(s.getTempsFin() == user.getTempsFin() ? s.getTempsFin() : user.getTempsFin()));
                    found[0] = true;
                }
            });
            if (!user.getTempsFin().isZero()) {
                System.out.println("c'est fini");
            }
            
            
            // si on l'as pas trouver avant on l'ajoute.
            if (!found[0]) {
            	System.err.println(currentUser.getPseudo() + " : AddList : " + user.getPseudo());
                Platform.runLater(() -> sessionList.add(user));
            }
            Platform.runLater(() -> rootCtrl.refreshList());
        }
    }

    /*
        mise a jour du current user :
     */

	// retourne l'object gerant l'avancement du questionnaire :

	public RepondreQuestionnaire getAvancement() {
		return avancement;
	}

	// recupere le current user :

    public SessionUser getCurrentUser() {
        return currentUser;
    }

	

    public ObservableList<SessionUser> getSessionList() {
            return sessionList;
    }

    


    //Inner Class
    //----------------------------------
    private class Out implements Runnable {
        public Out() {
	        new Thread(this).start();
        }
        
        //public void setToSend() {this.toSend = true;}
        //private boolean getToSend() {return toSend;}

        @Override
        public void run() {
            try {
                oos = new ObjectOutputStream((socket.getOutputStream()));
                oos.flush();
            } catch (IOException e) {
                launchError("Erreur de Flux", "Erreur lors de la création du OOS : " + e.getMessage());
            }
            
            while (isRunning) {
                try {
                    Thread.sleep(1);
                    if (getToSend()) {
                        try {
                            System.out.println("------------------------------");
                            System.out.println("- Client : envoi currentUser -");
                            System.out.println("------------------------------");
                            oos.writeObject(getCurrentUser());
                            oos.flush();
                            oos.reset();
                            toSend = false;
                            sendDone = true;
                            System.out.println("tosend = " + toSend);
                            System.out.println("sendDone = " + sendDone);
                        } catch (IOException e) {
                        	launchError("Erreur de Flux", "Erreur lors de l'envoi de l'objet : " + e.getMessage());
                        	closeInOut();
                        }
                    }
                } catch (InterruptedException e) {
                	launchError("Erreur de Thread", "Erreur lors de l'exécution du Sleep : " + e.getMessage());
                }
            }
        }
    }

    private class In implements Runnable {
        Object received;

        public In() {
            new Thread(this).start();
        }

        @Override
        public void run() {
            // on cree l'imput stream au debut du thread
            try {
                ois = new ObjectInputStream((socket.getInputStream()));
            } catch (IOException e) {
            	launchError("Erreur de Flux", "Erreur lors de la création du OIS : " + e.getMessage());
            }
            
            while (isRunning) {
                // switch sur la classe recu, puis lancement d'un thread pour traiter l'info recu.
                try {
                    received = ois.readObject();
                    System.out.println("----------------------------------------------------------");
                    System.out.println("Class Reçus : " + received.getClass().getSimpleName());
                    switch (received.getClass().getSimpleName()) {
                        case "Questionnaire":
                            questionnaire = (Questionnaire) received;

                            startTest();
                            break;
                        case "SessionUser":
                            SessionUser user = (SessionUser) received;
	                        new Thread(() -> {
	                        	System.out.println("----------------------------------------------------------");
		                        System.err.println(currentUser.getPseudo() + " à reçu : " + user.getPseudo());
		                        
		                        //On vérifie si il s'agit d'une suppression ou une modification
                                if (user.isDelete()) {
                                    deleteSessionList(user);
                                } else {
		                        	updateSessionUserList(user);
		                        }
	                        }).start();
	                        break;
                    }
                } catch (IOException | ClassNotFoundException e) {
	            	closeInOut();
	            }
                
                received = null;
            }
        }
    }
    
    public void closeInOut(){
    	//On gère la fermeture des flux, la fin de boucle et la remise à zero des objets.
    	try {
			socket.close();
		} catch (IOException e) {
			launchError("Erreur de Socket", "Impossible de fermer la Socket : " + e.getMessage());
		} finally {
			questionnaire = null;
    		avancement = null;
    		sessionList.clear();
			oos = null;
			ois = null;
			socket = null;
			isRunning = false;
            Platform.runLater(() -> rootCtrl.goToJoinSalon());
        }
    }
}
