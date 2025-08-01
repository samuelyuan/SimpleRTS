import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.awt.Cursor;
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
        // Mouse near right edge - should scroll right
        int screenWidth = Constants.SCREEN_WIDTH;
        boolean scrolled = cameraManager.handleCameraScrolling(screenWidth - 10, 100);
        
        assertTrue(scrolled, "Should scroll when mouse is near right edge");
        assertEquals(5, cameraManager.getCameraX(), "Should scroll right by SCROLL_AMOUNT");
        assertEquals(0, cameraManager.getCameraY());
    }
    
    @Test
    void testHandleCameraScrollingLeftEdge() {
        // Mouse near left edge - should scroll left
        boolean scrolled = cameraManager.handleCameraScrolling(10, 100);
        
        assertTrue(scrolled, "Should scroll when mouse is near left edge");
        assertEquals(0, cameraManager.getCameraX(), "Should scroll left but be constrained to minimum 0");
        assertEquals(0, cameraManager.getCameraY());
    }
    
    @Test
    void testHandleCameraScrollingBottomEdge() {
        // Mouse near bottom edge - should scroll down
        int screenHeight = Constants.SCREEN_HEIGHT;
        boolean scrolled = cameraManager.handleCameraScrolling(100, screenHeight - 10);
        
        assertTrue(scrolled, "Should scroll when mouse is near bottom edge");
        assertEquals(0, cameraManager.getCameraX());
        assertEquals(5, cameraManager.getCameraY(), "Should scroll down by SCROLL_AMOUNT");
    }
    
    @Test
    void testHandleCameraScrollingTopEdge() {
        // Mouse near top edge - should scroll up
        boolean scrolled = cameraManager.handleCameraScrolling(100, 10);
        
        assertTrue(scrolled, "Should scroll when mouse is near top edge");
        assertEquals(0, cameraManager.getCameraX());
        assertEquals(0, cameraManager.getCameraY(), "Should scroll up but be constrained to minimum 0");
    }
    
    @Test
    void testHandleCameraScrollingCorner() {
        // Mouse in top-right corner - should prioritize horizontal scrolling
        int screenWidth = Constants.SCREEN_WIDTH;
        boolean scrolled = cameraManager.handleCameraScrolling(screenWidth - 10, 10);
        
        assertTrue(scrolled, "Should scroll when mouse is in corner");
        assertEquals(5, cameraManager.getCameraX(), "Should prioritize horizontal scrolling");
        assertEquals(0, cameraManager.getCameraY(), "Should not scroll vertically when horizontal scrolling occurs");
    }
    
    @Test
    void testHorizontalScrollingPriority() {
        // Test that horizontal scrolling takes priority over vertical scrolling
        int screenWidth = Constants.SCREEN_WIDTH;
        int screenHeight = Constants.SCREEN_HEIGHT;
        
        // Position mouse at both edges (should trigger horizontal scrolling only)
        boolean scrolled = cameraManager.handleCameraScrolling(screenWidth - 10, screenHeight - 10);
        
        assertTrue(scrolled, "Should scroll when mouse is at edges");
        assertEquals(5, cameraManager.getCameraX(), "Should scroll horizontally");
        assertEquals(0, cameraManager.getCameraY(), "Should not scroll vertically due to horizontal priority");
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
        
        // Should accumulate scroll amount but respect bounds
        int expectedScroll = 10 * 5; // 10 iterations * SCROLL_AMOUNT
        int maxX = 400 + 5;
        int actualScroll = Math.min(expectedScroll, maxX);
        
        assertEquals(actualScroll, cameraManager.getCameraX(), "Should accumulate scroll but respect bounds");
    }
    
    @Test
    void testScrollingWithZeroCoordinates() {
        // Test scrolling behavior at (0,0)
        boolean scrolled = cameraManager.handleCameraScrolling(0, 0);
        
        assertTrue(scrolled, "Should scroll when mouse is at (0,0)");
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
        assertEquals(5, cameraManager.getCameraX(), "Should scroll right from maximum X");
        assertEquals(0, cameraManager.getCameraY(), "Should not scroll vertically when horizontal scrolling occurs first");
    }
    
    @Test
    void testScrollingFromNonZeroPosition() {
        // Test scrolling when starting from a non-zero position
        cameraManager.setCameraX(10);
        cameraManager.setCameraY(10);
        
        // Scroll left from non-zero position
        boolean scrolled = cameraManager.handleCameraScrolling(10, 100);
        
        assertTrue(scrolled, "Should scroll when mouse is near left edge");
        assertEquals(5, cameraManager.getCameraX(), "Should scroll left by SCROLL_AMOUNT from 10");
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
        assertEquals(0, cameraManager.getCameraX(), "Should not scroll horizontally");
        assertEquals(5, cameraManager.getCameraY(), "Should scroll down by SCROLL_AMOUNT");
    }
    
    // ==================== INTEGRATION TESTS ====================
    
    @Test
    void testFullScrollingCycle() {
        // Test a complete scrolling cycle: right, down, left, up
        int screenWidth = Constants.SCREEN_WIDTH;
        int screenHeight = Constants.SCREEN_HEIGHT;
        
        // Scroll right
        cameraManager.handleCameraScrolling(screenWidth - 10, 100);
        assertEquals(5, cameraManager.getCameraX());
        assertEquals(0, cameraManager.getCameraY());
        
        // Scroll down
        cameraManager.handleCameraScrolling(100, screenHeight - 10);
        assertEquals(5, cameraManager.getCameraX());
        assertEquals(5, cameraManager.getCameraY());
        
        // Scroll left
        cameraManager.handleCameraScrolling(10, 100);
        assertEquals(0, cameraManager.getCameraX());
        assertEquals(5, cameraManager.getCameraY());
        
        // Scroll up
        cameraManager.handleCameraScrolling(100, 10);
        assertEquals(0, cameraManager.getCameraX());
        assertEquals(0, cameraManager.getCameraY());
    }
    
    @Test
    void testScrollingPrecision() {
        // Test that scrolling uses exact SCROLL_AMOUNT values
        int screenWidth = Constants.SCREEN_WIDTH;
        
        // Multiple small scrolls should accumulate precisely
        for (int i = 0; i < 5; i++) {
            cameraManager.handleCameraScrolling(screenWidth - 10, 100);
        }
        
        assertEquals(25, cameraManager.getCameraX(), "Should accumulate exactly 5 * SCROLL_AMOUNT");
    }
} 