import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import graphics.Point;
import utils.TileCoordinateConverter;
import utils.Constants;

/**
 * Manages unit lifecycle operations including creation, loading, removal, and
 * tracking.
 */
public class UnitLifecycleManager {

    private ArrayList<GameUnit> playerList;
    private ArrayList<GameUnit> enemyList;
    private boolean isSpawned = false; // only spawn once per day

    public UnitLifecycleManager() {
        this.playerList = new ArrayList<>();
        this.enemyList = new ArrayList<>();
    }

    /**
     * Initializes the unit manager with unit positions
     */
    public void init(Map<Point, Integer> allyUnitPositions, Map<Point, Integer> enemyUnitPositions) {
        clearUnits();
        loadAllUnits(allyUnitPositions, enemyUnitPositions);
    }

    /**
     * Clears all units from both lists
     */
    public void clearUnits() {
        playerList.clear();
        enemyList.clear();
    }

    /**
     * Loads all units from position maps
     */
    public void loadAllUnits(Map<Point, Integer> allyUnitPositions, Map<Point, Integer> enemyUnitPositions) {
        loadPlayerUnits(allyUnitPositions);
        loadEnemyUnits(enemyUnitPositions);
    }

    public void loadPlayerUnits(Map<Point, Integer> allyUnitPositions) {
        for (Entry<Point, Integer> entry : allyUnitPositions.entrySet()) {
            Point worldPos = TileCoordinateConverter.mapToScreen(entry.getKey());
            GameUnit unit = new GameUnit(worldPos.x, worldPos.y, true, entry.getValue());
            unit.setFactionId(Constants.PLAYER_FACTION_ID);
            playerList.add(unit);
        }
    }

    public void loadEnemyUnits(Map<Point, Integer> enemyUnitPositions) {
        for (Entry<Point, Integer> entry : enemyUnitPositions.entrySet()) {
            Point worldPos = TileCoordinateConverter.mapToScreen(entry.getKey());
            GameUnit unit = new GameUnit(worldPos.x, worldPos.y, false, entry.getValue());
            unit.setFactionId(Constants.ENEMY_FACTION_ID);
            enemyList.add(unit);
        }
    }

    /**
     * Removes dead units from a unit list
     */
    public void removeDeadUnits(int map[][], ArrayList<GameUnit> unitList, int deadUnitIndex) {
        if (deadUnitIndex >= 0 && deadUnitIndex < unitList.size()) {
            GameUnit deadUnit = unitList.get(deadUnitIndex);
            removeUnit(deadUnit, map);
            unitList.remove(deadUnitIndex);
        }
    }

    /**
     * Gets the total unit count for a faction
     */
    public int getUnitCount(int factionId) {
        return getUnitList(factionId).size();
    }

    /**
     * Gets the unit list for a specific faction
     */
    public ArrayList<GameUnit> getUnitList(int factionId) {
        if (factionId == GameFlag.FACTION_PLAYER) {
            return playerList;
        } else if (factionId == GameFlag.FACTION_ENEMY) {
            return enemyList;
        }
        return new ArrayList<>(); // Return empty list for unknown factions
    }

    /**
     * Gets the player unit list
     */
    public ArrayList<GameUnit> getPlayerList() {
        return playerList;
    }

    /**
     * Gets the enemy unit list
     */
    public ArrayList<GameUnit> getEnemyList() {
        return enemyList;
    }

    /**
     * Checks if units have been spawned for the current day
     */
    public boolean isSpawned() {
        return isSpawned;
    }

    /**
     * Updates the spawn state based on current game time
     */
    public void updateSpawnState(int currentHour) {
        // Set to true at spawn hour to prevent repeated spawning
        if (currentHour == GameTimer.SPAWN_HOUR && !isSpawned) {
            isSpawned = true;
        }

        // Reset after the hour has passed
        if (currentHour > GameTimer.SPAWN_HOUR) {
            isSpawned = false;
        }
    }

    /**
     * Spawns a unit on the map at the specified position
     */
    public void spawnUnit(GameUnit unit, int[][] map, Point mapPos, int factionId) {
        Point worldPos = TileCoordinateConverter.mapToWorld(mapPos);
        unit.setFactionId(factionId);

        // Update the map array with the unit
        if (factionId == GameFlag.FACTION_PLAYER) {
            map[mapPos.y][mapPos.x] = Constants.UNIT_ID_LIGHT + 1;
        } else if (factionId == GameFlag.FACTION_ENEMY) {
            map[mapPos.y][mapPos.x] = Constants.UNIT_ID_LIGHT + 4;
        }

        addUnit(unit); // Add to the appropriate faction list
    }

    /**
     * Removes a unit from the map
     */
    public void removeUnit(GameUnit unit, int[][] map) {
        Point curMap = TileCoordinateConverter.screenToMap(unit.getCurrentPosition());
        map[curMap.y][curMap.x] = 0;

        Point destMap = TileCoordinateConverter.screenToMap(unit.getDestination());
        map[destMap.y][destMap.x] = 0;
    }

    /**
     * Adds a unit to the appropriate faction list
     */
    public void addUnit(GameUnit unit) {
        if (unit.getFactionId() == GameFlag.FACTION_PLAYER) {
            playerList.add(unit);
        } else if (unit.getFactionId() == GameFlag.FACTION_ENEMY) {
            enemyList.add(unit);
        }
    }

    /**
     * Removes a unit from its faction list
     */
    public void removeUnitFromList(GameUnit unit) {
        if (unit.getFactionId() == GameFlag.FACTION_PLAYER) {
            playerList.remove(unit);
        } else if (unit.getFactionId() == GameFlag.FACTION_ENEMY) {
            enemyList.remove(unit);
        }
    }

    /**
     * Gets all units from both factions
     */
    public ArrayList<GameUnit> getAllUnits() {
        ArrayList<GameUnit> allUnits = new ArrayList<>();
        allUnits.addAll(playerList);
        allUnits.addAll(enemyList);
        return allUnits;
    }

    /**
     * Gets all alive units from a faction
     */
    public ArrayList<GameUnit> getAliveUnits(int factionId) {
        ArrayList<GameUnit> aliveUnits = new ArrayList<>();
        ArrayList<GameUnit> unitList = getUnitList(factionId);

        for (GameUnit unit : unitList) {
            if (unit.isAlive()) {
                aliveUnits.add(unit);
            }
        }

        return aliveUnits;
    }

    /**
     * Gets all dead units from a faction
     */
    public ArrayList<GameUnit> getDeadUnits(int factionId) {
        ArrayList<GameUnit> deadUnits = new ArrayList<>();
        ArrayList<GameUnit> unitList = getUnitList(factionId);

        for (GameUnit unit : unitList) {
            if (!unit.isAlive()) {
                deadUnits.add(unit);
            }
        }

        return deadUnits;
    }

    /**
     * Cleans up dead units from both factions
     */
    public void cleanupDeadUnits(int[][] map) {
        // Remove dead player units
        for (int i = playerList.size() - 1; i >= 0; i--) {
            if (!playerList.get(i).isAlive()) {
                removeDeadUnits(map, playerList, i);
            }
        }

        // Remove dead enemy units
        for (int i = enemyList.size() - 1; i >= 0; i--) {
            if (!enemyList.get(i).isAlive()) {
                removeDeadUnits(map, enemyList, i);
            }
        }
    }
}
