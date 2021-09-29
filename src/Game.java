public class Game {
	private DisplayWindow window;

	public Game(int width, int height) {
		window = new DisplayWindow(width, height, this);
	}

	public void update(double elapsedTime) {}

	public void draw(double elapsedTime) {
		window.draw(elapsedTime);
	}
}