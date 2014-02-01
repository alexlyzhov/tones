public class InvalidTrackDataException extends Exception {
	public InvalidTrackDataException(String message) {
		super(message);
	}

	public void showDialog() {
		printStackTrace(); //show a new Dialog here
	}
}