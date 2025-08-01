import graphics.Color;
import graphics.GameFont;
import graphics.IGraphics;
import input.GameMouseEvent;
import ui.UIButton;
import ui.UIComponent;
import ui.UILabel;
import utils.Constants;

public class StateGameInstructions extends StateMachine {
    private UIComponent root;
    private final ImageService imageService;

    public StateGameInstructions(GameStateManager gameStateManager) {
        this.imageService = gameStateManager.getImageService();
        // Fonts and color
        GameFont mainFont = new GameFont("Comic Sans", GameFont.BOLD, 20);
        GameFont sectionFont = new GameFont("Comic Sans", GameFont.BOLD, 18);
        GameFont bodyFont = new GameFont("Comic Sans", GameFont.PLAIN, 16);
        Color color = Color.BLACK;

        root = new UIComponent(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT) {
            @Override
            protected void draw(IGraphics g) {
                // Draw background image
                g.drawImage(imageService.getGameImage(ImageConstants.IMGID_BG_MENU), 0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
                // Draw title image
                g.drawImage(imageService.getGameImage(ImageConstants.IMGID_MENU_INSTRUCT), Constants.SCREEN_WIDTH / 2 - 300, 50, 600, 400);
            }
        };

        // Section: About
        UILabel aboutHeader = new UILabel(Constants.SCREEN_WIDTH / 2 - 25, 150, "About");
        aboutHeader.setFont(sectionFont);
        aboutHeader.setColor(color);
        root.addChild(aboutHeader);

        UILabel aboutBody = new UILabel(Constants.SCREEN_WIDTH / 2 - 250, 175, "The player commands allied (blue) units to defeat enemy (red) units.");
        aboutBody.setFont(bodyFont);
        aboutBody.setColor(color);
        root.addChild(aboutBody);

        // Section: Controls
        UILabel controlsHeader = new UILabel(Constants.SCREEN_WIDTH / 2 - 25, 200, "Controls");
        controlsHeader.setFont(sectionFont);
        controlsHeader.setColor(color);
        root.addChild(controlsHeader);

        UILabel controlsBody1 = new UILabel(Constants.SCREEN_WIDTH / 2 - 250, 225, "First select all units by left clicking and dragging the mouse");
        controlsBody1.setFont(bodyFont);
        controlsBody1.setColor(color);
        root.addChild(controlsBody1);

        UILabel controlsBody2 = new UILabel(Constants.SCREEN_WIDTH / 2 - 250, 250, "Then, right click a point on the map as the destination.");
        controlsBody2.setFont(bodyFont);
        controlsBody2.setColor(color);
        root.addChild(controlsBody2);

        // Return button
        int buttonLeft = Constants.SCREEN_WIDTH / 2 - 100;
        int buttonTop = 350;
        int buttonWidth = 200, buttonHeight = 50;
        UIButton returnButton = new UIButton(buttonLeft, buttonTop, buttonWidth, buttonHeight, "Return", () -> {
            gameStateManager.setNewState(GameState.STATE_MENU);
        });
        returnButton.setFont(mainFont);
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
    public ui.UIComponent getRoot() { return root; }
}
