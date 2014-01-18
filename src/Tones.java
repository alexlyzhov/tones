import java.util.ArrayList;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.*;

public class Tones extends Application {
	public static void main(String args[]) {
		launch(args);
	}

	private Player player;
	private boolean playing = false;

	public void start(Stage primaryStage) {
		player.init();
		primaryStage.setTitle("Tones");
		VBox vbox = new VBox();

		final TextField frequencyField = new TextField();
		vbox.getChildren().add(frequencyField);

        HBox hbox = new HBox();
		Button playButton = new Button("Play");
		playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if(!playing) {
					float[] freqs = parseFreqs(frequencyField.getText());
					if(freqs.length > 0) {
						player = new Player(freqs);
						playing = true;
					}
				}
            }
        });
        hbox.getChildren().add(playButton);
		Button stopButton = new Button("Stop");
		stopButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if(player != null) {
					player.stopSound();
					playing = false;
				}
            }
        });
        hbox.getChildren().add(stopButton);
        vbox.getChildren().add(hbox);
        
        primaryStage.setScene(new Scene(vbox));
        primaryStage.show();
	}

	private float[] parseFreqs(String text) {
		ArrayList<Float> freqsList = new ArrayList<Float>();
		String[] tokens = text.split(" ");
		for(String token: tokens) {
			try {
				Float freq = Float.parseFloat(token);
				freqsList.add(freq);
			} catch(Exception ex) {}
		}
		float[] freqsArray = new float[freqsList.size()];
		for(int i = 0; i < freqsArray.length; i++) {
			freqsArray[i] = freqsList.get(i);
		}
		return freqsArray;
	}
}