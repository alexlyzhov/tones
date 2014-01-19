import javafx.scene.text.*;

public class Log extends Text {
	public void write(String string) {
		String currentText = getText();
		currentText += string;
		currentText += "\n";
		setText(currentText);
	}
}