import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.event.*;

public class CalculationsPane extends VBox {
	private ToneSystemHBox toneSystemHBox;
	private CalculationsHBox calculationsHBox;
	private Messages messages = Messages.getInstance();

	public CalculationsPane(ComposingPane composingPane) {
		setSpacing(5);

		toneSystemHBox = new ToneSystemHBox();
		calculationsHBox = new CalculationsHBox(toneSystemHBox, composingPane.getFrequencyField());
		getChildren().addAll(toneSystemHBox, calculationsHBox);
	}

	private class ToneSystemHBox extends HBox {
		private ToggleButton harmonicSeriesToggleButton, equalTemperamentToggleButton;

		public ToneSystemHBox() {
			setSpacing(5);
			setAlignment(Pos.CENTER);

			ToggleGroup toneSystemToggleGroup = new ToggleGroup();
			harmonicSeriesToggleButton = new ToggleButton(messages.getMessage("harmonicSeries"));
			equalTemperamentToggleButton = new ToggleButton(messages.getMessage("equalTemperament"));
			harmonicSeriesToggleButton.setToggleGroup(toneSystemToggleGroup);
			equalTemperamentToggleButton.setToggleGroup(toneSystemToggleGroup);
			harmonicSeriesToggleButton.fire();
			getChildren().addAll(harmonicSeriesToggleButton, equalTemperamentToggleButton);
		}

		public ToneSystem getToneSystem() {
			return harmonicSeriesToggleButton.isSelected() ? ToneSystem.HARMONIC : ToneSystem.TEMPERED;
		}
	}

	private class CalculationsHBox extends HBox {
		private TextField resultingFreqField;
		private ToneSystemHBox toneSystemHBox;

		private TextField originalFreqField;
		private ToggleButton plusToggleButton, minusToggleButton;
		private TextField multiplierField;
		private ChoiceBox intervalChoiceBox;

		@SuppressWarnings("unchecked")
		public CalculationsHBox(ToneSystemHBox toneSystemHBox, TextField resultingFreqField) {
			this.resultingFreqField = resultingFreqField;
			this.toneSystemHBox = toneSystemHBox;
			setSpacing(5);

			originalFreqField = new TextField();
			originalFreqField.setPromptText(messages.getMessage("initialFrequency"));
			getChildren().add(originalFreqField);

			ToggleGroup signToggleGroup = new ToggleGroup();
			plusToggleButton = new ToggleButton("+");
			plusToggleButton.setStyle("-fx-background-radius: 0");
			minusToggleButton = new ToggleButton("-");
			minusToggleButton.setStyle("-fx-background-radius: 0");
			plusToggleButton.setToggleGroup(signToggleGroup);
			minusToggleButton.setToggleGroup(signToggleGroup);
			plusToggleButton.fire();
			HBox signHBox = new HBox();
			signHBox.getChildren().addAll(plusToggleButton, minusToggleButton);
			getChildren().add(signHBox);

			multiplierField = new TextField();
			multiplierField.setText("1");
			multiplierField.setPrefWidth(40);
			getChildren().add(multiplierField);

			intervalChoiceBox = new ChoiceBox<Interval>();
			intervalChoiceBox.getItems().addAll((Object[]) Interval.values());
			intervalChoiceBox.setValue(Interval.MINOR_SECOND);
			getChildren().add(intervalChoiceBox);

			Button calculateButton = new Button(messages.getMessage("calculate"));
			calculateButton.setOnAction(new EventHandler<ActionEvent>() {
	            public void handle(ActionEvent event) {
	            	calculate();
	            }
	        });
			getChildren().add(calculateButton);
		}

		private void calculate() {
			try {
				double originalFreq = Double.parseDouble(originalFreqField.getText());
				ToneSystem toneSystem = toneSystemHBox.getToneSystem();
				int multiplier = getMultiplier();
				double resultingFreq = toneSystem.calculateFrequency(originalFreq, sumCalculation(), multiplier, getInterval());
				setResultingFrequency(resultingFreq);
			} catch(NumberFormatException ex) {}
		}

		private boolean sumCalculation() {
			return plusToggleButton.isSelected();
		}

		private Interval getInterval() {
			return (Interval) intervalChoiceBox.getValue();
		}

		private int getMultiplier() {
			return Integer.parseInt(multiplierField.getText());
		}

		private void setResultingFrequency(double freq) {
			resultingFreqField.setText(String.format("%.3f", freq));
		}
	}
}