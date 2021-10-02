//class for storing a paint-by-numbers image
import java.awt.Color;

public class PaintByNumber {
	//the following definitions are just for test purposes. these would change based on the image file selected
	//we will probably have to use ArrayList or something instead of arrays
	private int[][] pixels = {{1, 1, 1}, {2, 1, 2}, {3, 3, 3}}; //ints corresponding to pixel colors
	private int[][] grid = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}}; //ints corresponding to color user has painted in
	private Color[] palette = {Color.LIGHT_GRAY, Color.RED, Color.BLUE, Color.CYAN}; //colors in image. index = grid number corresponding to color
	
	private int width;
	private int height;
	
	public PaintByNumber() {
		width = pixels[0].length;
		height = pixels.length;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public Color getColor(int x, int y) {
		return palette[grid[y][x]];
	}
	
	public int getNumber(int x, int y) {
		return pixels[y][x];
	}
}