import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;

public class StateGameWin extends StateMachine {
	public void run() {
		// Draw menu background
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_BG_MENU),
				0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight, null);

		// Draw the title
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_MENU_VICTORY),
				SimpleRTS.screenWidth / 2 - 300, 50, 600, 500, null);

		SimpleRTS.offscr.setColor(Color.BLACK);
		GameFont.setFont(new Font("Comic Sans", Font.BOLD, 16));
		GameFont.printString("War Ends for the Better", SimpleRTS.screenWidth / 2 - 100, 200);

		GameFont.setFont(new Font("Comic Sans", Font.PLAIN, 16));

		GameFont.printString("The enemy surrenders...",
				SimpleRTS.screenWidth / 2 - 100, 225);
		GameFont.printString("Peace shall return...",
				SimpleRTS.screenWidth / 2 - 100, 250);

		// Return button
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_ICON_RETURN),
				SimpleRTS.screenWidth / 2 - 100, 350, 200, 50, null);
	}

	public void handleMouseCommand(MouseEvent e) {
		// If win game, reset.
		if (SimpleRTS.isMouseInBounds(e,
				SimpleRTS.screenWidth / 2 - 100, SimpleRTS.screenWidth / 2 + 100, 350, 400)) {
			GameMap.numLevel = 1;
			GameMap.loadMap();
			GameUnitManager.init(SimpleRTS.playerList, SimpleRTS.enemyList);
			SimpleRTS.setNewState(SimpleRTS.GameState.STATE_MENU);
		}
	}
}
