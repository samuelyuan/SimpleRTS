import java.util.ArrayList;
import graphics.Point;
import pathfinding.PathCache;
import pathfinding.PathfindingUtils;
import utils.TileCoordinateConverter;

/**
 * Manages pathfinding and group destination logic for multiple units.
 * Handles collision avoidance, shared path caching, and group destination coordination.
 */
public class MultiUnitPathfindingManager {
    private PathCache sharedPathCache;
    
    public MultiUnitPathfindingManager() {
        this.sharedPathCache = new PathCache();
    }
    
    public MultiUnitPathfindingManager(PathCache sharedPathCache) {
        this.sharedPathCache = sharedPathCache;
    }
    
    /**
     * Gets the shared path cache instance.
     * @return The shared PathCache instance
     */
    public PathCache getSharedPathCache() {
        return sharedPathCache;
    }
    
    /**
     * Clears the shared path cache.
     */
    public void clearPathCache() {
        sharedPathCache.clear();
    }
    
    /**
     * Updates group destinations for units moving to the same destination
     * @param map The game map
     * @param playerList List of player units
     * @param enemyList List of enemy units
     */
    public void updateGroupDestinations(int[][] map, ArrayList<GameUnit> playerList, ArrayList<GameUnit> enemyList) {
        // Get all units for collision detection
        ArrayList<GameUnit> allUnits = new ArrayList<>();
        allUnits.addAll(playerList);
        allUnits.addAll(enemyList);
        
        // Process player units
        for (GameUnit unit : playerList) {
            if (!unit.isAlive()) continue;
            updateUnitGroupDestination(unit, map, playerList, allUnits);
        }
        
        // Process enemy units
        for (GameUnit unit : enemyList) {
            if (!unit.isAlive()) continue;
            updateUnitGroupDestination(unit, map, enemyList, allUnits);
        }
    }
    
    /**
     * Updates group destination for a specific unit
     * @param unit The unit to update
     * @param map The game map
     * @param unitList The list containing the unit
     * @param allUnits All units for collision detection
     */
    private void updateUnitGroupDestination(GameUnit unit, int[][] map, ArrayList<GameUnit> unitList, ArrayList<GameUnit> allUnits) {
        for (GameUnit other : unitList) {
            if (other == unit) continue;
            if (!other.isAlive()) continue;
            
            if (isSameDestination(unit, other)) {
                if (!other.getMovementController().getIsPathCreated()) continue;
                
                Point newDest = other.getMovementController().recalculateDest(map, TileCoordinateConverter.screenToMap(unit.getDestination()));
                
                				// Validate the new destination before setting it
				if (PathfindingUtils.isValidDestination(newDest, map) && !isDestinationOccupiedByUnit(newDest, allUnits, other)) {
					other.setDestination(TileCoordinateConverter.mapToScreen(newDest));
					other.getMovementController().updateDestination(TileCoordinateConverter.screenToMap(other.getDestination()));
				} else {
					// If recalculateDest failed, try to find a fallback destination
					Point fallbackDest = PathfindingUtils.findFallbackDestination(map, TileCoordinateConverter.screenToMap(other.getCurrentPosition()));
					if (fallbackDest != null) {
						other.setDestination(TileCoordinateConverter.mapToScreen(fallbackDest));
						other.getMovementController().updateDestination(TileCoordinateConverter.screenToMap(other.getDestination()));
					}
					// If no fallback found, the unit will stay in place (better than moving to invalid location)
				}
            }
        }
    }
    
    /**
     * Checks if two units have the same destination
     * @param unit1 First unit
     * @param unit2 Second unit
     * @return true if they have the same destination
     */
    private boolean isSameDestination(GameUnit unit1, GameUnit unit2) {
        Point otherMapDest = TileCoordinateConverter.screenToMap(unit2.getDestination());
        Point playerMapDest = TileCoordinateConverter.screenToMap(unit1.getDestination());
        return otherMapDest.equals(playerMapDest);
    }
    
    /**
     * Checks if a destination is occupied by another unit
     * @param dest The destination to check
     * @param allUnits All units to check against
     * @param excludeUnit Unit to exclude from the check
     * @return true if the destination is occupied
     */
    private boolean isDestinationOccupiedByUnit(Point dest, ArrayList<GameUnit> allUnits, GameUnit excludeUnit) {
        if (allUnits == null) return false;
        
        for (GameUnit unit : allUnits) {
            if (unit == excludeUnit) continue; // Skip the unit we're checking for
            if (!unit.isAlive()) continue; // Skip dead units
            
            // Check if unit is at or very close to the destination
            Point unitPos = unit.getCurrentPosition();
            double distance = Math.sqrt(
                Math.pow(dest.x - unitPos.x, 2) + 
                Math.pow(dest.y - unitPos.y, 2)
            );
            
            // If unit is within 25 pixels (half a tile), consider it occupied
            if (distance < 25) {
                return true;
            }
        }
        return false;
    }
}
