package vce.methodes.data;

import java.io.Serializable;

public class User implements Serializable {

    private String nom, prenom, pseudo;
    private int id;

	// Constructeur
	public User(int id, String nom, String prenom, String pseudo) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
    }

    // Get pseudo

    public String getPseudo() {
        return this.pseudo;
    }

	// get Id
	public int getId() {
		return id;
	}

	// To string object override pour facilité l'affichage dans l'ui :p
	@Override
    public String toString() {
	    return this.id + " " + this.prenom + " " + this.nom + " (" + this.pseudo + ").";
	}

}
