import java.awt.Cursor;

import input.MouseListenerRegistrar;

/**
 * Manages camera scrolling and bounds checking for the game.
 */
public class CameraManager {
    
    // Camera scrolling constants
    private static final int SCROLL_AMOUNT = 5;
    private static final int SCROLL_MARGIN = 25;
    private static final int CURSOR_MARGIN = 50;
    
    // Camera bounds (should be configurable based on map size)
    private static final int MAX_CAMERA_X = 400 + SCROLL_AMOUNT;
    private static final int MAX_CAMERA_Y = Constants.SCREEN_HEIGHT;
    
    private int cameraX = 0;
    private int cameraY = 0;
    
    private final MouseListenerRegistrar registrar;
    
    public CameraManager(MouseListenerRegistrar registrar) {
        this.registrar = registrar;
    }
    
    /**
     * Handles camera scrolling based on mouse position near screen edges
     * 
     * @param gameX The game X coordinate of the mouse
     * @param gameY The game Y coordinate of the mouse
     * @return true if scrolling occurred, false otherwise
     */
    public boolean handleCameraScrolling(int gameX, int gameY) {
        int screenWidth = Constants.SCREEN_WIDTH;
        int screenHeight = Constants.SCREEN_HEIGHT;
        
        boolean scrolled = false;
        
        // Scroll right
        if (gameX > screenWidth - SCROLL_MARGIN) {
            addCameraX(SCROLL_AMOUNT);
            setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
            scrolled = true;
        }
        // Scroll left
        else if (gameX < SCROLL_MARGIN) {
            addCameraX(-SCROLL_AMOUNT);
            setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
            scrolled = true;
        }
        // Scroll down
        else if (gameY > screenHeight - SCROLL_MARGIN) {
            addCameraY(SCROLL_AMOUNT);
            setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
            scrolled = true;
        }
        // Scroll up
        else if (gameY < SCROLL_MARGIN) {
            addCameraY(-SCROLL_AMOUNT);
            setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
            scrolled = true;
        }
        
        // Default cursor when not near edges
        if (!scrolled && isInCenterArea(gameX, gameY, screenWidth, screenHeight)) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        
        // Keep camera within bounds
        constrainCameraPosition();
        
        return scrolled;
    }
    
    /**
     * Checks if the mouse is in the center area (not near edges)
     */
    private boolean isInCenterArea(int gameX, int gameY, int screenWidth, int screenHeight) {
        return gameX > CURSOR_MARGIN && gameX < screenWidth - CURSOR_MARGIN
                && gameY > CURSOR_MARGIN && gameY < screenHeight - CURSOR_MARGIN;
    }
    
    /**
     * Constrains camera position to stay within map boundaries
     */
    private void constrainCameraPosition() {
        // Too far left
        if (cameraX < 0) {
            cameraX = 0;
        }
        
        // Too far up
        if (cameraY < 0) {
            cameraY = 0;
        }
        
        // Too far right
        if (cameraX > MAX_CAMERA_X) {
            cameraX = MAX_CAMERA_X;
        }
        
        // Too far down
        if (cameraY > MAX_CAMERA_Y) {
            cameraY = MAX_CAMERA_Y;
        }
    }
    
    /**
     * Sets the cursor for the game window
     */
    private void setCursor(Cursor cursor) {
        if (registrar instanceof SimpleRTS) {
            ((SimpleRTS) registrar).setCursor(cursor);
        }
    }
    
    // Camera getters and setters
    public int getCameraX() {
        return cameraX;
    }
    
    public int getCameraY() {
        return cameraY;
    }
    
    public void setCameraX(int cameraX) {
        this.cameraX = cameraX;
        constrainCameraPosition();
    }
    
    public void setCameraY(int cameraY) {
        this.cameraY = cameraY;
        constrainCameraPosition();
    }
    
    public void addCameraX(int deltaX) {
        this.cameraX += deltaX;
        constrainCameraPosition();
    }
    
    public void addCameraY(int deltaY) {
        this.cameraY += deltaY;
        constrainCameraPosition();
    }
    
    /**
     * Updates camera bounds based on map size
     * 
     * @param mapWidth The width of the current map
     * @param mapHeight The height of the current map
     */
    public void updateCameraBounds(int mapWidth, int mapHeight) {
        // This method can be used to dynamically adjust camera bounds
        // based on the actual map size rather than using hard-coded values
    }
} 