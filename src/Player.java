import javax.sound.sampled.*;

public class Player {
	public final static int BUFFER_CHUNK_SIZE = Sound.FRAME_RATE / 100; //place constants in chordplayer
	public final static int BUFFER_SIZE = BUFFER_CHUNK_SIZE * 10;
	private SourceDataLine sourceDataLine = null;
	private ChordPlayer chordPlayer = null;
	private Thread playingThread = null;
	private boolean halting = false;

	public Player() throws LineUnavailableException {
		AudioFormat af = Sound.getAudioFormat();
		DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, af);
	    sourceDataLine = (SourceDataLine) AudioSystem.getLine(lineInfo);
	    sourceDataLine.open(af, BUFFER_SIZE);
	}

	public void play(final Track track) throws IllegalPlayerActionException {
		if(isPlaying()) {
			throw new IllegalPlayerActionException("The player has already started playback");
		}
		final int chordDuration = (int) (track.getDuration() / track.size());
		playingThread = new Thread() {
			public void run() {
				sourceDataLine.start();
				for(int i = 0; i < track.size(); i++) {
					Chord chord = track.getChord(i);
					chordPlayer = new ChordPlayer(sourceDataLine, chord, chordDuration, 50, 50, 200); //create new internal Thread class
					chordPlayer.play();
					if(halting) {
						halting = false;
						break;
					}
				}
				sourceDataLine.stop();
			}
		};
		playingThread.start();
	}

	public void stop() throws IllegalPlayerActionException {
		if(!isPlaying()) {
			throw new IllegalPlayerActionException("The player has already stopped playback");
		}
		halting = true;
		chordPlayer.fadeOut(); //this should stop all playing
	}

	private boolean isPlaying() { //check sourceDataLine or just variable playing here
		// return (sourceDataLine.isActive()); //isAlive()
		if((playingThread == null) || (!playingThread.isAlive())) {
			return false;
		}
		return true;
	}
}