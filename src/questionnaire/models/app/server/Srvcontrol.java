package questionnaire.models.app.server;

import questionnaire.models.app.server.Srvin;
import questionnaire.models.app.server.Srvout;
import questionnaire.models.donnees.users.User;

import java.net.Socket;

public class Srvcontrol {

    private Socket server;
    private Srvin in;
    private Srvout out;
    private User user;

    public Srvcontrol(Socket newserver) {

        server = newserver;
        in = new Srvin(server);
        out = new Srvout(server);

    }

    public Socket getServer() {
        return server;
    }

    public Srvin getIn() {
        return in;
    }

    public Srvout getOut() {
        return out;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
