import java.util.ArrayList;

import graphics.Point;
import map.TileConverter;

/**
 * Handles unit spawning logic around flags.
 */
public class UnitSpawner {
    private final GameUnitManager unitManager;
    private boolean isSpawned = false; // only spawn once per day
    
    public UnitSpawner(GameUnitManager unitManager) {
        this.unitManager = unitManager;
    }
    
    public void spawnUnitsNearFlag(int[][] map, GameFlag flag) {
        // Spawn four units around the flag (N, S, E, W)
        for (int i = 0; i < 4; i++) {
            int flagFactionId = flag.getControlFaction();
            
            // Try WEST first
            if (isTileAvailable(map, flag.getMapX() - 1, flag.getMapY(), flagFactionId)) {
                addUnitToMap(map, flag.getMapX() - 1, flag.getMapY(), flagFactionId);
            }
            // Try EAST
            else if (isTileAvailable(map, flag.getMapX() + 1, flag.getMapY(), flagFactionId)) {
                addUnitToMap(map, flag.getMapX() + 1, flag.getMapY(), flagFactionId);
            }
            // Try NORTH
            else if (isTileAvailable(map, flag.getMapX(), flag.getMapY() - 1, flagFactionId)) {
                addUnitToMap(map, flag.getMapX(), flag.getMapY() - 1, flagFactionId);
            }
            // Try SOUTH
            else if (isTileAvailable(map, flag.getMapX(), flag.getMapY() + 1, flagFactionId)) {
                addUnitToMap(map, flag.getMapX(), flag.getMapY() + 1, flagFactionId);
            } else {
                break; // No available tiles
            }
        }
    }
    
    public boolean isTileAvailable(int[][] map, int x, int y, int factionId) {
        ArrayList<GameUnit> unitList = getUnitList(factionId);
        
        // Check bounds
        if (x < 0 || y < 0 || y >= map.length || x >= map[0].length) {
            return false;
        }
        
        // If there's a wall, then it's occupied
        if (map[y][x] == TileConverter.TILE_WALL) {
            return false;
        }
        
        // If a unit is standing on the desired tile, the tile is considered to be occupied
        for (GameUnit unit : unitList) {
            if (unit.isOnTile(map, x, y)) {
                return false;
            }
        }
        
        return true;
    }
    
    public void addUnitToMap(int[][] map, int x, int y, int factionId) {
        ArrayList<GameUnit> unitList = getUnitList(factionId);
        
        // Create new unit
        GameUnit newUnit = new GameUnit(TileCoordinateConverter.mapToScreen(x, y).x, TileCoordinateConverter.mapToScreen(x, y).y,
                (factionId == GameFlag.FACTION_PLAYER), Constants.UNIT_ID_LIGHT);
        newUnit.spawn(map, new Point(x, y), factionId);
        
        // Add unit to list
        unitList.add(newUnit);
    }
    
    private ArrayList<GameUnit> getUnitList(int factionId) {
        if (factionId == GameFlag.FACTION_PLAYER) {
            return unitManager.getPlayerList();
        } else if (factionId == GameFlag.FACTION_ENEMY) {
            return unitManager.getEnemyList();
        }
        return new ArrayList<>();
    }
    
    public boolean isSpawned() {
        return isSpawned;
    }
    
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
} 