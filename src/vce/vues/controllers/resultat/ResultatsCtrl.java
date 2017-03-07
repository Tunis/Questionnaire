package vce.vues.controllers.resultat;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import vce.vues.controllers.RootCtrl;

public class ResultatsCtrl {
	public TableView resultatView;
	public TableColumn colPseudo;
	public TableColumn colScore;
	public TableColumn colTime;


	private RootCtrl rootCtrl;


	public void init(RootCtrl rootCtrl) {
		this.rootCtrl = rootCtrl;
	}
}
