package questionnaire.models.app.server;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable{

    private int port = 0;
    private String host;
    private List<Srvcontrol> connections = new ArrayList<>();
    private ServerSocket server;
    public Server () {


        for (int i = 30000; i < 31000 ; i++) {
            try {
                server = new ServerSocket(i);
                port = i;
                // ne fonctionne pas en local : retourne toujours 0.0.0.0
                host = server.getInetAddress().getHostAddress();
                break;
            }
            catch (IOException ignored){}
        }

        new Thread(this).start();

    }

    public String getHost() {
        return host;
    }

    @Override
    public void run() {

        while (true) {
            try {
                connections.add(new Srvcontrol(server.accept()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public ServerSocket getServer() {
        return server;
    }

    public int getPort() {
        return port;
    }

    public List<Srvcontrol> getConnections() {
        return connections;
    }
}
