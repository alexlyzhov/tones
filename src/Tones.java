import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Tones {
	public static void main(String args[]) {
		new Tones(args);
	}

	int channels = 1;
	AudioFormat.Encoding enc = AudioFormat.Encoding.PCM_SIGNED;
	int frameRate = 44100;
	int mBitSampleSize = 16;
	int sFrameSize = mBitSampleSize / 8 * channels;
	boolean bigEndian = false;
	AudioFormat af = new AudioFormat(enc, frameRate, mBitSampleSize, channels, sFrameSize, frameRate, bigEndian);
	float volume = 1F;
	long frames = 0L;

	public Tones(String args[]) {
		
		SourceDataLine sourceDataLine = null;
		DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, af);
		try {
		    sourceDataLine = (SourceDataLine) AudioSystem.getLine(lineInfo);
		    sourceDataLine.open(af);
		} catch (LineUnavailableException ex) {
		    System.out.println("The line is unavailable");
		}
		sourceDataLine.start();

		ArrayList<Float> freqsList = new ArrayList<Float>();
		for(String arg: args) {
			Float freq = null;
			try {
				freq = Float.parseFloat(arg);
			} catch(Exception ex) {System.out.println("Argument is unparsable");}
			freqsList.add(freq);
		}
		float[] freqs = new float[freqsList.size()];
		for(int i = 0; i < freqs.length; i++) {
			freqs[i] = freqsList.get(i);
		}

		byte[] data = getData(freqs, 1);
		while(true) {
			if(sourceDataLine.available() >= data.length) {
				data = getData(freqs, 1);
				sourceDataLine.write(data, 0, data.length);
			}
		}
	}

	private short[] getData(float frequency, int neededFrames) {
		float amplitude = (float) (volume * (Math.pow(2, mBitSampleSize - 1) - 1));
		int frames = Math.round(frameRate / frequency);
		short[] data = new short[neededFrames];
		int initFrame = (int) (this.frames % frames);
		int completedFrames = 0;
		for(int frame = initFrame; frame < frames; frame++) {
			float phase = ((float) frame) / ((float) frames);
			float sin = (float) Math.sin(2 * Math.PI * phase);
			short pressure = (short) Math.round(sin * amplitude);
			data[completedFrames] = pressure;
			if(++completedFrames >= neededFrames) break;
		}
		while(completedFrames < neededFrames) {
			for(int frame = 0; frame < frames; frame++) {
				float phase = ((float) frame) / ((float) frames);
				float sin = (float) Math.sin(2 * Math.PI * phase);
				short pressure = (short) Math.round(sin * amplitude);
				data[completedFrames] = pressure;
				if(++completedFrames >= neededFrames) break;
			}
		}
		return data;
	}

	private byte[] getData(float[] frequencies, int newFrames) {
		short[] data = new short[newFrames];
		for(float frequency: frequencies) {
			short[] currentData = getData(frequency, newFrames);
			for(int i = 0; i < currentData.length; i++) {
				data[i] += (currentData[i] / frequencies.length);
			}
		}
		frames += newFrames;
		byte[] finalData = new byte[newFrames * sFrameSize];
		ByteBuffer buffer = ByteBuffer.wrap(finalData).order(ByteOrder.LITTLE_ENDIAN);
		for(int i = 0; i < data.length; i++) {
			buffer.putShort(data[i]);
		}
		buffer.flip();
		return finalData;
	}
}