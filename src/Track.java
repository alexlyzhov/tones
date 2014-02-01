import java.util.List;

public class Track {
	private int duration, innerDelay, chordFadeInterval, trackFadeInterval;
	private List<Chord> chords;

	public Track(int duration, int innerDelay, int chordFadeInterval, int trackFadeInterval, List<Chord> chordsList) {
		this.duration = duration;
		this.innerDelay = innerDelay;
		this.chordFadeInterval = chordFadeInterval;
		this.trackFadeInterval = trackFadeInterval;
		this.chords = chordsList;
	}

	public Chord getChord(int index) {
		Chord chord = chords.get(index);
		int chordDuration = duration / size();
		int chordPreDelay = (index == 0) ? 0 : (innerDelay / 2);
		int chordPostDelay = (index == size() - 1) ? 0 : (innerDelay / 2);
		int chordFadeInDuration = (index == 0) ? trackFadeInterval : chordFadeInterval;
		int chordFadeOutDuration = (index == size() - 1) ? trackFadeInterval : chordFadeInterval;
		chord.setInfo(chordDuration, chordPreDelay, chordPostDelay, chordFadeInDuration, chordFadeOutDuration);
		return chord;
	}

	public int size() {
		return chords.size();
	}

	public int getDuration() {
		return duration;
	}

	public String toString() {
		String result = "Duration " + duration + "\nChords: ";
		for(int i = 0; i < chords.size() - 1; i++) {
			result += chords.get(i).toString() + " ";
		}
		result += chords.get(chords.size() - 1).toString();
		return result;
	}
}