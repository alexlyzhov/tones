import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Player extends Thread {
	private static SourceDataLine sourceDataLine = null;
	private final static int BUFFER_CHUNK_SIZE = Sound.FRAME_RATE / 100;
	private final static int BUFFER_SIZE = BUFFER_CHUNK_SIZE * 10;
	private boolean running;
	private float freqs[];
	private Volume volume;
	private long frames;

	public static void init() {
		AudioFormat af = new AudioFormat(Sound.ENCODING,
										 Sound.SAMPLE_RATE,
										 Sound.SAMPLE_SIZE_IN_BITS,
										 Sound.CHANNELS,
										 Sound.FRAME_SIZE,
										 Sound.FRAME_RATE,
										 Sound.BIG_ENDIAN);
		try {
			DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, af);
		    sourceDataLine = (SourceDataLine) AudioSystem.getLine(lineInfo);
		    sourceDataLine.open(af, BUFFER_SIZE);
			sourceDataLine.start();
		} catch (LineUnavailableException ex) {
		    System.out.println("The line is unavailable");
		}		
	}

	public Player(Track track) {
		this.freqs = track.getFreqs();
		start();
	}

	public void stopSound() {
		if(running) {
			volume.fadeOut(frames);
		}
	}

	private void destroySound() {
		sourceDataLine.flush();
		sourceDataLine.stop();
	}

	public void run() {
		frames = 0L;
		volume = new Volume();
		byte[] data = null;
		boolean init = true;
		running = true;
		while(running) {
			if((init) || (sourceDataLine.available() >= data.length)) {
				data = getData(freqs, volume, frames, BUFFER_CHUNK_SIZE);
				frames += BUFFER_CHUNK_SIZE;
				sourceDataLine.write(data, 0, data.length);
				init = false;
			}
			running = volume.getRunning();
		}
		destroySound();
	}

	private byte[] getData(float[] frequencies, Volume volume, long frames, int newFrames) {
		short[] data = new short[newFrames];
		for(float frequency: frequencies) {
			short[] currentData = getFrequencyData(frequency, volume, frames, newFrames);
			for(int i = 0; i < currentData.length; i++) {
				data[i] += (currentData[i] / frequencies.length);
			}
		}
		
		byte[] finalData = new byte[newFrames * Sound.FRAME_SIZE];
		ByteBuffer buffer = ByteBuffer.wrap(finalData).order(ByteOrder.LITTLE_ENDIAN);
		for(int i = 0; i < data.length; i++) {
			for(int channel = 0; channel < Sound.CHANNELS; channel++) {
				buffer.putShort(data[i]);
			}
		}
		buffer.flip();

		return finalData;
	}

	private short[] getFrequencyData(float frequency, Volume volume, long frames, int neededFrames) {
		Sound sound = new Sound(frequency, volume);
		short[] data = sound.generateOutputData(frames, neededFrames);
		return data;
	}
}