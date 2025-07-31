import graphics.Point;
import map.TileConverter;

/**
 * Handles visibility and line-of-sight calculations for game units.
 */
public class UnitVisibility {
    
    /**
     * Checks if an enemy unit is visible to this unit by performing line-of-sight calculations.
     * 
     * @param map The game map data
     * @param observer The unit doing the observing
     * @param target The unit being observed
     * @return true if the target is visible, false otherwise
     */
    public static boolean checkVisible(int[][] map, GameUnit observer, GameUnit target) {
        Point observerPos = new Point(observer.getMapPoint(observer.getCurrentPosition()));
        Point targetPos = new Point(observer.getMapPoint(target.getCurrentPosition()));

        // Same row
        if (Math.abs(observerPos.y - targetPos.y) <= 1) {
            return checkRowVisible(map, observerPos.x, observerPos.y, targetPos.x, targetPos.y);
        }

        // Same column
        if (Math.abs(observerPos.x - targetPos.x) <= 1) {
            return checkColumnVisible(map, observerPos.x, observerPos.y, targetPos.x, targetPos.y);
        }

        // Otherwise, trace a line of sight between the two tiles and determine whether
        // the line intersects any walls
        return checkDiagonalVisible(map, observerPos.x, observerPos.y, targetPos.x, targetPos.y);
    }
    
    /**
     * Checks visibility along a horizontal line (same row).
     * 
     * @param map The game map data
     * @param entityX1 X coordinate of the observer
     * @param entityY1 Y coordinate of the observer
     * @param entityX2 X coordinate of the target
     * @param entityY2 Y coordinate of the target
     * @return true if the line of sight is clear, false if blocked by a wall
     */
    public static boolean checkRowVisible(int[][] map, int entityX1, int entityY1, int entityX2, int entityY2) {
        int minX = Math.min(entityX1, entityX2);
        int maxX = Math.max(entityX1, entityX2);

        // Check the entire row from start to end, including endpoints
        for (int x = minX; x <= maxX; x++) {
            if (map[entityY1][x] == TileConverter.TILE_WALL) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks visibility along a vertical line (same column).
     * 
     * @param map The game map data
     * @param entityX1 X coordinate of the observer
     * @param entityY1 Y coordinate of the observer
     * @param entityX2 X coordinate of the target
     * @param entityY2 Y coordinate of the target
     * @return true if the line of sight is clear, false if blocked by a wall
     */
    public static boolean checkColumnVisible(int[][] map, int entityX1, int entityY1, int entityX2, int entityY2) {
        int minY = Math.min(entityY1, entityY2);
        int maxY = Math.max(entityY1, entityY2);

        // Check the entire column from start to end, including endpoints
        for (int y = minY; y <= maxY; y++) {
            if (map[y][entityX1] == TileConverter.TILE_WALL) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks visibility along a diagonal line using line-of-sight algorithms.
     * Uses both X and Y axis traversal to ensure accurate wall detection.
     * 
     * @param map The game map data
     * @param entityX1 X coordinate of the observer
     * @param entityY1 Y coordinate of the observer
     * @param entityX2 X coordinate of the target
     * @param entityY2 Y coordinate of the target
     * @return true if the line of sight is clear, false if blocked by a wall
     */
    public static boolean checkDiagonalVisible(int[][] map, int entityX1, int entityY1, int entityX2, int entityY2) {
        double deltaY = entityY2 - entityY1;
        double deltaX = entityX2 - entityX1;

        // Check Y-axis traversal
        if (entityY1 < entityY2) {
            double slope = deltaX / deltaY;
            double curX = entityX1 + 0.5 * slope;
            for (int y = entityY1 + 1; y <= entityY2; y++) {
                if (map[y][(int) Math.round(curX)] == TileConverter.TILE_WALL) {
                    return false;
                }
                curX += slope;
            }
        } else {
            double slope = deltaX / deltaY;
            double curX = entityX1 - 0.5 * slope;
            for (int y = entityY1 - 1; y >= entityY2; y--) {
                if (map[y][(int) Math.round(curX)] == TileConverter.TILE_WALL) {
                    return false;
                }
                curX -= slope;
            }
        }

        // Check X-axis traversal
        if (entityX1 < entityX2) {
            double curY = entityY1 + 0.5 * deltaY / deltaX;
            for (int x = entityX1 + 1; x <= entityX2; x++) {
                if (map[(int) Math.round(curY)][x] == TileConverter.TILE_WALL) {
                    return false;
                }
                curY += deltaY / deltaX;
            }
        } else {
            double curY = entityY1 - 0.5 * deltaY / deltaX;
            for (int x = entityX1 - 1; x >= entityX2; x--) {
                if (map[(int) Math.round(curY)][x] == TileConverter.TILE_WALL) {
                    return false;
                }
                curY -= deltaY / deltaX;
            }
        }

        return true;
    }
    
    /**
     * Calculates the distance between two points using Manhattan distance.
     * 
     * @param point1 First point
     * @param point2 Second point
     * @return Manhattan distance between the points
     */
    public static int calculateDistance(Point point1, Point point2) {
        return Math.abs(point1.x - point2.x) + Math.abs(point1.y - point2.y);
    }
    
    /**
     * Calculates the Euclidean distance between two points.
     * 
     * @param point1 First point
     * @param point2 Second point
     * @return Euclidean distance between the points
     */
    public static double calculateEuclideanDistance(Point point1, Point point2) {
        double deltaX = point1.x - point2.x;
        double deltaY = point1.y - point2.y;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }
} 