import graphics.Point;

public class TileCoordinateConverterTest {
    
    public static void main(String[] args) {
        testBasicConversions();
        testCameraConversions();
        testDistanceCalculations();
        System.out.println("All coordinate utility tests passed!");
    }
    
    public static void testBasicConversions() {
        System.out.println("Testing basic coordinate conversions...");
        
        // Test screen to map conversion
        Point screenPoint = new Point(150, 200);
        Point mapPoint = TileCoordinateConverter.screenToMap(screenPoint);
        assert mapPoint.x == 3 : "Expected map X to be 3, got " + mapPoint.x;
        assert mapPoint.y == 4 : "Expected map Y to be 4, got " + mapPoint.y;
        
        // Test map to screen conversion
        Point backToScreen = TileCoordinateConverter.mapToScreen(mapPoint);
        assert backToScreen.x == 150 : "Expected screen X to be 150, got " + backToScreen.x;
        assert backToScreen.y == 200 : "Expected screen Y to be 200, got " + backToScreen.y;
        
        // Test individual coordinate conversion
        Point individual = TileCoordinateConverter.mapToScreen(5, 6);
        assert individual.x == 250 : "Expected screen X to be 250, got " + individual.x;
        assert individual.y == 300 : "Expected screen Y to be 300, got " + individual.y;
        
        System.out.println("✓ Basic conversions work correctly");
    }
    
    public static void testCameraConversions() {
        System.out.println("Testing camera offset conversions...");
        
        Point screenPoint = new Point(100, 150);
        int cameraX = 200;
        int cameraY = 300;
        
        Point mapPoint = TileCoordinateConverter.screenToMapWithCamera(screenPoint, cameraX, cameraY);
        assert mapPoint.x == 6 : "Expected map X to be 6, got " + mapPoint.x;
        assert mapPoint.y == 9 : "Expected map Y to be 9, got " + mapPoint.y;
        
        Point backToScreen = TileCoordinateConverter.mapToScreenWithCamera(mapPoint, cameraX, cameraY);
        assert backToScreen.x == 100 : "Expected screen X to be 100, got " + backToScreen.x;
        assert backToScreen.y == 150 : "Expected screen Y to be 150, got " + backToScreen.y;
        
        System.out.println("✓ Camera conversions work correctly");
    }
    
    public static void testDistanceCalculations() {
        System.out.println("Testing distance calculations...");
        
        Point mapPoint1 = new Point(1, 1);
        Point mapPoint2 = new Point(3, 4);
        
        int manhattanDist = TileCoordinateConverter.manhattanDistance(mapPoint1, mapPoint2);
        assert manhattanDist == 5 : "Expected Manhattan distance to be 5, got " + manhattanDist;
        
        Point screenPoint1 = new Point(50, 50);  // Map (1, 1)
        Point screenPoint2 = new Point(150, 200); // Map (3, 4)
        
        int screenManhattanDist = TileCoordinateConverter.manhattanDistanceInTiles(screenPoint1, screenPoint2);
        assert screenManhattanDist == 5 : "Expected screen Manhattan distance to be 5, got " + screenManhattanDist;
        
        System.out.println("✓ Distance calculations work correctly");
    }
} 