import java.util.ArrayList;

import graphics.Point;
import map.TileConverter;
import pathfinding.PathUnit;
import utils.Constants;
import utils.TileCoordinateConverter;

public class GameUnit {
	// public int state, waypointNum, waypointX, waypointY;

	public Point getMapPoint(Point screenPoint) {
		return TileCoordinateConverter.screenToMap(screenPoint);
	}

	// Location on map
	private Point currentPosition;
	private Point destination;

	public Point getCurrentPosition() {
		return this.currentPosition;
	}

	public void setCurrentPosition(Point position) {
		this.currentPosition = position;
	}

	public Point getDestination() {
		return this.destination;
	}

	public void setDestination(Point destination) {
		this.destination = destination;
	}

	// Physical state
	private boolean isAttacking = false; // if attacking, stand still. if not, then move.

	public boolean isAttacking() {
		return this.isAttacking;
	}

	public void setAttacking(boolean attacking) {
		this.isAttacking = attacking;
	}

	private int classType;

	public int getClassType() {
		return this.classType;
	}

	public void setClassType(int classType) {
		this.classType = classType;
	}

	// private int speed = 0;
	private boolean isPlayerUnit;

	public boolean isPlayerUnit() {
		return isPlayerUnit;
	}

	public void setPlayerUnit(boolean isPlayerUnit) {
		this.isPlayerUnit = isPlayerUnit;
	}

	private int health;

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
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

	private int direction = 0; // north, south, east, west

	public int getDirection() {
		return this.direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	// Mouse selection
	private boolean isPlayerSelected;
	private boolean isClickedOn = false; // if clicked on, then unit is selected
	private boolean isHovered = false; // if mouse is hovering over unit

	public boolean isPlayerSelected() {
		return this.isPlayerSelected;
	}

	public void setPlayerSelected(boolean selected) {
		this.isPlayerSelected = selected;
	}

	public boolean isClickedOn() {
		return this.isClickedOn;
	}

	public void setClickedOn(boolean clickedOn) {
		this.isClickedOn = clickedOn;
	}

	public boolean isHovered() {
		return this.isHovered;
	}

	public void setHovered(boolean hovered) {
		this.isHovered = hovered;
	}

	// Misc data
	private PathUnit pathUnit;

	public boolean isPathCreated() {
		return pathUnit.getIsPathCreated();
	}

	public void startMoving() {
		pathUnit.startMoving();
	}
	
	public boolean isMoving() {
		return pathUnit.getIsMoving();
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
		Point mapEnd = getMapPoint(this.getDestination());

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
				other.setDestination(other.pathUnit.recalculateDest(map, getMapPoint(this.getDestination())));
				updateCurrentMapEnd(getMapPoint(other.getDestination()));
			}
		}
	}

	boolean isSameDestination(GameUnit other) {
		Point otherMapDest = getMapPoint(other.getDestination());
		Point playerMapDest = getMapPoint(this.getDestination());
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
		return manhattanDist <= ATTACK_RADIUS && UnitVisibility.checkVisible(map, this, enemy);
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


}
