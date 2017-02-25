package questionnaire.controllers.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import questionnaire.controllers.Gui;
import questionnaire.controllers.views.login.CreateUserCtrl;
import questionnaire.controllers.views.login.LoginCtrl;
import questionnaire.controllers.views.login.MenuCtrl;
import questionnaire.controllers.views.questionnaire.QuestionnaireCtrl;
import questionnaire.controllers.views.salon.SalonCtrl;
import questionnaire.models.donnees.users.User;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RootCtrl implements Initializable {
	
	@FXML
	private BorderPane root;
	
	
	// login view et controller :
	private VBox login;
	private LoginCtrl loginCtrl;
    // salon view et controller :
    private VBox salon;
    private SalonCtrl salonCtrl;
	// createUser view et controller :
	private VBox createUser;
	private CreateUserCtrl createUserCtrl;
	// questionnaire view et controller :
	private BorderPane Questionnaire;
	private QuestionnaireCtrl questionnaireCtrl;
	// menu view et controller :
	private VBox menu;
	private MenuCtrl menuCtrl;
	// autres variable
	private List<User> users = new ArrayList<>();
	private User user;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		users = User.getAlluser();

		FXMLLoader loadLogin = new FXMLLoader();
		try {
			loadLogin.setLocation(Gui.class.getResource("/login/login.fxml"));
			login = loadLogin.load();
			loginCtrl = loadLogin.getController();
			loginCtrl.init(this, users);
		} catch (IOException e) {
			e.printStackTrace();
		}
        FXMLLoader loadsalon = new FXMLLoader();
        try {
            loadsalon.setLocation(Gui.class.getResource("/salon/salon.fxml"));
            salon = loadsalon.load();
            salonCtrl = loadsalon.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }

		FXMLLoader loadCreateUser = new FXMLLoader();
		try {
			loadCreateUser.setLocation(Gui.class.getResource("/login/createUser.fxml"));
			createUser = loadCreateUser.load();
			createUserCtrl = loadCreateUser.getController();
			createUserCtrl.init(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// loading menu view and controller :
		FXMLLoader loadMenu = new FXMLLoader();
		try {
			loadMenu.setLocation(Gui.class.getResource("/login/menu.fxml"));
			menu = loadMenu.load();
			menuCtrl = loadMenu.getController();
			menuCtrl.init(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		root.setCenter(login);
	}
	
	public void changeView(String view){
		switch (view){
			case "login":
				root.setCenter(login);
				break;
			case "createUser":
				root.setCenter(createUser);
				break;
			case "menu":
				root.setCenter(menu);
				break;
            case "salon":
                salonCtrl.init(this, user);
                root.setCenter(salon);
                break;
			default: root.setCenter(login);
		}
	}

	public void setUser(User user) {
		this.user = user;
        System.out.println(user.toString());
        root.setTop(new Label(user.toString()));
	}
}
