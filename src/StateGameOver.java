import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;

public class StateGameOver extends StateMachine {
	public void run() {
		// Draw menu background
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_BG_MENU),
				0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight, null);

		// Draw the title
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_MENU_DEFEAT),
				SimpleRTS.screenWidth / 2 - 300, 50, 600, 500, null);

		// Message body
		GameFont.setFont(new Font("Comic Sans", Font.PLAIN, 18));
		GameFont.setColor(Color.RED);
		GameFont.printString("Without any forces left, you surrender to the enemy. The war is over.",
				SimpleRTS.screenWidth / 4, 125);

		// Return button
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_ICON_RETURN),
				SimpleRTS.screenWidth / 2 - 100, 350, 200, 50, null);
	}

	public void handleMouseCommand(MouseEvent e) {
		// if game over, go to main menu
		if (SimpleRTS.isMouseInBounds(e,
				SimpleRTS.screenWidth / 2 - 100, SimpleRTS.screenWidth / 2 + 100, 350, 400)) {
			GameUnitManager.init(SimpleRTS.playerList, SimpleRTS.enemyList);
			SimpleRTS.setNewState(SimpleRTS.GameState.STATE_MENU);
		}
	}

}
