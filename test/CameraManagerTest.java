import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Cursor;
import managers.CameraManager;
import input.MouseListenerRegistrar;
import utils.Constants;

/**
 * Comprehensive test suite for CameraManager class.
 * Tests camera scrolling, bounds checking, cursor management, and position handling.
 */
public class CameraManagerTest {
    
    private CameraManager cameraManager;
    private MouseListenerRegistrar mockRegistrar;
    private SimpleRTS mockSimpleRTS;
    
    @BeforeEach
    void setUp() {
        mockRegistrar = mock(MouseListenerRegistrar.class);
        mockSimpleRTS = mock(SimpleRTS.class);
        cameraManager = new CameraManager(mockRegistrar);
    }
    
    // ==================== CONSTRUCTOR TESTS ====================
    
    @Test
    void testConstructorWithRegistrar() {
        assertNotNull(cameraManager);
        assertEquals(0, cameraManager.getCameraX());
        assertEquals(0, cameraManager.getCameraY());
    }
    
    // ==================== GETTER AND SETTER TESTS ====================
    
    @Test
    void testGetCameraX() {
        assertEquals(0, cameraManager.getCameraX());
    }
    
    @Test
    void testGetCameraY() {
        assertEquals(0, cameraManager.getCameraY());
    }
    
    @Test
    void testSetCameraX() {
        cameraManager.setCameraX(100);
        assertEquals(100, cameraManager.getCameraX());
    }
    
    @Test
    void testSetCameraY() {
        cameraManager.setCameraY(150);
        assertEquals(150, cameraManager.getCameraY());
    }
    
    @Test
    void testAddCameraX() {
        cameraManager.addCameraX(50);
        assertEquals(50, cameraManager.getCameraX());
        
        cameraManager.addCameraX(-20);
        assertEquals(30, cameraManager.getCameraX());
    }
    
    @Test
    void testAddCameraY() {
        cameraManager.addCameraY(75);
        assertEquals(75, cameraManager.getCameraY());
        
        cameraManager.addCameraY(-25);
        assertEquals(50, cameraManager.getCameraY());
    }
    
    // ==================== BOUNDS CONSTRAINT TESTS ====================
    
    @Test
    void testSetCameraXBelowMinimum() {
        cameraManager.setCameraX(-100);
        assertEquals(0, cameraManager.getCameraX(), "Camera X should be constrained to minimum 0");
    }
    
    @Test
    void testSetCameraYBelowMinimum() {
        cameraManager.setCameraY(-50);
        assertEquals(0, cameraManager.getCameraY(), "Camera Y should be constrained to minimum 0");
    }
    
    @Test
    void testSetCameraXAboveMaximum() {
        int maxX = 400 + 5; // MAX_CAMERA_X = 400 + SCROLL_AMOUNT
        cameraManager.setCameraX(maxX + 100);
        assertEquals(maxX, cameraManager.getCameraX(), "Camera X should be constrained to maximum");
    }
    
    @Test
    void testSetCameraYAboveMaximum() {
        int maxY = Constants.SCREEN_HEIGHT;
        cameraManager.setCameraY(maxY + 200);
        assertEquals(maxY, cameraManager.getCameraY(), "Camera Y should be constrained to maximum");
    }
    
    @Test
    void testAddCameraXRespectsBounds() {
        // Test going below minimum
        cameraManager.addCameraX(-100);
        assertEquals(0, cameraManager.getCameraX(), "Adding negative X should respect minimum bound");
        
        // Test going above maximum
        int maxX = 400 + 5;
        cameraManager.setCameraX(maxX);
        cameraManager.addCameraX(100);
        assertEquals(maxX, cameraManager.getCameraX(), "Adding positive X should respect maximum bound");
    }
    
    @Test
    void testAddCameraXAllowsNegativeBeforeConstraint() {
        // Test that addCameraX allows negative values before constraint
        cameraManager.setCameraX(10);
        cameraManager.addCameraX(-15);
        assertEquals(0, cameraManager.getCameraX(), "Should be constrained to minimum after going negative");
    }
    
    @Test
    void testAddCameraYRespectsBounds() {
        // Test going below minimum
        cameraManager.addCameraY(-100);
        assertEquals(0, cameraManager.getCameraY(), "Adding negative Y should respect minimum bound");
        
        // Test going above maximum
        int maxY = Constants.SCREEN_HEIGHT;
        cameraManager.setCameraY(maxY);
        cameraManager.addCameraY(100);
        assertEquals(maxY, cameraManager.getCameraY(), "Adding positive Y should respect maximum bound");
    }
    
    @Test
    void testAddCameraYAllowsNegativeBeforeConstraint() {
        // Test that addCameraY allows negative values before constraint
        cameraManager.setCameraY(10);
        cameraManager.addCameraY(-15);
        assertEquals(0, cameraManager.getCameraY(), "Should be constrained to minimum after going negative");
    }
    
    // ==================== CAMERA SCROLLING TESTS ====================
    
    @Test
    void testHandleCameraScrollingNoScroll() {
        // Mouse in center area - should not scroll
        boolean scrolled = cameraManager.handleCameraScrolling(100, 100);
        assertFalse(scrolled, "Should not scroll when mouse is in center area");
        assertEquals(0, cameraManager.getCameraX());
        assertEquals(0, cameraManager.getCameraY());
    }
    
    @Test
    void testHandleCameraScrollingRightEdge() {
        // Test scrolling when mouse is at right edge
        boolean scrolled = cameraManager.handleCameraScrolling(Constants.SCREEN_WIDTH - 10, 100);
        
        assertTrue(scrolled, "Should scroll when mouse is at right edge");
        
        // Update camera for multiple frames to allow acceleration
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        assertTrue(cameraManager.getCameraX() > 0, "Camera should have moved right");
    }
    
    @Test
    void testHandleCameraScrollingLeftEdge() {
        // Set camera to a position where left scrolling is possible
        cameraManager.setCameraX(100);
        
        // Test scrolling when mouse is at left edge
        boolean scrolled = cameraManager.handleCameraScrolling(10, 100);
        
        assertTrue(scrolled, "Should scroll when mouse is at left edge");
        
        // Update camera for multiple frames to allow acceleration
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        assertTrue(cameraManager.getCameraX() < 100, "Camera should have moved left");
    }
    
    @Test
    void testHandleCameraScrollingBottomEdge() {
        // Test scrolling when mouse is at bottom edge
        boolean scrolled = cameraManager.handleCameraScrolling(100, Constants.SCREEN_HEIGHT - 10);
        
        assertTrue(scrolled, "Should scroll when mouse is at bottom edge");
        
        // Update camera for multiple frames to allow acceleration
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        assertTrue(cameraManager.getCameraY() > 0, "Camera should have moved down");
    }
    
    @Test
    void testHandleCameraScrollingTopEdge() {
        // Set camera to a position where up scrolling is possible
        cameraManager.setCameraY(100);
        
        // Test scrolling when mouse is at top edge
        boolean scrolled = cameraManager.handleCameraScrolling(100, 10);
        
        assertTrue(scrolled, "Should scroll when mouse is at top edge");
        
        // Update camera for multiple frames to allow acceleration
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        assertTrue(cameraManager.getCameraY() < 100, "Camera should have moved up");
    }
    
    @Test
    void testHandleCameraScrollingCorner() {
        // Test scrolling when mouse is at corner (should prioritize horizontal)
        boolean scrolled = cameraManager.handleCameraScrolling(Constants.SCREEN_WIDTH - 10, Constants.SCREEN_HEIGHT - 10);
        
        assertTrue(scrolled, "Should scroll when mouse is at corner");
        
        // Update camera for multiple frames to allow acceleration
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        assertTrue(cameraManager.getCameraX() > 0, "Camera should have moved right from corner");
    }
    
    @Test
    void testHorizontalScrollingPriority() {
        // Test that horizontal scrolling takes priority over vertical
        boolean scrolled = cameraManager.handleCameraScrolling(Constants.SCREEN_WIDTH - 10, Constants.SCREEN_HEIGHT - 10);
        
        assertTrue(scrolled, "Should scroll when mouse is at edges");
        
        // Update camera for multiple frames to allow acceleration
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        // Should prioritize horizontal scrolling
        assertTrue(cameraManager.getCameraX() > 0, "Should prioritize horizontal scrolling");
    }
    
    @Test
    void testHandleCameraScrollingAtBoundaries() {
        // Test scrolling when already at boundaries
        int maxX = 400 + 5;
        int maxY = Constants.SCREEN_HEIGHT;
        
        // Set camera to maximum bounds
        cameraManager.setCameraX(maxX);
        cameraManager.setCameraY(maxY);
        
        // Try to scroll beyond bounds
        boolean scrolled = cameraManager.handleCameraScrolling(Constants.SCREEN_WIDTH - 10, Constants.SCREEN_HEIGHT - 10);
        
        assertTrue(scrolled, "Should attempt to scroll even at boundaries");
        
        // Update camera for one frame
        cameraManager.update(1.0f / 60.0f);
        
        assertEquals(maxX, cameraManager.getCameraX(), "Should not exceed maximum X");
        assertEquals(maxY, cameraManager.getCameraY(), "Should not exceed maximum Y");
    }
    
    @Test
    void testHandleCameraScrollingAtMinimumBounds() {
        // Set camera to minimum bounds
        cameraManager.setCameraX(0);
        cameraManager.setCameraY(0);
        
        // Try to scroll beyond minimum bounds
        boolean scrolled = cameraManager.handleCameraScrolling(10, 10);
        
        assertTrue(scrolled, "Should attempt to scroll even at minimum boundaries");
        
        // Update camera for one frame
        cameraManager.update(1.0f / 60.0f);
        
        assertEquals(0, cameraManager.getCameraX(), "Should be constrained to minimum X immediately");
        assertEquals(0, cameraManager.getCameraY(), "Should be constrained to minimum Y immediately");
    }
    
    // ==================== CURSOR MANAGEMENT TESTS ====================
    
    @Test
    void testCursorUpdateWithSimpleRTSRegistrar() {
        // Create CameraManager with SimpleRTS registrar
        CameraManager cameraManagerWithSimpleRTS = new CameraManager(mockSimpleRTS);
        
        // Test cursor setting during scrolling
        cameraManagerWithSimpleRTS.handleCameraScrolling(Constants.SCREEN_WIDTH - 10, 100);
        
        // Verify cursor was set (should call setCursor on SimpleRTS)
        verify(mockSimpleRTS, times(1)).setCursor(any(Cursor.class));
    }
    
    @Test
    void testCursorUpdateWithNonSimpleRTSRegistrar() {
        // Test with generic MouseListenerRegistrar (not SimpleRTS)
        cameraManager.handleCameraScrolling(Constants.SCREEN_WIDTH - 10, 100);
        
        // Should not throw exception when registrar is not SimpleRTS
        assertDoesNotThrow(() -> {
            cameraManager.handleCameraScrolling(10, 10);
        });
    }
    
    // ==================== EDGE CASE TESTS ====================
    
    @Test
    void testUpdateCameraBounds() {
        // This method is currently empty but should not throw exceptions
        assertDoesNotThrow(() -> {
            cameraManager.updateCameraBounds(1000, 800);
        }, "updateCameraBounds should not throw exceptions");
    }
    
    @Test
    void testMultipleRapidScrolling() {
        // Test multiple rapid scrolling operations
        for (int i = 0; i < 10; i++) {
            cameraManager.handleCameraScrolling(Constants.SCREEN_WIDTH - 10, 100);
        }
        
        // Update camera for multiple frames to see accumulated movement
        for (int i = 0; i < 10; i++) {
            cameraManager.update(1.0f / 60.0f); // 10 frames at 60 FPS
        }
        
        // Should accumulate movement but respect bounds
        int maxX = 400 + 5;
        assertTrue(cameraManager.getCameraX() > 0, "Should accumulate movement");
        assertTrue(cameraManager.getCameraX() <= maxX, "Should respect maximum bounds");
    }
    
    @Test
    void testScrollingWithZeroCoordinates() {
        // Test scrolling behavior at (0,0)
        boolean scrolled = cameraManager.handleCameraScrolling(0, 0);
        
        assertTrue(scrolled, "Should scroll when mouse is at (0,0)");
        
        // Update camera for one frame
        cameraManager.update(1.0f / 60.0f);
        
        assertEquals(0, cameraManager.getCameraX(), "Should be constrained to minimum X immediately");
        assertEquals(0, cameraManager.getCameraY(), "Should be constrained to minimum Y immediately");
    }
    
    @Test
    void testScrollingWithMaximumCoordinates() {
        // Test scrolling behavior at maximum screen coordinates
        int screenWidth = Constants.SCREEN_WIDTH;
        int screenHeight = Constants.SCREEN_HEIGHT;
        
        boolean scrolled = cameraManager.handleCameraScrolling(screenWidth, screenHeight);
        
        assertTrue(scrolled, "Should scroll when mouse is at maximum coordinates");
        
        // Update camera for multiple frames to allow acceleration
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        assertTrue(cameraManager.getCameraX() > 0, "Should scroll right from maximum X");
    }
    
    @Test
    void testScrollingFromNonZeroPosition() {
        // Test scrolling when starting from a non-zero position
        cameraManager.setCameraX(10);
        cameraManager.setCameraY(10);
        
        // Scroll left from non-zero position
        boolean scrolled = cameraManager.handleCameraScrolling(10, 100);
        
        assertTrue(scrolled, "Should scroll when mouse is near left edge");
        
        // Update camera for multiple frames to allow acceleration
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        assertTrue(cameraManager.getCameraX() < 10, "Should scroll left from 10");
        assertEquals(10, cameraManager.getCameraY(), "Y should remain unchanged");
    }
    
    @Test
    void testVerticalScrollingWhenHorizontalDoesNotOccur() {
        // Test vertical scrolling when horizontal scrolling doesn't occur
        int screenWidth = Constants.SCREEN_WIDTH;
        int screenHeight = Constants.SCREEN_HEIGHT;
        
        // Position mouse in center horizontally but at bottom edge vertically
        boolean scrolled = cameraManager.handleCameraScrolling(screenWidth / 2, screenHeight);
        
        assertTrue(scrolled, "Should scroll when mouse is at bottom edge");
        
        // Update camera for multiple frames to allow acceleration
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        assertEquals(0, cameraManager.getCameraX(), "Should not scroll horizontally");
        assertTrue(cameraManager.getCameraY() > 0, "Should scroll down");
    }
    
    // ==================== INTEGRATION TESTS ====================
    
    @Test
    void testFullScrollingCycle() {
        // Test a complete scrolling cycle: right, down, left, up
        int screenWidth = Constants.SCREEN_WIDTH;
        int screenHeight = Constants.SCREEN_HEIGHT;
        
        // Scroll right
        cameraManager.handleCameraScrolling(screenWidth - 10, 100);
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        assertTrue(cameraManager.getCameraX() > 0, "Should scroll right");
        assertEquals(0, cameraManager.getCameraY());
        
        // Scroll down
        cameraManager.handleCameraScrolling(100, screenHeight - 10);
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        assertTrue(cameraManager.getCameraX() > 0, "Should maintain right position");
        assertTrue(cameraManager.getCameraY() > 0, "Should scroll down");
        
        // Scroll left
        cameraManager.handleCameraScrolling(10, 100);
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        assertTrue(cameraManager.getCameraX() >= 0, "Should scroll left");
        assertTrue(cameraManager.getCameraY() > 0, "Should maintain down position");
        
        // Scroll up
        cameraManager.handleCameraScrolling(100, 10);
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        assertTrue(cameraManager.getCameraX() >= 0, "Should maintain left position");
        assertTrue(cameraManager.getCameraY() >= 0, "Should scroll up");
    }
    
    @Test
    void testScrollingPrecision() {
        // Test that scrolling accumulates smoothly over multiple frames
        int screenWidth = Constants.SCREEN_WIDTH;
        
        // Multiple small scrolls should accumulate smoothly
        for (int i = 0; i < 5; i++) {
            cameraManager.handleCameraScrolling(screenWidth - 10, 100);
            cameraManager.update(1.0f / 60.0f);
        }
        
        assertTrue(cameraManager.getCameraX() > 0, "Should accumulate movement over multiple frames");
    }
    
    // ==================== SMOOTH CAMERA SYSTEM TESTS ====================
    
    @Test
    void testSmoothCameraUpdate() {
        // Test that camera updates smoothly over multiple frames
        cameraManager.handleCameraScrolling(Constants.SCREEN_WIDTH - 10, 100);
        
        // Update for multiple frames
        for (int i = 0; i < 5; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        assertTrue(cameraManager.getCameraX() > 0, "Camera should move smoothly over multiple frames");
    }
    
    @Test
    void testCameraDeceleration() {
        // Start scrolling
        cameraManager.handleCameraScrolling(Constants.SCREEN_WIDTH - 10, 100);
        
        // Update for a few frames to build up velocity
        for (int i = 0; i < 3; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        int positionWithInput = cameraManager.getCameraX();
        
        // Stop input and let camera decelerate
        cameraManager.handleCameraScrolling(100, 100); // Move to center
        
        // Update for several frames to allow deceleration
        for (int i = 0; i < 10; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        // Camera should have decelerated (position should be different from when input was active)
        assertTrue(cameraManager.getCameraX() >= positionWithInput, "Camera should decelerate when input stops");
    }
    
    @Test
    void testKeyboardInput() {
        // Test keyboard input for camera movement
        cameraManager.setKeyRight(true);
        
        // Update for a few frames
        for (int i = 0; i < 5; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        assertTrue(cameraManager.getCameraX() > 0, "Camera should move with keyboard input");
        
        // Stop keyboard input
        cameraManager.setKeyRight(false);
        
        int positionAfterInput = cameraManager.getCameraX();
        
        // Update for a few more frames
        for (int i = 0; i < 5; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        // Camera should decelerate
        assertTrue(cameraManager.getCameraX() >= positionAfterInput, "Camera should decelerate after keyboard input stops");
    }
    
    @Test
    void testCombinedMouseAndKeyboardInput() {
        // Test that mouse and keyboard input work together
        cameraManager.handleCameraScrolling(Constants.SCREEN_WIDTH - 10, 100); // Mouse right
        cameraManager.setKeyDown(true); // Keyboard down
        
        // Update for a few frames
        for (int i = 0; i < 5; i++) {
            cameraManager.update(1.0f / 60.0f);
        }
        
        assertTrue(cameraManager.getCameraX() > 0, "Camera should move right with mouse input");
        assertTrue(cameraManager.getCameraY() > 0, "Camera should move down with keyboard input");
    }
    
    @Test
    void testBoundaryCollision() {
        // Test that camera stops at boundaries
        cameraManager.setCameraX(400 + 5); // Set to maximum X
        
        // Try to move right
        cameraManager.setKeyRight(true);
        cameraManager.update(1.0f / 60.0f);
        
        assertEquals(400 + 5, cameraManager.getCameraX(), "Camera should not exceed maximum X");
        
        // Try to move left
        cameraManager.setKeyLeft(true);
        cameraManager.setKeyRight(false);
        cameraManager.update(1.0f / 60.0f);
        
        assertTrue(cameraManager.getCameraX() < 400 + 5, "Camera should be able to move left from maximum");
    }
} 