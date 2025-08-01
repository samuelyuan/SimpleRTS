package utils;
import graphics.Point;

/**
 * Utility class for coordinate conversions between screen and map coordinates.
 * Centralizes the logic for converting between different coordinate systems.
 */
public class TileCoordinateConverter {
    
    /**
     * Converts screen coordinates to map coordinates.
     * @param screenPoint The point in screen coordinates
     * @return The corresponding map coordinates
     */
    public static Point screenToMap(Point screenPoint) {
        return new Point(
            screenPoint.x / Constants.TILE_WIDTH, 
            screenPoint.y / Constants.TILE_HEIGHT
        );
    }
    
    /**
     * Converts map coordinates to screen coordinates.
     * @param mapPoint The point in map coordinates
     * @return The corresponding screen coordinates
     */
    public static Point mapToScreen(Point mapPoint) {
        return new Point(
            mapPoint.x * Constants.TILE_WIDTH, 
            mapPoint.y * Constants.TILE_HEIGHT
        );
    }
    
    /**
     * Converts individual map coordinates to screen coordinates.
     * @param mapX The X coordinate in map space
     * @param mapY The Y coordinate in map space
     * @return The corresponding screen coordinates
     */
    public static Point mapToScreen(int mapX, int mapY) {
        return new Point(
            mapX * Constants.TILE_WIDTH, 
            mapY * Constants.TILE_HEIGHT
        );
    }
    
    /**
     * Converts individual screen coordinates to map coordinates.
     * @param screenX The X coordinate in screen space
     * @param screenY The Y coordinate in screen space
     * @return The corresponding map coordinates
     */
    public static Point screenToMap(int screenX, int screenY) {
        return new Point(
            screenX / Constants.TILE_WIDTH, 
            screenY / Constants.TILE_HEIGHT
        );
    }
    
    /**
     * Gets the center point of a tile in screen coordinates.
     * @param mapX The X coordinate in map space
     * @param mapY The Y coordinate in map space
     * @return The center point of the tile in screen coordinates
     */
    public static Point getTileCenter(int mapX, int mapY) {
        return new Point(
            mapX * Constants.TILE_WIDTH + Constants.TILE_WIDTH / 2,
            mapY * Constants.TILE_HEIGHT + Constants.TILE_HEIGHT / 2
        );
    }
    
    /**
     * Gets the center point of a tile in screen coordinates.
     * @param mapPoint The point in map coordinates
     * @return The center point of the tile in screen coordinates
     */
    public static Point getTileCenter(Point mapPoint) {
        return getTileCenter(mapPoint.x, mapPoint.y);
    }
    
    /**
     * Converts screen coordinates to map coordinates with camera offset.
     * @param screenPoint The point in screen coordinates
     * @param cameraX The camera X offset
     * @param cameraY The camera Y offset
     * @return The corresponding map coordinates
     */
    public static Point screenToMapWithCamera(Point screenPoint, int cameraX, int cameraY) {
        return new Point(
            (screenPoint.x + cameraX) / Constants.TILE_WIDTH,
            (screenPoint.y + cameraY) / Constants.TILE_HEIGHT
        );
    }
    
    /**
     * Converts map coordinates to screen coordinates with camera offset.
     * @param mapPoint The point in map coordinates
     * @param cameraX The camera X offset
     * @param cameraY The camera Y offset
     * @return The corresponding screen coordinates
     */
    public static Point mapToScreenWithCamera(Point mapPoint, int cameraX, int cameraY) {
        Point screenPoint = mapToScreen(mapPoint);
        return new Point(
            screenPoint.x - cameraX,
            screenPoint.y - cameraY
        );
    }
    
    /**
     * Calculates Manhattan distance between two map coordinates.
     * @param mapPoint1 First map point
     * @param mapPoint2 Second map point
     * @return Manhattan distance in tiles
     */
    public static int manhattanDistance(Point mapPoint1, Point mapPoint2) {
        return Math.abs(mapPoint1.x - mapPoint2.x) + Math.abs(mapPoint1.y - mapPoint2.y);
    }
    
    /**
     * Calculates Manhattan distance between two screen coordinates.
     * @param screenPoint1 First screen point
     * @param screenPoint2 Second screen point
     * @return Manhattan distance in tiles
     */
    public static int manhattanDistanceInTiles(Point screenPoint1, Point screenPoint2) {
        Point mapPoint1 = screenToMap(screenPoint1);
        Point mapPoint2 = screenToMap(screenPoint2);
        return manhattanDistance(mapPoint1, mapPoint2);
    }
} 