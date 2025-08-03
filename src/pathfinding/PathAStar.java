package pathfinding;

import graphics.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Comparator;

public class PathAStar {
	/*
	 * key:
	 * 0 - empty space
	 * 1 - wall
	 * 2 - start (at map[3][1])
	 * 3 - finish (at map[3][5])
	 */

	// Simple Test Case #1
	/*
	 * public static int [][] map = {
	 * { 0, 0, 0, 0, 0, 0, 0 } ,
	 * { 0, 0, 0, 0, 0, 0, 0 } ,
	 * { 0, 0, 0, 1, 0, 0, 0 } ,
	 * { 0, 2, 0, 1, 0, 3, 0 } ,
	 * { 0, 0, 0, 1, 0, 0, 0 } ,
	 * { 0, 0, 0, 0, 0, 0, 0 }
	 * };
	 */

	/*
	 * Test Case #2
	 * public static int [][] map = {
	 * { 0, 0, 0, 0, 0, 0, 0 } ,
	 * { 0, 1, 1, 1, 1, 1, 0 } ,
	 * { 0, 1, 0, 0, 0, 1, 0 } ,
	 * { 0, 1, 0, 0, 0, 1, 0 } ,
	 * { 0, 1, 1, 1, 1, 1, 0 } ,
	 * { 0, 2, 1, 3, 0, 0, 0 }
	 */

	public static final int TILE_WALL = 1;
	public static final int TILE_START = 2;
	public static final int TILE_END = 3;

	// Comparator for PriorityQueue to sort by F-cost
	private static class MapNodeComparator implements Comparator<PathNode> {
		@Override
		public int compare(PathNode a, PathNode b) {
			return Integer.compare(a.getF(), b.getF());
		}
	}

	public static int getMapTile(int[][] map, int x, int y) {
		return map[y][x];
	}

	public static boolean isValidLocation(int[][] map, int x, int y) {
		// map height != map width
		if (x >= 0 && y >= 0 && x < map[0].length && y < map.length)
			return true;
		else
			return false;
	}

	public static Point getStartPoint(int[][] map) {
		// Search through all the map tiles until the start point is found.
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				if (getMapTile(map, x, y) == TILE_START)
					return new Point(x, y);
			}
		}

		// Point can't be found
		return null;
	}

	public static Point getEndPoint(int[][] map) {
		// Search through all the map tiles until the end point is found.
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				if (getMapTile(map, x, y) == TILE_END)
					return new Point(x, y);
			}
		}

		// Point can't be found
		return null;
	}

	public static ArrayList<PathNode> generatePath(int[][] map, int startX, int startY, int finalX, int finalY) {
		// Use PriorityQueue for efficient min F-cost retrieval
		PriorityQueue<PathNode> openList = new PriorityQueue<>(new MapNodeComparator());
		// Use HashSet for O(1) lookup in closed list
		HashSet<String> closedSet = new HashSet<>();
		ArrayList<PathNode> finalList = new ArrayList<PathNode>();

		// Early termination if start or end is invalid
		if (!isValidLocation(map, startX, startY) || !isValidLocation(map, finalX, finalY)) {
			return null;
		}

		// Early termination if start or destination is a wall
		if (getMapTile(map, startX, startY) == TILE_WALL || getMapTile(map, finalX, finalY) == TILE_WALL) {
			return null;
		}

		// add start node to open list
		PathNode startNode = new PathNode(startX, startY, 0, PathNode.findH(startX, startY, finalX, finalY), null);
		openList.add(startNode);

		// Direction arrays for 8-directional movement
		int[][] directions = {
			{-1, -1}, {-1, 0}, {-1, 1},
			{0, -1},           {0, 1},
			{1, -1},  {1, 0},  {1, 1}
		};

		while (!openList.isEmpty()) {
			PathNode currentNode = openList.poll();
			
			// Check if we reached the destination
			if (currentNode.getX() == finalX && currentNode.getY() == finalY) {
				// Reconstruct path
				PathNode tempNode = currentNode;
				do {
					finalList.add(tempNode);
					tempNode = tempNode.getParent();
				} while (tempNode != null);
				
				Collections.reverse(finalList);
				return finalList;
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

				// Skip if out of bounds
				if (!isValidLocation(map, newX, newY)) {
					continue;
				}

				// Skip if wall
				if (getMapTile(map, newX, newY) == TILE_WALL) {
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

		// No path found
		return null;
	}
}
