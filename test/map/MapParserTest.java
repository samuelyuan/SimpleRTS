package map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import graphics.Point;

public class MapParserTest {
    @Test
    public void testParseMapData_basic() {
        List<String> data = Arrays.asList(
            "2", // height
            "2", // width
            "Land", "Wall",
            "Unit +1 1", "Flag -1"
        );
        MapParseResult result = MapParser.parseMapData(data);
        assertEquals(2, result.mapData.length);
        assertEquals(2, result.mapData[0].length);
        // Check tile types
        assertEquals(MapParser.tileStrToId("Land", 0, 0, new HashMap<>(), new HashMap<>(), new HashMap<>()), result.mapData[0][0]);
        assertEquals(MapParser.tileStrToId("Wall", 1, 0, new HashMap<>(), new HashMap<>(), new HashMap<>()), result.mapData[0][1]);
        // Check unit and flag positions
        assertTrue(result.allyUnitPositions.containsKey(new Point(0,1)));
        assertTrue(result.flagPositions.containsKey(new Point(1,1)));
    }

    @Test
    public void testTileStrToId_unitAndFlag() {
        Map<Point, Integer> allyUnits = new HashMap<>();
        Map<Point, Integer> enemyUnits = new HashMap<>();
        Map<Point, Integer> flags = new HashMap<>();
        int id = MapParser.tileStrToId("Unit +1 2", 3, 4, allyUnits, enemyUnits, flags);
        assertTrue(allyUnits.containsKey(new Point(3,4)));
        id = MapParser.tileStrToId("Unit -1 1", 1, 2, allyUnits, enemyUnits, flags);
        assertTrue(enemyUnits.containsKey(new Point(1,2)));
        id = MapParser.tileStrToId("Flag +1", 5, 6, allyUnits, enemyUnits, flags);
        assertTrue(flags.containsKey(new Point(5,6)));
    }
} 