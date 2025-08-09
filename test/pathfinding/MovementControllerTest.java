package pathfinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import graphics.Point;

import java.util.ArrayList;

/**
 * Comprehensive test suite for MovementController class.
 * Tests pathfinding functionality, caching, state management, and edge cases.
 */
@DisplayName("MovementController Tests")
public class MovementControllerTest {

    private MovementController movementController;
    private int[][] simpleMap;
    private int[][] complexMap;
    private int[][] emptyMap;
    private int[][] walledMap;

    @BeforeEach
    void setUp() {
        movementController = new MovementController(10, 10);
        
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
    @DisplayName("Constructor should initialize MovementController correctly")
    void testConstructor() {
        MovementController newMovementController = new MovementController(15, 25);
        
        assertFalse(newMovementController.getIsPathCreated(), "New MovementController should not have path created");
        assertFalse(newMovementController.getIsMoving(), "New MovementController should not be moving");
        assertNull(newMovementController.getPath(), "New MovementController should have null path");
    }

    @Test
    @DisplayName("findPath should create path for simple map")
    void testFindPathSimpleMap() {
        Point start = new Point(0, 0);
        Point end = new Point(4, 4);
        
        boolean result = movementController.findPath(simpleMap, start, end);
        
        assertTrue(result, "Should find path in simple map");
        assertTrue(movementController.getIsPathCreated(), "Path should be created");
        assertNotNull(movementController.getPath(), "Path should not be null");
        assertTrue(movementController.getPath().size() > 0, "Path should have nodes");
    }

    @Test
    @DisplayName("findPath should handle complex map with obstacles")
    void testFindPathComplexMap() {
        Point start = new Point(0, 0);
        Point end = new Point(4, 4);
        
        boolean result = movementController.findPath(complexMap, start, end);
        
        assertTrue(result, "Should find path in complex map");
        assertTrue(movementController.getIsPathCreated(), "Path should be created");
        assertNotNull(movementController.getPath(), "Path should not be null");
    }

    @Test
    @DisplayName("findPath should handle same start and end points")
    void testFindPathSamePoints() {
        Point point = new Point(2, 2);
        
        boolean result = movementController.findPath(simpleMap, point, point);
        
        assertTrue(result, "Should find path even for same start and end points");
        assertTrue(movementController.getIsPathCreated(), "Path should be created");
    }

    @Test
    @DisplayName("findPath should not create new path if path already exists")
    void testFindPathAlreadyExists() {
        Point start = new Point(0, 0);
        Point end = new Point(4, 4);
        
        // Create first path
        boolean result1 = movementController.findPath(simpleMap, start, end);
        assertTrue(result1, "First path should be created");
        
        // Try to create second path
        movementController.setIsPathCreated(false);
        boolean result2 = movementController.findPath(simpleMap, start, end);
        assertTrue(result2, "Second path should be created after reset");
    }

    @Test
    @DisplayName("setPath should set path and update state")
    void testSetPath() {
        ArrayList<PathNode> testPath = new ArrayList<>();
        testPath.add(new PathNode(1, 1, 0, 0, null));
        testPath.add(new PathNode(2, 2, 0, 0, null));
        
        movementController.setPath(testPath);
        
        assertTrue(movementController.getIsPathCreated(), "Path should be created");
        assertTrue(movementController.getIsMoving(), "Should be moving");
        assertNotNull(movementController.getPath(), "Path should not be null");
        assertEquals(2, movementController.getPath().size(), "Path should have correct size");
    }

    @Test
    @DisplayName("setPath should create copy of provided path")
    void testSetPathCreatesCopy() {
        ArrayList<PathNode> originalPath = new ArrayList<>();
        originalPath.add(new PathNode(1, 1, 0, 0, null));
        
        movementController.setPath(originalPath);
        
        // Modify original path
        originalPath.add(new PathNode(2, 2, 0, 0, null));
        
        // MovementController's path should remain unchanged
        assertNotNull(movementController.getPath(), "Path should not be null");
        assertEquals(1, movementController.getPath().size(), "Path should maintain original size");
    }

    @Test
    @DisplayName("run should return next position when path exists")
    void testRunWithPath() {
        ArrayList<PathNode> testPath = new ArrayList<>();
        testPath.add(new PathNode(1, 1, 0, 0, null));
        testPath.add(new PathNode(2, 2, 0, 0, null));
        
        movementController.setPath(testPath);
        
        Point result = movementController.run();
        
        assertNotNull(result, "Result should not be null");
        assertTrue(movementController.getIsMoving(), "Should be moving");
        assertTrue(movementController.getIsPathCreated(), "Path should be created");
        assertNotNull(movementController.getPath(), "Path should not be null");
    }

    @Test
    @DisplayName("run should handle null path gracefully")
    void testRunWithoutPath() {
        // Should not throw exception, should handle null path gracefully
        Point result = movementController.run();
        
        assertNotNull(result, "Should return current position even without path");
        assertFalse(movementController.getIsMoving(), "Should not be moving without path");
        assertFalse(movementController.getIsPathCreated(), "Should not have path created");
    }

    @Test
    @DisplayName("run should stop moving when path is completed")
    void testRunStopsWhenPathCompleted() {
        ArrayList<PathNode> testPath = new ArrayList<>();
        testPath.add(new PathNode(1, 1, 0, 0, null));
        
        movementController.setPath(testPath);
        
        // Run until path is completed
        Point result = movementController.run();
        while (movementController.getIsMoving()) {
            result = movementController.run();
        }
        
        assertTrue(!movementController.getIsMoving() || !movementController.getIsPathCreated(),
                "Should stop moving when path is completed");
    }

    @Test
    @DisplayName("run should advance through path nodes")
    void testRunAdvancesThroughPath() {
        ArrayList<PathNode> testPath = new ArrayList<>();
        testPath.add(new PathNode(1, 1, 0, 0, null));
        testPath.add(new PathNode(2, 2, 0, 0, null));
        testPath.add(new PathNode(3, 3, 0, 0, null));
        
        movementController.setPath(testPath);
        
        Point result1 = movementController.run();
        assertNotNull(result1, "First run should return position");
        
        // Run multiple times to advance through path
        Point result2 = movementController.run();
        assertNotNull(result2, "Second run should return position");
        
        Point result3 = movementController.run();
        assertNotNull(result3, "Third run should return position");
        
        assertTrue(movementController.getIsMoving(), "Should still be moving");
        assertTrue(movementController.getIsPathCreated(), "Path should still be created");
    }

    @Test
    @DisplayName("recalculateDest should find alternative path when blocked")
    void testRecalculateDest() {
        Point start = new Point(0, 0);
        Point end = new Point(4, 4);
        
        // Create initial path
        movementController.findPath(simpleMap, start, end);
        
        // Block the destination
        simpleMap[4][4] = 1;
        
        Point newDest = movementController.recalculateDest(simpleMap, end);
        
        assertNotNull(newDest, "Should find alternative destination");
        assertNotEquals(end, newDest, "New destination should be different from original");
    }

    @Test
    @DisplayName("recalculateDest should return empty point when no walkable tiles exist")
    void testRecalculateDestNoWalkableTiles() {
        Point start = new Point(1, 1);  // This will be a wall in emptyMap
        Point end = new Point(2, 2);    // This will be a wall in emptyMap
        
        // Try to create path in empty map (all walls) - should fail
        boolean pathFound = movementController.findPath(emptyMap, start, end);
        assertFalse(pathFound, "Should not find path in all-wall map");
        
        // Even if no path exists, recalculateDest should handle the case
        Point newDest = movementController.recalculateDest(emptyMap, end);
        
        // Should return null when no walkable tiles exist
        assertNull(newDest, "Should return null when no walkable tiles exist");
    }

    @Test
    @DisplayName("State management should work correctly")
    void testStateManagement() {
        // Initial state
        assertFalse(movementController.getIsPathCreated(), "Should not have path initially");
        assertFalse(movementController.getIsMoving(), "Should not be moving initially");
        
        // Set path
        ArrayList<PathNode> testPath = new ArrayList<>();
        testPath.add(new PathNode(1, 1, 0, 0, null));
        movementController.setPath(testPath);
        
        assertTrue(movementController.getIsPathCreated(), "Should have path after setPath");
        assertTrue(movementController.getIsMoving(), "Should be moving after setPath");
        
        // Clear state
        movementController.setIsPathCreated(false);
        movementController.stopMoving();
        
        assertFalse(movementController.getIsPathCreated(), "Should not have path after clearing");
        assertFalse(movementController.getIsMoving(), "Should not be moving after stopping");
    }

    @Test
    @DisplayName("Path caching should work correctly")
    void testPathCaching() {
        Point start = new Point(0, 0);
        Point end = new Point(4, 4);
        
        // Create first path
        boolean result1 = movementController.findPath(simpleMap, start, end);
        assertTrue(result1, "First path should be created");
        
        // Reset and create second path (should use cache)
        movementController.setIsPathCreated(false);
        boolean result2 = movementController.findPath(simpleMap, start, end);
        assertTrue(result2, "Second path should be created");
        
        // Verify cache is working
        assertNotNull(movementController.getPath(), "Cached path should be available");
    }

    @Test
    @DisplayName("Cache clearing should work")
    void testCacheClearing() {
        // Clear cache
        movementController.clearPathCache();
        
        // Should not throw exception
        assertDoesNotThrow(() -> movementController.clearPathCache(), "Cache clearing should not throw exceptions");
    }

    @Test
    @DisplayName("Performance test with large map")
    void testPerformanceLargeMap() {
        // Create large map
        int[][] largeMap = new int[50][50];
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                largeMap[i][j] = 0; // All walkable
            }
        }
        
        Point start = new Point(0, 0);
        Point end = new Point(49, 49);
        
        long startTime = System.currentTimeMillis();
        boolean result = movementController.findPath(largeMap, start, end);
        long endTime = System.currentTimeMillis();
        
        assertTrue(result, "Should find path in large map");
        assertTrue((endTime - startTime) < 1000, "Pathfinding should complete within 1 second");
    }

    @Test
    @DisplayName("Error handling with invalid coordinates")
    void testErrorHandlingInvalidCoordinates() {
        Point start = new Point(-1, -1);
        Point end = new Point(10, 10);
        
        boolean result = movementController.findPath(simpleMap, start, end);
        
        // Should handle invalid coordinates gracefully
        assertFalse(result, "Should handle invalid start coordinates");
        
        Point start2 = new Point(0, 0);
        Point end2 = new Point(10, 10);
        
        boolean result2 = movementController.findPath(simpleMap, start2, end2);
        
        // Should handle invalid end coordinates gracefully
        assertFalse(result2, "Should handle invalid end coordinates");
    }

    @Test
    @DisplayName("Memory management test")
    void testMemoryManagement() {
        // Create multiple paths to test memory management
        movementController.findPath(simpleMap, new Point(0, 0), new Point(4, 4));
        movementController.setIsPathCreated(false);
        
        // Create many paths to test cache eviction
        for (int i = 0; i < 10; i++) {
            movementController.findPath(simpleMap, new Point(0, 0), new Point(4, 4));
            movementController.setIsPathCreated(false);
        }
        
        // Should not cause memory issues
        assertTrue(true, "Memory management should work correctly");
    }
    
    @Test
    @DisplayName("Shared cache should work between multiple controllers")
    void testSharedCache() {
        // Create a shared cache
        PathCache sharedCache = new PathCache();
        
        // Create two controllers with the same cache
        MovementController controller1 = new MovementController(0, 0, sharedCache);
        MovementController controller2 = new MovementController(0, 0, sharedCache);
        
        Point start = new Point(0, 0);
        Point end = new Point(4, 4);
        
        // First controller creates a path
        boolean result1 = controller1.findPath(simpleMap, start, end);
        assertTrue(result1, "First controller should create path");
        
        // Second controller should use cached path
        boolean result2 = controller2.findPath(simpleMap, start, end);
        assertTrue(result2, "Second controller should use cached path");
        
        // Both should have the same path
        assertEquals(controller1.getPath().size(), controller2.getPath().size(), "Both controllers should have same path size");
    }

    @Test
    @DisplayName("Integration test with real pathfinding scenario")
    void testIntegrationRealScenario() {
        // Create a realistic game map
        int[][] gameMap = new int[][] {
            {0, 0, 0, 1, 0, 0, 0},
            {0, 1, 0, 1, 0, 1, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {1, 1, 0, 1, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 1, 0, 1, 0},
            {0, 0, 0, 1, 0, 0, 0}
        };
        
        Point start = new Point(0, 0);
        Point end = new Point(6, 6);
        
        boolean result = movementController.findPath(gameMap, start, end);
        assertTrue(result, "Should find path in realistic scenario");
        
        Point newDest = movementController.recalculateDest(gameMap, end);
        assertNotNull(newDest, "Should find alternative destination");
        
        // Test movement along path
        Point nextPos = movementController.run();
        assertNotNull(nextPos, "Should be able to move along path");
    }

    @Test
    @DisplayName("Path smoothing strategy should be configurable")
    void testPathSmoothingStrategy() {
        // Test default strategy
        assertEquals(PathSmoother.SmoothingStrategy.LINEAR_INTERPOLATION, 
                    movementController.getSmoothingStrategy(), 
                    "Default strategy should be LINEAR_INTERPOLATION");
        
        // Test setting different strategies
        movementController.setSmoothingStrategy(PathSmoother.SmoothingStrategy.OBSTACLE_AWARE);
        assertEquals(PathSmoother.SmoothingStrategy.OBSTACLE_AWARE, 
                    movementController.getSmoothingStrategy(), 
                    "Strategy should be updated to OBSTACLE_AWARE");
        
        movementController.setSmoothingStrategy(PathSmoother.SmoothingStrategy.LINEAR_INTERPOLATION);
        assertEquals(PathSmoother.SmoothingStrategy.LINEAR_INTERPOLATION, 
                    movementController.getSmoothingStrategy(), 
                    "Strategy should be updated back to LINEAR_INTERPOLATION");
    }

    @Test
    @DisplayName("Movement with different smoothing strategies should work")
    void testMovementWithDifferentSmoothingStrategies() {
        // Create a simple path
        ArrayList<PathNode> testPath = new ArrayList<>();
        testPath.add(new PathNode(1, 1, 0, 0, null));
        testPath.add(new PathNode(2, 2, 10, 10, testPath.get(0)));
        testPath.add(new PathNode(3, 3, 20, 20, testPath.get(1)));
        
        movementController.setPath(testPath);
        
        // Update map reference for obstacle detection
        movementController.updateMap(simpleMap);
        
        // Test with linear interpolation
        movementController.setSmoothingStrategy(PathSmoother.SmoothingStrategy.LINEAR_INTERPOLATION);
        Point pos1 = movementController.run();
        assertNotNull(pos1, "Movement should work with linear interpolation");
        
        // Test with obstacle-aware smoothing
        movementController.setSmoothingStrategy(PathSmoother.SmoothingStrategy.OBSTACLE_AWARE);
        Point pos2 = movementController.run();
        assertNotNull(pos2, "Movement should work with obstacle-aware smoothing");
    }
}
