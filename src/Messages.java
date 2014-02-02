import java.util.*;

public class Messages {
	private static Messages instance;

	public static Messages getInstance() {
		if(instance == null) {
			instance = new Messages();
		}
		return instance;
	}

	private Locale locale;
	private ResourceBundle bundle;

	private Messages() {
		locale = Locale.getDefault();
		bundle = ResourceBundle.getBundle("bundles.Messages", locale);
	}

	public String getMessage(String token) {
		String message = bundle.getString(token);
		try {
			message = new String(message.getBytes("ISO-8859-1"), "UTF-8");
		} catch(Exception ex) {ex.printStackTrace();}
		return message;
	}
}