package pathfinding;
import graphics.Point;
import utils.TileCoordinateConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashMap;

public class PathUnit {
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

		// Generate new path with explored nodes
		PathAStar.PathfindingResult result = PathAStar.generatePathWithExploredNodes(map, start.x, start.y, end.x, end.y);
		if (result != null && result.path != null) {
			// Cache the path (LRU cache handles eviction automatically)
			pathCache.put(cacheKey, new ArrayList<>(result.path));
			movePath = new ArrayList<>(result.path);
			exploredNodes = result.exploredNodes; // Store explored nodes for visualization
			nodeCounter = 1;
			isPathCreated = true;
			return true;
		} else {
			// Store explored nodes even if path failed
			if (result != null) {
				exploredNodes = result.exploredNodes;
			}
			return false;
		}
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

		// First, try the immediate 8 adjacent tiles
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
				return newDest;
			}
		}

		// If no immediate tile found, search in expanding circles and find the closest one
		int maxRadius = 8; // Increased max radius but we'll prioritize closer tiles
		int closestDistance = Integer.MAX_VALUE;
		Point closestTile = null;
		
		// Search in expanding circles, but collect all candidates and pick the closest
		for (int radius = 2; radius <= maxRadius; radius++) {
			boolean foundInThisRadius = false;
			
			// Search in a circular pattern around the destination
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dx = -radius; dx <= radius; dx++) {
					// Skip corners to make it more circular and efficient
					if (Math.abs(dx) == radius && Math.abs(dy) == radius) {
						continue;
					}
					
					int newX = playerMapDest.x + dx;
					int newY = playerMapDest.y + dy;

					// Check bounds
					if (newY < 0 || newY >= mapHeight || newX < 0 || newX >= mapWidth) {
						continue;
					}

					// Check if tile is walkable
					if (map[newY][newX] == 0) {
						int distance = Math.abs(dx) + Math.abs(dy); // Manhattan distance
						if (distance < closestDistance) {
							closestDistance = distance;
							closestTile = new Point(newX, newY);
							foundInThisRadius = true;
						}
					}
				}
			}
			
			// If we found tiles in this radius and we're within reasonable distance, use the closest one
			if (foundInThisRadius && closestDistance <= 4) {
				break; // Don't search further if we have a reasonably close tile
			}
		}
		
		// If we found a reasonably close tile, use it
		if (closestTile != null && closestDistance <= 6) {
			newDest = TileCoordinateConverter.mapToScreen(closestTile.x, closestTile.y);
			return newDest;
		}
		
		// If no reasonably close tile found, try to find the closest walkable tile anywhere
		// but limit the search to avoid extremely long distances
		if (closestTile == null || closestDistance > 6) {
			closestDistance = Integer.MAX_VALUE;
			closestTile = null;
			
			// Search in a larger area but still prioritize closer tiles
			for (int y = Math.max(0, playerMapDest.y - 10); y <= Math.min(mapHeight - 1, playerMapDest.y + 10); y++) {
				for (int x = Math.max(0, playerMapDest.x - 10); x <= Math.min(mapWidth - 1, playerMapDest.x + 10); x++) {
					if (map[y][x] == 0) {
						int distance = Math.abs(x - playerMapDest.x) + Math.abs(y - playerMapDest.y);
						if (distance < closestDistance) {
							closestDistance = distance;
							closestTile = new Point(x, y);
						}
					}
				}
			}
		}
		
		// If we found any walkable tile, use it
		if (closestTile != null) {
			newDest = TileCoordinateConverter.mapToScreen(closestTile.x, closestTile.y);
		}

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
