import javax.sound.sampled.*;

public class Player extends Track {
	public final static AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;
	public final static boolean BIG_ENDIAN = false;
	public final static int SAMPLE_RATE = 44100;
	public final static int SAMPLE_SIZE_IN_BITS = 16;
	public final static int CHANNELS = 1;
	public final static int FRAME_RATE = SAMPLE_RATE;
	public final static int FRAME_SIZE = SAMPLE_SIZE_IN_BITS / 8 * CHANNELS;
	public final static int BUFFER_CHUNK_SIZE = FRAME_RATE / 100;
	public final static int BUFFER_SIZE = BUFFER_CHUNK_SIZE * 10;

	private static SourceDataLine sourceDataLine;
	private Messages messages = Messages.getInstance();

	private boolean playing;
	private ChordPlayer chordPlayer;
	private long startTime;

	public Player(Track track) throws InitFailedPlayerException {
		super(track);
		if(sourceDataLine == null) {
			try {
				AudioFormat af = new AudioFormat(ENCODING, SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, FRAME_SIZE, FRAME_RATE, BIG_ENDIAN);
				DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, af);
			    sourceDataLine = (SourceDataLine) AudioSystem.getLine(lineInfo);
			    sourceDataLine.open(af, BUFFER_SIZE);
			} catch(LineUnavailableException ex) {
				throw new InitFailedPlayerException(messages.getMessage("dataLineNotAvailable"));
			}
		}
	}

	public static SourceDataLine getSourceDataLine() {
		return sourceDataLine;
	}

	public void play() throws IllegalActionPlayerException {
		if(isPlaying()) {
			throw new IllegalActionPlayerException();
		}
	    sourceDataLine.start();
		startTime = System.currentTimeMillis();
		playing = true;
		(new Thread() {
			public void run() {
				for(int i = 0; (i < chords.size()) && playing; i++) {
					Chord chord = chords.get(i);
					chordPlayer = new ChordPlayer(chord);
					chordPlayer.play(); //chord.play();
				}
				playing = false;
				sourceDataLine.stop();
			}
		}).start();
	}

	public void stop() throws IllegalActionPlayerException {
		if(!isPlaying()) {
			throw new IllegalActionPlayerException();
		}
		playing = false;
		chordPlayer.stop();
	}

	public double getCurrentPosition() throws IllegalActionPlayerException {
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
		return (playing || (sourceDataLine.isActive()));
	}
}