import java.awt.Point;
import java.util.ArrayList;

public class GameUnit {
	// public int state, waypointNum, waypointX, waypointY;

	/*
	 * The units have one class type.
	 * 
	 * Type1 - Light unit. (Regular infantry)
	 * Type2 - Medium unit. (Anti-armor infantry)
	 * Type3 - Heavy unit. (Tank)
	 * 
	 * Type1 > Type2
	 * The light unit outruns the medium unit.
	 * 
	 * Type2 > Type3
	 * The medium unit can damage the heavy unit's armor.
	 * 
	 * Type3 > Type1
	 * The heavy unit's armor reduces light unit's attack and counterattacks with
	 * strong weapons/
	 */
	public static final int UNIT_ID_LIGHT = 1;
	public static final int UNIT_ID_MEDIUM = 2;
	public static final int UNIT_ID_HEAVY = 3;

	public static final int STATE_IDLE = 1;
	public static final int STATE_MOVE = 2;
	public static final int STATE_ATTACK = 3;

	public static final int DIR_NORTH = 0;
	public static final int DIR_SOUTH = 1;
	public static final int DIR_EAST = 2;
	public static final int DIR_WEST = 3;

	public Point getMapPoint(Point screenPoint) {
		return new Point(screenPoint.x / GameMap.TILE_WIDTH, screenPoint.y / GameMap.TILE_HEIGHT);
	}

	// Location on map
	private Point currentPosition;

	public Point getCurrentPoint() {
		return this.currentPosition;
	}

	public Point destination;

	// Physical state
	private boolean isAttacking = false; // if attacking, stand still. if not, then move.

	private int classType;

	public int getClassType() {
		return this.classType;
	}

	// private int speed = 0;
	private boolean isPlayerUnit;

	public boolean getIsPlayerUnit() {
		return isPlayerUnit;
	}

	private int health;

	public int getHealth() {
		return health;
	}

	public boolean isAlive() {
		return (health > 0);
	}

	public int direction = 0; // north, south, east, west

	// Mouse selection
	public boolean isPlayerSelected;
	public boolean isClickedOn = false; // if clicked on, then unit is selected

	// Misc data
	private PathUnit pathUnit;

	public boolean isPathCreated() {
		return pathUnit.getIsPathCreated();
	}

	public void startMoving() {
		pathUnit.startMoving();
	}

	public GameUnit(int positionX, int positionY, boolean isPlayerUnit, int classType) {
		this.currentPosition = new Point(positionX, positionY);
		this.destination = new Point();
		this.pathUnit = new PathUnit(positionX, positionY);

		this.isPlayerUnit = isPlayerUnit;
		this.classType = classType;

		this.health = 100;
	}

	public boolean isOnTile(int[][] map, int tileX, int tileY) {
		Point mapPos = getMapPoint(currentPosition);

		if (mapPos.x == tileX && mapPos.y == tileY && map[tileY][tileX] != 0)
			return true;

		return false;
	}

	public void spawn(int[][] map, Point mapPos, int factionId) {
		// 2,3,4 - player units
		// 5,6,7 - enemy units
		// initial value is 1, but map stores values differently
		// make sure to adjust values (+1 for ally since code is 2, +4 for enemy, since
		// code is 5)
		if (factionId == GameFlag.FACTION_PLAYER)
			map[mapPos.y][mapPos.x] = GameUnit.UNIT_ID_LIGHT + 1;
		else if (factionId == GameFlag.FACTION_ENEMY)
			map[mapPos.y][mapPos.x] = GameUnit.UNIT_ID_LIGHT + 4;
	}

	public void die(int[][] map) {
		Point curMap = getMapPoint(currentPosition);
		map[curMap.y][curMap.x] = 0;

		Point destMap = getMapPoint(destination);
		map[destMap.y][destMap.x] = 0;
	}

	/*
	 * Use AStar pathfinding algorithm to find an optimal path that takes obstacles
	 * into account
	 */
	private int currentMapEndX = 0, currentMapEndY = 0;

	public void findPath(int[][] map) {
		Point mapStart = getMapPoint(this.currentPosition);
		Point mapEnd = getMapPoint(this.destination);

		// If the units are ordered to start moving, then begin calculating paths
		if (pathUnit.getIsMoving() == true) {
			// generate path only once
			if (pathUnit.findPath(map, mapStart, mapEnd)) {
				// erase current position from map if current pos isn't wall or flag
				if (map[mapStart.y][mapStart.x] != GameMap.TILE_WALL
						&& map[mapStart.y][mapStart.x] != 8 && map[mapStart.y][mapStart.x] != 9)
					map[mapStart.y][mapStart.x] = 0;

				// add end position to the map
				if (map[mapEnd.y][mapEnd.x] != GameMap.TILE_WALL
						&& map[mapEnd.y][mapEnd.x] != 8 && map[mapEnd.y][mapEnd.x] != 9)
					map[mapEnd.y][mapEnd.x] = classType + 1;

				currentMapEndX = mapEnd.x;
				currentMapEndY = mapEnd.y;
				// System.out.println(printMap(map));
			}

			// If path is found, then begin moving
			if (pathUnit.isPathFound()) {
				// destination point changed, so recalculate path
				if (currentMapEndX != mapEnd.x || currentMapEndY != mapEnd.y) {
					pathUnit.setIsPathCreated(false);
				}

				// move from one node to the next
				moveToDestination(map);
			}
		}

		// visualize path
		// drawPathfinding();
	}

	/*
	 * public void drawPathfinding()
	 * {
	 * if (pathUnit.getIsPathCreated() == true)
	 * {
	 * //render the complete path from start to finish
	 * for (int i = pathUnit.getPath().size() - 1; i >= 0; i--)
	 * {
	 * SimpleRTS.offscr.setColor(Color.ORANGE);
	 * SimpleRTS.offscr.drawRect(pathUnit.getPath().get(i).getX() *
	 * GameMap.TILE_WIDTH - SimpleRTS.cameraX,
	 * pathUnit.getPath().get(i).getY() * GameMap.TILE_HEIGHT - SimpleRTS.cameraY,
	 * GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT);
	 * }
	 * }
	 * }
	 */

	/*
	 * Change the player position until player reaches waypoint.
	 */
	public void moveToDestination(int[][] map) {
		// If player isn't moving, then don't do anything
		if (pathUnit.getIsMoving() == false)
			return;

		// player must be moving, but not attacking
		// if (isAttacking == false)

		// Get the corresponding unit list
		ArrayList<GameUnit> unitList = (isPlayerUnit == true) ? SimpleRTS.playerList : SimpleRTS.enemyList;

		// Iterate through the whole player list, adding units to a group
		for (int i = 0; i < unitList.size(); i++) {
			GameUnit other = unitList.get(i);

			// don't compare to itself
			if (other == this)
				continue;

			// make sure destination is same
			Point otherMapDest = getMapPoint(other.destination);
			Point playerMapDest = getMapPoint(this.destination);

			if (otherMapDest.x == playerMapDest.x && otherMapDest.y == playerMapDest.y) {
				if (other.pathUnit.getIsPathCreated() == false)
					continue;

				other.destination = other.pathUnit.recalculateDest(map, playerMapDest);

				Point currentEnd = getMapPoint(other.destination);
				currentMapEndX = currentEnd.x;
				currentMapEndY = currentEnd.y;

				// Point otherMapCurrent = getMapPoint(other.current);
				// otherMapDest = getMapPoint(other.destination);

				// other.pathUnit.findPath(map, otherMapCurrent, otherMapDest);
				// map[otherMapCurrent.y][otherMapCurrent.x] = 0;
				// map[otherMapDest.y][otherMapDest.x] = other.classType + 1;
			}

			unitList.set(i, other); // modify unit's destination if needed
		}

		currentPosition = pathUnit.run();
	}

	/*
	 * When the ally unit is close enough to see the enemy, the ally and enemy will
	 * fight each other
	 */
	public void interactWithEnemy(int[][] map, ArrayList<GameUnit> enemyList) {
		final int ATTACK_RADIUS = 8;

		for (int i = 0; i < enemyList.size(); i++) {
			GameUnit enemy = enemyList.get(i);
			int manhattanDist = Math.abs(currentPosition.x - enemy.currentPosition.x) / GameMap.TILE_WIDTH +
					Math.abs(currentPosition.y - enemy.currentPosition.y) / GameMap.TILE_HEIGHT;

			if (manhattanDist <= ATTACK_RADIUS && this.checkVisible(map, enemy)) {
				isAttacking = true;

				// remove enemy damage from ally and vice versa
				this.health -= enemy.dealDamagePoints(this);
				enemy.health -= this.dealDamagePoints(enemy);

				// draw red box around enemy units under fire
				/*
				 * SimpleRTS.offscr.setColor(Color.RED);
				 * SimpleRTS.offscr.drawRect(enemy.getCurrentPoint().x - SimpleRTS.cameraX,
				 * enemy.getCurrentPoint().y - SimpleRTS.cameraY,
				 * GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT);
				 */
			} else {
				isAttacking = false;
			}
		}
	}

	// Different types of units deal different damage
	// (ex. a light unit would do more damage to a medium unit, but less to a heavy
	// unit)
	public int dealDamagePoints(GameUnit enemy) {
		switch (classType) {
			case UNIT_ID_LIGHT:
				switch (enemy.classType) {
					case UNIT_ID_LIGHT:
						return 2;
					case UNIT_ID_MEDIUM:
						return 3;
					case UNIT_ID_HEAVY:
						return 2;
				}
				break;
			case UNIT_ID_MEDIUM:
				switch (enemy.classType) {
					case UNIT_ID_LIGHT:
						return 2;
					case UNIT_ID_MEDIUM:
						return 2;
					case UNIT_ID_HEAVY:
						return 3;
				}
				break;
			case UNIT_ID_HEAVY:
				switch (enemy.classType) {
					case UNIT_ID_LIGHT:
						return 5;
					case UNIT_ID_MEDIUM:
						return 2;
					case UNIT_ID_HEAVY:
						return 2;
				}
				break;
		}

		return 1;
	}

	/*
	 * Goal: Make sure enemy is within sight of player so that the player cannot
	 * shoot through walls.
	 */
	public boolean checkVisible(int map[][], GameUnit enemy) {
		Point entity1 = new Point(getMapPoint(this.currentPosition));
		Point entity2 = new Point(getMapPoint(enemy.currentPosition));

		// same row
		if (Math.abs(entity1.y - entity2.y) <= 1) {
			return checkRowVisible(map, entity1.x, entity1.y, entity2.x, entity2.y);
		}

		// same column
		if (Math.abs(entity1.x - entity2.x) <= 1) {
			return checkColumnVisible(map, entity1.x, entity1.y, entity2.x, entity2.y);
		}

		// otherwise, trace a line of sight between the two tiles and determine whether
		// the line intersects any tiles
		return checkDiagonalVisible(map, entity1.x, entity1.y, entity2.x, entity2.y);
	}

	public boolean checkRowVisible(int map[][], int entityX1, int entityY1, int entityX2, int entityY2) {
		int minX = Math.min(entityX1, entityX2);
		int maxX = Math.max(entityX1, entityX2);

		for (int x = minX + 1; x <= maxX - 1; x++) {
			if (map[entityY1][x] == GameMap.TILE_WALL)
				return false;
		}

		return true;
	}

	public boolean checkColumnVisible(int map[][], int entityX1, int entityY1, int entityX2, int entityY2) {
		int minY = Math.min(entityY1, entityY2);
		int maxY = Math.max(entityY1, entityY2);

		for (int y = minY; y <= maxY; y++) {
			if (map[y][entityX1] == GameMap.TILE_WALL)
				return false;
		}
		return true;
	}

	public boolean checkDiagonalVisible(int map[][], int entityX1, int entityY1, int entityX2, int entityY2) {
		double deltaY = entityY2 - entityY1;
		double deltaX = entityX2 - entityX1;

		if (entityY1 < entityY2) {
			double slope = deltaX / deltaY;
			double curX = entityX1 + 0.5 * slope;
			for (int y = entityY1 + 1; y <= entityY2; y++) {
				if (map[y][(int) Math.round(curX)] == GameMap.TILE_WALL)
					return false;

				curX += slope;
			}
		} else {
			double slope = deltaX / deltaY;
			double curX = entityX1 - 0.5 * slope;
			for (int y = entityY1 - 1; y >= entityY2; y--) {
				if (map[y][(int) Math.round(curX)] == GameMap.TILE_WALL)
					return false;

				curX -= slope;
			}
		}

		if (entityX1 < entityX2) {
			double curY = entityY1 + 0.5 * deltaY / deltaX;
			for (int x = entityX1 + 1; x <= entityX2; x++) {
				if (map[(int) Math.round(curY)][x] == GameMap.TILE_WALL)
					return false;

				curY += deltaY / deltaX;
			}
		} else {
			double curY = entityY1 - 0.5 * deltaY / deltaX;
			for (int x = entityX1 - 1; x >= entityX2; x--) {
				if (map[(int) Math.round(curY)][x] == GameMap.TILE_WALL)
					return false;

				curY -= deltaY / deltaX;
			}
		}

		return true;
	}
}
