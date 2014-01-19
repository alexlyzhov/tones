import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.event.*;

public class Tones extends Application {
	public static void main(String args[]) {
		launch(args);
	}

	private Player player;
	private boolean playing = false;
	private Track.ToneSystem toneSystem;
	private Text logText;

	public void start(Stage primaryStage) {
		player.init();
		primaryStage.setTitle("Tones");
		VBox vbox = new VBox();

		ToggleGroup toggleGroup = new ToggleGroup();
		RadioButton harmonicSeriesRadioButton = new RadioButton("Harmonic series");
		RadioButton equalTemperamentRadioButton = new RadioButton("Equal temperament");
		harmonicSeriesRadioButton.setToggleGroup(toggleGroup);
		equalTemperamentRadioButton.setToggleGroup(toggleGroup);
		harmonicSeriesRadioButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
               toneSystem = Track.ToneSystem.HARMONIC;
            }
        });
		harmonicSeriesRadioButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
               toneSystem = Track.ToneSystem.TEMPERED;
            }
        });
		vbox.getChildren().add(harmonicSeriesRadioButton);
		vbox.getChildren().add(equalTemperamentRadioButton);
		harmonicSeriesRadioButton.fire();

		final TextField frequencyField = new TextField();
		vbox.getChildren().add(frequencyField);

        HBox hbox = new HBox();
		Button playButton = new Button("Play");
		playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                if(!playing) {
                	try {
                		Track newTrack = new Track(frequencyField.getText(), toneSystem);
                		log("Frequencies are interpreted as " + newTrack.getFreqsText());
                		player = new Player(newTrack);
                		playing = true;
                	} catch(InvalidTrackDataException ex) {
                		log(ex.toString());
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

        logText = new Text();
        vbox.getChildren().add(logText);
        
        primaryStage.setScene(new Scene(vbox, 500, 500));
        primaryStage.show();
	}

	private void log(String string) {
		String currentText = logText.getText();
		currentText += string;
		currentText += "\n";
		logText.setText(currentText);
	}
}