import java.awt.event.*;

public class Input implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {
	private boolean[] pressed;
	private MouseWheelEvent lastWheelEvent;
	private MouseEvent lastDragEvent;
	private MouseEvent lastMoveEvent;
	
	public Input() {
		pressed = new boolean[255];
		lastWheelEvent = null;
		lastDragEvent = null;
		lastMoveEvent = null;
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		lastWheelEvent = e;
	}
	
	public void mouseDragged(MouseEvent e) {
		lastDragEvent = e;
	}
	
	public void mouseMoved(MouseEvent e) {
		lastMoveEvent = e;
	}
	
	public void mouseClicked(MouseEvent e) {}
	
	public MouseWheelEvent getLastWheelEvent() {
		return lastWheelEvent;
	}
	
	public MouseEvent getLastDragEvent() {
		return lastDragEvent;
	}
	
	public MouseEvent getLastMoveEvent() {
		return lastMoveEvent;
	}
	
	public void clearEvents() {
		lastWheelEvent = null;
		lastDragEvent = null;
		lastMoveEvent = null;
	}
	
//key input handling
	public boolean isPressed(int keyCode) {
		return pressed[keyCode];
	}
  
	public void keyPressed(KeyEvent e) {
		pressed[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		pressed[e.getKeyCode()] = false;
	}

//functions from interfaces that we probably won't use
	public void keyTyped(KeyEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
}