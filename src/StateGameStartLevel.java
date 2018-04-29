import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class StateGameStartLevel extends StateMachine
{
	private ArrayList<String> description = null;
	private int buttonLeftBound = SimpleRTS.screenWidth / 2 - 100;
	private int buttonTopMostBound = 350;
	private int buttonWidth = 200, buttonHeight = 50;
	
	public void run() 
	{
		//Draw menu background
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_BG_MENU), 
				0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight, null);

		//Title
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_MENU_START),
				SimpleRTS.screenWidth / 2 - 300, 50, 600, 500, null);
		
		//new font for message body
		GameFont.setColor(Color.BLACK);
		GameFont.setFont(new Font("Comic Sans", Font.BOLD, 20));

		//Load description
		if (description == null) 
		{
			boolean isBegin = true;
			int maxLineWidth = 50; //the line can have at most 70 chars
			description = GameMap.loadMapDescription(GameMap.numLevel, isBegin, maxLineWidth);
		}
		
		//Print each line of the description
		int printTextLeft = SimpleRTS.screenWidth/4;
		int printTextTop = 125;
		for (int i = 0; i < description.size(); i++)
		{
			GameFont.printString(description.get(i), 
					printTextLeft, printTextTop);
			printTextTop += 20;
		}

		//Draw the option buttons
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_ICON_START), 
				buttonLeftBound, buttonTopMostBound, 
				buttonWidth, buttonHeight, null);
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_ICON_RETURN), 
				buttonLeftBound, buttonTopMostBound + 75, 
				buttonWidth, buttonHeight, null);
	}

	public void handleMouseCommand(MouseEvent e) 
	{
		int buttonRightBound = buttonLeftBound + buttonWidth;	
		
		//Clicked on "Start"
		if (SimpleRTS.isMouseInBounds(e, 
				buttonLeftBound, buttonRightBound, 
				buttonTopMostBound, buttonTopMostBound + buttonHeight))
		{
			if (GameMap.numLevel <= SimpleRTS.MAX_LVL + 1)
				SimpleRTS.setNewState(SimpleRTS.GameState.STATE_MAIN);
			else 
				SimpleRTS.setNewState(SimpleRTS.GameState.STATE_WIN);
		}
		
		//Clicked on "Return"
		if (SimpleRTS.isMouseInBounds(e, 
				buttonLeftBound, buttonRightBound, 
				(buttonTopMostBound + 75), (buttonTopMostBound + 75) + buttonHeight))
		{
			SimpleRTS.setNewState(SimpleRTS.GameState.STATE_MENU);
		}
	}

}
