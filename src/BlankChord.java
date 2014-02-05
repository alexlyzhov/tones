import java.util.List;
import java.util.Iterator;

public class BlankChord {
	private List<Frequency> frequencies;
	private Messages messages = Messages.getInstance();

	public BlankChord(List<Frequency> frequencies) {
		this.frequencies = frequencies;
	}

	public BlankChord(BlankChord another) {
		this.frequencies = another.getFrequencies();
	}

	public List<Frequency> getFrequencies() {
		return frequencies;
	}

	public short getData(int frame, double volume) {
		short result = 0;
		for(int i = 0; i < frequencies.size(); i++) {
			Frequency frequency = frequencies.get(i);
			short data = frequency.getData(frame, volume);
			result += (data / frequencies.size());
		}
		return result;
	}

	public String toString() {
		if(frequencies.isEmpty()) return messages.getMessage("silence");

		String result = "[";

		Iterator<Frequency> iterator = frequencies.iterator();
		while(iterator.hasNext()) {
			Frequency frequency = iterator.next();
			result += frequency.toString();
			if(iterator.hasNext()) {
				result += " ";
			}
		}

		result += "]";
		return result;
	}
}