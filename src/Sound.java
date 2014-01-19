import javax.sound.sampled.AudioFormat;
import java.util.ArrayList;

public class Sound {
	public final static AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;
	public final static int SAMPLE_RATE = 44100;
	public final static int SAMPLE_SIZE_IN_BITS = 16;
	public final static int CHANNELS = 2;
	public final static int FRAME_SIZE = SAMPLE_SIZE_IN_BITS / 8 * CHANNELS;
	public final static int FRAME_RATE = SAMPLE_RATE;
	public final static boolean BIG_ENDIAN = false;

	public static AudioFormat getAudioFormat() {
		AudioFormat af = new AudioFormat(ENCODING,
										 SAMPLE_RATE,
										 SAMPLE_SIZE_IN_BITS,
										 CHANNELS,
										 FRAME_SIZE,
										 FRAME_RATE,
										 BIG_ENDIAN
										);
		return af;
	}

	private static class FunctionData {
		float phase;
		float sin;

		public FunctionData(float phase) {
			this.phase = phase;
			this.sin = (float) Math.sin(2 * Math.PI * phase);
		}

		public FunctionData(FunctionData functionData) {
			this.phase = functionData.phase;
			this.sin = functionData.sin;
		}

		public short getPressure(float amplitude) {
			short pressure = (short) Math.round(sin * amplitude);
			return pressure;
		}
	}

	int period;
	FunctionData[] functionData;
	Volume volume;

	public Sound(float frequency, Volume volume) {
		this.volume = volume;
		period = Math.round(FRAME_RATE / frequency);
		functionData = generateFunctionData();
	}

	private FunctionData[] generateFunctionData() {
		functionData = new FunctionData[period];
		for(int frame = 0; frame < period; frame++) {
			float phase = ((float) frame) / period;
			functionData[frame] = new FunctionData(phase);
		}
		return functionData;
	}

	public short[] generateOutputData(long elapsedFrames, int neededFrames) {
		short[] outputData = new short[neededFrames];
		int pointer = 0;
		int startFrames = (int) elapsedFrames % period;

		int firstCycleFrames = Math.min(neededFrames, period - startFrames);
		for(int i = startFrames; i < startFrames + firstCycleFrames; i++) {
			outputData[pointer++] = getSoundData(elapsedFrames + pointer);
		}
		int remainingFrames = neededFrames - firstCycleFrames;

		while(remainingFrames >= period) {
			for(int i = 0; i < period; i++) {
				outputData[pointer++] = getSoundData(elapsedFrames + pointer);
			}
			remainingFrames -= period;
		}

		for(int i = 0; i < remainingFrames; i++) {
			outputData[pointer++] = getSoundData(elapsedFrames + pointer);
		}

		if(neededFrames != outputData.length) System.out.println("Something went wrong");
		return outputData;
	}

	private short getSoundData(long frames) {
		int frame = (int) (frames % period);
		float amplitude = getAmplitude(volume.getVolume(frames));
		return functionData[frame].getPressure(amplitude);
	}

	public static float getAmplitude(float volume) {
		float amplitude = (float) (volume * (Math.pow(2, SAMPLE_SIZE_IN_BITS - 1) - 1));
		return amplitude;
	}
}