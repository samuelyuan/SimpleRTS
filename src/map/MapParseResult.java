package map;

import java.util.Map;

import graphics.Point;

public class MapParseResult {
    public final int[][] mapData;
    public final String[][] drawData;
    public final Map<Point, Integer> allyUnitPositions;
    public final Map<Point, Integer> enemyUnitPositions;
    public final Map<Point, Integer> flagPositions;

    public MapParseResult(
        int[][] mapData,
        String[][] drawData,
        Map<Point, Integer> allyUnitPositions,
        Map<Point, Integer> enemyUnitPositions,
        Map<Point, Integer> flagPositions
    ) {
        this.mapData = mapData;
        this.drawData = drawData;
        this.allyUnitPositions = allyUnitPositions;
        this.enemyUnitPositions = enemyUnitPositions;
        this.flagPositions = flagPositions;
    }
}
