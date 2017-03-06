package vce.ihm.controllers.salon;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import vce.ihm.controllers.RootCtrl;
import vce.session.Session;

import java.net.Socket;

public class JoinSalonCtrl {


    public TextField champIp;
    public TextField champPort;
    public TextField champDuree;
    private RootCtrl rootCtrl;

    public void createSalon(ActionEvent actionEvent) {
        if (!champDuree.getText().isEmpty()) {
            try {
                int duree = Integer.parseInt(champDuree.getText());
                if (duree > 1) {
                    rootCtrl.createSalon(duree);
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public void joinSalon(ActionEvent actionEvent) {
        Socket socket = Session.connectServer(champIp.getText(), Integer.parseInt(champPort.getText()));
        if (socket != null) {
            rootCtrl.createSession(socket);
        } else {
            // TODO: 06/03/2017 erreur message
            System.out.println("serveur inconnu");
        }
    }

    public void init(RootCtrl rootCtrl) {
        this.rootCtrl = rootCtrl;
    }
}
