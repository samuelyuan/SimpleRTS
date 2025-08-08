package map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MapValidatorTest {
    
    private int[][] testMap;
    
    @BeforeEach
    void setUp() {
        // Create a 5x5 test map
        testMap = new int[][] {
            {0, 0, 1, 0, 0},  // Row 0: empty, empty, wall, empty, empty
            {0, 1, 1, 1, 0},  // Row 1: empty, wall, wall, wall, empty
            {0, 0, 0, 0, 0},  // Row 2: all empty
            {1, 1, 0, 1, 1},  // Row 3: wall, wall, empty, wall, wall
            {0, 0, 0, 0, 0}   // Row 4: all empty
        };
    }
    
    @Test
    void testIsValidLocation_ValidCoordinates() {
        assertTrue(MapValidator.isValidLocation(testMap, 0, 0), "Location (0, 0) should be valid");
        assertTrue(MapValidator.isValidLocation(testMap, 2, 2), "Location (2, 2) should be valid");
        assertTrue(MapValidator.isValidLocation(testMap, 4, 4), "Location (4, 4) should be valid");
    }
    
    @Test
    void testIsValidLocation_InvalidCoordinates() {
        assertFalse(MapValidator.isValidLocation(testMap, -1, 0), "Location (-1, 0) should be invalid");
        assertFalse(MapValidator.isValidLocation(testMap, 0, -1), "Location (0, -1) should be invalid");
        assertFalse(MapValidator.isValidLocation(testMap, 5, 0), "Location (5, 0) should be invalid");
        assertFalse(MapValidator.isValidLocation(testMap, 0, 5), "Location (0, 5) should be invalid");
        assertFalse(MapValidator.isValidLocation(testMap, 10, 10), "Location (10, 10) should be invalid");
    }
    
    @Test
    void testIsWalkable_ValidWalkableTiles() {
        assertTrue(MapValidator.isWalkable(testMap, 0, 0), "Tile (0, 0) should be walkable");
        assertTrue(MapValidator.isWalkable(testMap, 2, 2), "Tile (2, 2) should be walkable");
        assertTrue(MapValidator.isWalkable(testMap, 4, 4), "Tile (4, 4) should be walkable");
    }
    
    @Test
    void testIsWalkable_WallTiles() {
        assertFalse(MapValidator.isWalkable(testMap, 2, 0), "Tile (2, 0) should not be walkable (wall)");
        assertFalse(MapValidator.isWalkable(testMap, 1, 1), "Tile (1, 1) should not be walkable (wall)");
        assertFalse(MapValidator.isWalkable(testMap, 0, 3), "Tile (0, 3) should not be walkable (wall)");
    }
    
    @Test
    void testIsWalkable_OutOfBounds() {
        assertFalse(MapValidator.isWalkable(testMap, -1, 0), "Out of bounds should not be walkable");
        assertFalse(MapValidator.isWalkable(testMap, 5, 0), "Out of bounds should not be walkable");
        assertFalse(MapValidator.isWalkable(testMap, 0, 5), "Out of bounds should not be walkable");
    }
    
    @Test
    void testIsWall_ValidWallTiles() {
        assertTrue(MapValidator.isWall(testMap, 2, 0), "Tile (2, 0) should be a wall");
        assertTrue(MapValidator.isWall(testMap, 1, 1), "Tile (1, 1) should be a wall");
        assertTrue(MapValidator.isWall(testMap, 0, 3), "Tile (0, 3) should be a wall");
    }
    
    @Test
    void testIsWall_NonWallTiles() {
        assertFalse(MapValidator.isWall(testMap, 0, 0), "Tile (0, 0) should not be a wall");
        assertFalse(MapValidator.isWall(testMap, 2, 2), "Tile (2, 2) should not be a wall");
        assertFalse(MapValidator.isWall(testMap, 4, 4), "Tile (4, 4) should not be a wall");
    }
    
    @Test
    void testIsWall_OutOfBounds() {
        assertFalse(MapValidator.isWall(testMap, -1, 0), "Out of bounds should not be a wall");
        assertFalse(MapValidator.isWall(testMap, 5, 0), "Out of bounds should not be a wall");
    }
    
    @Test
    void testGetTileSafely_ValidCoordinates() {
        assertEquals(0, MapValidator.getTileSafely(testMap, 0, 0), "Should return 0 for empty tile");
        assertEquals(TileConverter.TILE_WALL, MapValidator.getTileSafely(testMap, 2, 0), "Should return wall value");
        assertEquals(0, MapValidator.getTileSafely(testMap, 2, 2), "Should return 0 for empty tile");
    }
    
    @Test
    void testGetTileSafely_OutOfBounds() {
        assertEquals(-1, MapValidator.getTileSafely(testMap, -1, 0), "Should return -1 for out of bounds");
        assertEquals(-1, MapValidator.getTileSafely(testMap, 5, 0), "Should return -1 for out of bounds");
        assertEquals(-1, MapValidator.getTileSafely(testMap, 0, 5), "Should return -1 for out of bounds");
    }
    
    @Test
    void testIsTileType_ValidMatches() {
        assertTrue(MapValidator.isTileType(testMap, 0, 0, 0), "Should match empty tile type");
        assertTrue(MapValidator.isTileType(testMap, 2, 0, TileConverter.TILE_WALL), "Should match wall tile type");
    }
    
    @Test
    void testIsTileType_NoMatches() {
        assertFalse(MapValidator.isTileType(testMap, 0, 0, TileConverter.TILE_WALL), "Should not match wall type for empty tile");
        assertFalse(MapValidator.isTileType(testMap, 2, 0, 0), "Should not match empty type for wall tile");
    }
    
    @Test
    void testIsTileType_OutOfBounds() {
        assertFalse(MapValidator.isTileType(testMap, -1, 0, 0), "Out of bounds should not match any type");
        assertFalse(MapValidator.isTileType(testMap, 5, 0, TileConverter.TILE_WALL), "Out of bounds should not match any type");
    }
}
