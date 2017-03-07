package vce.methodes.bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public abstract class Bdd {

    private static Connection instance;
    private static ResourceBundle bundle = ResourceBundle.getBundle("configs.properties.config");
    private static String motor = bundle.getString("sgbd.type");
    private static String user = bundle.getString("sgbd.login");
    private static String pass = bundle.getString("sgbd.password");
    private static String address = bundle.getString("sgbd.address");
    private static String port = bundle.getString("sgbd.port");
    private static String bddname = bundle.getString("sgbd.bdd");
    private static String url = "jdbc:"+motor+"://"+address+":"+port+"/"+bddname+"?user="+user+"&password="+pass+"";

    public static Connection getInstance() {
        if (instance == null) {
            try {
	            // les derniere version du mysql Connector on plus besoin du forClass :)
	            instance = DriverManager.getConnection(url);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
