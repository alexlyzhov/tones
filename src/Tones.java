import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.event.*;
import javax.sound.sampled.LineUnavailableException;

public class Tones extends Application {
	public static void main(String args[]) {
		launch(args);
	}

	private Player player;
	private Track.ToneSystem toneSystem;
	private Log log;

	public void start(Stage primaryStage) {
		try {
			player = new Player();
		} catch(LineUnavailableException ex) {
			fatalError("The player line is unavailable");
		}

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
		equalTemperamentRadioButton.setOnAction(new EventHandler<ActionEvent>() {
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
            	try {
            		Track newTrack = new Track(frequencyField.getText(), toneSystem);
            		log.write("Frequencies are interpreted as " + newTrack.getFreqsText());
            		player.play(newTrack);
            	} catch(InvalidTrackDataException | IllegalPlayerActionException ex) {
            		log.write(ex.toString());
            	}
            }
        });
        hbox.getChildren().add(playButton);
		Button stopButton = new Button("Stop");
		stopButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
            	try {
            		player.stop();
            	} catch(IllegalPlayerActionException ex) {
            		log.write(ex.toString());
            	}
            }
        });
        hbox.getChildren().add(stopButton);
        vbox.getChildren().add(hbox);

        log = new Log();
        vbox.getChildren().add(log);
        
        primaryStage.setScene(new Scene(vbox, 500, 500));
        primaryStage.show();
	}

	private void fatalError(String string) {
		Stage dialogStage = new Stage();
		dialogStage.initModality(Modality.WINDOW_MODAL);
		dialogStage.initStyle(StageStyle.UTILITY);
		dialogStage.setTitle("Error");
		VBox vbox = new VBox();
		Text stringText = new Text(string);
		Button okButton = new Button("OK");
		okButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
				System.exit(1);
            }
        });
		vbox.getChildren().add(stringText);
        vbox.getChildren().add(okButton);
		dialogStage.setScene(new Scene(vbox));
		dialogStage.show();
	}
}