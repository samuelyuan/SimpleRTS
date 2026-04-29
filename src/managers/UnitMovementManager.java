package managers;

import java.util.ArrayList;
import entities.GameUnit;
import graphics.Point;
import input.GameMouseEvent;
import utils.Constants;
import utils.DistanceUtils;
import utils.FormationUtils;

/**
 * Manages unit movement, formations, and movement-related logic.
 */
public class UnitMovementManager {
    
    /**
     * Handles unit movement based on mouse input
     * @param e Mouse event
     * @param cameraX Camera X position
     * @param cameraY Camera Y position
     * @param playerList List of player units
     * @param enemyList List of enemy units
     */
    public void handleUnitMovement(GameMouseEvent e, int cameraX, int cameraY, 
                                 ArrayList<GameUnit> playerList, ArrayList<GameUnit> enemyList) {
        if (e.getType() == Constants.MOUSE_PRESSED) {
            ArrayList<GameUnit> selectedUnits = getSelectedUnits(playerList, enemyList);
            
            if (selectedUnits.size() == 1) {
                handleSingleUnitMovement(e, selectedUnits.get(0), cameraX, cameraY);
            } else if (selectedUnits.size() > 1) {
                handleFormationMovement(e, selectedUnits, cameraX, cameraY);
            }
        }
    }
    
    /**
     * Handles movement for a single unit
     */
    private void handleSingleUnitMovement(GameMouseEvent e, GameUnit unit, int cameraX, int cameraY) {
        if (e.getType() == Constants.MOUSE_PRESSED) {
            Point targetPos = new Point(e.getX() + cameraX, e.getY() + cameraY);
            unit.setDestination(targetPos);
            unit.startMoving();
        }
    }
    
    /**
     * Handles movement for multiple units in formation
     */
    private void handleFormationMovement(GameMouseEvent e, ArrayList<GameUnit> selectedUnits, int cameraX, int cameraY) {
        if (e.getType() == Constants.MOUSE_PRESSED) {
            Point targetDestination = new Point(e.getX() + cameraX, e.getY() + cameraY);
            ArrayList<Point> formationPositions = calculateFormationPositions(targetDestination, selectedUnits.size());
            
            for (int i = 0; i < selectedUnits.size() && i < formationPositions.size(); i++) {
                GameUnit unit = selectedUnits.get(i);
                Point formationPos = formationPositions.get(i);
                unit.setDestination(formationPos);
                unit.startMoving();
            }
        }
    }
    
    /**
     * Gets all currently selected units
     */
    private ArrayList<GameUnit> getSelectedUnits(ArrayList<GameUnit> playerList, ArrayList<GameUnit> enemyList) {
        ArrayList<GameUnit> selectedUnits = new ArrayList<>();
        
        // Check player units
        for (GameUnit unit : playerList) {
            if (unit.isSelected()) {
                selectedUnits.add(unit);
            }
        }
        
        // Check enemy units
        for (GameUnit unit : enemyList) {
            if (unit.isSelected()) {
                selectedUnits.add(unit);
            }
        }
        
        return selectedUnits;
    }
    
    /**
     * Calculates formation positions for multiple units
     */
    private ArrayList<Point> calculateFormationPositions(Point targetDestination, int unitCount) {
        ArrayList<Point> positions = new ArrayList<>();
        
        if (unitCount <= 1) {
            positions.add(targetDestination);
            return positions;
        }
        
        int radius = FormationUtils.calculateFormationRadius(unitCount, 2);
        
        for (int i = 0; i < unitCount; i++) {
            Point formationPos = FormationUtils.calculateFormationPosition(targetDestination, i, unitCount, radius);
            positions.add(formationPos);
        }
        
        return positions;
    }
    
    /**
     * Checks if a unit has reached its destination
     * @param unit The unit to check
     * @param tolerance Distance tolerance for considering destination reached
     * @return true if unit is at destination, false otherwise
     */
    public boolean hasReachedDestination(GameUnit unit, double tolerance) {
        Point current = unit.getCurrentPosition();
        Point destination = unit.getDestination();
        return DistanceUtils.isWithinEuclidean(current, destination, tolerance);
    }
    
    /**
     * Gets the distance between two units
     * @param unit1 First unit
     * @param unit2 Second unit
     * @return Distance between units
     */
    public double getDistanceBetweenUnits(GameUnit unit1, GameUnit unit2) {
        Point pos1 = unit1.getCurrentPosition();
        Point pos2 = unit2.getCurrentPosition();
        return DistanceUtils.euclidean(pos1, pos2);
    }
    
    /**
     * Checks if units are close enough to form a group
     * @param units List of units to check
     * @param maxDistance Maximum distance for grouping
     * @return true if units are close enough to group, false otherwise
     */
    public boolean canFormGroup(ArrayList<GameUnit> units, double maxDistance) {
        if (units.size() <= 1) {
            return true;
        }
        
        for (int i = 0; i < units.size(); i++) {
            for (int j = i + 1; j < units.size(); j++) {
                if (getDistanceBetweenUnits(units.get(i), units.get(j)) > maxDistance) {
                    return false;
                }
            }
        }
        
        return true;
    }
}

