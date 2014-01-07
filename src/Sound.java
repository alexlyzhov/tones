import javax.sound.sampled.AudioFormat;
import java.util.ArrayList;

public class Sound {
	public final static AudioFormat.Encoding ENCODING = AudioFormat.Encoding.PCM_SIGNED;
	public final static int SAMPLE_RATE = 44100;
	public final static int SAMPLE_SIZE_IN_BITS = 16;
	public final static int CHANNELS = 1;
	public final static int FRAME_SIZE = SAMPLE_SIZE_IN_BITS / 8 * CHANNELS;
	public final static int FRAME_RATE = SAMPLE_RATE;
	public final static boolean BIG_ENDIAN = false;

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
	}

	private static class SoundData extends FunctionData {
		short pressure;

		public SoundData(FunctionData functionData, float amplitude) {
			super(functionData);
			pressure = (short) Math.round(sin * amplitude);
		}

		public short getPressure() {
			return pressure;
		}

		public static float getAmplitude(float volume) {
			float amplitude = (float) (volume * (Math.pow(2, SAMPLE_SIZE_IN_BITS - 1) - 1));
			System.out.println(amplitude);
			return amplitude;
		}
	}

	int frames;
	FunctionData[] functionData;

	public Sound(float frequency) {
		frames = Math.round(FRAME_RATE / frequency);
		functionData = generateFunctionData(frames);
	}

	private FunctionData[] generateFunctionData(int frames) {
		functionData = new FunctionData[frames];
		for(int frame = 0; frame < frames; frame++) {
			float phase = ((float) frame) / frames;
			functionData[frame] = new FunctionData(phase);
		}
		return functionData;
	}

	public short[] generateOutputData(float volume, long elapsedFrames, int neededFrames) {
		short[] outputData = new short[neededFrames];
		int pointer = 0;
		int startFrames = (int) elapsedFrames % frames;
		short[] cycleData = generateOutputData(volume);

		int firstCycleFrames = Math.min(neededFrames, frames - startFrames);
		for(int i = startFrames; i < startFrames + firstCycleFrames; i++) {
			outputData[pointer++] = cycleData[i];
		}
		int remainingFrames = neededFrames - firstCycleFrames;

		while(remainingFrames >= frames) {
			for(int i = 0; i < frames; i++) {
				outputData[pointer++] = cycleData[i];
			}
			remainingFrames -= frames;
		}

		for(int i = 0; i < remainingFrames; i++) {
			outputData[pointer++] = cycleData[i];
		}

		if(neededFrames != outputData.length) System.out.println("Something went wrong");
		return outputData;
	}

	private short[] generateOutputData(float volume) {
		short[] result = new short[frames];
		SoundData[] soundData = generateSoundData(volume);
		for(int i = 0; i < frames; i++) {
			result[i] = soundData[i].getPressure();
		}
		return result;
	}

	private SoundData[] generateSoundData(float volume) {
		float amplitude = SoundData.getAmplitude(volume);
		SoundData[] soundData = new SoundData[frames];
		for(int i = 0; i < soundData.length; i++) {
			soundData[i] = new SoundData(functionData[i], amplitude);
		}
		return soundData;
	}
}