import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import entities.GameFlag;
import entities.GameUnit;
import entities.GameUnitManager;
import managers.UnitSpawnManager;
import graphics.Point;
import utils.Constants;
import map.TileConverter;

/**
 * Test class for the flexible unit spawning system
 */
public class UnitSpawnTest {
    private GameUnitManager unitManager;
    private int[][] testMap;
    private GameFlag testFlag;
    
    @BeforeEach
    void setUp() {
        unitManager = new GameUnitManager();
        
        // Create a simple test map (10x10 with some walls)
        testMap = new int[10][10];
        // Add some walls
        testMap[3][3] = 1; // Wall
        testMap[3][4] = 1; // Wall
        testMap[4][3] = 1; // Wall
        
        // Create a test flag at position (5, 5)
        testFlag = new GameFlag(5, 5, GameFlag.FACTION_PLAYER);
    }
    
    @Test
    void testDefaultSpawn() {
        // Test default spawning behavior
        int initialCount = unitManager.getUnitCount(GameFlag.FACTION_PLAYER);
        unitManager.spawnUnitsNearFlag(testMap, testFlag);
        int finalCount = unitManager.getUnitCount(GameFlag.FACTION_PLAYER);
        
        // Should spawn some units (exact count depends on available space)
        assertTrue(finalCount > initialCount);
    }
    
    @Test
    void testCustomSpawnParameters() {
        // Test spawning with custom parameters
        int initialCount = unitManager.getUnitCount(GameFlag.FACTION_PLAYER);
        unitManager.spawnUnitsNearFlag(testMap, testFlag, 2, 1, Constants.UNIT_ID_LIGHT);
        int finalCount = unitManager.getUnitCount(GameFlag.FACTION_PLAYER);
        
        // Should spawn exactly 2 units (or fewer if space is limited)
        assertTrue(finalCount >= initialCount);
        assertTrue(finalCount <= initialCount + 2);
    }
    
    @Test
    void testFormationSpawn() {
        // Test formation spawning
        int initialCount = unitManager.getUnitCount(GameFlag.FACTION_PLAYER);
        unitManager.spawnUnitsInFormation(testMap, 5, 5, GameFlag.FACTION_PLAYER, Constants.UNIT_ID_LIGHT, 3);
        int finalCount = unitManager.getUnitCount(GameFlag.FACTION_PLAYER);
        
        // Should spawn some units in formation
        assertTrue(finalCount > initialCount);
    }
    
    @Test
    void testPathSpawn() {
        // Test spawning along a path
        int initialCount = unitManager.getUnitCount(GameFlag.FACTION_PLAYER);
        Point start = new Point(0, 5);
        Point end = new Point(5, 5);
        unitManager.spawnUnitsAlongPath(testMap, start, end, GameFlag.FACTION_PLAYER, Constants.UNIT_ID_LIGHT, 3);
        int finalCount = unitManager.getUnitCount(GameFlag.FACTION_PLAYER);
        
        // Should spawn some units along the path
        assertTrue(finalCount > initialCount);
    }
    
    @Test
    void testSpawnConfiguration() {
        // Test spawn configuration
        UnitSpawnManager.SpawnConfig config = new UnitSpawnManager.SpawnConfig(6, 3, Constants.UNIT_ID_HEAVY);
        unitManager.setSpawnConfig(config);
        
        // Verify configuration was set
        UnitSpawnManager.SpawnConfig retrievedConfig = unitManager.getSpawnConfig();
        assertEquals(6, retrievedConfig.defaultUnitCount);
        assertEquals(3, retrievedConfig.maxSpawnDistance);
        assertEquals(Constants.UNIT_ID_HEAVY, retrievedConfig.defaultUnitType);
    }
    
    @Test
    void testSpawnConditions() {
        // Test spawn condition checking
        UnitSpawnManager.SpawnConfig config = new UnitSpawnManager.SpawnConfig();
        config.maxUnitsPerFaction = 0; // No units allowed
        unitManager.setSpawnConfig(config);
        
        // Should not be able to spawn when at unit limit
        assertFalse(unitManager.canSpawn(GameFlag.FACTION_PLAYER));
        
        // Reset config
        config.maxUnitsPerFaction = 50;
        config.spawnChance = 0.0; // 0% chance to spawn
        unitManager.setSpawnConfig(config);
        
        // Should not be able to spawn with 0% chance
        assertFalse(unitManager.canSpawn(GameFlag.FACTION_PLAYER));
    }
    
    @Test
    void testUnitCountTracking() {
        // Test unit count tracking
        assertEquals(0, unitManager.getUnitCount(GameFlag.FACTION_PLAYER));
        assertEquals(0, unitManager.getUnitCount(GameFlag.FACTION_ENEMY));
        
        // Spawn some units
        unitManager.spawnUnitsNearFlag(testMap, testFlag, 2, 1, Constants.UNIT_ID_LIGHT);
        
        // Check counts
        assertTrue(unitManager.getUnitCount(GameFlag.FACTION_PLAYER) > 0);
        assertEquals(0, unitManager.getUnitCount(GameFlag.FACTION_ENEMY));
    }
    
    @Test
    void testSpawnWithObstacles() {
        // Test spawning when there are obstacles
        // Create a flag at (5, 5) and block all spawnable positions within maxDistance=1
        GameFlag surroundedFlag = new GameFlag(5, 5, GameFlag.FACTION_PLAYER);
        
        // Block all positions within distance 1 of the flag (but not the flag itself)
        testMap[4][5] = TileConverter.TILE_WALL; // North
        testMap[6][5] = TileConverter.TILE_WALL; // South  
        testMap[5][4] = TileConverter.TILE_WALL; // West
        testMap[5][6] = TileConverter.TILE_WALL; // East
        testMap[4][4] = TileConverter.TILE_WALL; // Northwest
        testMap[4][6] = TileConverter.TILE_WALL; // Northeast
        testMap[6][4] = TileConverter.TILE_WALL; // Southwest
        testMap[6][6] = TileConverter.TILE_WALL; // Southeast
        
        // Also disable random spawning to ensure only pattern spawning is used
        UnitSpawnManager.SpawnConfig config = unitManager.getSpawnConfig();
        config.useRandomSpawns = false;
        unitManager.setSpawnConfig(config);
        
        int initialCount = unitManager.getUnitCount(GameFlag.FACTION_PLAYER);
        unitManager.spawnUnitsNearFlag(testMap, surroundedFlag, 4, 1, Constants.UNIT_ID_LIGHT);
        int finalCount = unitManager.getUnitCount(GameFlag.FACTION_PLAYER);
        
        // Should not spawn units if no space is available
        assertEquals(initialCount, finalCount);
    }
} 