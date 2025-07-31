import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import graphics.Point;
import map.TileConverter;

/**
 * Tests for the UnitVisibility class.
 */
public class UnitVisibilityTest {

    private int[][] map;
    private GameUnit observerUnit;
    private GameUnit targetUnit;

    @BeforeEach
    public void setUp() {
        // Create a test map with walls
        map = new int[][]{
            {0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 0},
            {0, 1, 0, 0, 0, 1, 0},
            {0, 2, 0, 0, 0, 1, 0}, 
            {0, 1, 1, 1, 1, 1, 0},
            {0, 0, 1, 3, 0, 0, 0}
        };

        // Initialize units
        observerUnit = new GameUnit(2, 2, true, Constants.UNIT_ID_LIGHT);
        targetUnit = new GameUnit(3, 3, false, Constants.UNIT_ID_MEDIUM);
    }

    @Test
    public void testCheckVisibleWithClearLineOfSight() {
        // Units should be able to see each other when there's a clear path
        assertTrue(UnitVisibility.checkVisible(map, observerUnit, targetUnit), 
                "Units should be visible to each other with clear line of sight");
    }

    @Test
    public void testCheckVisibleWithWallBlocking() {
        // Add a wall between the units (observer at map (0,0), target at map (0,0) - same tile)
        // Let's place units further apart and add a wall between them
        GameUnit observerAtMap = new GameUnit(0, 0, true, Constants.UNIT_ID_LIGHT); // map (0,0)
        GameUnit targetAtMap = new GameUnit(100, 0, false, Constants.UNIT_ID_MEDIUM); // map (2,0)
        
        // Add a wall at map position (1,0) to block the line of sight
        map[0][1] = TileConverter.TILE_WALL;
        
        assertFalse(UnitVisibility.checkVisible(map, observerAtMap, targetAtMap), 
                "Units should not be visible when wall blocks line of sight");
    }

    @Test
    public void testCheckRowVisible() {
        // Test row visibility (no walls in the path)
        assertTrue(UnitVisibility.checkRowVisible(map, 2, 2, 4, 2), 
                "Should see path without walls");

        // Test row visibility (wall in the path)
        assertFalse(UnitVisibility.checkRowVisible(map, 2, 2, 5, 2), 
                "Should not see path due to wall at (5, 2)");
    }

    @Test
    public void testCheckColumnVisible() {
        // Test column visibility (no walls in the path)
        assertTrue(UnitVisibility.checkColumnVisible(map, 2, 2, 2, 3), 
                "Should see path without walls");

        // Test column visibility (wall in the path)
        assertFalse(UnitVisibility.checkColumnVisible(map, 2, 2, 2, 6), 
                "Should not see path to (2, 6) due to wall at (2, 5)");
    }

    @Test
    public void testCheckDiagonalVisible() {
        // Test diagonal visibility (no walls in the path)
        assertTrue(UnitVisibility.checkDiagonalVisible(map, 2, 2, 3, 3), 
                "Should see diagonal path without walls");

        // Test diagonal visibility (wall in the path)
        assertFalse(UnitVisibility.checkDiagonalVisible(map, 2, 2, 4, 4),
                "Should not see diagonal path due to wall at (4, 4)");
    }

    @Test
    public void testCalculateDistance() {
        Point point1 = new Point(0, 0);
        Point point2 = new Point(3, 4);
        
        assertEquals(7, UnitVisibility.calculateDistance(point1, point2), 
                "Manhattan distance should be 7");
    }

    @Test
    public void testCalculateEuclideanDistance() {
        Point point1 = new Point(0, 0);
        Point point2 = new Point(3, 4);
        
        assertEquals(5.0, UnitVisibility.calculateEuclideanDistance(point1, point2), 0.001, 
                "Euclidean distance should be 5.0");
    }

    @Test
    public void testCheckVisibleSamePosition() {
        // Units at the same position should be visible to each other
        GameUnit samePosUnit = new GameUnit(2, 2, false, Constants.UNIT_ID_HEAVY);
        
        assertTrue(UnitVisibility.checkVisible(map, observerUnit, samePosUnit), 
                "Units at the same position should be visible to each other");
    }

    @Test
    public void testCheckVisibleEdgeCases() {
        // Test with units at map edges
        GameUnit edgeUnit1 = new GameUnit(0, 0, false, Constants.UNIT_ID_LIGHT);
        GameUnit edgeUnit2 = new GameUnit(6, 6, false, Constants.UNIT_ID_LIGHT);
        
        // These should not throw exceptions
        assertDoesNotThrow(() -> UnitVisibility.checkVisible(map, edgeUnit1, edgeUnit2));
    }
} 