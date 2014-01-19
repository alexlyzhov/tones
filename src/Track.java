import java.util.ArrayList;

public class Track {
	public enum ToneSystem {
		HARMONIC, TEMPERED;
	}
	private float[] freqs;

	public Track(String text, ToneSystem toneSystem) throws InvalidTrackDataException {
		this.freqs = parseFreqs(text, toneSystem);
	}

	private float[] parseFreqs(String text, ToneSystem toneSystem) throws InvalidTrackDataException {
		ArrayList<Float> freqsList = new ArrayList<Float>();
		float prev = 0;
		String[] tokens = text.split(" ");
		for(String token: tokens) {
			if(!token.equals("")) {
				try {
					float freq = 0;
					if(token.endsWith("s")) {
						int semitones = Integer.parseInt(token.substring(1, token.length() - 1));
						float freqAlteration = 0;
						if(toneSystem == ToneSystem.HARMONIC) {
							freqAlteration = prev;
							for(int i = 1; i < semitones; i++) {
								freqAlteration *= (1 / 15);
							}
						} else if(toneSystem == ToneSystem.TEMPERED) {
							freqAlteration = semitones * ((float) Math.pow((double) 2, (double) 1 / 12));
						}
						if(prev == 0) {
							throw new InvalidTrackDataTokenException(token);
						} else {
							if(token.startsWith("+")) {
								freq = prev + freqAlteration;
							} else if(token.startsWith("-")) {
								freq = prev - freqAlteration;
							} else {
								throw new InvalidTrackDataTokenException(token);
							}
						}
					} else {
						freq = Float.parseFloat(token);
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
		float[] freqsArray = new float[freqsList.size()];
		for(int i = 0; i < freqsArray.length; i++) {
			freqsArray[i] = freqsList.get(i);
		}
		return freqsArray;
	}

	public String getFreqsText() {
		StringBuffer text = new StringBuffer();
		for(float freq: freqs) {
			text.append(freq);
			text.append(" ");
		}
		return text.toString();
	}

	public float[] getFreqs() {
		return freqs;
	}
}