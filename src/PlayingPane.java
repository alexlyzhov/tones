import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.beans.value.*;

import javafx.application.Platform;

public class PlayingPane extends VBox {
	final ComposingPane composingPane;
	final Label trackInfo, chordInfo;
	private Messages messages = Messages.getInstance();
	private Player player;
	private final int infoUpdateDelay = 50;

	public PlayingPane(ComposingPane composingPane) {
		setSpacing(5);
		this.composingPane = composingPane;

        HBox buttonsHBox = new HBox();
		Button playButton = new Button(messages.getMessage("play"));
		//style buttons and pane; create new pause button
		// playButton.setStyle("-fx-font: 22 arial; -fx-base: #b6e7c9;");
		playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
            	playButtonAction();
            }
        });
		Button stopButton = new Button(messages.getMessage("stop"));
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
			Track newTrack = composingPane.createTrack();
			player = new Player(newTrack);
			player.play();
			startInfoUpdateCycle();
		} catch(DialogException ex) {
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
				Platform.runLater(new InfoClearRunnable());
			}
		}).start();
	}

	private class InfoUpdateRunnable implements Runnable {
		public void run() {
			String trackInfoString = "";
			try {
				String trackPositionString = String.format("%.1f", player.getCurrentPosition());
				String trackDurationString = String.format("%.1f", player.getSecondsDuration());
				trackInfoString = trackPositionString + " / " + trackDurationString;
			} catch(IllegalActionPlayerException ex) {}
			trackInfo.setText(trackInfoString);
			String chordInfoString = "";
			try {
				chordInfoString = messages.getMessage("currentChord") + ": ";
				Chord currentChord = player.getCurrentChord();
				if(currentChord != null) {
					chordInfoString += currentChord.toString();
				} else {
					chordInfoString += messages.getMessage("silence");
				}
			} catch(IllegalActionPlayerException ex) {}
			chordInfo.setText(chordInfoString);
		}
	}

	private class InfoClearRunnable implements Runnable {
		public void run() {
			trackInfo.setText("");
			chordInfo.setText("");
		}
	}
}