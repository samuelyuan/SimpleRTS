import java.applet.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

/*
 * In every map, the player has to eliminate all the enemy forces.
 */

public class SimpleRTS extends Applet implements MouseMotionListener, MouseListener, Runnable
{
	//camera data
	public static int cameraX = 0, cameraY = 0;

	//map data
	public static final int MAX_LVL = 2;

	//screen 
	public static final int screenWidth = 1076;
	public static final int screenHeight = 768;
	
	//Backbuffer data
	private Image offscreenImage;
	public static Graphics offscr;
	private int width, height;
	
	//unit data
	public static ArrayList<GameUnit> playerList = new ArrayList<GameUnit>();
	public static ArrayList<GameUnit> enemyList = new ArrayList<GameUnit>();

	public static enum GameState
	{
		STATE_NULL, 
		STATE_MENU, 
		STATE_INSTRUCT,
		STATE_STARTLVL,
		STATE_MAIN,
		STATE_NEXTLVL,
		STATE_GAMEOVER,
		STATE_WIN
	};
	
	//state handling
	private static GameState stateID = GameState.STATE_NULL;
	private static GameState nextState = GameState.STATE_NULL;
	private static StateMachine currentState = null;
	
	public static void setNewState(GameState newState)
	{
		nextState = newState;
	}
	
	public static void changeState()
	{
		if (nextState != GameState.STATE_NULL)
		{
			switch (nextState)
			{
				case STATE_MENU: 		currentState = new StateGameMenu(); 			break;					
				case STATE_INSTRUCT: 	currentState = new StateGameInstructions(); 	break;					
				case STATE_MAIN: 		currentState = new StateGameMain(); 			break;
				case STATE_STARTLVL: 	currentState = new StateGameStartLevel(); 		break;					
				case STATE_GAMEOVER: 	currentState = new StateGameOver(); 			break;					
				case STATE_NEXTLVL: 	currentState = new StateGameNextLevel(); 		break;					
				case STATE_WIN: 		currentState = new StateGameWin(); 				break;
			}
			
			stateID = nextState;
			nextState = GameState.STATE_NULL;
		}
	}

	public void init()
	{	
		Thread thread = new Thread(this);
		thread.start();
		
		setSize(screenWidth, screenHeight);

		addMouseListener(this);
		addMouseMotionListener(this);
		
		setFont(new Font("Comic Sans", Font.BOLD, 24));
		GameImage.loadAllImages();
		
		//create backbuffer
		width = getWidth();
		height = getHeight();
		offscreenImage = createImage(width, height);
		offscr = offscreenImage.getGraphics();	
		
		//init player
		GameMap.numLevel = 1;
		GameMap.loadMap();
		GameUnitManager.init(playerList, enemyList);
		
		currentState = new StateGameMenu();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 * 
	 * The program begins execution here.
	 */
	public void run() 
	{
		while (true)
		{
			repaint();
			try
			{
				Thread.sleep(10);
			} catch (InterruptedException e) { }
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 * 
	 * The program is split into distinct gamestates. It will execute the current gamestate and switch
	 * to another state if certain conditions are met.
	 * 
	 * The default gamestate is STATE_MENU (the main menu).
	 */
	public void paint (Graphics g)
	{
		//clear back buffer to black
		offscr.setColor(Color.black);
		offscr.fillRect(0, 0, width, height);
				
		currentState.run();
		changeState();

		//send back buffer to front buffer
		g.drawImage(offscreenImage, 0, 0, this);
	}
	
	public void update(Graphics g)
	{
		paint(g);
	}

	/*
	 * The mouse will scroll the world without going out of bounds.
	 */
	public void mouseScrollWorld(MouseEvent e)
	{	
		final int scrollAmount = 5;
		//mouseX = e.getX();
		//mouseY = e.getY();
	
		final int margin = 25;
		//scroll with mouse
		//scroll right
		if (e.getX() > screenWidth - margin)
		{
			cameraX += scrollAmount;
			setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
		}
		//scroll left
		else if (e.getX() < margin)
		{
			cameraX -= scrollAmount;
			setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
		}
		
		//scroll down
		if (e.getY() > screenHeight - margin)
		{
			cameraY += scrollAmount;
			setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
		}
		//scroll up
		else if (e.getY() < margin)
		{
			cameraY -= scrollAmount;
			setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
		}
		
		//don't scroll at all
		if (e.getX() > 50 && e.getX() < screenWidth - 50
				&& e.getY() > 50 && e.getY() < screenHeight - 50)
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		
		//keep camera inside map
		//too far left
		if (cameraX < 0) 
			cameraX = 0;
		
		//too far up
		if (cameraY < 0)
			cameraY = 0;
		
		//Too far right or too far left depend on the size of the level!
		//too far right
		if (cameraX > 400 + scrollAmount) 
			cameraX = 400 + scrollAmount;
		
		//too far down
		if (cameraY > screenHeight)
			cameraY = screenHeight;
		
		//make sure world display changes
		repaint();
	}
	
	public void mouseDragged(MouseEvent e) 	
	{ 
		if (currentState instanceof StateGameMain)
			Mouse.dragSelectBox(e); 
	}
	
	public void mouseMoved(MouseEvent e) 	
	{
		if (currentState instanceof StateGameMain)
			mouseScrollWorld(e); 
	}
	
	public static boolean isMouseInBounds(MouseEvent e, int left, int right, int top, int bottom)
	{
		if (e.getX() >= left && e.getX() <= right && e.getY() >= top && e.getY() <= bottom)
			return true;
		else 
			return false;
	}
	
	public void mouseClicked(MouseEvent e) 	
	{ 
	}
	
	//Mouse should only be held down when forming selection boxes in-game
	public void mousePressed(MouseEvent e) 	
	{
		if (currentState instanceof StateGameMain)
			Mouse.createSelectBox(e); 
	}
	
	//Form a selection box in-game
	public void mouseReleased(MouseEvent e) 
	{ 
		currentState.handleMouseCommand(e);
		
		if (currentState instanceof StateGameMain)
			Mouse.releaseSelectBox(); 
	}
	
	public void mouseEntered(MouseEvent arg0) { }
	public void mouseExited(MouseEvent arg0) { }
}
