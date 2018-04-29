import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class StateGameMenu extends StateMachine
{
	public Graphics getBuffer()
	{
		return SimpleRTS.offscr;
	}
	
	public void drawImage(int imgId, int x, int y, int width, int height)
	{
		getBuffer().drawImage(GameImage.getImage(imgId), x, y, width, height, null);
	}
	
	public void run()
	{
		//Draw menu background image
		drawImage(GameImage.IMGID_BG_MENU, 0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight);
		
		//Draw menu title
		drawImage(GameImage.IMGID_MENU_TITLE, SimpleRTS.screenWidth / 2 - 250, 50, 500, 400);
		
		//Draw menu options
		int optionLeftBound = SimpleRTS.screenWidth / 2 - 100;
		int optionTopBound = SimpleRTS.screenHeight / 2 - 200;
		int optionWidth = 200, optionHeight = 50;
		drawImage(GameImage.IMGID_ICON_CAMPAIGN, optionLeftBound, optionTopBound, optionWidth, optionHeight);
		drawImage(GameImage.IMGID_ICON_INSTRUCT, optionLeftBound, optionTopBound + 100, optionWidth, optionHeight);
	}
	
	public void handleMouseCommand(MouseEvent e)
	{
		int leftBound = SimpleRTS.screenWidth / 2 - 100;
		int rightBound = leftBound + 200;
		int topBound = SimpleRTS.screenHeight / 2 - 200;
	
		//start game
		if (SimpleRTS.isMouseInBounds(e, 
				leftBound, rightBound, 
				topBound, topBound + 50))
			SimpleRTS.setNewState(SimpleRTS.GameState.STATE_STARTLVL);
		
		//display instructions
		if (SimpleRTS.isMouseInBounds(e, 
				leftBound, rightBound, 
				topBound + 100, topBound + 150))
			SimpleRTS.setNewState(SimpleRTS.GameState.STATE_INSTRUCT);
	}
}
