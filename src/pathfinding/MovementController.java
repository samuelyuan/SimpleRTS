package pathfinding;
import graphics.Point;
import utils.TileCoordinateConverter;
import map.MapValidator;

import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class MovementController {
	// steering vehicle data
	private PathVector2D currentVelocity, currentLocation;
	private double maxVelocity, maxForce;
	private double mass;

	private PathVector2D nextLocation;

	// Path finding
	private ArrayList<PathNode> movePath = null;
	private ArrayList<PathNode> exploredNodes = null; // Store explored nodes for visualization
	private int nodeCounter;
	private boolean isPathCreated = false;

	// Path caching for performance
	private static final int MAX_CACHE_SIZE = 100; // Reduced from 1000
	private static LinkedHashMap<String, ArrayList<PathNode>> pathCache = 
		new LinkedHashMap<String, ArrayList<PathNode>>(MAX_CACHE_SIZE, 0.75f, true) {
			@Override
			protected boolean removeEldestEntry(Map.Entry<String, ArrayList<PathNode>> eldest) {
				return size() > MAX_CACHE_SIZE;
			}
		};

	// physical state
	private boolean isMoving = false;

	// Stuck detection and recovery
	private PathVector2D lastPosition = null;
	private int stuckCounter = 0;
	private static final int STUCK_THRESHOLD = 30; // frames
	private static final double STUCK_DISTANCE_THRESHOLD = 5.0; // pixels
	
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
		currentLocation = new PathVector2D(playerX, playerY);
		currentVelocity = new PathVector2D(1, 1);
		lastPosition = new PathVector2D(playerX, playerY);

		maxVelocity = 1.5;
		maxForce = 1.0;
		mass = 1;

		nodeCounter = 1;
	}

	public boolean findPath(int map[][], Point start, Point end) {
		if (isPathCreated == true)
			return false;

		// Check cache first
		String cacheKey = start.x + "," + start.y + "->" + end.x + "," + end.y;
		ArrayList<PathNode> cachedPath = pathCache.get(cacheKey);
		
		if (cachedPath != null) {
			setPath(cachedPath);
			return true;
		}

		// Create new path using A* algorithm
		PathAStar.PathfindingResult result = PathAStar.generatePathWithExploredNodes(map, start.x, start.y, end.x, end.y);
		
		if (result != null && result.path != null && result.path.size() > 0) {
			// Cache the successful path
			pathCache.put(cacheKey, new ArrayList<>(result.path));
			setPath(result.path);
			setExploredNodes(result.exploredNodes);
			return true;
		}
		
		return false;
	}

	public static void clearPathCache() {
		pathCache.clear();
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
		Point currentPos = new Point((int) currentLocation.getX(), (int) currentLocation.getY());
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
		if (movePath.size() == 0 || nodeCounter >= movePath.size()) {
			stopMoving();
			isPathCreated = false;
			resetStuckDetection();
			return new Point((int) currentLocation.getX(), (int) currentLocation.getY());
		}

		// Check if unit is stuck
		if (isStuck()) {
			handleStuckUnit();
		}

		// Get location of next waypoint with path smoothing
		PathNode mapLocation = movePath.get(nodeCounter);
		Point targetPoint = getSmoothedTarget(mapLocation);
		
		nextLocation = new PathVector2D(targetPoint.x, targetPoint.y);
		updateLocation(nextLocation);

		Point newLocation = new Point((int) currentLocation.getX(), (int) currentLocation.getY());

		// Increased waypoint threshold for smoother movement
		// Units will move more fluidly between waypoints
		if (PathVector2D.getDistance(currentLocation, nextLocation) < 15) {  // Increased from 5
			nodeCounter++;
		}

		return newLocation;
	}
	
	// Check if unit is stuck (hasn't moved significantly)
	private boolean isStuck() {
		if (lastPosition == null) {
			lastPosition = new PathVector2D(currentLocation.getX(), currentLocation.getY());
			return false;
		}
		
		double distance = PathVector2D.getDistance(currentLocation, lastPosition);
		if (distance < STUCK_DISTANCE_THRESHOLD) {
			stuckCounter++;
		} else {
			stuckCounter = 0;
			lastPosition = new PathVector2D(currentLocation.getX(), currentLocation.getY());
		}
		
		return stuckCounter > STUCK_THRESHOLD;
	}
	
	// Handle stuck unit recovery
	private void handleStuckUnit() {
		// Reset stuck detection
		resetStuckDetection();
		
		// Force path recalculation
		isPathCreated = false;
		
		// Add some random movement to break out of stuck position
		double randomAngle = Math.random() * 2 * Math.PI;
		double randomDistance = 10 + Math.random() * 20; // 10-30 pixels
		
		PathVector2D randomOffset = new PathVector2D(
			Math.cos(randomAngle) * randomDistance,
			Math.sin(randomAngle) * randomDistance
		);
		
		currentLocation = currentLocation.add(randomOffset);
	}
	
	// Reset stuck detection
	private void resetStuckDetection() {
		stuckCounter = 0;
		if (lastPosition != null) {
			lastPosition = new PathVector2D(currentLocation.getX(), currentLocation.getY());
		}
	}

	private Point getSmoothedTarget(PathNode currentNode) {
		// Basic smoothing: interpolate between current and next waypoint
		Point destPos = TileCoordinateConverter.mapToScreen(currentNode.getX(), currentNode.getY());
		
		// If we have a next waypoint, smooth the path
		if (nodeCounter + 1 < movePath.size()) {
			PathNode nextNode = movePath.get(nodeCounter + 1);
			Point nextPos = TileCoordinateConverter.mapToScreen(nextNode.getX(), nextNode.getY());
			
			// Simple linear interpolation for smoother movement
			double smoothingFactor = 0.3; // Adjust for more/less smoothing
			int smoothedX = (int) (destPos.x * (1 - smoothingFactor) + nextPos.x * smoothingFactor);
			int smoothedY = (int) (destPos.y * (1 - smoothingFactor) + nextPos.y * smoothingFactor);
			
			return new Point(smoothedX, smoothedY);
		}
		
		return destPos;
	}

	private void updateLocation(PathVector2D targetLocation) {
		PathVector2D seekForce = seek(targetLocation);
		seekForce.scale(1 / mass); // F = ma --> a = F / m
		PathVector2D currentAcceleration = seekForce;

		currentVelocity = currentVelocity.add(currentAcceleration);
		currentVelocity.limit(maxVelocity);
		currentLocation = currentLocation.add(currentVelocity);
	}
	

	private PathVector2D seek(PathVector2D targetLocation) {
		PathVector2D desiredVelocity = targetLocation.subtract(currentLocation);
		desiredVelocity.limit(maxVelocity);

		PathVector2D steeringForce = desiredVelocity.subtract(currentVelocity);
		steeringForce.limit(maxForce);

		return steeringForce;
	}
}
