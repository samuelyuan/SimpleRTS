package map;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import graphics.Point;

public class MapParser {
    public static MapParseResult parseMapData(List<String> data) {
        int height = parseMapHeight(data);
        int width = parseMapWidth(data);

        int[][] mapdata = new int[height][width];
        String[][] drawData = new String[height][width];

        Map<Point, Integer> allyUnitPositions = new HashMap<>();
        Map<Point, Integer> enemyUnitPositions = new HashMap<>();
        Map<Point, Integer> flagPositions = new HashMap<>();

        populateMapArrays(data, mapdata, drawData, allyUnitPositions, enemyUnitPositions, flagPositions);

        return new MapParseResult(mapdata, drawData, allyUnitPositions, enemyUnitPositions, flagPositions);
    }

    private static int parseMapHeight(List<String> data) {
        return Integer.parseInt(data.get(0));
    }

    private static int parseMapWidth(List<String> data) {
        return Integer.parseInt(data.get(1));
    }

    private static int parseTile(
        String tileStr,
        int x, int y,
        Map<Point, Integer> allyUnitPositions,
        Map<Point, Integer> enemyUnitPositions,
        Map<Point, Integer> flagPositions
    ) {
        return tileStrToId(tileStr, x, y, allyUnitPositions, enemyUnitPositions, flagPositions);
    }

    private static void populateMapArrays(
        List<String> data,
        int[][] mapdata,
        String[][] drawData,
        Map<Point, Integer> allyUnitPositions,
        Map<Point, Integer> enemyUnitPositions,
        Map<Point, Integer> flagPositions
    ) {
        int height = mapdata.length;
        int width = mapdata[0].length;
        int tileCounter = 2;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String tileStr = data.get(tileCounter);
                int tileId = parseTile(tileStr, x, y, allyUnitPositions, enemyUnitPositions, flagPositions);
                mapdata[y][x] = tileId;
                drawData[y][x] = tileStr;
                tileCounter++;
            }
        }
    }

    public static int tileStrToId(
        String tileStr,
        int x, int y,
        Map<Point, Integer> allyUnitPositions,
        Map<Point, Integer> enemyUnitPositions,
        Map<Point, Integer> flagPositions
    ) {
        if (tileStr.startsWith("Land")) {
            return parseLandTile(tileStr);
        }
        if (tileStr.startsWith("Wall")) {
            return parseWallTile(tileStr);
        }
        if (tileStr.startsWith("Unit")) {
            return parseUnitTile(tileStr, x, y, allyUnitPositions, enemyUnitPositions);
        }
        if (tileStr.startsWith("Flag")) {
            return parseFlagTile(tileStr, x, y, flagPositions);
        }
        // fallback
        return 0;
    }

    private static int parseLandTile(String tileStr) {
        return TileConverter.tileStrToBaseId("Land");
    }

    private static int parseWallTile(String tileStr) {
        return TileConverter.tileStrToBaseId("Wall");
    }

    private static int parseUnitTile(
        String tileStr, int x, int y,
        Map<Point, Integer> allyUnitPositions,
        Map<Point, Integer> enemyUnitPositions
    ) {
        Point pos = new Point(x, y);
        String unitInfo = tileStr.substring("Unit ".length()); // e.g., "+1 2" or "-1 1"
        String[] parts = unitInfo.trim().split(" ");
        if (parts.length == 2) {
            String faction = parts[0];
            int type = Integer.parseInt(parts[1]);
            if (faction.equals("+1")) {
                allyUnitPositions.put(pos, type);
                return type + 1;
            } else if (faction.equals("-1")) {
                enemyUnitPositions.put(pos, type);
                return type + 4;
            }
        }
        return 0;
    }

    private static int parseFlagTile(
        String tileStr, int x, int y,
        Map<Point, Integer> flagPositions
    ) {
        Point pos = new Point(x, y);
        if (tileStr.contains("+1")) {
            flagPositions.put(pos, 1);
            return TileConverter.TILE_FLAG_ALLY;
        } else if (tileStr.contains("-1")) {
            flagPositions.put(pos, -1);
            return TileConverter.TILE_FLAG_ENEMY;
        }
        return 0;
    }
} 