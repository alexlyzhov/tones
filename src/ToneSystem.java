public enum ToneSystem {
	HARMONIC, TEMPERED;

	public double countFrequency(double prev, int semitones) {
		switch(this) {
			case HARMONIC:
				boolean addition = (semitones >= 0);
				if(!addition) {
					semitones = -semitones;
				}
				double freqRatio = Math.pow(2, (semitones / 12));
				if(freqRatio < 1) freqRatio = 1;
				double overtone = 0;
				if(addition) {
					overtone = prev * freqRatio;
				} else {
					overtone = prev / freqRatio;
				}
				semitones %= 12;
				double[] intervals = {1, (16d/15), (9d/8), (6d/5), (5d/4), (4d/3), (45d/32), (3d/2), (8d/5), (5d/3), (16d/9), (15d/8), 2};
				if(addition) {
					overtone *= intervals[semitones];
				} else {
					overtone /= intervals[semitones];
				}
				return overtone;
			case TEMPERED:
				double temperedFreqRatio = Math.pow(Math.pow(2, (1d / 12)), semitones);
				return prev * temperedFreqRatio;
			default:
				System.out.println("unknown toneSystem"); //learn how to deal with this situation properly
				return 0;
		}
	}
}