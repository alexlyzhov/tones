import javax.sound.sampled.*;

public class Tones {
	public static void main(String args[]) {
		new Tones();
	}

	public Tones() {
		// printDebugInfo();

		AudioFormat.Encoding enc = AudioFormat.Encoding.PCM_SIGNED;
		// AudioFormat.Encoding enc = AudioFormat.Encoding.PCM_FLOAT;
		// int channels = 1; int frameSize = 2;
		int channels = 2; int frameSize = 4;
		boolean bigEndian = false;
		// boolean bigEndian = true;
		AudioFormat af = new AudioFormat(enc, 44100, 16, channels, frameSize, 44100, bigEndian);

		Clip clip = null;
		DataLine.Info lineInfo = new DataLine.Info(Clip.class, af);

		float amplitude = 1F;
		float frequency = 1500F;
		amplitude = (float) (amplitude * (Math.pow(2, af.getSampleSizeInBits() - 1) - 1));
		int period = Math.round(af.getFrameRate() / frequency);
		int bufferLength = period * af.getFrameSize();
		byte[] data = new byte[bufferLength];
		for(int frame = 0; frame < period; frame++) {
			float fPeriodPos = (float) frame / (float) period;
			float fValue = (float) Math.sin(fPeriodPos * 2 * Math.PI);
			int value = Math.round(fValue * amplitude);
			int baseAddress = frame * af.getFrameSize();
			data[baseAddress + 0] = (byte) (value & 0xFF);
			data[baseAddress + 1] = (byte) ((value >>> 8) & 0xFF);
			data[baseAddress + 2] = (byte) (value & 0xFF);
			data[baseAddress + 3] = (byte) ((value >>> 8) & 0xFF);
		}
		
		if (!AudioSystem.isLineSupported(lineInfo)) {
			System.out.println("line is not supported");
		}
		try {
		    clip = (Clip) AudioSystem.getLine(lineInfo);
		    clip.open(af, data, 0, bufferLength);
		} catch (LineUnavailableException ex) {
		    System.out.println("line is unavailable");
		}
		while(true) {
			clip.loop(clip.LOOP_CONTINUOUSLY);
		}

		// Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
		// for(Mixer.Info info: mixerInfo) {
		// 	Mixer mixer = AudioSystem.getMixer(info);
		// 	System.out.println("desc: " + info.getDescription());
		// 	Line.Info[] newLineInfo = mixer.getSourceLineInfo(lineInfo);
		// 	for(Line.Info newLine: newLineInfo) {
		// 		System.out.println(newLine.toString());
		// 	}
		// }
	}

	private void printDebugInfo() {
		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
		for(Mixer.Info info: mixerInfo) {
			System.out.println("desc: " + info.getDescription());
			System.out.println("name: " + info.getName());
			System.out.println("vendor: " + info.getVendor());
			System.out.println("version: " + info.getVersion());
			Mixer mixer = AudioSystem.getMixer(info);
			Line.Info[] sourceLineInfo = mixer.getSourceLineInfo();
			for(Line.Info lineInfo: sourceLineInfo) {
				System.out.println(lineInfo.toString());
			}
		}
	}
}