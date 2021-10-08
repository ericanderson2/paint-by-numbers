import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

public class DisplayWindow extends JFrame {
	private Canvas canvas;
	private Game game;
	private PaintByNumber testImg;
	
	private double lastFrame = System.currentTimeMillis() / 1000d;
	
	public static final double[][] VERTICAL_ED = {{1,0,-1}, {1,0,-1}, {1,0,-1}};
	public static final double[][] HORIZONTAL_ED = {{1,1,1}, {0,0,0}, {-1,-1,-1}};
	
	public DisplayWindow(int width, int height, Game game, Input input) {
		setTitle("Paint By Numbers");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
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

		this.game = game;
		canvas.createBufferStrategy(2);
		setVisible(true);
		
		try {
			BufferedImage img = ImageIO.read(new File("anders.png"));
			testImg = new PaintByNumber(img);
		} catch (IOException e) {
			System.exit(-1);
		}
	}

	public void draw(double elapsedTime) {
		BufferStrategy bufferStrat = canvas.getBufferStrategy();
		Graphics graphics = bufferStrat.getDrawGraphics();
		
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		int gridSize = (int)(game.DEFAULT_GRID_SIZE * game.zoom);
		
		for (int x = 0; x < testImg.getWidth(); x++) {
			for (int y = 0; y < testImg.getHeight(); y++) {
				Color col = testImg.getActualColor(x, y);
				graphics.setColor(col);
				int xCoord = x * gridSize + game.GRID_SCREEN_OFFSET_X + game.grid_offset_x;
				int yCoord = y * gridSize + game.GRID_SCREEN_OFFSET_Y + game.grid_offset_y;
				
				if (xCoord >= 0 - gridSize && xCoord <= canvas.getWidth()
					&& yCoord >= 0 - gridSize && yCoord <= canvas.getHeight()) {
					if ( (game.mousePressedX>xCoord && game.mousePressedX < xCoord+gridSize)
							&& (game.mousePressedY>yCoord && game.mousePressedY<yCoord + gridSize))
							graphics.fillRect(xCoord, yCoord, gridSize, gridSize);
						graphics.setColor(Color.WHITE);
						graphics.drawString(""+testImg.getNumber(x,y), xCoord+gridSize/2 -3, yCoord + gridSize/2 +4);
						graphics.setColor(col);
						if (col == Color.LIGHT_GRAY) {
							graphics.setColor(Color.BLACK);
							graphics.drawRect(xCoord, yCoord, gridSize, gridSize);
							graphics.drawString("" + testImg.getNumber(x, y), xCoord + gridSize / 2 - 3, yCoord + gridSize / 2 + 4);
						}
				}
			}
		}
		//graphics.drawImage(testImg.edgeDetection(img, VERTICAL_ED, HORIZONTAL_ED),0,80,canvas);
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fillRect(0, 0, canvas.getWidth(), (int)(100 * game.gui_scale));
		graphics.setColor(Color.BLACK);
		//int fps = Math.min((int)(60 / elapsedTime), 60);
		//int fps = (int)(60 / elapsedTime);
		double fps = 1 / (System.currentTimeMillis() / 1000d - lastFrame);
		lastFrame = System.currentTimeMillis() / 1000d;
		graphics.drawString("FPS: " + new DecimalFormat("#.##").format(fps), canvas.getWidth() - 60, 15);
		graphics.drawString("ZOOM: " + new DecimalFormat("#.##").format(game.zoom), 10, 15);
		graphics.drawString("[-]/[+] : change GUI scale", 10, 30);
		graphics.drawString("scroll wheel : change image scale", 10, 45);
		graphics.drawString("Colors: " + testImg.paletteSize(), 10, 60);
		
		/*
		int paletteX = (int)(canvas.getWidth() - testImg.paletteSize() * 50 * game.gui_scale - 25 * game.gui_scale);
		
		for (int i = 1; i < testImg.paletteSize(); i++) {
			graphics.setColor(testImg.paletteColor(i));
			graphics.fillRect((int)(i * 50 * game.gui_scale + paletteX), (int)(25 * game.gui_scale), (int)(50 * game.gui_scale), (int)(50 * game.gui_scale));
		}
		*/
		graphics.setColor(Color.YELLOW);
		graphics.drawLine(canvas.getWidth() - 60, 200, (int)(canvas.getWidth() - 60 + 50 * Math.cos(game.debug_indicator_angle)), (int)(200 + 50 * Math.sin(game.debug_indicator_angle)));
		
		graphics.dispose();
		bufferStrat.show();
	}
}