import javax.sound.sampled.*;

public class TrackPlayer implements Runnable {
	private SourceDataLine sourceDataLine = null;
	private Track track = null;
	private ChordPlayer chordPlayer = null;
	private boolean finishPlaying = false;
	private long startTime;

	public TrackPlayer(SourceDataLine sourceDataLine, Track track) {
		this.sourceDataLine = sourceDataLine;
		this.track = track;
	}

	public void run() {
		startTime = System.currentTimeMillis();
		sourceDataLine.start();
		for(int i = 0; (i < track.size()) && (!finishPlaying); i++) {
			Chord chord = track.getChord(i);
			chordPlayer = new ChordPlayer(sourceDataLine, chord);
			chordPlayer.play();
		}
		sourceDataLine.stop();
	}

	public void stop() {
		finishPlaying = true;
		chordPlayer.stop();
	}

	public double getTrackDuration() {
		return (track.getDuration() / 1000d);
	}

	public double getTrackPosition() {
		return (double) ((System.currentTimeMillis() - startTime) / 1000d);
	}

	public Chord getCurrentChord() {
		return chordPlayer.getChord();
	}
}