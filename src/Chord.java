public class Chord extends BlankChord {
	private enum EnvelopeStage {FADE_IN, PLATEAU, FADE_OUT, NULL}
	protected int preDelay, postDelay;
	private int maxFrames, fadeInFrames, fadeOutFrames;
	private int duration, fadeInDuration, fadeOutDuration;
	private short[] shortData;

	public Chord(BlankChord blankChord, final int duration, final int preDelay, final int postDelay, final int fadeInDuration, final int fadeOutDuration) {
		super(blankChord);
		this.duration = duration;
		this.preDelay = preDelay;
		this.postDelay = postDelay;
		this.fadeInDuration = fadeInDuration;
		this.fadeOutDuration = fadeOutDuration;

		maxFrames = getFramesFromMillis(duration - preDelay - postDelay);
		fadeInFrames = getFramesFromMillis(fadeInDuration);
		fadeOutFrames = getFramesFromMillis(fadeOutDuration);

		DataCalculation dataCalculation = new DataCalculation();
		shortData = new short[maxFrames];
		for(int frame = 0; frame < shortData.length; frame++) {
			shortData[frame] = dataCalculation.getFrameData(frame);
		}
	}

	private class DataCalculation {
		private EnvelopeStage envelopeStage = EnvelopeStage.FADE_IN;
		private double volume;
		private int fadeOutInitFrame; double fadeOutInitPhase;

		public short getFrameData(int frame) {
			if((frame >= maxFrames - fadeOutFrames - 1) && ((envelopeStage == EnvelopeStage.PLATEAU) || (envelopeStage == EnvelopeStage.FADE_IN))) {
				fadeOutInitFrame = frame;
				if(envelopeStage == EnvelopeStage.PLATEAU) {
					fadeOutInitPhase = 0;
				} else if(envelopeStage == EnvelopeStage.FADE_IN) {
					double fadeInPhase = getFadeInPhase(frame);
					fadeOutInitPhase = 1 - fadeInPhase;
				}
				envelopeStage = EnvelopeStage.FADE_OUT;
			}
			if(envelopeStage == EnvelopeStage.FADE_IN) {
				double phase = getFadeInPhase(frame);
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
					envelopeStage = EnvelopeStage.NULL;
				}
			}

			return getData(frame, volume);
		}

		private double getFadeInPhase(int frame) { //separate class with data calculation
			return (fadeInFrames != 0) ? ((double) frame / fadeInFrames) : 1;
		}

		private double getLogarithmicVolume(double linearVolume) {
			final double FADE_BASE = Math.E;
			return (Math.pow(FADE_BASE, linearVolume) - 1) / (FADE_BASE - 1);
		}
	}

	private int getFramesFromMillis(int millisDuration) {
		return (int) (millisDuration / 1000d * Player.FRAME_RATE);
	}

	public Chord(Chord another) {
		super(another);
		this.shortData = another.getShortData();
	}

	public int getPreDelay() {
		return preDelay;
	}

	public int getPostDelay() {
		return postDelay;
	}

	public short[] getShortData() {
		return shortData;
	}
}