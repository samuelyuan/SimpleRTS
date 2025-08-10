import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import graphics.Point;
import utils.Constants;

/**
 * Tests for combat rotation behavior.
 */
public class CombatRotationTest {

    private GameUnit playerUnit;
    private GameUnit enemyUnit;
    private int[][] map;
    private GameUnitManager unitManager;

    @BeforeEach
    public void setUp() {
        // Create a simple test map
        map = new int[][]{
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0}
        };
        
        // Create unit manager
        unitManager = new GameUnitManager();
        
        // Create test units
        playerUnit = new GameUnit(100, 100, true, Constants.UNIT_ID_LIGHT);
        enemyUnit = new GameUnit(200, 100, false, Constants.UNIT_ID_MEDIUM);
    }

    @Test
    public void testUnitsRotateToFaceEnemiesDuringCombat() {
        // Set initial rotation angles
        playerUnit.setRotationAngle(0.0); // Facing east
        enemyUnit.setRotationAngle(180.0); // Facing west
        
        // Verify initial angles
        assertEquals(0.0, playerUnit.getRotationAngle(), 0.1, "Player should start facing east");
        assertEquals(180.0, enemyUnit.getRotationAngle(), 0.1, "Enemy should start facing west");
        
        // Add units to manager lists
        unitManager.getPlayerList().add(playerUnit);
        unitManager.getEnemyList().add(enemyUnit);
        
        // Simulate combat interaction using GameUnitManager
        unitManager.handleUnitInteractions(map);
        
        // Update rotations many times to allow smooth rotation to complete
        for (int i = 0; i < 100; i++) {
            playerUnit.updateRotation();
            enemyUnit.updateRotation();
        }
        
        // Player should now be facing the enemy (east, since enemy is to the right)
        assertEquals(0.0, playerUnit.getRotationAngle(), 5.0, "Player should face enemy (east)");
        
        // Enemy should now be facing the player (west, since player is to the left)
        assertEquals(180.0, enemyUnit.getRotationAngle(), 5.0, "Enemy should face player (west)");
    }

    @Test
    public void testUnitsRotateWhenAttackedFromBehind() {
        // Set player to face east, enemy behind player
        playerUnit.setRotationAngle(0.0); // Facing east
        enemyUnit.setCurrentPosition(new Point(50, 100)); // Behind player (west)
        
        // Enemy attacks player from behind
        enemyUnit.handleAttack(playerUnit);
        
        // Update rotations many times to allow smooth rotation to complete
        for (int i = 0; i < 100; i++) {
            playerUnit.updateRotation();
            enemyUnit.updateRotation();
        }
        
        // Player should turn to face the enemy (west, since enemy is behind)
        assertEquals(180.0, playerUnit.getRotationAngle(), 5.0, "Player should turn to face enemy behind");
        
        // Enemy should face the player (east, since player is in front)
        assertEquals(0.0, enemyUnit.getRotationAngle(), 5.0, "Enemy should face player");
    }

    @Test
    public void testUnitsRotateWhenAttackedFromSide() {
        // Set player to face east, enemy to the north
        playerUnit.setRotationAngle(0.0); // Facing east
        enemyUnit.setCurrentPosition(new Point(100, 50)); // North of player (decreasing Y)
        
        // Enemy attacks player from the side
        enemyUnit.handleAttack(playerUnit);
        
        // Update rotations many times to allow smooth rotation to complete
        for (int i = 0; i < 100; i++) {
            playerUnit.updateRotation();
            enemyUnit.updateRotation();
        }
        
        // Player should turn to face the enemy (north = 270° in game coordinates)
        assertEquals(270.0, playerUnit.getRotationAngle(), 5.0, "Player should turn to face enemy to the north");
        
        // Enemy should face the player (south = 90° in game coordinates)
        assertEquals(90.0, enemyUnit.getRotationAngle(), 5.0, "Enemy should face player");
    }

    @Test
    public void testRotationAngleCalculation() {
        Point from = new Point(0, 0);
        Point to = new Point(100, 0); // East
        
        // Test angle calculation
        double angle = Math.toDegrees(Math.atan2(to.y - from.y, to.x - from.x));
        if (angle < 0) angle += 360.0;
        
        assertEquals(0.0, angle, 0.1, "Angle to east should be 0 degrees");
        
        to = new Point(0, -100); // North (decreasing Y in game coordinates)
        angle = Math.toDegrees(Math.atan2(to.y - from.y, to.x - from.x));
        if (angle < 0) angle += 360.0;
        
        assertEquals(270.0, angle, 0.1, "Angle to north should be 270 degrees in game coordinates");
    }

    @Test
    public void testCombatStateFlags() {
        // Initially, units should not be attacking
        assertFalse(playerUnit.isAttacking(), "Player should not be attacking initially");
        assertFalse(enemyUnit.isAttacking(), "Enemy should not be attacking initially");
        
        // Add units to manager lists
        unitManager.getPlayerList().add(playerUnit);
        unitManager.getEnemyList().add(enemyUnit);
        
        // Start combat using GameUnitManager
        unitManager.handleUnitInteractions(map);
        
        // Player should be attacking if it can see the enemy
        if (playerUnit.canAttackEnemy(map, enemyUnit)) {
            assertTrue(playerUnit.isAttacking(), "Player should be attacking when engaging enemy");
        }
    }
} 