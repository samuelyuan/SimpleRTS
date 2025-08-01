package pathfinding;
import graphics.Point;

import java.util.ArrayList;

/**
 * Integration tests for pathfinding system.
 * Tests the interaction between PathAStar, PathUnit, and PathNode components.
 */
public class PathfindingTest {
    
    public static void main(String[] args) {
        testPathfindingPerformance();
        testPathSmoothing();
        testIntegrationScenarios();
    }
    
    public static void testPathfindingPerformance() {
        System.out.println("Testing pathfinding performance...");
        
        // Create a simple test map
        int[][] map = {
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0}
        };
        
        Point start = new Point(1, 3);
        Point end = new Point(5, 3);
        
        // Test multiple pathfinding calls to measure performance
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            ArrayList<PathNode> path = PathAStar.generatePath(map, start.x, start.y, end.x, end.y);
            if (path == null) {
                System.out.println("Path not found!");
                return;
            }
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("100 pathfinding calls took: " + (endTime - startTime) + "ms");
        
        // Test path caching
        PathUnit pathUnit = new PathUnit(50, 150);
        startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            pathUnit.findPath(map, start, end);
        }
        
        endTime = System.currentTimeMillis();
        System.out.println("100 cached pathfinding calls took: " + (endTime - startTime) + "ms");
    }
    
    public static void testPathSmoothing() {
        System.out.println("\nTesting path smoothing...");
        
        // Create a simple path
        ArrayList<PathNode> path = new ArrayList<>();
        path.add(new PathNode(1, 1, 0, 0, null));
        path.add(new PathNode(2, 1, 10, 10, path.get(0)));
        path.add(new PathNode(3, 1, 20, 20, path.get(1)));
        path.add(new PathNode(4, 1, 30, 30, path.get(2)));
        
        PathUnit pathUnit = new PathUnit(50, 50);
        pathUnit.setPath(path);
        
        // Test movement with smoothing
        for (int i = 0; i < 10; i++) {
            Point newPos = pathUnit.run();
            System.out.println("Position: (" + newPos.x + ", " + newPos.y + ")");
        }
    }
    
    public static void testIntegrationScenarios() {
        System.out.println("\nTesting integration scenarios...");
        
        // Test complex map scenario
        int[][] complexMap = {
            {0, 0, 1, 0, 0, 0, 0},
            {0, 1, 1, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0},
            {0, 1, 1, 1, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0}
        };
        
        Point start = new Point(0, 0);
        Point end = new Point(6, 5);
        
        PathUnit pathUnit = new PathUnit(0, 0);
        boolean pathFound = pathUnit.findPath(complexMap, start, end);
        
        if (pathFound) {
            System.out.println("Complex path found successfully");
            
            // Test movement along the path
            int steps = 0;
            while (pathUnit.getIsPathCreated() && steps < 20) {
                Point pos = pathUnit.run();
                if (pos != null) {
                    System.out.println("Step " + steps + ": (" + pos.x + ", " + pos.y + ")");
                }
                steps++;
            }
        } else {
            System.out.println("Complex path not found");
        }
        
        // Test dynamic obstacle scenario
        System.out.println("\nTesting dynamic obstacle scenario...");
        int[][] dynamicMap = {
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
        };
        
        PathUnit dynamicPathUnit = new PathUnit(0, 0);
        dynamicPathUnit.findPath(dynamicMap, new Point(0, 0), new Point(4, 4));
        
        // Add obstacle during movement
        dynamicMap[2][2] = 1;
        
        Point newDest = dynamicPathUnit.recalculateDest(dynamicMap, new Point(4, 4));
        if (newDest != null) {
            System.out.println("Recalculated destination: (" + newDest.x + ", " + newDest.y + ")");
        } else {
            System.out.println("No alternative destination found");
        }
    }
} 