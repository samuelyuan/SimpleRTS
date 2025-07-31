import java.util.List;

import graphics.Point;

public class GameFogWar {
    private boolean[][] visibleData;
    private boolean[][] visitedData; // Track areas that have been visited/explored

    public GameFogWar(int mapHeight, int mapWidth) {
        reset(mapHeight, mapWidth);
    }

    public boolean[][] getVisibleData() {
        return visibleData;
    }

    public boolean[][] getVisitedData() {
        return visitedData;
    }

    public boolean isTileVisible(int x, int y) {
        return visibleData[y][x];
    }

    public boolean isTileVisited(int x, int y) {
        return visitedData[y][x];
    }

    public void calculateFogOfWar(List<GameUnit> playerList, int[][] mapdata) {
        // Clear current visibility
        for (int y = 0; y < visibleData.length; y++) {
            for (int x = 0; x < visibleData[y].length; x++) {
                visibleData[y][x] = false;
            }
        }

        for (GameUnit player : playerList) {
            Point location = player.getCurrentPosition();
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

                    int targetX = mapX + dx;
                    int targetY = mapY + dy;
                    
                    // Mark as currently visible
                    visibleData[targetY][targetX] = true;
                    
                    // Mark as visited (explored)
                    visitedData[targetY][targetX] = true;
                }
            }
        }
    }

    public void reset(int mapHeight, int mapWidth) {
        visibleData = new boolean[mapHeight][mapWidth];
        visitedData = new boolean[mapHeight][mapWidth];
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                visibleData[y][x] = false;
                visitedData[y][x] = false;
            }
        }
    }
}
