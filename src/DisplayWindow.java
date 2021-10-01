import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.text.DecimalFormat;

public class DisplayWindow extends JFrame {
	private Canvas canvas;
	private Game game;
	
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
		addMouseListener(input);
		canvas.addMouseMotionListener(input);
		addMouseWheelListener(input);
		pack();

		this.game = game;
		canvas.createBufferStrategy(2);
		setVisible(true);
	}

	public void draw(double elapsedTime) {
		BufferStrategy bufferStrat = canvas.getBufferStrategy();
		Graphics graphics = bufferStrat.getDrawGraphics();
		
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		graphics.setColor(Color.WHITE);
		graphics.drawString("FPS: " + ((int) 60 / elapsedTime), canvas.getWidth() - 60, 15);
		graphics.drawString("ZOOM: " + new DecimalFormat("#.##").format(game.zoom), 15, 15);
		
		graphics.dispose();
		bufferStrat.show();
	}
}