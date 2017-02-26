package questionnaire.controllers.views.login;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import questionnaire.controllers.views.RootCtrl;
import questionnaire.models.app.server.Server;

import java.net.URL;
import java.util.ResourceBundle;

public class MenuCtrl implements Initializable {
	
	
	private RootCtrl rootCtrl;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public void createSalon(ActionEvent actionEvent) {
		rootCtrl.setServer(new Server());
		rootCtrl.changeView("salon");
	}
	
	public void joinSalon(ActionEvent actionEvent) {
		System.out.println("rejoindre un salon");
	}
	
	public void init(RootCtrl rootCtrl){
		this.rootCtrl = rootCtrl;
	}
}
