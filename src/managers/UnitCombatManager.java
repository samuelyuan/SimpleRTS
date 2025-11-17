package managers;

import java.util.ArrayList;
import entities.GameUnit;
import graphics.Point;

/**
 * Manages unit combat interactions and combat-related logic.
 */
public class UnitCombatManager {
    
    /**
     * Handles interactions between player units and enemy units
     * @param map The game map
     * @param playerList List of player units
     * @param enemyList List of enemy units
     */
    public void handleUnitInteractions(int[][] map, ArrayList<GameUnit> playerList, ArrayList<GameUnit> enemyList) {
        // Handle player units attacking enemies
        for (GameUnit playerUnit : playerList) {
            if (playerUnit.isAlive()) {
                handleUnitEnemyInteraction(playerUnit, map, enemyList);
            }
        }
        
        // Handle enemy units attacking players
        for (GameUnit enemyUnit : enemyList) {
            if (enemyUnit.isAlive()) {
                handleUnitEnemyInteraction(enemyUnit, map, playerList);
            }
        }
    }
    
    /**
     * Handles interaction between a single unit and a list of potential enemies
     * @param unit The unit performing the interaction
     * @param map The game map
     * @param enemyList The list of potential enemies
     */
    private void handleUnitEnemyInteraction(GameUnit unit, int[][] map, ArrayList<GameUnit> enemyList) {
        boolean canAttackAny = false;
        
        for (GameUnit enemy : enemyList) {
            if (enemy.isAlive() && unit.getCombatSystem().canAttackEnemy(map, enemy)) {
                unit.getCombatSystem().handleAttack(enemy);
                canAttackAny = true;
            }
        }
        
        // Only set isAttacking to false if we can't attack any enemies
        if (!canAttackAny) {
            unit.getCombatSystem().setAttacking(false);
        }
    }
    
    /**
     * Checks if two units can engage in combat
     * @param unit1 First unit
     * @param unit2 Second unit
     * @return true if units can fight, false otherwise
     */
    public boolean canUnitsFight(GameUnit unit1, GameUnit unit2) {
        // Units must be alive
        if (!unit1.isAlive() || !unit2.isAlive()) {
            return false;
        }
        
        // Units must be from different factions
        if (unit1.getFactionId() == unit2.getFactionId()) {
            return false;
        }
        
        // Units must be within combat range
        Point pos1 = unit1.getCurrentPosition();
        Point pos2 = unit2.getCurrentPosition();
        
        double distance = Math.sqrt(Math.pow(pos2.x - pos1.x, 2) + Math.pow(pos2.y - pos1.y, 2));
        
        // Assuming combat range is 1 tile (could be made configurable)
        return distance <= 1.0;
    }
    
    /**
     * Gets all units that can attack a target unit
     * @param targetUnit The unit being targeted
     * @param potentialAttackers List of units that might attack
     * @param map The game map
     * @return List of units that can attack the target
     */
    public ArrayList<GameUnit> getUnitsThatCanAttack(GameUnit targetUnit, ArrayList<GameUnit> potentialAttackers, int[][] map) {
        ArrayList<GameUnit> attackers = new ArrayList<>();
        
        for (GameUnit attacker : potentialAttackers) {
            if (attacker.isAlive() && attacker.getCombatSystem().canAttackEnemy(map, targetUnit)) {
                attackers.add(attacker);
            }
        }
        
        return attackers;
    }
    
    /**
     * Checks if a unit is currently in combat
     * @param unit The unit to check
     * @return true if the unit is attacking, false otherwise
     */
    public boolean isUnitInCombat(GameUnit unit) {
        return unit.getCombatSystem().isAttacking();
    }
    
    /**
     * Stops all combat for a unit
     * @param unit The unit to stop combat for
     */
    public void stopCombat(GameUnit unit) {
        unit.getCombatSystem().setAttacking(false);
    }
}

