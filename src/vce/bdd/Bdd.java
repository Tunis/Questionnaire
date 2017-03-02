package vce.bdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Bdd {

    private Connection instance;
    private String url = "jdbc:mysql://localhost/vce?user=root&password=";

    public Bdd() {
    }

    public Connection getInstance() {
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
