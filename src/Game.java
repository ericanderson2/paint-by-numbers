import java.lang.Math;
import java.awt.event.KeyEvent;

public class Game {
	private DisplayWindow window;
	private Input input;
	
	private double ZOOM_SPEED = 10.0;
	private double MIN_ZOOM = 0.1;
	private double MAX_ZOOM = 10.0;
	public double zoom = 1;
	
	public Game(int width, int height) {
		input = new Input();
		window = new DisplayWindow(width, height, this, input);
	}

	public void update(double elapsedTime) {
		if (input.getLastWheelEvent() != null) {
			zoom += input.getLastWheelEvent().getPreciseWheelRotation() * input.getLastWheelEvent().getScrollAmount() * elapsedTime * ZOOM_SPEED;
			zoom = Math.min(zoom, MAX_ZOOM);
			zoom = Math.max(zoom, MIN_ZOOM);
		}
		if (input.getLastDragEvent() != null) {
			//drag te image
		}
		input.clearEvents();
	}

	public void draw(double elapsedTime) {
		window.draw(elapsedTime);
	}
}