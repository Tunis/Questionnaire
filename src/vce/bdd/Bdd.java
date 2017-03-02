package vce.bdd;

import java.sql.Connection;

public class Bdd {

    private Connection instance;

    public Connection getInstance() {
        return instance;
    }
}
