import graphics.Point;
import java.util.ArrayList;

public class PathfindingTest {
    
    public static void main(String[] args) {
        testPathfindingPerformance();
        testPathSmoothing();
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
            ArrayList<MapNode> path = PathAStar.generatePath(map, start.x, start.y, end.x, end.y);
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
        ArrayList<MapNode> path = new ArrayList<>();
        path.add(new MapNode(1, 1, 0, 0, null));
        path.add(new MapNode(2, 1, 10, 10, path.get(0)));
        path.add(new MapNode(3, 1, 20, 20, path.get(1)));
        path.add(new MapNode(4, 1, 30, 30, path.get(2)));
        
        PathUnit pathUnit = new PathUnit(50, 50);
        pathUnit.setPath(path);
        
        // Test movement with smoothing
        for (int i = 0; i < 10; i++) {
            Point newPos = pathUnit.run();
            System.out.println("Position: (" + newPos.x + ", " + newPos.y + ")");
        }
    }
} 