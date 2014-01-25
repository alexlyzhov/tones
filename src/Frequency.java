public class Frequency {
	private static class FunctionData {
		double phase;
		double sin;

		public FunctionData(double phase) {
			this.phase = phase;
			this.sin = (double) Math.sin(2 * Math.PI * phase);
		}

		public FunctionData(FunctionData functionData) {
			this.phase = functionData.phase;
			this.sin = functionData.sin;
		}

		public short getPressure(double amplitude) {
			short pressure = (short) Math.round(sin * amplitude);
			return pressure;
		}
	}

	private double value;
	private FunctionData[] functionData;

	public Frequency(double value) {
		this.value = value;
		functionData = generateFunctionData();
	}

	private FunctionData[] generateFunctionData() {
		int period = getPeriod();
		functionData = new FunctionData[period];
		for(int frame = 0; frame < period; frame++) {
			double phase = ((double) frame) / period;
			functionData[frame] = new FunctionData(phase);
		}
		return functionData;
	}

	private int getPeriod() {
		return (int) (Player.FRAME_RATE / this.value);
	}

	public short getData(int frame, double volume) {
		frame = (int) (frame % getPeriod());
		double amplitude = getAmplitude(volume);
		return functionData[frame].getPressure(amplitude);
	}

	private double getAmplitude(double volume) {
		double amplitude = volume * (Math.pow(2, Player.SAMPLE_SIZE_IN_BITS - 1) - 1);
		return amplitude;
	}

	public String toString() {
		return String.format("%.1f", value);
	}

	public double getValue() {
		return value;
	}
}