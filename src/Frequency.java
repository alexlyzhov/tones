public class Frequency {
	private double value;

	public Frequency(double value) {
		this.value = value;
	}

	public String toString() {
		return String.format("%.1f", value);
	}

	public double getValue() {
		return value;
	}
}