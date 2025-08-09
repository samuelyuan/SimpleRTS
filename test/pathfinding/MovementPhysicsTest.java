package pathfinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import graphics.Point;

/**
 * Comprehensive test suite for MovementPhysics class.
 */
@DisplayName("MovementPhysics Tests")
public class MovementPhysicsTest {

    private MovementPhysics physics;

    @BeforeEach
    void setUp() {
        physics = new MovementPhysics(10.0, 20.0);
    }

    @Test
    @DisplayName("Constructor should initialize MovementPhysics correctly")
    void testConstructor() {
        MovementPhysics newPhysics = new MovementPhysics(15.0, 25.0);
        
        assertEquals(15.0, newPhysics.getCurrentX(), 0.001, "X position should be initialized correctly");
        assertEquals(25.0, newPhysics.getCurrentY(), 0.001, "Y position should be initialized correctly");
        assertEquals(1.5, newPhysics.getMaxVelocity(), 0.001, "Default max velocity should be 1.5");
        assertEquals(1.0, newPhysics.getMaxForce(), 0.001, "Default max force should be 1.0");
        assertEquals(1.0, newPhysics.getMass(), 0.001, "Default mass should be 1.0");
    }

    @Test
    @DisplayName("Constructor with custom properties should set physics properties correctly")
    void testConstructorWithCustomProperties() {
        MovementPhysics customPhysics = new MovementPhysics(5.0, 10.0, 2.5, 1.5, 2.0);
        
        assertEquals(5.0, customPhysics.getCurrentX(), 0.001, "X position should be initialized correctly");
        assertEquals(10.0, customPhysics.getCurrentY(), 0.001, "Y position should be initialized correctly");
        assertEquals(2.5, customPhysics.getMaxVelocity(), 0.001, "Custom max velocity should be set correctly");
        assertEquals(1.5, customPhysics.getMaxForce(), 0.001, "Custom max force should be set correctly");
        assertEquals(2.0, customPhysics.getMass(), 0.001, "Custom mass should be set correctly");
    }

    @Test
    @DisplayName("getCurrentPosition should return correct Point")
    void testGetCurrentPosition() {
        Point position = physics.getCurrentPosition();
        
        assertEquals(10, position.x, "X coordinate should match current X position");
        assertEquals(20, position.y, "Y coordinate should match current Y position");
    }

    @Test
    @DisplayName("setPosition should update position correctly")
    void testSetPosition() {
        physics.setPosition(30.0, 40.0);
        
        assertEquals(30.0, physics.getCurrentX(), 0.001, "X position should be updated");
        assertEquals(40.0, physics.getCurrentY(), 0.001, "Y position should be updated");
    }

    @Test
    @DisplayName("setPhysicsProperties should update physics properties correctly")
    void testSetPhysicsProperties() {
        physics.setPhysicsProperties(3.0, 2.0, 1.5);
        
        assertEquals(3.0, physics.getMaxVelocity(), 0.001, "Max velocity should be updated");
        assertEquals(2.0, physics.getMaxForce(), 0.001, "Max force should be updated");
        assertEquals(1.5, physics.getMass(), 0.001, "Mass should be updated");
    }

    @Test
    @DisplayName("updatePosition should move towards target")
    void testUpdatePosition() {
        double initialX = physics.getCurrentX();
        double initialY = physics.getCurrentY();
        
        // Update position towards a target
        physics.updatePosition(initialX + 10.0, initialY + 10.0);
        
        // Should have moved towards the target
        assertTrue(physics.getCurrentX() > initialX, "Should have moved towards target X");
        assertTrue(physics.getCurrentY() > initialY, "Should have moved towards target Y");
    }

    @Test
    @DisplayName("updatePosition should respect max velocity")
    void testUpdatePositionRespectsMaxVelocity() {
        // Set a very low max velocity
        physics.setPhysicsProperties(0.1, 1.0, 1.0);
        
        double initialX = physics.getCurrentX();
        double initialY = physics.getCurrentY();
        
        // Try to move to a far target
        physics.updatePosition(initialX + 100.0, initialY + 100.0);
        
        // Should not have moved more than max velocity
        double distanceMoved = MovementPhysics.getDistance(initialX, initialY, 
                                                          physics.getCurrentX(), physics.getCurrentY());
        assertTrue(distanceMoved <= 0.1, "Should not exceed max velocity");
    }

    

    @Test
    @DisplayName("getDistance should calculate distance correctly")
    void testGetDistance() {
        double distance = MovementPhysics.getDistance(0.0, 0.0, 3.0, 4.0);
        assertEquals(5.0, distance, 0.001, "Distance should be calculated correctly (3-4-5 triangle)");
    }

    @Test
    @DisplayName("getDistance should return 0 for same points")
    void testGetDistanceSamePoints() {
        double distance = MovementPhysics.getDistance(10.0, 20.0, 10.0, 20.0);
        assertEquals(0.0, distance, 0.001, "Distance should be 0 for same points");
    }

    @Test
    @DisplayName("Physics should handle zero mass gracefully")
    void testZeroMass() {
        physics.setPhysicsProperties(1.0, 1.0, 0.0);
        
        // Should not throw exception
        assertDoesNotThrow(() -> {
            physics.updatePosition(physics.getCurrentX() + 10.0, physics.getCurrentY() + 10.0);
        }, "Should handle zero mass without throwing exception");
    }

    @Test
    @DisplayName("Physics should handle negative coordinates")
    void testNegativeCoordinates() {
        physics.setPosition(-10.0, -20.0);
        
        assertEquals(-10.0, physics.getCurrentX(), 0.001, "Should handle negative X coordinate");
        assertEquals(-20.0, physics.getCurrentY(), 0.001, "Should handle negative Y coordinate");
        
        // Should be able to move from negative coordinates
        assertDoesNotThrow(() -> {
            physics.updatePosition(-5.0, -15.0);
        }, "Should handle movement from negative coordinates");
    }
}
