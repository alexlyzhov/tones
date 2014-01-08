public class Volume {
	private enum EnvelopeStage {FADE_IN, PLATEAU, FADE_OUT}
	private EnvelopeStage envelopeStage;

	private final static int VOLUME_FADE_PERIOD = 300;
	private final static float MAX_VOLUME = 1F;
	private float linearVolume = 0F;
	private float logarithmicVolume = 0F;

	public Volume() {
		envelopeStage = EnvelopeStage.FADE_IN;
	}

	public float getVolume(long frames) {
		updateVolume(frames);
		return logarithmicVolume;
	}

	private void updateVolume(long frames) {
		if(envelopeStage != EnvelopeStage.PLATEAU) {
			float fadeSeconds = (float) VOLUME_FADE_PERIOD / 1000;
			int fadeFrames = (int) (fadeSeconds * Sound.SAMPLE_RATE);
			float phase = ((float) frames) / fadeFrames;
			if(envelopeStage == EnvelopeStage.FADE_IN) {
				linearVolume = MAX_VOLUME * phase;
				final float FADE_BASE = (float) Math.E;
				logarithmicVolume = (float) ((Math.pow(FADE_BASE, linearVolume) -1) / (FADE_BASE - 1));
				if(linearVolume == MAX_VOLUME) {
					envelopeStage = EnvelopeStage.PLATEAU;
				}
			}
		}
	}
}