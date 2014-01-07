import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Tones {
	public static void main(String args[]) {
		new Tones(args);
	}

	// private final static int VOLUME_FADE_PERIOD = 500;
	// private final static float MAX_VOLUME = 1F;
	// private boolean volumeProgress = true;
	// private float volume = 0F;
	private float volume = 1F;
	private long frames = 0L;
	private SourceDataLine sourceDataLine = null;
	private final static int BUFFER_SIZE = Sound.SAMPLE_RATE / 100;
	private float[] freqs;
	//move source files to ./src

	public Tones(String args[]) {
		AudioFormat af = new AudioFormat(Sound.ENCODING,
										 Sound.SAMPLE_RATE,
										 Sound.SAMPLE_SIZE_IN_BITS,
										 Sound.CHANNELS, //test 2 channels and fix both classes
										 Sound.FRAME_SIZE,
										 Sound.FRAME_RATE,
										 Sound.BIG_ENDIAN);
		try {
			DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, af);
		    sourceDataLine = (SourceDataLine) AudioSystem.getLine(lineInfo);
		    sourceDataLine.open(af);
			sourceDataLine.start();
		} catch (LineUnavailableException ex) {
		    System.out.println("The line is unavailable");
		}

		//make a simple gui
		ArrayList<Float> freqsList = new ArrayList<Float>();
		for(String arg: args) {
			Float freq = null;
			try {
				freq = Float.parseFloat(arg);
			} catch(Exception ex) {System.out.println("Argument is unparsable");}
			freqsList.add(freq);
		}
		freqs = new float[freqsList.size()];
		for(int i = 0; i < freqs.length; i++) {
			freqs[i] = freqsList.get(i);
		}

		boolean init = true;
		byte[] data = null;
		while(true) {
			if((init) || (sourceDataLine.available() >= data.length)) { //fading effect
				updateVolume();
				data = getData(freqs, volume, frames, BUFFER_SIZE);
				frames += BUFFER_SIZE;
				sourceDataLine.write(data, 0, data.length);
				init = false;
				decreaseFrames();
			}
		}
	}

	private void updateVolume() {
		// float updatesPerMillisecond = ((float) Sound.FRAME_RATE) / BUFFER_SIZE / 1000;
		// float volumeFadeChunk = MAX_VOLUME / (VOLUME_FADE_PERIOD * updatesPerMillisecond);
		// if((volumeProgress) && (volume < MAX_VOLUME)) {
		// 	if((volume + volumeFadeChunk) <= MAX_VOLUME) {
		// 		volume += volumeFadeChunk;
		// 	} else {
		// 		volume = MAX_VOLUME;
		// 	}
		// 	System.out.println("new volume: " + volume);
		// }
	}

	private void decreaseFrames() {
		int[] framesArray = new int[freqs.length];
		for(int i = 0; i < freqs.length; i++) {
			framesArray[i] = Math.round(Sound.FRAME_RATE / freqs[i]);
		}
		long lcm = framesArray[0];
		for(int i = 1; i < framesArray.length; i++) {
			long a = lcm; long b = framesArray[i];
			while(b > 0) {
				long tmp = b;
				b = a % b;
				a = tmp;
			}
			long gcd = a;
			if(lcm > Long.MAX_VALUE / Sound.FRAME_RATE) {
				lcm = -1;
				break;
			}
			lcm *= (framesArray[i] / gcd);
		}
		if((frames >= lcm) && (lcm != -1)) {
			frames = frames %= lcm;
		}
	}

	private byte[] getData(float[] frequencies, float volume, long frames, int newFrames) {
		short[] data = new short[newFrames];
		for(float frequency: frequencies) {
			short[] currentData = getFrequencyData(frequency, volume, frames, newFrames);
			for(int i = 0; i < currentData.length; i++) {
				data[i] += (currentData[i] / frequencies.length);
			}
		}
		for(short pressure: data) {
			System.out.print(pressure + " ");
		}
		System.out.println();
		
		byte[] finalData = new byte[newFrames * Sound.FRAME_SIZE];
		ByteBuffer buffer = ByteBuffer.wrap(finalData).order(ByteOrder.LITTLE_ENDIAN);
		for(int i = 0; i < data.length; i++) {
			buffer.putShort(data[i]);
		}
		buffer.flip();

		return finalData;
	}

	private short[] getFrequencyData(float frequency, float volume, long frames, int neededFrames) {
		Sound sound = new Sound(frequency);
		short[] data = sound.generateOutputData(volume, frames, neededFrames);
		return data;
	}
}