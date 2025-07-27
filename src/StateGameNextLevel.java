import java.util.ArrayList;

import graphics.Color;
import graphics.GameFont;
import graphics.IGraphics;
import ui.UIComponent;
import ui.UIButton;
import ui.UILabel;
import input.GameMouseEvent;

public class StateGameNextLevel extends StateMachine {
    private ArrayList<String> description = null;
    private UIComponent root;
    private GameStateManager stateManager;

    public StateGameNextLevel(GameStateManager stateManager) {
        this.stateManager = stateManager;

        // Load description
        boolean isBegin = false;
        int maxLineWidth = 50;
        GameMap gameMap = stateManager.getGameMap();
        description = gameMap.loadMapDescription(gameMap.getNumLevel(), isBegin, maxLineWidth);

        // Font and color for all labels/buttons
        GameFont font = new GameFont("Comic Sans", GameFont.BOLD, 20);
        Color color = Color.BLACK;

        // Build UI tree
        root = new UIComponent(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT) {
            @Override
            protected void draw(IGraphics g) {
                // Draw background image
                g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_BG_MENU), 0, 0, Constants.SCREEN_WIDTH,
                        Constants.SCREEN_HEIGHT);
                // Draw title image
                g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_MENU_VICTORY),
                        Constants.SCREEN_WIDTH / 2 - 300, 50, 600, 500);
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
        // Add Campaign/Next Level button
        int buttonLeftBound = Constants.SCREEN_WIDTH / 2 - 100;
        int buttonTopMostBound = 350;
        int buttonWidth = 200, buttonHeight = 50;
        UIButton nextButton = new UIButton(buttonLeftBound, buttonTopMostBound, buttonWidth, buttonHeight, "Next",
                () -> {
                    GameMap gameMapBtn = stateManager.getGameMap();
                    gameMapBtn.setNumLevel(gameMapBtn.getNumLevel() + 1);
                    if (gameMapBtn.getNumLevel() < Constants.MAX_LVL + 1) {
                        stateManager.setNewState(GameState.STATE_STARTLVL);
                    } else {
                        stateManager.setNewState(GameState.STATE_WIN);
                    }
                });
        nextButton.setFont(font);
        nextButton.setColor(color);
        root.addChild(nextButton);
    }

    public void run(IGraphics g) {
        root.render(g);
    }

    public void handleMouseCommand(GameMouseEvent e) {
        root.handleMouse(e);
    }

    @Override
    public ui.UIComponent getRoot() {
        return root;
    }
}
