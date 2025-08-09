package pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Comparator;
import map.MapValidator;

/**
 * A* pathfinding algorithm implementation.
 * 
 * This class is responsible for finding the shortest path between two points
 * on a 2D grid using the A* algorithm with 8-directional movement.
 */
public class PathAStar {
	/*
	 * Tile values:
	 * 0 - empty space (Land)
	 * 1 - wall
	 * Note: Start and end points are now passed as explicit coordinates
	 * rather than being encoded in the map tiles
	 */

	// Performance limits to prevent infinite loops
	private static final int MAX_ITERATIONS = 5000;
	private static final int MAX_OPEN_NODES = 2000;

	// 8-directional movement directions: diagonal and cardinal
	private static final int[][] MOVEMENT_DIRECTIONS = {
			{ -1, -1 }, { -1, 0 }, { -1, 1 },
			{ 0, -1 }, { 0, 1 },
			{ 1, -1 }, { 1, 0 }, { 1, 1 }
	};

	/**
	 * Comparator for PriorityQueue to sort by F-cost (total cost)
	 */
	private static class PathNodeComparator implements Comparator<PathNode> {
		@Override
		public int compare(PathNode a, PathNode b) {
			return Integer.compare(a.getF(), b.getF());
		}
	}

	/**
	 * Result class to hold both path and explored nodes for visualization
	 */
	public static class PathfindingResult {
		public final ArrayList<PathNode> path;
		public final ArrayList<PathNode> exploredNodes;

		public PathfindingResult(ArrayList<PathNode> path, ArrayList<PathNode> exploredNodes) {
			this.path = path;
			this.exploredNodes = exploredNodes;
		}
	}

	/**
	 * Generates a path using A* algorithm.
	 * 
	 * @param map    The game map (2D array)
	 * @param startX Starting X coordinate
	 * @param startY Starting Y coordinate
	 * @param finalX Destination X coordinate
	 * @param finalY Destination Y coordinate
	 * @return List of PathNodes representing the path, or null if no path found
	 */
	public static ArrayList<PathNode> generatePath(int[][] map, int startX, int startY, int finalX, int finalY) {
		PathfindingResult result = generatePathWithExploredNodes(map, startX, startY, finalX, finalY);
		return result != null ? result.path : null;
	}

	/**
	 * Generates a path using A* algorithm and returns both path and explored nodes.
	 * 
	 * @param map    The game map (2D array)
	 * @param startX Starting X coordinate
	 * @param startY Starting Y coordinate
	 * @param finalX Destination X coordinate
	 * @param finalY Destination Y coordinate
	 * @return PathfindingResult containing path and explored nodes, or null if no
	 *         path found
	 */
	public static PathfindingResult generatePathWithExploredNodes(int[][] map, int startX, int startY, int finalX,
			int finalY) {
		// Validate inputs
		if (!isValidPathfindingRequest(map, startX, startY, finalX, finalY)) {
			return null;
		}

		// Initialize data structures
		PriorityQueue<PathNode> openList = new PriorityQueue<>(new PathNodeComparator());
		HashSet<String> closedSet = new HashSet<>();
		ArrayList<PathNode> exploredNodes = new ArrayList<>();

		// Create and add start node
		PathNode startNode = createStartNode(startX, startY, finalX, finalY);
		openList.add(startNode);

		int iterationCount = 0;

		// Main A* algorithm loop
		while (!openList.isEmpty() && iterationCount < MAX_ITERATIONS && openList.size() < MAX_OPEN_NODES) {
			iterationCount++;
			PathNode currentNode = openList.poll();

			// Add to explored nodes for visualization
			exploredNodes.add(currentNode);

			// Check if we reached the destination
			if (isDestinationReached(currentNode, finalX, finalY)) {
				ArrayList<PathNode> path = reconstructPath(currentNode);
				return new PathfindingResult(path, exploredNodes);
			}

			// Process current node
			processCurrentNode(currentNode, closedSet, openList, map, finalX, finalY);
		}

		// No path found (either due to no path or performance limits)
		return new PathfindingResult(null, exploredNodes);
	}

	/**
	 * Validates the pathfinding request parameters.
	 */
	private static boolean isValidPathfindingRequest(int[][] map, int startX, int startY, int finalX, int finalY) {
		// Check if coordinates are within bounds
		if (!MapValidator.isValidLocation(map, startX, startY) || !MapValidator.isValidLocation(map, finalX, finalY)) {
			return false;
		}

		// Check if start or destination is a wall
		if (MapValidator.isWall(map, startX, startY) || MapValidator.isWall(map, finalX, finalY)) {
			return false;
		}

		return true;
	}

	/**
	 * Creates the initial start node for the pathfinding algorithm.
	 */
	private static PathNode createStartNode(int startX, int startY, int finalX, int finalY) {
		int hCost = PathNode.findH(startX, startY, finalX, finalY);
		return new PathNode(startX, startY, 0, hCost, null);
	}

	/**
	 * Checks if the current node is the destination.
	 */
	private static boolean isDestinationReached(PathNode currentNode, int finalX, int finalY) {
		return currentNode.getX() == finalX && currentNode.getY() == finalY;
	}

	/**
	 * Reconstructs the path from the destination node back to the start.
	 */
	private static ArrayList<PathNode> reconstructPath(PathNode destinationNode) {
		ArrayList<PathNode> path = new ArrayList<>();
		PathNode currentNode = destinationNode;

		while (currentNode != null) {
			path.add(currentNode);
			currentNode = currentNode.getParent();
		}

		Collections.reverse(path);
		return path;
	}

	/**
	 * Processes the current node by adding it to closed set and exploring
	 * neighbors.
	 */
	private static void processCurrentNode(PathNode currentNode, HashSet<String> closedSet,
			PriorityQueue<PathNode> openList, int[][] map, int finalX, int finalY) {
		// Add to closed set
		String nodeKey = createNodeKey(currentNode.getX(), currentNode.getY());
		if (closedSet.contains(nodeKey)) {
			return;
		}
		closedSet.add(nodeKey);

		// Explore all 8 directions
		for (int[] direction : MOVEMENT_DIRECTIONS) {
			exploreNeighbor(currentNode, direction, closedSet, openList, map, finalX, finalY);
		}
	}

	/**
	 * Explores a neighbor node in the specified direction.
	 */
	private static void exploreNeighbor(PathNode currentNode, int[] direction, HashSet<String> closedSet,
			PriorityQueue<PathNode> openList, int[][] map, int finalX, int finalY) {
		int newX = currentNode.getX() + direction[0];
		int newY = currentNode.getY() + direction[1];

		// Skip if out of bounds or wall
		if (!MapValidator.isWalkable(map, newX, newY)) {
			return;
		}

		// Skip if already in closed set
		String newNodeKey = createNodeKey(newX, newY);
		if (closedSet.contains(newNodeKey)) {
			return;
		}

		// Create and add new node
		PathNode newNode = createNeighborNode(currentNode, newX, newY, finalX, finalY);
		openList.add(newNode);
	}

	/**
	 * Creates a neighbor node with calculated costs.
	 */
	private static PathNode createNeighborNode(PathNode parentNode, int newX, int newY, int finalX, int finalY) {
		int newG = parentNode.getG() + PathNode.findG(parentNode.getX(), parentNode.getY(), newX, newY);
		int newH = PathNode.findH(newX, newY, finalX, finalY);
		return new PathNode(newX, newY, newG, newH, parentNode);
	}

	/**
	 * Creates a unique key for a node based on its coordinates.
	 */
	private static String createNodeKey(int x, int y) {
		return x + "," + y;
	}
}
