import java.util.ArrayList;
import java.util.Map;

import graphics.Point;
import input.GameMouseEvent;
import pathfinding.PathCache;
import utils.Constants;
import utils.TileCoordinateConverter;

/**
 * Coordinates unit management by delegating to specialized managers.
 * This class now focuses on coordination rather than implementation details.
 * 
 * Responsibilities:
 * - Coordinates specialized managers
 * - Provides high-level unit management interface
 * - Manages shared resources (pathfinding cache)
 */
public class GameUnitManager {
    
    // Specialized managers for different concerns
    private final UnitLifecycleManager lifecycleManager;
    private final UnitSpawnManager spawnManager;
    private final UnitCombatManager combatManager;
    private final UnitMovementManager movementManager;
    
    // Pathfinding manager
    private MultiUnitPathfindingManager pathfindingManager;
    
    public GameUnitManager() {
        this.lifecycleManager = new UnitLifecycleManager();
        this.spawnManager = new UnitSpawnManager();
        this.combatManager = new UnitCombatManager();
        this.movementManager = new UnitMovementManager();
        this.pathfindingManager = new MultiUnitPathfindingManager();
    }
    
    /**
     * Sets the spawn configuration
     */
    public void setSpawnConfig(UnitSpawnManager.SpawnConfig config) {
        spawnManager.setSpawnConfig(config);
    }
    
    /**
     * Gets the current spawn configuration
     */
    public UnitSpawnManager.SpawnConfig getSpawnConfig() {
        return spawnManager.getSpawnConfig();
    }
    
    /**
     * Checks if spawning is allowed based on current conditions
     */
    public boolean canSpawn(int factionId) {
        int currentUnitCount = lifecycleManager.getUnitCount(factionId);
        return spawnManager.canSpawn(factionId, currentUnitCount);
    }
    
    /**
     * Gets the total unit count for a faction
     */
    public int getUnitCount(int factionId) {
        return lifecycleManager.getUnitCount(factionId);
    }
    
    /**
     * Gets the player unit list
     */
    public ArrayList<GameUnit> getPlayerList() {
        return lifecycleManager.getPlayerList();
    }
    
    /**
     * Gets the enemy unit list
     */
    public ArrayList<GameUnit> getEnemyList() {
        return lifecycleManager.getEnemyList();
    }
    
    /**
     * Initializes the unit manager with unit positions
     */
    public void init(Map<Point, Integer> allyUnitPositions, Map<Point, Integer> enemyUnitPositions) {
        lifecycleManager.init(allyUnitPositions, enemyUnitPositions);
    }
    
    /**
     * Clears all units from both lists
     */
    public void clearUnits() {
        lifecycleManager.clearUnits();
    }
    
    /**
     * Loads all units from position maps
     */
    public void loadAllUnits(Map<Point, Integer> allyUnitPositions, Map<Point, Integer> enemyUnitPositions) {
        lifecycleManager.loadAllUnits(allyUnitPositions, enemyUnitPositions);
    }
    
    /**
     * Loads player units from position map
     */
    public void loadPlayerUnits(Map<Point, Integer> allyUnitPositions) {
        lifecycleManager.loadPlayerUnits(allyUnitPositions);
    }
    
    /**
     * Loads enemy units from position map
     */
    public void loadEnemyUnits(Map<Point, Integer> enemyUnitPositions) {
        lifecycleManager.loadEnemyUnits(enemyUnitPositions);
    }
    
    /**
     * Removes dead units from a unit list
     */
    public void removeDeadUnits(int map[][], ArrayList<GameUnit> unitList, int deadUnitIndex) {
        lifecycleManager.removeDeadUnits(map, unitList, deadUnitIndex);
    }
    
    /**
     * Handles unit movement based on mouse input
     */
    public void handleUnitMovement(GameMouseEvent e, int cameraX, int cameraY) {
        movementManager.handleUnitMovement(e, cameraX, cameraY, 
                                        lifecycleManager.getPlayerList(), 
                                        lifecycleManager.getEnemyList());
    }
    
    /**
     * Spawns units near a flag with flexible configuration
     */
    public void spawnUnitsNearFlag(int[][] map, GameFlag flag) {
        int factionId = flag.getControlFaction();
        int currentUnitCount = lifecycleManager.getUnitCount(factionId);
        
        // Get spawn configuration
        UnitSpawnManager.SpawnConfig config = spawnManager.getSpawnConfig();
        
        // Calculate spawn positions using the spawn manager
        int flagX = flag.getMapX();
        int flagY = flag.getMapY();
        
        // Try to spawn units in a pattern around the flag
        int spawnedCount = 0;
        int maxUnits = Math.min(config.defaultUnitCount, 4); // Cap at 4 units for testing
        
        // Pattern: Adjacent tiles (distance 1)
        int[][] spawnPositions = {
            {flagX - 1, flagY},           // West
            {flagX + 1, flagY},           // East
            {flagX, flagY - 1},           // North
            {flagX, flagY + 1}            // South
        };
        
        for (int[] pos : spawnPositions) {
            if (spawnedCount >= maxUnits) break;
            
            if (spawnManager.isTileAvailable(map, pos[0], pos[1], factionId)) {
                // Create a new unit
                Point worldPos = TileCoordinateConverter.mapToScreen(new Point(pos[0], pos[1]));
                GameUnit unit = new GameUnit(worldPos.x, worldPos.y, true, config.defaultUnitType);
                unit.setFactionId(factionId);
                
                // Add unit to the appropriate list
                if (factionId == GameFlag.FACTION_PLAYER) {
                    lifecycleManager.getPlayerList().add(unit);
                } else if (factionId == GameFlag.FACTION_ENEMY) {
                    lifecycleManager.getEnemyList().add(unit);
                }
                
                // Mark the tile as occupied in the map
                map[pos[1]][pos[0]] = (factionId == GameFlag.FACTION_PLAYER) ? 
                    Constants.UNIT_ID_LIGHT + 1 : Constants.UNIT_ID_LIGHT + 4;
                
                spawnedCount++;
            }
        }
    }
    
    /**
     * Spawns units near a flag with custom parameters
     */
    public void spawnUnitsNearFlag(int[][] map, GameFlag flag, int unitCount, int maxDistance, int unitType) {
        int factionId = flag.getControlFaction();
        int flagX = flag.getMapX();
        int flagY = flag.getMapY();
        
        // Try to spawn units in a pattern around the flag
        int spawnedCount = 0;
        
        // Pattern: Adjacent tiles (distance 1)
        int[][] spawnPositions = {
            {flagX - 1, flagY},           // West
            {flagX + 1, flagY},           // East
            {flagX, flagY - 1},           // North
            {flagX, flagY + 1}            // South
        };
        
        for (int[] pos : spawnPositions) {
            if (spawnedCount >= unitCount) break;
            
            if (spawnManager.isTileAvailable(map, pos[0], pos[1], factionId)) {
                // Create a new unit
                Point worldPos = TileCoordinateConverter.mapToScreen(new Point(pos[0], pos[1]));
                GameUnit unit = new GameUnit(worldPos.x, worldPos.y, true, unitType);
                unit.setFactionId(factionId);
                
                // Add unit to the appropriate list
                if (factionId == GameFlag.FACTION_PLAYER) {
                    lifecycleManager.getPlayerList().add(unit);
                } else if (factionId == GameFlag.FACTION_ENEMY) {
                    lifecycleManager.getEnemyList().add(unit);
                }
                
                // Mark the tile as occupied in the map
                map[pos[1]][pos[0]] = (factionId == GameFlag.FACTION_PLAYER) ? 
                    Constants.UNIT_ID_LIGHT + 1 : Constants.UNIT_ID_LIGHT + 4;
                
                spawnedCount++;
            }
        }
        
        // If we need more units and maxDistance allows, try distance 2
        if (spawnedCount < unitCount && maxDistance >= 2) {
            int[][] distance2Positions = {
                {flagX - 2, flagY},           // West
                {flagX + 2, flagY},           // East
                {flagX, flagY - 2},           // North
                {flagX, flagY + 2}            // South
            };
            
            for (int[] pos : distance2Positions) {
                if (spawnedCount >= unitCount) break;
                
                if (spawnManager.isTileAvailable(map, pos[0], pos[1], factionId)) {
                    // Create a new unit
                    Point worldPos = TileCoordinateConverter.mapToScreen(new Point(pos[0], pos[1]));
                    GameUnit unit = new GameUnit(worldPos.x, worldPos.y, true, unitType);
                    unit.setFactionId(factionId);
                    
                    // Add unit to the appropriate list
                    if (factionId == GameFlag.FACTION_PLAYER) {
                        lifecycleManager.getPlayerList().add(unit);
                    } else if (factionId == GameFlag.FACTION_ENEMY) {
                        lifecycleManager.getEnemyList().add(unit);
                    }
                    
                    // Mark the tile as occupied in the map
                    map[pos[1]][pos[0]] = (factionId == GameFlag.FACTION_PLAYER) ? 
                        Constants.UNIT_ID_LIGHT + 1 : Constants.UNIT_ID_LIGHT + 4;
                    
                    spawnedCount++;
                }
            }
        }
    }
    
    /**
     * Spawns units in a formation pattern
     */
    public void spawnUnitsInFormation(int[][] map, int centerX, int centerY, int factionId, int unitType, int unitCount) {
        // Calculate formation radius based on unit count
        int radius = calculateFormationRadius(unitCount);
        
        for (int i = 0; i < unitCount; i++) {
            Point formationPos = calculateFormationPosition(new Point(centerX, centerY), i, unitCount, radius);
            
            if (spawnManager.isTileAvailable(map, formationPos.x, formationPos.y, factionId)) {
                // Create a new unit
                Point worldPos = TileCoordinateConverter.mapToScreen(formationPos);
                GameUnit unit = new GameUnit(worldPos.x, worldPos.y, true, unitType);
                unit.setFactionId(factionId);
                
                // Add unit to the appropriate list
                if (factionId == GameFlag.FACTION_PLAYER) {
                    lifecycleManager.getPlayerList().add(unit);
                } else if (factionId == GameFlag.FACTION_ENEMY) {
                    lifecycleManager.getEnemyList().add(unit);
                }
                
                // Mark the tile as occupied in the map
                map[formationPos.y][formationPos.x] = (factionId == GameFlag.FACTION_PLAYER) ? 
                    Constants.UNIT_ID_LIGHT + 1 : Constants.UNIT_ID_LIGHT + 4;
            }
        }
    }
    
    /**
     * Calculates the radius for formation spawning based on unit count
     */
    private int calculateFormationRadius(int unitCount) {
        if (unitCount <= 4) return 3;
        if (unitCount <= 8) return 4;
        if (unitCount <= 16) return 5;
        return 6;
    }
    
    /**
     * Calculates position for a unit in a formation
     */
    private Point calculateFormationPosition(Point center, int unitIndex, int totalUnits, int radius) {
        if (totalUnits <= 1) {
            return new Point(center.x, center.y);
        }
        
        // Calculate angle and distance for this unit
        double angle = (2 * Math.PI * unitIndex) / totalUnits;
        double distance = radius;
        
        // Calculate offset from center
        int offsetX = (int) (Math.cos(angle) * distance);
        int offsetY = (int) (Math.sin(angle) * distance);
        
        return new Point(center.x + offsetX, center.y + offsetY);
    }
    
    /**
     * Spawns units along a path between two points
     */
    public void spawnUnitsAlongPath(int[][] map, Point startPos, Point endPos, int factionId, int unitType, int unitCount) {
        int spawned = 0;
        double stepX = (endPos.x - startPos.x) / (double) (unitCount + 1);
        double stepY = (endPos.y - startPos.y) / (double) (unitCount + 1);
        
        for (int i = 1; i <= unitCount && spawned < unitCount; i++) {
            int spawnX = (int) (startPos.x + stepX * i);
            int spawnY = (int) (startPos.y + stepY * i);
            
            if (spawnManager.isTileAvailable(map, spawnX, spawnY, factionId)) {
                // Create a new unit
                Point worldPos = TileCoordinateConverter.mapToScreen(new Point(spawnX, spawnY));
                GameUnit unit = new GameUnit(worldPos.x, worldPos.y, true, unitType);
                unit.setFactionId(factionId);
                
                // Add unit to the appropriate list
                if (factionId == GameFlag.FACTION_PLAYER) {
                    lifecycleManager.getPlayerList().add(unit);
                } else if (factionId == GameFlag.FACTION_ENEMY) {
                    lifecycleManager.getEnemyList().add(unit);
                }
                
                // Mark the tile as occupied in the map
                map[spawnY][spawnX] = (factionId == GameFlag.FACTION_PLAYER) ? 
                    Constants.UNIT_ID_LIGHT + 1 : Constants.UNIT_ID_LIGHT + 4;
                
                spawned++;
            }
        }
    }
    
    /**
     * Spawns a unit on the map at the specified position
     */
    public void spawnUnit(GameUnit unit, int[][] map, Point mapPos, int factionId) {
        lifecycleManager.spawnUnit(unit, map, mapPos, factionId);
    }
    
    /**
     * Removes a unit from the map
     */
    public void removeUnit(GameUnit unit, int[][] map) {
        lifecycleManager.removeUnit(unit, map);
    }
    
    /**
     * Handles interactions between player units and enemy units
     */
    public void handleUnitInteractions(int[][] map) {
        combatManager.handleUnitInteractions(map, 
                                          lifecycleManager.getPlayerList(), 
                                          lifecycleManager.getEnemyList());
    }
    
    /**
     * Updates group destinations for units moving to the same destination
     */
    public void updateGroupDestinations(int[][] map) {
        pathfindingManager.updateGroupDestinations(map, 
                                                lifecycleManager.getPlayerList(), 
                                                lifecycleManager.getEnemyList());
    }
    
    /**
     * Gets the shared path cache for pathfinding
     */
    public PathCache getSharedPathCache() {
        return pathfindingManager.getSharedPathCache();
    }

    /**
     * Checks if a tile is available for unit placement
     */
    public boolean isTileAvailable(int[][] map, int x, int y, int factionId) {
        return spawnManager.isTileAvailable(map, x, y, factionId);
    }

    /**
     * Clears the path cache
     */
    public void clearPathCache() {
        pathfindingManager.clearPathCache();
    }
    
    /**
     * Updates the spawn state based on current game time
     */
    public void updateSpawnState(int currentHour) {
        lifecycleManager.updateSpawnState(currentHour);
    }
    
    /**
     * Checks if units have been spawned for the current day
     */
    public boolean isSpawned() {
        return lifecycleManager.isSpawned();
    }
    
    /**
     * Gets the unit list for a specific faction
     */
    public ArrayList<GameUnit> getUnitList(int factionId) {
        return lifecycleManager.getUnitList(factionId);
    }
    
    /**
     * Gets all units from both factions
     */
    public ArrayList<GameUnit> getAllUnits() {
        return lifecycleManager.getAllUnits();
    }
    
    /**
     * Gets all alive units from a faction
     */
    public ArrayList<GameUnit> getAliveUnits(int factionId) {
        return lifecycleManager.getAliveUnits(factionId);
    }
    
    /**
     * Gets all dead units from a faction
     */
    public ArrayList<GameUnit> getDeadUnits(int factionId) {
        return lifecycleManager.getDeadUnits(factionId);
    }
    
    /**
     * Cleans up dead units from both factions
     */
    public void cleanupDeadUnits(int[][] map) {
        lifecycleManager.cleanupDeadUnits(map);
    }
}
