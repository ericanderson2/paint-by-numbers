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
	private int sidebarWidth;
	private int outlineScale;
	
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
		
		sidebarWidth = 220;
		outlineScale = 2;
		
		setVisible(true);
	}

	public void draw(double elapsedTime) {
		BufferStrategy bufferStrat = canvas.getBufferStrategy();
		Graphics graphics = bufferStrat.getDrawGraphics();
		
		//draw background
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		//draw sidebard
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fillRect(0, 0, sidebarWidth, canvas.getHeight());
		
		//calculate font size
		int gridSize = (int)(game.grid_size * game.zoom);
		Font font = new Font("TimesRoman", Font.PLAIN, (int)(gridSize * 0.8));
		graphics.setFont(font);
		FontMetrics metrics = graphics.getFontMetrics(font);
		
		if (game.currentImg != null) {
			//draw main paint by number grid
			try { //try statement in case user switches the image while this loop is executing
				for (int x = 0; x < game.currentImg.getWidth(); x++) {
					for (int y = 0; y < game.currentImg.getHeight(); y++) {
						int xCoord = gridSize * x + game.grid_offset_x + ((canvas.getWidth() + sidebarWidth) / 2) - ((game.currentImg.getWidth() * gridSize) / 2);
						int yCoord = gridSize * y + game.grid_offset_y + (canvas.getHeight() / 2) - ((game.currentImg.getHeight() * gridSize) / 2);
						
						if (xCoord >= 0 - gridSize && xCoord <= canvas.getWidth()
							&& yCoord >= 0 - gridSize && yCoord <= canvas.getHeight()) {
								if (game.currentImg.getNumber(x, y) > 0) {
									Color col = game.currentImg.getColor(x, y);
									graphics.setColor(col);
									graphics.fillRect(xCoord, yCoord, gridSize, gridSize);
									graphics.setColor(Color.BLACK);
									String text = Integer.toString(game.currentImg.getNumber(x, y));
									if (col != game.currentImg.getActualColor(x, y)) {
										graphics.drawString(text, xCoord + gridSize / 2 - metrics.stringWidth(text) / 2, yCoord + gridSize + gridSize / 2 - metrics.getHeight() / 2 - gridSize / 4);
									}
									if (col == Color.LIGHT_GRAY) {
										/*if (x > 0 && game.currentImg.getNumber(x, y) != game.currentImg.getNumber(x - 1, y)) {
											graphics.drawLine(xCoord, yCoord, xCoord, yCoord + gridSize);
										}
										if (x < game.currentImg.getWidth() - 1 && game.currentImg.getNumber(x, y) != game.currentImg.getNumber(x + 1, y)) {
											graphics.drawLine(xCoord + gridSize, yCoord, xCoord + gridSize, yCoord + gridSize);
										}
										if (y > 0 && game.currentImg.getNumber(x, y) != game.currentImg.getNumber(x, y - 1)) {
											graphics.drawLine(xCoord, yCoord, xCoord + gridSize, yCoord);
										}
										if (y < game.currentImg.getHeight() - 1 && game.currentImg.getNumber(x, y) != game.currentImg.getNumber(x, y + 1)) {
											graphics.drawLine(xCoord, yCoord + gridSize, xCoord + gridSize, yCoord + gridSize);
										}*/
										graphics.drawString(text, xCoord + gridSize / 2 - metrics.stringWidth(text) / 2, yCoord + gridSize + gridSize / 2 - metrics.getHeight() / 2 - gridSize / 4);
										graphics.drawRect(xCoord, yCoord, gridSize, gridSize);
									}
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
					graphics.fillRect(10 + ((i - 1) * paletteRectSize) % (sidebarWidth - 20), 40 + outlineY + game.currentImg.getHeight() * outlineScale + (int)(((i - 1) * paletteRectSize) / (sidebarWidth - 20)) * paletteRectSize, paletteRectSize, paletteRectSize);
				}
				
				font = new Font("TimesRoman", Font.PLAIN, 24);
				graphics.setFont(font);
				metrics = graphics.getFontMetrics(font);
				graphics.setColor(Color.BLACK);
				
				String text = game.currentImg.getName();
				graphics.drawString(text, sidebarWidth / 2 - metrics.stringWidth(text) / 2, (int)(20 - 8 + outlineY + game.currentImg.getHeight() * outlineScale + metrics.getHeight() / 2));
			
			} catch (Exception e) {
				//current image was switched mid draw command
			}
			
			
		}
		
		//show fps
		graphics.setFont(new Font("TimesRoman", Font.PLAIN, 12));
		graphics.setColor(Color.WHITE);
		graphics.drawString("FPS: " + (int)(1 / elapsedTime), canvas.getWidth() - 60, 15);
	
		graphics.dispose();
		bufferStrat.show();
	}
	
	public void mouseMoved(MouseEvent e) {
		//set mouse coords so drag events have a starting point (when calculating the difference between mouse positions over multiple frames)
		game.lastMouseX = e.getX();
		game.lastMouseY = e.getY();
	}
	
	public void mouseDragged(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			if (game.lastMouseX > -1) {
				//drag the paint by numbers image
				game.grid_offset_x += e.getX() - game.lastMouseX;
				game.grid_offset_y += e.getY() - game.lastMouseY;
				game.lastMouseX = e.getX();
				game.lastMouseY = e.getY();
			}
		} else {
			colorGrid(e.getX(), e.getY());
		}
    }
	
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 1) {
			colorGrid(e.getX(), e.getY());
		}
	}
	
	private void colorGrid(int x, int y) {
		if (game.currentImg != null) {
			int gridSize = (int)(game.grid_size * game.zoom);
			int xCoord = (x - game.grid_offset_x - (((canvas.getWidth() + sidebarWidth) / 2) - ((game.currentImg.getWidth() * gridSize) / 2))) / gridSize;
			int yCoord = (y - game.grid_offset_y - ((canvas.getHeight() / 2) - ((game.currentImg.getHeight() * gridSize) / 2))) / gridSize;
			
			//color the grid space the user clicks on
			if (xCoord >= 0 && xCoord < game.currentImg.getWidth()
				&& yCoord >= 0 && yCoord < game.currentImg.getHeight()) {
					//game.currentImg.setGridColor(xCoord, yCoord, game.currentImg.colorToPalette(game.currentImg.getActualColor(xCoord, yCoord)));
					game.currentImg.floodFill(xCoord, yCoord, game.currentImg.colorToPalette(game.currentImg.getActualColor(xCoord, yCoord)));
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
				game.currentImg = new PaintByNumber(img, 4, filename);
			}
			
			game.zeroSettings();
			
			sidebarWidth = 220;
			outlineScale = 2;
			outlineScale = Math.max(game.currentImg.getWidth() / 200, 2);
			sidebarWidth = Math.max(sidebarWidth, 20 + outlineScale * game.currentImg.getWidth());
			
			if ((canvas.getWidth() - sidebarWidth) / game.currentImg.getWidth() < 
				canvas.getHeight() / game.currentImg.getHeight()) {
				game.grid_size = (canvas.getWidth() - sidebarWidth) / (game.currentImg.getWidth() * 2);
			} else {
				game.grid_size = canvas.getHeight() / (game.currentImg.getHeight() * 2);
			}
			game.zoom = 2.0;
			
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