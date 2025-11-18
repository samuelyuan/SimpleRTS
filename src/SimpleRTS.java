import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import input.GameMouseListener;
import input.MouseListenerRegistrar;
import managers.CameraManager;
import utils.PathResolver;
import utils.Constants;

/*
 * SimpleRTS - Main application class with game loop separated from JFrame
 * This achieves clean separation of concerns with minimal complexity.
 */

public class SimpleRTS extends JFrame implements MouseListenerRegistrar, Runnable {
	// Backbuffer data
	private Image offscreenImage;
	private Graphics offscr;
	private int width, height;
	private static final int GAME_WIDTH = Constants.SCREEN_WIDTH;
	private static final int GAME_HEIGHT = Constants.SCREEN_HEIGHT;

	// Game logic components (separated from UI)
	private GameStateManager stateManager;
	private MouseHandler mouseHandler;
	private CameraManager cameraManager;
	private ImageService imageService;

	// UI components
	private JPanel gamePanel;

	// Game loop control
	private volatile boolean isRunning = false;
	private Thread gameThread;

	public SimpleRTS() {
		setTitle("SimpleRTS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(GAME_WIDTH, GAME_HEIGHT);
		setResizable(true);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(800, 600));

		// Initialize backbuffer
		width = GAME_WIDTH;
		height = GAME_HEIGHT;
		offscreenImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		offscr = offscreenImage.getGraphics();

		// Initialize game logic (separated from UI)
		initializeGameLogic();

		// Create UI components
		createGamePanel();
		add(gamePanel);

		// Setup input handlers
		setupInputHandlers();
	}

	private void initializeGameLogic() {
		// Initialize core services
		PathResolver pathResolver = new PathResolver();
		this.imageService = new ImageService(pathResolver);
		imageService.loadImages();

		// Initialize game managers
		this.cameraManager = new CameraManager(this);
		this.stateManager = new GameStateManager(this, cameraManager, imageService);
		this.mouseHandler = new MouseHandler(this, stateManager, cameraManager);
	}

	private void createGamePanel() {
		gamePanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				renderGame(g);
			}
		};
	}

	private void setupInputHandlers() {
		gamePanel.addMouseListener(mouseHandler);
		gamePanel.addMouseMotionListener(mouseHandler);
		gamePanel.setFocusable(true);

		KeyboardHandler keyboardHandler = new KeyboardHandler(cameraManager);
		gamePanel.addKeyListener(keyboardHandler);

		mouseHandler.setCurrentState(stateManager.getCurrentState());
	}

	private void renderGame(Graphics g) {
		// Create graphics adapter
		graphics.IGraphics ig = new graphics.AwtGraphicsAdapter(offscr);
		
		// Clear back buffer to black using abstraction
		ig.clear(graphics.Color.BLACK);

		// Render game state
		stateManager.getCurrentState().run(ig);
		stateManager.changeState();

		// Calculate scaling to fit the panel while maintaining aspect ratio
		int panelWidth = getWidth();
		int panelHeight = getHeight();

		double scaleX = (double) panelWidth / GAME_WIDTH;
		double scaleY = (double) panelHeight / GAME_HEIGHT;
		double scale = Math.min(scaleX, scaleY);

		int scaledWidth = (int) (GAME_WIDTH * scale);
		int scaledHeight = (int) (GAME_HEIGHT * scale);

		// Center the scaled image
		int x = (panelWidth - scaledWidth) / 2;
		int y = (panelHeight - scaledHeight) / 2;

		// Send back buffer to front buffer with scaling
		g.drawImage(offscreenImage, x, y, scaledWidth, scaledHeight, this);
	}
	


	// Game loop - separated from UI thread
	@Override
	public void run() {
		long lastTime = System.nanoTime();
		long currentTime;
		float deltaTime;

		while (isRunning) {
			currentTime = System.nanoTime();
			deltaTime = (currentTime - lastTime) / 1_000_000_000.0f;
			lastTime = currentTime;

			// Cap delta time to prevent large jumps
			if (deltaTime > 0.1f) {
				deltaTime = 0.1f;
			}

			// Update game logic (separated from rendering)
			updateGameLogic(deltaTime);

			// Trigger UI update on UI thread
			SwingUtilities.invokeLater(() -> gamePanel.repaint());

			try {
				// Target 60 FPS
				long sleepTime = 16 - (System.nanoTime() - currentTime) / 1_000_000;
				if (sleepTime > 0) {
					Thread.sleep(sleepTime);
				}
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	private void updateGameLogic(float deltaTime) {
		// Update camera
		cameraManager.update(deltaTime);

		// Game state updates happen in the render method
		// This separation allows for future expansion of game logic
	}

	public void start() {
		if (!isRunning) {
			isRunning = true;
			gameThread = new Thread(this, "GameLoop-Thread");
			gameThread.start();
		}
	}

	public void stop() {
		isRunning = false;
		if (gameThread != null) {
			try {
				gameThread.join(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	// Input abstraction support
	public void addGameMouseListener(GameMouseListener listener) {
		mouseHandler.addGameMouseListener(listener);
	}

	public void clearGameMouseListeners() {
		mouseHandler.clearGameMouseListeners();
	}

	public MouseHandler getMouseHandler() {
		return mouseHandler;
	}

	// Main method
	public static void main(String[] args) {
		// Initialize configuration and logging
		utils.GameConfig.initialize();
		utils.Logger.setLevel(utils.Logger.Level.valueOf(utils.GameConfig.getString("debug.log_level")));
		
		SwingUtilities.invokeLater(() -> {
			SimpleRTS game = new SimpleRTS();
			game.setVisible(true);
			game.start();
		});
	}
}
