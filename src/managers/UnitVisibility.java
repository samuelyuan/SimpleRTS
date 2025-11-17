package managers;

import entities.GameUnit;
import graphics.Point;
import map.TileConverter;
import map.MapValidator;
import utils.Constants;

/**
 * Handles visibility and line-of-sight calculations for game units.
 * Provides efficient algorithms for determining if units can see each other
 * across different types of terrain (horizontal, vertical, and diagonal).
 * Now includes Field of View (FOV) calculations for more realistic visibility.
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
        if (map == null || observer == null || target == null) {
            return false;
        }
        
        Point observerPos = new Point(observer.getMapPoint(observer.getCurrentPosition()));
        Point targetPos = new Point(observer.getMapPoint(target.getCurrentPosition()));
        
        // Validate positions are within map bounds
        if (!MapValidator.isValidLocation(map, observerPos.x, observerPos.y) ||
            !MapValidator.isValidLocation(map, targetPos.x, targetPos.y)) {
            return false;
        }

        // Check if target is within FOV cone
        if (!isWithinFOV(observer, target)) {
            return false;
        }

        // Same row (horizontal line of sight)
        if (Math.abs(observerPos.y - targetPos.y) <= 1) {
            return checkHorizontalVisibility(map, observerPos, targetPos);
        }

        // Same column (vertical line of sight)
        if (Math.abs(observerPos.x - targetPos.x) <= 1) {
            return checkVerticalVisibility(map, observerPos, targetPos);
        }

        // Diagonal line of sight using Bresenham's algorithm
        return checkDiagonalVisibility(map, observerPos, targetPos);
    }
    
    /**
     * Checks if a target unit is within the observer's field of view cone.
     * 
     * @param observer The unit doing the observing
     * @param target The unit being observed
     * @return true if target is within FOV, false otherwise
     */
    public static boolean isWithinFOV(GameUnit observer, GameUnit target) {
        Point observerPos = observer.getCurrentPosition();
        Point targetPos = target.getCurrentPosition();
        
        // Calculate angle from observer to target
        double deltaX = targetPos.x - observerPos.x;
        double deltaY = targetPos.y - observerPos.y;
        double angleToTarget = Math.toDegrees(Math.atan2(deltaY, deltaX));
        
        // Normalize angle to 0-360 range
        if (angleToTarget < 0) {
            angleToTarget += 360.0;
        }
        
        // Get observer's current rotation angle
        double observerRotation = observer.getRotationAngle();
        
        // Calculate the angle difference
        double angleDiff = Math.abs(angleToTarget - observerRotation);
        
        // Handle angle wrapping (e.g., going from 350° to 10°)
        if (angleDiff > 180.0) {
            angleDiff = 360.0 - angleDiff;
        }
        
        // Check if target is within FOV cone
        return angleDiff <= Constants.FOV_HALF_ANGLE;
    }
    
    /**
     * Calculates the angle from one point to another in degrees.
     * 
     * @param from The starting point
     * @param to The target point
     * @return Angle in degrees (0-360)
     */
    public static double calculateAngleToTarget(Point from, Point to) {
        double deltaX = to.x - from.x;
        double deltaY = to.y - from.y;
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));
        
        // Normalize to 0-360 range
        if (angle < 0) {
            angle += 360.0;
        }
        
        return angle;
    }
    
    /**
     * Checks visibility along a horizontal line (same row).
     * 
     * @param map The game map data
     * @param start Starting position
     * @param end Ending position
     * @return true if the line of sight is clear, false if blocked by a wall
     */
    public static boolean checkHorizontalVisibility(int[][] map, Point start, Point end) {
        int minX = Math.min(start.x, end.x);
        int maxX = Math.max(start.x, end.x);
        int y = start.y;

        // Check each tile along the horizontal line
        for (int x = minX; x <= maxX; x++) {
            if (map[y][x] == TileConverter.TILE_WALL) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks visibility along a vertical line (same column).
     * 
     * @param map The game map data
     * @param start Starting position
     * @param end Ending position
     * @return true if the line of sight is clear, false if blocked by a wall
     */
    public static boolean checkVerticalVisibility(int[][] map, Point start, Point end) {
        int minY = Math.min(start.y, end.y);
        int maxY = Math.max(start.y, end.y);
        int x = start.x;

        // Check each tile along the vertical line
        for (int y = minY; y <= maxY; y++) {
            if (map[y][x] == TileConverter.TILE_WALL) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks visibility along a diagonal line using Bresenham's line algorithm.
     * This provides accurate line-of-sight calculation without floating-point issues.
     * 
     * @param map The game map data
     * @param start Starting position
     * @param end Ending position
     * @return true if the line of sight is clear, false if blocked by a wall
     */
    public static boolean checkDiagonalVisibility(int[][] map, Point start, Point end) {
        int x0 = start.x, y0 = start.y;
        int x1 = end.x, y1 = end.y;
        
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        
        int x = x0, y = y0;
        
        while (true) {
            // Check if current tile is a wall
            if (map[y][x] == TileConverter.TILE_WALL) {
                return false;
            }
            
            // Check if we've reached the end point
            if (x == x1 && y == y1) {
                break;
            }
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
        
        return true;
    }
    
    /**
     * Calculates the Manhattan distance between two points.
     * Manhattan distance is the sum of absolute differences of coordinates.
     * 
     * @param point1 First point
     * @param point2 Second point
     * @return Manhattan distance between the points
     */
    public static int calculateManhattanDistance(Point point1, Point point2) {
        return Math.abs(point1.x - point2.x) + Math.abs(point1.y - point2.y);
    }
    
    /**
     * Calculates the Euclidean distance between two points.
     * Euclidean distance is the straight-line distance between points.
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
    
    /**
     * Checks if two units are within a specified range of each other.
     * 
     * @param unit1 First unit
     * @param unit2 Second unit
     * @param maxRange Maximum range to check
     * @return true if units are within range, false otherwise
     */
    public static boolean isWithinRange(GameUnit unit1, GameUnit unit2, int maxRange) {
        Point pos1 = unit1.getMapPoint(unit1.getCurrentPosition());
        Point pos2 = unit2.getMapPoint(unit2.getCurrentPosition());
        return calculateManhattanDistance(pos1, pos2) <= maxRange;
    }
}

