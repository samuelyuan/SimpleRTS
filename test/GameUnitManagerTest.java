import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import graphics.Point;
import input.GameMouseEvent;
import utils.Constants;
import utils.TileCoordinateConverter;

public class GameUnitManagerTest {
    private GameUnitManager unitManager;

    @BeforeEach
    public void setUp() {
        unitManager = new GameUnitManager();
    }

    @Test
    public void testClearUnits() {
        // Add dummy units
        unitManager.getPlayerList().add(new GameUnit(0, 0, true, Constants.UNIT_ID_LIGHT));
        unitManager.getEnemyList().add(new GameUnit(1, 1, false, Constants.UNIT_ID_MEDIUM));
        assertFalse(unitManager.getPlayerList().isEmpty());
        assertFalse(unitManager.getEnemyList().isEmpty());
        unitManager.clearUnits();
        assertTrue(unitManager.getPlayerList().isEmpty());
        assertTrue(unitManager.getEnemyList().isEmpty());
    }

    @Test
    public void testLoadPlayerUnits() {
        Map<Point, Integer> allyUnits = new HashMap<>();
        allyUnits.put(new Point(2, 3), Constants.UNIT_ID_LIGHT);
        allyUnits.put(new Point(4, 5), Constants.UNIT_ID_HEAVY);
        unitManager.loadPlayerUnits(allyUnits);
        assertEquals(2, unitManager.getPlayerList().size());
        assertEquals(TileCoordinateConverter.mapToScreen(2, 3), unitManager.getPlayerList().get(0).getCurrentPosition());
        assertEquals(Constants.UNIT_ID_LIGHT, unitManager.getPlayerList().get(0).getClassType());
    }

    @Test
    public void testLoadEnemyUnits() {
        Map<Point, Integer> enemyUnits = new HashMap<>();
        enemyUnits.put(new Point(1, 1), Constants.UNIT_ID_MEDIUM);
        unitManager.loadEnemyUnits(enemyUnits);
        assertEquals(1, unitManager.getEnemyList().size());
        assertEquals(TileCoordinateConverter.mapToScreen(1, 1), unitManager.getEnemyList().get(0).getCurrentPosition());
        assertEquals(Constants.UNIT_ID_MEDIUM, unitManager.getEnemyList().get(0).getClassType());
    }

    @Test
    public void testLoadAllUnits() {
        Map<Point, Integer> allyUnits = new HashMap<>();
        Map<Point, Integer> enemyUnits = new HashMap<>();
        allyUnits.put(new Point(2, 3), Constants.UNIT_ID_LIGHT);
        enemyUnits.put(new Point(1, 1), Constants.UNIT_ID_MEDIUM);
        unitManager.loadAllUnits(allyUnits, enemyUnits);
        assertEquals(1, unitManager.getPlayerList().size());
        assertEquals(1, unitManager.getEnemyList().size());
    }



    @Test
    public void testRemoveDeadUnits() {
        // Add a unit to the player list
        GameUnit unit = new GameUnit(0, 0, true, Constants.UNIT_ID_LIGHT);
        unitManager.getPlayerList().add(unit);
        assertEquals(1, unitManager.getPlayerList().size());
        
        // Remove the unit
        int[][] map = new int[10][10];
        unitManager.removeDeadUnits(map, unitManager.getPlayerList(), 0);
        assertEquals(0, unitManager.getPlayerList().size());
    }

    @Test
    public void testPrintMap() {
        int[][] map = {{1, 2}, {3, 4}};
        String result = unitManager.printMap(map);
        assertTrue(result.contains("MAP:"));
        assertTrue(result.contains("1 2"));
        assertTrue(result.contains("3 4"));
    }

    // ==================== UNIT MOVEMENT TESTS ====================

    @Test
    public void testHandleUnitMovementSingleUnit() {
        // Create and select a single unit
        GameUnit unit = new GameUnit(100, 100, true, Constants.UNIT_ID_LIGHT);
        unit.setPlayerSelected(true);
        unitManager.getPlayerList().add(unit);
        
        // Create a mouse event for movement
        GameMouseEvent mouseEvent = new GameMouseEvent(GameMouseEvent.Type.PRESSED, 200, 200, 3); // Right click at (200, 200)
        
        // Handle movement
        unitManager.handleUnitMovement(mouseEvent, 0, 0);
        
        // Verify unit is moving and has a destination
        assertTrue(unit.isMoving());
        assertNotNull(unit.getDestination());
    }

    @Test
    public void testHandleUnitMovementMultipleUnits() {
        // Create and select multiple units
        GameUnit unit1 = new GameUnit(100, 100, true, Constants.UNIT_ID_LIGHT);
        GameUnit unit2 = new GameUnit(110, 110, true, Constants.UNIT_ID_MEDIUM);
        unit1.setPlayerSelected(true);
        unit2.setPlayerSelected(true);
        unitManager.getPlayerList().add(unit1);
        unitManager.getPlayerList().add(unit2);
        
        // Create a mouse event for movement
        GameMouseEvent mouseEvent = new GameMouseEvent(GameMouseEvent.Type.PRESSED, 300, 300, 3); // Right click at (300, 300)
        
        // Handle movement
        unitManager.handleUnitMovement(mouseEvent, 0, 0);
        
        // Verify both units are moving and have destinations
        assertTrue(unit1.isMoving());
        assertTrue(unit2.isMoving());
        assertNotNull(unit1.getDestination());
        assertNotNull(unit2.getDestination());
        
        // Verify they have different destinations (formation)
        assertNotEquals(unit1.getDestination(), unit2.getDestination());
    }

    @Test
    public void testHandleUnitMovementNoSelectedUnits() {
        // Create a unit but don't select it
        GameUnit unit = new GameUnit(100, 100, true, Constants.UNIT_ID_LIGHT);
        unit.setPlayerSelected(false);
        unitManager.getPlayerList().add(unit);
        
        // Create a mouse event for movement
        GameMouseEvent mouseEvent = new GameMouseEvent(GameMouseEvent.Type.PRESSED, 200, 200, 3);
        
        // Handle movement - should not throw exception
        assertDoesNotThrow(() -> {
            unitManager.handleUnitMovement(mouseEvent, 0, 0);
        });
        
        // Unit should not be moving since it wasn't selected
        assertFalse(unit.isMoving());
    }

    @Test
    public void testHandleUnitMovementWithCameraOffset() {
        // Create and select a unit
        GameUnit unit = new GameUnit(100, 100, true, Constants.UNIT_ID_LIGHT);
        unit.setPlayerSelected(true);
        unitManager.getPlayerList().add(unit);
        
        // Create a mouse event
        GameMouseEvent mouseEvent = new GameMouseEvent(GameMouseEvent.Type.PRESSED, 200, 200, 3);
        
        // Handle movement with camera offset
        int cameraX = 50;
        int cameraY = 50;
        unitManager.handleUnitMovement(mouseEvent, cameraX, cameraY);
        
        // Verify unit is moving
        assertTrue(unit.isMoving());
        assertNotNull(unit.getDestination());
    }

    @Test
    public void testFormationCalculation() {
        // Test formation calculation indirectly through movement
        GameUnit unit1 = new GameUnit(100, 100, true, Constants.UNIT_ID_LIGHT);
        GameUnit unit2 = new GameUnit(110, 110, true, Constants.UNIT_ID_MEDIUM);
        GameUnit unit3 = new GameUnit(120, 120, true, Constants.UNIT_ID_HEAVY);
        
        unit1.setPlayerSelected(true);
        unit2.setPlayerSelected(true);
        unit3.setPlayerSelected(true);
        
        unitManager.getPlayerList().add(unit1);
        unitManager.getPlayerList().add(unit2);
        unitManager.getPlayerList().add(unit3);
        
        // Move to a specific location
        GameMouseEvent mouseEvent = new GameMouseEvent(GameMouseEvent.Type.PRESSED, 500, 500, 3);
        unitManager.handleUnitMovement(mouseEvent, 0, 0);
        
        // All units should be moving
        assertTrue(unit1.isMoving());
        assertTrue(unit2.isMoving());
        assertTrue(unit3.isMoving());
        
        // All should have different destinations
        assertNotEquals(unit1.getDestination(), unit2.getDestination());
        assertNotEquals(unit2.getDestination(), unit3.getDestination());
        		assertNotEquals(unit1.getDestination(), unit3.getDestination());
	}
	
	// ==================== UNIT SPAWNING TESTS ====================
	
	@Test
	public void testSpawnUnitsNearFlag() {
		// Create a test map
		int[][] map = new int[10][10];
		
		// Create a flag
		GameFlag flag = new GameFlag(5, 5, GameFlag.FACTION_PLAYER);
		
		// Spawn units near the flag
		unitManager.spawnUnitsNearFlag(map, flag);
		
		// Should have spawned units (up to 4)
		assertTrue(unitManager.getPlayerList().size() > 0, "Should spawn at least one unit");
		assertTrue(unitManager.getPlayerList().size() <= 4, "Should spawn at most 4 units");
	}
	
	@Test
	public void testIsTileAvailable() {
		// Create a test map
		int[][] map = new int[10][10];
		
		// Test available tile
		assertTrue(unitManager.isTileAvailable(map, 5, 5, GameFlag.FACTION_PLAYER), "Empty tile should be available");
		
		// Test out of bounds
		assertFalse(unitManager.isTileAvailable(map, -1, 5, GameFlag.FACTION_PLAYER), "Out of bounds should not be available");
		assertFalse(unitManager.isTileAvailable(map, 5, -1, GameFlag.FACTION_PLAYER), "Out of bounds should not be available");
		assertFalse(unitManager.isTileAvailable(map, 10, 5, GameFlag.FACTION_PLAYER), "Out of bounds should not be available");
		assertFalse(unitManager.isTileAvailable(map, 5, 10, GameFlag.FACTION_PLAYER), "Out of bounds should not be available");
	}
	
	@Test
	public void testSpawnStateManagement() {
		// Initially not spawned
		assertFalse(unitManager.isSpawned(), "Should not be spawned initially");
		
		// Update to spawn hour
		unitManager.updateSpawnState(GameTimer.SPAWN_HOUR);
		assertTrue(unitManager.isSpawned(), "Should be spawned at spawn hour");
		
		// Update after spawn hour
		unitManager.updateSpawnState(GameTimer.SPAWN_HOUR + 1);
		assertFalse(unitManager.isSpawned(), "Should reset after spawn hour");
	}
	
	@Test
	public void testGetUnitList() {
		// Test player faction
		ArrayList<GameUnit> playerUnits = unitManager.getUnitList(GameFlag.FACTION_PLAYER);
		assertSame(unitManager.getPlayerList(), playerUnits, "Should return player list for player faction");
		
		// Test enemy faction
		ArrayList<GameUnit> enemyUnits = unitManager.getUnitList(GameFlag.FACTION_ENEMY);
		assertSame(unitManager.getEnemyList(), enemyUnits, "Should return enemy list for enemy faction");
		
		// Test invalid faction
		ArrayList<GameUnit> invalidUnits = unitManager.getUnitList(999);
		assertTrue(invalidUnits.isEmpty(), "Should return empty list for invalid faction");
	}
} 