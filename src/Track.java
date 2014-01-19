import java.util.ArrayList;

public class Track {
	public enum ToneSystem {
		HARMONIC, TEMPERED;

		private double countFrequency(double prev, int semitones) {
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
	private double[] freqs;
	private ToneSystem toneSystem;

	public Track(String text, ToneSystem toneSystem) throws InvalidTrackDataException {
		this.toneSystem = toneSystem;
		this.freqs = parseFreqs(text);
	}

	private double[] parseFreqs(String text) throws InvalidTrackDataException {
		ArrayList<Double> freqsList = new ArrayList<Double>();
		double prev = 0;
		String[] tokens = text.split(" ");
		for(String token: tokens) {
			if(!token.equals("")) { //process empty strings earlier
				try {
					double freq = 0;
					if(token.endsWith("s")) {
						if(prev == 0) {
							throw new InvalidTrackDataTokenException(token);
						}
						int semitones = Integer.parseInt(token.substring(0, token.length() - 1));
						freq = toneSystem.countFrequency(prev, semitones);
					} else {
						freq = Double.parseDouble(token);
					}
					freqsList.add(freq);
					prev = freq;
				} catch(NumberFormatException ex) {
					throw new InvalidTrackDataTokenException(token);
				}
			}
		}
		if(freqsList.size() == 0) {
			throw new InvalidTrackDataException("No track data was found");
		}
		double[] freqsArray = new double[freqsList.size()];
		for(int i = 0; i < freqsArray.length; i++) {
			freqsArray[i] = freqsList.get(i);
		}
		return freqsArray; //return a list of chords
	}

	public String getFreqsText() { //temporary
		StringBuffer text = new StringBuffer();
		for(double freq: freqs) {
			text.append(freq);
			text.append(" ");
		}
		return text.toString();
	}

	public double[] getFreqs() {
		return freqs;
	}
}