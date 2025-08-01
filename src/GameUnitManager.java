import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Map;

import graphics.Point;
import input.GameMouseEvent;
import map.TileConverter;
import utils.Constants;
import utils.TileCoordinateConverter;

public class GameUnitManager {
	private GameFlagManager flagManager;
	private ArrayList<GameUnit> playerList;
	private ArrayList<GameUnit> enemyList;
	private boolean isSpawned = false; // only spawn once per day

	public GameUnitManager(GameFlagManager flagManager) {
		this.playerList = new ArrayList<>();
		this.enemyList = new ArrayList<>();
		this.flagManager = flagManager;
	}

	public ArrayList<GameUnit> getPlayerList() {
		return playerList;
	}

	public ArrayList<GameUnit> getEnemyList() {
		return enemyList;
	}

	public GameFlagManager getFlagManager() {
		return flagManager;
	}

	public void init(Map<Point, Integer> allyUnitPositions, Map<Point, Integer> enemyUnitPositions, Map<Point, Integer> flagPositions) {
		clearUnits();
		loadAllUnits(allyUnitPositions, enemyUnitPositions);
		loadAllFlags(flagPositions);
	}

	/**
	 * Clears all unit lists.
	 */
	public void clearUnits() {
		playerList.clear();
		enemyList.clear();
	}

	/**
	 * Loads all player and enemy units from the map.
	 */
	public void loadAllUnits(Map<Point, Integer> allyUnitPositions, Map<Point, Integer> enemyUnitPositions) {
		loadPlayerUnits(allyUnitPositions);
		loadEnemyUnits(enemyUnitPositions);
	}

	/**
	 * Loads all flags from the map.
	 */
	public void loadAllFlags(Map<Point, Integer> flagPositions) {
		loadFlag(flagPositions);
	}

	public void loadPlayerUnits(Map<Point, Integer> allyUnitPositions) {
		for (Entry<Point, Integer> entry : allyUnitPositions.entrySet()) {
			Point initialPosition = entry.getKey();
			int classType = entry.getValue();
			playerList.add(new GameUnit(TileCoordinateConverter.mapToScreen(initialPosition.x, initialPosition.y).x, TileCoordinateConverter.mapToScreen(initialPosition.x, initialPosition.y).y,
				true, classType));
		}
	}

	public void loadEnemyUnits(Map<Point, Integer> enemyUnitPositions) {
		for (Entry<Point, Integer> entry : enemyUnitPositions.entrySet()) {
			Point initialPosition = entry.getKey();
			int classType = entry.getValue();
			enemyList.add(new GameUnit(TileCoordinateConverter.mapToScreen(initialPosition.x, initialPosition.y).x, TileCoordinateConverter.mapToScreen(initialPosition.x, initialPosition.y).y,
				false, classType));
		}
	}

	public void loadFlag(Map<Point, Integer> flagPositions) {
		for (Entry<Point, Integer> entry : flagPositions.entrySet()) {
			Point position = entry.getKey();
			int faction = entry.getValue();
			if (faction == GameFlag.FACTION_PLAYER)
				flagManager.addPlayerFlag(position.x, position.y);
			else if (faction == GameFlag.FACTION_ENEMY)
				flagManager.addEnemyFlag(position.x, position.y);
		}
	}

	public void checkFlagStates(GameUnit unit, int factionId) {
		Point unitMapPos = TileCoordinateConverter.screenToMap(unit.getCurrentPosition());
		int unitMapX = unitMapPos.x;
		int unitMapY = unitMapPos.y;
		flagManager.checkFlagState(unitMapX, unitMapY, factionId);
	}

	public boolean isFlagsListEmpty(int factionId) {
		if (factionId == GameFlag.FACTION_PLAYER) {
			return flagManager.isPlayerFlagsEmpty();
		} else if (factionId == GameFlag.FACTION_ENEMY) {
			return flagManager.isEnemyFlagsEmpty();
		} else {
			return true;
		}
	}

	public void removeDeadUnits(int map[][], ArrayList<GameUnit> unitList, int deadUnitIndex) {
		GameUnit deadUnit = unitList.get(deadUnitIndex);
		deadUnit.die(map);
		unitList.remove(deadUnitIndex);
	}

	public String printMap(int[][] map) {
		String s = "MAP:\n";
		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				s += map[y][x];
				s += " ";
			}
			s += "\n";
		}
		return s;
	}
	
	// ==================== UNIT MOVEMENT METHODS ====================
	
	/**
	 * Handles unit movement based on mouse input
	 * 
	 * @param e The mouse event containing click coordinates
	 * @param cameraX The current camera X position
	 * @param cameraY The current camera Y position
	 */
	public void handleUnitMovement(GameMouseEvent e, int cameraX, int cameraY) {
		// Get all selected units
		ArrayList<GameUnit> selectedUnits = getSelectedUnits();
		
		if (selectedUnits.size() == 1) {
			// Single unit - direct movement
			handleSingleUnitMovement(e, selectedUnits.get(0), cameraX, cameraY);
		} else {
			// Multiple units - formation movement
			handleFormationMovement(e, selectedUnits, cameraX, cameraY);
		}
	}
	
	/**
	 * Handles movement for a single unit
	 */
	private void handleSingleUnitMovement(GameMouseEvent e, GameUnit unit, int cameraX, int cameraY) {
		// Convert screen coordinates to world coordinates with camera offset
		unit.setDestination(TileCoordinateConverter.screenToMapWithCamera(
			new Point(e.x, e.y), 
			cameraX, 
			cameraY
		));
		// Convert back to screen coordinates for the destination
		unit.setDestination(TileCoordinateConverter.mapToScreen(unit.getDestination()));

		// Unit should start moving
		unit.startMoving();
	}
	
	/**
	 * Handles movement for multiple units using formation
	 */
	private void handleFormationMovement(GameMouseEvent e, ArrayList<GameUnit> selectedUnits, int cameraX, int cameraY) {
		// Get the target destination (center of formation)
		Point targetDestination = TileCoordinateConverter.screenToMapWithCamera(
			new Point(e.x, e.y), 
			cameraX, 
			cameraY
		);
		
		// Calculate formation positions
		ArrayList<Point> formationPositions = calculateFormationPositions(targetDestination, selectedUnits.size());
		
		// Assign destinations to each unit
		for (int i = 0; i < selectedUnits.size() && i < formationPositions.size(); i++) {
			GameUnit unit = selectedUnits.get(i);
			Point formationPos = formationPositions.get(i);
			
			// Convert formation position to screen coordinates
			unit.setDestination(TileCoordinateConverter.mapToScreen(formationPos));
			unit.startMoving();
		}
	}
	
	/**
	 * Gets all currently selected units
	 */
	private ArrayList<GameUnit> getSelectedUnits() {
		ArrayList<GameUnit> selectedUnits = new ArrayList<>();
		for (GameUnit unit : playerList) {
			if (unit.isPlayerSelected()) {
				selectedUnits.add(unit);
			}
		}
		return selectedUnits;
	}
	
	/**
	 * Calculates formation positions around a target destination
	 * Creates a formation that spreads units out in a logical pattern
	 */
	private ArrayList<Point> calculateFormationPositions(Point targetDestination, int unitCount) {
		ArrayList<Point> positions = new ArrayList<>();
		
		if (unitCount <= 1) {
			positions.add(targetDestination);
			return positions;
		}
		
		// Calculate formation size based on unit count
		int formationRadius = calculateFormationRadius(unitCount);
		
		// Create formation positions
		for (int i = 0; i < unitCount; i++) {
			Point formationPos = calculateFormationPosition(targetDestination, i, unitCount, formationRadius);
			positions.add(formationPos);
		}
		
		return positions;
	}
	
	/**
	 * Calculates the radius of the formation based on unit count
	 */
	private int calculateFormationRadius(int unitCount) {
		// Larger formations need more space
		if (unitCount <= 4) return 1;
		if (unitCount <= 9) return 2;
		if (unitCount <= 16) return 3;
		return 4; // For very large groups
	}
	
	/**
	 * Calculates a specific position in the formation
	 */
	private Point calculateFormationPosition(Point center, int unitIndex, int totalUnits, int radius) {
		// Create a spiral pattern starting from the center
		if (unitIndex == 0) {
			return center; // First unit goes to center
		}
		
		// Calculate position in a grid pattern around the center
		int gridSize = radius * 2 + 1;
		int unitsPerRing = 8; // 8 directions around the center
		
		int ring = (unitIndex - 1) / unitsPerRing + 1;
		int positionInRing = (unitIndex - 1) % unitsPerRing;
		
		if (ring > radius) {
			// Fallback: spread units in a larger area
			ring = radius;
		}
		
		// Calculate offset from center
		int offsetX = 0, offsetY = 0;
		
		switch (positionInRing) {
			case 0: offsetX = ring; offsetY = 0; break;      // East
			case 1: offsetX = ring; offsetY = -ring; break;   // Northeast
			case 2: offsetX = 0; offsetY = -ring; break;      // North
			case 3: offsetX = -ring; offsetY = -ring; break;  // Northwest
			case 4: offsetX = -ring; offsetY = 0; break;      // West
			case 5: offsetX = -ring; offsetY = ring; break;   // Southwest
			case 6: offsetX = 0; offsetY = ring; break;       // South
			case 7: offsetX = ring; offsetY = ring; break;    // Southeast
		}
		
		return new Point(center.x + offsetX, center.y + offsetY);
	}
	
	// ==================== UNIT SPAWNING METHODS ====================
	
	/**
	 * Spawns units near a flag
	 */
	public void spawnUnitsNearFlag(int[][] map, GameFlag flag) {
		// Spawn four units around the flag (N, S, E, W)
		for (int i = 0; i < 4; i++) {
			int flagFactionId = flag.getControlFaction();
			
			// Try WEST first
			if (isTileAvailable(map, flag.getMapX() - 1, flag.getMapY(), flagFactionId)) {
				addUnitToMap(map, flag.getMapX() - 1, flag.getMapY(), flagFactionId);
			}
			// Try EAST
			else if (isTileAvailable(map, flag.getMapX() + 1, flag.getMapY(), flagFactionId)) {
				addUnitToMap(map, flag.getMapX() + 1, flag.getMapY(), flagFactionId);
			}
			// Try NORTH
			else if (isTileAvailable(map, flag.getMapX(), flag.getMapY() - 1, flagFactionId)) {
				addUnitToMap(map, flag.getMapX(), flag.getMapY() - 1, flagFactionId);
			}
			// Try SOUTH
			else if (isTileAvailable(map, flag.getMapX(), flag.getMapY() + 1, flagFactionId)) {
				addUnitToMap(map, flag.getMapX(), flag.getMapY() + 1, flagFactionId);
			} else {
				break; // No available tiles
			}
		}
	}
	
	public boolean isTileAvailable(int[][] map, int x, int y, int factionId) {
		ArrayList<GameUnit> unitList = getUnitList(factionId);
		
		// Check bounds
		if (x < 0 || y < 0 || y >= map.length || x >= map[0].length) {
			return false;
		}
		
		// If there's a wall, then it's occupied
		if (map[y][x] == TileConverter.TILE_WALL) {
			return false;
		}
		
		// If a unit is standing on the desired tile, the tile is considered to be occupied
		for (GameUnit unit : unitList) {
			if (unit.isOnTile(map, x, y)) {
				return false;
			}
		}
		
		return true;
	}
	
	private void addUnitToMap(int[][] map, int x, int y, int factionId) {
		ArrayList<GameUnit> unitList = getUnitList(factionId);
		
		// Create new unit
		GameUnit newUnit = new GameUnit(TileCoordinateConverter.mapToScreen(x, y).x, TileCoordinateConverter.mapToScreen(x, y).y,
				(factionId == GameFlag.FACTION_PLAYER), Constants.UNIT_ID_LIGHT);
		newUnit.spawn(map, new Point(x, y), factionId);
		
		// Add unit to list
		unitList.add(newUnit);
	}
	
	public ArrayList<GameUnit> getUnitList(int factionId) {
		if (factionId == GameFlag.FACTION_PLAYER) {
			return playerList;
		} else if (factionId == GameFlag.FACTION_ENEMY) {
			return enemyList;
		}
		return new ArrayList<>();
	}
	
	public boolean isSpawned() {
		return isSpawned;
	}
	
	public void updateSpawnState(int currentHour) {
		// Set to true at spawn hour to prevent repeated spawning
		if (currentHour == GameTimer.SPAWN_HOUR && !isSpawned) {
			isSpawned = true;
		}
		
		// Reset after the hour has passed
		if (currentHour > GameTimer.SPAWN_HOUR) {
			isSpawned = false;
		}
	}
}
