import java.util.ArrayList;

public class Track {
	private int duration, innerDelay, chordFadeInterval, trackFadeInterval;
	private ArrayList<Chord> chords;

	public Track(int duration, int innerDelay, int chordFadeInterval, int trackFadeInterval) {
		this.duration = duration;
		this.innerDelay = innerDelay;
		this.chordFadeInterval = chordFadeInterval;
		this.trackFadeInterval = trackFadeInterval;
		chords = new ArrayList<Chord>();
	}

	public void add(Chord chord) {
		chords.add(chord);
	}

	public void addAll(ArrayList<Chord> chordsList) {
		chords.addAll(chordsList);
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

	public String toString() {
		String result = "Duration " + duration + "\nChords: ";
		for(int i = 0; i < chords.size() - 1; i++) {
			result += chords.get(i).toString() + " ";
		}
		result += chords.get(chords.size() - 1).toString();
		return result;
	}
}