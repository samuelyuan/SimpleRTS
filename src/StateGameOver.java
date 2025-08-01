import graphics.Color;
import graphics.GameFont;
import graphics.IGraphics;
import input.GameMouseEvent;
import ui.UIComponent;
import ui.UIButton;
import ui.UILabel;
import utils.Constants;

public class StateGameOver extends StateMachine {
    private UIComponent root;
    private GameStateManager stateManager;
    private final ImageService imageService;

    public StateGameOver(GameStateManager stateManager) {
        this.imageService = stateManager.getImageService();
        this.stateManager = stateManager;

        // Fonts and color
        GameFont messageFont = new GameFont("Comic Sans", GameFont.PLAIN, 18);
        Color messageColor = Color.RED;

        root = new UIComponent(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT) {
            @Override
            protected void draw(IGraphics g) {
                // Draw background image
                g.drawImage(imageService.getGameImage(ImageConstants.IMGID_BG_MENU), 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
                // Draw title image
                g.drawImage(imageService.getGameImage(ImageConstants.IMGID_MENU_DEFEAT), Constants.SCREEN_WIDTH / 2 - 300, 50, 600, 500);
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
            stateManager.setNewState(GameState.STATE_MENU);
        });
        returnButton.setFont(messageFont);
        returnButton.setColor(Color.BLACK);
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
