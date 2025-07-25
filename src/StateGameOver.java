import graphics.AwtGraphicsAdapter;
import graphics.Color;
import graphics.GameFont;
import graphics.IGraphics;
import input.GameMouseEvent;
import ui.UIComponent;
import ui.UIButton;
import ui.UILabel;

public class StateGameOver extends StateMachine {
    private UIComponent root;
    private GameUnitManager unitManager;
    private SimpleRTS simpleRTS;

    public StateGameOver(SimpleRTS simpleRTS, GameUnitManager unitManager) {
        this.simpleRTS = simpleRTS;
        this.unitManager = unitManager;

        // Fonts and color
        GameFont messageFont = new GameFont("Comic Sans", GameFont.PLAIN, 18);
        Color messageColor = Color.RED;

        root = new UIComponent(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT) {
            @Override
            protected void draw(IGraphics g) {
                // Draw background image
                g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_BG_MENU), 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
                // Draw title image
                g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_MENU_DEFEAT), Constants.SCREEN_WIDTH / 2 - 300, 50, 600, 500);
            }
        };

        // Message label
        UILabel messageLabel = new UILabel(Constants.SCREEN_WIDTH / 4, 125, "Without any forces left, you surrender to the enemy. The war is over.");
        messageLabel.setFont(messageFont);
        messageLabel.setColor(messageColor);
        root.addChild(messageLabel);

        // Return button
        int buttonLeft = Constants.SCREEN_WIDTH / 2 - 100;
        int buttonTop = 350;
        int buttonWidth = 200, buttonHeight = 50;
        UIButton returnButton = new UIButton(buttonLeft, buttonTop, buttonWidth, buttonHeight, "Return", () -> {
            simpleRTS.setNewState(GameState.STATE_MENU);
        });
        returnButton.setFont(messageFont);
        returnButton.setColor(Color.BLACK);
        root.addChild(returnButton);
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
