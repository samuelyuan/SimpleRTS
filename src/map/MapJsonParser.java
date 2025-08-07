package map;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import graphics.Point;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class MapJsonParser {
    
    /**
     * Parse map data from JSON format
     * Expected JSON structure:
     * {
     *   "width": 30,
     *   "height": 32,
     *   "tiles": [
     *     ["Wall", "Wall", "Land", ...],
     *     ["Land", "Land", "Wall", ...],
     *     ...
     *   ]
     * }
     */
    public static MapParseResult parseMapDataFromJson(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            
            int width = json.getInt("width");
            int height = json.getInt("height");
            
            JSONArray tilesArray = json.getJSONArray("tiles");
            
            int[][] mapData = new int[height][width];
            String[][] drawData = new String[height][width];
            
            Map<Point, Integer> allyUnitPositions = new HashMap<>();
            Map<Point, Integer> enemyUnitPositions = new HashMap<>();
            Map<Point, Integer> flagPositions = new HashMap<>();
            
            // Parse tiles array
            for (int y = 0; y < height; y++) {
                JSONArray row = tilesArray.getJSONArray(y);
                for (int x = 0; x < width; x++) {
                    String tileStr = row.getString(x);
                    int tileId = parseTile(tileStr, x, y, allyUnitPositions, enemyUnitPositions, flagPositions);
                    mapData[y][x] = tileId;
                    drawData[y][x] = tileStr;
                }
            }
            
            return new MapParseResult(mapData, drawData, allyUnitPositions, enemyUnitPositions, flagPositions);
            
        } catch (JSONException e) {
            throw new RuntimeException("Failed to parse JSON map data: " + e.getMessage(), e);
        }
    }
    
    /**
     * Parse map data from JSON file
     */
    public static MapParseResult parseMapDataFromJsonFile(String filename) {
        List<String> fileContent = FileUtils.parseFile(filename);
        if (fileContent == null || fileContent.isEmpty()) {
            throw new RuntimeException("Failed to load JSON file: " + filename);
        }
        
        // Join all lines into a single JSON string
        StringBuilder jsonBuilder = new StringBuilder();
        for (String line : fileContent) {
            jsonBuilder.append(line);
        }
        
        return parseMapDataFromJson(jsonBuilder.toString());
    }
    
    /**
     * Convert map data to JSON format
     */
    public static String mapDataToJson(MapParseResult mapData) {
        try {
            JSONObject json = new JSONObject();
            json.put("width", mapData.mapData[0].length);
            json.put("height", mapData.mapData.length);
            
            JSONArray tilesArray = new JSONArray();
            for (int y = 0; y < mapData.mapData.length; y++) {
                JSONArray row = new JSONArray();
                for (int x = 0; x < mapData.mapData[0].length; x++) {
                    row.put(mapData.drawData[y][x]);
                }
                tilesArray.put(row);
            }
            json.put("tiles", tilesArray);
            
            return json.toString(2); // Pretty print with 2 spaces indentation
            
        } catch (JSONException e) {
            throw new RuntimeException("Failed to convert map data to JSON: " + e.getMessage(), e);
        }
    }
    
    // Text to JSON conversion methods removed - conversion already completed
    
    private static int parseTile(
        String tileStr,
        int x, int y,
        Map<Point, Integer> allyUnitPositions,
        Map<Point, Integer> enemyUnitPositions,
        Map<Point, Integer> flagPositions
    ) {
        // Reuse the existing tile parsing logic from MapParser
        return MapParser.tileStrToId(tileStr, x, y, allyUnitPositions, enemyUnitPositions, flagPositions);
    }
} 