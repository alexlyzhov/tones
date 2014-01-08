public class Volume {
	private enum EnvelopeStage {FADE_IN, PLATEAU, FADE_OUT, NULL}
	private EnvelopeStage envelopeStage;

	private final static int VOLUME_FADE_PERIOD = 50;
	private final static float MAX_VOLUME = 1F;
	private float linearVolume = 0F;
	private float logarithmicVolume = 0F;
	private float fadeOutInitFrame;

	public Volume() {
		envelopeStage = EnvelopeStage.FADE_IN;
	}

	public float getVolume(long frames) {
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
		float fadeSeconds = (float) VOLUME_FADE_PERIOD / 1000;
		int fadeFrames = (int) (fadeSeconds * Sound.SAMPLE_RATE);
		final float FADE_BASE = (float) Math.E;

		if(envelopeStage == EnvelopeStage.FADE_IN) {
			float phase = ((float) frames) / fadeFrames;
			linearVolume = MAX_VOLUME * phase;
			logarithmicVolume = (float) ((Math.pow(FADE_BASE, linearVolume) -1) / (FADE_BASE - 1));
			if(linearVolume == MAX_VOLUME) {
				envelopeStage = EnvelopeStage.PLATEAU;
			}
		}
		if(envelopeStage == EnvelopeStage.FADE_OUT) {
			float phase = ((float) (frames - fadeOutInitFrame)) / fadeFrames;
			linearVolume = 1 - MAX_VOLUME * phase;
			logarithmicVolume = (float) ((Math.pow(FADE_BASE, linearVolume) -1) / (FADE_BASE - 1));
			if(linearVolume == 0) {
				envelopeStage = EnvelopeStage.NULL;
			}
		}
	}
}