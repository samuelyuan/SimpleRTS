import java.applet.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import input.GameMouseEvent;
import input.GameMouseListener;
import java.util.List;
import java.util.ArrayList;

/*
 * In every map, the player has to eliminate all the enemy forces.
 */

public class SimpleRTS extends Applet implements MouseMotionListener, MouseListener, Runnable {
	// Backbuffer data
	private Image offscreenImage;
	public static Graphics offscr;
	private int width, height;

	// state handling
	private GameStateManager stateManager;

	public void init() {
		Thread thread = new Thread(this);
		thread.start();

		setSize(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
		addMouseListener(this);
		addMouseMotionListener(this);

		// create backbuffer
		width = getWidth();
		height = getHeight();
		offscreenImage = createImage(width, height);
		offscr = offscreenImage.getGraphics();

		// Initialize game logic and resources
		initializeGameLogic();
	}

	private void initializeGameLogic() {
		GameImageManager.setImgData(GameImagePreloader.loadGameImages());
        GameImageManager.setTileData(GameImagePreloader.loadTileImages());
		GameImageManager.generateDarkImages();

		stateManager = new GameStateManager(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 * 
	 * The program begins execution here.
	 */
	public void run() {
		while (true) {
			repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 * 
	 * The program is split into distinct gamestates. It will execute the current
	 * gamestate and switch
	 * to another state if certain conditions are met.
	 * 
	 * The default gamestate is STATE_MENU (the main menu).
	 */
	public void paint(Graphics g) {
		// clear back buffer to black
		offscr.setColor(Color.black);
		offscr.fillRect(0, 0, width, height);

		graphics.IGraphics ig = new graphics.AwtGraphicsAdapter(offscr);
		stateManager.getCurrentState().run(ig);
		stateManager.changeState();

		// send back buffer to front buffer
		g.drawImage(offscreenImage, 0, 0, this);
	}

	public void update(Graphics g) {
		paint(g);
	}

	/*
	 * The mouse will scroll the world without going out of bounds.
	 */
	public void mouseScrollWorld(MouseEvent e) {
		final int scrollAmount = 5;
		// mouseX = e.getX();
		// mouseY = e.getY();

		final int margin = 25;
		// scroll with mouse
		// scroll right
		if (e.getX() > Constants.SCREEN_WIDTH - margin) {
			stateManager.addCameraX(scrollAmount);
			setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
		}
		// scroll left
		else if (e.getX() < margin) {
			stateManager.addCameraX(-scrollAmount);
			setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
		}

		// scroll down
		if (e.getY() > Constants.SCREEN_HEIGHT - margin) {
			stateManager.addCameraY(scrollAmount);
			setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
		}
		// scroll up
		else if (e.getY() < margin) {
			stateManager.addCameraY(-scrollAmount);
			setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
		}

		// don't scroll at all
		if (e.getX() > 50 && e.getX() < Constants.SCREEN_WIDTH - 50
				&& e.getY() > 50 && e.getY() < Constants.SCREEN_HEIGHT - 50)
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		// keep camera inside map
		// too far left
		if (stateManager.getCameraX() < 0)
			stateManager.setCameraX(0);

		// too far up
		if (stateManager.getCameraY() < 0)
			stateManager.setCameraY(0);

		// Too far right or too far left depend on the size of the level!
		// too far right
		if (stateManager.getCameraX() > 400 + scrollAmount)
			stateManager.setCameraX(400 + scrollAmount);

		// too far down
		if (stateManager.getCameraY() > Constants.SCREEN_HEIGHT)
			stateManager.setCameraY(Constants.SCREEN_HEIGHT);

		// make sure world display changes
		repaint();
	}

	// Input abstraction support
	private List<GameMouseListener> mouseListeners = new ArrayList<>();

	public void addGameMouseListener(GameMouseListener listener) {
		mouseListeners.add(listener);
	}

	public void clearGameMouseListeners() {
		mouseListeners.clear();
	}

	private void dispatchGameMouseEvent(GameMouseEvent event) {
		for (GameMouseListener listener : mouseListeners) {
			listener.onGameMouseEvent(event);
		}
	}

	public void mouseDragged(MouseEvent e) {
		GameMouseEvent ge = new GameMouseEvent(
			GameMouseEvent.Type.DRAGGED, e.getX(), e.getY(), e.getButton()
		);
		dispatchGameMouseEvent(ge);
		if (stateManager.getCurrentState() instanceof StateGameMain)
			Mouse.dragSelectBox(ge);
	}

	public void mouseMoved(MouseEvent e) {
		dispatchGameMouseEvent(new GameMouseEvent(
			GameMouseEvent.Type.MOVED, e.getX(), e.getY(), e.getButton()
		));
		if (stateManager.getCurrentState() instanceof StateGameMain)
			mouseScrollWorld(e);
	}

	public void mouseClicked(MouseEvent e) {
	}

	// Mouse should only be held down when forming selection boxes in-game
	public void mousePressed(MouseEvent e) {
		GameMouseEvent ge = new GameMouseEvent(
			GameMouseEvent.Type.PRESSED, e.getX(), e.getY(), e.getButton()
		);
		dispatchGameMouseEvent(ge);
		if (stateManager.getCurrentState() instanceof StateGameMain)
			Mouse.createSelectBox(ge);
	}

	// Form a selection box in-game
	public void mouseReleased(MouseEvent e) {
		GameMouseEvent ge = new GameMouseEvent(
			GameMouseEvent.Type.RELEASED, e.getX(), e.getY(), e.getButton()
		);
		stateManager.getCurrentState().handleMouseCommand(ge);

		if (stateManager.getCurrentState() instanceof StateGameMain)
			Mouse.releaseSelectBox();
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}
}
