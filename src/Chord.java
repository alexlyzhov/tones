public class Chord extends BlankChord {
	private int duration, preDelay, postDelay, fadeInDuration, fadeOutDuration;

	public Chord(BlankChord blankChord, int duration, int preDelay, int postDelay, int fadeInDuration, int fadeOutDuration) {
		super(blankChord);
		this.duration = duration;
		this.preDelay = preDelay;
		this.postDelay = postDelay;
		this.fadeInDuration = fadeInDuration;
		this.fadeOutDuration = fadeOutDuration;
	}

	public int getDuration() {
		return duration;
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
}