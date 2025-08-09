package pathfinding;
import graphics.Point;
import utils.TileCoordinateConverter;
import map.MapValidator;

import java.util.ArrayList;

public class MovementController {
	// Movement physics component
	private MovementPhysics physics;

	// Cached target location to avoid object creation
	private double targetX, targetY;

	// Path finding
	private ArrayList<PathNode> movePath = null;
	private ArrayList<PathNode> exploredNodes = null;
	
	// Path smoothing strategy
	private PathSmoother.SmoothingStrategy smoothingStrategy = PathSmoother.getDefaultStrategy();
	
	// Current map reference for obstacle detection
	private int[][] currentMap = null;
	private int nodeCounter;
	private boolean isPathCreated = false;

	// Path cache instance
	private PathCache pathCache;

	// physical state
	private boolean isMoving = false;

	// Pathfinding state management
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

	public boolean getIsPathCreated() {
		return isPathCreated;
	}

	public void setIsPathCreated(boolean flag) {
		this.isPathCreated = flag;
	}

	public boolean getIsMoving() {
		return isMoving;
	}

	public ArrayList<PathNode> getPath() {
		return movePath;
	}

	public ArrayList<PathNode> getExploredNodes() {
		return exploredNodes;
	}

	/**
	 * Gets the current position from the physics component.
	 * @return Current position
	 */
	public Point getCurrentPosition() {
		return physics.getCurrentPosition();
	}

	/**
	 * Gets the current X position from the physics component.
	 * @return Current X position
	 */
	public double getCurrentX() {
		return physics.getCurrentX();
	}

	/**
	 * Gets the current Y position from the physics component.
	 * @return Current Y position
	 */
	public double getCurrentY() {
		return physics.getCurrentY();
	}

	/**
	 * Sets the physics properties.
	 * @param maxVelocity Maximum velocity
	 * @param maxForce Maximum steering force
	 * @param mass Mass of the object
	 */
	public void setPhysicsProperties(double maxVelocity, double maxForce, double mass) {
		physics.setPhysicsProperties(maxVelocity, maxForce, mass);
	}

	public void setPath(ArrayList<PathNode> path) {
		this.movePath = new ArrayList<>(path);
		this.nodeCounter = 1;
		this.isPathCreated = true;
		this.isMoving = true;
	}

	public void setExploredNodes(ArrayList<PathNode> exploredNodes) {
		this.exploredNodes = exploredNodes != null ? new ArrayList<>(exploredNodes) : null;
	}

	public void startMoving() {
		this.isMoving = true;
	}

	public void stopMoving() {
		this.isMoving = false;
	}

	public MovementController(int playerX, int playerY) {
		this(playerX, playerY, new PathCache());
	}
	
	public MovementController(int playerX, int playerY, PathCache pathCache) {
		physics = new MovementPhysics(playerX, playerY);
		this.pathCache = pathCache;
		nodeCounter = 1;
	}

	/**
	 * Main pathfinding coordination method.
	 * Simplified version that focuses on core functionality.
	 * @return Alternative destination if pathfinding failed, null otherwise
	 */
	public Point coordinatePathfinding(int[][] map, Point currentPosition, Point destination, int unitClassType) {
		Point mapStart = TileCoordinateConverter.screenToMap(currentPosition);
		Point mapEnd = TileCoordinateConverter.screenToMap(destination);
		
		if (!isMoving) return null;
		
		// Update failure timer for visual feedback
		updateFailureTimer();
		
		// Simple pathfinding: if we don't have a path or destination changed, find a new path
		if (!isPathCreated || destinationChanged(mapEnd)) {
			if (findPath(map, mapStart, mapEnd)) {
				// Path found successfully
				updateDestination(mapEnd);
				recordSuccess(); // Clear any previous failure state
			} else {
				// Pathfinding failed, try to find alternative
				Point alternativeDest = PathfindingUtils.findAlternativeDestination(map, mapEnd);
				if (alternativeDest != null) {
					setIsPathCreated(false);
					return TileCoordinateConverter.mapToScreen(alternativeDest);
				} else {
					// No alternative found, record failure for visual feedback
					recordFailure();
				}
				return null;
			}
		}
		
		// If we have a path, move along it
		if (isPathFound()) {
			run();
		}
		
		return null;
	}

	public boolean findPath(int map[][], Point start, Point end) {
		if (isPathCreated == true)
			return false;

		// Store map reference for obstacle detection
		this.currentMap = map;

		// Create new path using A* algorithm
		PathAStar.PathfindingResult result = PathAStar.generatePathWithExploredNodes(map, start.x, start.y, end.x, end.y);
		
		if (result != null && result.path != null && result.path.size() > 0) {
			setPath(result.path);
			setExploredNodes(result.exploredNodes);
			return true;
		}
		
		return false;
	}

	public void clearPathCache() {
		pathCache.clear();
	}
	
	/**
	 * Sets the path smoothing strategy
	 * @param strategy The smoothing strategy to use
	 */
	public void setSmoothingStrategy(PathSmoother.SmoothingStrategy strategy) {
		this.smoothingStrategy = strategy;
	}
	
	/**
	 * Gets the current path smoothing strategy
	 * @return The current smoothing strategy
	 */
	public PathSmoother.SmoothingStrategy getSmoothingStrategy() {
		return smoothingStrategy;
	}
	
	/**
	 * Updates the current map reference for obstacle detection
	 * @param map The current game map
	 */
	public void updateMap(int[][] map) {
		this.currentMap = map;
	}

	public Point recalculateDest(int map[][], Point playerMapDest) {
		// If we have a valid path, try to find a new destination near the original
		if (movePath != null && movePath.size() > 0) {
			// Start from the current waypoint and look for alternative destinations
			for (int i = nodeCounter; i < movePath.size(); i++) {
				PathNode waypoint = movePath.get(i);
				
				// Check if this waypoint is walkable
				if (MapValidator.isWalkable(map, waypoint.getX(), waypoint.getY())) {
					// Found a valid waypoint, return it as screen coordinates
					return TileCoordinateConverter.mapToScreen(waypoint.getX(), waypoint.getY());
				}
			}
		}
		
		// If no valid waypoint found in current path, try to find a new path to a nearby location
		Point currentPos = physics.getCurrentPosition();
		Point mapPos = TileCoordinateConverter.screenToMap(currentPos);
		
		// Search in expanding circles around the original destination
		for (int radius = 1; radius <= 3; radius++) {
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dx = -radius; dx <= radius; dx++) {
					// Skip corners for efficiency
					if (Math.abs(dx) == radius && Math.abs(dy) == radius) {
						continue;
					}
					
					int newX = playerMapDest.x + dx;
					int newY = playerMapDest.y + dy;
					
					// Check if tile is walkable
					if (MapValidator.isWalkable(map, newX, newY)) {
						// Try to find a path to this location
						PathAStar.PathfindingResult result = PathAStar.generatePathWithExploredNodes(map, mapPos.x, mapPos.y, newX, newY);
						
						if (result != null && result.path != null && result.path.size() > 0) {
							// Found a valid path, update our path and return the new destination
							setPath(result.path);
							setExploredNodes(result.exploredNodes);
							return TileCoordinateConverter.mapToScreen(newX, newY);
						}
					}
				}
			}
		}
		
		// If no alternative found, return null
		return null;
	}

	public boolean isPathFound() {
		return movePath != null;
	}

	public Point run() {
		// Empty path || reached destination
		if (movePath == null || movePath.size() == 0 || nodeCounter >= movePath.size()) {
			stopMoving();
			isPathCreated = false;
			return physics.getCurrentPosition();
		}

		// Get location of next waypoint with optimized path smoothing
		PathNode currentNode = movePath.get(nodeCounter);
		PathNode nextNode = (nodeCounter + 1 < movePath.size()) ? movePath.get(nodeCounter + 1) : null;
		
		// Use PathSmoother to calculate target position
		PathSmoother.SmoothingResult result = PathSmoother.calculateTargetPosition(
			currentNode, nextNode, smoothingStrategy, currentMap);
		
		if (result.isValid) {
			targetX = result.targetX;
			targetY = result.targetY;
			physics.updatePosition(targetX, targetY);
		}

		// Increased waypoint threshold for smoother movement
		if (MovementPhysics.getDistance(physics.getCurrentX(), physics.getCurrentY(), targetX, targetY) < 15) {
			nodeCounter++;
		}

		return physics.getCurrentPosition();
	}

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
