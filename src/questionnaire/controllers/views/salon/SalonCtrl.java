package questionnaire.controllers.views.salon;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import questionnaire.controllers.views.RootCtrl;
import questionnaire.models.donnees.questionnaire.Questionnaire;
import questionnaire.models.donnees.users.User;

import java.net.URL;
import java.util.ResourceBundle;

public class SalonCtrl implements Initializable {

    private ObservableList<User> userList = FXCollections.observableArrayList();

    @FXML
    private ListView<User> salonlist;
    private RootCtrl rootCtrl;
    private Questionnaire questionnaire;

    @FXML
    void salonready(ActionEvent event) {
        ToggleButton btn = (ToggleButton) event.getSource();
        if(btn.isSelected()){
            // user ready envoyer status au server.
        }else{
            // user not ready envoyer status au server.
        }
    }

    public void init(RootCtrl root, User user) {
        userList.add(user);
        this.rootCtrl = root;

        userList.forEach(System.out::println);

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        salonlist.setItems(userList);
        questionnaire = new Questionnaire();
    }
}
