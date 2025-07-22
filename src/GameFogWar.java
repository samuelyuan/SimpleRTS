import java.awt.Point;

public class GameFogWar {
	private static boolean[][] visibleData;

	public static void init(int mapHeight, int mapWidth) {
		visibleData = new boolean[mapHeight][mapWidth];
		for (int y = 0; y < mapHeight; y++) {
			for (int x = 0; x < mapWidth; x++) {
				visibleData[y][x] = false;
			}
		}
	}

	public static boolean IsTileVisible(int x, int y) {
		return visibleData[y][x];
	}

	public static void calculateFogOfWar() {
		// calculate fog of war
		for (int i = 0; i < SimpleRTS.playerList.size(); i++) {
			Point location = SimpleRTS.playerList.get(i).getCurrentPoint();
			Point mapPoint = SimpleRTS.playerList.get(i).getMapPoint(location);
			int mapX = (int) mapPoint.getX();
			int mapY = (int) mapPoint.getY();

			int range = 5;

			for (int dy = -range; dy <= range; dy++) {
				if (mapY + dy < 0 || mapY + dy >= GameMap.mapdata.length) {
					continue;
				}

				for (int dx = -range; dx <= range; dx++) {
					if (mapX + dx < 0 || mapX + dx >= GameMap.mapdata[0].length) {
						continue;
					}

					visibleData[mapY + dy][mapX + dx] = true;
				}
			}
		}
	}
}
