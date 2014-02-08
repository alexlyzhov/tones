import javax.sound.sampled.*;

public class Player extends Track {
	public final static AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;
	public final static boolean BIG_ENDIAN = false;
	public final static int SAMPLE_RATE = 44100;
	public final static int SAMPLE_SIZE_IN_BITS = 16;
	public final static int CHANNELS = 1;
	public final static int FRAME_RATE = SAMPLE_RATE;
	public final static int FRAME_SIZE = SAMPLE_SIZE_IN_BITS / 8 * CHANNELS;
	public final static AudioFormat AUDIO_FORMAT = new AudioFormat(ENCODING, SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, FRAME_SIZE, FRAME_RATE, BIG_ENDIAN);

	private Messages messages = Messages.getInstance();
	private boolean active;
	private ChordPlayer chordPlayer;
	private long startTime;

	public Player(Track track) throws InitFailedPlayerException {
		super(track);
	}

	public void play() throws IllegalActionPlayerException {
			if(isActive()) {
				throw new IllegalActionPlayerException();
			}
			startTime = System.currentTimeMillis();
			active = true;
			(new Thread() {
				public void run() {
					for(int i = 0; (i < chords.size()) && active; i++) {
						Chord chord = chords.get(i);
						chordPlayer = new ChordPlayer(chord);
						chordPlayer.play();
					}
					active = false;
				}
			}).start();
	}

	public void stop() throws IllegalActionPlayerException {
		if(!isActive()) {
			throw new IllegalActionPlayerException();
		}
		active = false;
		chordPlayer.stop();
	}

	public double getCurrentPosition() throws IllegalActionPlayerException {
		if(!isActive()) {
			throw new IllegalActionPlayerException();
		}
		return (System.currentTimeMillis() - startTime) / 1000d;
	}

	public Chord getCurrentChord() throws IllegalActionPlayerException {
		if(!isActive()) {
			throw new IllegalActionPlayerException();
		}
		if(chordPlayer == null) return null;
		return chordPlayer.playingIsActive() ? chordPlayer.getChord() : null;
	}

	public boolean isActive() {
		return active;
	}
}