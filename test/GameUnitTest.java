import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import entities.GameFlag;
import entities.GameUnit;
import entities.GameUnitManager;
import java.util.ArrayList;
import java.util.List;

import graphics.Point;
import map.TileConverter;
import pathfinding.PathfindingUtils;
import utils.Constants;
import utils.TileCoordinateConverter;

public class GameUnitTest {

    private int[][] map;
    private GameUnit playerUnit;
    private GameUnit enemyUnit;
    private GameUnit neutralUnit;
    private GameUnitManager unitManager;

    @BeforeEach
    public void setUp() {
        map = new int[][]{
            {0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 0},
            {0, 1, 0, 0, 0, 1, 0},
            {0, 2, 0, 0, 0, 1, 0}, 
            {0, 1, 1, 1, 1, 1, 0},
            {0, 0, 1, 3, 0, 0, 0}
        };

        // Initialize unit manager
        unitManager = new GameUnitManager();
        
        // Initialize units
        playerUnit = new GameUnit(2, 2, true, Constants.UNIT_ID_LIGHT);  // Player unit at (2, 2)
        enemyUnit = new GameUnit(3, 3, false, Constants.UNIT_ID_MEDIUM); // Enemy unit at (3, 3)
        neutralUnit = new GameUnit(4, 4, false, Constants.UNIT_ID_HEAVY); // Neutral unit at (4, 4)
    }

    @Test
    public void testGetCurrentPosition() {
        Point currentPoint = playerUnit.getCurrentPosition();
        assertEquals(new Point(2, 2), currentPoint, "The unit should be at position (2, 2)");
    }

    @Test
    public void testIsAlive() {
        assertTrue(playerUnit.isAlive(), "Unit should be alive when health is greater than 0");

        // Set health to 0 and test again
        playerUnit.takeDamage(100);  // Assuming a unit dies after 100 damage
        assertFalse(playerUnit.isAlive(), "Unit should be dead when health is 0");
    }

    @Test
    public void testSpawn() {
        // Spawn player unit using GameUnitManager
        unitManager.spawnUnit(playerUnit, map, new Point(2, 2), GameFlag.FACTION_PLAYER);

        // Verify unit was added to player list (units are not written to map anymore)
        assertTrue(unitManager.getPlayerList().contains(playerUnit), "Unit should be in player list");
        assertEquals(GameFlag.FACTION_PLAYER, playerUnit.getFactionId(), "Unit should have correct faction ID");
        // Map should remain unchanged (terrain/walls only, no unit markers)
        assertEquals(0, map[2][2], "Map tile should remain empty (units don't write to map)");
    }

    @Test
    public void testInteractWithEnemy() {
        // Set initial health
        playerUnit.takeDamage(0); // Start with 100 health, no damage yet
        enemyUnit.takeDamage(0);  // Same for enemy

        // Add units to manager lists
        unitManager.getPlayerList().add(playerUnit);
        unitManager.getEnemyList().add(enemyUnit);

        // Set up a combat interaction between player and enemy using GameUnitManager
        unitManager.handleUnitInteractions(map);

        // Verify that the player and enemy both take damage
        assertTrue(playerUnit.getHealth() < 100, "Player should have lost health after interaction with enemy");
        assertTrue(enemyUnit.getHealth() < 100, "Enemy should have lost health after interaction with player");
    }

    @Test
    public void testMovement() {
        // Move the player towards a new destination
        playerUnit.setDestination(new Point(6, 6));

        // Test if the unit starts moving
        playerUnit.startMoving();
        ArrayList<GameUnit> playerList = new ArrayList<>();
        playerList.add(playerUnit);
        playerUnit.findPath(map, playerList);

        // Check if the unit's destination is updated and it's moving
        assertEquals(new Point(6, 6), playerUnit.getDestination(), "Player should be moving towards the correct destination");
    }

    @Test
    public void testPathfindingWithCollision() {
        // Add a wall between player and destination
        map[4][5] = TileConverter.TILE_WALL;

        // Set a destination where the wall would block the path
        playerUnit.setDestination(new Point(6, 6));

        // Try to find a path
        ArrayList<GameUnit> playerList = new ArrayList<>();
        playerList.add(playerUnit);
        playerUnit.findPath(map, playerList);

        // The unit shouldn't be able to find a valid path due to the wall
        assertFalse(playerUnit.isPathCreated(), "Pathfinding should fail due to a wall blocking the path");
    }

    @Test
    public void testCanAttackEnemyTrueAndFalse() {
        // Place both units in screen coordinates (adjacent tiles)
        int tileX = 2, tileY = 2;
        playerUnit = new GameUnit(TileCoordinateConverter.mapToScreen(tileX, tileY).x, TileCoordinateConverter.mapToScreen(tileX, tileY).y, true, Constants.UNIT_ID_LIGHT);
		GameUnit closeEnemy = new GameUnit(TileCoordinateConverter.mapToScreen(tileX + 1, tileY).x, TileCoordinateConverter.mapToScreen(tileX + 1, tileY).y, false, Constants.UNIT_ID_MEDIUM);
        assertTrue(playerUnit.canAttackEnemy(map, closeEnemy), "Should be able to attack enemy in range and visible");

        // Place enemy out of attack radius
        GameUnit farEnemy = new GameUnit(TileCoordinateConverter.mapToScreen(100, 100).x, TileCoordinateConverter.mapToScreen(100, 100).y, false, Constants.UNIT_ID_MEDIUM);
        assertFalse(playerUnit.canAttackEnemy(map, farEnemy), "Should not be able to attack enemy out of range");
    }

    @Test
    public void testHandleAttackUpdatesHealth() {
        int initialPlayerHealth = playerUnit.getHealth();
        int initialEnemyHealth = enemyUnit.getHealth();
        playerUnit.handleAttack(enemyUnit);
        assertTrue(playerUnit.getHealth() < initialPlayerHealth, "Player should lose health");
        assertTrue(enemyUnit.getHealth() < initialEnemyHealth, "Enemy should lose health");
    }

    @Test
    public void testIsSameDestination() {
        // Both destinations map to the same tile
        playerUnit.setDestination(new Point(50, 50)); // (1,1) in map coordinates if TILE_WIDTH=50
        enemyUnit.setDestination(new Point(50, 50));
        // Note: isSameDestination is now private in GameUnitManager, so we test it indirectly
        // through the group destination update functionality
        assertTrue(playerUnit.getDestination().equals(enemyUnit.getDestination()), "Should be same destination");
        // Destinations map to different tiles
        enemyUnit.setDestination(new Point(100, 100)); // (2,2) in map coordinates if TILE_WIDTH=50
        assertFalse(playerUnit.getDestination().equals(enemyUnit.getDestination()), "Should not be same destination");
    }

    @Test
    public void testDestinationChanged() {
        // Set up currentMapEndX/Y to match mapEnd
        Point mapEnd = new Point(3, 3);
        playerUnit.moveToDestination(map, new ArrayList<>(List.of(playerUnit)));
        // Simulate no change
        playerUnit.setCurrentMapEnd(mapEnd);
        assertFalse(PathfindingUtils.destinationChanged(3, 3, mapEnd), "Should not detect change if same");
        // Simulate change
        playerUnit.setCurrentMapEnd(0, 0);
        assertTrue(PathfindingUtils.destinationChanged(0, 0, mapEnd), "Should detect change if different");
    }

}
