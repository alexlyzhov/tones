import java.util.ArrayList;

public class Track {
	private double duration;
	private ArrayList<Chord> chords; //except null chords[] size

	public Track(double duration) {
		this.duration = duration;
		chords = new ArrayList<Chord>();
	}

	public void add(Chord chord) {
		chords.add(chord);
	}

	public int size() {
		return chords.size();
	}

	public Chord getChord(int index) {
		if((index < 0) || (index >= chords.size())) { //check for accordance with ArrayList
			throw new ArrayIndexOutOfBoundsException();
		}
		return chords.get(index);
	}

	public String toString() {
		String result = "Duration " + String.format("%.1f", duration) + "\nChords: ";
		for(int i = 0; i < chords.size() - 1; i++) {
			result += chords.get(i).toString() + " ";
		}
		result += chords.get(chords.size() - 1).toString();
		return result;
	}

	public double getDuration() {
		return duration;
	}
}