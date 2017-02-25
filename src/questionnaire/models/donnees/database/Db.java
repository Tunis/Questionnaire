package questionnaire.models.donnees.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {

    // MySQL only ...

    private static String url ="jdbc:mysql://";
    private static String host,port,dbname,user,pass;
    private static Connection dbconnect;

    public Db(String hostc, String portc, String dbnamec, String userc, String passc) {

        host = hostc;
        port = portc;
        dbname = dbnamec;
        user = userc;
        pass = passc;

    }

    public static Connection getDb () {

        if(dbconnect == null)
        {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                dbconnect = DriverManager.getConnection(url+host+"/"+dbname+"?user="+user+"&password="+pass);
            }
            catch(ClassNotFoundException | SQLException error)
            {
                return null;
            }
        }
        return dbconnect;

    }

}
