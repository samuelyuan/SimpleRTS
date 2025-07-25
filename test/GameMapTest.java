import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graphics.Point;
import map.MapParseResult;
import map.TileConverter;

public class GameMapTest {
    @Test
    public void testFormatMapDescriptionWrapsAndMatchesLevel() {
        String rawLine = "#2a This is a simple test to check line wrapping";
        ArrayList<String> result = GameMap.formatMapDescription(rawLine, 2, 20);

        assertNotNull(result);
        assertTrue(result.size() > 1); // should wrap
        assertTrue(result.get(0).startsWith("This")); // confirm it's formatting content
    }

    @Test
    public void testParseMapDataValidInput() {
        List<String> input = List.of(
                "2", "2",
                "Land", "Wall",
                "Flag", "Land");

        MapParseResult result = GameMap.parseMapData(input);

        assertEquals(2, result.mapData.length);
        assertEquals(2, result.mapData[0].length);
        assertEquals("Land", result.drawData[0][0]);
        assertEquals("Wall", result.drawData[0][1]);
        assertEquals("Flag", result.drawData[1][0]);
        assertEquals("Land", result.drawData[1][1]);
    }

    @Test
    public void testTileStrToId_Land() {
        Map<Point, Integer> ally = new HashMap<>();
        Map<Point, Integer> enemy = new HashMap<>();
        Map<Point, Integer> flags = new HashMap<>();

        int result = GameMap.tileStrToId("Land", 0, 0, ally, enemy, flags);

        assertEquals(0, result);
        assertTrue(ally.isEmpty());
        assertTrue(enemy.isEmpty());
        assertTrue(flags.isEmpty());
    }

    @Test
    public void testTileStrToId_Wall() {
        Map<Point, Integer> ally = new HashMap<>();
        Map<Point, Integer> enemy = new HashMap<>();
        Map<Point, Integer> flags = new HashMap<>();

        int result = GameMap.tileStrToId("Wall", 1, 1, ally, enemy, flags);

        assertEquals(1, result);
        assertTrue(ally.isEmpty());
        assertTrue(enemy.isEmpty());
        assertTrue(flags.isEmpty());
    }

    @Test
    public void testTileStrToId_AllyUnit() {
        Map<Point, Integer> ally = new HashMap<>();
        Map<Point, Integer> enemy = new HashMap<>();
        Map<Point, Integer> flags = new HashMap<>();

        int result = GameMap.tileStrToId("Unit +1 2", 2, 3, ally, enemy, flags);

        assertEquals(3, result); // 2 + 1
        Point p = new Point(2, 3);
        assertTrue(ally.containsKey(p));
        assertEquals(2, ally.get(p));
    }

    @Test
    public void testTileStrToId_EnemyUnit() {
        Map<Point, Integer> ally = new HashMap<>();
        Map<Point, Integer> enemy = new HashMap<>();
        Map<Point, Integer> flags = new HashMap<>();

        int result = GameMap.tileStrToId("Unit -1 1", 4, 4, ally, enemy, flags);

        assertEquals(5, result); // 1 + 4
        Point p = new Point(4, 4);
        assertTrue(enemy.containsKey(p));
        assertEquals(1, enemy.get(p));
    }

    @Test
    public void testTileStrToId_AllyFlag() {
        Map<Point, Integer> ally = new HashMap<>();
        Map<Point, Integer> enemy = new HashMap<>();
        Map<Point, Integer> flags = new HashMap<>();

        int result = GameMap.tileStrToId("Flag +1", 1, 1, ally, enemy, flags);

        assertEquals(TileConverter.TILE_FLAG_ALLY, result);
        Point p = new Point(1, 1);
        assertTrue(flags.containsKey(p));
        assertEquals(1, flags.get(p));
    }

    @Test
    public void testTileStrToId_EnemyFlag() {
        Map<Point, Integer> ally = new HashMap<>();
        Map<Point, Integer> enemy = new HashMap<>();
        Map<Point, Integer> flags = new HashMap<>();

        int result = GameMap.tileStrToId("Flag -1", 0, 2, ally, enemy, flags);

        assertEquals(TileConverter.TILE_FLAG_ENEMY, result);
        Point p = new Point(0, 2);
        assertTrue(flags.containsKey(p));
        assertEquals(-1, flags.get(p));
    }

    @Test
    public void testTileStrToId_UnknownTypeReturnsDefault() {
        Map<Point, Integer> ally = new HashMap<>();
        Map<Point, Integer> enemy = new HashMap<>();
        Map<Point, Integer> flags = new HashMap<>();

        int result = GameMap.tileStrToId("UnknownType", 5, 5, ally, enemy, flags);

        assertEquals(0, result); // default to 0
        assertTrue(ally.isEmpty());
        assertTrue(enemy.isEmpty());
        assertTrue(flags.isEmpty());
    }

    @Test
    public void testGenerateMapTileStrings() {
        // Set up a simple map for testing
        int[][] mapdata = new int[][] {
            {0, 1, 8},
            {5, 6, 9}
        };
        String[][] tileStrings = GameMap.generateMapTileStrings(mapdata);
        assertEquals(2, tileStrings.length);
        assertEquals(3, tileStrings[0].length);

        // Adjust these expected values if your TileConverter returns different strings
        assertEquals(TileConverter.STR_LAND, tileStrings[0][0]);
        assertEquals(TileConverter.STR_WALL, tileStrings[0][1]);
        assertEquals(TileConverter.STR_FLAG, tileStrings[0][2]);
        assertEquals(TileConverter.STR_UNIT_LIGHT_ENEMY, tileStrings[1][0]);
        assertEquals(TileConverter.STR_UNIT_MEDIUM, tileStrings[1][1]);
        assertEquals(TileConverter.STR_FLAG, tileStrings[1][2]);
    }
}
