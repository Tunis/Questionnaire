package questionnaire.models.app.server;

import java.io.*;
import java.net.Socket;

public class Srvout implements Runnable{

    private Socket server;
    private ObjectOutputStream out;
    //private Status status;

    public Srvout(Socket client) {
        this.server = client;
        //this.status - null;
        new Thread(this).start();
    }

    @Override
    public void run() {

        try {
            out = new ObjectOutputStream(new BufferedOutputStream(server.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {



        }

    }
}
