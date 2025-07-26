import graphics.Color;
import graphics.GameFont;
import graphics.IGraphics;
import input.GameMouseEvent;
import ui.UIComponent;
import ui.UIButton;
import ui.UILabel;

public class StateGameWin extends StateMachine {
    private UIComponent root;
    private GameStateManager stateManager;

    public StateGameWin(GameStateManager stateManager) {
        this.stateManager = stateManager;

        // Fonts and color
        GameFont titleFont = new GameFont("Comic Sans", GameFont.BOLD, 16);
        GameFont bodyFont = new GameFont("Comic Sans", GameFont.PLAIN, 16);
        Color color = Color.BLACK;

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

        // Title label
        UILabel titleLabel = new UILabel(Constants.SCREEN_WIDTH / 2 - 100, 200, "War Ends for the Better");
        titleLabel.setFont(titleFont);
        titleLabel.setColor(color);
        root.addChild(titleLabel);

        // Body labels
        UILabel body1 = new UILabel(Constants.SCREEN_WIDTH / 2 - 100, 225, "The enemy surrenders...");
        body1.setFont(bodyFont);
        body1.setColor(color);
        root.addChild(body1);

        UILabel body2 = new UILabel(Constants.SCREEN_WIDTH / 2 - 100, 250, "Peace shall return...");
        body2.setFont(bodyFont);
        body2.setColor(color);
        root.addChild(body2);

        // Return button
        int buttonLeft = Constants.SCREEN_WIDTH / 2 - 100;
        int buttonTop = 350;
        int buttonWidth = 200, buttonHeight = 50;
        UIButton returnButton = new UIButton(buttonLeft, buttonTop, buttonWidth, buttonHeight, "Return", () -> {
            // Reset to first level after win
            GameMap gameMap = stateManager.getGameMap();
            gameMap.numLevel = 1;
            stateManager.setNewState(GameState.STATE_MENU);
        });
        returnButton.setFont(titleFont);
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
    public ui.UIComponent getRoot() {
        return root;
    }
}
