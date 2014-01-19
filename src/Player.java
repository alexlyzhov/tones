import javax.sound.sampled.*;

public class Player {
	public final static int BUFFER_CHUNK_SIZE = Sound.FRAME_RATE / 100;
	public final static int BUFFER_SIZE = BUFFER_CHUNK_SIZE * 10;
	private SourceDataLine sourceDataLine = null;
	private PlayerRunnable playerRunnable = null;
	private Thread playerThread = null;

	public Player() throws LineUnavailableException {
		AudioFormat af = Sound.getAudioFormat();
		DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, af);
	    sourceDataLine = (SourceDataLine) AudioSystem.getLine(lineInfo);
	    sourceDataLine.open(af, BUFFER_SIZE);
		sourceDataLine.start();
	}

	public void play(Track track) throws IllegalPlayerActionException {
		if(isPlaying()) {
			throw new IllegalPlayerActionException("The player has already started playback");
		}
		playerRunnable = new PlayerRunnable(sourceDataLine, track);
		playerThread = new Thread(playerRunnable);
		playerThread.start();
	}

	public void stop() throws IllegalPlayerActionException {
		if(!isPlaying()) {
			throw new IllegalPlayerActionException("The player has already stopped playback");
		}
		playerRunnable.fadeOut();
	}

	private boolean isPlaying() {
		if((playerThread == null) || (!playerThread.isAlive())) {
			return false;
		}
		return true;
	}
}