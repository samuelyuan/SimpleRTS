import ui.UIComponent;
import ui.UIButton;
import graphics.IGraphics;
import input.GameMouseEvent;

public class StateGameMenu extends StateMachine {
    private UIComponent root;
    private GameStateManager stateManager;

    public StateGameMenu(GameStateManager stateManager) {
        this.stateManager = stateManager;

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
            stateManager.setNewState(GameState.STATE_STARTLVL);
        });
        root.addChild(campaignButton);

        // Instructions button
        UIButton instructButton = new UIButton(optionLeftBound, optionTopBound + 100, optionWidth, optionHeight, "Instructions", () -> {
            stateManager.setNewState(GameState.STATE_INSTRUCT);
        });
        root.addChild(instructButton);
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
