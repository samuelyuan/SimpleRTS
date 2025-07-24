import java.awt.Font;
import java.awt.event.MouseEvent;

import graphics.AwtGraphicsAdapter;
import graphics.Color;
import graphics.IGraphics;
import ui.UIButton;
import ui.UIComponent;
import ui.UILabel;

public class StateGameInstructions extends StateMachine {
    private UIComponent root;
    private GameUnitManager unitManager;
    private SimpleRTS simpleRTS;

    public StateGameInstructions(SimpleRTS simpleRTS, GameUnitManager unitManager) {
        this.simpleRTS = simpleRTS;
        this.unitManager = unitManager;

        // Fonts and color
        Font mainFont = new Font("Comic Sans", Font.BOLD, 20);
        Font sectionFont = new Font("Comic Sans", Font.BOLD, 18);
        Font bodyFont = new Font("Comic Sans", Font.PLAIN, 16);
        Color color = Color.BLACK;

        root = new UIComponent(0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight) {
            @Override
            protected void draw(IGraphics g) {
                // Draw background image
                g.drawImage(GameImage.getImage(ImageConstants.IMGID_BG_MENU), 0, 0, SimpleRTS.screenWidth, SimpleRTS.screenHeight);
                // Draw title image
                g.drawImage(GameImage.getImage(ImageConstants.IMGID_MENU_INSTRUCT), SimpleRTS.screenWidth / 2 - 300, 50, 600, 400);
            }
        };

        // Section: About
        UILabel aboutHeader = new UILabel(SimpleRTS.screenWidth / 2 - 25, 150, "About");
        aboutHeader.setFont(sectionFont);
        aboutHeader.setColor(color);
        root.addChild(aboutHeader);

        UILabel aboutBody = new UILabel(SimpleRTS.screenWidth / 2 - 250, 175, "The player commands allied (blue) units to defeat enemy (red) units.");
        aboutBody.setFont(bodyFont);
        aboutBody.setColor(color);
        root.addChild(aboutBody);

        // Section: Controls
        UILabel controlsHeader = new UILabel(SimpleRTS.screenWidth / 2 - 25, 200, "Controls");
        controlsHeader.setFont(sectionFont);
        controlsHeader.setColor(color);
        root.addChild(controlsHeader);

        UILabel controlsBody1 = new UILabel(SimpleRTS.screenWidth / 2 - 250, 225, "First select all units by left clicking and dragging the mouse");
        controlsBody1.setFont(bodyFont);
        controlsBody1.setColor(color);
        root.addChild(controlsBody1);

        UILabel controlsBody2 = new UILabel(SimpleRTS.screenWidth / 2 - 250, 250, "Then, right click a point on the map as the destination.");
        controlsBody2.setFont(bodyFont);
        controlsBody2.setColor(color);
        root.addChild(controlsBody2);

        // Return button
        int buttonLeft = SimpleRTS.screenWidth / 2 - 100;
        int buttonTop = 350;
        int buttonWidth = 200, buttonHeight = 50;
        UIButton returnButton = new UIButton(buttonLeft, buttonTop, buttonWidth, buttonHeight, "Return", () -> {
            simpleRTS.setNewState(SimpleRTS.GameState.STATE_MENU);
        });
        returnButton.setFont(mainFont);
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
