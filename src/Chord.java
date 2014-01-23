import java.util.Iterator;
import java.util.ArrayList;

public class Chord {
	private ArrayList<Frequency> frequencies;

	public Chord() {
		frequencies = new ArrayList<Frequency>();
	}

	public String toString() {
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

	public void add(double frequencyValue) {
		Frequency frequency = new Frequency(frequencyValue);
		frequencies.add(frequency);
	}

	public int size() {
		return frequencies.size();
	}

	public Frequency getFrequency(int index) {
		if((index < 0) || (index >= frequencies.size())) {
			throw new ArrayIndexOutOfBoundsException();
		}
		return frequencies.get(index);
	}
}