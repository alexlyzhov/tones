import javafx.application.Platform;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.event.*;
import javax.sound.sampled.LineUnavailableException; //check imports
import java.util.ArrayList;
import javafx.beans.value.*;
import javafx.geometry.*;

public class TonesVBox extends VBox {
	private TextField frequencyField; //check fields
	private ListView<Double> frequencyList;
	private Slider durationSlider, innerDelaySlider, chordFadeDurationSlider, trackFadeDurationSlider;
	private RadioButton harmonicSeriesRadioButton, equalTemperamentRadioButton;
	private ProgressBar playbackProgressBar;
	private final double initialTrackDuration = 2D;

	public TonesVBox(Player player) {
		setAlignment(Pos.CENTER);
		setSpacing(5);
        setPadding(new Insets(10, 10, 10, 10));

		// ToggleGroup toggleGroup = new ToggleGroup();
		// harmonicSeriesRadioButton = new RadioButton("Harmonic series");
		// equalTemperamentRadioButton = new RadioButton("Equal temperament");
		// harmonicSeriesRadioButton.setToggleGroup(toggleGroup);
		// equalTemperamentRadioButton.setToggleGroup(toggleGroup);
		// getChildren().add(harmonicSeriesRadioButton);
		// getChildren().add(equalTemperamentRadioButton);
		// harmonicSeriesRadioButton.fire();

		HBox trackCompositionHBox = new HBox();

		VBox frequencyFieldVBox = new VBox();

		frequencyField = new TextField();
		frequencyField.setPromptText("Frequencies");
		frequencyFieldVBox.getChildren().add(frequencyField);

		Button frequencyListAddButton = new Button("Add");
		frequencyListAddButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
            	try {
            		double freq = Double.parseDouble(frequencyField.getText());
            		frequencyList.getItems().add(freq);
            	} catch(NumberFormatException ex) {ex.printStackTrace();}
            }
        });
        frequencyFieldVBox.getChildren().add(frequencyListAddButton);

		Button frequencyListRemoveButton = new Button("Remove");
		frequencyListRemoveButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
            	Double frequencyToRemove = frequencyList.getFocusModel().getFocusedItem();
            	if(frequencyToRemove != null) {
            		frequencyList.getItems().remove(frequencyToRemove);
            	}
            }
        });
        frequencyFieldVBox.getChildren().add(frequencyListRemoveButton);

		Button createChordButton = new Button("Create chord");
		createChordButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
            	
            }
        });
        frequencyFieldVBox.getChildren().add(createChordButton);

        trackCompositionHBox.getChildren().add(frequencyFieldVBox);

		frequencyList = new ListView<Double>();
		frequencyList.setPrefHeight(120);
		frequencyList.setPrefWidth(80);
		trackCompositionHBox.getChildren().add(frequencyList);

		getChildren().add(trackCompositionHBox);

        Separator separator2 = new Separator();
        getChildren().add(separator2);

		GridPane slidersGrid = new GridPane();
		getChildren().add(slidersGrid);
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
		durationSlider.setSnapToTicks(true);
		final Label durationLabel = new Label(String.format("%.1f seconds", durationSlider.getValue()));
		// final Label durationLabel = new Label(String.format("%4.1s seconds", durationSlider.getValue()));
		durationSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
				durationLabel.setText(String.format("%.1f seconds", newValue));
				double doubleValue = newValue.doubleValue();
				innerDelaySlider.setMax(doubleValue);
				trackFadeDurationSlider.setMax(doubleValue);
				chordFadeDurationSlider.setMax(doubleValue);
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
		innerDelaySlider.setSnapToTicks(true);
		final Label innerDelayLabel = new Label(String.format("%.1f seconds", innerDelaySlider.getValue()));
		innerDelaySlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
				innerDelayLabel.setText(String.format("%.1f seconds", newValue));
			}
		});
		slidersGrid.add(new Label("Inner delay: "), 1, 2);
		slidersGrid.add(innerDelaySlider, 2, 2);
		slidersGrid.add(innerDelayLabel, 3, 2);

		trackFadeDurationSlider = new Slider();
		trackFadeDurationSlider.setMin(0);
		trackFadeDurationSlider.setMax(initialTrackDuration);
		trackFadeDurationSlider.setValue(0);
		trackFadeDurationSlider.setMajorTickUnit(0.1);
		trackFadeDurationSlider.setMinorTickCount(0);
		trackFadeDurationSlider.setBlockIncrement(0.1);
		trackFadeDurationSlider.setShowTickMarks(false);
		trackFadeDurationSlider.setShowTickLabels(false);
		trackFadeDurationSlider.setSnapToTicks(true);
		final Label trackFadeIntervalLabel = new Label(String.format("%.1f seconds", trackFadeDurationSlider.getValue()));
		trackFadeDurationSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
				trackFadeIntervalLabel.setText(String.format("%.1f seconds", newValue));
			}
		});
		slidersGrid.add(new Label("Track fade interval: "), 1, 3);
		slidersGrid.add(trackFadeDurationSlider, 2, 3);
		slidersGrid.add(trackFadeIntervalLabel, 3, 3);

		chordFadeDurationSlider = new Slider();
		chordFadeDurationSlider.setMin(0);
		chordFadeDurationSlider.setMax(initialTrackDuration);
		chordFadeDurationSlider.setValue(0);
		chordFadeDurationSlider.setMajorTickUnit(0.1);
		chordFadeDurationSlider.setMinorTickCount(0);
		chordFadeDurationSlider.setBlockIncrement(0.1);
		chordFadeDurationSlider.setShowTickMarks(false);
		chordFadeDurationSlider.setShowTickLabels(false);
		chordFadeDurationSlider.setSnapToTicks(true);
		final Label chordFadeIntervalLabel = new Label(String.format("%.1f seconds", chordFadeDurationSlider.getValue()));
		chordFadeDurationSlider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
				chordFadeIntervalLabel.setText(String.format("%.1f seconds", newValue));
			}
		});
		slidersGrid.add(new Label("Chord fade interval: "), 1, 4);
		slidersGrid.add(chordFadeDurationSlider, 2, 4);
		slidersGrid.add(chordFadeIntervalLabel, 3, 4);

        getChildren().add(new PlayingPane(player));
	}

	private int getTrackDuration() { //move these methods to controls pane
		return (int) (durationSlider.getValue() * 1000);
	}

	private int getInnerDelay() {
		return (int) (innerDelaySlider.getValue() * 1000);
	}

	private int getTrackFadeDuration() {
		return (int) (trackFadeDurationSlider.getValue() * 1000);
	}

	private int getChordFadeDuration() {
		return (int) (chordFadeDurationSlider.getValue() * 1000);
	}

	private Track createTrack() throws InvalidTrackDataException { //remove
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
						if(token.endsWith("s")) { //process count operations in a separate frame; use and refactor the ToneSystem class
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
		Track track = new Track(getTrackDuration(), getInnerDelay(), getChordFadeDuration(), getTrackFadeDuration(), chordsList);
		if(track.size() == 0) {
			throw new InvalidTrackDataException("No track data was found");
		}
		return track;
	}

	private class InvalidTrackDataException extends Exception {
		public InvalidTrackDataException(String message) {
			super(message);
		}

		public void showDialog() {
			printStackTrace(); //show a new Dialog here
		}
	}

	private class PlayingPane extends VBox {
		final Player player;
		final Label trackInfo, chordInfo;
		private final int infoUpdateDelay = 50;

		public PlayingPane(Player player) {
			this.player = player;

	        HBox buttonsHBox = new HBox();
			Button playButton = new Button("Play");
			//style buttons and pane; create new pause button
			// playButton.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
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
	        getChildren().add(buttonsHBox);

	        trackInfo = new Label(""); //progress bar and more nice data displaying
	        chordInfo = new Label("");
	        getChildren().addAll(trackInfo, chordInfo);
		}

		private void playButtonAction() {
			try {
				Track newTrack = createTrack();
				player.play(newTrack);
				startInfoUpdateCycle();
			} catch(InvalidTrackDataException ex) {
				ex.showDialog();
			} catch(IllegalActionPlayerException ex) {}
		}

		private void stopButtonAction() {
			try {
				player.stop();
			} catch(IllegalActionPlayerException ex) {}
		}

		private void startInfoUpdateCycle() {
			(new Thread() {
				public void run() {
					while(player.isPlaying()) {
						Platform.runLater(new InfoUpdateRunnable());
						try {
							Thread.sleep(50);
						} catch(InterruptedException ex) {ex.printStackTrace();}
					}
				}
			}).start();
		}

		private class InfoUpdateRunnable implements Runnable {
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
		}
	}
}