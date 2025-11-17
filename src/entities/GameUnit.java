package entities;

import java.util.ArrayList;

import managers.CombatSystem;
import graphics.Point;
import pathfinding.PathNode;
import pathfinding.MovementController;
import pathfinding.PathCache;
import utils.Constants;
import utils.TileCoordinateConverter;

public class GameUnit {
	// Location on map
	private Point currentPosition;
	private Point destination;
	
	// Combat system
	private CombatSystem combatSystem;
	private int classType;
	private boolean isPlayerUnit;
	private int health;
	
	// Movement and rotation
	private int direction = 0; // north, south, east, west
	private double rotationAngle = 0.0; // 360-degree rotation angle in degrees
	private double targetRotationAngle = 0.0; // Target rotation angle for smooth interpolation
	
	// Mouse selection
	private boolean isPlayerSelected;
	private boolean isClickedOn = false; // if clicked on, then unit is selected
	private boolean isHovered = false; // if mouse is hovering over unit
	
	// Pathfinding coordination
	private MovementController movementController;

	// Unit information
	private int factionId;

	public Point getMapPoint(Point screenPoint) {
		return TileCoordinateConverter.screenToMap(screenPoint);
	}

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

	public int getClassType() {
		return this.classType;
	}

	public void setClassType(int classType) {
		this.classType = classType;
	}

	public boolean isPlayerUnit() {
		return isPlayerUnit;
	}

	public void setPlayerUnit(boolean isPlayerUnit) {
		this.isPlayerUnit = isPlayerUnit;
	}

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
		this(positionX, positionY, isPlayerUnit, classType, new PathCache());
	}
	
	public GameUnit(int positionX, int positionY, boolean isPlayerUnit, int classType, PathCache pathCache) {
		this.currentPosition = new Point(positionX, positionY);
		this.destination = new Point();
		
		this.movementController = new MovementController(positionX, positionY, pathCache);
		
		this.combatSystem = new CombatSystem(this);

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

	/**
	 * Delegates pathfinding coordination to the movement controller
	 */
	public void findPath(int[][] map, ArrayList<GameUnit> unitList) {
		Point alternativeDest = movementController.coordinatePathfinding(map, currentPosition, destination, classType);
		if (alternativeDest != null) {
			setDestination(alternativeDest);
		}
		// Update unit position after movement
		setCurrentPosition(movementController.getCurrentPosition());
	}
	
	/**
	 * Moves the unit to its destination along the calculated path.
	 * This method is kept for backward compatibility with tests.
	 */
	public void moveToDestination(int[][] map, ArrayList<GameUnit> unitList) {
		// This method now just delegates to the movement controller
		// The actual movement is handled within coordinatePathfinding
		Point alternativeDest = movementController.coordinatePathfinding(map, currentPosition, destination, classType);
		if (alternativeDest != null) {
			setDestination(alternativeDest);
		}
		// Update unit position after movement
		setCurrentPosition(movementController.getCurrentPosition());
	}

	// Public setters for testing
	public void setCurrentMapEnd(Point p) {
		movementController.setCurrentMapEnd(p);
	}

	public void setCurrentMapEnd(int x, int y) {
		movementController.setCurrentMapEnd(x, y);
	}
	
	/**
	 * Updates the pathfinding failure timer
	 */
	public void updatePathfindingFailureTimer() {
		movementController.updateFailureTimer();
	}
	
	/**
	 * Returns true if pathfinding recently failed
	 */
	public boolean isPathfindingFailed() {
		return movementController.isPathfindingFailed();
	}
	
	/**
	 * Returns the remaining failure display time
	 */
	public int getPathfindingFailureTimer() {
		return movementController.getFailureTimer();
	}
	
	/**
	 * Returns the combat system for this unit
	 */
	public CombatSystem getCombatSystem() {
		return combatSystem;
	}

	/**
	 * Returns the movement controller for this unit
	 */
	public MovementController getMovementController() {
		return movementController;
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

	public int getFactionId() {
		return factionId;
	}

	public void setFactionId(int factionId) {
		this.factionId = factionId;
	}

	public boolean isSelected() {
		return isPlayerSelected;
	}
}

