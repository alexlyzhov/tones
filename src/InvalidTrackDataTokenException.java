public class InvalidTrackDataTokenException extends InvalidTrackDataException {
	public InvalidTrackDataTokenException(String token) {
		super("Error while parsing track data on the token \"" + token + "\"");
	}
}