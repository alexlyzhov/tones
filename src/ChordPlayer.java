import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class ChordPlayer {
	private class Cycle {
		private final int BUFFER_SIZE = Player.BUFFER_SIZE * 100;
		private Chord chord;
		private EnvelopeStage envelopeStage;
		private double volume;
		private int maxFrames, fadeInFrames, fadeOutFrames;
		private short[] buffer;
		private int fadeOutInitFrame;

		public Cycle(Chord chord) {
			this.chord = chord;
			maxFrames = getFrames(chord.getActivePlayDuration());
			fadeInFrames = getFrames(chord.getFadeInDuration());
			fadeOutFrames = getFrames(chord.getFadeOutDuration());
			envelopeStage = EnvelopeStage.FADE_IN;
			volume = 0D;
		}

		private int getFrames(int millisDuration) {
			return (int) (millisDuration / 1000d * Player.FRAME_RATE);
		}

		public short getFrameData(int frame) {
			updateVolume(frame);
			short result = 0;
			for(int i = 0; i < chord.size(); i++) {
				Frequency frequency = chord.getFrequency(i);
				short data = frequency.getData(frame, volume);
				result += (data / chord.size());
			}
			return result;
		}

		private void updateVolume(int frame) {
			if((frame >= maxFrames - fadeOutFrames) && (envelopeStage == EnvelopeStage.PLATEAU)) {
				fadeOut();
			}
			if(envelopeStage == EnvelopeStage.FADE_IN) {
				double phase = 0;
				if(fadeInFrames == 0) {
					phase = 1;
				} else {
					phase = ((double) frame) / fadeInFrames;
				}
				double linearVolume = phase;
				this.volume = getLogarithmicVolume(linearVolume);
				if(linearVolume == 1D) {
					envelopeStage = EnvelopeStage.PLATEAU;
				}
			} else if(envelopeStage == EnvelopeStage.FADE_OUT) {
				if(fadeOutInitFrame == -1) {
					fadeOutInitFrame = frame;
				}
				double phase = ((double) (frame - fadeOutInitFrame)) / fadeOutFrames;
				double linearVolume = 1 - phase;
				this.volume = getLogarithmicVolume(linearVolume);
				if(linearVolume == 0D) {
					envelopeStage = EnvelopeStage.NULL;
				}
			}
		}

		private double getLogarithmicVolume(double linearVolume) {
			final double FADE_BASE = Math.E;
			return (Math.pow(FADE_BASE, linearVolume) - 1) / (FADE_BASE - 1);
		}

		public void fadeOut() {
			fadeOutInitFrame = -1;
			envelopeStage = EnvelopeStage.FADE_OUT;
		}

		public int getMaxFrames() {
			return maxFrames;
		}
	}

	private enum EnvelopeStage {FADE_IN, PLATEAU, FADE_OUT, NULL}
	private SourceDataLine sourceDataLine;
	private Chord chord;
	private Cycle cycle;

	public ChordPlayer(SourceDataLine sourceDataLine, Chord chord) {
		this.sourceDataLine = sourceDataLine;
		this.chord = chord;
		cycle = new Cycle(chord);
	}

	public void play() {
		try {
			Thread.sleep(chord.getPreDelay());
		} catch(InterruptedException ex) {ex.printStackTrace();}

		long activePlayStartTime = System.currentTimeMillis();
		int frames = 0;
		while(frames < cycle.getMaxFrames()) {
			if(sourceDataLine.available() >= Player.BUFFER_CHUNK_SIZE) {
				int framesToGet = Math.min(Player.BUFFER_CHUNK_SIZE, cycle.getMaxFrames() - frames);
				short[] shortData = new short[framesToGet];
				for(int i = 0; i < shortData.length; i++) {
					shortData[i] = cycle.getFrameData(frames + i);
				}
				byte[] byteData = getByteData(shortData);
				frames += shortData.length;
				sourceDataLine.write(byteData, 0, byteData.length);
			}
		}
		sourceDataLine.flush();

		try {
			Thread.sleep(chord.getPostDelay());
		} catch(InterruptedException ex) {ex.printStackTrace();}
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

	public void fadeOut() {
		cycle.fadeOut();
	}
}