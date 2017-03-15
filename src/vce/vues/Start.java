package vce.vues;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import vce.models.deployment.GenerateDirectoy;
import vce.vues.controllers.RootCtrl;

public class Start extends Application{

	private Stage primaryStage;
	private RootCtrl rootCtrl;

	public static void main(String[] args) {
		Application.launch(Start.class);
	}

	@Override
	public void start(Stage stage) throws Exception {
		this.primaryStage = stage;
		//TODO : New thread avec génération des dossier res et pdf + ajout des images depuis l'archive

		//Génération des dossiers et extraction des fichiers nécessaire au fonctionnement de l'application.
		new Thread(new GenerateDirectoy()).start();

		primaryStage.setTitle("VCE project");
		primaryStage.setMinHeight(600);
		primaryStage.setMinWidth(800);
		primaryStage.centerOnScreen();
		primaryStage.setMaximized(true);
		// a changer si on veut faire des chose avant de couper l'appli ;)
		primaryStage.setOnCloseRequest((e) -> System.exit(0));

		FXMLLoader loadRoot = new FXMLLoader(Start.class.getResource("/ihm/root.fxml"));
		BorderPane scene = loadRoot.load();
		rootCtrl = loadRoot.getController();

		//FXMLLoader loader = new FXMLLoader(Start.class.getResource("/ihm/resultat/resultat.fxml"));
		//VBox scene = loader.load();

		primaryStage.setScene(new Scene(scene));

		initShortcut();

		primaryStage.show();
	}

	private void initShortcut() {
		Platform.runLater(() -> primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_ANY), () -> {
			System.out.println("raccourci question suivante");
			if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getSqlConfig())) {
				rootCtrl.getQuestionnaireCtrl().nextQuestion(null);
			}
		}));

		Platform.runLater(() -> primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_ANY), () -> {
			System.out.println("raccourci disconnect");
			if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getInscription())) {
				rootCtrl.getInscriptionCtrl().goToLogin(null);
			} else if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getJoinSalon())) {
				rootCtrl.getJoinSalonCtrl().disconnect(null);
			} else if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getCreateSalonView())) {
				rootCtrl.getCreateSalonCtrl().Disconnect(null);
			} else if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getSalonView())) {
				rootCtrl.getSalonCtrl().disconnect(null);
			} else if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getResultats())) {
				rootCtrl.getResultatsCtrl().back(null);
			}
		}));

		Platform.runLater(() -> primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_ANY), () -> {
			System.out.println("raccourci question suivante");
			if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getQuestionnaire())) {
				rootCtrl.getQuestionnaireCtrl().nextQuestion(null);
			}
		}));
		Platform.runLater(() -> primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.P, KeyCombination.SHORTCUT_ANY), () -> {
			System.out.println("raccourci question precedente");
			if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getQuestionnaire())) {
				rootCtrl.getQuestionnaireCtrl().prevQuestion(null);
			}
		}));
		Platform.runLater(() -> primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.F, KeyCombination.SHORTCUT_ANY), () -> {
			System.out.println("raccourci fin questionnaire");
			if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getQuestionnaire())) {
				rootCtrl.getQuestionnaireCtrl().endQuestionnaire(null);
			}
		}));
		Platform.runLater(() -> primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.G, KeyCombination.SHORTCUT_ANY), () -> {
			System.out.println("raccourci fin questionnaire");
			if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getQuestionnaire())) {
				rootCtrl.getQuestionnaireCtrl().endQuestionnaire(null);
			}
		}));

		Platform.runLater(() -> primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_ANY), () -> {
			System.out.println("raccourci login");
			if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getLogin())) {
				rootCtrl.getLoginCtrl().tryLogin(null);
			} else if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getSalonView())) {
				rootCtrl.getSalonCtrl().launch(null);
			}
		}));

		Platform.runLater(() -> primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.I, KeyCombination.SHORTCUT_ANY), () -> {
			System.out.println("raccourci inscription");
			if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getLogin())) {
				rootCtrl.getLoginCtrl().goToInscription(null);
			}
		}));

		Platform.runLater(() -> primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_ANY), () -> {
			System.out.println("raccourci createSalon");
			if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getJoinSalon())) {
				rootCtrl.getJoinSalonCtrl().createSalon(null);
			}
		}));

		Platform.runLater(() -> primaryStage.getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.J, KeyCombination.SHORTCUT_ANY), () -> {
			System.out.println("raccourci join salon");
			if (rootCtrl.getRoot().getCenter().equals(rootCtrl.getJoinSalon())) {
				rootCtrl.getJoinSalonCtrl().joinSalon(null);
			}
		}));
	}
}
