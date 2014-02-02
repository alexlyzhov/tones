public class Frequency {
	private double freq;
	private FunctionData functionData;

	public Frequency(double freq) throws InvalidDataException {
		if((freq < 0) || (freq > 20000)) {
			throw new InvalidDataException();
		}
		this.freq = freq;
		functionData = new FunctionData();
	}

	private class FunctionData {
		private int period;
		private FunctionPhaseData[] phaseDataArray;

		public FunctionData() {
			this.period = (int) (Player.FRAME_RATE / Frequency.this.freq);

			phaseDataArray = new FunctionPhaseData[period];
			for(int frame = 0; frame < period; frame++) {
				double phase = ((double) frame) / period;
				phaseDataArray[frame] = new FunctionPhaseData(phase);
			}
		}

		public FunctionPhaseData getPhaseData(int frame) {
			frame = (int) (frame % period);
			FunctionPhaseData phaseData = phaseDataArray[frame];
			return phaseData;
		}
	}

	private static class FunctionPhaseData {
		double functionValue;

		public FunctionPhaseData(double phase) {
			functionValue = Math.sin(2 * Math.PI * phase);
		}

		public short getPressure(double amplitude) {
			short pressure = (short) Math.round(functionValue * amplitude);
			return pressure;
		}
	}

	public short getData(int frame, double volume) {
		double amplitude = volume * (Math.pow(2, Player.SAMPLE_SIZE_IN_BITS - 1) - 1);
		FunctionPhaseData functionPhaseData = functionData.getPhaseData(frame);
		return functionPhaseData.getPressure(amplitude);
	}

	public double getValue() {
		return freq;
	}

	public String toString() {
		return String.format("%.2f", freq);
	}

	public boolean equals(Object o) {
		if(o instanceof Frequency) {
			Frequency another = (Frequency) o;
			return (getValue() == another.getValue());
		}
		return false;
	}
}