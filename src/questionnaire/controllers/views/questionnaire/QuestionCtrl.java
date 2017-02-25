package questionnaire.controllers.views.questionnaire;

import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import questionnaire.controllers.views.RootCtrl;

import java.net.URL;
import java.util.ResourceBundle;

public class QuestionCtrl implements Initializable {
    public FlowPane questionText;
    public RadioButton reponse1;
    public ToggleGroup reponseGroup;
    public RadioButton reponse2;
    public RadioButton reponse3;
    public RadioButton reponse4;
    private RootCtrl rootCtrl;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void init(RootCtrl rootCtrl) {
        this.rootCtrl = rootCtrl;
    }
}
