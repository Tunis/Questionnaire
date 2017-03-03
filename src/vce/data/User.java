package vce.data;

import java.io.Serializable;

public class User implements Serializable {

    private String nom, prenom, pseudo;
    private int id;

    // Constructeur avec connexion à la db
    public User(int id, String nom, String prenom, String pseudo) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
    }

    // Get pseudo

    /**
     * @return
     */
    public String getPseudo() {
        return this.pseudo;
    }

    public int getId() {
        return id;
    }

    // To string object override
    @Override
    public String toString() {
        String Userinfo = this.id + " " + this.prenom + " " + this.nom + " (" + this.pseudo + ").";
        return Userinfo;
    }

}
