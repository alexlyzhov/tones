import javax.sound.sampled.*;

public class Player {
	public final static AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;
	public final static boolean BIG_ENDIAN = false;
	public final static int SAMPLE_RATE = 44100;
	public final static int SAMPLE_SIZE_IN_BITS = 16;
	public final static int CHANNELS = 1;
	public final static int FRAME_RATE = SAMPLE_RATE;
	public final static int FRAME_SIZE = SAMPLE_SIZE_IN_BITS / 8 * CHANNELS;
	public final static int BUFFER_CHUNK_SIZE = FRAME_RATE / 100;
	public final static int BUFFER_SIZE = BUFFER_CHUNK_SIZE * 10;

	private Messages messages = Messages.getInstance();
	private SourceDataLine sourceDataLine;
	private Track track;
	private ChordPlayer chordPlayer;
	private boolean finishPlaying;
	private long startTime;

	public Player() throws InitFailedPlayerException {
		try {
			AudioFormat af = new AudioFormat(ENCODING, SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, FRAME_SIZE, FRAME_RATE, BIG_ENDIAN);
			DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, af);
		    sourceDataLine = (SourceDataLine) AudioSystem.getLine(lineInfo);
		    sourceDataLine.open(af, BUFFER_SIZE);
		} catch(LineUnavailableException ex) {
			throw new InitFailedPlayerException(messages.getMessage("dataLineNotAvailable"));
		}
	}

	public void play(final Track track) throws IllegalActionPlayerException {
		if(isPlaying()) {
			throw new IllegalActionPlayerException();
		}
		this.track = track;
		PlayerRunnable playerRunnable = new PlayerRunnable();
		Thread playerThread = new Thread(playerRunnable);
		playerThread.start();
	}

	private class PlayerRunnable implements Runnable {
		public void run() {
			startTime = System.currentTimeMillis();
			sourceDataLine.start();
			finishPlaying = false;
			for(int i = 0; (i < track.size()) && (!finishPlaying); i++) {
				Chord chord = track.getChord(i);
				chordPlayer = new ChordPlayer(sourceDataLine, chord);
				chordPlayer.play();
			}
			sourceDataLine.stop();
		}
	}

	public void stop() throws IllegalActionPlayerException {
		if(!isPlaying()) {
			throw new IllegalActionPlayerException();
		}
		finishPlaying = true;
		chordPlayer.stop();
	}

	public double getTrackDuration() throws IllegalActionPlayerException {
		if(!isPlaying()) {
			throw new IllegalActionPlayerException();
		}
		return (track.getDuration() / 1000d);
	}

	public double getTrackPosition() throws IllegalActionPlayerException {
		if(!isPlaying()) {
			throw new IllegalActionPlayerException();
		}
		return (System.currentTimeMillis() - startTime) / 1000d;
	}

	public Chord getCurrentChord() throws IllegalActionPlayerException {
		if(!isPlaying()) {
			throw new IllegalActionPlayerException();
		}
		return chordPlayer.getChord();
	}

	public boolean isPlaying() {
		return (sourceDataLine.isActive());
	}
}