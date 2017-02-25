package questionnaire.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import questionnaire.models.donnees.database.Db;

public class Gui extends Application {

	public static void main(String[] args) {

		// TODO: 25/02/2017 a reactiver lorsque la db sera recuperé
		//new Db("localhost","","QUESTIONNAIRE","root","root");
		Application.launch(Gui.class);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("VCE style");
		primaryStage.centerOnScreen();
		primaryStage.setMaximized(true);
		
		FXMLLoader loadRoot = new FXMLLoader();
		// TODO: 25/02/2017 remettre le root.fxml ici et le borderPane
		loadRoot.setLocation(Gui.class.getResource("/views/salon/salon.fxml"));
		VBox root = loadRoot.load();
		
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
}
