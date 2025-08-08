package pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Comparator;
import map.MapValidator;


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

	// Comparator for PriorityQueue to sort by F-cost
	private static class MapNodeComparator implements Comparator<PathNode> {
		@Override
		public int compare(PathNode a, PathNode b) {
			return Integer.compare(a.getF(), b.getF());
		}
	}

	// Result class to hold both path and explored nodes
	public static class PathfindingResult {
		public final ArrayList<PathNode> path;
		public final ArrayList<PathNode> exploredNodes;

		public PathfindingResult(ArrayList<PathNode> path, ArrayList<PathNode> exploredNodes) {
			this.path = path;
			this.exploredNodes = exploredNodes;
		}
	}



	public static ArrayList<PathNode> generatePath(int[][] map, int startX, int startY, int finalX, int finalY) {
		PathfindingResult result = generatePathWithExploredNodes(map, startX, startY, finalX, finalY);
		return result != null ? result.path : null;
	}

	public static PathfindingResult generatePathWithExploredNodes(int[][] map, int startX, int startY, int finalX,
			int finalY) {
		// Use PriorityQueue for efficient min F-cost retrieval
		PriorityQueue<PathNode> openList = new PriorityQueue<>(new MapNodeComparator());
		// Use HashSet for O(1) lookup in closed list
		HashSet<String> closedSet = new HashSet<>();
		ArrayList<PathNode> finalList = new ArrayList<PathNode>();
		ArrayList<PathNode> exploredNodes = new ArrayList<PathNode>();

		int iterationCount = 0;

		// Early termination if start or end coordinates are invalid
		if (!MapValidator.isValidLocation(map, startX, startY) || !MapValidator.isValidLocation(map, finalX, finalY)) {
			return null;
		}

		// Early termination if start or destination is a wall
		if (MapValidator.isWall(map, startX, startY) || MapValidator.isWall(map, finalX, finalY)) {
			return null;
		}

		// add start node to open list
		PathNode startNode = new PathNode(startX, startY, 0, PathNode.findH(startX, startY, finalX, finalY), null);
		openList.add(startNode);

		// Direction arrays for 8-directional movement
		int[][] directions = {
				{ -1, -1 }, { -1, 0 }, { -1, 1 },
				{ 0, -1 }, { 0, 1 },
				{ 1, -1 }, { 1, 0 }, { 1, 1 }
		};

		while (!openList.isEmpty() && iterationCount < MAX_ITERATIONS && openList.size() < MAX_OPEN_NODES) {
			iterationCount++;
			PathNode currentNode = openList.poll();

			// Add to explored nodes for visualization
			exploredNodes.add(currentNode);

			// Check if we reached the destination
			if (currentNode.getX() == finalX && currentNode.getY() == finalY) {
				// Reconstruct path
				PathNode tempNode = currentNode;
				do {
					finalList.add(tempNode);
					tempNode = tempNode.getParent();
				} while (tempNode != null);

				Collections.reverse(finalList);
				return new PathfindingResult(finalList, exploredNodes);
			}

			// Add to closed set
			String nodeKey = currentNode.getX() + "," + currentNode.getY();
			if (closedSet.contains(nodeKey)) {
				continue;
			}
			closedSet.add(nodeKey);

			// Check all 8 directions
			for (int[] dir : directions) {
				int newX = currentNode.getX() + dir[0];
				int newY = currentNode.getY() + dir[1];

				// Skip if out of bounds or wall
				if (!MapValidator.isWalkable(map, newX, newY)) {
					continue;
				}

				// Skip if already in closed set
				String newNodeKey = newX + "," + newY;
				if (closedSet.contains(newNodeKey)) {
					continue;
				}

				// Calculate costs
				int newG = currentNode.getG() + PathNode.findG(currentNode.getX(), currentNode.getY(), newX, newY);
				int newH = PathNode.findH(newX, newY, finalX, finalY);
				int newF = newG + newH;

				// Create new node
				PathNode newNode = new PathNode(newX, newY, newG, newH, currentNode);
				openList.add(newNode);
			}
		}

		// No path found (either due to no path or performance limits)
		return new PathfindingResult(null, exploredNodes);
	}
}
