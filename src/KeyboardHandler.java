import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import utils.Constants;

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
            case KeyEvent.VK_F:
                // Toggle FOV rendering on/off
                Constants.FOV_RENDERING_ENABLED = !Constants.FOV_RENDERING_ENABLED;
                System.out.println("FOV Rendering: " + (Constants.FOV_RENDERING_ENABLED ? "ON" : "OFF"));
                break;
            case KeyEvent.VK_E:
                // Toggle enemy FOV visibility (for debugging)
                Constants.FOV_SHOW_ENEMY_UNITS = !Constants.FOV_SHOW_ENEMY_UNITS;
                System.out.println("Enemy FOV: " + (Constants.FOV_SHOW_ENEMY_UNITS ? "ON" : "OFF"));
                break;
            case KeyEvent.VK_T:
                // Toggle selected-only FOV mode (using T instead of S to avoid conflict)
                Constants.FOV_SHOW_SELECTED_ONLY = !Constants.FOV_SHOW_SELECTED_ONLY;
                System.out.println("Selected-only FOV: " + (Constants.FOV_SHOW_SELECTED_ONLY ? "ON" : "OFF"));
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