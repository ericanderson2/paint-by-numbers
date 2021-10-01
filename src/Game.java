import java.awt.event.KeyEvent;

public class Game {
	private DisplayWindow window;
	private Input input;
	
	public Game(int width, int height) {
		input = new Input();
		window = new DisplayWindow(width, height, this, input);
	}

	public void update(double elapsedTime) {
		if (input.isPressed(KeyEvent.VK_W)) {
			System.out.println("test");
		}
	}

	public void draw(double elapsedTime) {
		window.draw(elapsedTime);
	}
}