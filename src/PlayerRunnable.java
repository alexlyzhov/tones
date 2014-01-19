import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PlayerRunnable implements Runnable {
	private SourceDataLine sourceDataLine;
	private float[] freqs;
	private Volume volume;
	private long frames;

	public PlayerRunnable(SourceDataLine sourceDataLine, Track track) {
		this.sourceDataLine = sourceDataLine;
		this.freqs = track.getFreqs(); //keep track and not just freqs
	}

	public void run() {
		frames = 0L;
		volume = new Volume();
		byte[] data = null;
		boolean init = true;
		boolean running = true;
		while(running) {
			if((init) || (sourceDataLine.available() >= data.length)) {
				data = getData(freqs, volume, frames, Player.BUFFER_CHUNK_SIZE);
				frames += Player.BUFFER_CHUNK_SIZE;
				sourceDataLine.write(data, 0, data.length);
				init = false;
			}
			running = volume.getRunning();
		}
		destroySound();
	}

	private void destroySound() {
		sourceDataLine.flush();
		sourceDataLine.stop();
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

	public void fadeOut() {
		volume.fadeOut(frames);
	}
}