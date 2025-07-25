import java.util.ArrayList;

import ui.UIComponent;
import ui.UIButton;
import ui.UILabel;
import graphics.Color;
import graphics.GameFont;
import graphics.IGraphics;
import input.GameMouseEvent;

public class StateGameStartLevel extends StateMachine {
	private ArrayList<String> description = null;
	private UIComponent root;
	private GameStateManager stateManager;

	public StateGameStartLevel(GameStateManager stateManager) {
		this.stateManager = stateManager;
		// Load description
		boolean isBegin = true;
		int maxLineWidth = 50;
		GameMap gameMap = stateManager.getGameMap();
		description = gameMap.loadMapDescription(gameMap.numLevel, isBegin, maxLineWidth);

		// Font and color for all labels/buttons
		GameFont font = new GameFont("Comic Sans", GameFont.BOLD, 20);
		Color color = Color.BLACK;

		// Build UI tree
		root = new UIComponent(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT) {
			@Override
			protected void draw(IGraphics g) {
				// Draw background image
				g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_BG_MENU), 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
				// Draw title image
				g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_MENU_START), Constants.SCREEN_WIDTH / 2 - 300, 50, 600, 500);
			}
		};
		// Add description labels
		int printTextLeft = Constants.SCREEN_WIDTH / 4;
		int printTextTop = 125;
		for (int i = 0; i < description.size(); i++) {
			UILabel label = new UILabel(printTextLeft, printTextTop + i * 20, description.get(i));
			label.setFont(font);
			label.setColor(color);
			root.addChild(label);
		}
		// Add Start button
		int buttonLeftBound = Constants.SCREEN_WIDTH / 2 - 100;
		int buttonTopMostBound = 350;
		int buttonWidth = 200, buttonHeight = 50;
		UIButton startButton = new UIButton(buttonLeftBound, buttonTopMostBound, buttonWidth, buttonHeight, "Start", () -> {
			GameMap gameMapBtn = stateManager.getGameMap();
			if (gameMapBtn.numLevel <= Constants.MAX_LVL + 1)
				stateManager.setNewState(GameState.STATE_MAIN);
			else
				stateManager.setNewState(GameState.STATE_WIN);
		});
		startButton.setFont(font);
		startButton.setColor(color);
		root.addChild(startButton);
		// Add Return button
		UIButton returnButton = new UIButton(buttonLeftBound, buttonTopMostBound + 75, buttonWidth, buttonHeight, "Return", () -> {
			stateManager.setNewState(GameState.STATE_MENU);
		});
		returnButton.setFont(font);
		returnButton.setColor(color);
		root.addChild(returnButton);
	}

	public void run(IGraphics g) {
		root.render(g);
	}

	public void handleMouseCommand(GameMouseEvent e) {
		root.handleMouse(e);
	}

	@Override
	public ui.UIComponent getRoot() { return root; }
}
