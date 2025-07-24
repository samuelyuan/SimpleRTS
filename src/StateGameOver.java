import ui.UIComponent;
import ui.UIButton;
import ui.UILabel;
import graphics.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import graphics.AwtGraphicsAdapter;
import graphics.IGraphics;

public class StateGameOver extends StateMachine {
    private UIComponent root;
    private GameUnitManager unitManager;
    private SimpleRTS simpleRTS;

    public StateGameOver(SimpleRTS simpleRTS, GameUnitManager unitManager) {
        this.simpleRTS = simpleRTS;
        this.unitManager = unitManager;

        // Fonts and color
        Font messageFont = new Font("Comic Sans", Font.PLAIN, 18);
        Color messageColor = Color.RED;

        root = new UIComponent(0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight) {
            @Override
            protected void draw(IGraphics g) {
                // Draw background image
                g.drawImage(GameImage.getImage(ImageConstants.IMGID_BG_MENU), 0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight);
                // Draw title image
                g.drawImage(GameImage.getImage(ImageConstants.IMGID_MENU_DEFEAT), SimpleRTS.screenWidth / 2 - 300, 50, 600, 500);
            }
        };

        // Message label
        UILabel messageLabel = new UILabel(SimpleRTS.screenWidth / 4, 125, "Without any forces left, you surrender to the enemy. The war is over.");
        messageLabel.setFont(messageFont);
        messageLabel.setColor(messageColor);
        root.addChild(messageLabel);

        // Return button
        int buttonLeft = SimpleRTS.screenWidth / 2 - 100;
        int buttonTop = 350;
        int buttonWidth = 200, buttonHeight = 50;
        UIButton returnButton = new UIButton(buttonLeft, buttonTop, buttonWidth, buttonHeight, "Return", () -> {
            simpleRTS.setNewState(SimpleRTS.GameState.STATE_MENU);
        });
        returnButton.setFont(messageFont);
        returnButton.setColor(Color.BLACK);
        root.addChild(returnButton);
    }

    public void run() {
        root.render(new AwtGraphicsAdapter(SimpleRTS.offscr));
    }

    public void handleMouseCommand(MouseEvent e) {
        root.handleMouse(e);
    }
}
