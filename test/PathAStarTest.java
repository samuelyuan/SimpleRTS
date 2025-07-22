import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.awt.Point;
import java.util.ArrayList;

public class PathAStarTest {

    private int[][] map;

    @BeforeEach
    public void setUp() {
        // Simple map for testing
        map = new int[][]{
            {0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 0},
            {0, 1, 0, 0, 0, 1, 0},
            {0, 2, 0, 0, 0, 1, 0}, 
            {0, 1, 1, 1, 1, 1, 0},
            {0, 0, 1, 3, 0, 0, 0}
        };
    }

    @Test
    public void testGetMapTile() {
        // Test tile retrieval (should return correct value based on coordinates)
        assertEquals(0, PathAStar.getMapTile(map, 0, 0), "Tile at (0, 0) should be 0 (empty space)");
        assertEquals(1, PathAStar.getMapTile(map, 1, 1), "Tile at (1, 1) should be 1 (wall)");
        assertEquals(2, PathAStar.getMapTile(map, 1, 3), "Tile at (1, 2) should be 2 (start point)");
        assertEquals(3, PathAStar.getMapTile(map, 3, 5), "Tile at (3, 5) should be 3 (end point)");
    }

    @Test
    public void testIsValidLocation() {
        // Test if location is valid
        assertTrue(PathAStar.isValidLocation(map, 0, 0), "Location (0, 0) should be valid");
        assertTrue(PathAStar.isValidLocation(map, 6, 5), "Location (6, 5) should be valid");
        assertFalse(PathAStar.isValidLocation(map, -1, 0), "Location (-1, 0) should be invalid");
        assertFalse(PathAStar.isValidLocation(map, 7, 7), "Location (7, 7) should be invalid");
    }

    @Test
    public void testGetStartPoint() {
        // Test that start point is found correctly (should be (1, 2) in this map)
        Point start = PathAStar.getStartPoint(map);
        assertNotNull(start, "Start point should not be null");
        assertEquals(1, start.x, "Start point x-coordinate should be 1");
        assertEquals(3, start.y, "Start point y-coordinate should be 3");
    }

    @Test
    public void testGetEndPoint() {
        // Test that end point is found correctly (should be (3, 5) in this map)
        Point end = PathAStar.getEndPoint(map);
        assertNotNull(end, "End point should not be null");
        assertEquals(3, end.x, "End point x-coordinate should be 3");
        assertEquals(5, end.y, "End point y-coordinate should be 5");
    }

    @Test
    public void testGeneratePath() {
        // Test generating path between start (1, 2) and end (3, 5)
        Point start = PathAStar.getStartPoint(map);
        Point end = PathAStar.getEndPoint(map);

        ArrayList<MapNode> path = PathAStar.generatePath(map, start.x, start.y, end.x, end.y);

        // Test if the path is not null
        assertNotNull(path, "Path should not be null");

        // Test if the path starts at the start point and ends at the end point
        assertEquals(start.x, path.get(0).getX(), "Path should start at the start point");
        assertEquals(start.y, path.get(0).getY(), "Path should start at the start point");
        assertEquals(end.x, path.get(path.size() - 1).getX(), "Path should end at the end point");
        assertEquals(end.y, path.get(path.size() - 1).getY(), "Path should end at the end point");
    }

    @Test
    public void testNoPathAvailable() {
        // Modify map to block the path between start and end
        map[0][3] = 1; // Blocking the start path

        Point start = PathAStar.getStartPoint(map);
        Point end = PathAStar.getEndPoint(map);

        ArrayList<MapNode> path = PathAStar.generatePath(map, start.x, start.y, end.x, end.y);

        // Assert that no path is found
        assertNull(path, "No path should be found when the path is blocked");
    }
}
