import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.beans.value.*;

import javafx.application.Platform;

public class PlayingPane extends VBox {
	final ComposingPane composingPane;
	final Player player;
	final Label trackInfo, chordInfo;
	private final int infoUpdateDelay = 50;

	public PlayingPane(ComposingPane composingPane, Player player) {
		setSpacing(5);
		this.composingPane = composingPane;
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
			Track newTrack = composingPane.createTrack();
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