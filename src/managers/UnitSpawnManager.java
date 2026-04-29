package managers;

import java.util.ArrayList;

import entities.GameFlag;
import graphics.Point;
import utils.Constants;
import utils.FormationUtils;

/**
 * Manages unit spawning logic including patterns, formations, and configuration.
 */
public class UnitSpawnManager {
    
    /**
     * Configuration class for spawn settings
     */
    public static class SpawnConfig {
        public int defaultUnitCount = 4;
        public int maxSpawnDistance = 2;
        public int defaultUnitType = Constants.UNIT_ID_LIGHT;
        public boolean useRandomSpawns = true;
        public boolean useFormationSpawns = false;
        public int maxUnitsPerFaction = 50; // Prevent unlimited spawning
        public double spawnChance = 1.0; // 100% chance to spawn
        
        public SpawnConfig() {}
        
        public SpawnConfig(int unitCount, int maxDistance, int unitType) {
            this.defaultUnitCount = unitCount;
            this.maxSpawnDistance = maxDistance;
            this.defaultUnitType = unitType;
        }
    }
    
    private final SpawnConfig spawnConfig;
    
    public UnitSpawnManager() {
        this.spawnConfig = new SpawnConfig();
    }
    
    public UnitSpawnManager(SpawnConfig config) {
        this.spawnConfig = config;
    }
    
    /**
     * Sets the spawn configuration
     */
    public void setSpawnConfig(SpawnConfig config) {
        // Copy values to avoid external modification
        this.spawnConfig.defaultUnitCount = config.defaultUnitCount;
        this.spawnConfig.maxSpawnDistance = config.maxSpawnDistance;
        this.spawnConfig.defaultUnitType = config.defaultUnitType;
        this.spawnConfig.useRandomSpawns = config.useRandomSpawns;
        this.spawnConfig.useFormationSpawns = config.useFormationSpawns;
        this.spawnConfig.maxUnitsPerFaction = config.maxUnitsPerFaction;
        this.spawnConfig.spawnChance = config.spawnChance;
    }
    
    /**
     * Gets the current spawn configuration
     */
    public SpawnConfig getSpawnConfig() {
        return spawnConfig;
    }
    
    /**
     * Checks if spawning is allowed based on current conditions
     */
    public boolean canSpawn(int currentUnitCount) {
        // Check unit limit
        if (currentUnitCount >= spawnConfig.maxUnitsPerFaction) {
            return false;
        }
        
        // Check spawn chance
        if (Math.random() > spawnConfig.spawnChance) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Spawns units near a flag with flexible configuration
     */
    public int spawnUnitsNearFlag(int[][] map, GameFlag flag, int currentUnitCount) {
        return getSpawnPositionsNearFlag(map, flag, currentUnitCount).size();
    }
    
    /**
     * Spawns units near a flag with custom parameters
     */
    public int spawnUnitsNearFlag(int[][] map, GameFlag flag, int unitCount, int maxDistance) {
        return getSpawnPositionsNearFlag(map, flag, unitCount, maxDistance).size();
    }

    /**
     * Gets spawn positions near a flag using current configuration.
     */
    public ArrayList<Point> getSpawnPositionsNearFlag(int[][] map, GameFlag flag, int currentUnitCount) {
        ArrayList<Point> positions = new ArrayList<>();

        if (!canSpawn(currentUnitCount)) {
            return positions;
        }

        return getSpawnPositionsNearFlag(map, flag, spawnConfig.defaultUnitCount, spawnConfig.maxSpawnDistance);
    }

    /**
     * Gets spawn positions near a flag with custom parameters.
     */
    public ArrayList<Point> getSpawnPositionsNearFlag(int[][] map, GameFlag flag, int unitCount, int maxDistance) {
        int flagX = flag.getMapX();
        int flagY = flag.getMapY();

        ArrayList<Point> positions = new ArrayList<>();
        addPatternPositions(positions, map, flagX, flagY, 1, unitCount - positions.size());

        if (positions.size() < unitCount && maxDistance >= 2) {
            addPatternPositions(positions, map, flagX, flagY, 2, unitCount - positions.size());
        }

        if (positions.size() < unitCount && spawnConfig.useRandomSpawns) {
            addRandomPositions(positions, map, flagX, flagY, maxDistance, unitCount - positions.size());
        }

        if (positions.size() < unitCount && spawnConfig.useFormationSpawns) {
            positions.addAll(getSpawnPositionsInFormation(
                    map,
                    flagX,
                    flagY,
                    unitCount - positions.size()));
        }

        return positions;
    }
    
    /**
     * Spawns units in a ring pattern around a center point
     */
    private void addPatternPositions(ArrayList<Point> positions, int[][] map, int centerX, int centerY, int distance, int maxUnits) {
        int added = 0;
        // Define spawn positions in a ring pattern
        int[][] spawnPositions = {
            {centerX - distance, centerY},           // West
            {centerX + distance, centerY},           // East
            {centerX, centerY - distance},           // North
            {centerX, centerY + distance},           // South
            {centerX - distance, centerY - distance}, // Northwest
            {centerX + distance, centerY - distance}, // Northeast
            {centerX - distance, centerY + distance}, // Southwest
            {centerX + distance, centerY + distance}  // Southeast
        };
        
        // Try each position
        for (int[] pos : spawnPositions) {
            if (added >= maxUnits) break;
            if (isTileAvailable(map, pos[0], pos[1])) {
                positions.add(new Point(pos[0], pos[1]));
                added++;
            }
        }
    }
    
    /**
     * Spawns units at random positions within a given distance
     */
    private void addRandomPositions(ArrayList<Point> positions, int[][] map, int centerX, int centerY, int maxDistance, int maxUnits) {
        int spawned = 0;
        int attempts = 0;
        int maxAttempts = maxUnits * 10; // Prevent infinite loops
        
        while (spawned < maxUnits && attempts < maxAttempts) {
            // Generate random position within maxDistance
            int offsetX = (int) ((Math.random() - 0.5) * 2 * maxDistance);
            int offsetY = (int) ((Math.random() - 0.5) * 2 * maxDistance);
            
            int spawnX = centerX + offsetX;
            int spawnY = centerY + offsetY;
            
            if (isTileAvailable(map, spawnX, spawnY)) {
                positions.add(new Point(spawnX, spawnY));
                spawned++;
            }
            
            attempts++;
        }
    }
    
    /**
     * Spawns units in a formation pattern
     */
    public int spawnUnitsInFormation(int[][] map, int centerX, int centerY, int unitCount) {
        return getSpawnPositionsInFormation(map, centerX, centerY, unitCount).size();
    }

    /**
     * Gets formation spawn positions.
     */
    public ArrayList<Point> getSpawnPositionsInFormation(int[][] map, int centerX, int centerY, int unitCount) {
        ArrayList<Point> positions = new ArrayList<>();
        int radius = FormationUtils.calculateBaseFormationRadius(unitCount);

        for (int i = 0; i < unitCount && positions.size() < unitCount; i++) {
            Point formationPos = FormationUtils.calculateFormationPosition(new Point(centerX, centerY), i, unitCount, radius);
            if (isTileAvailable(map, formationPos.x, formationPos.y)) {
                positions.add(new Point(formationPos.x, formationPos.y));
            }
        }

        return positions;
    }
    
    /**
     * Spawns units along a path between two points
     */
    public int spawnUnitsAlongPath(int[][] map, Point startPos, Point endPos, int unitCount) {
        return getSpawnPositionsAlongPath(map, startPos, endPos, unitCount).size();
    }

    /**
     * Gets spawn positions along a path.
     */
    public ArrayList<Point> getSpawnPositionsAlongPath(int[][] map, Point startPos, Point endPos, int unitCount) {
        ArrayList<Point> positions = new ArrayList<>();
        double stepX = (endPos.x - startPos.x) / (double) (unitCount + 1);
        double stepY = (endPos.y - startPos.y) / (double) (unitCount + 1);

        for (int i = 1; i <= unitCount && positions.size() < unitCount; i++) {
            int spawnX = (int) (startPos.x + stepX * i);
            int spawnY = (int) (startPos.y + stepY * i);

            if (isTileAvailable(map, spawnX, spawnY)) {
                positions.add(new Point(spawnX, spawnY));
            }
        }

        return positions;
    }
    
    /**
     * Checks if a tile is available for unit placement
     */
    public boolean isTileAvailable(int[][] map, int x, int y) {
        // Check bounds
        if (x < 0 || y < 0 || y >= map.length || x >= map[0].length) {
            return false;
        }
        
        // Check if tile is empty
        if (map[y][x] != 0) {
            return false;
        }
        
        // Check if tile is accessible (not blocked by terrain)
        // For now, assume all empty tiles are accessible
        return true;
    }

}

