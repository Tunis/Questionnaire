package vce.models.bdd;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public abstract class Bdd {

    private static String url;

    private static Connection instance;

    public static Connection getInstance() {


        if (instance == null) {



            // dossier actuel

            System.getProperty("user.dir");
            File bdd_config = new File(System.getProperty("user.dir")+"/bdd_config.txt");

            if(bdd_config.exists())
            {
                // fichier existe on lis la config
                System.out.println("fichier existe");
                Map<String,String> bdd_info = new HashMap<>();
                try {
                    String line;
                    BufferedReader read = new BufferedReader(new FileReader(bdd_config));
                    while((line = read.readLine()) != null)
                    {
                        String[] info = line.split("=");
                        bdd_info.put(info[0],info[1]);
                    }

                    url = "jdbc:"+bdd_info.get("bdd.type")+"://"+bdd_info.get("bdd.address")+":"+bdd_info.get("bdd.port")+"/"+bdd_info.get("bdd.name")+"?user="+bdd_info.get("bdd.login")+"&password="+bdd_info.get("bdd.password")+"";


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else
            {
                // fichier absent on le crée
                System.out.println("fichier absent");
                PrintWriter out;
                try {
                    out = new PrintWriter(new FileWriter(bdd_config));
                    out.write("bdd.type=mysql");
                    out.println();
                    out.write("bdd.address=127.0.0.1");
                    out.println();
                    out.write("bdd.port=3306");
                    out.println();
                    out.write("bdd.name=vce");
                    out.println();
                    out.write("bdd.login=root");
                    out.println();
                    out.write("bdd.password=root");
                    out.close();
                    url = "jdbc:mysql://127.0.0.1:3306/vce?user=root&password=root";
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            try {
	            // les derniere version du mysql Connector on plus besoins du forClass :)
	            instance = DriverManager.getConnection(url);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
