import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import graphics.Point;
import map.TileConverter;
import utils.Constants;

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
    public void testFOVWithRotation() {
        // Test FOV when observer is rotated
        
        // Set observer to face north (270 degrees in game coordinates)
        observerUnit.setRotationAngle(270.0);
        
        // Place target directly north of observer (decreasing Y)
        targetUnit.setCurrentPosition(new Point(observerUnit.getCurrentPosition().x, observerUnit.getCurrentPosition().y - 100));
        assertTrue(UnitVisibility.isWithinFOV(observerUnit, targetUnit), 
                "Target directly north should be within FOV when facing north");
        
        // Place target south of observer (behind, increasing Y)
        targetUnit.setCurrentPosition(new Point(observerUnit.getCurrentPosition().x, observerUnit.getCurrentPosition().y + 100));
        assertFalse(UnitVisibility.isWithinFOV(observerUnit, targetUnit), 
                "Target behind should not be within FOV");
    }

    @Test
    public void testCompleteVisibilityWithFOV() {
        // Test that the complete visibility check includes FOV
        
        // Set observer to face east
        observerUnit.setRotationAngle(0.0);
        
        // Place target behind observer (should fail FOV check)
        targetUnit.setCurrentPosition(new Point(observerUnit.getCurrentPosition().x - 100, observerUnit.getCurrentPosition().y));
        assertFalse(UnitVisibility.checkVisible(map, observerUnit, targetUnit), 
                "Complete visibility check should fail when target is outside FOV");
        
        // Place target in front of observer (should pass FOV check)
        targetUnit.setCurrentPosition(new Point(observerUnit.getCurrentPosition().x + 100, observerUnit.getCurrentPosition().y));
        assertTrue(UnitVisibility.checkVisible(map, observerUnit, targetUnit), 
                "Complete visibility check should pass when target is within FOV");
    }

    @Test
    public void testHorizontalVisibility() {
        // Create a simple test map for horizontal visibility
        int[][] simpleMap = new int[][]{
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        };
        
        Point start = new Point(0, 0);
        Point end = new Point(2, 0);
        
        // Clear horizontal line
        assertTrue(UnitVisibility.checkHorizontalVisibility(simpleMap, start, end), 
                "Horizontal visibility should be clear");
        
        // Add wall in the middle
        simpleMap[0][1] = TileConverter.TILE_WALL;
        assertFalse(UnitVisibility.checkHorizontalVisibility(simpleMap, start, end), 
                "Horizontal visibility should be blocked by wall");
    }

    @Test
    public void testVerticalVisibility() {
        // Create a simple test map for vertical visibility
        int[][] simpleMap = new int[][]{
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        };
        
        Point start = new Point(0, 0);
        Point end = new Point(0, 2);
        
        // Clear vertical line
        assertTrue(UnitVisibility.checkVerticalVisibility(simpleMap, start, end), 
                "Vertical visibility should be clear");
        
        // Add wall in the middle
        simpleMap[1][0] = TileConverter.TILE_WALL;
        assertFalse(UnitVisibility.checkVerticalVisibility(simpleMap, start, end), 
                "Vertical visibility should be blocked by wall");
    }

    @Test
    public void testDiagonalVisibility() {
        // Create a simple test map for diagonal visibility
        int[][] simpleMap = new int[][]{
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 0, 0}
        };
        
        Point start = new Point(0, 0);
        Point end = new Point(2, 2);
        
        // Clear diagonal line
        assertTrue(UnitVisibility.checkDiagonalVisibility(simpleMap, start, end), 
                "Diagonal visibility should be clear");
        
        // Add wall in the middle
        simpleMap[1][1] = TileConverter.TILE_WALL;
        assertFalse(UnitVisibility.checkDiagonalVisibility(simpleMap, start, end), 
                "Diagonal visibility should be blocked by wall");
    }

    @Test
    public void testCalculateAngleToTarget() {
        Point from = new Point(0, 0);
        Point to = new Point(100, 0); // East
        
        double angle = UnitVisibility.calculateAngleToTarget(from, to);
        assertEquals(0.0, angle, 0.1, "Angle to east should be 0 degrees");
        
        to = new Point(0, -100); // North (decreasing Y in game coordinates)
        angle = UnitVisibility.calculateAngleToTarget(from, to);
        assertEquals(270.0, angle, 0.1, "Angle to north should be 270 degrees in game coordinates");
        
        to = new Point(-100, 0); // West
        angle = UnitVisibility.calculateAngleToTarget(from, to);
        assertEquals(180.0, angle, 0.1, "Angle to west should be 180 degrees");
        
        to = new Point(0, 100); // South (increasing Y in game coordinates)
        angle = UnitVisibility.calculateAngleToTarget(from, to);
        assertEquals(90.0, angle, 0.1, "Angle to south should be 90 degrees in game coordinates");
    }
} 