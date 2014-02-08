import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.*;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.*;
import javafx.beans.value.*;

public class ComposingPane extends VBox {
	private final TextField frequencyField = new TextField();
	private MillisecondsSlider durationSlider, innerDelaySlider, chordFadeDurationSlider, trackFadeDurationSlider;
	private ListView<BlankChord> chordsListView;
	private Messages messages = Messages.getInstance();

	public ComposingPane() {
		setSpacing(5);
		ChordsComposingPane chordsComposingPane = new ChordsComposingPane();
		SlidersPane slidersPane = new SlidersPane();
		getChildren().addAll(chordsComposingPane, slidersPane);
	}

	public Track createTrack() throws InvalidDataException {
		List<BlankChord> chordsList = chordsListView.getItems();
		if(chordsList.isEmpty()) {
			throw new InvalidDataException(messages.getMessage("emptyChordList"));
		}
		int duration = durationSlider.getMillisecondsValue();
		int innerDelay = innerDelaySlider.getMillisecondsValue();
		int chordFade = chordFadeDurationSlider.getMillisecondsValue();
		int trackFade = trackFadeDurationSlider.getMillisecondsValue();
		Track track = new Track(chordsList, duration, innerDelay, chordFade, trackFade);
		return track;
	}

	public TextField getFrequencyField() {
		return frequencyField;
	}

	private class ChordsComposingPane extends HBox {
		private ListView<Frequency> frequenciesListView; //create a new abstract ListView class with getAddButton and getRemoveButton methods

		public ChordsComposingPane() {
			setSpacing(5);

			VBox frequencyFieldVBox = new VBox();
			frequencyFieldVBox.setSpacing(3);

			frequencyField.setPromptText(messages.getMessage("newFrequency"));
			frequencyFieldVBox.getChildren().add(frequencyField);

			Button frequencyListAddButton = new Button(messages.getMessage("addFrequency"));
			frequencyListAddButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent event) {
	            	try {
	            		double doubleValue = Double.parseDouble(frequencyField.getText());
	            		Frequency frequency = new Frequency(doubleValue);
	            		if(frequenciesListView.getItems().contains(frequency)) {
	            			throw new InvalidDataException(messages.getMessage("freqAlreadyAdded"));
	            		}
	            		frequenciesListView.getItems().add(frequency);
	            		frequencyField.clear();
	            	} catch(NumberFormatException ex) {
	            	} catch(InvalidDataException ex) {}
	            }
	        });
	        frequencyFieldVBox.getChildren().add(frequencyListAddButton);

			Button frequencyListRemoveButton = new Button(messages.getMessage("removeFrequency"));
			frequencyListRemoveButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent event) {
	            	Frequency frequencyToRemove = frequenciesListView.getFocusModel().getFocusedItem();
	            	if(frequencyToRemove != null) {
	            		frequenciesListView.getItems().remove(frequencyToRemove);
	            	}
	            }
	        });
	        frequencyFieldVBox.getChildren().add(frequencyListRemoveButton);

			frequenciesListView = new ListView<Frequency>();
			frequenciesListView.setPrefHeight(120);
			frequenciesListView.setPrefWidth(80);

			HBox frequenciesHBox = new HBox();
			frequenciesHBox.setSpacing(5);
			frequenciesHBox.getChildren().addAll(frequencyFieldVBox, frequenciesListView);
			BorderPane frequenciesBorderPane = new BorderPane();
			Label frequenciesLabel = new Label(messages.getMessage("frequenciesInChord"));
			frequenciesLabel.setStyle("-fx-font-weight: bold;");
			frequenciesBorderPane.setCenter(frequenciesHBox);
			frequenciesBorderPane.setTop(frequenciesLabel);
			frequenciesBorderPane.setAlignment(frequenciesLabel, Pos.CENTER);

			Button createChordButton = new Button(messages.getMessage("createChord"));
			createChordButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent event) {
	            	if(!frequenciesListView.getItems().isEmpty()) {
	            		BlankChord blankChord = new BlankChord(new ArrayList<Frequency>(frequenciesListView.getItems()));
	            		chordsListView.getItems().add(blankChord);
	            		frequenciesListView.getItems().clear();
	            	}
	            }
	        });

			Button chordsListRemoveButton = new Button(messages.getMessage("removeChord"));
			chordsListRemoveButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent event) {
	            	BlankChord chordToRemove = chordsListView.getFocusModel().getFocusedItem();
	            	if(chordToRemove != null) {
	            		chordsListView.getItems().remove(chordToRemove);
	            	}
	            }
	        });

			VBox chordsVBox = new VBox();
			chordsVBox.setSpacing(3);
			chordsVBox.getChildren().addAll(createChordButton, chordsListRemoveButton);

			chordsListView = new ListView<BlankChord>();
			chordsListView.setPrefHeight(120);
			chordsListView.setPrefWidth(180);

			HBox chordsHBox = new HBox();
			chordsHBox.setSpacing(5);
			chordsHBox.getChildren().addAll(chordsVBox, chordsListView);
			BorderPane chordsBorderPane = new BorderPane();
			Label chordsLabel = new Label(messages.getMessage("chordsInTrack"));
			chordsLabel.setStyle("-fx-font-weight: bold;");
			chordsBorderPane.setCenter(chordsHBox);
			chordsBorderPane.setTop(chordsLabel);
			chordsBorderPane.setAlignment(chordsLabel, Pos.CENTER);

	        getChildren().addAll(frequenciesBorderPane, chordsBorderPane);
		}
	}

	private class SlidersPane extends GridPane {
		private final double initialTrackDuration = 2D;

		public SlidersPane() {
			setVgap(5);
			setAlignment(Pos.CENTER);

			durationSlider = new MillisecondsSlider(0.1, 30, initialTrackDuration);
			durationSlider.valueProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
					double doubleValue = newValue.doubleValue();
					innerDelaySlider.setMax(doubleValue);
					trackFadeDurationSlider.setMax(doubleValue);
					chordFadeDurationSlider.setMax(doubleValue);
				}
			});
			add(new Label(messages.getMessage("trackDuration") + ": "), 1, 1);
			add(durationSlider, 2, 1);
			add(durationSlider.getNewSecondsLabel(), 3, 1);

			innerDelaySlider = new MillisecondsSlider(0, initialTrackDuration, 0);
			add(new Label(messages.getMessage("innerDelay") + ": "), 1, 2);
			add(innerDelaySlider, 2, 2);
			add(innerDelaySlider.getNewSecondsLabel(), 3, 2);

			trackFadeDurationSlider = new MillisecondsSlider(0, initialTrackDuration, 0);
			add(new Label(messages.getMessage("trackFadeDuration") + ": "), 1, 3);
			add(trackFadeDurationSlider, 2, 3);
			add(trackFadeDurationSlider.getNewSecondsLabel(), 3, 3);

			chordFadeDurationSlider = new MillisecondsSlider(0, initialTrackDuration, 0);
			add(new Label(messages.getMessage("chordFadeDuration") + ": "), 1, 4);
			add(chordFadeDurationSlider, 2, 4);
			add(chordFadeDurationSlider.getNewSecondsLabel(), 3, 4);
		}
	}

	private class MillisecondsSlider extends Slider {
		public MillisecondsSlider(double min, double max, double initialValue) {
			setMin(min);
			setMax(max);
			setValue(initialValue);
			setMajorTickUnit(0.1);
			setMinorTickCount(0);
			setBlockIncrement(0.1);
			setShowTickMarks(false);
			setShowTickLabels(false);
			setSnapToTicks(true);
		}

		public int getMillisecondsValue() {
			return (int) (getValue() * 1000);
		}

		public Label getNewSecondsLabel() {
			final Label label = new Label(getSecondsLabelValue());
			valueProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
					label.setText(getSecondsLabelValue());
				}
			});
			return label;
		}

		private String getSecondsLabelValue() {
			return String.format("%.1f", getValue()) + " " + messages.getMessage("secondsGenitive");
		}
	}
}