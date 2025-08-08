package pathfinding;

import graphics.Point;

/**
 * Encapsulates pathfinding state and management for game units.
 * This class handles cooldowns, failure tracking, and retry logic for pathfinding operations.
 */
public class PathfindingState {
    
    // Pathfinding state
    private int currentMapEndX = 0;
    private int currentMapEndY = 0;
    private int pathfindingCooldown = 0;
    private boolean pathfindingFailed = false;
    private int pathfindingFailureTimer = 0;
    private int pathfindingFailureCount = 0;
    
    // Constants
    private static final int PATHFINDING_COOLDOWN_FRAMES = 10; // Only recalculate every 10 frames
    private static final int PATHFINDING_FAILURE_DISPLAY_FRAMES = 60; // Show failure indicator for 1 second (60 frames)
    private static final int MAX_PATHFINDING_RETRIES = 5; // Maximum consecutive failures before giving up
    
    /**
     * Checks if pathfinding should be skipped due to cooldown
     */
    public boolean isOnCooldown() {
        return pathfindingCooldown > 0;
    }
    
    /**
     * Decrements the cooldown timer
     */
    public void decrementCooldown() {
        if (pathfindingCooldown > 0) {
            pathfindingCooldown--;
        }
    }
    
    /**
     * Sets the cooldown timer to the maximum value
     */
    public void setCooldown() {
        pathfindingCooldown = PATHFINDING_COOLDOWN_FRAMES;
    }
    
    /**
     * Resets the cooldown timer to allow immediate pathfinding
     */
    public void resetCooldown() {
        pathfindingCooldown = 0;
    }
    
    /**
     * Checks if pathfinding has failed too many times consecutively
     */
    public boolean hasExceededMaxRetries() {
        return pathfindingFailureCount >= MAX_PATHFINDING_RETRIES;
    }
    
    /**
     * Records a pathfinding failure
     */
    public void recordFailure() {
        pathfindingFailed = true;
        pathfindingFailureTimer = PATHFINDING_FAILURE_DISPLAY_FRAMES;
        pathfindingFailureCount++;
    }
    
    /**
     * Records a pathfinding success
     */
    public void recordSuccess() {
        pathfindingFailed = false;
        pathfindingFailureCount = 0;
    }
    
    /**
     * Updates the failure timer
     */
    public void updateFailureTimer() {
        if (pathfindingFailureTimer > 0) {
            pathfindingFailureTimer--;
            if (pathfindingFailureTimer == 0) {
                pathfindingFailed = false; // Clear failure state when timer expires
            }
        }
    }
    
    /**
     * Checks if pathfinding recently failed and is still showing the failure indicator
     */
    public boolean isPathfindingFailed() {
        return pathfindingFailed && pathfindingFailureTimer > 0;
    }
    
    /**
     * Gets the remaining failure display time
     */
    public int getFailureTimer() {
        return pathfindingFailureTimer;
    }
    
    /**
     * Gets the current failure count
     */
    public int getFailureCount() {
        return pathfindingFailureCount;
    }
    
    /**
     * Checks if destination coordinates have changed
     */
    public boolean destinationChanged(Point mapEnd) {
        return currentMapEndX != mapEnd.x || currentMapEndY != mapEnd.y;
    }
    
    /**
     * Updates the current destination coordinates
     */
    public void updateDestination(Point mapEnd) {
        currentMapEndX = mapEnd.x;
        currentMapEndY = mapEnd.y;
    }
    
    /**
     * Gets the current map end X coordinate
     */
    public int getCurrentMapEndX() {
        return currentMapEndX;
    }
    
    /**
     * Gets the current map end Y coordinate
     */
    public int getCurrentMapEndY() {
        return currentMapEndY;
    }
    
    /**
     * Sets the current map end coordinates
     */
    public void setCurrentMapEnd(int x, int y) {
        this.currentMapEndX = x;
        this.currentMapEndY = y;
    }
    
    /**
     * Sets the current map end coordinates from a Point
     */
    public void setCurrentMapEnd(Point p) {
        this.currentMapEndX = p.x;
        this.currentMapEndY = p.y;
    }
    
    /**
     * Gets the maximum number of pathfinding retries
     */
    public static int getMaxPathfindingRetries() {
        return MAX_PATHFINDING_RETRIES;
    }
    
    /**
     * Gets the pathfinding failure display frames
     */
    public static int getPathfindingFailureDisplayFrames() {
        return PATHFINDING_FAILURE_DISPLAY_FRAMES;
    }
}
