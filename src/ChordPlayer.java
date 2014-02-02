import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.ArrayList;

public class ChordPlayer {
	private class Cycle {
		private Chord chord;
		private EnvelopeStage envelopeStage;
		private double volume;
		private int maxFrames, fadeInFrames, fadeOutFrames;
		private short[] buffer;
		private int fadeOutInitFrame; double fadeOutInitPhase;
		private boolean fadeOutFlag;
		private short prevFrameData;

		public Cycle(Chord chord) {
			this.chord = chord;
			maxFrames = getFrames(chord.getDuration() - chord.getPreDelay() - chord.getPostDelay());
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
			if(envelopeStage == EnvelopeStage.NULL) {
				return 0;
			}
			short result = 0;
			for(int i = 0; i < chord.size(); i++) {
				Frequency frequency = chord.getFrequency(i);
				short data = frequency.getData(frame, volume);
				if((envelopeStage == EnvelopeStage.FADE_OUT) && (fadeOutFrames == 0) && (((int) prevFrameData * data)) <= 0) {
					data = 0;
					// System.out.println("null in getFrameData()");
					envelopeStage = EnvelopeStage.NULL;
				}
				prevFrameData = data;
				result += (data / chord.size());
			}
			return result;
		}

		private void updateVolume(int frame) {
			if((frame >= maxFrames - fadeOutFrames - 1) && (envelopeStage == EnvelopeStage.PLATEAU)) {
				fadeOut();
			}
			if(fadeOutFlag) {
				fadeOutInitFrame = frame;
				if(envelopeStage == EnvelopeStage.PLATEAU) {
					fadeOutInitPhase = 0;
				} else if(envelopeStage == EnvelopeStage.FADE_IN) {
					double fadeInPhase = (fadeInFrames != 0) ? ((double) frame) / fadeInFrames : 1;
					fadeOutInitPhase = 1 - fadeInPhase;
				} else {
					System.out.println("invalid envelope stage");
				}
				envelopeStage = EnvelopeStage.FADE_OUT;
				fadeOutFlag = false;
			}
			if(envelopeStage == EnvelopeStage.FADE_IN) {
				double phase = (fadeInFrames != 0) ? ((double) frame) / fadeInFrames : 1;
				double linearVolume = phase;
				if(linearVolume < 1D) {
					this.volume = getLogarithmicVolume(linearVolume);
				} else {
					this.volume = 1;
					envelopeStage = EnvelopeStage.PLATEAU;
				}
			} else if(envelopeStage == EnvelopeStage.FADE_OUT) {
				double phase = (fadeOutFrames != 0) ? (fadeOutInitPhase + ((double) (frame - fadeOutInitFrame)) / fadeOutFrames) : 0;
				double linearVolume = 1 - phase;
				if(linearVolume > 0D) {
					this.volume = getLogarithmicVolume(linearVolume);
				} else {
					this.volume = 0;
					System.out.println("null in envfo");
					envelopeStage = EnvelopeStage.NULL;
				}
			}
		}

		private double getLogarithmicVolume(double linearVolume) {
			final double FADE_BASE = Math.E;
			return (Math.pow(FADE_BASE, linearVolume) - 1) / (FADE_BASE - 1);
		}

		public void fadeOut() {
			fadeOutFlag = true;
		}

		public void stop() {
			envelopeStage = EnvelopeStage.NULL;
		}

		public int getMaxFrames() {
			return maxFrames;
		}

		public boolean isAlive() {
			return (envelopeStage != EnvelopeStage.NULL);
		}
	}

	private enum EnvelopeStage {FADE_IN, PLATEAU, FADE_OUT, NULL}
	private SourceDataLine sourceDataLine;
	private Chord chord;
	private Cycle cycle;
	private int frames;
	private boolean playingIsActive = false;

	public ChordPlayer(Chord chord) {
		this.sourceDataLine = Player.getSourceDataLine();
		this.chord = chord;
		cycle = new Cycle(chord);
	}

	public void play() {
		try {
			Thread.sleep(chord.getPreDelay());
		} catch(InterruptedException ex) {ex.printStackTrace();}

		long activePlayStartTime = System.currentTimeMillis();
		frames = 0;
		playingIsActive = true;
		while(cycle.isAlive()) {
			if(frames > cycle.getMaxFrames()) {
				System.out.println("max frames exceeded");
			}
			if(sourceDataLine.available() >= Player.BUFFER_CHUNK_SIZE) {
				int framesToGet = Player.BUFFER_CHUNK_SIZE;
				ArrayList<Short> shortDataList = new ArrayList<Short>();
				for(int i = 0; i < framesToGet; i++) {
					if(cycle.isAlive()) {
						shortDataList.add(cycle.getFrameData(frames + i));
					}
				}
				short[] shortData = new short[shortDataList.size()];
				for(int i = 0; i < shortDataList.size(); i++) {
					shortData[i] = shortDataList.get(i);
				}
				byte[] byteData = getByteData(shortData);
				frames += shortData.length;
				if(byteData.length != 2)
				sourceDataLine.write(byteData, 0, byteData.length);
			}
		}
		playingIsActive = false;

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

	public void stop() {
		cycle.stop();
	}

	public Chord getChord() {
		return playingIsActive ? chord : null;
	}
}