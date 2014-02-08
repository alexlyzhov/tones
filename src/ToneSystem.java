public enum ToneSystem {
	HARMONIC, TEMPERED;

	public double calculateFrequency(double originalFreq, boolean sumCalculation, int multiplier, Interval interval) {
		double resultingFreq = 0;
		int semitones = 0;
		switch(interval) {
			case MINOR_SECOND: semitones = 1; break;
			case MAJOR_SECOND: semitones = 2; break;
			case MINOR_THIRD: semitones = 3; break;
			case MAJOR_THIRD: semitones = 4; break;
			case PERFECT_FOURTH: semitones = 5; break;
			case TRITONE: semitones = 6; break;
			case PERFECT_FIFTH: semitones = 7; break;
			case MINOR_SIXTH: semitones = 8; break;
			case MAJOR_SIXTH: semitones = 9; break;
			case MINOR_SEVENTH: semitones = 10; break;
			case MAJOR_SEVENTH: semitones = 11; break;
			case PERFECT_OCTAVE: semitones = 12; break;
		}
		semitones *= multiplier;
		switch(this) {
			case HARMONIC:
				double overtone = 0;
				int harmonicOvertoneRatio = (semitones / 12);
				if(harmonicOvertoneRatio != 0) {
					overtone = sumCalculation ? originalFreq * (harmonicOvertoneRatio * 2) : originalFreq / (harmonicOvertoneRatio * 2);
				} else {
					overtone = originalFreq;
				}
				semitones %= 12;
				double[] intervals = {1, (16d/15), (9d/8), (6d/5), (5d/4), (4d/3), (45d/32), (3d/2), (8d/5), (5d/3), (16d/9), (15d/8)};
				if(sumCalculation) {
					overtone *= intervals[semitones];
				} else {
					overtone /= intervals[semitones];
				}
				resultingFreq = overtone;
				break;
			case TEMPERED:
				double temperedFreqRatio = Math.pow(Math.pow(2, (1d / 12)), semitones);
				resultingFreq = sumCalculation ? originalFreq * temperedFreqRatio : originalFreq / temperedFreqRatio;
				break;
		}
		return resultingFreq;
	}
}