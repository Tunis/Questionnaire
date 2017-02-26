package questionnaire.models.donnees.users;

import questionnaire.models.donnees.database.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class User {

    private int id;
    private String nom, prenom, pseudo, password;

    public User(int idUser, String nameUser, String firstnameUser, String pseudoUser) {
        id = idUser;
        nom = nameUser;
        prenom = firstnameUser;
        pseudo = pseudoUser;

    }

    public static User createUser(String nameUser, String firstnameUser, String pseudoUser) {
        boolean insered = false;

        Connection db = Db.getDb();
        int userId;
        User newUser = null;

        if(db != null)
        {
            try {

                PreparedStatement insertUser = db.prepareStatement("INSERT INTO User(nameUser, firstnameUser, pseudoUser) VALUES(?,?,?)",new String[]{"idUser"});
                insertUser.setString(1,nameUser);
                insertUser.setString(2,firstnameUser);
                insertUser.setString(3,pseudoUser);

                if(insertUser.executeUpdate() > 0)
                {
                    ResultSet insert = insertUser.getGeneratedKeys();
                    System.out.println(insert);
                    if (insert.next()){
                        userId = insert.getInt(1);
                        return new User(userId, nameUser, firstnameUser, pseudoUser);
                    }
                }



            } catch (SQLException ignored) {}
        }

        return newUser;
    }

    public static List<User> getAlluser() {

        Connection db = Db.getDb();
        List<User> usersList = new ArrayList<>();
        if(db != null)
        {
            try {
                Statement getUsers = db.createStatement();
                ResultSet users = getUsers.executeQuery("SELECT * FROM User");
                while (users.next()) {
                    User user = new User(users.getInt("idUser"),
                            users.getString("nameUser"),
                            users.getString("firstnameUser"),
                            users.getString("pseudoUser"));

                    usersList.add(user);
                }
                return usersList;
            } catch (SQLException error) {return null;}

        }
        else
        {
            return null;
        }

    }

    @Override
    public String toString()
    {
        return prenom + " " + nom + " ("+pseudo+")";
    }

}
