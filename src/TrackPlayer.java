import javax.sound.sampled.*;

public class TrackPlayer implements Runnable {
	private SourceDataLine sourceDataLine = null;
	private Track track = null;
	private ChordPlayer chordPlayer = null;
	private boolean finishPlaying = false;

	public TrackPlayer(SourceDataLine sourceDataLine, Track track) {
		this.sourceDataLine = sourceDataLine;
		this.track = track;
	}

	public void run() {
		sourceDataLine.start();
		for(int i = 0; (i < track.size()) && (!finishPlaying); i++) {
			Chord chord = track.getChord(i);
			chordPlayer = new ChordPlayer(sourceDataLine, chord);
			chordPlayer.play();
		}
		sourceDataLine.stop();
	}

	public void fadeOut() {
		finishPlaying = true;
		chordPlayer.fadeOut();
	}
}