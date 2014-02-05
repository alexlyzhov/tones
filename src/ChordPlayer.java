import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.ArrayList;

public class ChordPlayer implements Runnable {
	private Chord chord;
	private Thread playingThread;
	private boolean playing;
	private byte[] byteData;
	private Clip clip;

	public ChordPlayer(Chord chord) {
		this.chord = chord;
		byteData = getByteData(chord.getShortData());

		try {
			clip = AudioSystem.getClip();
		} catch(LineUnavailableException ex) {ex.printStackTrace();}
	}

	private byte[] getByteData(short[] shortData) {
		byte[] byteData = new byte[shortData.length * Player.FRAME_SIZE];
		ByteOrder byteOrder = Player.BIG_ENDIAN ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
		ByteBuffer buffer = ByteBuffer.wrap(byteData).order(byteOrder);
		for(int i = 0; i < shortData.length; i++) {
			for(int channel = 0; channel < Player.CHANNELS; channel++) {
				buffer.putShort(shortData[i]);
			}
		}
		buffer.flip();
		return byteData;
	}

	public void play() {
		playing = true;
		playingThread = new Thread(this);
		playingThread.start();
		try {
			playingThread.join();
		} catch(InterruptedException ex) {ex.printStackTrace();}
	}

	public void stop() {
		playing = false;
		playingThread.interrupt();
	}

	public void run() {
		sleep(chord.getPreDelay());
		if(!playing) {
			return;
		}
		
		try {
			clip.open(Player.AUDIO_FORMAT, byteData, 0, byteData.length);
			clip.setFramePosition(0);
			clip.start();
			sleep(clip.getMicrosecondLength() / 1000);
		} catch(LineUnavailableException ex) {ex.printStackTrace();}

		if(!playing) {
			return;
		}
		sleep(chord.getPostDelay());
	}

	private void sleep(long duration) {
		try {
			Thread.sleep(duration);
		} catch(InterruptedException ex) {}
	}

	public boolean playingIsActive() {
		return clip.isRunning();
	}

	public Chord getChord() {
		return chord;
	}
}