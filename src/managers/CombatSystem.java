package managers;

import entities.GameUnit;
import graphics.Point;
import utils.Constants;
import utils.TileCoordinateConverter;

/**
 * Handles all combat-related functionality for game units.
 * This includes attack logic, damage calculation, and combat state management.
 */
public class CombatSystem {
    private boolean isAttacking = false;
    private int lastDamageDealt = 0;
    private boolean wasCriticalHit = false;
    private GameUnit owner;
    
    public CombatSystem(GameUnit owner) {
        this.owner = owner;
    }
    
    /**
     * Checks if the unit is currently attacking
     */
    public boolean isAttacking() {
        return this.isAttacking;
    }
    
    /**
     * Sets the attacking state
     */
    public void setAttacking(boolean attacking) {
        this.isAttacking = attacking;
    }
    
    /**
     * Gets the last damage dealt by this unit
     */
    public int getLastDamageDealt() {
        return lastDamageDealt;
    }
    
    /**
     * Checks if the last hit was critical
     */
    public boolean wasLastHitCritical() {
        return wasCriticalHit;
    }
    
    /**
     * Clears combat effects (damage dealt, critical hit status)
     */
    public void clearCombatEffects() {
        lastDamageDealt = 0;
        wasCriticalHit = false;
    }
    
    /**
     * Checks if this unit can attack the given enemy
     */
    public boolean canAttackEnemy(int[][] map, GameUnit enemy) {
        final int ATTACK_RADIUS = 8;
        int manhattanDist = TileCoordinateConverter.manhattanDistanceInTiles(
            owner.getCurrentPosition(), enemy.getCurrentPosition());
        // Now includes FOV check - units can only attack enemies they can see within
        // their field of view
        return manhattanDist <= ATTACK_RADIUS && UnitVisibility.checkVisible(map, owner, enemy);
    }
    
    /**
     * Handles an attack between this unit and an enemy
     */
    public void handleAttack(GameUnit enemy) {
        isAttacking = true;

        // Rotate to face the enemy being attacked
        rotateToFaceTarget(enemy);

        // Make enemy rotate to face back (for counter-attack)
        enemy.getCombatSystem().rotateToFaceTarget(owner);

        // Calculate damage
        int damageToEnemy = dealDamagePoints(enemy);
        int damageToSelf = enemy.getCombatSystem().dealDamagePoints(owner);

        // Apply damage
        owner.setHealth(owner.getHealth() - damageToSelf);
        enemy.setHealth(enemy.getHealth() - damageToEnemy);

        // Notify enemy that it took damage (so it can turn to face attacker)
        enemy.getCombatSystem().onTakeDamage(owner);

        // Check for critical hits (10% chance)
        boolean isCriticalHit = Math.random() < 0.1;
        if (isCriticalHit) {
            damageToEnemy = (int) (damageToEnemy * 1.5); // 50% bonus damage
            enemy.setHealth(enemy.getHealth() - (int) (damageToEnemy * 0.5)); // Apply bonus damage
        }

        // Store damage info for combat effects
        this.lastDamageDealt = damageToEnemy;
        this.wasCriticalHit = isCriticalHit;
        enemy.getCombatSystem().lastDamageDealt = damageToSelf;
        enemy.getCombatSystem().wasCriticalHit = false; // Enemy doesn't get critical hits for now
    }
    
    /**
     * Calculates damage points based on unit types
     */
    public int dealDamagePoints(GameUnit enemy) {
        int attacker = owner.getClassType() - 1; // UNIT_ID_LIGHT = 1 → index 0
        int defender = enemy.getClassType() - 1;

        if (attacker < 0 || attacker >= Constants.DAMAGE_MATRIX.length ||
                defender < 0 || defender >= Constants.DAMAGE_MATRIX[0].length) {
            return 1; // fallback value
        }

        return Constants.DAMAGE_MATRIX[attacker][defender];
    }
    
    /**
     * Rotates the unit to face a target unit.
     * This is used during combat to ensure units face their enemies.
     */
    public void rotateToFaceTarget(GameUnit target) {
        Point myPos = owner.getCurrentPosition();
        Point targetPos = target.getCurrentPosition();

        // Calculate angle to target
        double deltaX = targetPos.x - myPos.x;
        double deltaY = targetPos.y - myPos.y;
        double angleToTarget = Math.toDegrees(Math.atan2(deltaY, deltaX));

        // Normalize angle to 0-360 range
        if (angleToTarget < 0) {
            angleToTarget += 360.0;
        }

        // Set target rotation angle for smooth rotation
        owner.setTargetRotationAngle(angleToTarget);
    }
    
    /**
     * Called when this unit takes damage from an attacker.
     * This can be used to make the unit turn to face the attacker,
     * even if it can't currently attack back (e.g., due to FOV).
     */
    public void onTakeDamage(GameUnit attacker) {
        // Rotate to face the attacker (even if we can't attack back)
        rotateToFaceTarget(attacker);
    }
}

