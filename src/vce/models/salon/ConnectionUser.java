package vce.models.salon;

import vce.models.data.SessionUser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class ConnectionUser implements Runnable {

	private Socket socketUser;
	private Salon salon;
	//Pour les envois (send)
	private String commandeSend = "";
	private SessionUser sessionSend = null;
	private boolean sendDone = true;
	//Pour le Out et le IN
	private ObjectInputStream ois = null;
	private ObjectOutputStream oos = null;
	private boolean isRunning = true;
	private SessionUser session = null;


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
		// ajout a la map des socket du salon :
		this.salon.setMapSocket(session.getPseudo(), this);

		// pour chaque session du server on l'envoi a la nouvelle connexion :
		salon.getSessionListServer().forEach(su -> send("SESSION", su));
	}

	//Getters
	//----------------------------------

	private SessionUser getSessionSend() {
		return this.sessionSend;
	}

	private String getCommandeSend() {
		return this.commandeSend;
	}

	private boolean getSendDone() {
		return this.sendDone;
	}

	//Methods
	//----------------------------------
	//Commande : CURRENT_USER / QUESTIONNAIRE / SESSION / CLOSE
	public void send(String commande) {
		while (!getSendDone()) {
		}
		this.commandeSend = commande;
		sendDone = false;
	}

	public void send(String commande, SessionUser session) {
		while (!getSendDone()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				salon.launchError("Erreur de Thread", "Erreur lors de l'exécution du Sleep : " + e.getMessage());
			}
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

		@Override
		public void run() {
			try {
				oos = new ObjectOutputStream(socketUser.getOutputStream());
				oos.flush();
			} catch (IOException e) {
				salon.launchError("Erreur de Flux", "Erreur lors de la création du OOS : " + e.getMessage());
				//On gère la fermeture des flux et la fin de boucle
				closeInOut();
			}

			while (isRunning) {
				try {
					//Selon la commande reçus on envoi l'objet correspondant, sinon on ne fait rien
					Thread.sleep(1);
					if (!Objects.equals(getCommandeSend(), "")) {
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
								oos.writeObject(getSessionSend());
								oos.flush();
								oos.reset();
								break;
							default:
								break;
						}
						// envoi effectué on remet en attente :
						commandeSend = "";
						sendDone = true;
					}
				} catch (IOException e) {
					salon.launchError("Erreur de Flux", "Erreur lors de l'envoi de l'objet : " + e.getMessage());
					//On gère la fermeture des flux et la fin de boucle
					closeInOut();
				} catch (InterruptedException e) {
					salon.launchError("Erreur de Thread", "Erreur lors de l'exécution du Sleep : " + e.getMessage());
				}
			}
		}
	}

	//Flux d'entré pour un client
	//Ne re�ois que des objets SessionUser => met à jour la list de SessionUser et la MapSocket.
	class In implements Runnable {
		boolean firstCo = true;

		@Override
		public void run() {
			try {
				ois = new ObjectInputStream(socketUser.getInputStream());
			} catch (IOException e) {
				salon.launchError("Erreur de Flux", "Erreur lors de la création du OIS : " + e.getMessage());
				isRunning = false;
			}

			while (isRunning) {
				try {
					session = (SessionUser) ois.readObject();
					if (firstCo) {
						//Ajoute la socket à la liste
						setSalonMapSocket(session);
						salon.setSessionListServer(session);
						// on envoi la nouvelle session a tout les autres :
						salon.sendAll("SESSION", session);
						firstCo = false;
					} else {
						salon.setSessionListServer(session);
						// on envoi la nouvelle session a tout les autres :
						salon.sendAll("SESSION", session);
					}
				} catch (IOException | ClassNotFoundException e) {
					closeInOut();
				}
			}
		}
	}

	private void closeInOut() {
		//On gère la fermeture des flux et la fin de boucle
		session.setIsDelete(true);
		salon.deleteMapSocket(session);
		salon.deleteSessionListServer(session);
		salon.sendAll("CLOSE", session);

		try {
			socketUser.close();
		} catch (IOException e) {
			salon.launchError("Erreur de Socket", "Impossible de fermer la Socket : " + e.getMessage());
		} finally {
			isRunning = false;
			oos = null;
			ois = null;
			socketUser = null;
		}
	}

	public void serverCloseInOut() {
		//On gère la fermeture des flux et la fin de boucle
		try {
			socketUser.close();
		} catch (IOException e) {
			salon.launchError("Erreur de Socket", "Impossible de fermer la Socket : " + e.getMessage());
		} finally {
			isRunning = false;
			oos = null;
			ois = null;
			socketUser = null;
		}
	}
}
