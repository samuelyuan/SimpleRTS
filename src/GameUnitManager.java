import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Map;

import graphics.Point;
import input.GameMouseEvent;
import map.MapValidator;
import map.TileConverter;
import utils.Constants;
import utils.TileCoordinateConverter;

public class GameUnitManager {
	private ArrayList<GameUnit> playerList;
	private ArrayList<GameUnit> enemyList;
	private boolean isSpawned = false; // only spawn once per day
	
	// Spawn configuration
	private SpawnConfig spawnConfig;
	
	/**
	 * Configuration class for spawn settings
	 */
	public static class SpawnConfig {
		public int defaultUnitCount = 4;
		public int maxSpawnDistance = 2;
		public int defaultUnitType = Constants.UNIT_ID_LIGHT;
		public boolean useRandomSpawns = true;
		public boolean useFormationSpawns = false;
		public int maxUnitsPerFaction = 50; // Prevent unlimited spawning
		public double spawnChance = 1.0; // 100% chance to spawn
		
		public SpawnConfig() {}
		
		public SpawnConfig(int unitCount, int maxDistance, int unitType) {
			this.defaultUnitCount = unitCount;
			this.maxSpawnDistance = maxDistance;
			this.defaultUnitType = unitType;
		}
	}

	public GameUnitManager() {
		this.playerList = new ArrayList<>();
		this.enemyList = new ArrayList<>();
		this.spawnConfig = new SpawnConfig(); // Default configuration
	}
	
	/**
	 * Sets the spawn configuration
	 */
	public void setSpawnConfig(SpawnConfig config) {
		this.spawnConfig = config;
	}
	
	/**
	 * Gets the current spawn configuration
	 */
	public SpawnConfig getSpawnConfig() {
		return spawnConfig;
	}
	
	/**
	 * Checks if spawning is allowed based on current conditions
	 */
	public boolean canSpawn(int factionId) {
		// Check unit limit
		ArrayList<GameUnit> factionUnits = getUnitList(factionId);
		if (factionUnits.size() >= spawnConfig.maxUnitsPerFaction) {
			return false;
		}
		
		// Check spawn chance
		if (Math.random() > spawnConfig.spawnChance) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Gets the total unit count for a faction
	 */
	public int getUnitCount(int factionId) {
		return getUnitList(factionId).size();
	}

	public ArrayList<GameUnit> getPlayerList() {
		return playerList;
	}

	public ArrayList<GameUnit> getEnemyList() {
		return enemyList;
	}

	public void init(Map<Point, Integer> allyUnitPositions, Map<Point, Integer> enemyUnitPositions) {
		clearUnits();
		loadAllUnits(allyUnitPositions, enemyUnitPositions);
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

	/**
	 * Spawns units near a flag with flexible configuration
	 */
	public void spawnUnitsNearFlag(int[][] map, GameFlag flag) {
		int factionId = flag.getControlFaction();
		
		// Check if spawning is allowed
		if (!canSpawn(factionId)) {
			return;
		}
		
		spawnUnitsNearFlag(map, flag, spawnConfig.defaultUnitCount, spawnConfig.maxSpawnDistance, spawnConfig.defaultUnitType);
	}
	
	/**
	 * Spawns units near a flag with custom parameters
	 * @param map The game map
	 * @param flag The flag to spawn units near
	 * @param unitCount Number of units to spawn
	 * @param maxDistance Maximum spawn distance from flag
	 * @param unitType Type of unit to spawn
	 */
	public void spawnUnitsNearFlag(int[][] map, GameFlag flag, int unitCount, int maxDistance, int unitType) {
		int flagFactionId = flag.getControlFaction();
		
		// Check if spawning is allowed
		if (!canSpawn(flagFactionId)) {
			return;
		}
		
		int flagX = flag.getMapX();
		int flagY = flag.getMapY();
		
		// Try different spawn patterns
		int spawnedCount = 0;
		
		// Pattern 1: Adjacent tiles (distance 1)
		spawnedCount += spawnInPattern(map, flagX, flagY, flagFactionId, unitType, 1, unitCount - spawnedCount);
		
		// Pattern 2: Ring around flag (distance 2)
		if (spawnedCount < unitCount && maxDistance >= 2) {
			spawnedCount += spawnInPattern(map, flagX, flagY, flagFactionId, unitType, 2, unitCount - spawnedCount);
		}
		
		// Pattern 3: Random positions within maxDistance (if enabled)
		if (spawnedCount < unitCount && spawnConfig.useRandomSpawns) {
			spawnedCount += spawnRandomly(map, flagX, flagY, flagFactionId, unitType, maxDistance, unitCount - spawnedCount);
		}
		
		// Pattern 4: Formation spawn (if enabled)
		if (spawnedCount < unitCount && spawnConfig.useFormationSpawns) {
			spawnUnitsInFormation(map, flagX, flagY, flagFactionId, unitType, unitCount - spawnedCount);
		}
	}
	
	/**
	 * Spawns units in a ring pattern around a center point
	 */
	private int spawnInPattern(int[][] map, int centerX, int centerY, int factionId, int unitType, int distance, int maxUnits) {
		int spawned = 0;
		
		// Define spawn positions in a ring pattern
		int[][] spawnPositions = {
			{centerX - distance, centerY},           // West
			{centerX + distance, centerY},           // East
			{centerX, centerY - distance},           // North
			{centerX, centerY + distance},           // South
			{centerX - distance, centerY - distance}, // Northwest
			{centerX + distance, centerY - distance}, // Northeast
			{centerX - distance, centerY + distance}, // Southwest
			{centerX + distance, centerY + distance}  // Southeast
		};
		
		// Try each position
		for (int[] pos : spawnPositions) {
			if (spawned >= maxUnits) break;
			
			if (isTileAvailable(map, pos[0], pos[1], factionId)) {
				addUnitToMap(map, pos[0], pos[1], factionId, unitType);
				spawned++;
			}
		}
		
		return spawned;
	}
	
	/**
	 * Spawns units at random positions within a given distance
	 */
	private int spawnRandomly(int[][] map, int centerX, int centerY, int factionId, int unitType, int maxDistance, int maxUnits) {
		int spawned = 0;
		int attempts = 0;
		int maxAttempts = maxUnits * 10; // Prevent infinite loops
		
		while (spawned < maxUnits && attempts < maxAttempts) {
			// Generate random position within maxDistance
			int offsetX = (int)(Math.random() * (maxDistance * 2 + 1)) - maxDistance;
			int offsetY = (int)(Math.random() * (maxDistance * 2 + 1)) - maxDistance;
			
			int spawnX = centerX + offsetX;
			int spawnY = centerY + offsetY;
			
			// Check if position is valid and available
			if (isTileAvailable(map, spawnX, spawnY, factionId)) {
				addUnitToMap(map, spawnX, spawnY, factionId, unitType);
				spawned++;
			}
			
			attempts++;
		}
		
		return spawned;
	}
	
	/**
	 * Spawns units in a formation pattern (useful for reinforcements)
	 */
	public void spawnUnitsInFormation(int[][] map, int centerX, int centerY, int factionId, int unitType, int unitCount) {
		// Calculate formation radius based on unit count
		int radius = calculateFormationRadius(unitCount);
		
		for (int i = 0; i < unitCount; i++) {
			Point formationPos = calculateFormationPosition(new Point(centerX, centerY), i, unitCount, radius);
			
			if (isTileAvailable(map, formationPos.x, formationPos.y, factionId)) {
				addUnitToMap(map, formationPos.x, formationPos.y, factionId, unitType);
			}
		}
	}
	
	/**
	 * Spawns units along a path (useful for reinforcements arriving from off-map)
	 */
	public void spawnUnitsAlongPath(int[][] map, Point startPos, Point endPos, int factionId, int unitType, int unitCount) {
		// Calculate path direction
		int dx = endPos.x - startPos.x;
		int dy = endPos.y - startPos.y;
		
		// Normalize direction
		if (dx != 0) dx = dx / Math.abs(dx);
		if (dy != 0) dy = dy / Math.abs(dy);
		
		// Spawn units along the path
		for (int i = 0; i < unitCount; i++) {
			int spawnX = startPos.x + (dx * i * 2); // Space units apart
			int spawnY = startPos.y + (dy * i * 2);
			
			if (isTileAvailable(map, spawnX, spawnY, factionId)) {
				addUnitToMap(map, spawnX, spawnY, factionId, unitType);
			}
		}
	}
	
	public boolean isTileAvailable(int[][] map, int x, int y, int factionId) {
		ArrayList<GameUnit> unitList = getUnitList(factionId);
		
		// Check bounds
		if (!MapValidator.isValidLocation(map, x, y)) {
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
	
	private void addUnitToMap(int[][] map, int x, int y, int factionId, int unitType) {
		ArrayList<GameUnit> unitList = getUnitList(factionId);
		
		// Create new unit
		GameUnit newUnit = new GameUnit(TileCoordinateConverter.mapToScreen(x, y).x, TileCoordinateConverter.mapToScreen(x, y).y,
				(factionId == GameFlag.FACTION_PLAYER), unitType);
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
