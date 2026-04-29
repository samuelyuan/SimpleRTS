package entities;

import java.util.ArrayList;
import java.util.Map;

import managers.UnitLifecycleManager;
import managers.UnitSpawnManager;
import managers.UnitCombatManager;
import managers.UnitMovementManager;
import managers.MultiUnitPathfindingManager;
import graphics.Point;
import input.GameMouseEvent;

/**
 * Coordinates unit management by delegating to specialized managers.
 * This class now focuses on coordination rather than implementation details.
 * 
 * Responsibilities:
 * - Coordinates specialized managers
 * - Provides high-level unit management interface
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
        return spawnManager.canSpawn(currentUnitCount);
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
    public void removeDeadUnits(ArrayList<GameUnit> unitList, int deadUnitIndex) {
        lifecycleManager.removeDeadUnits(unitList, deadUnitIndex);
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
        UnitSpawnManager.SpawnConfig config = spawnManager.getSpawnConfig();
        ArrayList<Point> spawnPositions = spawnManager.getSpawnPositionsNearFlag(map, flag, currentUnitCount);
        addUnitsAtMapPositions(spawnPositions, factionId, config.defaultUnitType);
    }
    
    /**
     * Spawns units near a flag with custom parameters
     */
    public void spawnUnitsNearFlag(int[][] map, GameFlag flag, int unitCount, int maxDistance, int unitType) {
        int factionId = flag.getControlFaction();
        ArrayList<Point> spawnPositions = spawnManager.getSpawnPositionsNearFlag(map, flag, unitCount, maxDistance);
        addUnitsAtMapPositions(spawnPositions, factionId, unitType);
    }
    
    /**
     * Spawns units in a formation pattern
     */
    public void spawnUnitsInFormation(int[][] map, int centerX, int centerY, int factionId, int unitType, int unitCount) {
        ArrayList<Point> spawnPositions = spawnManager.getSpawnPositionsInFormation(map, centerX, centerY, unitCount);
        addUnitsAtMapPositions(spawnPositions, factionId, unitType);
    }
    
    /**
     * Spawns units along a path between two points
     */
    public void spawnUnitsAlongPath(int[][] map, Point startPos, Point endPos, int factionId, int unitType, int unitCount) {
        ArrayList<Point> spawnPositions = spawnManager.getSpawnPositionsAlongPath(map, startPos, endPos, unitCount);
        addUnitsAtMapPositions(spawnPositions, factionId, unitType);
    }

    private void addUnitsAtMapPositions(ArrayList<Point> mapPositions, int factionId, int unitType) {
        for (Point mapPos : mapPositions) {
            lifecycleManager.createAndAddUnitAtMapPosition(mapPos, factionId, unitType);
        }
    }
    
    /**
     * Spawns a unit on the map at the specified position
     */
    public void spawnUnit(GameUnit unit, int factionId) {
        lifecycleManager.spawnUnit(unit, factionId);
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
     * Checks if a tile is available for unit placement
     */
    public boolean isTileAvailable(int[][] map, int x, int y) {
        return spawnManager.isTileAvailable(map, x, y);
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
    public void cleanupDeadUnits() {
        lifecycleManager.cleanupDeadUnits();
    }
}

