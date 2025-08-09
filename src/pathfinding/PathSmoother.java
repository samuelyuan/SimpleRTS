package pathfinding;

import graphics.Point;
import utils.TileCoordinateConverter;
import map.MapValidator;

/**
 * Handles path smoothing and optimization for movement.
 * Extracted from MovementController to improve separation of concerns
 * and enable different smoothing strategies.
 */
public class PathSmoother {
    
    /**
     * Different smoothing strategies that can be applied to paths
     */
    public enum SmoothingStrategy {
        LINEAR_INTERPOLATION,    // Simple linear smoothing between waypoints
        OBSTACLE_AWARE          // Smoothing that avoids obstacles
    }
    
    /**
     * Result of path smoothing operation
     */
    public static class SmoothingResult {
        public final double targetX;
        public final double targetY;
        public final boolean isValid;
        
        public SmoothingResult(double targetX, double targetY, boolean isValid) {
            this.targetX = targetX;
            this.targetY = targetY;
            this.isValid = isValid;
        }
    }
    
    // Default smoothing factor for linear interpolation
    private static final double DEFAULT_SMOOTHING_FACTOR = 0.3;
    
    /**
     * Calculates the optimized target position for movement.
     * This method avoids object creation for better performance.
     * 
     * @param currentNode The current waypoint
     * @param nextNode The next waypoint (can be null if at end of path)
     * @param strategy The smoothing strategy to use
     * @param map The game map for obstacle detection
     * @return SmoothingResult containing target coordinates
     */
    public static SmoothingResult calculateTargetPosition(PathNode currentNode, PathNode nextNode, 
                                                        SmoothingStrategy strategy, int[][] map) {
        // Convert current node to screen coordinates
        Point currentScreenPos = TileCoordinateConverter.mapToScreen(currentNode.getX(), currentNode.getY());
        double targetX = currentScreenPos.x;
        double targetY = currentScreenPos.y;
        
        // Apply smoothing if we have a next waypoint
        if (nextNode != null) {
            switch (strategy) {
                case LINEAR_INTERPOLATION:
                    return applyLinearInterpolation(currentScreenPos, nextNode, DEFAULT_SMOOTHING_FACTOR);
                case OBSTACLE_AWARE:
                    return applyObstacleAwareSmoothing(currentScreenPos, nextNode, map);
                default:
                    return new SmoothingResult(targetX, targetY, true);
            }
        }
        
        return new SmoothingResult(targetX, targetY, true);
    }
    
    /**
     * Applies linear interpolation smoothing between waypoints
     */
    private static SmoothingResult applyLinearInterpolation(Point currentPos, PathNode nextNode, double smoothingFactor) {
        Point nextScreenPos = TileCoordinateConverter.mapToScreen(nextNode.getX(), nextNode.getY());
        
        double targetX = currentPos.x * (1 - smoothingFactor) + nextScreenPos.x * smoothingFactor;
        double targetY = currentPos.y * (1 - smoothingFactor) + nextScreenPos.y * smoothingFactor;
        
        return new SmoothingResult(targetX, targetY, true);
    }
    
    /**
     * Applies obstacle-aware smoothing that avoids walls and obstacles
     */
    private static SmoothingResult applyObstacleAwareSmoothing(Point currentPos, PathNode nextNode, int[][] map) {
        Point nextScreenPos = TileCoordinateConverter.mapToScreen(nextNode.getX(), nextNode.getY());
        
        // If no map is provided, fall back to linear interpolation
        if (map == null) {
            return applyLinearInterpolation(currentPos, nextNode, DEFAULT_SMOOTHING_FACTOR);
        }
        
        // Check if direct path to smoothed position is blocked
        Point currentMapPos = TileCoordinateConverter.screenToMap(currentPos);
        Point nextMapPos = TileCoordinateConverter.screenToMap(nextScreenPos);
        
        // If there's a direct path, use linear interpolation
        if (isDirectPathClear(currentMapPos, nextMapPos, map)) {
            return applyLinearInterpolation(currentPos, nextNode, DEFAULT_SMOOTHING_FACTOR);
        }
        
        // If blocked, use the original waypoint position
        return new SmoothingResult(currentPos.x, currentPos.y, true);
    }
    
    /**
     * Checks if there's a clear direct path between two map positions
     */
    private static boolean isDirectPathClear(Point start, Point end, int[][] map) {
        // If no map is provided, assume path is clear
        if (map == null) {
            return true;
        }
        
        // Simple line-of-sight check using Bresenham's algorithm
        int x0 = start.x;
        int y0 = start.y;
        int x1 = end.x;
        int y1 = end.y;
        
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;
        
        while (x0 != x1 || y0 != y1) {
            // Check if current position is walkable
            if (!MapValidator.isWalkable(map, x0, y0)) {
                return false;
            }
            
            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x0 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y0 += sy;
            }
        }
        
        return true;
    }
    
    /**
     * Optimized coordinate calculation without object creation.
     * Updates the provided coordinate array directly.
     * 
     * @param node The path node to convert
     * @param coords Array to store [x, y] coordinates
     */
    public static void calculateCoordinates(PathNode node, double[] coords) {
        Point screenPos = TileCoordinateConverter.mapToScreen(node.getX(), node.getY());
        coords[0] = screenPos.x;
        coords[1] = screenPos.y;
    }
    
    /**
     * Gets the default smoothing strategy
     */
    public static SmoothingStrategy getDefaultStrategy() {
        return SmoothingStrategy.LINEAR_INTERPOLATION;
    }
}
