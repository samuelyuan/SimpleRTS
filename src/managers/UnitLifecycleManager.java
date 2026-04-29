package managers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import entities.GameFlag;
import entities.GameUnit;
import graphics.Point;
import utils.Constants;
import utils.TileCoordinateConverter;

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
        loadUnits(allyUnitPositions, true, Constants.PLAYER_FACTION_ID);
    }

    public void loadEnemyUnits(Map<Point, Integer> enemyUnitPositions) {
        loadUnits(enemyUnitPositions, false, Constants.ENEMY_FACTION_ID);
    }

    private void loadUnits(Map<Point, Integer> unitPositions, boolean isPlayerUnit, int factionId) {
        ArrayList<GameUnit> unitList = listForFaction(factionId);
        if (unitList == null) {
            return;
        }

        for (Entry<Point, Integer> entry : unitPositions.entrySet()) {
            Point worldPos = TileCoordinateConverter.mapToScreen(entry.getKey());
            GameUnit unit = new GameUnit(worldPos.x, worldPos.y, isPlayerUnit, entry.getValue());
            unit.setFactionId(factionId);
            unitList.add(unit);
        }
    }

    private ArrayList<GameUnit> listForFaction(int factionId) {
        if (factionId == GameFlag.FACTION_PLAYER) {
            return playerList;
        }
        if (factionId == GameFlag.FACTION_ENEMY) {
            return enemyList;
        }
        return null;
    }

    private ArrayList<GameUnit> getUnitListOrEmpty(int factionId) {
        ArrayList<GameUnit> unitList = listForFaction(factionId);
        if (unitList == null) {
            return new ArrayList<>();
        }
        return unitList;
    }

    /**
     * Removes dead units from a unit list
     */
    public void removeDeadUnits(ArrayList<GameUnit> unitList, int deadUnitIndex) {
        if (deadUnitIndex >= 0 && deadUnitIndex < unitList.size()) {
            // Simply remove from list - units are not stored on map
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
        return getUnitListOrEmpty(factionId);
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
    public void spawnUnit(GameUnit unit, int factionId) {
        unit.setFactionId(factionId);

        addUnit(unit); // Add to the appropriate faction list
    }

    /**
     * Creates and adds a unit at a map position for the given faction.
     */
    public void createAndAddUnitAtMapPosition(Point mapPos, int factionId, int unitType) {
        Point worldPos = TileCoordinateConverter.mapToScreen(mapPos);
        GameUnit unit = new GameUnit(worldPos.x, worldPos.y, true, unitType);
        unit.setFactionId(factionId);
        addUnit(unit);
    }

    /**
     * Adds a unit to the appropriate faction list
     */
    public void addUnit(GameUnit unit) {
        ArrayList<GameUnit> unitList = listForFaction(unit.getFactionId());
        if (unitList != null) {
            unitList.add(unit);
        }
    }

    /**
     * Removes a unit from its faction list
     */
    public void removeUnitFromList(GameUnit unit) {
        ArrayList<GameUnit> unitList = listForFaction(unit.getFactionId());
        if (unitList != null) {
            unitList.remove(unit);
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
    public void cleanupDeadUnits() {
        // Remove dead player units
        for (int i = playerList.size() - 1; i >= 0; i--) {
            if (!playerList.get(i).isAlive()) {
                removeDeadUnits(playerList, i);
            }
        }

        // Remove dead enemy units
        for (int i = enemyList.size() - 1; i >= 0; i--) {
            if (!enemyList.get(i).isAlive()) {
                removeDeadUnits(enemyList, i);
            }
        }
    }
}

