package vce.vues.controllers.salon;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import vce.models.session.Session;
import vce.vues.controllers.RootCtrl;

import java.net.Socket;

public class JoinSalonCtrl {


    public TextField champIp;
    public TextField champPort;
    public TextField champDuree;
    private RootCtrl rootCtrl;

    public void createSalon(ActionEvent actionEvent) {
        // TODO: 09/03/2017 a verifier
        if (!champDuree.getText().isEmpty()) {
            try {
                int duree = Integer.parseInt(champDuree.getText());
                if (duree > 0) {
                    rootCtrl.createSalon(duree);
                } else {
                    rootCtrl.error("Durée incorrecte", "merci de mettre au moins 1 minute de durée");
                }
            } catch (NumberFormatException ignored) {
                rootCtrl.error("erreur de saisie", "merci de rentrez un nombre");
            }
        }
    }

    public void joinSalon(ActionEvent actionEvent) {
        Socket socket = Session.connectServer(champIp.getText(), Integer.parseInt(champPort.getText()));
        if (socket != null) {
            rootCtrl.createSession(socket);
        } else {
            rootCtrl.error("Connexion serveur", "Serveur inconnu, verifié l'ip ou le port");
        }
    }

    public void init(RootCtrl rootCtrl) {
        this.rootCtrl = rootCtrl;
    }

    public void disconnect(ActionEvent actionEvent) {
        rootCtrl.goToLogin();
    }
}
