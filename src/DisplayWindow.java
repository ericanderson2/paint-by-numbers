//these imports are a mess. I'm not sure all of them are currently used either
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
		//window setup
		setTitle("Paint By Numbers");
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		//Select Image Button
		JButton selectImageButton = new JButton("Select Image");
		selectImageButton.setBounds(10, 10, 200, 30);
		selectImageButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				openFileSelector();
			}
		});
		this.add(selectImageButton);
		
		//Reveal Image Button
		JButton revealImageButton = new JButton("Reveal Image");
		revealImageButton.setBounds(10, 50, 200, 30);
		revealImageButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				revealImage();
			}
		});
		this.add(revealImageButton);
		
		//canvas set up.
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setFocusable(false);
		setBackground(Color.BLACK);
		add(canvas);
		addMouseWheelListener(input); //input is essentially deprecated, should be switched to this class as a listener
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
		
		//draw background
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		int sidebarWidth = 220;
		int outlineScale = 2;
		if (game.currentImg != null) {
			//resize the sidebar based on the size of the image's outline
			//scale the outline size so that small and large images end up about the same size
			outlineScale = Math.max(200 / game.currentImg.getWidth(), 2);
			sidebarWidth = Math.max(sidebarWidth, 20 + outlineScale * game.currentImg.getWidth());
		}
		
		//draw sidebard
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fillRect(0, 0, sidebarWidth, canvas.getHeight());
		
		//show fps
		graphics.setColor(Color.WHITE);
		graphics.drawString("FPS: " + (int)(1 / elapsedTime), canvas.getWidth() - 60, 15);
		
		if (game.currentImg != null) {
			int gridSize = (int)(game.DEFAULT_GRID_SIZE * game.zoom);
			
			//draw main paint by number grid
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
							if (col != game.currentImg.getActualColor(x, y)) {
								graphics.drawString(""+game.currentImg.getNumber(x,y), xCoord+gridSize/2 -3, yCoord + gridSize/2 +4);
							}
							if (col == Color.LIGHT_GRAY) {
								graphics.setColor(Color.BLACK);
								graphics.drawRect(xCoord, yCoord, gridSize, gridSize);
								graphics.drawString("" + game.currentImg.getNumber(x, y), xCoord + gridSize / 2 - 3, yCoord + gridSize / 2 + 4);
							}
					}
				}
			}
			
			//redraw sidebar to cover image if user drags it to the left
			graphics.setColor(Color.LIGHT_GRAY);
			graphics.fillRect(0, 0, sidebarWidth, canvas.getHeight());
			
			//draw mini black and white outline
			int outlineX = (sidebarWidth / 2) - (game.currentImg.getWidth() * outlineScale / 2);
			int outlineY = 100;
			for (int x = 0; x < game.currentImg.getWidth(); x++) {
				for (int y = 0; y < game.currentImg.getHeight(); y++) {
					graphics.setColor(game.currentImg.getOutlineColor(x, y));
					graphics.fillRect(outlineX + x * outlineScale, outlineY + y * outlineScale, outlineScale, outlineScale);
				}
			}
			
			//draw color palette
			int paletteRectSize = (sidebarWidth - 20) / 4;
			for (int i = 1; i < game.currentImg.paletteSize(); i++) {
				graphics.setColor(game.currentImg.paletteColor(i));
				graphics.fillRect(10 + ((i - 1) * paletteRectSize) % (sidebarWidth - 20), 20 + outlineY + game.currentImg.getHeight() * outlineScale + (int)(((i - 1) * paletteRectSize) / (sidebarWidth - 20)) * paletteRectSize, paletteRectSize, paletteRectSize);
			}
		}
	
		graphics.dispose();
		bufferStrat.show();
	}
	
	public void mouseMoved(MouseEvent e) {
		//set mouse coords so drag events have a starting point (when calculating the difference between mouse positions over multiple frames)
		game.lastMouseX = e.getX();
		game.lastMouseY = e.getY();
	}
	
	public void mouseDragged(MouseEvent e) {
		if (game.lastMouseX > -1) {
			//drag the paint by numbers image
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
		
		//color the grid space the user clicks on
		if (game.currentImg != null) {
			if (xCoord >= 0 && xCoord < game.currentImg.getWidth()
				&& yCoord >= 0 && yCoord < game.currentImg.getHeight()) {
					game.currentImg.setGridColor(xCoord, yCoord, game.currentImg.colorToPalette(game.currentImg.getActualColor(xCoord, yCoord)));
			}
		}
	}
	
	//these functions must be here as part of their respective interface, but are unused
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	
	//open a popup to let the user choose an image
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
	
	//completely reveal the image
	private void revealImage() {
		if (game.currentImg != null) {
			game.currentImg.revealImage();
		}
	}
}