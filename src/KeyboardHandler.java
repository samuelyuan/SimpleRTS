import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import utils.GameConfig;
import utils.Logger;

/**
 * Handles keyboard input for the game.
 * Manages camera movement and other keyboard-based controls.
 */
public class KeyboardHandler extends KeyAdapter {

    private final CameraManager cameraManager;

    public KeyboardHandler(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                cameraManager.setKeyRight(true);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                cameraManager.setKeyLeft(true);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                cameraManager.setKeyDown(true);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                cameraManager.setKeyUp(true);
                break;
            // FOV Debug Controls
            case KeyEvent.VK_F3:
                // Toggle FOV rendering on/off
                GameConfig.toggleFovRendering();
                GameConfig.toggleFovShowEnemyUnits();
                Logger.info("FOV Rendering: " + (GameConfig.isFovRenderingEnabled() ? "ON" : "OFF"));
                Logger.info("Enemy FOV: " + (GameConfig.isFovShowEnemyUnits() ? "ON" : "OFF"));
                break;

            // Pathfinding debug controls
            case KeyEvent.VK_F4:
                GameConfig.setShowPaths(!GameConfig.isShowPaths());
                Logger.info("Show Paths: " + (GameConfig.isShowPaths() ? "ON" : "OFF"));
                break;

            case KeyEvent.VK_F5:
                // Toggle enhanced pathfinding visualization (all map nodes with color coding)
                GameConfig.setShowAllMapNodes(!GameConfig.isShowAllMapNodes());
                Logger.info("Enhanced Pathfinding Visualization: " + (GameConfig.isShowAllMapNodes() ? "ON" : "OFF"));
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                cameraManager.setKeyRight(false);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                cameraManager.setKeyLeft(false);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                cameraManager.setKeyDown(false);
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                cameraManager.setKeyUp(false);
                break;
        }
    }
}