import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

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

	public static ArrayList<MapNode> generatePath(int[][] map, int startX, int startY, int finalX, int finalY) {
		ArrayList<MapNode> adjacentList = new ArrayList<MapNode>();
		ArrayList<MapNode> openList = new ArrayList<MapNode>();
		ArrayList<MapNode> closedList = new ArrayList<MapNode>();
		ArrayList<MapNode> finalList = new ArrayList<MapNode>();

		int curX, curY;
		int minF, numElement;

		// add start node to closed list
		MapNode startNode = new MapNode(startX, startY, 0, 0, null);
		closedList.add(startNode);

		// add all squares near start square to adjacent list
		curX = startX;
		curY = startY;

		for (int i = curX - 1; i <= curX + 1; i++) {
			for (int j = curY - 1; j <= curY + 1; j++) {
				// avoid the center which is square itself
				if (i == curX && j == curY)
					continue;

				// skip all locations not on the map
				if (!isValidLocation(map, i, j))
					continue;

				// skip unwalkable tiles
				if (getMapTile(map, i, j) == TILE_WALL)
					continue;

				int tempGCost = MapNode.findG(i, j, curX, curY);
				int tempHCost = MapNode.findH(i, j, finalX, finalY);
				openList.add(new MapNode(i, j, tempGCost, tempHCost, closedList.get(0)));
			}
		}

		// search for lowest f-cost near starting node
		if (openList.size() <= 0) {
			return null;
		}

		minF = openList.get(0).getF();
		numElement = 0;
		for (int i = 1; i < openList.size(); i++) {
			if (openList.get(i).getF() <= minF) {
				minF = openList.get(i).getF();
				numElement = i;
			}
		}

		while (!(openList.get(numElement).getX() == finalX && openList.get(numElement).getY() == finalY)) {
			// switch element from open list to closed list
			// first add to closed, then remove from open
			closedList.add(openList.get(numElement));
			openList.remove(numElement);

			curX = closedList.get(closedList.size() - 1).getX();
			curY = closedList.get(closedList.size() - 1).getY();

			adjacentList.clear();
			for (int i = curX - 1; i <= curX + 1; i++) {
				for (int j = curY - 1; j <= curY + 1; j++) {
					if (isValidLocation(map, i, j) && (i != curX || j != curY)) {
						int adjacentG = MapNode.findG(i, j, curX, curY) + closedList.get(closedList.size() - 1).getG();
						int adjacentH = MapNode.findH(i, j, finalX, finalY);
						adjacentList
								.add(new MapNode(i, j, adjacentG, adjacentH, closedList.get(closedList.size() - 1)));
					}
				}
			}

			// remove duplicates added to closed list
			for (int i = 0; i < adjacentList.size(); i++) {
				for (int j = 0; j < closedList.size(); j++) {
					if (adjacentList.get(i).getX() == closedList.get(j).getX()
							&& adjacentList.get(i).getY() == closedList.get(j).getY()) {
						adjacentList.remove(i);
						i--;
						break;
					}
				}
			}

			// remove unwalkables
			for (int i = 0; i < adjacentList.size(); i++) {
				if (getMapTile(map, adjacentList.get(i).getX(), adjacentList.get(i).getY()) == TILE_WALL) {
					adjacentList.remove(i);
					i--;
				}
			}

			// remove duplicates added to open list
			for (int i = 0; i < adjacentList.size(); i++) {
				for (int j = 0; j < openList.size(); j++) {
					if (adjacentList.get(i).getX() == openList.get(j).getX() &&
							adjacentList.get(i).getY() == openList.get(j).getY()) {
						int newG = MapNode.findG(curX, curY, adjacentList.get(i).getX(), adjacentList.get(i).getY()) +
								MapNode.findG(curX, curY, closedList.get(closedList.size() - 2).getX(),
										closedList.get(closedList.size() - 2).getY());
						int oldG = MapNode.findG(adjacentList.get(i).getX(), adjacentList.get(i).getY(),
								closedList.get(closedList.size() - 2).getX(),
								closedList.get(closedList.size() - 2).getY());

						// overwrite G cost and F cost since better path to that square has been found
						if (newG < oldG) {
							openList.get(j).setG(newG);
							openList.get(j).setF(newG + openList.get(j).getH()); // f = g + h
							openList.get(j).setParent(adjacentList.get(i));
						}
						adjacentList.remove(i);
						i--;
						break;
					}
				}
			}

			// add new elements to adjacent list
			for (int i = 0; i < adjacentList.size(); i++) {
				openList.add(adjacentList.get(i));
			}

			if (openList.size() == 0)
				break;

			minF = openList.get(0).getF();
			numElement = 0;

			for (int i = 1; i < openList.size(); i++) {
				if (openList.get(i).getF() <= minF) {
					// don't add nonadjacent elements
					if (Math.abs(openList.get(i).getX() - curX) <= 1 && Math.abs(openList.get(i).getY() - curY) <= 1) {
						minF = openList.get(i).getF();
						numElement = i;
					}
				}
			}
		}

		// add final node to closed list
		if (openList.size() != 0) {
			closedList.add(openList.get(numElement));

			// trace path back from finish to start
			MapNode tempNode = closedList.get(closedList.size() - 1);
			do {
				finalList.add(tempNode);
				tempNode = tempNode.getParent();

			} while (tempNode != null);

			// list is stored backwards so reverse it
			Collections.reverse(finalList);
		} else {
			finalList = null;
		}

		return finalList;
	}

	public static void main(String[] args) {
		int[][] map = {
				{ 0, 0, 0, 0, 0, 0, 0 },
				{ 0, 1, 1, 1, 1, 1, 0 },
				{ 0, 1, 0, 0, 0, 1, 0 },
				{ 0, 1, 0, 0, 0, 1, 0 },
				{ 0, 1, 1, 1, 1, 1, 0 },
				{ 0, 2, 1, 3, 0, 0, 0 }
		};

		Point startPoint = getStartPoint(map);
		Point endPoint = getEndPoint(map);

		ArrayList<MapNode> path = generatePath(map, startPoint.x, startPoint.y, endPoint.x, endPoint.y);
		// System.out.println("Path contents (from goal to start): ");
		// for (int i = 0; i < path.size(); i++)
		// System.out.println(path.get(i));
	}
}
