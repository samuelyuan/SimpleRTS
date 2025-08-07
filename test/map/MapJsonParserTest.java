package map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.Map;
import graphics.Point;

public class MapJsonParserTest {
    
    private String sampleJson;
    
    @BeforeEach
    void setUp() {
        sampleJson = """
            {
              "width": 3,
              "height": 2,
              "tiles": [
                ["Wall", "Land", "Wall"],
                ["Land", "Unit +1 1", "Land"]
              ]
            }
            """;
    }
    
    @Test
    void testParseMapDataFromJson() {
        MapParseResult result = MapJsonParser.parseMapDataFromJson(sampleJson);
        
        assertNotNull(result);
        assertEquals(2, result.mapData.length); // height
        assertEquals(3, result.mapData[0].length); // width
        
        // Check tile data
        assertEquals(1, result.mapData[0][0]); // Wall
        assertEquals(0, result.mapData[0][1]); // Land
        assertEquals(1, result.mapData[0][2]); // Wall
        assertEquals(0, result.mapData[1][0]); // Land
        assertEquals(2, result.mapData[1][1]); // Unit +1 1
        assertEquals(0, result.mapData[1][2]); // Land
        
        // Check draw data
        assertEquals("Wall", result.drawData[0][0]);
        assertEquals("Land", result.drawData[0][1]);
        assertEquals("Wall", result.drawData[0][2]);
        assertEquals("Land", result.drawData[1][0]);
        assertEquals("Unit +1 1", result.drawData[1][1]);
        assertEquals("Land", result.drawData[1][2]);
        
        // Check unit positions
        assertEquals(1, result.allyUnitPositions.size());
        assertTrue(result.allyUnitPositions.containsKey(new Point(1, 1)));
        assertEquals(1, result.allyUnitPositions.get(new Point(1, 1)));
    }
    
    @Test
    void testMapDataToJson() {
        // Create a simple map result
        int[][] mapData = {{1, 0}, {0, 2}};
        String[][] drawData = {{"Wall", "Land"}, {"Land", "Unit +1 1"}};
        Map<Point, Integer> allyUnits = Map.of(new Point(1, 1), 1);
        Map<Point, Integer> enemyUnits = Map.of();
        Map<Point, Integer> flags = Map.of();
        
        MapParseResult mapResult = new MapParseResult(mapData, drawData, allyUnits, enemyUnits, flags);
        String jsonString = MapJsonParser.mapDataToJson(mapResult);
        
        assertNotNull(jsonString);
        assertTrue(jsonString.contains("\"width\": 2"));
        assertTrue(jsonString.contains("\"height\": 2"));
        assertTrue(jsonString.contains("\"tiles\""));
        
        // Parse it back to verify
        MapParseResult parsed = MapJsonParser.parseMapDataFromJson(jsonString);
        assertEquals(2, parsed.mapData.length);
        assertEquals(2, parsed.mapData[0].length);
    }
    
    @Test
    void testInvalidJson() {
        String invalidJson = "{ invalid json }";
        assertThrows(RuntimeException.class, () -> {
            MapJsonParser.parseMapDataFromJson(invalidJson);
        });
    }
    
    @Test
    void testMissingRequiredFields() {
        String incompleteJson = "{\"width\": 3}";
        assertThrows(RuntimeException.class, () -> {
            MapJsonParser.parseMapDataFromJson(incompleteJson);
        });
    }
} 