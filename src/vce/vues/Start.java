package vce.vues;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Start extends Application{

	public static void main(String[] args) {
		Application.launch(Start.class);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("VCE project");
		primaryStage.setMinHeight(400);
		primaryStage.setMinWidth(600);
		primaryStage.centerOnScreen();

		// a changer si on veut faire des chose avant de couper l'appli ;)
		primaryStage.setOnCloseRequest((e) -> System.exit(0));

		FXMLLoader loadRoot = new FXMLLoader(Start.class.getResource("/ihm/root.fxml"));
		BorderPane scene = loadRoot.load();

		//FXMLLoader loader = new FXMLLoader(Start.class.getResource("/ihm/resultat/resultat.fxml"));
		//VBox scene = loader.load();

		primaryStage.setScene(new Scene(scene));

		primaryStage.show();
	}
}
