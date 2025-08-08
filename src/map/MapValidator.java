package map;

/**
 * Centralized map validation utility for map operations.
 * Provides consistent validation for map bounds, walkable tiles, and wall detection.
 */
public class MapValidator {
    
    /**
     * Checks if the given coordinates are within the map bounds.
     * 
     * @param map The game map
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if coordinates are within bounds, false otherwise
     */
    public static boolean isValidLocation(int[][] map, int x, int y) {
        return x >= 0 && y >= 0 && y < map.length && x < map[0].length;
    }
    
    /**
     * Checks if the tile at the given coordinates is walkable (empty space).
     * 
     * @param map The game map
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if tile is walkable, false otherwise
     */
    public static boolean isWalkable(int[][] map, int x, int y) {
        return isValidLocation(map, x, y) && map[y][x] == 0;
    }
    
    /**
     * Checks if the tile at the given coordinates is a wall.
     * 
     * @param map The game map
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if tile is a wall, false otherwise
     */
    public static boolean isWall(int[][] map, int x, int y) {
        return isValidLocation(map, x, y) && map[y][x] == TileConverter.TILE_WALL;
    }
    
    /**
     * Safely retrieves the tile value at the given coordinates.
     * Returns -1 if coordinates are out of bounds.
     * 
     * @param map The game map
     * @param x X coordinate
     * @param y Y coordinate
     * @return tile value or -1 if out of bounds
     */
    public static int getTileSafely(int[][] map, int x, int y) {
        return isValidLocation(map, x, y) ? map[y][x] : -1;
    }
    
    /**
     * Checks if the tile at the given coordinates is a specific tile type.
     * 
     * @param map The game map
     * @param x X coordinate
     * @param y Y coordinate
     * @param tileType The tile type to check for
     * @return true if tile matches the specified type, false otherwise
     */
    public static boolean isTileType(int[][] map, int x, int y, int tileType) {
        return isValidLocation(map, x, y) && map[y][x] == tileType;
    }
}
