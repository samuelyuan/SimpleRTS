import graphics.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import ui.UIComponent;
import ui.UIButton;
import ui.UILabel;
import graphics.AwtGraphicsAdapter;
import graphics.IGraphics;

public class StateGameStartLevel extends StateMachine {
	private ArrayList<String> description = null;
	private UIComponent root;
	private GameUnitManager unitManager;
	private SimpleRTS simpleRTS;

	public StateGameStartLevel(SimpleRTS simpleRTS, GameUnitManager unitManager) {
		this.simpleRTS = simpleRTS;
		this.unitManager = unitManager;
		// Load description
		boolean isBegin = true;
		int maxLineWidth = 50;
		description = GameMap.loadMapDescription(GameMap.numLevel, isBegin, maxLineWidth);

		// Font and color for all labels/buttons
		Font font = new Font("Comic Sans", Font.BOLD, 20);
		Color color = Color.BLACK;

		// Build UI tree
		root = new UIComponent(0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight) {
			@Override
			protected void draw(IGraphics g) {
				// Draw background image
				g.drawImage(GameImage.getImage(ImageConstants.IMGID_BG_MENU), 0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight);
				// Draw title image
				g.drawImage(GameImage.getImage(ImageConstants.IMGID_MENU_START), SimpleRTS.screenWidth / 2 - 300, 50, 600, 500);
			}
		};
		// Add description labels
		int printTextLeft = SimpleRTS.screenWidth / 4;
		int printTextTop = 125;
		for (int i = 0; i < description.size(); i++) {
			UILabel label = new UILabel(printTextLeft, printTextTop + i * 20, description.get(i));
			label.setFont(font);
			label.setColor(color);
			root.addChild(label);
		}
		// Add Start button
		int buttonLeftBound = SimpleRTS.screenWidth / 2 - 100;
		int buttonTopMostBound = 350;
		int buttonWidth = 200, buttonHeight = 50;
		UIButton startButton = new UIButton(buttonLeftBound, buttonTopMostBound, buttonWidth, buttonHeight, "Start", () -> {
			if (GameMap.numLevel <= SimpleRTS.MAX_LVL + 1)
				simpleRTS.setNewState(SimpleRTS.GameState.STATE_MAIN);
			else
				simpleRTS.setNewState(SimpleRTS.GameState.STATE_WIN);
		});
		startButton.setFont(font);
		startButton.setColor(color);
		root.addChild(startButton);
		// Add Return button
		UIButton returnButton = new UIButton(buttonLeftBound, buttonTopMostBound + 75, buttonWidth, buttonHeight, "Return", () -> {
			simpleRTS.setNewState(SimpleRTS.GameState.STATE_MENU);
		});
		returnButton.setFont(font);
		returnButton.setColor(color);
		root.addChild(returnButton);
	}

	public void run() {
		root.render(new AwtGraphicsAdapter(SimpleRTS.offscr));
	}

	public void handleMouseCommand(java.awt.event.MouseEvent e) {
		root.handleMouse(e);
	}
}
