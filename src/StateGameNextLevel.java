import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class StateGameNextLevel extends StateMachine
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

		//Draw the title
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_MENU_VICTORY),
				SimpleRTS.screenWidth / 2 - 300, 50, 600, 500, null);

		GameFont.setFont(new Font("Comic Sans", Font.BOLD, 20));
		GameFont.setColor(Color.BLACK);
		
		//Load description
		if (description == null) 
		{
			boolean isBegin = false;
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
		
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_ICON_CAMPAIGN), 
				buttonLeftBound, buttonTopMostBound, 
				buttonWidth, buttonHeight, null);
	}

	public void handleMouseCommand(MouseEvent e) 
	{
		if (SimpleRTS.isMouseInBounds(e, 
				buttonLeftBound, buttonLeftBound + buttonWidth, 
				buttonTopMostBound, buttonTopMostBound + buttonHeight))
		{
			GameMap.numLevel++;
			
			if (GameMap.numLevel < SimpleRTS.MAX_LVL + 1)
			{
				GameMap.loadMap();
				GameUnitManager.init(SimpleRTS.playerList, SimpleRTS.enemyList);
				SimpleRTS.setNewState(SimpleRTS.GameState.STATE_STARTLVL);
			}
			else
				SimpleRTS.setNewState(SimpleRTS.GameState.STATE_WIN);
		}
	}

}
