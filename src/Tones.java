import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.event.*;
import javax.sound.sampled.LineUnavailableException;
import java.util.ArrayList;
import javafx.beans.value.*;
import javafx.geometry.*;

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
		private TextField frequencyField;
		private Slider durationSlider, innerDelaySlider, chordFadeIntervalSlider, trackFadeIntervalSlider; //fadeDuration
		private RadioButton harmonicSeriesRadioButton, equalTemperamentRadioButton;
		private final double initialTrackDuration = 2D;

		public TonesWindow() {
			setTitle("Tones");
			vbox.setSpacing(3);
			vbox.setAlignment(Pos.CENTER);
	        vbox.setPadding(new Insets(10, 10, 10, 10));

			Text tonesText = new Text("tones");
			tonesText.setFont(new Font(20));
			vbox.getChildren().add(tonesText);

	        Separator separator1 = new Separator();
	        vbox.getChildren().add(separator1);

			ToggleGroup toggleGroup = new ToggleGroup();
			harmonicSeriesRadioButton = new RadioButton("Harmonic series");
			equalTemperamentRadioButton = new RadioButton("Equal temperament");
			harmonicSeriesRadioButton.setToggleGroup(toggleGroup);
			equalTemperamentRadioButton.setToggleGroup(toggleGroup);
			vbox.getChildren().add(harmonicSeriesRadioButton);
			vbox.getChildren().add(equalTemperamentRadioButton);
			harmonicSeriesRadioButton.fire();

			frequencyField = new TextField();
			frequencyField.setPromptText("Frequencies");
			vbox.getChildren().add(frequencyField);

	        Separator separator2 = new Separator();
	        vbox.getChildren().add(separator2);

			GridPane slidersGrid = new GridPane();
			vbox.getChildren().add(slidersGrid);
			slidersGrid.setVgap(5);
			slidersGrid.setAlignment(Pos.CENTER);

			durationSlider = new Slider();
			durationSlider.setMin(0.1);
			durationSlider.setMax(30);
			durationSlider.setValue(initialTrackDuration);
			durationSlider.setMajorTickUnit(0.1);
			durationSlider.setMinorTickCount(0);
			durationSlider.setBlockIncrement(0.1);
			durationSlider.setShowTickMarks(false);
			durationSlider.setShowTickLabels(false);
			final Label durationLabel = new Label(String.format("%.1f seconds", durationSlider.getValue()));
			// final Label durationLabel = new Label(String.format("%4.1s seconds", durationSlider.getValue()));
			durationSlider.valueProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
					durationLabel.setText(String.format("%.1f seconds", newValue));
					double doubleValue = newValue.doubleValue();
					innerDelaySlider.setMax(doubleValue);
					trackFadeIntervalSlider.setMax(doubleValue);
					chordFadeIntervalSlider.setMax(doubleValue);
				}
			});
			slidersGrid.add(new Label("Track duration: "), 1, 1);
			slidersGrid.add(durationSlider, 2, 1);
			slidersGrid.add(durationLabel, 3, 1);

			innerDelaySlider = new Slider();
			innerDelaySlider.setMin(0);
			innerDelaySlider.setMax(initialTrackDuration);
			innerDelaySlider.setValue(0);
			innerDelaySlider.setMajorTickUnit(0.1);
			innerDelaySlider.setMinorTickCount(0);
			innerDelaySlider.setBlockIncrement(0.1);
			innerDelaySlider.setShowTickMarks(false);
			innerDelaySlider.setShowTickLabels(false);
			final Label innerDelayLabel = new Label(String.format("%.1f seconds", innerDelaySlider.getValue()));
			innerDelaySlider.valueProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
					innerDelayLabel.setText(String.format("%.1f seconds", newValue));
				}
			});
			slidersGrid.add(new Label("Inner delay: "), 1, 2);
			slidersGrid.add(innerDelaySlider, 2, 2);
			slidersGrid.add(innerDelayLabel, 3, 2);

			trackFadeIntervalSlider = new Slider();
			trackFadeIntervalSlider.setMin(0);
			trackFadeIntervalSlider.setMax(initialTrackDuration);
			trackFadeIntervalSlider.setValue(0);
			trackFadeIntervalSlider.setMajorTickUnit(0.1);
			trackFadeIntervalSlider.setMinorTickCount(0);
			trackFadeIntervalSlider.setBlockIncrement(0.1);
			trackFadeIntervalSlider.setShowTickMarks(false);
			trackFadeIntervalSlider.setShowTickLabels(false);
			final Label trackFadeIntervalLabel = new Label(String.format("%.1f seconds", trackFadeIntervalSlider.getValue()));
			trackFadeIntervalSlider.valueProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
					trackFadeIntervalLabel.setText(String.format("%.1f seconds", newValue));
				}
			});
			slidersGrid.add(new Label("Track fade interval: "), 1, 3);
			slidersGrid.add(trackFadeIntervalSlider, 2, 3);
			slidersGrid.add(trackFadeIntervalLabel, 3, 3);

			chordFadeIntervalSlider = new Slider();
			chordFadeIntervalSlider.setMin(0);
			chordFadeIntervalSlider.setMax(initialTrackDuration);
			chordFadeIntervalSlider.setValue(0);
			chordFadeIntervalSlider.setMajorTickUnit(0.1);
			chordFadeIntervalSlider.setMinorTickCount(0);
			chordFadeIntervalSlider.setBlockIncrement(0.1);
			chordFadeIntervalSlider.setShowTickMarks(false);
			chordFadeIntervalSlider.setShowTickLabels(false);
			final Label chordFadeIntervalLabel = new Label(String.format("%.1f seconds", chordFadeIntervalSlider.getValue()));
			chordFadeIntervalSlider.valueProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
					chordFadeIntervalLabel.setText(String.format("%.1f seconds", newValue));
				}
			});
			slidersGrid.add(new Label("Chord fade interval: "), 1, 4);
			slidersGrid.add(chordFadeIntervalSlider, 2, 4);
			slidersGrid.add(chordFadeIntervalLabel, 3, 4);

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
	        buttonsHBox.setAlignment(Pos.CENTER);
	        vbox.getChildren().add(buttonsHBox);

	        final Label trackInfo = new Label("");
	        final Label chordInfo = new Label("");
	        (new Thread() {
	        	public void run() {
	        		while(true) { //run this cycle only if the player is active and stop with the track stoppage
	        			Platform.runLater(new Runnable() {
	        				public void run() {
			        			String trackInfoString = "";
			        			try {
			        				String trackPositionString = String.format("%.1f", player.getTrackPosition());
			        				String trackDurationString = String.format("%.1f", player.getTrackDuration());
			        				trackInfoString = trackPositionString + " / " + trackDurationString;
			        			} catch(IllegalActionPlayerException ex) {}
	        					trackInfo.setText(trackInfoString);
	        					String chordInfoString = "";
	        					try {
	        						chordInfoString = "Current chord: " + player.getCurrentChord().toString();
	        					} catch(IllegalActionPlayerException ex) {}
	        					chordInfo.setText(chordInfoString);
	        				}
	        			});
	        			try {
	        				Thread.sleep(50);
	        			} catch(InterruptedException ex) {ex.printStackTrace();}
	        		}
	        	}
	        }).start();
	        vbox.getChildren().add(trackInfo);
	        vbox.getChildren().add(chordInfo);
	        
	        setScene(new Scene(vbox));
	        show();
		}

		private int getTrackDuration() {
			return (int) (durationSlider.getValue() * 1000);
		}

		private int getInnerDelay() {
			return (int) (innerDelaySlider.getValue() * 1000);
		}

		private int getTrackFadeDuration() {
			return (int) (trackFadeIntervalSlider.getValue() * 1000);
		}

		private int getChordFadeDuration() {
			return (int) (chordFadeIntervalSlider.getValue() * 1000);
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
			Track track = new Track(getTrackDuration(), getInnerDelay(), getChordFadeDuration(), getTrackFadeDuration());
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