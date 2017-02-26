package questionnaire.controllers.views.login;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import questionnaire.controllers.views.RootCtrl;
import questionnaire.models.donnees.users.User;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LoginCtrl implements Initializable {
	public ComboBox<User> listUser;
	private RootCtrl rootCtrl;
	
	public void loginUser(ActionEvent actionEvent) {
		User newUser = listUser.getSelectionModel().getSelectedItem();
		rootCtrl.setUser(newUser);
		rootCtrl.changeView("menu");
	}
	
	public void createUser(ActionEvent actionEvent) {
		rootCtrl.changeView("createUser");
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void init(RootCtrl rootCtrl, List<User> users){

		this.rootCtrl = rootCtrl;
		listUser.getItems().addAll(users);
	}
}
