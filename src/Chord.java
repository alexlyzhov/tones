import java.util.Iterator;
import java.util.ArrayList;

public class Chord {
	private ArrayList<Frequency> frequencies;
	private int duration, preDelay, postDelay, fadeInDuration, fadeOutDuration;

	public Chord() {
		frequencies = new ArrayList<Frequency>();
	}

	public void add(Frequency frequency) {
		frequencies.add(frequency);
	}

	public Frequency getFrequency(int index) {
		return frequencies.get(index);
	}

	public int size() {
		return frequencies.size();
	}

	public void setInfo(int duration, int preDelay, int postDelay, int fadeInDuration, int fadeOutDuration) {
		this.duration = duration;
		this.preDelay = preDelay;
		this.postDelay = postDelay;
		this.fadeInDuration = fadeInDuration;
		this.fadeOutDuration = fadeOutDuration;
	}

	public int getActivePlayDuration() {
		System.out.println(duration + " " + preDelay + " " + postDelay + " active " + (duration - preDelay - postDelay));
		return duration - preDelay - postDelay;
	}

	public int getPreDelay() {
		return preDelay;
	}


	public int getPostDelay() {
		return postDelay;
	}


	public int getFadeInDuration() {
		return fadeInDuration;
	}


	public int getFadeOutDuration() {
		return fadeOutDuration;
	}


	public String toString() {
		if(frequencies.isEmpty()) return "silence";

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