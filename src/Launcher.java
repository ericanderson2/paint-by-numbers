public class Launcher {
	public static void main(String[] args) {
		new Thread(new MainLoop(new Game(1200, 675))).start();
	}
}