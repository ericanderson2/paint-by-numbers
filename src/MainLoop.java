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
		long currentTime = System.currentTimeMillis();
		double elapsedTime = 1;
		
		while(running) {	
			lastTime = currentTime;
			
			update(elapsedTime);
			draw(elapsedTime);
			
			currentTime = System.currentTimeMillis();
			elapsedTime = (currentTime - lastTime) / 1000d;		
		}
	}

	private void update(double elapsedTime) {
		game.update(elapsedTime);
	}

	private void draw(double elapsedTime) {
		game.draw(elapsedTime);
	}
}