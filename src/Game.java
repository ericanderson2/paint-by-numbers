import java.lang.Math;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.FileDialog;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.*;
import javax.swing.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class Game {
	private DisplayWindow window;
	private Input input;
	
	public int grid_size = 5;
	public int GRID_SCREEN_OFFSET_X = 300;
	public int GRID_SCREEN_OFFSET_Y = 100;
	public int grid_offset_x = 0;
	public int grid_offset_y = 0;
	private double MAX_GUI_SCALE = 1.5;
	private double MIN_GUI_SCALE = 0.5;
	public double gui_scale = 1.0;
	
	private double ZOOM_SPEED = 10.0;
	private double MIN_ZOOM = 1.0;
	private double MAX_ZOOM = 10.0;
	public double zoom = 1;
	
	public int lastMouseX = -1;
	public int lastMouseY = -1;
	public int mousePressedX = -1;
	public int mousePressedY = -1;
	
	public PaintByNumber currentImg;
	
	public Game(int width, int height) {
		input = new Input();
		window = new DisplayWindow(width, height, this, input);
		
		currentImg = null;
	}

	public void update(double elapsedTime) {
		//most of this code can be thrown out. we just need to move scroll handling to DisplayWindow
		//there probably won't need to be a game update function since all updates happen as a response
		//to user input, and that all got moved to DisplayWindow
		if (input.getLastKeyEvent() != null) {
			char keyChar = input.getLastKeyEvent().getKeyChar();
			if (keyChar == '=' || keyChar == '+') {
				gui_scale *= 1.1;
			} else if (keyChar == '-' || keyChar == '_') {
				gui_scale *= 0.9;
			} /*else if (keyChar == 'A' || keyChar == 'a') {
				try {		
					FileDialog fd = new FileDialog(window, "Choose a file", FileDialog.LOAD);
					fd.setDirectory(System.getProperty("user.dir"));
					fd.setFile("*.png");
					fd.setVisible(true);
					String filename = fd.getFile();
					if (filename != null) {
						BufferedImage img = ImageIO.read(new File(fd.getDirectory() + filename));
						currentImg = new PaintByNumber(img);
					}
				} catch (IOException e) {
					System.out.println("Error opening dialog and reading image");
					currentImg = null;
				}
			}*/
			gui_scale = Math.min(gui_scale, MAX_GUI_SCALE);
			gui_scale = Math.max(gui_scale, MIN_GUI_SCALE);
		}
		if (input.getLastWheelEvent() != null) {
			zoom += input.getLastWheelEvent().getPreciseWheelRotation() * input.getLastWheelEvent().getScrollAmount() * elapsedTime * ZOOM_SPEED;
			zoom = Math.min(zoom, MAX_ZOOM);
			zoom = Math.max(zoom, MIN_ZOOM);
		}
		/*if (input.getLastPressEvent() != null) {
			lastMouseX = input.getLastPressEvent().getX();
			mousePressedX = lastMouseX;
			lastMouseY = input.getLastPressEvent().getY();
			mousePressedY = lastMouseY;
		}
		if (input.getLastDragEvent() != null) {
			if (lastMouseX > -1 && input.getLastDragEvent().getButton() == 0) {
				grid_offset_x += input.getLastDragEvent().getX() - lastMouseX;
				grid_offset_y += input.getLastDragEvent().getY() - lastMouseY;
			}
			lastMouseX = input.getLastDragEvent().getX();
			lastMouseY = input.getLastDragEvent().getY();
		} else if (input.getLastMoveEvent() != null) {
			lastMouseX = input.getLastMoveEvent().getX();
			lastMouseY = input.getLastMoveEvent().getY();
		}*/
		input.clearEvents();
	}

	public void draw(double elapsedTime) {
		window.draw(elapsedTime);
	}
	
	public void zeroSettings() {
		grid_offset_x = 0;
		grid_offset_y = 0;
		zoom = 1;
		gui_scale = 1.0;
	}
}