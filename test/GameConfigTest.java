import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import utils.GameConfig;

/**
 * Tests for the unified GameConfig management system.
 * Focuses on FOV and debugging settings used during development.
 */
public class GameConfigTest {

    @BeforeEach
    public void setUp() {
        // Reset settings to defaults before each test
        GameConfig.resetToDefaults();
    }

    @Test
    public void testDefaultFOVSettings() {
        // Test that FOV is disabled by default
        assertFalse(GameConfig.isFovRenderingEnabled(), "FOV should be disabled by default");
        assertFalse(GameConfig.isFovShowEnemyUnits(), "Enemy FOV should be disabled by default");

    }

    @Test
    public void testDefaultDebugSettings() {
        // Test that debug settings are disabled by default
        assertFalse(GameConfig.isDebugMode(), "Debug mode should be disabled by default");
        assertFalse(GameConfig.isShowFPS(), "FPS display should be disabled by default");
    }

    @Test
    public void testFOVToggleMethods() {
        // Test FOV rendering toggle
        assertFalse(GameConfig.isFovRenderingEnabled(), "Should start disabled");
        GameConfig.toggleFovRendering();
        assertTrue(GameConfig.isFovRenderingEnabled(), "Should be enabled after toggle");
        GameConfig.toggleFovRendering();
        assertFalse(GameConfig.isFovRenderingEnabled(), "Should be disabled after second toggle");

        // Test enemy FOV toggle
        assertFalse(GameConfig.isFovShowEnemyUnits(), "Should start disabled");
        GameConfig.toggleFovShowEnemyUnits();
        assertTrue(GameConfig.isFovShowEnemyUnits(), "Should be enabled after toggle");


    }

    @Test
    public void testFOVSetterMethods() {
        // Test direct setters
        GameConfig.setFovRenderingEnabled(true);
        assertTrue(GameConfig.isFovRenderingEnabled(), "Should be enabled after setter");

        GameConfig.setFovShowEnemyUnits(true);
        assertTrue(GameConfig.isFovShowEnemyUnits(), "Should be enabled after setter");


    }

    @Test
    public void testFOVStatusString() {
        // Test status string when FOV is disabled
        String statusDisabled = GameConfig.getFovStatusString();
        assertEquals("FOV: OFF", statusDisabled, "Status should show FOV OFF when disabled");

        // Test status string when FOV is enabled
        GameConfig.setFovRenderingEnabled(true);
        GameConfig.setFovShowEnemyUnits(true);
        
        String statusEnabled = GameConfig.getFovStatusString();
        assertEquals("FOV: ON | Enemy: ON", statusEnabled, 
                "Status should show FOV settings when enabled");
    }

    @Test
    public void testDebugStatusString() {
        // Test debug status string
        String statusDisabled = GameConfig.getDebugStatusString();
        assertEquals("Debug: OFF | FPS: OFF", statusDisabled, "Status should show debug OFF when disabled");

        // Test when debug is enabled
        GameConfig.setDebugMode(true);
        GameConfig.setShowFPS(true);
        
        String statusEnabled = GameConfig.getDebugStatusString();
        assertEquals("Debug: ON | FPS: ON", statusEnabled, "Status should show debug ON when enabled");
    }

    @Test
    public void testResetToDefaults() {
        // Change some settings
        GameConfig.setFovRenderingEnabled(true);
        GameConfig.setFovShowEnemyUnits(true);
        GameConfig.setDebugMode(true);
        GameConfig.setShowFPS(true);

        // Verify they were changed
        assertTrue(GameConfig.isFovRenderingEnabled());
        assertTrue(GameConfig.isFovShowEnemyUnits());
        assertTrue(GameConfig.isDebugMode());
        assertTrue(GameConfig.isShowFPS());

        // Reset to defaults
        GameConfig.resetToDefaults();

        // Verify they were reset
        assertFalse(GameConfig.isFovRenderingEnabled(), "FOV should be reset to disabled");
        assertFalse(GameConfig.isFovShowEnemyUnits(), "Enemy FOV should be reset to disabled");

        assertFalse(GameConfig.isDebugMode(), "Debug mode should be reset to disabled");
        assertFalse(GameConfig.isShowFPS(), "FPS display should be reset to disabled");
    }
} 