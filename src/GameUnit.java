import java.util.ArrayList;

import graphics.Point;
import map.TileConverter;

public class GameUnit {
	// public int state, waypointNum, waypointX, waypointY;

	public Point getMapPoint(Point screenPoint) {
		return TileCoordinateConverter.screenToMap(screenPoint);
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

	public void takeDamage(int damage) {
		this.health -= damage;
		if (this.health < 0) {
			this.health = 0; // Ensure health doesn't go below 0 for player units
		}
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
			map[mapPos.y][mapPos.x] = Constants.UNIT_ID_LIGHT + 1;
		else if (factionId == GameFlag.FACTION_ENEMY)
			map[mapPos.y][mapPos.x] = Constants.UNIT_ID_LIGHT + 4;
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
	private int pathfindingCooldown = 0; // Add cooldown to prevent excessive pathfinding
	private static final int PATHFINDING_COOLDOWN_FRAMES = 10; // Only recalculate every 10 frames

	public void findPath(int[][] map, ArrayList<GameUnit> unitList) {
		Point mapStart = getMapPoint(this.currentPosition);
		Point mapEnd = getMapPoint(this.destination);

		if (!pathUnit.getIsMoving()) return;

		// Add cooldown to prevent excessive pathfinding
		if (pathfindingCooldown > 0) {
			pathfindingCooldown--;
			return;
		}

		// Only recalculate path if destination changed or no path exists
		if (shouldGeneratePath(map, mapStart, mapEnd)) {
			if (generatePath(map, mapStart, mapEnd)) {
				updateMapAfterPathfinding(map, mapStart, mapEnd);
				currentMapEndX = mapEnd.x;
				currentMapEndY = mapEnd.y;
				pathfindingCooldown = PATHFINDING_COOLDOWN_FRAMES; // Set cooldown
			}
		}

		if (pathUnit.isPathFound()) {
			handlePathFound(map, mapEnd, unitList);
		}
	}

	boolean shouldGeneratePath(int[][] map, Point mapStart, Point mapEnd) {
		// Only generate path if we don't have one or destination changed
		return !pathUnit.getIsPathCreated() || destinationChanged(mapEnd);
	}

	boolean generatePath(int[][] map, Point mapStart, Point mapEnd) {
		return pathUnit.findPath(map, mapStart, mapEnd);
	}

	void updateMapAfterPathfinding(int[][] map, Point mapStart, Point mapEnd) {
		if (map[mapStart.y][mapStart.x] != TileConverter.TILE_WALL
				&& map[mapStart.y][mapStart.x] != 8 && map[mapStart.y][mapStart.x] != 9)
			map[mapStart.y][mapStart.x] = 0;

		if (map[mapEnd.y][mapEnd.x] != TileConverter.TILE_WALL
				&& map[mapEnd.y][mapEnd.x] != 8 && map[mapEnd.y][mapEnd.x] != 9)
			map[mapEnd.y][mapEnd.x] = classType + 1;
	}

	private void handlePathFound(int[][] map, Point mapEnd, ArrayList<GameUnit> unitList) {
		if (destinationChanged(mapEnd)) {
			pathUnit.setIsPathCreated(false);
		}
		moveToDestination(map, unitList);
	}

	boolean destinationChanged(Point mapEnd) {
		return currentMapEndX != mapEnd.x || currentMapEndY != mapEnd.y;
	}

	/*
	 * public void drawPathfinding(IGraphics g, GameStateManager stateManager)
	 * {
	 * if (pathUnit.getIsPathCreated() == true)
	 * {
	 * //render the complete path from start to finish
	 * g.setColor(Color.ORANGE);
	 * for (int i = pathUnit.getPath().size() - 1; i >= 0; i--)
	 * {
	 * MapNode pathNode = pathUnit.getPath().get(i);
	 * int screenX = pathNode.getX() * Constants.TILE_WIDTH - stateManager.getCameraX();
	 * int screenY = pathNode.getY() * Constants.TILE_HEIGHT - stateManager.getCameraY();
	 * g.drawRect(screenX, screenY, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
	 * }
	 * }
	 * }
	 */

	/*
	 * Change the player position until player reaches waypoint.
	 */
	public void moveToDestination(int[][] map, ArrayList<GameUnit> unitList) {
		if (!pathUnit.getIsMoving()) return;
		updateGroupDestinations(map, unitList);
		currentPosition = pathUnit.run();
	}

	private void updateGroupDestinations(int[][] map, ArrayList<GameUnit> unitList) {
		for (GameUnit other : unitList) {
			if (other == this) continue;
			if (isSameDestination(other)) {
				if (!other.pathUnit.getIsPathCreated()) continue;
				other.destination = other.pathUnit.recalculateDest(map, getMapPoint(this.destination));
				updateCurrentMapEnd(getMapPoint(other.destination));
			}
		}
	}

	boolean isSameDestination(GameUnit other) {
		Point otherMapDest = getMapPoint(other.destination);
		Point playerMapDest = getMapPoint(this.destination);
		return otherMapDest.equals(playerMapDest);
	}

	private void updateCurrentMapEnd(Point currentEnd) {
		currentMapEndX = currentEnd.x;
		currentMapEndY = currentEnd.y;
	}

	// Package-private setters for testing
	void setCurrentMapEnd(Point p) {
		this.currentMapEndX = p.x;
		this.currentMapEndY = p.y;
	}

	void setCurrentMapEnd(int x, int y) {
		this.currentMapEndX = x;
		this.currentMapEndY = y;
	}

	/*
	 * When the ally unit is close enough to see the enemy, the ally and enemy will
	 * fight each other
	 */
	public void interactWithEnemy(int[][] map, ArrayList<GameUnit> enemyList) {
		for (GameUnit enemy : enemyList) {
			if (canAttackEnemy(map, enemy)) {
				handleAttack(enemy);
			} else {
				isAttacking = false;
			}
		}
	}

	boolean canAttackEnemy(int[][] map, GameUnit enemy) {
		final int ATTACK_RADIUS = 8;
		int manhattanDist = TileCoordinateConverter.manhattanDistanceInTiles(currentPosition, enemy.currentPosition);
		return manhattanDist <= ATTACK_RADIUS && this.checkVisible(map, enemy);
	}

	void handleAttack(GameUnit enemy) {
		isAttacking = true;
		this.health -= enemy.dealDamagePoints(this);
		enemy.health -= this.dealDamagePoints(enemy);
	}

	// Different types of units deal different damage
	// (ex. a light unit would do more damage to a medium unit, but less to a heavy
	// unit)
	public int dealDamagePoints(GameUnit enemy) {
		int attacker = this.classType - 1; // UNIT_ID_LIGHT = 1 → index 0
		int defender = enemy.classType - 1;

		if (attacker < 0 || attacker >= Constants.DAMAGE_MATRIX.length ||
			defender < 0 || defender >= Constants.DAMAGE_MATRIX[0].length) {
			return 1; // fallback value
		}

		return Constants.DAMAGE_MATRIX[attacker][defender];
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

		// Check the entire row from start to end, including endpoints
		for (int x = minX; x <= maxX; x++) {
			if (map[entityY1][x] == TileConverter.TILE_WALL)
				return false;
		}

		return true;
	}

	public boolean checkColumnVisible(int map[][], int entityX1, int entityY1, int entityX2, int entityY2) {
		int minY = Math.min(entityY1, entityY2);
		int maxY = Math.max(entityY1, entityY2);

		// Check the entire column from start to end, including endpoints
		for (int y = minY; y <= maxY; y++) {
			if (map[y][entityX1] == TileConverter.TILE_WALL)
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
				if (map[y][(int) Math.round(curX)] == TileConverter.TILE_WALL)
					return false;

				curX += slope;
			}
		} else {
			double slope = deltaX / deltaY;
			double curX = entityX1 - 0.5 * slope;
			for (int y = entityY1 - 1; y >= entityY2; y--) {
				if (map[y][(int) Math.round(curX)] == TileConverter.TILE_WALL)
					return false;

				curX -= slope;
			}
		}

		if (entityX1 < entityX2) {
			double curY = entityY1 + 0.5 * deltaY / deltaX;
			for (int x = entityX1 + 1; x <= entityX2; x++) {
				if (map[(int) Math.round(curY)][x] == TileConverter.TILE_WALL)
					return false;

				curY += deltaY / deltaX;
			}
		} else {
			double curY = entityY1 - 0.5 * deltaY / deltaX;
			for (int x = entityX1 - 1; x >= entityX2; x--) {
				if (map[(int) Math.round(curY)][x] == TileConverter.TILE_WALL)
					return false;

				curY -= deltaY / deltaX;
			}
		}

		return true;
	}
}
