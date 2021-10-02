import java.lang.Math;
import java.awt.event.KeyEvent;

public class Game {
	private DisplayWindow window;
	private Input input;
	
	public int DEFAULT_GRID_SIZE = 15;
	public int GRID_SCREEN_OFFSET_X = 50;
	public int GRID_SCREEN_OFFSET_Y = 50;
	public int grid_offset_x = 0;
	public int grid_offset_y = 0;
	
	private double ZOOM_SPEED = 10.0;
	private double MIN_ZOOM = 1.0;
	private double MAX_ZOOM = 10.0;
	public double zoom = 1;
	
	private int lastMouseX = -1;
	private int lastMouseY = -1;
	
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
		if (input.getLastPressEvent() != null) {
			lastMouseX = input.getLastPressEvent().getX();
			lastMouseY = input.getLastPressEvent().getY();
		}
		if (input.getLastDragEvent() != null) {
			if (lastMouseX > -1) {
				grid_offset_x += input.getLastDragEvent().getX() - lastMouseX;
				grid_offset_y += input.getLastDragEvent().getY() - lastMouseY;
			}
			lastMouseX = input.getLastDragEvent().getX();
			lastMouseY = input.getLastDragEvent().getY();
		} else if (input.getLastMoveEvent() != null) {
			lastMouseX = input.getLastMoveEvent().getX();
			lastMouseY = input.getLastMoveEvent().getY();
		}
		input.clearEvents();
	}

	public void draw(double elapsedTime) {
		window.draw(elapsedTime);
	}
}