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

		new Db("localhost","","QUESTIONNAIRE","root","root");
		Application.launch(Gui.class);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("VCE style");
		primaryStage.centerOnScreen();
		primaryStage.setMaximized(true);
		
		FXMLLoader loadRoot = new FXMLLoader();
		loadRoot.setLocation(Gui.class.getResource("/salon/salon.fxml"));
		VBox root = loadRoot.load();
		
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
}
