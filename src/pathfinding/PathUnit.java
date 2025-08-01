package pathfinding;
import graphics.Point;
import utils.TileCoordinateConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PathUnit {
	// steering vehicle data
	private PathVector2D currentVelocity, currentLocation;
	private double maxVelocity, maxForce;
	private double mass;

	private PathVector2D nextLocation;

	// Path finding
	private ArrayList<PathNode> movePath = null;
	private int nodeCounter;
	private boolean isPathCreated = false;

	// Path caching for performance
	private static Map<String, ArrayList<PathNode>> pathCache = new HashMap<>();
	private static final int MAX_CACHE_SIZE = 1000;

	// physical state
	private boolean isMoving = false;

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

	public void setPath(ArrayList<PathNode> path) {
		this.movePath = new ArrayList<>(path);
		this.nodeCounter = 1;
		this.isPathCreated = true;
		this.isMoving = true;
	}

	public void startMoving() {
		this.isMoving = true;
	}

	public void stopMoving() {
		this.isMoving = false;
	}

	public PathUnit(int playerX, int playerY) {
		currentLocation = new PathVector2D(playerX, playerY);
		currentVelocity = new PathVector2D(1, 1);

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
			movePath = new ArrayList<>(cachedPath); // Create a copy
			nodeCounter = 1;
			isPathCreated = true;
			return true;
		}

		// Generate new path
		movePath = PathAStar.generatePath(map, start.x, start.y, end.x, end.y);
		if (movePath != null) {
			// Cache the path
			cachePath(cacheKey, movePath);
			nodeCounter = 1;
			isPathCreated = true;
			return true;
		} else
			return false;
	}

	private void cachePath(String key, ArrayList<PathNode> path) {
		// Limit cache size to prevent memory issues
		if (pathCache.size() >= MAX_CACHE_SIZE) {
			// Remove oldest entries (simple approach - clear half the cache)
			pathCache.clear();
		}
		pathCache.put(key, new ArrayList<>(path));
	}

	// Clear cache when map changes significantly
	public static void clearPathCache() {
		pathCache.clear();
	}

	public Point recalculateDest(int map[][], Point playerMapDest) {
		Point newDest = new Point();

		// Check if map is null or empty
		if (map == null || map.length == 0 || map[0].length == 0) {
			return newDest;
		}

		int mapHeight = map.length;
		int mapWidth = map[0].length;

		// Define 8 directions: N, S, E, W, NW, NE, SW, SE
		int[][] directions = new int[][] {
				{ 0, -1 },  // North
				{ 0, 1 },   // South
				{ 1, 0 },   // East
				{ -1, 0 },  // West
				{ -1, -1 }, // Northwest
				{ 1, -1 },  // Northeast
				{ -1, 1 },  // Southwest
				{ 1, 1 }    // Southeast
		};

		// Check each direction for a walkable tile
		for (int i = 0; i < directions.length; i++) {
			int dx = directions[i][0];
			int dy = directions[i][1];
			
			int newX = playerMapDest.x + dx;
			int newY = playerMapDest.y + dy;

			// Check bounds
			if (newY < 0 || newY >= mapHeight || newX < 0 || newX >= mapWidth) {
				continue;
			}

			// Check if tile is walkable
			if (map[newY][newX] == 0) {
				newDest = TileCoordinateConverter.mapToScreen(newX, newY);
				break;
			}
		}

		/*
		 * boolean isTileFound = false;
		 * for (int dy = -1; dy <= 1; dy++)
		 * {
		 * //System.out.println("player map dest: " + playerMapDest.x + ", " +
		 * playerMapDest.y);
		 * if (playerMapDest.y + dy < 0) continue;
		 * 
		 * for (int dx = -1; dx <= 1; dx++)
		 * {
		 * if (dy == 0 && dx == 0)
		 * continue;
		 * 
		 * if (playerMapDest.x + dx < 0) continue;
		 * 
		 * if (map[playerMapDest.y + dy][playerMapDest.x + dx] == 0)
		 * {
		 * newDest.x = (playerMapDest.x + dx) * Constants.TILE_WIDTH;
		 * newDest.y = (playerMapDest.y + dy) * Constants.TILE_HEIGHT;
		 * isTileFound = true;
		 * }
		 * 
		 * if (isTileFound) break;
		 * }
		 * if (isTileFound) break;
		 * }
		 */

		// System.out.println("new dest: " + newDest.x + ", " + newDest.y);

		return newDest;
	}

	public boolean isPathFound() {
		return movePath != null;
	}

	public Point run() {
		// Empty path || reached destination
		if (movePath.size() == 0 || nodeCounter >= movePath.size()) {
			stopMoving();
			isPathCreated = false;

			return new Point((int) currentLocation.getX(), (int) currentLocation.getY());
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
