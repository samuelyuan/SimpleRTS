package pathfinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for PathSmoother class.
 * Tests path smoothing functionality, different strategies, and edge cases.
 */
@DisplayName("PathSmoother Tests")
public class PathSmootherTest {

    private int[][] simpleMap;
    private int[][] obstacleMap;
    private PathNode node1, node2, node3;

    @BeforeEach
    void setUp() {
        // Simple 5x5 map with clear path
        simpleMap = new int[][] {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
        };
        
        // Map with obstacles
        obstacleMap = new int[][] {
            {0, 0, 1, 0, 0},
            {0, 1, 1, 1, 0},
            {0, 0, 0, 0, 0},
            {0, 1, 1, 1, 0},
            {0, 0, 0, 0, 0}
        };
        
        // Create test path nodes
        node1 = new PathNode(1, 1, 0, 0, null);
        node2 = new PathNode(2, 2, 10, 10, node1);
        node3 = new PathNode(3, 3, 20, 20, node2);
    }

    @Test
    @DisplayName("Constructor should initialize PathSmoother correctly")
    void testPathSmootherInitialization() {
        // Test that the class can be instantiated and default strategy is available
        PathSmoother.SmoothingStrategy defaultStrategy = PathSmoother.getDefaultStrategy();
        assertNotNull(defaultStrategy, "Default strategy should not be null");
        assertEquals(PathSmoother.SmoothingStrategy.LINEAR_INTERPOLATION, defaultStrategy, 
                    "Default strategy should be LINEAR_INTERPOLATION");
    }

    @Test
    @DisplayName("calculateTargetPosition should work with linear interpolation")
    void testCalculateTargetPositionLinearInterpolation() {
        PathSmoother.SmoothingResult result = PathSmoother.calculateTargetPosition(
            node1, node2, PathSmoother.SmoothingStrategy.LINEAR_INTERPOLATION, simpleMap);
        
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isValid, "Result should be valid");
        assertTrue(result.targetX > 0, "Target X should be positive");
        assertTrue(result.targetY > 0, "Target Y should be positive");
    }

    @Test
    @DisplayName("calculateTargetPosition should work with null nextNode")
    void testCalculateTargetPositionNullNextNode() {
        PathSmoother.SmoothingResult result = PathSmoother.calculateTargetPosition(
            node1, null, PathSmoother.SmoothingStrategy.LINEAR_INTERPOLATION, simpleMap);
        
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isValid, "Result should be valid");
        assertTrue(result.targetX > 0, "Target X should be positive");
        assertTrue(result.targetY > 0, "Target Y should be positive");
    }

    @Test
    @DisplayName("calculateTargetPosition should work with obstacle-aware smoothing")
    void testCalculateTargetPositionObstacleAware() {
        PathSmoother.SmoothingResult result = PathSmoother.calculateTargetPosition(
            node1, node2, PathSmoother.SmoothingStrategy.OBSTACLE_AWARE, obstacleMap);
        
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isValid, "Result should be valid");
    }

    @Test
    @DisplayName("calculateCoordinates should update provided array")
    void testCalculateCoordinates() {
        double[] coords = new double[2];
        PathSmoother.calculateCoordinates(node1, coords);
        
        assertTrue(coords[0] > 0, "X coordinate should be positive");
        assertTrue(coords[1] > 0, "Y coordinate should be positive");
    }

    @Test
    @DisplayName("Different smoothing strategies should produce different results")
    void testDifferentSmoothingStrategies() {
        PathSmoother.SmoothingResult linearResult = PathSmoother.calculateTargetPosition(
            node1, node2, PathSmoother.SmoothingStrategy.LINEAR_INTERPOLATION, simpleMap);
        
        PathSmoother.SmoothingResult obstacleResult = PathSmoother.calculateTargetPosition(
            node1, node2, PathSmoother.SmoothingStrategy.OBSTACLE_AWARE, simpleMap);
        
        // Results should be different
        assertNotNull(linearResult, "Linear result should not be null");
        assertNotNull(obstacleResult, "Obstacle-aware result should not be null");
        assertTrue(linearResult.isValid, "Linear result should be valid");
        assertTrue(obstacleResult.isValid, "Obstacle-aware result should be valid");
    }

    @Test
    @DisplayName("SmoothingResult should have correct properties")
    void testSmoothingResultProperties() {
        double testX = 100.0;
        double testY = 200.0;
        boolean testValid = true;
        
        PathSmoother.SmoothingResult result = new PathSmoother.SmoothingResult(testX, testY, testValid);
        
        assertEquals(testX, result.targetX, "Target X should match input");
        assertEquals(testY, result.targetY, "Target Y should match input");
        assertEquals(testValid, result.isValid, "Valid flag should match input");
    }

    @Test
    @DisplayName("SmoothingStrategy enum should have all expected values")
    void testSmoothingStrategyEnum() {
        PathSmoother.SmoothingStrategy[] strategies = PathSmoother.SmoothingStrategy.values();
        
        assertEquals(2, strategies.length, "Should have 2 smoothing strategies");
        
        // Check that all expected strategies exist
        boolean hasLinear = false, hasObstacleAware = false;
        
        for (PathSmoother.SmoothingStrategy strategy : strategies) {
            switch (strategy) {
                case LINEAR_INTERPOLATION:
                    hasLinear = true;
                    break;
                case OBSTACLE_AWARE:
                    hasObstacleAware = true;
                    break;
            }
        }
        
        assertTrue(hasLinear, "Should have LINEAR_INTERPOLATION strategy");
        assertTrue(hasObstacleAware, "Should have OBSTACLE_AWARE strategy");
    }

    @Test
    @DisplayName("Edge case: null map should not cause errors")
    void testNullMap() {
        PathSmoother.SmoothingResult result = PathSmoother.calculateTargetPosition(
            node1, node2, PathSmoother.SmoothingStrategy.LINEAR_INTERPOLATION, null);
        
        assertNotNull(result, "Result should not be null even with null map");
        assertTrue(result.isValid, "Result should be valid");
    }

    @Test
    @DisplayName("Edge case: empty map should not cause errors")
    void testEmptyMap() {
        int[][] emptyMap = new int[0][0];
        PathSmoother.SmoothingResult result = PathSmoother.calculateTargetPosition(
            node1, node2, PathSmoother.SmoothingStrategy.LINEAR_INTERPOLATION, emptyMap);
        
        assertNotNull(result, "Result should not be null even with empty map");
        assertTrue(result.isValid, "Result should be valid");
    }
}
