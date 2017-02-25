package questionnaire.models.app.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class Srvin implements Runnable{

    private Socket server;
    private ObjectInputStream in;

    public Srvin(Socket client) {
        this.server = client;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            in = new ObjectInputStream(new BufferedInputStream(server.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (true) {
            try {

                Object toread = in.readObject();

                switch (toread.getClass().getSimpleName())
                {
                    case "User":
                        System.out.println("Action pour user");
                        break;
                    case "Resultat":
                        System.out.println("Action pour résultat");
                        break;
                    case "Update":
                        System.out.println("Action pour update");
                        break;
                    default:
                        System.out.println("Error");
                        break;
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
