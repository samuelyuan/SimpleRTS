import graphics.GameFont;
import graphics.Color;
import graphics.IGraphics;
import ui.UIComponent;
import ui.UILabel;

/**
 * Handles rendering of the Heads-Up Display (HUD).
 */
public class RendererHUD {
    private final UIComponent hudRoot;
    private final UILabel playerCountLabel;
    private final UILabel enemyCountLabel;
    private final UILabel timerDayLabel;
    private final UILabel timerHourLabel;

    public RendererHUD() {
        this.hudRoot = createHUDRoot();
        this.playerCountLabel = createPlayerCountLabel();
        this.enemyCountLabel = createEnemyCountLabel();
        this.timerDayLabel = createTimerDayLabel();
        this.timerHourLabel = createTimerHourLabel();

        setupHUDHierarchy();
    }

    private UIComponent createHUDRoot() {
        return new UIComponent(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT) {
            @Override
            protected void draw(IGraphics g) {
                // Root doesn't draw anything
            }
        };
    }

    private UILabel createPlayerCountLabel() {
        UILabel label = new UILabel(Constants.SCREEN_WIDTH / 2 - 100,
                Constants.SCREEN_HEIGHT - 100 + 65, "");
        label.setFont(new GameFont("Comic Sans", GameFont.PLAIN, 32));
        label.setColor(Color.WHITE);
        return label;
    }

    private UILabel createEnemyCountLabel() {
        UILabel label = new UILabel(Constants.SCREEN_WIDTH / 2 + 150,
                Constants.SCREEN_HEIGHT - 100 + 65, "");
        label.setFont(new GameFont("Comic Sans", GameFont.PLAIN, 32));
        label.setColor(Color.WHITE);
        return label;
    }

    private UILabel createTimerDayLabel() {
        UILabel label = new UILabel(Constants.SCREEN_WIDTH / 2 - 25,
                Constants.SCREEN_HEIGHT - 100 + 25, "");
        label.setFont(new GameFont("Comic Sans", GameFont.PLAIN, 20));
        label.setColor(Color.WHITE);
        return label;
    }

    private UILabel createTimerHourLabel() {
        UILabel label = new UILabel(Constants.SCREEN_WIDTH / 2 - 25,
                Constants.SCREEN_HEIGHT - 100 + 50, "");
        label.setFont(new GameFont("Comic Sans", GameFont.PLAIN, 20));
        label.setColor(Color.WHITE);
        return label;
    }

    private void setupHUDHierarchy() {
        hudRoot.addChild(playerCountLabel);
        hudRoot.addChild(enemyCountLabel);
        hudRoot.addChild(timerDayLabel);
        hudRoot.addChild(timerHourLabel);
    }

    /**
     * Renders the complete HUD.
     * 
     * @param g           The graphics context
     * @param unitManager The unit manager
     * @param gameTimer   The game timer
     */
    public void renderHUD(IGraphics g, GameUnitManager unitManager, GameTimer gameTimer) {
        updateHUDLabels(unitManager, gameTimer);
        renderHUDBackgrounds(g);
        hudRoot.render(g);
    }

    private void updateHUDLabels(GameUnitManager unitManager, GameTimer gameTimer) {
        playerCountLabel.setText(String.valueOf(unitManager.getPlayerList().size()));
        enemyCountLabel.setText(""); // intentionally left blank to hide enemy count
        timerDayLabel.setText("Day: " + gameTimer.getDay());
        timerHourLabel.setText(gameTimer.getHour() + ":00");
    }

    private void renderHUDBackgrounds(IGraphics g) {
        // Draw player panel background
        g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_SUPPLY_PLAYER),
                Constants.SCREEN_WIDTH / 2 - 250, Constants.SCREEN_HEIGHT - 100, 200, 100);

        // Draw enemy panel background
        g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_SUPPLY_ENEMY),
                Constants.SCREEN_WIDTH / 2 + 100, Constants.SCREEN_HEIGHT - 100, 200, 100);

        // Draw timer background
        g.drawImage(GameImageManager.getImage(ImageConstants.IMGID_GAME_TIMER),
                Constants.SCREEN_WIDTH / 2 - 50, Constants.SCREEN_HEIGHT - 100, 150, 100);
    }

    /**
     * Gets the HUD root component for mouse handling.
     * 
     * @return The HUD root component
     */
    public UIComponent getHUDRoot() {
        return hudRoot;
    }
}