package vce.vues;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import vce.models.deployment.GenerateDirectoy;

public class Start extends Application{

	public static void main(String[] args) {
		Application.launch(Start.class);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		//TODO : New thread avec génération des dossier res et pdf + ajout des images depuis l'archive
		GenerateDirectoy dir = new GenerateDirectoy();
		
		primaryStage.setTitle("VCE project");
		primaryStage.setMinHeight(600);
		primaryStage.setMinWidth(800);
		primaryStage.centerOnScreen();
		primaryStage.setMaximized(true);
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
