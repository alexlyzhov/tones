import org.gnome.gtk.*;
import org.gnome.gdk.Event;
import java.util.ArrayList;

public class Tones extends Window {
	public static void main(String args[]) {
		Gtk.init(args);
		new Tones(args);
	}

	private Entry frequencyEntry;
	private Button playButton, stopButton;
	private Player player;
	private boolean playing = false;

	public Tones(String[] args) {
		hide();
		setTitle("Tones");
		destroyOnDelete();
		setTonesIcon();

		player.init();

		VBox vbox = new VBox(false, 0);
		vbox.add(frequencyEntry = new Entry());
		HBox buttons = new HBox(false, 0);
		buttons.add(playButton = new Button("Play"));
		playButton.connect(new Button.Clicked() {
			public void onClicked(Button button) {
				if(!playing) {
					float[] freqs = parseFreqs(frequencyEntry.getText());
					if(freqs.length > 0) {
						player = new Player(freqs);
						playing = true;
					}
				}
			}
		});
		buttons.add(stopButton = new Button("Stop"));
		stopButton.connect(new Button.Clicked() {
			public void onClicked(Button button) {
				if(player != null) {
					player.stopSound();
					playing = false;
				}
			}
		});
		vbox.add(buttons);

		add(vbox);
		showAll();
		setCenterLocation();
		Gtk.main(); //include windows and mac gtk native libraries
	}

	private float[] parseFreqs(String text) {
		ArrayList<Float> freqsList = new ArrayList<Float>();
		String[] tokens = text.split(" ");
		for(String token: tokens) {
			try {
				Float freq = Float.parseFloat(token);
				freqsList.add(freq);
			} catch(Exception ex) {}
		}
		float[] freqsArray = new float[freqsList.size()];
		for(int i = 0; i < freqsArray.length; i++) {
			freqsArray[i] = freqsList.get(i);
		}
		return freqsArray;
	}

	public void destroyOnDelete() {
		connect(new Window.DeleteEvent() {
			public boolean onDeleteEvent(Widget source, Event event) {
				source.destroy();
				return false;
			}
		});
	}

	private void setTonesIcon() {
		//find icon, put it in ./ico and set it here
		// try {
		// 	Pixbuf icon = new Pixbuf("ico/" + ico);
		// 	w.setIcon(icon);
		// } catch(Exception ex) {ex.printStackTrace();}
	}

	private void setCenterLocation() {
		int sw = getScreen().getWidth();
		int sh = getScreen().getHeight();
		int x = (sw - getWidth()) / 2;
		int y = (sh - getHeight()) / 2;
		move(x, y);
	}
}