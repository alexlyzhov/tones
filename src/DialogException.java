public class DialogException extends Exception {
	private String message;

	public DialogException(String message) {
		super(message);
		this.message = message;
	}

	public void showDialog() {
		printStackTrace(); //show a new Dialog here
	}
}