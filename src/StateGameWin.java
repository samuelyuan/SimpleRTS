import ui.UIComponent;
import ui.UIButton;
import ui.UILabel;
import graphics.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import graphics.AwtGraphicsAdapter;
import graphics.IGraphics;

public class StateGameWin extends StateMachine {
    private UIComponent root;
    private GameUnitManager unitManager;
    private SimpleRTS simpleRTS;

    public StateGameWin(SimpleRTS simpleRTS, GameUnitManager unitManager) {
        this.simpleRTS = simpleRTS;
        this.unitManager = unitManager;

        // Fonts and color
        Font titleFont = new Font("Comic Sans", Font.BOLD, 16);
        Font bodyFont = new Font("Comic Sans", Font.PLAIN, 16);
        Color color = Color.BLACK;

        root = new UIComponent(0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight) {
            @Override
            protected void draw(IGraphics g) {
                // Draw background image
                g.drawImage(GameImage.getImage(ImageConstants.IMGID_BG_MENU), 0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight);
                // Draw title image
                g.drawImage(GameImage.getImage(ImageConstants.IMGID_MENU_VICTORY), SimpleRTS.screenWidth / 2 - 300, 50, 600, 500);
            }
        };

        // Title label
        UILabel titleLabel = new UILabel(SimpleRTS.screenWidth / 2 - 100, 200, "War Ends for the Better");
        titleLabel.setFont(titleFont);
        titleLabel.setColor(color);
        root.addChild(titleLabel);

        // Body labels
        UILabel body1 = new UILabel(SimpleRTS.screenWidth / 2 - 100, 225, "The enemy surrenders...");
        body1.setFont(bodyFont);
        body1.setColor(color);
        root.addChild(body1);

        UILabel body2 = new UILabel(SimpleRTS.screenWidth / 2 - 100, 250, "Peace shall return...");
        body2.setFont(bodyFont);
        body2.setColor(color);
        root.addChild(body2);

        // Return button
        int buttonLeft = SimpleRTS.screenWidth / 2 - 100;
        int buttonTop = 350;
        int buttonWidth = 200, buttonHeight = 50;
        UIButton returnButton = new UIButton(buttonLeft, buttonTop, buttonWidth, buttonHeight, "Return", () -> {
            // Reset to first level after win
            GameMap.numLevel = 1;
            simpleRTS.setNewState(SimpleRTS.GameState.STATE_MENU);
        });
        returnButton.setFont(titleFont);
        returnButton.setColor(color);
        root.addChild(returnButton);
    }

    public void run() {
        root.render(new AwtGraphicsAdapter(SimpleRTS.offscr));
    }

    public void handleMouseCommand(MouseEvent e) {
        root.handleMouse(e);
    }
}
