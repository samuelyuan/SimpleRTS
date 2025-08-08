import java.util.ArrayList;

import graphics.Point;
import pathfinding.PathNode;
import pathfinding.MovementController;
import pathfinding.PathfindingState;
import pathfinding.PathfindingUtils;
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

	// Combat system
	private CombatSystem combatSystem;

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
	private double rotationAngle = 0.0; // 360-degree rotation angle in degrees
	private double targetRotationAngle = 0.0; // Target rotation angle for smooth interpolation


	public int getDirection() {
		return this.direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public double getRotationAngle() {
		return this.rotationAngle;
	}
	
	public void setRotationAngle(double angle) {
		this.rotationAngle = angle;
		// Keep angle within 0-360 range
		while (this.rotationAngle < 0) this.rotationAngle += 360.0;
		while (this.rotationAngle >= 360.0) this.rotationAngle -= 360.0;
	}
	
	public double getTargetRotationAngle() {
		return this.targetRotationAngle;
	}
	
	public void setTargetRotationAngle(double angle) {
		this.targetRotationAngle = angle;
		// Keep angle within 0-360 range
		while (this.targetRotationAngle < 0) this.targetRotationAngle += 360.0;
		while (this.targetRotationAngle >= 360.0) this.targetRotationAngle -= 360.0;
	}
	
	// Update rotation smoothly towards target angle
	public void updateRotation() {
		if (Math.abs(this.rotationAngle - this.targetRotationAngle) > Constants.MIN_ROTATION_THRESHOLD) {
			// Calculate shortest rotation direction
			double angleDiff = this.targetRotationAngle - this.rotationAngle;
			
			// Handle angle wrapping (e.g., going from 350° to 10°)
			if (angleDiff > 180.0) {
				angleDiff -= 360.0;
			} else if (angleDiff < -180.0) {
				angleDiff += 360.0;
			}
			
			// Smooth interpolation
			this.rotationAngle += angleDiff * Constants.ROTATION_SMOOTHING_FACTOR;
			
			// Keep angle within 0-360 range
			while (this.rotationAngle < 0) this.rotationAngle += 360.0;
			while (this.rotationAngle >= 360.0) this.rotationAngle -= 360.0;
		}
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
	private MovementController movementController;

	public boolean isPathCreated() {
		return movementController.getIsPathCreated();
	}

	public void startMoving() {
		movementController.startMoving();
	}
	
	public boolean isMoving() {
		return movementController.getIsMoving();
	}

	public void stopMoving() {
		movementController.stopMoving();
	}

	public ArrayList<PathNode> getPath() {
		return movementController.getPath();
	}

	public ArrayList<PathNode> getExploredNodes() {
		return movementController.getExploredNodes();
	}

	public GameUnit(int positionX, int positionY, boolean isPlayerUnit, int classType) {
		this.currentPosition = new Point(positionX, positionY);
		this.destination = new Point();
		this.movementController = new MovementController(positionX, positionY);
		this.combatSystem = new CombatSystem(this);
		this.pathfindingState = new PathfindingState();

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
	private PathfindingState pathfindingState;

	public void findPath(int[][] map, ArrayList<GameUnit> unitList) {
		Point mapStart = getMapPoint(this.currentPosition);
		Point mapEnd = getMapPoint(this.getDestination());

		if (!movementController.getIsMoving()) return;

		// Add cooldown to prevent excessive pathfinding
		if (pathfindingState.isOnCooldown()) {
			pathfindingState.decrementCooldown();
			return;
		}

		// Stop trying if we've failed too many times
		if (pathfindingState.hasExceededMaxRetries()) {
			stopMoving();
			pathfindingState.recordFailure();
			return;
		}

		// Only recalculate path if destination changed or no path exists
		if (shouldGeneratePath(map, mapStart, mapEnd)) {
			if (generatePath(map, mapStart, mapEnd)) {
				PathfindingUtils.updateMapAfterPathfinding(map, mapStart, mapEnd, classType);
				pathfindingState.updateDestination(mapEnd);
				pathfindingState.setCooldown(); // Set cooldown
				pathfindingState.recordSuccess(); // Clear failure state on success
			} else {
				// Pathfinding failed - increment failure count and try to find an alternative destination
				pathfindingState.recordFailure();
				
				Point alternativeDest = PathfindingUtils.findAlternativeDestination(map, mapEnd);
				if (alternativeDest != null) {
					this.setDestination(alternativeDest);
					// Reset pathfinding state to try again with new destination
					movementController.setIsPathCreated(false);
					pathfindingState.resetCooldown(); // Allow immediate retry
				}
			}
		}

		if (movementController.isPathFound()) {
			handlePathFound(map, mapEnd, unitList);
		}
	}

	boolean shouldGeneratePath(int[][] map, Point mapStart, Point mapEnd) {
		// Only generate path if we don't have one or destination changed
		return !movementController.getIsPathCreated() || pathfindingState.destinationChanged(mapEnd);
	}

	boolean generatePath(int[][] map, Point mapStart, Point mapEnd) {
		return movementController.findPath(map, mapStart, mapEnd);
	}

	private void handlePathFound(int[][] map, Point mapEnd, ArrayList<GameUnit> unitList) {
		if (pathfindingState.destinationChanged(mapEnd)) {
			movementController.setIsPathCreated(false);
		}
		moveToDestination(map, unitList);
	}



	/*
	 * Change the player position until player reaches waypoint.
	 */
	public void moveToDestination(int[][] map, ArrayList<GameUnit> unitList) {
		if (!movementController.getIsMoving()) return;
		updateGroupDestinations(map, unitList);
		currentPosition = movementController.run();
	}

	private void updateGroupDestinations(int[][] map, ArrayList<GameUnit> unitList) {
		// Get all units for collision detection
		ArrayList<GameUnit> allUnits = new ArrayList<>();
		allUnits.addAll(unitList);
		// Note: In a full implementation, you'd also add enemy units here
		
		for (GameUnit other : unitList) {
			if (other == this) continue;
			if (isSameDestination(other)) {
				if (!other.movementController.getIsPathCreated()) continue;
				
				Point newDest = other.movementController.recalculateDest(map, getMapPoint(this.getDestination()));
				
				// Validate the new destination before setting it
				if (PathfindingUtils.isValidDestination(newDest, map) && !isDestinationOccupiedByUnit(newDest, allUnits)) {
					other.setDestination(newDest);
					other.pathfindingState.updateDestination(getMapPoint(other.getDestination()));
				} else {
					// If recalculateDest failed, try to find a fallback destination
					Point fallbackDest = PathfindingUtils.findFallbackDestination(map, getMapPoint(other.getCurrentPosition()));
					if (fallbackDest != null) {
						other.setDestination(fallbackDest);
						other.pathfindingState.updateDestination(getMapPoint(other.getDestination()));
					}
					// If no fallback found, the unit will stay in place (better than moving to invalid location)
				}
			}
		}
	}
	


// Check if a destination is occupied by another unit
private boolean isDestinationOccupiedByUnit(Point dest, ArrayList<GameUnit> allUnits) {
	if (allUnits == null) return false;
	
	for (GameUnit unit : allUnits) {
		if (unit == this) continue; // Skip self
		if (!unit.isAlive()) continue; // Skip dead units
		
		// Check if unit is at or very close to the destination
		Point unitPos = unit.getCurrentPosition();
		double distance = Math.sqrt(
			Math.pow(dest.x - unitPos.x, 2) + 
			Math.pow(dest.y - unitPos.y, 2)
		);
		
		// If unit is within 25 pixels (half a tile), consider it occupied
		if (distance < 25) {
			return true;
		}
	}
	return false;
}


	


	boolean isSameDestination(GameUnit other) {
		Point otherMapDest = getMapPoint(other.getDestination());
		Point playerMapDest = getMapPoint(this.getDestination());
		return otherMapDest.equals(playerMapDest);
	}

	// Package-private setters for testing
	void setCurrentMapEnd(Point p) {
		pathfindingState.setCurrentMapEnd(p);
	}

	void setCurrentMapEnd(int x, int y) {
		pathfindingState.setCurrentMapEnd(x, y);
	}

	/*
	 * When the ally unit is close enough to see the enemy, the ally and enemy will
	 * fight each other
	 */
	public void interactWithEnemy(int[][] map, ArrayList<GameUnit> enemyList) {
		boolean canAttackAny = false;
		for (GameUnit enemy : enemyList) {
			if (combatSystem.canAttackEnemy(map, enemy)) {
				combatSystem.handleAttack(enemy);
				canAttackAny = true;
			}
		}
		// Only set isAttacking to false if we can't attack any enemies
		if (!canAttackAny) {
			combatSystem.setAttacking(false);
		}
	}


	
	/**
	 * Updates the pathfinding failure timer
	 */
	public void updatePathfindingFailureTimer() {
		pathfindingState.updateFailureTimer();
	}
	
	/**
	 * Returns true if pathfinding recently failed
	 */
	public boolean isPathfindingFailed() {
		return pathfindingState.isPathfindingFailed();
	}
	
	/**
	 * Returns the remaining failure display time
	 */
	public int getPathfindingFailureTimer() {
		return pathfindingState.getFailureTimer();
	}
	
	/**
	 * Returns the combat system for this unit
	 */
	public CombatSystem getCombatSystem() {
		return combatSystem;
	}
	
	/**
	 * Returns the pathfinding state for this unit
	 */
	public PathfindingState getPathfindingState() {
		return pathfindingState;
	}
	
	// Combat delegation methods for backward compatibility
	public boolean isAttacking() {
		return combatSystem.isAttacking();
	}
	
	public void setAttacking(boolean attacking) {
		combatSystem.setAttacking(attacking);
	}
	
	public int getLastDamageDealt() {
		return combatSystem.getLastDamageDealt();
	}
	
	public boolean wasLastHitCritical() {
		return combatSystem.wasLastHitCritical();
	}
	
	public void clearCombatEffects() {
		combatSystem.clearCombatEffects();
	}
	
	// Direct combat methods for testing compatibility
	public boolean canAttackEnemy(int[][] map, GameUnit enemy) {
		return combatSystem.canAttackEnemy(map, enemy);
	}
	
	public void handleAttack(GameUnit target) {
		combatSystem.handleAttack(target);
	}
}
