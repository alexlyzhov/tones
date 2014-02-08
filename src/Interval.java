public enum Interval {
	MINOR_SECOND("minorSecond"), MAJOR_SECOND("majorSecond"), MINOR_THIRD("minorThird"),
	MAJOR_THIRD("majorThird"), PERFECT_FOURTH("perfectFourth"), TRITONE("tritone"),
	PERFECT_FIFTH("perfectFifth"), MINOR_SIXTH("minorSixth"), MAJOR_SIXTH("majorSixth"),
	MINOR_SEVENTH("majorSeventh"), MAJOR_SEVENTH("majorSeventh"),
	PERFECT_OCTAVE("perfectOctave");

	private String stringRepr;
	private Messages messages = Messages.getInstance();

	private Interval(String tokenRepr) {
		this.stringRepr = messages.getMessage(tokenRepr);
	}

	public String toString() {
		return stringRepr;
	}
}