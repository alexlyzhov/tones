import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Tones extends Application {
	private Stage tonesStage;
	private Scene tonesScene;

	public static void main(String args[]) {
		launch(args);
	}

	public void start(Stage primaryStage) {
		TonesVBox tonesVBox = new TonesVBox();
		
		tonesScene = new Scene(tonesVBox);
		
		tonesStage = new Stage();
		tonesStage.setTitle("Tones");
		tonesStage.setScene(tonesScene);
		tonesStage.show();
	}
}