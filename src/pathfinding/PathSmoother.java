package pathfinding;

import graphics.Point;
import utils.TileCoordinateConverter;

/**
 * Handles path smoothing for movement using linear interpolation.
 * Provides simple smoothing between waypoints to create more natural movement.
 */
public class PathSmoother {
    
    // Smoothing factor for linear interpolation (30% towards next waypoint)
    private static final double SMOOTHING_FACTOR = 0.3;
    
    /**
     * Calculates the smoothed target position between current and next waypoint.
     * Updates the provided double array with smoothed coordinates.
     * 
     * @param currentNode The current waypoint
     * @param nextNode The next waypoint (can be null if at end of path)
     * @param coords Array to store [x, y] coordinates (modified in place)
     */
    public static void calculateTargetPosition(PathNode currentNode, PathNode nextNode, double[] coords) {
        // Convert current node to screen coordinates
        Point currentScreenPos = TileCoordinateConverter.mapToScreen(currentNode.getX(), currentNode.getY());
        double targetX = currentScreenPos.x;
        double targetY = currentScreenPos.y;
        
        // If we have a next waypoint, apply smoothing
        if (nextNode != null) {
            Point nextScreenPos = TileCoordinateConverter.mapToScreen(nextNode.getX(), nextNode.getY());
            targetX = currentScreenPos.x * (1 - SMOOTHING_FACTOR) + nextScreenPos.x * SMOOTHING_FACTOR;
            targetY = currentScreenPos.y * (1 - SMOOTHING_FACTOR) + nextScreenPos.y * SMOOTHING_FACTOR;
        }
        
        coords[0] = targetX;
        coords[1] = targetY;
    }
}
