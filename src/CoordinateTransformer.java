import utils.Constants;

/**
 * Utility class for transforming coordinates between screen space and game space.
 * Handles the scaling and centering that occurs when the window is resized.
 */
public class CoordinateTransformer {
    
    private static final int GAME_WIDTH = Constants.SCREEN_WIDTH;
    private static final int GAME_HEIGHT = Constants.SCREEN_HEIGHT;
    
    /**
     * Transforms screen coordinates to game coordinates.
     * Accounts for scaling and centering of the game area.
     * 
     * @param screenX The screen X coordinate
     * @param screenY The screen Y coordinate
     * @param panelWidth The current panel width
     * @param panelHeight The current panel height
     * @return The transformed game coordinates, or null if outside game area
     */
    public static GameCoordinates screenToGame(int screenX, int screenY, int panelWidth, int panelHeight) {
        // Calculate scaling to fit the panel while maintaining aspect ratio
        double scaleX = (double) panelWidth / GAME_WIDTH;
        double scaleY = (double) panelHeight / GAME_HEIGHT;
        double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio
        
        int scaledWidth = (int) (GAME_WIDTH * scale);
        int scaledHeight = (int) (GAME_HEIGHT * scale);
        
        // Calculate the offset to center the game area
        int offsetX = (panelWidth - scaledWidth) / 2;
        int offsetY = (panelHeight - scaledHeight) / 2;
        
        // Check if the point is within the game area
        if (screenX < offsetX || screenX > offsetX + scaledWidth ||
            screenY < offsetY || screenY > offsetY + scaledHeight) {
            return null; // Outside game area
        }
        
        // Transform the coordinates
        int gameX = (int) ((screenX - offsetX) / scale);
        int gameY = (int) ((screenY - offsetY) / scale);
        
        return new GameCoordinates(gameX, gameY, scale);
    }
    
    /**
     * Transforms game coordinates to screen coordinates.
     * 
     * @param gameX The game X coordinate
     * @param gameY The game Y coordinate
     * @param panelWidth The current panel width
     * @param panelHeight The current panel height
     * @return The transformed screen coordinates
     */
    public static ScreenCoordinates gameToScreen(int gameX, int gameY, int panelWidth, int panelHeight) {
        // Calculate scaling to fit the panel while maintaining aspect ratio
        double scaleX = (double) panelWidth / GAME_WIDTH;
        double scaleY = (double) panelHeight / GAME_HEIGHT;
        double scale = Math.min(scaleX, scaleY); // Maintain aspect ratio
        
        int scaledWidth = (int) (GAME_WIDTH * scale);
        int scaledHeight = (int) (GAME_HEIGHT * scale);
        
        // Calculate the offset to center the game area
        int offsetX = (panelWidth - scaledWidth) / 2;
        int offsetY = (panelHeight - scaledHeight) / 2;
        
        // Transform the coordinates
        int screenX = (int) (gameX * scale) + offsetX;
        int screenY = (int) (gameY * scale) + offsetY;
        
        return new ScreenCoordinates(screenX, screenY, scale);
    }
    
    /**
     * Gets the current scale factor for the given panel dimensions.
     * 
     * @param panelWidth The current panel width
     * @param panelHeight The current panel height
     * @return The current scale factor
     */
    public static double getScale(int panelWidth, int panelHeight) {
        double scaleX = (double) panelWidth / GAME_WIDTH;
        double scaleY = (double) panelHeight / GAME_HEIGHT;
        return Math.min(scaleX, scaleY);
    }
    
    /**
     * Gets the game area bounds in screen coordinates.
     * 
     * @param panelWidth The current panel width
     * @param panelHeight The current panel height
     * @return The game area bounds
     */
    public static GameAreaBounds getGameAreaBounds(int panelWidth, int panelHeight) {
        double scaleX = (double) panelWidth / GAME_WIDTH;
        double scaleY = (double) panelHeight / GAME_HEIGHT;
        double scale = Math.min(scaleX, scaleY);
        
        int scaledWidth = (int) (GAME_WIDTH * scale);
        int scaledHeight = (int) (GAME_HEIGHT * scale);
        
        int offsetX = (panelWidth - scaledWidth) / 2;
        int offsetY = (panelHeight - scaledHeight) / 2;
        
        return new GameAreaBounds(offsetX, offsetY, scaledWidth, scaledHeight, scale);
    }
    
    /**
     * Container class for game coordinates with scale information.
     */
    public static class GameCoordinates {
        public final int x, y;
        public final double scale;
        
        public GameCoordinates(int x, int y, double scale) {
            this.x = x;
            this.y = y;
            this.scale = scale;
        }
    }
    
    /**
     * Container class for screen coordinates with scale information.
     */
    public static class ScreenCoordinates {
        public final int x, y;
        public final double scale;
        
        public ScreenCoordinates(int x, int y, double scale) {
            this.x = x;
            this.y = y;
            this.scale = scale;
        }
    }
    
    /**
     * Container class for game area bounds information.
     */
    public static class GameAreaBounds {
        public final int offsetX, offsetY, width, height;
        public final double scale;
        
        public GameAreaBounds(int offsetX, int offsetY, int width, int height, double scale) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.width = width;
            this.height = height;
            this.scale = scale;
        }
    }
} 