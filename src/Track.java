import java.util.List;
import java.util.ArrayList;

public class Track {
	protected List<Chord> chords;
	protected int duration;
	private Messages messages = Messages.getInstance();

	public Track(List<BlankChord> blankChordsList, int duration, int innerDelay, int chordFadeInterval, int trackFadeInterval) {
		chords = new ArrayList<Chord>();
		for(int index = 0; index < blankChordsList.size(); index++) {
			int chordDuration = duration / blankChordsList.size();
			int chordPreDelay = (index == 0) ? 0 : (innerDelay / 2);
			int chordPostDelay = (index == blankChordsList.size() - 1) ? 0 : (innerDelay / 2);
			int chordFadeInDuration = (index == 0) ? trackFadeInterval : chordFadeInterval;
			int chordFadeOutDuration = (index == blankChordsList.size() - 1) ? trackFadeInterval : chordFadeInterval;
			Chord newChord = new Chord(blankChordsList.get(index), chordDuration, chordPreDelay, chordPostDelay, chordFadeInDuration, chordFadeOutDuration);
			chords.add(newChord);
		}
		this.duration = duration;
	}

	public Track(Track another) {
		this.chords = another.chords;
		this.duration = another.duration;
	}

	public String toString() {
		String result = messages.getMessage("duration") + " " + getSecondsDuration() + "\n" + messages.getMessage("chords");
		for(int i = 0; i < chords.size() - 1; i++) {
			result += chords.get(i).toString() + " ";
		}
		result += chords.get(chords.size() - 1).toString();
		return result;
	}

	public double getSecondsDuration() {
		return (duration / 1000d);
	}
}