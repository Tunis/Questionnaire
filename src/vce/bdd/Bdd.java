package vce.bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class Bdd {

    private static Connection instance;
    private static String url = "jdbc:mysql://localhost/vce?user=root&password=root";

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
