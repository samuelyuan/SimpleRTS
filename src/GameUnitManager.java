import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Map;

import graphics.Point;

public class GameUnitManager {
	private GameFlagManager flagManager;
	private ArrayList<GameUnit> playerList;
	private ArrayList<GameUnit> enemyList;

	public GameUnitManager(GameFlagManager flagManager) {
		this.playerList = new ArrayList<>();
		this.enemyList = new ArrayList<>();
		this.flagManager = flagManager;
	}

	public ArrayList<GameUnit> getPlayerList() {
		return playerList;
	}

	public ArrayList<GameUnit> getEnemyList() {
		return enemyList;
	}

	public GameFlagManager getFlagManager() {
		return flagManager;
	}

	public void init(Map<Point, Integer> allyUnitPositions, Map<Point, Integer> enemyUnitPositions, Map<Point, Integer> flagPositions) {
		clearUnits();
		loadAllUnits(allyUnitPositions, enemyUnitPositions);
		loadAllFlags(flagPositions);
	}

	/**
	 * Clears all unit lists.
	 */
	public void clearUnits() {
		playerList.clear();
		enemyList.clear();
	}

	/**
	 * Loads all player and enemy units from the map.
	 */
	public void loadAllUnits(Map<Point, Integer> allyUnitPositions, Map<Point, Integer> enemyUnitPositions) {
		loadPlayerUnits(allyUnitPositions);
		loadEnemyUnits(enemyUnitPositions);
	}

	/**
	 * Loads all flags from the map.
	 */
	public void loadAllFlags(Map<Point, Integer> flagPositions) {
		loadFlag(flagPositions);
	}

	public void loadPlayerUnits(Map<Point, Integer> allyUnitPositions) {
		for (Entry<Point, Integer> entry : allyUnitPositions.entrySet()) {
			Point initialPosition = entry.getKey();
			int classType = entry.getValue();
			playerList.add(new GameUnit(initialPosition.x * GameMap.TILE_WIDTH, initialPosition.y * GameMap.TILE_HEIGHT,
				true, classType));
		}
	}

	public void loadEnemyUnits(Map<Point, Integer> enemyUnitPositions) {
		for (Entry<Point, Integer> entry : enemyUnitPositions.entrySet()) {
			Point initialPosition = entry.getKey();
			int classType = entry.getValue();
			enemyList.add(new GameUnit(initialPosition.x * GameMap.TILE_WIDTH, initialPosition.y * GameMap.TILE_HEIGHT,
				false, classType));
		}
	}

	public void loadFlag(Map<Point, Integer> flagPositions) {
		for (Entry<Point, Integer> entry : flagPositions.entrySet()) {
			Point position = entry.getKey();
			int faction = entry.getValue();
			if (faction == GameFlag.FACTION_PLAYER)
				flagManager.addPlayerFlag(position.x, position.y);
			else if (faction == GameFlag.FACTION_ENEMY)
				flagManager.addEnemyFlag(position.x, position.y);
		}
	}

	public void checkFlagStates(GameUnit unit, int factionId) {
		int unitMapX = unit.getCurrentPoint().x / GameMap.TILE_WIDTH;
		int unitMapY = unit.getCurrentPoint().y / GameMap.TILE_HEIGHT;
		flagManager.checkFlagState(unitMapX, unitMapY, factionId);
	}

	public boolean isFlagsListEmpty(int factionId) {
		if (factionId == GameFlag.FACTION_PLAYER) {
			return flagManager.isPlayerFlagsEmpty();
		} else if (factionId == GameFlag.FACTION_ENEMY) {
			return flagManager.isEnemyFlagsEmpty();
		} else {
			return true;
		}
	}

	public void removeDeadUnits(int map[][], ArrayList<GameUnit> unitList, int deadUnitIndex) {
		GameUnit deadUnit = unitList.get(deadUnitIndex);
		deadUnit.die(map);
		unitList.remove(deadUnitIndex);
	}

	public String printMap(int[][] map) {
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
