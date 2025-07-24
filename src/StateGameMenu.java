import ui.UIComponent;
import ui.UIButton;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import graphics.AwtGraphicsAdapter;
import graphics.IGraphics;

public class StateGameMenu extends StateMachine {
    private UIComponent root;
    private GameUnitManager unitManager;
    private SimpleRTS simpleRTS;

    public StateGameMenu(SimpleRTS simpleRTS, GameUnitManager unitManager) {
        this.simpleRTS = simpleRTS;
        this.unitManager = unitManager;

        root = new UIComponent(0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight) {
            @Override
            protected void draw(IGraphics g) {
                // Draw background image
                g.drawImage(GameImage.getImage(ImageConstants.IMGID_BG_MENU), 0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight);
                // Draw title image
                g.drawImage(GameImage.getImage(ImageConstants.IMGID_MENU_TITLE), SimpleRTS.screenWidth / 2 - 250, 50, 500, 400);
            }
        };

        int optionLeftBound = SimpleRTS.screenWidth / 2 - 100;
        int optionTopBound = SimpleRTS.screenHeight / 2 - 200;
        int optionWidth = 200, optionHeight = 50;

        // Campaign button
        UIButton campaignButton = new UIButton(optionLeftBound, optionTopBound, optionWidth, optionHeight, "Campaign", () -> {
            simpleRTS.setNewState(SimpleRTS.GameState.STATE_STARTLVL);
        });
        root.addChild(campaignButton);

        // Instructions button
        UIButton instructButton = new UIButton(optionLeftBound, optionTopBound + 100, optionWidth, optionHeight, "Instructions", () -> {
            simpleRTS.setNewState(SimpleRTS.GameState.STATE_INSTRUCT);
        });
        root.addChild(instructButton);
    }

    public void run() {
        root.render(new AwtGraphicsAdapter(SimpleRTS.offscr));
    }

    public void handleMouseCommand(MouseEvent e) {
        root.handleMouse(e);
    }
}
