import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.event.*;
import javax.sound.sampled.LineUnavailableException;
import java.util.ArrayList;

public class Tones extends Application {
	private Player player;
	

	public static void main(String args[]) {
		launch(args);
	}

	public void start(Stage primaryStage) {
		try {
			player = new Player();
		} catch(InitFailedPlayerException ex) {fatalError(ex.toString());}
		TonesWindow window = new TonesWindow();
	}

	private class TonesWindow extends Stage { //later make a nice gui including all the debug info in itself
		private VBox vbox = new VBox();
		private TextField frequencyField, durationField, innerDelayField, chordFadeIntervalField, trackFadeIntervalField;
		private RadioButton harmonicSeriesRadioButton, equalTemperamentRadioButton;

		public TonesWindow() {
			setTitle("Tones");

			ToggleGroup toggleGroup = new ToggleGroup();
			harmonicSeriesRadioButton = new RadioButton("Harmonic series");
			equalTemperamentRadioButton = new RadioButton("Equal temperament");
			harmonicSeriesRadioButton.setToggleGroup(toggleGroup);
			equalTemperamentRadioButton.setToggleGroup(toggleGroup);
			vbox.getChildren().add(harmonicSeriesRadioButton);
			vbox.getChildren().add(equalTemperamentRadioButton);
			harmonicSeriesRadioButton.fire();

			HBox durationHBox = new HBox();
			durationField = new TextField();
			durationHBox.getChildren().add(new Label("Track duration: "));
			durationHBox.getChildren().add(durationField);
			vbox.getChildren().add(durationHBox);

			HBox innerDelayHBox = new HBox();
			innerDelayField = new TextField();
			innerDelayHBox.getChildren().add(new Label("Inner delay: "));
			innerDelayHBox.getChildren().add(innerDelayField);
			vbox.getChildren().add(innerDelayHBox);

			HBox chordFadeIntervalHBox = new HBox();
			chordFadeIntervalField = new TextField();
			chordFadeIntervalHBox.getChildren().add(new Label("Chord fade interval: "));
			chordFadeIntervalHBox.getChildren().add(chordFadeIntervalField);
			vbox.getChildren().add(chordFadeIntervalHBox);

			HBox trackFadeIntervalHBox = new HBox();
			trackFadeIntervalField = new TextField();
			trackFadeIntervalHBox.getChildren().add(new Label("Track fade interval: "));
			trackFadeIntervalHBox.getChildren().add(trackFadeIntervalField);
			vbox.getChildren().add(trackFadeIntervalHBox);

			frequencyField = new TextField();
			vbox.getChildren().add(frequencyField);

	        HBox buttonsHBox = new HBox();
			Button playButton = new Button("Play");
			playButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent event) {
	            	playButtonAction();
	            }
	        });
			Button stopButton = new Button("Stop");
			stopButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent event) {
	            	stopButtonAction();
	            }
	        });
	        buttonsHBox.getChildren().addAll(playButton, stopButton);
	        vbox.getChildren().add(buttonsHBox);
	        
	        setScene(new Scene(vbox, 500, 500));
	        show();
		}

		private void playButtonAction() {
			try {
				Track newTrack = createTrack();
				System.out.println(newTrack.toString());
				player.play(newTrack);
			} catch(InvalidTrackDataException | IllegalActionPlayerException ex) {
				System.out.println(ex.toString());
			}
		}

		private void stopButtonAction() {
			try {
				player.stop();
			} catch(IllegalActionPlayerException ex) {
				System.out.println(ex.toString());
			}
		}

		private Track createTrack() throws InvalidTrackDataException {
			ToneSystem toneSystem = harmonicSeriesRadioButton.isSelected() ? ToneSystem.HARMONIC : ToneSystem.TEMPERED;
			ArrayList<Chord> chordsList = new ArrayList<Chord>();
			Chord chord = new Chord();
			chordsList.add(chord);
			double prev = 0;
			String[] tokens = frequencyField.getText().split(" ");
			for(String token: tokens) {
				if(!token.equals("")) { //process empty strings earlier
					if(token.equals(".")) {
						chord = new Chord();
						chordsList.add(chord);
					} else {
						try {
							double freq = 0;
							if(token.endsWith("s")) {
								if(prev == 0) {
									throw new InvalidTrackDataException("Error while parsing track data on the token \"" + token + "\"");
								}
								int semitones = Integer.parseInt(token.substring(0, token.length() - 1));
								freq = toneSystem.countFrequency(prev, semitones);
							} else {
								freq = Double.parseDouble(token);
							}
							Frequency frequency = new Frequency(freq);
							chord.add(frequency);
							prev = freq;
						} catch(NumberFormatException ex) {
							throw new InvalidTrackDataException("Error while parsing track data on the token \"" + token + "\"");
						}
					}
				}
			}
			Track track = new Track(Integer.parseInt(durationField.getText()), Integer.parseInt(innerDelayField.getText()),
								    Integer.parseInt(chordFadeIntervalField.getText()), Integer.parseInt(trackFadeIntervalField.getText()));
			track.addAll(chordsList);
			if(track.size() == 0) {
				throw new InvalidTrackDataException("No track data was found");
			}
			return track;
		}

		public class InvalidTrackDataException extends Exception {
			public InvalidTrackDataException(String message) {
				super(message);
			}
		}
	}

	private void fatalError(String string) {
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