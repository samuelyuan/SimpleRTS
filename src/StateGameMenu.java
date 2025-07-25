import ui.UIComponent;
import ui.UIButton;
import graphics.AwtGraphicsAdapter;
import graphics.IGraphics;
import input.GameMouseEvent;

public class StateGameMenu extends StateMachine {
    private UIComponent root;
    private GameUnitManager unitManager;
    private SimpleRTS simpleRTS;

    public StateGameMenu(SimpleRTS simpleRTS, GameUnitManager unitManager) {
        this.simpleRTS = simpleRTS;
        this.unitManager = unitManager;

        root = new UIComponent(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT) {
            @Override
            protected void draw(IGraphics g) {
                // Draw background image
                g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_BG_MENU), 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
                // Draw title image
                g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_MENU_TITLE), Constants.SCREEN_WIDTH / 2 - 250, 50, 500, 400);
            }
        };

        int optionLeftBound = Constants.SCREEN_WIDTH / 2 - 100;
        int optionTopBound = Constants.SCREEN_HEIGHT / 2 - 200;
        int optionWidth = 200, optionHeight = 50;

        // Campaign button
        UIButton campaignButton = new UIButton(optionLeftBound, optionTopBound, optionWidth, optionHeight, "Campaign", () -> {
            simpleRTS.setNewState(GameState.STATE_STARTLVL);
        });
        root.addChild(campaignButton);

        // Instructions button
        UIButton instructButton = new UIButton(optionLeftBound, optionTopBound + 100, optionWidth, optionHeight, "Instructions", () -> {
            simpleRTS.setNewState(GameState.STATE_INSTRUCT);
        });
        root.addChild(instructButton);
        simpleRTS.clearGameMouseListeners();
        UIComponent.registerAllListeners(root, simpleRTS::addGameMouseListener);
    }

    public void run() {
        root.render(new AwtGraphicsAdapter(SimpleRTS.offscr));
    }

    public void handleMouseCommand(GameMouseEvent e) {
        root.handleMouse(e);
    }
}
