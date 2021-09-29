public class MainLoop implements Runnable {
	private Game game;
	private boolean running;
	
	public MainLoop(Game game) {
		this.game = game;
	}

	@Override
	public void run() {
		running = true;
		long lastTime = System.currentTimeMillis();
		long currentTime;
		
		while(running) {	
			currentTime = System.currentTimeMillis();
			double elapsedTime = (currentTime - lastTime) / 1000d;
			lastTime = currentTime;
			
			update(elapsedTime);
			draw(elapsedTime);
		}
	}

	private void update(double elapsedTime) {
		game.update(elapsedTime);
	}

	private void draw(double elapsedTime) {
		game.draw(elapsedTime);
	}
}