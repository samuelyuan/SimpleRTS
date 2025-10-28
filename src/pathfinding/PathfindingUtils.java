package pathfinding;

import graphics.Point;
import map.TileConverter;
import map.MapValidator;
import utils.TileCoordinateConverter;

/**
 * Utility class for pathfinding-related operations that don't require GameUnit
 * objects.
 * Contains methods extracted from GameUnit to improve separation of concerns.
 */
public class PathfindingUtils {
    
    // Constants for alternative destination search
    private static final int MAX_ALTERNATIVE_RADIUS = 4;
    private static final int MAX_ALTERNATIVE_DISTANCE = 3;
    private static final int EARLY_EXIT_DISTANCE = 2;
    
    // Constants for fallback destination search  
    private static final int MAX_FALLBACK_RADIUS = 6;
    private static final int MAX_FALLBACK_DISTANCE = 6;
    private static final int FALLBACK_EARLY_EXIT_DISTANCE = 4;

    /**
     * Finds an alternative destination when pathfinding fails.
     * Searches in expanding circles around the original destination for walkable
     * tiles.
     * 
     * @param map          The game map
     * @param originalDest The original destination that failed
     * @return An alternative destination, or null if none found
     */
    public static Point findAlternativeDestination(int[][] map, Point originalDest) {
        // Search in expanding circles around the original destination
        // Use simple distance-based approach instead of expensive pathfinding tests
        int closestDistance = Integer.MAX_VALUE;
        Point closestTile = null;

        for (int radius = 1; radius <= MAX_ALTERNATIVE_RADIUS; radius++) {
            boolean foundInThisRadius = false;

            for (int dy = -radius; dy <= radius; dy++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    // Skip corners for efficiency
                    if (Math.abs(dx) == radius && Math.abs(dy) == radius) {
                        continue;
                    }

                    int newX = originalDest.x + dx;
                    int newY = originalDest.y + dy;

                    // Check if tile is walkable (simple check, no pathfinding test)
                    if (MapValidator.isWalkable(map, newX, newY)) {
                        int distance = Math.abs(dx) + Math.abs(dy); // Manhattan distance

                        // Only consider tiles within reasonable distance
                        if (distance <= MAX_ALTERNATIVE_DISTANCE && distance < closestDistance) {
                            closestDistance = distance;
                            closestTile = new Point(newX, newY);
                            foundInThisRadius = true;
                        }
                    }
                }
            }

            // If we found a reasonably close tile, use it immediately
            if (foundInThisRadius && closestDistance <= EARLY_EXIT_DISTANCE) {
                break;
            }
        }

        // If we found a tile within reasonable distance, use it
        if (closestTile != null && closestDistance <= MAX_ALTERNATIVE_DISTANCE) {
            return TileCoordinateConverter.mapToScreen(closestTile.x, closestTile.y);
        }

        // If no alternative found, return null
        return null;
    }

    /**
     * Validates if a destination is valid (within bounds and walkable).
     * 
     * @param dest The destination point in screen coordinates
     * @param map  The game map
     * @return true if the destination is valid, false otherwise
     */
    public static boolean isValidDestination(Point dest, int[][] map) {
        if (dest == null)
            return false;

        Point mapPoint = TileCoordinateConverter.screenToMap(dest);

        return MapValidator.isWalkable(map, mapPoint.x, mapPoint.y);
    }

    /**
     * Checks if destination coordinates have changed.
     * 
     * @param currentEndX Current end X coordinate
     * @param currentEndY Current end Y coordinate
     * @param mapEnd      The new destination point
     * @return true if destination changed, false otherwise
     */
    public static boolean destinationChanged(int currentEndX, int currentEndY, Point mapEnd) {
        return currentEndX != mapEnd.x || currentEndY != mapEnd.y;
    }


    /**
     * Finds a fallback destination when recalculateDest fails.
     * Searches in expanding circles around the unit's current position.
     * 
     * @param map        The game map
     * @param currentPos The current position in map coordinates
     * @return A fallback destination, or null if none found
     */
    public static Point findFallbackDestination(int[][] map, Point currentPos) {
        // Search in expanding circles around the unit's current position
        // but prioritize closer destinations to avoid long travel distances
        int closestDistance = Integer.MAX_VALUE;
        Point closestTile = null;

        for (int radius = 1; radius <= MAX_FALLBACK_RADIUS; radius++) {
            boolean foundInThisRadius = false;

            for (int dy = -radius; dy <= radius; dy++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    // Skip corners for efficiency
                    if (Math.abs(dx) == radius && Math.abs(dy) == radius) {
                        continue;
                    }

                    int newX = currentPos.x + dx;
                    int newY = currentPos.y + dy;

                    // Check if tile is walkable
                    if (MapValidator.isWalkable(map, newX, newY)) {
                        int distance = Math.abs(dx) + Math.abs(dy); // Manhattan distance
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestTile = new Point(newX, newY);
                            foundInThisRadius = true;
                        }
                    }
                }
            }

            // If we found a reasonably close tile, use it
            if (foundInThisRadius && closestDistance <= FALLBACK_EARLY_EXIT_DISTANCE) {
                break;
            }
        }

        // If we found a tile within reasonable distance, use it
        if (closestTile != null && closestDistance <= MAX_FALLBACK_DISTANCE) {
            return TileCoordinateConverter.mapToScreen(closestTile.x, closestTile.y);
        }

        // If no fallback found, return null (unit will stay in place)
        return null;
    }
}
