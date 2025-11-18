import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import managers.CameraManager;
import java.awt.event.KeyEvent;
import java.awt.Component;

/**
 * Test suite for KeyboardHandler class.
 * Tests keyboard input handling for camera movement.
 */
public class KeyboardHandlerTest {
    
    private KeyboardHandler keyboardHandler;
    private CameraManager mockCameraManager;
    private Component mockComponent;
    
    @BeforeEach
    void setUp() {
        mockCameraManager = mock(CameraManager.class);
        mockComponent = mock(Component.class);
        keyboardHandler = new KeyboardHandler(mockCameraManager);
    }
    
    @Test
    void testConstructor() {
        assertNotNull(keyboardHandler);
    }
    
    @Test
    void testKeyPressedRightArrow() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, 'R');
        keyboardHandler.keyPressed(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyRight(true);
    }
    
    @Test
    void testKeyPressedDKey() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_D, 'D');
        keyboardHandler.keyPressed(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyRight(true);
    }
    
    @Test
    void testKeyPressedLeftArrow() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, 'L');
        keyboardHandler.keyPressed(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyLeft(true);
    }
    
    @Test
    void testKeyPressedAKey() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_A, 'A');
        keyboardHandler.keyPressed(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyLeft(true);
    }
    
    @Test
    void testKeyPressedDownArrow() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, 'D');
        keyboardHandler.keyPressed(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyDown(true);
    }
    
    @Test
    void testKeyPressedSKey() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_S, 'S');
        keyboardHandler.keyPressed(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyDown(true);
    }
    
    @Test
    void testKeyPressedUpArrow() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, 'U');
        keyboardHandler.keyPressed(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyUp(true);
    }
    
    @Test
    void testKeyPressedWKey() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_W, 'W');
        keyboardHandler.keyPressed(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyUp(true);
    }
    
    @Test
    void testKeyReleasedRightArrow() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, 'R');
        keyboardHandler.keyReleased(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyRight(false);
    }
    
    @Test
    void testKeyReleasedDKey() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_D, 'D');
        keyboardHandler.keyReleased(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyRight(false);
    }
    
    @Test
    void testKeyReleasedLeftArrow() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_LEFT, 'L');
        keyboardHandler.keyReleased(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyLeft(false);
    }
    
    @Test
    void testKeyReleasedAKey() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_A, 'A');
        keyboardHandler.keyReleased(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyLeft(false);
    }
    
    @Test
    void testKeyReleasedDownArrow() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_DOWN, 'D');
        keyboardHandler.keyReleased(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyDown(false);
    }
    
    @Test
    void testKeyReleasedSKey() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_S, 'S');
        keyboardHandler.keyReleased(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyDown(false);
    }
    
    @Test
    void testKeyReleasedUpArrow() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, 'U');
        keyboardHandler.keyReleased(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyUp(false);
    }
    
    @Test
    void testKeyReleasedWKey() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, KeyEvent.VK_W, 'W');
        keyboardHandler.keyReleased(keyEvent);
        
        verify(mockCameraManager, times(1)).setKeyUp(false);
    }
    
    @Test
    void testUnhandledKey() {
        KeyEvent keyEvent = new KeyEvent(mockComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_SPACE, ' ');
        keyboardHandler.keyPressed(keyEvent);
        
        // Should not call any camera manager methods for unhandled keys
        verify(mockCameraManager, never()).setKeyRight(anyBoolean());
        verify(mockCameraManager, never()).setKeyLeft(anyBoolean());
        verify(mockCameraManager, never()).setKeyDown(anyBoolean());
        verify(mockCameraManager, never()).setKeyUp(anyBoolean());
    }
    
    @Test
    void testMultipleKeyPresses() {
        // Press multiple keys
        KeyEvent rightKey = new KeyEvent(mockComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_RIGHT, 'R');
        KeyEvent upKey = new KeyEvent(mockComponent, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, KeyEvent.VK_UP, 'U');
        
        keyboardHandler.keyPressed(rightKey);
        keyboardHandler.keyPressed(upKey);
        
        verify(mockCameraManager, times(1)).setKeyRight(true);
        verify(mockCameraManager, times(1)).setKeyUp(true);
    }
} 