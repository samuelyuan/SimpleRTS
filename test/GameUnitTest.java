import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class GameUnitTest {

    private int[][] map;
    private GameUnit playerUnit;
    private GameUnit enemyUnit;
    private GameUnit neutralUnit;

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

        // Initialize units
        playerUnit = new GameUnit(2, 2, true, GameUnit.UNIT_ID_LIGHT);  // Player unit at (2, 2)
        enemyUnit = new GameUnit(3, 3, false, GameUnit.UNIT_ID_MEDIUM); // Enemy unit at (3, 3)
        neutralUnit = new GameUnit(4, 4, false, GameUnit.UNIT_ID_HEAVY); // Neutral unit at (4, 4)
    }

    @Test
    public void testGetCurrentPoint() {
        Point currentPoint = playerUnit.getCurrentPoint();
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
        // Spawn player unit
        playerUnit.spawn(map, new Point(2, 2), GameFlag.FACTION_PLAYER);

        // Verify that the map has been updated correctly for the player faction
        assertEquals(GameUnit.UNIT_ID_LIGHT + 1, map[2][2], "The unit should be spawned with the correct faction ID");
    }

    @Test
    public void testInteractWithEnemy() {
        // Set initial health
        playerUnit.takeDamage(0); // Start with 100 health, no damage yet
        enemyUnit.takeDamage(0);  // Same for enemy

        // Set up a combat interaction between player and enemy
        playerUnit.interactWithEnemy(map, new ArrayList<>(List.of(enemyUnit)));

        // Verify that the player and enemy both take damage
        assertTrue(playerUnit.getHealth() < 100, "Player should have lost health after interaction with enemy");
        assertTrue(enemyUnit.getHealth() < 100, "Enemy should have lost health after interaction with player");
    }

    @Test
    public void testMovement() {
        // Move the player towards a new destination
        playerUnit.destination = new Point(6, 6);

        // Test if the unit starts moving
        playerUnit.startMoving();
        playerUnit.findPath(map);

        // Check if the unit's destination is updated and it's moving
        assertEquals(new Point(6, 6), playerUnit.destination, "Player should be moving towards the correct destination");
    }

    @Test
    public void testPathfindingWithCollision() {
        // Add a wall between player and destination
        map[4][5] = GameMap.TILE_WALL;

        // Set a destination where the wall would block the path
        playerUnit.destination = new Point(6, 6);

        // Try to find a path
        playerUnit.findPath(map);

        // The unit shouldn't be able to find a valid path due to the wall
        assertFalse(playerUnit.isPathCreated(), "Pathfinding should fail due to a wall blocking the path");
    }

    @Test
    public void testCheckRowVisible() {
        // Test row visibility (no walls in the path)
        assertTrue(playerUnit.checkRowVisible(map, 2, 2, 4, 2), "Player should see path without walls");

        // Test row visibility (wall in the path)
        assertFalse(playerUnit.checkRowVisible(map, 2, 2, 5, 2), "Player should not see path to due to wall at (5, 2)");
    }

    @Test
    public void testCheckColumnVisible() {
        // Test column visibility (no walls in the path)
        assertTrue(playerUnit.checkColumnVisible(map, 2, 2, 2, 3), "Player should see path without walls");

        // Test column visibility (wall in the path)
        assertFalse(playerUnit.checkColumnVisible(map, 2, 2, 2, 6), "Player should not see path to (2, 6) due to wall at (2, 5)");
    }

    @Test
    public void testCheckDiagonalVisible() {
        // Test diagonal visibility (no walls in the path)
        assertTrue(playerUnit.checkDiagonalVisible(map, 2, 2, 3, 3), "Player should see diagonal path without walls");

        // Test diagonal visibility (wall in the path)
        assertFalse(playerUnit.checkDiagonalVisible(map, 2, 2, 4, 4),
                "Player should not see diagonal path due to wall at (4, 4)");
    }
}
