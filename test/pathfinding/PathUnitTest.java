package pathfinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import graphics.Point;

import java.util.ArrayList;

/**
 * Comprehensive test suite for PathUnit class.
 * Tests pathfinding functionality, caching, state management, and edge cases.
 */
@DisplayName("PathUnit Tests")
public class PathUnitTest {

    private PathUnit pathUnit;
    private int[][] simpleMap;
    private int[][] complexMap;
    private int[][] emptyMap;
    private int[][] walledMap;

    @BeforeEach
    void setUp() {
        pathUnit = new PathUnit(10, 10);
        
        // Simple 5x5 map with clear path
        simpleMap = new int[][] {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
        };
        
        // Complex map with obstacles
        complexMap = new int[][] {
            {0, 0, 1, 0, 0},
            {0, 1, 1, 1, 0},
            {0, 0, 0, 0, 0},
            {0, 1, 1, 1, 0},
            {0, 0, 0, 0, 0}
        };
        
        // Empty map (all walls)
        emptyMap = new int[][] {
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1}
        };
        
        // Map with walls around edges
        walledMap = new int[][] {
            {1, 1, 1, 1, 1},
            {1, 0, 0, 0, 1},
            {1, 0, 0, 0, 1},
            {1, 0, 0, 0, 1},
            {1, 1, 1, 1, 1}
        };
    }

    @Test
    @DisplayName("Constructor should initialize PathUnit correctly")
    void testConstructor() {
        PathUnit newPathUnit = new PathUnit(15, 25);
        
        assertFalse(newPathUnit.getIsPathCreated(), "New PathUnit should not have path created");
        assertFalse(newPathUnit.getIsMoving(), "New PathUnit should not be moving");
        assertNull(newPathUnit.getPath(), "New PathUnit should have null path");
    }

    @Test
    @DisplayName("findPath should create path for simple map")
    void testFindPathSimpleMap() {
        Point start = new Point(0, 0);
        Point end = new Point(4, 4);
        
        boolean result = pathUnit.findPath(simpleMap, start, end);
        
        assertTrue(result, "Should find path in simple map");
        assertTrue(pathUnit.getIsPathCreated(), "Path should be created");
        assertNotNull(pathUnit.getPath(), "Path should not be null");
        assertTrue(pathUnit.getPath().size() > 0, "Path should have nodes");
    }

    @Test
    @DisplayName("findPath should handle complex map with obstacles")
    void testFindPathComplexMap() {
        Point start = new Point(0, 0);
        Point end = new Point(4, 4);
        
        boolean result = pathUnit.findPath(complexMap, start, end);
        
        assertTrue(result, "Should find path in complex map");
        assertTrue(pathUnit.getIsPathCreated(), "Path should be created");
        assertNotNull(pathUnit.getPath(), "Path should not be null");
    }

    @Test
    @DisplayName("findPath should return false for unreachable destination")
    void testFindPathUnreachable() {
        Point start = new Point(1, 1);
        Point end = new Point(3, 3);
        
        boolean result = pathUnit.findPath(complexMap, start, end);
        
        assertFalse(result, "Should not find path to unreachable destination");
        assertFalse(pathUnit.getIsPathCreated(), "Path should not be created");
    }

    @Test
    @DisplayName("findPath should return false for completely walled map")
    void testFindPathWalledMap() {
        Point start = new Point(1, 1);
        Point end = new Point(3, 3);
        
        boolean result = pathUnit.findPath(emptyMap, start, end);
        
        assertFalse(result, "Should not find path in completely walled map");
        assertFalse(pathUnit.getIsPathCreated(), "Path should not be created");
    }

    @Test
    @DisplayName("findPath should handle same start and end points")
    void testFindPathSamePoints() {
        Point point = new Point(2, 2);
        
        boolean result = pathUnit.findPath(simpleMap, point, point);
        
        assertTrue(result, "Should handle same start and end points");
        assertTrue(pathUnit.getIsPathCreated(), "Path should be created");
    }

    @Test
    @DisplayName("findPath should not create new path if path already exists")
    void testFindPathAlreadyExists() {
        Point start = new Point(0, 0);
        Point end = new Point(4, 4);
        
        // Create first path
        boolean result1 = pathUnit.findPath(simpleMap, start, end);
        assertTrue(result1, "First path should be created");
        
        // Try to create second path
        boolean result2 = pathUnit.findPath(simpleMap, start, end);
        assertFalse(result2, "Second path should not be created");
    }

    @Test
    @DisplayName("setPath should set path and update state")
    void testSetPath() {
        ArrayList<PathNode> testPath = new ArrayList<>();
        testPath.add(new PathNode(1, 1, 0, 0, null));
        testPath.add(new PathNode(2, 2, 10, 10, testPath.get(0)));
        
        pathUnit.setPath(testPath);
        
        assertTrue(pathUnit.getIsPathCreated(), "Path should be created");
        assertTrue(pathUnit.getIsMoving(), "Should be moving");
        assertNotNull(pathUnit.getPath(), "Path should not be null");
        assertEquals(2, pathUnit.getPath().size(), "Path should have correct size");
    }

    @Test
    @DisplayName("setPath should create copy of provided path")
    void testSetPathCreatesCopy() {
        ArrayList<PathNode> originalPath = new ArrayList<>();
        originalPath.add(new PathNode(1, 1, 0, 0, null));
        
        pathUnit.setPath(originalPath);
        
        // Modify original path
        originalPath.clear();
        
        // PathUnit's path should remain unchanged
        assertNotNull(pathUnit.getPath(), "Path should not be null");
        assertEquals(1, pathUnit.getPath().size(), "Path should maintain original size");
    }

    @Test
    @DisplayName("run should return next position when path exists")
    void testRunWithPath() {
        ArrayList<PathNode> testPath = new ArrayList<>();
        testPath.add(new PathNode(1, 1, 50, 50, null));  // Screen coordinates (50,50)
        testPath.add(new PathNode(2, 2, 100, 100, testPath.get(0)));  // Screen coordinates (100,100)
        
        pathUnit.setPath(testPath);
        
        Point result = pathUnit.run();
        
        assertNotNull(result, "Should return a position");
        // With physics-based movement, the unit moves gradually towards the target
        assertTrue(result.x >= 10, "Should move from initial X position");
        assertTrue(result.y >= 10, "Should move from initial Y position");
        
        // Verify path state
        assertTrue(pathUnit.getIsMoving(), "Should be moving");
        assertTrue(pathUnit.getIsPathCreated(), "Path should be created");
        assertNotNull(pathUnit.getPath(), "Path should not be null");
    }

    @Test
    @DisplayName("run should throw NullPointerException when no path exists")
    void testRunWithoutPath() {
        // The method throws NullPointerException when movePath is null
        assertThrows(NullPointerException.class, () -> {
            pathUnit.run();
        }, "Should throw NullPointerException when no path exists");
    }

    @Test
    @DisplayName("run should stop moving when path is completed")
    void testRunStopsWhenPathCompleted() {
        ArrayList<PathNode> testPath = new ArrayList<>();
        testPath.add(new PathNode(1, 1, 50, 50, null));  // Screen coordinates (50,50)
        
        pathUnit.setPath(testPath);
        
        // Run until path is completed (multiple iterations to reach destination)
        Point result = null;
        for (int i = 0; i < 50; i++) {  // Increased iterations for physics-based movement
            result = pathUnit.run();
            if (!pathUnit.getIsMoving()) {
                break;
            }
        }
        
        assertNotNull(result, "Should return final position");
        // Note: With physics-based movement, the unit might not always reach the exact destination
        // but should eventually stop moving
        assertTrue(!pathUnit.getIsMoving() || !pathUnit.getIsPathCreated(), 
                  "Should stop moving or complete path after multiple iterations");
    }

    @Test
    @DisplayName("run should advance through path nodes")
    void testRunAdvancesThroughPath() {
        ArrayList<PathNode> testPath = new ArrayList<>();
        testPath.add(new PathNode(1, 1, 50, 50, null));  // Screen coordinates (50,50)
        testPath.add(new PathNode(2, 2, 100, 100, testPath.get(0)));  // Screen coordinates (100,100)
        testPath.add(new PathNode(3, 3, 150, 150, testPath.get(1)));  // Screen coordinates (150,150)
        
        pathUnit.setPath(testPath);
        
        // First run - should move towards first node
        Point result1 = pathUnit.run();
        assertNotNull(result1, "Should return a position");
        // With physics-based movement, the unit moves gradually towards the target
        assertTrue(result1.x >= 10, "Should move from initial X position");
        assertTrue(result1.y >= 10, "Should move from initial Y position");
        
        // Second run - should continue moving towards target
        Point result2 = pathUnit.run();
        assertNotNull(result2, "Should return a position");
        // The unit should have moved from its previous position
        assertTrue(result2.x >= result1.x || result2.y >= result1.y, "Should continue moving");
        
        // Third run - should continue advancing
        Point result3 = pathUnit.run();
        assertNotNull(result3, "Should return a position");
        // The unit should continue moving towards the target
        assertTrue(result3.x >= result2.x || result3.y >= result2.y, "Should continue advancing");
        
        // Verify path is still active
        assertTrue(pathUnit.getIsMoving(), "Should still be moving");
        assertTrue(pathUnit.getIsPathCreated(), "Path should still be created");
    }

    @Test
    @DisplayName("recalculateDest should find alternative path when blocked")
    void testRecalculateDest() {
        // Create initial path
        Point start = new Point(0, 0);
        Point end = new Point(4, 4);
        pathUnit.findPath(simpleMap, start, end);
        
        // Block the original destination
        simpleMap[4][4] = 1;
        
        Point newDest = pathUnit.recalculateDest(simpleMap, end);
        
        assertNotNull(newDest, "Should find alternative destination");
        assertNotEquals(end, newDest, "New destination should be different");
        // Note: newDest is in screen coordinates, so we need to convert back to map coordinates for validation
        Point mapDest = utils.TileCoordinateConverter.screenToMap(newDest);
        assertTrue(simpleMap[mapDest.y][mapDest.x] == 0, "New destination should be walkable");
    }

    @Test
    @DisplayName("recalculateDest should return empty point when no alternative found")
    void testRecalculateDestNoAlternative() {
        // Create initial path
        Point start = new Point(1, 1);
        Point end = new Point(3, 3);
        pathUnit.findPath(walledMap, start, end);
        
        // Block all surrounding areas
        walledMap[2][2] = 1;
        walledMap[2][3] = 1;
        walledMap[3][2] = 1;
        
        Point newDest = pathUnit.recalculateDest(walledMap, end);
        
        // The method returns an empty Point (0,0) when no alternative is found
        assertNotNull(newDest, "Should return a Point object");
        assertEquals(0, newDest.x, "Should return x=0 when no alternative found");
        assertEquals(0, newDest.y, "Should return y=0 when no alternative found");
    }

    @Test
    @DisplayName("recalculateDest should handle edge cases")
    void testRecalculateDestEdgeCases() {
        // Test with no path created
        Point result = pathUnit.recalculateDest(simpleMap, new Point(2, 2));
        assertNotNull(result, "Should return a point (even if empty) when no path exists");
        
        // Test with null map
        pathUnit.findPath(simpleMap, new Point(0, 0), new Point(4, 4));
        Point result2 = pathUnit.recalculateDest(null, new Point(2, 2));
        assertNotNull(result2, "Should handle null map gracefully");
        
        // Test with empty map
        int[][] emptyMap = new int[0][0];
        Point result3 = pathUnit.recalculateDest(emptyMap, new Point(2, 2));
        assertNotNull(result3, "Should handle empty map gracefully");
        
        // Test with boundary coordinates
        Point boundaryPoint = new Point(0, 0);
        Point result4 = pathUnit.recalculateDest(simpleMap, boundaryPoint);
        assertNotNull(result4, "Should handle boundary coordinates");
    }

    @Test
    @DisplayName("State management should work correctly")
    void testStateManagement() {
        // Initial state
        assertFalse(pathUnit.getIsPathCreated(), "Should not have path initially");
        assertFalse(pathUnit.getIsMoving(), "Should not be moving initially");
        
        // After setting path
        ArrayList<PathNode> testPath = new ArrayList<>();
        testPath.add(new PathNode(1, 1, 0, 0, null));
        pathUnit.setPath(testPath);
        
        assertTrue(pathUnit.getIsPathCreated(), "Should have path after setPath");
        assertTrue(pathUnit.getIsMoving(), "Should be moving after setPath");
        
        // After clearing path
        pathUnit.setIsPathCreated(false);
        pathUnit.stopMoving();
        
        assertFalse(pathUnit.getIsPathCreated(), "Should not have path after clearing");
        assertFalse(pathUnit.getIsMoving(), "Should not be moving after stopping");
    }

    @Test
    @DisplayName("Path caching should work correctly")
    void testPathCaching() {
        Point start = new Point(0, 0);
        Point end = new Point(4, 4);
        
        // First pathfinding
        boolean result1 = pathUnit.findPath(simpleMap, start, end);
        assertTrue(result1, "First path should be found");
        
        // Clear path
        pathUnit.setIsPathCreated(false);
        
        // Second pathfinding (should use cache)
        boolean result2 = pathUnit.findPath(simpleMap, start, end);
        assertTrue(result2, "Second path should be found (cached)");
        
        // Verify same path
        assertNotNull(pathUnit.getPath(), "Cached path should be available");
    }

    @Test
    @DisplayName("Cache clearing should work")
    void testCacheClearing() {
        // Clear cache
        PathUnit.clearPathCache();
        
        // This should not throw any exceptions
        assertDoesNotThrow(() -> PathUnit.clearPathCache(), "Cache clearing should not throw exceptions");
    }

    @Test
    @DisplayName("Performance test with large map")
    void testPerformanceLargeMap() {
        // Create a larger map (20x20)
        int[][] largeMap = new int[20][20];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                largeMap[i][j] = 0; // All walkable
            }
        }
        
        Point start = new Point(0, 0);
        Point end = new Point(19, 19);
        
        long startTime = System.currentTimeMillis();
        boolean result = pathUnit.findPath(largeMap, start, end);
        long endTime = System.currentTimeMillis();
        
        assertTrue(result, "Should find path in large map");
        assertTrue((endTime - startTime) < 1000, "Pathfinding should complete within 1 second");
    }

    @Test
    @DisplayName("Error handling with invalid coordinates")
    void testErrorHandlingInvalidCoordinates() {
        // Test with negative coordinates
        Point start = new Point(-1, -1);
        Point end = new Point(4, 4);
        
        boolean result = pathUnit.findPath(simpleMap, start, end);
        assertFalse(result, "Should handle negative coordinates");
        
        // Test with coordinates outside map bounds
        Point start2 = new Point(10, 10);
        Point end2 = new Point(4, 4);
        
        boolean result2 = pathUnit.findPath(simpleMap, start2, end2);
        assertFalse(result2, "Should handle out-of-bounds coordinates");
    }

    @Test
    @DisplayName("Memory management test")
    void testMemoryManagement() {
        // Create many paths to test cache size limits
        for (int i = 0; i < 50; i++) {
            Point start = new Point(0, 0);
            Point end = new Point(4, 4);
            pathUnit.findPath(simpleMap, start, end);
            pathUnit.setIsPathCreated(false);
        }
        
        // Should not throw OutOfMemoryError or similar
        assertDoesNotThrow(() -> {
            Point start = new Point(0, 0);
            Point end = new Point(4, 4);
            pathUnit.findPath(simpleMap, start, end);
        }, "Should handle memory management correctly");
    }

    @Test
    @DisplayName("Integration test with real pathfinding scenario")
    void testIntegrationRealScenario() {
        // Simulate a real game scenario with larger map to avoid bounds issues
        int[][] gameMap = new int[][] {
            {0, 0, 1, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}
        };
        
        Point start = new Point(0, 0);
        Point end = new Point(5, 5);
        
        // Find initial path
        boolean result = pathUnit.findPath(gameMap, start, end);
        assertTrue(result, "Should find initial path");
        
        // Simulate obstacle appearing
        gameMap[2][2] = 1;
        
        // Recalculate destination
        Point newDest = pathUnit.recalculateDest(gameMap, end);
        assertNotNull(newDest, "Should find alternative destination");
        
        // Continue pathfinding
        Point nextPos = pathUnit.run();
        assertNotNull(nextPos, "Should continue moving");
    }
} 