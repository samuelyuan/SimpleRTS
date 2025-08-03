import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import graphics.Point;
import utils.Constants;

/**
 * Tests for the Field of View (FOV) system.
 */
public class FOVSystemTest {

    private GameUnit playerUnit;
    private GameUnit enemyUnit;

    @BeforeEach
    public void setUp() {
        // Reset FOV settings to defaults
        Constants.FOV_RENDERING_ENABLED = true;
        Constants.FOV_SHOW_ENEMY_UNITS = false;
        Constants.FOV_SHOW_SELECTED_ONLY = true;
        
        // Create test units
        playerUnit = new GameUnit(100, 100, true, Constants.UNIT_ID_LIGHT);
        enemyUnit = new GameUnit(200, 100, false, Constants.UNIT_ID_MEDIUM);
    }

    @Test
    public void testFOVToggleSettings() {
        // Test initial state
        assertTrue(Constants.FOV_RENDERING_ENABLED, "FOV should be enabled by default");
        assertFalse(Constants.FOV_SHOW_ENEMY_UNITS, "Enemy FOV should be disabled by default");
        assertTrue(Constants.FOV_SHOW_SELECTED_ONLY, "Selected-only mode should be enabled by default");
        
        // Test toggling
        Constants.FOV_RENDERING_ENABLED = false;
        assertFalse(Constants.FOV_RENDERING_ENABLED, "FOV should be disabled after toggle");
        
        Constants.FOV_SHOW_ENEMY_UNITS = true;
        assertTrue(Constants.FOV_SHOW_ENEMY_UNITS, "Enemy FOV should be enabled after toggle");
        
        Constants.FOV_SHOW_SELECTED_ONLY = false;
        assertFalse(Constants.FOV_SHOW_SELECTED_ONLY, "Selected-only mode should be disabled after toggle");
    }

    @Test
    public void testFOVVisibilityWithRotation() {
        // Set player unit to face east (0 degrees)
        playerUnit.setRotationAngle(0.0);
        
        // Place enemy directly in front (east)
        enemyUnit.setCurrentPosition(new Point(playerUnit.getCurrentPosition().x + 100, playerUnit.getCurrentPosition().y));
        assertTrue(UnitVisibility.isWithinFOV(playerUnit, enemyUnit), 
                "Enemy directly in front should be within FOV");
        
        // Place enemy behind (west)
        enemyUnit.setCurrentPosition(new Point(playerUnit.getCurrentPosition().x - 100, playerUnit.getCurrentPosition().y));
        assertFalse(UnitVisibility.isWithinFOV(playerUnit, enemyUnit), 
                "Enemy behind should not be within FOV");
        
        // Rotate player to face north (270 degrees in game coordinates)
        playerUnit.setRotationAngle(270.0);
        
        // Place enemy north
        enemyUnit.setCurrentPosition(new Point(playerUnit.getCurrentPosition().x, playerUnit.getCurrentPosition().y - 100));
        assertTrue(UnitVisibility.isWithinFOV(playerUnit, enemyUnit), 
                "Enemy north should be within FOV when facing north");
        
        // Place enemy south (behind)
        enemyUnit.setCurrentPosition(new Point(playerUnit.getCurrentPosition().x, playerUnit.getCurrentPosition().y + 100));
        assertFalse(UnitVisibility.isWithinFOV(playerUnit, enemyUnit), 
                "Enemy south should not be within FOV when facing north");
    }

    @Test
    public void testFOVAngleCalculations() {
        Point from = new Point(0, 0);
        Point to = new Point(100, 0); // East
        
        double angle = UnitVisibility.calculateAngleToTarget(from, to);
        assertEquals(0.0, angle, 0.1, "Angle to east should be 0 degrees");
        
        to = new Point(0, 100); // South
        angle = UnitVisibility.calculateAngleToTarget(from, to);
        assertEquals(90.0, angle, 0.1, "Angle to south should be 90 degrees");

        to = new Point(-100, 0); // West
        angle = UnitVisibility.calculateAngleToTarget(from, to);
        assertEquals(180.0, angle, 0.1, "Angle to west should be 180 degrees");
        
        to = new Point(0, -100); // North
        angle = UnitVisibility.calculateAngleToTarget(from, to);
        assertEquals(270.0, angle, 0.1, "Angle to north should be 270 degrees");
    }

    @Test
    public void testFOVEdgeCases() {
        // Test FOV at the exact edge (60 degrees from center)
        playerUnit.setRotationAngle(0.0);
        
        // Calculate position at exactly 60 degrees (edge of 120° FOV)
        double edgeAngle = 60.0;
        double edgeRad = Math.toRadians(edgeAngle);
        int x = playerUnit.getCurrentPosition().x + (int)(100 * Math.cos(edgeRad));
        int y = playerUnit.getCurrentPosition().y + (int)(100 * Math.sin(edgeRad));
        
        enemyUnit.setCurrentPosition(new Point(x, y));
        assertTrue(UnitVisibility.isWithinFOV(playerUnit, enemyUnit), 
                "Enemy at exact FOV edge should be visible");
        
        // Test just outside FOV (61 degrees)
        double outsideAngle = 61.0;
        double outsideRad = Math.toRadians(outsideAngle);
        x = playerUnit.getCurrentPosition().x + (int)(100 * Math.cos(outsideRad));
        y = playerUnit.getCurrentPosition().y + (int)(100 * Math.sin(outsideRad));
        
        enemyUnit.setCurrentPosition(new Point(x, y));
        assertFalse(UnitVisibility.isWithinFOV(playerUnit, enemyUnit), 
                "Enemy just outside FOV should not be visible");
    }

    @Test
    public void testFOVWithAngleWrapping() {
        // Test angle wrapping (e.g., going from 350° to 10°)
        playerUnit.setRotationAngle(350.0); // Facing slightly west of north
        
        // Place enemy at 10 degrees (slightly east of north)
        double targetAngle = 10.0;
        double targetRad = Math.toRadians(targetAngle);
        int x = playerUnit.getCurrentPosition().x + (int)(100 * Math.cos(targetRad));
        int y = playerUnit.getCurrentPosition().y + (int)(100 * Math.sin(targetRad));
        
        enemyUnit.setCurrentPosition(new Point(x, y));
        assertTrue(UnitVisibility.isWithinFOV(playerUnit, enemyUnit), 
                "Enemy should be visible when angle wrapping occurs");
    }
} 