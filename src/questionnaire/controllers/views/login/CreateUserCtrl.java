package questionnaire.controllers.views.login;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import questionnaire.controllers.views.RootCtrl;
import questionnaire.models.donnees.users.User;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateUserCtrl implements Initializable {
	
	
	private RootCtrl rootCtrl;
	public TextField champNom;
	public TextField champPrenom;
	public TextField champPseudo;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void init(RootCtrl rootCtrl){
		this.rootCtrl = rootCtrl;
	}
	
	public void createAccount(ActionEvent actionEvent) {


		if(champNom.getText().isEmpty() || champPrenom.getText().isEmpty() || champPseudo.getText().isEmpty())
		{
			Alert alert = new Alert(Alert.AlertType.WARNING);
			alert.setTitle("Attention !");
			alert.setHeaderText("Formulaire incomplet !");
			alert.setContentText("Merci de remplir tout les champs !");
			alert.showAndWait();
		}
		else
		{
			User newUser = User.createUser(champNom.getText(),champPrenom.getText(),champPseudo.getText());

			if (newUser == null)
			{
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Erreur !");
				alert.setHeaderText("l'Utilisateur existe déjà !");
				alert.setContentText("Il semblerais que les informations que vous avez rentrer sont déjà inscrite dans notre base de donnée veulliez vous connecter");
				alert.showAndWait();
				rootCtrl.changeView("login");
			}
			else
            {
                rootCtrl.setUser(newUser);
                // check si user create si oui on passe au menu
                rootCtrl.changeView("menu");
            }

		}
	}
}
