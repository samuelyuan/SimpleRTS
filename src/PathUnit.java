import graphics.Point;
import java.util.ArrayList;
import java.util.Collections;

public class PathUnit {
	// steering vehicle data
	private PathVector2D currentVelocity, currentLocation;
	private double maxVelocity, maxForce;
	private double mass;

	private PathVector2D nextLocation;

	// Path finding
	private ArrayList<MapNode> movePath = null;
	private int nodeCounter;
	private boolean isPathCreated = false;

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

	public ArrayList<MapNode> getPath() {
		return movePath;
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

		maxVelocity = 0.2;
		maxForce = 0.5;
		mass = 1;

		nodeCounter = 1;
	}

	public boolean findPath(int map[][], Point start, Point end) {
		if (isPathCreated == true)
			return false;

		movePath = PathAStar.generatePath(map, start.x, start.y, end.x, end.y);
		if (movePath != null) {
			nodeCounter = 1;
			isPathCreated = true;
			return true;
		} else
			return false;
	}

	public Point recalculateDest(int map[][], Point playerMapDest) {
		Point newDest = new Point();

		int[][] directions = new int[][] {
				{ 0, 1 },
				{ -1, 0 },
				{ 1, 0 },
				{ 0, 1 },
				{ -1, -1 },
				{ -1, 1 },
				{ 1, -1 },
				{ 1, 1 }
		};

		// Set one of the eight neighboring tiles surrounding the destination as another
		// destination point

		// fix it so that N,S,E,W is considered first, before NW,NE,SW,SE
		for (int i = 0; i < directions.length; i++) {
			int dx = directions[i][0];
			int dy = directions[i][1];

			if (playerMapDest.y + dy < 0 || playerMapDest.x + dx < 0)
				continue;

			if (map[playerMapDest.y + dy][playerMapDest.x + dx] == 0) {
				newDest = new Point(
					(playerMapDest.x + dx) * GameMap.TILE_WIDTH,
					(playerMapDest.y + dy) * GameMap.TILE_HEIGHT
				);
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
		 * newDest.x = (playerMapDest.x + dx) * GameMap.TILE_WIDTH;
		 * newDest.y = (playerMapDest.y + dy) * GameMap.TILE_HEIGHT;
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

		// Get location of next waypoint
		// Since the path is stored backwards, get the last node first
		MapNode mapLocation = movePath.get(nodeCounter);
		int destPosX = mapLocation.getX() * GameMap.TILE_WIDTH;
		int destPosY = mapLocation.getY() * GameMap.TILE_HEIGHT;

		nextLocation = new PathVector2D(destPosX, destPosY);
		updateLocation(nextLocation);

		Point newLocation = new Point((int) currentLocation.getX(), (int) currentLocation.getY());

		// don't go to next waypoint until unit is extremely close the current waypoint
		if (PathVector2D.getDistance(currentLocation, nextLocation) < 5) {
			nodeCounter++;
		}

		return newLocation;
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
