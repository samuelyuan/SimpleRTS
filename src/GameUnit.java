import java.util.ArrayList;

import graphics.Point;
import map.TileConverter;
import pathfinding.PathNode;
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
	private double rotationAngle = 0.0; // 360-degree rotation angle in degrees
	private double targetRotationAngle = 0.0; // Target rotation angle for smooth interpolation
	private int lastDamageDealt = 0; // Track damage for combat effects
	private boolean wasCriticalHit = false; // Track if last hit was critical

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

	public void stopMoving() {
		pathUnit.stopMoving();
	}

	public ArrayList<PathNode> getPath() {
		return pathUnit.getPath();
	}

	public ArrayList<PathNode> getExploredNodes() {
		return pathUnit.getExploredNodes();
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
	private boolean pathfindingFailed = false; // Track if pathfinding failed
	private int pathfindingFailureTimer = 0; // Timer for showing failure indicator
	private static final int PATHFINDING_FAILURE_DISPLAY_FRAMES = 60; // Show failure indicator for 1 second (60 frames)
	private int pathfindingFailureCount = 0; // Track consecutive failures
	private static final int MAX_PATHFINDING_RETRIES = 5; // Maximum consecutive failures before giving up

	public void findPath(int[][] map, ArrayList<GameUnit> unitList) {
		Point mapStart = getMapPoint(this.currentPosition);
		Point mapEnd = getMapPoint(this.getDestination());

		if (!pathUnit.getIsMoving()) return;

		// Add cooldown to prevent excessive pathfinding
		if (pathfindingCooldown > 0) {
			pathfindingCooldown--;
			return;
		}

		// Stop trying if we've failed too many times
		if (pathfindingFailureCount >= MAX_PATHFINDING_RETRIES) {
			stopMoving();
			pathfindingFailed = true;
			pathfindingFailureTimer = PATHFINDING_FAILURE_DISPLAY_FRAMES;
			return;
		}

		// Only recalculate path if destination changed or no path exists
		if (shouldGeneratePath(map, mapStart, mapEnd)) {
			if (generatePath(map, mapStart, mapEnd)) {
				updateMapAfterPathfinding(map, mapStart, mapEnd);
				currentMapEndX = mapEnd.x;
				currentMapEndY = mapEnd.y;
				pathfindingCooldown = PATHFINDING_COOLDOWN_FRAMES; // Set cooldown
				pathfindingFailed = false; // Clear failure state on success
				pathfindingFailureCount = 0; // Reset failure count on success
			} else {
				// Pathfinding failed - increment failure count and try to find an alternative destination
				pathfindingFailed = true;
				pathfindingFailureTimer = PATHFINDING_FAILURE_DISPLAY_FRAMES;
				pathfindingFailureCount++;
				
				Point alternativeDest = findAlternativeDestination(map, mapEnd);
				if (alternativeDest != null) {
					this.setDestination(alternativeDest);
					// Reset pathfinding state to try again with new destination
					pathUnit.setIsPathCreated(false);
					pathfindingCooldown = 0; // Allow immediate retry
				}
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
				
				Point newDest = other.pathUnit.recalculateDest(map, getMapPoint(this.getDestination()));
				
				// Validate the new destination before setting it
				if (isValidDestination(newDest, map)) {
					other.setDestination(newDest);
					updateCurrentMapEnd(getMapPoint(other.getDestination()));
				} else {
					// If recalculateDest failed, try to find a fallback destination
					Point fallbackDest = findFallbackDestination(map, other);
					if (fallbackDest != null) {
						other.setDestination(fallbackDest);
						updateCurrentMapEnd(getMapPoint(other.getDestination()));
					}
					// If no fallback found, the unit will stay in place (better than moving to invalid location)
				}
			}
		}
	}
	
	// Helper method to validate if a destination is valid
	private boolean isValidDestination(Point dest, int[][] map) {
		if (dest == null) return false;
		
		Point mapPoint = getMapPoint(dest);
		if (mapPoint.x < 0 || mapPoint.y < 0 || 
			mapPoint.y >= map.length || mapPoint.x >= map[0].length) {
			return false;
		}
		
		// Check if the destination is walkable
		return map[mapPoint.y][mapPoint.x] == 0;
	}
	
	// Fallback method to find a valid destination when recalculateDest fails
	private Point findFallbackDestination(int[][] map, GameUnit unit) {
		Point currentPos = getMapPoint(unit.getCurrentPosition());
		int mapHeight = map.length;
		int mapWidth = map[0].length;
		
		// Search in expanding circles around the unit's current position
		// but prioritize closer destinations to avoid long travel distances
		int closestDistance = Integer.MAX_VALUE;
		Point closestTile = null;
		
		for (int radius = 1; radius <= 6; radius++) {
			boolean foundInThisRadius = false;
			
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dx = -radius; dx <= radius; dx++) {
					// Skip corners for efficiency
					if (Math.abs(dx) == radius && Math.abs(dy) == radius) {
						continue;
					}
					
					int newX = currentPos.x + dx;
					int newY = currentPos.y + dy;
					
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
			
			// If we found a reasonably close tile, use it
			if (foundInThisRadius && closestDistance <= 4) {
				break;
			}
		}
		
		// If we found a tile within reasonable distance, use it
		if (closestTile != null && closestDistance <= 6) {
			return TileCoordinateConverter.mapToScreen(closestTile.x, closestTile.y);
		}
		
		// If no fallback found, return null (unit will stay in place)
		return null;
	}
	
	// Helper method to find an alternative destination when pathfinding fails
	private Point findAlternativeDestination(int[][] map, Point originalDest) {
		int mapHeight = map.length;
		int mapWidth = map[0].length;
		
		// Search in expanding circles around the original destination
		// Use simple distance-based approach instead of expensive pathfinding tests
		int closestDistance = Integer.MAX_VALUE;
		Point closestTile = null;
		
		for (int radius = 1; radius <= 4; radius++) { // Reduced max radius
			boolean foundInThisRadius = false;
			
			for (int dy = -radius; dy <= radius; dy++) {
				for (int dx = -radius; dx <= radius; dx++) {
					// Skip corners for efficiency
					if (Math.abs(dx) == radius && Math.abs(dy) == radius) {
						continue;
					}
					
					int newX = originalDest.x + dx;
					int newY = originalDest.y + dy;
					
					// Check bounds
					if (newY < 0 || newY >= mapHeight || newX < 0 || newX >= mapWidth) {
						continue;
					}
					
					// Check if tile is walkable (simple check, no pathfinding test)
					if (map[newY][newX] == 0) {
						int distance = Math.abs(dx) + Math.abs(dy); // Manhattan distance
						
						// Only consider tiles within reasonable distance
						if (distance <= 3 && distance < closestDistance) {
							closestDistance = distance;
							closestTile = new Point(newX, newY);
							foundInThisRadius = true;
						}
					}
				}
			}
			
			// If we found a reasonably close tile, use it immediately
			if (foundInThisRadius && closestDistance <= 2) {
				break;
			}
		}
		
		// If we found a tile within reasonable distance, use it
		if (closestTile != null && closestDistance <= 3) {
			return TileCoordinateConverter.mapToScreen(closestTile.x, closestTile.y);
		}
		
		// If no alternative found, return null
		return null;
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
		boolean canAttackAny = false;
		for (GameUnit enemy : enemyList) {
			if (canAttackEnemy(map, enemy)) {
				handleAttack(enemy);
				canAttackAny = true;
			}
		}
		// Only set isAttacking to false if we can't attack any enemies
		if (!canAttackAny) {
			isAttacking = false;
		}
	}

	boolean canAttackEnemy(int[][] map, GameUnit enemy) {
		final int ATTACK_RADIUS = 8;
		int manhattanDist = TileCoordinateConverter.manhattanDistanceInTiles(currentPosition, enemy.currentPosition);
		// Now includes FOV check - units can only attack enemies they can see within their field of view
		return manhattanDist <= ATTACK_RADIUS && UnitVisibility.checkVisible(map, this, enemy);
	}

	void handleAttack(GameUnit enemy) {
		isAttacking = true;
		
		// Rotate to face the enemy being attacked
		rotateToFaceTarget(enemy);
		
		// Make enemy rotate to face back (for counter-attack)
		enemy.rotateToFaceTarget(this);
		
		// Calculate damage
		int damageToEnemy = this.dealDamagePoints(enemy);
		int damageToSelf = enemy.dealDamagePoints(this);
		
		// Apply damage
		this.health -= damageToSelf;
		enemy.health -= damageToEnemy;
		
		// Notify enemy that it took damage (so it can turn to face attacker)
		enemy.onTakeDamage(this);
		
		// Check for critical hits (10% chance)
		boolean isCriticalHit = Math.random() < 0.1;
		if (isCriticalHit) {
			damageToEnemy = (int)(damageToEnemy * 1.5); // 50% bonus damage
			enemy.health -= (int)(damageToEnemy * 0.5); // Apply bonus damage
		}
		
		// Store damage info for combat effects
		this.lastDamageDealt = damageToEnemy;
		this.wasCriticalHit = isCriticalHit;
		enemy.lastDamageDealt = damageToSelf;
		enemy.wasCriticalHit = false; // Enemy doesn't get critical hits for now
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
	
	/**
	 * Rotates the unit to face a target unit.
	 * This is used during combat to ensure units face their enemies.
	 * 
	 * @param target The unit to face
	 */
	private void rotateToFaceTarget(GameUnit target) {
		Point myPos = this.currentPosition;
		Point targetPos = target.getCurrentPosition();
		
		// Calculate angle to target
		double deltaX = targetPos.x - myPos.x;
		double deltaY = targetPos.y - myPos.y;
		double angleToTarget = Math.toDegrees(Math.atan2(deltaY, deltaX));
		
		// Normalize angle to 0-360 range
		if (angleToTarget < 0) {
			angleToTarget += 360.0;
		}
		
		// Set target rotation angle for smooth rotation
		this.setTargetRotationAngle(angleToTarget);
	}
	
	/**
	 * Called when this unit takes damage from an attacker.
	 * This can be used to make the unit turn to face the attacker,
	 * even if it can't currently attack back (e.g., due to FOV).
	 * 
	 * @param attacker The unit that attacked this unit
	 */
	public void onTakeDamage(GameUnit attacker) {
		// Rotate to face the attacker (even if we can't attack back)
		rotateToFaceTarget(attacker);
	}
	
	// Combat effects getters
	public int getLastDamageDealt() {
		return lastDamageDealt;
	}
	
	public boolean wasLastHitCritical() {
		return wasCriticalHit;
	}
	
	public void clearCombatEffects() {
		lastDamageDealt = 0;
		wasCriticalHit = false;
	}
	
	/**
	 * Updates the pathfinding failure timer
	 */
	public void updatePathfindingFailureTimer() {
		if (pathfindingFailureTimer > 0) {
			pathfindingFailureTimer--;
			if (pathfindingFailureTimer == 0) {
				pathfindingFailed = false; // Clear failure state when timer expires
			}
		}
	}
	
	/**
	 * Returns true if pathfinding recently failed
	 */
	public boolean isPathfindingFailed() {
		return pathfindingFailed && pathfindingFailureTimer > 0;
	}
	
	/**
	 * Returns the remaining failure display time
	 */
	public int getPathfindingFailureTimer() {
		return pathfindingFailureTimer;
	}
}
