import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ChordPlayer {
	private SourceDataLine sourceDataLine;
	private Volume volume;
	private Chord chord;
	private int duration, preDelay, postDelay, fadeDuration;

	private long frames;

	public ChordPlayer(SourceDataLine sourceDataLine, Chord chord, int duration, int preDelay, int postDelay, int fadeDuration) {
		//test 0 fadeDuration
		this.sourceDataLine = sourceDataLine;
		this.chord = chord;
		this.duration = duration;
		this.preDelay = preDelay;
		this.postDelay = postDelay;
		this.fadeDuration = fadeDuration;
	}

	public void play() {
		try {
			Thread.sleep(preDelay);
		} catch(InterruptedException ex) {ex.printStackTrace();}

		volume = new Volume(fadeDuration);
		frames = 0L;
		int activePlayDuration = duration - preDelay - postDelay - fadeDuration;

		byte[] data = null;
		boolean running = true;
		boolean activePlay = true;
		long startTime = System.currentTimeMillis();
		while(running) {
			if(activePlay && ((startTime + activePlayDuration) <= System.currentTimeMillis())) {
				volume.fadeOut(frames);
				activePlay = false;
			}
			if((data == null) || (sourceDataLine.available() >= data.length)) {
				data = getData(chord, volume, frames, Player.BUFFER_CHUNK_SIZE);
				frames += Player.BUFFER_CHUNK_SIZE;
				sourceDataLine.write(data, 0, data.length);
			}
			running = volume.getRunning(); //not strategically correct, there should be some event processing
		}
		sourceDataLine.flush(); //try to omit and see what happens

		try {
			Thread.sleep(postDelay);
		} catch(InterruptedException ex) {ex.printStackTrace();}
	}

	public void fadeOut() {
		volume.fadeOut(frames);
	}

	private byte[] getData(Chord chord, Volume volume, long frames, int newFrames) {
		short[] data = new short[newFrames];
		for(int i = 0; i < chord.size(); i++) {
			double frequency = chord.getFrequency(i).getValue();
			short[] currentData = getFrequencyData(frequency, volume, frames, newFrames);
			for(int j = 0; j < currentData.length; j++) {
				// System.out.println("cl " + currentData.length + " cd " + currentData[i] + " cs " + chord.size());
				data[j] += (currentData[j] / chord.size());
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

	private short[] getFrequencyData(double frequency, Volume volume, long frames, int neededFrames) {
		Sound sound = new Sound(frequency, volume);
		short[] data = sound.generateOutputData(frames, neededFrames);
		return data;
	}
}