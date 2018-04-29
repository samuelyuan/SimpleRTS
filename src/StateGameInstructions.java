import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;

public class StateGameInstructions extends StateMachine
{
	public void run()
	{
		//draw background
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_BG_MENU), 
				0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight, null);
		
		//title
		SimpleRTS.offscr.setColor(Color.BLACK);
		GameFont.setFont(new Font("Comic Sans", Font.BOLD, 20));
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_MENU_INSTRUCT),
				SimpleRTS.screenWidth / 2 - 300, 50, 600, 400, null);
		
		//Game text
		GameFont.changeFont(Font.BOLD , 18);
		GameFont.printString("About", SimpleRTS.screenWidth / 2 - 25, 150);
		
		GameFont.changeFont(Font.PLAIN, 16);
		GameFont.printString("The player commands allied (blue) units to defeat enemy (red) units.", 
				SimpleRTS.screenWidth / 2 - 250, 175);
		
		GameFont.changeFont(Font.BOLD , 18);
		GameFont.printString("Controls", SimpleRTS.screenWidth / 2 - 25, 200);
		
		GameFont.changeFont(Font.PLAIN, 16);
		GameFont.printString("First select all units by left clicking and dragging the mouse", 
				SimpleRTS.screenWidth / 2 - 250, 225);
		GameFont.printString("Then, right click a point on the map as the destination.", 
				SimpleRTS.screenWidth / 2 - 250, 250);

		//Return button
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_ICON_RETURN), 
				SimpleRTS.screenWidth / 2 - 100, 350, 200, 50, null);
	}
	
	public void handleMouseCommand(MouseEvent e)
	{
		if (SimpleRTS.isMouseInBounds(e, 
				SimpleRTS.screenWidth / 2 - 100, SimpleRTS.screenWidth / 2 + 100, 350, 400))
			SimpleRTS.setNewState(SimpleRTS.GameState.STATE_MENU);
	}

}
