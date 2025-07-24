import java.util.List;

import graphics.Point;

public class GameFogWar {
    private boolean[][] visibleData;

    public GameFogWar(int mapHeight, int mapWidth) {
        reset(mapHeight, mapWidth);
    }

    public boolean[][] getVisibleData() {
        return visibleData;
    }

    public boolean isTileVisible(int x, int y) {
        return visibleData[y][x];
    }

    public void calculateFogOfWar(List<GameUnit> playerList, int[][] mapdata) {
        for (GameUnit player : playerList) {
            Point location = player.getCurrentPoint();
            Point mapPoint = player.getMapPoint(location);
            int mapX = (int) mapPoint.getX();
            int mapY = (int) mapPoint.getY();

            int range = 5;

            for (int dy = -range; dy <= range; dy++) {
                if (mapY + dy < 0 || mapY + dy >= mapdata.length) {
                    continue;
                }

                for (int dx = -range; dx <= range; dx++) {
                    if (mapX + dx < 0 || mapX + dx >= mapdata[0].length) {
                        continue;
                    }

                    visibleData[mapY + dy][mapX + dx] = true;
                }
            }
        }
    }

    public void reset(int mapHeight, int mapWidth) {
        visibleData = new boolean[mapHeight][mapWidth];
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                visibleData[y][x] = false;
            }
        }
    }
}
