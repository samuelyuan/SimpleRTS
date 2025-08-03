import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import input.GameMouseListener;
import input.MouseListenerRegistrar;
import utils.PathResolver;
import utils.Constants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

/*
 * In every map, the player has to eliminate all the enemy forces.
 */

public class SimpleRTS extends JFrame implements MouseListenerRegistrar, Runnable {
	// Backbuffer data
	private Image offscreenImage;
	private Graphics offscr;
	private int width, height;
	private static final int GAME_WIDTH = Constants.SCREEN_WIDTH;
	private static final int GAME_HEIGHT = Constants.SCREEN_HEIGHT;

	// state handling
	private GameStateManager stateManager;
	private MouseHandler mouseHandler;
	private CameraManager cameraManager;
	private final ImageService imageService;
	
	// JFrame components
	private JPanel gamePanel;

	public SimpleRTS() {
		setTitle("SimpleRTS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(GAME_WIDTH, GAME_HEIGHT);
		setResizable(true); // Allow resizing
		setLocationRelativeTo(null); // Center on screen
		setMinimumSize(new Dimension(800, 600)); // Set minimum size
		
		// Initialize backbuffer first
		width = GAME_WIDTH;
		height = GAME_HEIGHT;
		offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		offscr = offscreenImage.getGraphics();

		// Initialize dependencies
		PathResolver pathResolver = new PathResolver();
		this.imageService = new ImageService(pathResolver);

		// Initialize game logic and resources BEFORE creating the panel
		initializeGameLogic();
		
		// Create game panel after stateManager is initialized
		gamePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// Only render if stateManager is initialized
				if (stateManager != null && stateManager.getCurrentState() != null) {
					// clear back buffer to black
					offscr.setColor(Color.black);
					offscr.fillRect(0, 0, width, height);

					graphics.IGraphics ig = new graphics.AwtGraphicsAdapter(offscr);
					stateManager.getCurrentState().run(ig);
					stateManager.changeState();

					// Calculate scaling to fit the panel while maintaining aspect ratio
					int panelWidth = getWidth();
					int panelHeight = getHeight();
					
					double scaleX = (double) panelWidth / GAME_WIDTH;
					double scaleY = (double) panelHeight / GAME_HEIGHT;
					double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio
					
					int scaledWidth = (int) (GAME_WIDTH * scale);
					int scaledHeight = (int) (GAME_HEIGHT * scale);
					
					// Center the scaled image
					int x = (panelWidth - scaledWidth) / 2;
					int y = (panelHeight - scaledHeight) / 2;
					
					// send back buffer to front buffer with scaling
					g.drawImage(offscreenImage, x, y, scaledWidth, scaledHeight, this);
				}
			}
		};
		
		add(gamePanel);
		
		// Initialize mouse handler
		mouseHandler = new MouseHandler(this, stateManager, cameraManager);
		gamePanel.addMouseListener(mouseHandler);
		gamePanel.addMouseMotionListener(mouseHandler);
		
		// Add keyboard input for camera movement
		gamePanel.setFocusable(true);
		KeyboardHandler keyboardHandler = new KeyboardHandler(cameraManager);
		gamePanel.addKeyListener(keyboardHandler);
		
		// Set initial state in mouse handler
		mouseHandler.setCurrentState(stateManager.getCurrentState());
	}

	private void initializeGameLogic() {
		// Load images using dependency injection
		imageService.loadImages();
		
		// Create shared CameraManager instance
		cameraManager = new CameraManager(this);
		stateManager = new GameStateManager(this, cameraManager, imageService);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 * 
	 * The program begins execution here.
	 */
	public void run() {
		long lastTime = System.nanoTime();
		long currentTime;
		float deltaTime;
		
		while (true) {
			currentTime = System.nanoTime();
			deltaTime = (currentTime - lastTime) / 1_000_000_000.0f; // Convert to seconds
			lastTime = currentTime;
			
			// Cap delta time to prevent large jumps (e.g., when debugging)
			if (deltaTime > 0.1f) {
				deltaTime = 0.1f;
			}
			
			// Update camera every frame for smooth scrolling
			if (cameraManager != null) {
				cameraManager.update(deltaTime);
			}
			
			gamePanel.repaint();
			
			try {
				// Target 60 FPS (16.67ms per frame)
				long sleepTime = 16 - (System.nanoTime() - currentTime) / 1_000_000;
				if (sleepTime > 0) {
					Thread.sleep(sleepTime);
				}
			} catch (InterruptedException e) {
				// Thread interrupted, exit loop
				break;
			}
		}
	}

	// Input abstraction support - delegate to mouse handler
	public void addGameMouseListener(GameMouseListener listener) {
		mouseHandler.addGameMouseListener(listener);
	}

	public void clearGameMouseListeners() {
		mouseHandler.clearGameMouseListeners();
	}
	
	public MouseHandler getMouseHandler() {
		return mouseHandler;
	}
	
	// Main method to launch the application
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			SimpleRTS game = new SimpleRTS();
			game.setVisible(true);
			
			// Start game loop
			Thread gameThread = new Thread(game);
			gameThread.start();
		});
	}
}
