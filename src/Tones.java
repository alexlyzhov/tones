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
	private ToneSystem toneSystem;
	private Log log;
	private TextField frequencyField;

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
                toneSystem = ToneSystem.HARMONIC;
            }
        });
		equalTemperamentRadioButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                toneSystem = ToneSystem.TEMPERED;
            }
        });
		vbox.getChildren().add(harmonicSeriesRadioButton);
		vbox.getChildren().add(equalTemperamentRadioButton);
		harmonicSeriesRadioButton.fire();

		frequencyField = new TextField();
		vbox.getChildren().add(frequencyField);

        HBox hbox = new HBox();
		Button playButton = new Button("Play");
		playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
            	try {
            		Track newTrack = createTrack();
            		log.write("Playing new track");
            		log.write(newTrack.toString());
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

	private Track createTrack() throws InvalidTrackDataException {
		Chord chord = new Chord();
		double prev = 0;
		String[] tokens = frequencyField.getText().split(" ");
		for(String token: tokens) {
			if(!token.equals("")) { //process empty strings earlier
				try {
					double freq = 0;
					if(token.endsWith("s")) {
						if(prev == 0) {
							throw new InvalidTrackDataTokenException(token);
						}
						int semitones = Integer.parseInt(token.substring(0, token.length() - 1));
						freq = toneSystem.countFrequency(prev, semitones);
					} else {
						freq = Double.parseDouble(token);
					}
					chord.add(freq);
					prev = freq;
				} catch(NumberFormatException ex) {
					throw new InvalidTrackDataTokenException(token);
				}
			}
		}
		Track track = new Track(3000);
		track.add(chord);
		if(track.size() == 0) {
			throw new InvalidTrackDataException("No track data was found");
		}
		return track;
	}

	private void fatalError(String string) { //test
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