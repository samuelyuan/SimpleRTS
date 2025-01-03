import java.awt.Point;
import java.util.ArrayList;
import java.util.Map.Entry;

public class GameUnitManager {
	private static GameFlagManager flagManager;

	public static GameFlagManager getFlagManager() {
		return flagManager;
	}

	public static void init(ArrayList<GameUnit> playerList, ArrayList<GameUnit> enemyList) {
		playerList.clear();
		enemyList.clear();
		flagManager = new GameFlagManager();

		loadPlayerUnits(playerList);
		loadEnemyUnits(enemyList);
		loadFlag();
	}

	public static void loadPlayerUnits(ArrayList<GameUnit> playerList) {
		for (Entry<Point, Integer> entry : GameMap.getAllyUnitPositions().entrySet()) {
			Point initialPosition = entry.getKey();
			int classType = entry.getValue();

			playerList.add(new GameUnit(initialPosition.x * GameMap.TILE_WIDTH, initialPosition.y * GameMap.TILE_HEIGHT,
					true, classType));
		}
	}

	public static void loadEnemyUnits(ArrayList<GameUnit> enemyList) {
		for (Entry<Point, Integer> entry : GameMap.getEnemyUnitPositions().entrySet()) {
			Point initialPosition = entry.getKey();
			int classType = entry.getValue();

			enemyList.add(new GameUnit(initialPosition.x * GameMap.TILE_WIDTH, initialPosition.y * GameMap.TILE_HEIGHT,
					false, classType));
		}
	}

	public static void loadFlag() {
		for (Entry<Point, Integer> entry : GameMap.getFlagPositions().entrySet()) {
			Point position = entry.getKey();
			int faction = entry.getValue();

			if (faction == GameFlag.FACTION_PLAYER)
				flagManager.addPlayerFlag(position.x, position.y);
			else if (faction == GameFlag.FACTION_ENEMY)
				flagManager.addEnemyFlag(position.x, position.y);
		}
	}

	public static void checkFlagStates(GameUnit unit, int factionId) {
		int unitMapX = unit.getCurrentPoint().x / GameMap.TILE_WIDTH;
		int unitMapY = unit.getCurrentPoint().y / GameMap.TILE_HEIGHT;

		flagManager.checkFlagState(unitMapX, unitMapY, factionId);
	}

	public static boolean isFlagsListEmpty(int factionId) {
		if (factionId == GameFlag.FACTION_PLAYER)
			return flagManager.isPlayerFlagsEmpty();
		else if (factionId == GameFlag.FACTION_ENEMY)
			return flagManager.isEnemyFlagsEmpty();
		else
			return true;
	}

	public static void removeDeadUnits(int map[][], ArrayList<GameUnit> unitList, int deadUnitIndex) {
		GameUnit deadUnit = unitList.get(deadUnitIndex);

		// Erase from map
		deadUnit.die(map);

		// Erase from unit master list
		unitList.remove(deadUnitIndex);
	}

	public static String printMap(int[][] map) {
		String s = "MAP:\n";

		for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				s += map[y][x];
				s += " ";
			}

			s += "\n";
		}

		return s;
	}
}
