import graphics.Point;
import utils.Constants;

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
    public boolean canSpawn(int factionId, int currentUnitCount) {
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
        int factionId = flag.getControlFaction();
        
        // Check if spawning is allowed
        if (!canSpawn(factionId, currentUnitCount)) {
            return 0;
        }
        
        return spawnUnitsNearFlag(map, flag, spawnConfig.defaultUnitCount, 
                                spawnConfig.maxSpawnDistance, spawnConfig.defaultUnitType);
    }
    
    /**
     * Spawns units near a flag with custom parameters
     */
    public int spawnUnitsNearFlag(int[][] map, GameFlag flag, int unitCount, int maxDistance, int unitType) {
        int flagFactionId = flag.getControlFaction();
        int flagX = flag.getMapX();
        int flagY = flag.getMapY();
        
        // Try different spawn patterns
        int spawnedCount = 0;
        
        // Pattern 1: Adjacent tiles (distance 1)
        spawnedCount += spawnInPattern(map, flagX, flagY, flagFactionId, unitType, 1, unitCount - spawnedCount);
        
        // Pattern 2: Ring around flag (distance 2)
        if (spawnedCount < unitCount && maxDistance >= 2) {
            spawnedCount += spawnInPattern(map, flagX, flagY, flagFactionId, unitType, 2, unitCount - spawnedCount);
        }
        
        // Pattern 3: Random positions within maxDistance (if enabled)
        if (spawnedCount < unitCount && spawnConfig.useRandomSpawns) {
            spawnedCount += spawnRandomly(map, flagX, flagY, flagFactionId, unitType, maxDistance, unitCount - spawnedCount);
        }
        
        // Pattern 4: Formation spawn (if enabled)
        if (spawnedCount < unitCount && spawnConfig.useFormationSpawns) {
            spawnedCount += spawnUnitsInFormation(map, flagX, flagY, flagFactionId, unitType, unitCount - spawnedCount);
        }
        
        return spawnedCount;
    }
    
    /**
     * Spawns units in a ring pattern around a center point
     */
    private int spawnInPattern(int[][] map, int centerX, int centerY, int factionId, int unitType, int distance, int maxUnits) {
        int spawned = 0;
        
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
            if (spawned >= maxUnits) break;
            
            if (isTileAvailable(map, pos[0], pos[1], factionId)) {
                addUnitToMap(map, pos[0], pos[1], factionId, unitType);
                spawned++;
            }
        }
        
        return spawned;
    }
    
    /**
     * Spawns units at random positions within a given distance
     */
    private int spawnRandomly(int[][] map, int centerX, int centerY, int factionId, int unitType, int maxDistance, int maxUnits) {
        int spawned = 0;
        int attempts = 0;
        int maxAttempts = maxUnits * 10; // Prevent infinite loops
        
        while (spawned < maxUnits && attempts < maxAttempts) {
            // Generate random position within maxDistance
            int offsetX = (int) ((Math.random() - 0.5) * 2 * maxDistance);
            int offsetY = (int) ((Math.random() - 0.5) * 2 * maxDistance);
            
            int spawnX = centerX + offsetX;
            int spawnY = centerY + offsetY;
            
            if (isTileAvailable(map, spawnX, spawnY, factionId)) {
                addUnitToMap(map, spawnX, spawnY, factionId, unitType);
                spawned++;
            }
            
            attempts++;
        }
        
        return spawned;
    }
    
    /**
     * Spawns units in a formation pattern
     */
    public int spawnUnitsInFormation(int[][] map, int centerX, int centerY, int factionId, int unitType, int unitCount) {
        int spawned = 0;
        int radius = calculateFormationRadius(unitCount);
        
        for (int i = 0; i < unitCount && spawned < unitCount; i++) {
            Point formationPos = calculateFormationPosition(new Point(centerX, centerY), i, unitCount, radius);
            
            if (isTileAvailable(map, formationPos.x, formationPos.y, factionId)) {
                addUnitToMap(map, formationPos.x, formationPos.y, factionId, unitType);
                spawned++;
            }
        }
        
        return spawned;
    }
    
    /**
     * Spawns units along a path between two points
     */
    public int spawnUnitsAlongPath(int[][] map, Point startPos, Point endPos, int factionId, int unitType, int unitCount) {
        int spawned = 0;
        double stepX = (endPos.x - startPos.x) / (double) (unitCount + 1);
        double stepY = (endPos.y - startPos.y) / (double) (unitCount + 1);
        
        for (int i = 1; i <= unitCount && spawned < unitCount; i++) {
            int spawnX = (int) (startPos.x + stepX * i);
            int spawnY = (int) (startPos.y + stepY * i);
            
            if (isTileAvailable(map, spawnX, spawnY, factionId)) {
                addUnitToMap(map, spawnX, spawnY, factionId, unitType);
                spawned++;
            }
        }
        
        return spawned;
    }
    
    /**
     * Checks if a tile is available for unit placement
     */
    public boolean isTileAvailable(int[][] map, int x, int y, int factionId) {
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
    
    /**
     * Adds a unit to the map at the specified position
     */
    private void addUnitToMap(int[][] map, int x, int y, int factionId, int unitType) {
        // 2,3,4 - player units
        // 5,6,7 - enemy units
        if (factionId == GameFlag.FACTION_PLAYER) {
            map[y][x] = Constants.UNIT_ID_LIGHT + 1;
        } else if (factionId == GameFlag.FACTION_ENEMY) {
            map[y][x] = Constants.UNIT_ID_LIGHT + 4;
        }
    }
    
    /**
     * Calculates the radius for formation spawning based on unit count
     */
    private int calculateFormationRadius(int unitCount) {
        if (unitCount <= 4) return 1;
        if (unitCount <= 8) return 2;
        if (unitCount <= 16) return 3;
        return 4; // Cap at reasonable radius
    }
    
    /**
     * Calculates position for a unit in a formation
     */
    private Point calculateFormationPosition(Point center, int unitIndex, int totalUnits, int radius) {
        if (totalUnits <= 1) {
            return new Point(center.x, center.y);
        }
        
        // Calculate angle and distance for this unit
        double angle = (2 * Math.PI * unitIndex) / totalUnits;
        double distance = radius;
        
        // Calculate offset from center
        int offsetX = (int) (Math.cos(angle) * distance);
        int offsetY = (int) (Math.sin(angle) * distance);
        
        return new Point(center.x + offsetX, center.y + offsetY);
    }
}
