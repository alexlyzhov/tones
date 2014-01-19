public class Volume {
	private enum EnvelopeStage {FADE_IN, PLATEAU, FADE_OUT, NULL}
	private EnvelopeStage envelopeStage;

	private final static int VOLUME_FADE_PERIOD = 50;
	private final static double MAX_VOLUME = 1F;
	private double linearVolume = 0F;
	private double logarithmicVolume = 0F;
	private double fadeOutInitFrame;

	public Volume() {
		envelopeStage = EnvelopeStage.FADE_IN;
	}

	public double getVolume(long frames) {
		updateVolume(frames);
		return logarithmicVolume;
	}

	public void fadeOut(long frame) {
		envelopeStage = EnvelopeStage.FADE_OUT;
		fadeOutInitFrame = frame;
	}

	public boolean getRunning() {
		return envelopeStage != EnvelopeStage.NULL ? true : false;
	}

	private void updateVolume(long frames) {
		double fadeSeconds = (double) VOLUME_FADE_PERIOD / 1000;
		int fadeFrames = (int) (fadeSeconds * Sound.SAMPLE_RATE);
		final double FADE_BASE = (double) Math.E;

		if(envelopeStage == EnvelopeStage.FADE_IN) {
			double phase = ((double) frames) / fadeFrames;
			linearVolume = MAX_VOLUME * phase;
			logarithmicVolume = (double) ((Math.pow(FADE_BASE, linearVolume) -1) / (FADE_BASE - 1));
			if(linearVolume == MAX_VOLUME) {
				envelopeStage = EnvelopeStage.PLATEAU;
			}
		}
		if(envelopeStage == EnvelopeStage.FADE_OUT) {
			double phase = ((double) (frames - fadeOutInitFrame)) / fadeFrames;
			linearVolume = 1 - MAX_VOLUME * phase;
			logarithmicVolume = (double) ((Math.pow(FADE_BASE, linearVolume) -1) / (FADE_BASE - 1));
			if(linearVolume == 0) {
				envelopeStage = EnvelopeStage.NULL;
			}
		}
	}
}