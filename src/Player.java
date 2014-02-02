import javax.sound.sampled.*;

public class Player {
	public final static AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;
	public final static int SAMPLE_RATE = 44100;
	public final static int SAMPLE_SIZE_IN_BITS = 16;
	public final static int CHANNELS = 1;
	public final static int FRAME_SIZE = SAMPLE_SIZE_IN_BITS / 8 * CHANNELS;
	public final static int FRAME_RATE = SAMPLE_RATE;
	public final static boolean BIG_ENDIAN = false;
	public final static int BUFFER_CHUNK_SIZE = FRAME_RATE / 100; //move these buffer things to freq or chord player class
	public final static int BUFFER_SIZE = BUFFER_CHUNK_SIZE * 10;
	private SourceDataLine sourceDataLine = null;
	private TrackPlayer trackPlayer = null;
	private Messages messages = Messages.getInstance(); //there should not be message-interaction with user

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
		trackPlayer = new TrackPlayer(sourceDataLine, track);
		Thread trackPlayerThread = new Thread(trackPlayer);
		trackPlayerThread.start();
	}

	public void stop() throws IllegalActionPlayerException {
		if(!isPlaying()) {
			throw new IllegalActionPlayerException();
		}
		trackPlayer.stop();
	}

	public double getTrackDuration() throws IllegalActionPlayerException {
		if(!isPlaying()) {
			throw new IllegalActionPlayerException();
		}
		return trackPlayer.getTrackDuration();
	}

	public double getTrackPosition() throws IllegalActionPlayerException {
		if(!isPlaying()) {
			throw new IllegalActionPlayerException();
		}
		return trackPlayer.getTrackPosition();
	}

	public Chord getCurrentChord() throws IllegalActionPlayerException {
		if(!isPlaying()) {
			throw new IllegalActionPlayerException();
		}
		return trackPlayer.getCurrentChord();
	}

	public boolean isPlaying() {
		return (sourceDataLine.isActive());
	}
}