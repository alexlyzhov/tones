import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.control.*;

public class TonesVBox extends VBox {
	public TonesVBox() {
		setAlignment(Pos.CENTER);
		setSpacing(5);
        setPadding(new Insets(10, 10, 10, 10));
        ComposingPane composingPane = new ComposingPane();
        CalculationsPane calculationsPane = new CalculationsPane(composingPane);
        PlayingPane playingPane = new PlayingPane(composingPane);
		getChildren().addAll(calculationsPane, new Separator(), composingPane, new Separator(), playingPane);
	}
}