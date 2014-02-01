import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Tones extends Application {
	private Player player;
	private Stage tonesStage;
	private Scene tonesScene;

	public static void main(String args[]) {
		launch(args);
	}

	public void start(Stage primaryStage) {
		try {
			player = new Player();
			TonesVBox tonesVBox = new TonesVBox(player);
			
			tonesScene = new Scene(tonesVBox);
			
			tonesStage = new Stage();
			tonesStage.setTitle("Tones");
			tonesStage.setScene(tonesScene);
			tonesStage.show();
		} catch(InitFailedPlayerException ex) {new FatalErrorDialog(ex.toString());}
	}

	private class FatalErrorDialog extends Stage {
		public FatalErrorDialog(String message) {
			
		}
			// Stage dialogStage = new Stage();
			// dialogStage.initModality(Modality.WINDOW_MODAL);
			// dialogStage.initStyle(StageStyle.UTILITY);
			// dialogStage.setTitle("Error");
			// VBox vbox = new VBox();
			// Text stringText = new Text(string);
			// Button okButton = new Button("OK");
			// okButton.setOnAction(new EventHandler<ActionEvent>() {
	  //           public void handle(ActionEvent event) {
			// 		System.exit(1);
	  //           }
	  //       });
			// vbox.getChildren().add(stringText);
	  //       vbox.getChildren().add(okButton);
			// dialogStage.setScene(new Scene(vbox));
			// dialogStage.show();
	}
}