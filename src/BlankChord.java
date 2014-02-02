import java.util.List;
import java.util.Iterator;

public class BlankChord {
	private List<Frequency> frequencies; //protected
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

	public Frequency getFrequency(int index) { //remove
		return frequencies.get(index);
	}

	public int size() { //remove
		return frequencies.size();
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