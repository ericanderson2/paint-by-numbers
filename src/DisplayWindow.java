import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.awt.event.*;

public class DisplayWindow extends JFrame implements MouseMotionListener, MouseListener {
	private Canvas canvas;
	private Game game;
	
	public static final double[][] VERTICAL_ED = {{1,0,-1}, {1,0,-1}, {1,0,-1}};
	public static final double[][] HORIZONTAL_ED = {{1,1,1}, {0,0,0}, {-1,-1,-1}};
	
	public DisplayWindow(int width, int height, Game game, Input input) {
		setTitle("Paint By Numbers");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		//Select Image Button
		JButton selectImageButton = new JButton("Select Image");
		selectImageButton.setBounds(200, 10, 200, 30);
		selectImageButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				openFileSelector();
			}
		});
		this.add(selectImageButton);
		
		//Reveal Image Button
		JButton revealImageButton = new JButton("Reveal Image");
		revealImageButton.setBounds(410, 10, 200, 30);
		revealImageButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				revealImage();
			}
		});
		this.add(revealImageButton);
		
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setFocusable(false);
		setBackground(Color.BLACK);
		add(canvas);
		addKeyListener(input);
		canvas.addMouseListener(input);
		canvas.addMouseMotionListener(input);
		addMouseWheelListener(input);
		pack();
		
		canvas.addMouseMotionListener(this);
		canvas.addMouseListener(this);
		
		this.game = game;
		canvas.createBufferStrategy(2);
		
		setVisible(true);
	}

	public void draw(double elapsedTime) {
		BufferStrategy bufferStrat = canvas.getBufferStrategy();
		Graphics graphics = bufferStrat.getDrawGraphics();
		
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		if (game.currentImg != null) {
			int gridSize = (int)(game.DEFAULT_GRID_SIZE * game.zoom);
			
			for (int x = 0; x < game.currentImg.getWidth(); x++) {
				for (int y = 0; y < game.currentImg.getHeight(); y++) {
					Color col = game.currentImg.getColor(x, y);
					graphics.setColor(col);
					int xCoord = x * gridSize + game.GRID_SCREEN_OFFSET_X + game.grid_offset_x;
					int yCoord = y * gridSize + game.GRID_SCREEN_OFFSET_Y + game.grid_offset_y;
					
					graphics.fillRect(xCoord, yCoord, gridSize, gridSize);
					if (xCoord >= 0 - gridSize && xCoord <= canvas.getWidth()
						&& yCoord >= 0 - gridSize && yCoord <= canvas.getHeight()) {
							graphics.setColor(Color.BLACK);
							graphics.drawString(""+game.currentImg.getNumber(x,y), xCoord+gridSize/2 -3, yCoord + gridSize/2 +4);
							graphics.setColor(col);
							if (col == Color.LIGHT_GRAY) {
								graphics.setColor(Color.BLACK);
								graphics.drawRect(xCoord, yCoord, gridSize, gridSize);
								graphics.drawString("" + game.currentImg.getNumber(x, y), xCoord + gridSize / 2 - 3, yCoord + gridSize / 2 + 4);
							}
					}
				}
			}
		}
		
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fillRect(0, 0, canvas.getWidth(), (int)(100 * game.gui_scale));
		graphics.setColor(Color.BLACK);
		
		int fps = (int)(1 / elapsedTime);
		graphics.drawString("FPS: " + fps, canvas.getWidth() - 60, 15);
		graphics.drawString("ZOOM: " + new DecimalFormat("#.##").format(game.zoom), 10, 15);
		graphics.drawString("[-]/[+] : change GUI scale", 10, 30);
		graphics.drawString("scroll wheel : change image scale", 10, 45);
		//graphics.drawString("Colors: " + game.currentImg.paletteSize(), 10, 60);
		
		/*
		int paletteX = (int)(canvas.getWidth() - game.currentImg.paletteSize() * 50 * game.gui_scale - 25 * game.gui_scale);
		
		for (int i = 1; i < game.currentImg.paletteSize(); i++) {
			graphics.setColor(game.currentImg.paletteColor(i));
			graphics.fillRect((int)(i * 50 * game.gui_scale + paletteX), (int)(25 * game.gui_scale), (int)(50 * game.gui_scale), (int)(50 * game.gui_scale));
		}
		*/
		graphics.setColor(Color.YELLOW);
		graphics.drawLine(canvas.getWidth() - 60, 200, (int)(canvas.getWidth() - 60 + 50 * Math.cos(game.debug_indicator_angle)), (int)(200 + 50 * Math.sin(game.debug_indicator_angle)));
		
		graphics.dispose();
		bufferStrat.show();
	}
	
	public void mouseMoved(MouseEvent e) {
		game.lastMouseX = e.getX();
		game.lastMouseY = e.getY();
	}
	
	public void mouseDragged(MouseEvent e) {
		if (game.lastMouseX > -1) {
			game.grid_offset_x += e.getX() - game.lastMouseX;
			game.grid_offset_y += e.getY() - game.lastMouseY;
			game.lastMouseX = e.getX();
			game.lastMouseY = e.getY();
		}
    }
	
	public void mouseClicked(MouseEvent e) {
		int gridSize = (int)(game.DEFAULT_GRID_SIZE * game.zoom);
		int xCoord = (e.getX() - game.GRID_SCREEN_OFFSET_X - game.grid_offset_x) / gridSize;
		int yCoord = (e.getY() - game.GRID_SCREEN_OFFSET_Y - game.grid_offset_y) / gridSize;
		
		if (game.currentImg != null) {
			if (xCoord >= 0 && xCoord < game.currentImg.getWidth()
				&& yCoord >= 0 && yCoord < game.currentImg.getHeight()) {
					game.currentImg.setGridColor(xCoord, yCoord, game.currentImg.colorToPalette(game.currentImg.getActualColor(xCoord, yCoord)));
			}
		}
	}
	
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
	private void openFileSelector() {
		try {		
			FileDialog fd = new FileDialog(this, "Choose a file", FileDialog.LOAD);
			fd.setDirectory(System.getProperty("user.dir"));
			fd.setFile("*.png");
			fd.setVisible(true);
			String filename = fd.getFile();
			if (filename != null) {
				BufferedImage img = ImageIO.read(new File(fd.getDirectory() + filename));
				game.currentImg = new PaintByNumber(img);
			}
		} catch (IOException e) {
			System.out.println("Error opening dialog and reading image");
			game.currentImg = null;
		}
	}
	
	private void revealImage() {
		if (game.currentImg != null) {
			game.currentImg.revealImage();
		}
	}
}