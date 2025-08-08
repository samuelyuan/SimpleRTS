package pathfinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import graphics.Point;
import map.TileConverter;
import map.MapValidator;

public class PathAStarTest {

    private int[][] map;

    @BeforeEach
    public void setUp() {
        // Simple map for testing - no start/end tiles, just walls and empty space
        map = new int[][]{
            {0, 0, 0, 0, 0, 0, 0},
            {0, TileConverter.TILE_WALL, TileConverter.TILE_WALL, TileConverter.TILE_WALL, TileConverter.TILE_WALL, TileConverter.TILE_WALL, 0},
            {0, TileConverter.TILE_WALL, 0, 0, 0, TileConverter.TILE_WALL, 0},
            {0, 0, 0, 0, 0, TileConverter.TILE_WALL, 0}, 
            {0, TileConverter.TILE_WALL, TileConverter.TILE_WALL, TileConverter.TILE_WALL, TileConverter.TILE_WALL, TileConverter.TILE_WALL, 0},
            {0, 0, TileConverter.TILE_WALL, 0, 0, 0, 0}
        };
    }

    @Test
    public void testGetMapTile() {
        // Test tile retrieval (should return correct value based on coordinates)
        assertEquals(0, MapValidator.getTileSafely(map, 0, 0), "Tile at (0, 0) should be 0 (empty space)");
        assertEquals(TileConverter.TILE_WALL, MapValidator.getTileSafely(map, 1, 1), "Tile at (1, 1) should be wall");
        assertEquals(0, MapValidator.getTileSafely(map, 1, 3), "Tile at (1, 3) should be 0 (empty space)");
        assertEquals(0, MapValidator.getTileSafely(map, 3, 5), "Tile at (3, 5) should be 0 (empty space)");
    }

    @Test
    public void testIsValidLocation() {
        // Test if location is valid
        assertTrue(MapValidator.isValidLocation(map, 0, 0), "Location (0, 0) should be valid");
        assertTrue(MapValidator.isValidLocation(map, 6, 5), "Location (6, 5) should be valid");
        assertFalse(MapValidator.isValidLocation(map, -1, 0), "Location (-1, 0) should be invalid");
        assertFalse(MapValidator.isValidLocation(map, 7, 7), "Location (7, 7) should be invalid");
    }



    @Test
    public void testGeneratePath() {
        // Test generating path between hardcoded start (1, 3) and end (3, 5)
        int startX = 1, startY = 3;
        int endX = 3, endY = 5;

        ArrayList<PathNode> path = PathAStar.generatePath(map, startX, startY, endX, endY);

        // Test if the path is not null
        assertNotNull(path, "Path should not be null");

        // Test if the path starts at the start point and ends at the end point
        assertEquals(startX, path.get(0).getX(), "Path should start at the start point");
        assertEquals(startY, path.get(0).getY(), "Path should start at the start point");
        assertEquals(endX, path.get(path.size() - 1).getX(), "Path should end at the end point");
        assertEquals(endY, path.get(path.size() - 1).getY(), "Path should end at the end point");
    }

    @Test
    public void testNoPathAvailable() {
        // Modify map to block the path between start and end
        map[0][3] = TileConverter.TILE_WALL; // Blocking the path

        int startX = 1, startY = 3;
        int endX = 3, endY = 5;

        ArrayList<PathNode> path = PathAStar.generatePath(map, startX, startY, endX, endY);

        // Assert that no path is found
        assertNull(path, "No path should be found when the path is blocked");
    }
}
