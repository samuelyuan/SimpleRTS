import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import graphics.Color;
import graphics.AwtGraphicsAdapter;
import graphics.IGraphics;
import ui.UIComponent;
import ui.UIButton;
import ui.UILabel;

public class StateGameNextLevel extends StateMachine {
    private ArrayList<String> description = null;
    private UIComponent root;
    private GameUnitManager unitManager;
    private SimpleRTS simpleRTS;

    public StateGameNextLevel(SimpleRTS simpleRTS, GameUnitManager unitManager) {
        this.simpleRTS = simpleRTS;
        this.unitManager = unitManager;

        // Load description
        boolean isBegin = false;
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
                g.drawImage(GameImage.getImage(ImageConstants.IMGID_MENU_VICTORY), SimpleRTS.screenWidth / 2 - 300, 50, 600, 500);
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
        // Add Campaign/Next Level button
        int buttonLeftBound = SimpleRTS.screenWidth / 2 - 100;
        int buttonTopMostBound = 350;
        int buttonWidth = 200, buttonHeight = 50;
        UIButton nextButton = new UIButton(buttonLeftBound, buttonTopMostBound, buttonWidth, buttonHeight, "Next", () -> {
            GameMap.numLevel++;
            if (GameMap.numLevel < SimpleRTS.MAX_LVL + 1) {
                simpleRTS.setNewState(SimpleRTS.GameState.STATE_STARTLVL);
            } else {
                simpleRTS.setNewState(SimpleRTS.GameState.STATE_WIN);
            }
        });
        nextButton.setFont(font);
        nextButton.setColor(color);
        root.addChild(nextButton);
    }

    public void run() {
        root.render(new AwtGraphicsAdapter(SimpleRTS.offscr));
    }

    public void handleMouseCommand(MouseEvent e) {
        root.handleMouse(e);
    }
}
