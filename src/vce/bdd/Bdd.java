package vce.bdd;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Bdd implements Serializable {

    private static Connection instance;
    private static String url = "jdbc:mysql://localhost/vce?user=root&password=";

    public static Connection getInstance() {
        if (instance == null) {
            try {
                instance = DriverManager.getConnection(url);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
