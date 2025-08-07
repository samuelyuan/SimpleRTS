import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import utils.Constants;

public class CoordinateTransformerTest {
    
    private static final int GAME_WIDTH = Constants.SCREEN_WIDTH;  // 1076
    private static final int GAME_HEIGHT = Constants.SCREEN_HEIGHT; // 768
    
    @Test
    void testScreenToGameExactGameSize() {
        // Screen coordinates to game with exact panel size
        CoordinateTransformer.GameCoordinates result = CoordinateTransformer.screenToGame(100, 200, GAME_WIDTH, GAME_HEIGHT);
        
        assertNotNull(result, "Should return valid game coordinates");
        assertEquals(100, result.x, "Game X should match screen X");
        assertEquals(200, result.y, "Game Y should match screen Y");
        assertEquals(1.0, result.scale, 0.001, "Scale should be 1.0");
    }
    
    @Test
    void testScreenToGameOutsideBounds() {
        // Test coordinates outside the game area
        CoordinateTransformer.GameCoordinates result = CoordinateTransformer.screenToGame(-100, -100, GAME_WIDTH, GAME_HEIGHT);
        
        assertNull(result, "Should return null for coordinates outside game area");
    }
    
    @Test
    void testScreenToGameAtBoundaries() {
        // Test exactly at the boundaries
        CoordinateTransformer.GameCoordinates result1 = CoordinateTransformer.screenToGame(0, 0, GAME_WIDTH, GAME_HEIGHT);
        CoordinateTransformer.GameCoordinates result2 = CoordinateTransformer.screenToGame(GAME_WIDTH - 1, GAME_HEIGHT - 1, GAME_WIDTH, GAME_HEIGHT);
        
        assertNotNull(result1, "Should return valid coordinates at (0,0)");
        assertEquals(0, result1.x, "X should be 0 at left boundary");
        assertEquals(0, result1.y, "Y should be 0 at top boundary");
        
        assertNotNull(result2, "Should return valid coordinates at bottom-right boundary");
        assertEquals(GAME_WIDTH - 1, result2.x, "X should be at right boundary");
        assertEquals(GAME_HEIGHT - 1, result2.y, "Y should be at bottom boundary");
    }
    
    @Test
    void testGameToScreenExactGameSize() {
        // Game coordinates to screen with exact panel size
        CoordinateTransformer.ScreenCoordinates result = CoordinateTransformer.gameToScreen(100, 200, GAME_WIDTH, GAME_HEIGHT);
        
        assertNotNull(result, "Should return valid screen coordinates");
        assertEquals(100, result.x, "Screen X should match game X");
        assertEquals(200, result.y, "Screen Y should match game Y");
        assertEquals(1.0, result.scale, 0.001, "Scale should be 1.0");
    }
    
    @Test
    void testRoundTripTransformation() {
        // Test that screen->game->screen gives same result
        int originalScreenX = 500;
        int originalScreenY = 300;
        int panelWidth = GAME_WIDTH;
        int panelHeight = GAME_HEIGHT;
        
        CoordinateTransformer.GameCoordinates gameCoords = CoordinateTransformer.screenToGame(originalScreenX, originalScreenY, panelWidth, panelHeight);
        assertNotNull(gameCoords, "Screen to game should succeed");
        
        CoordinateTransformer.ScreenCoordinates screenCoords = CoordinateTransformer.gameToScreen(gameCoords.x, gameCoords.y, panelWidth, panelHeight);
        assertNotNull(screenCoords, "Game to screen should succeed");
        
        assertEquals(originalScreenX, screenCoords.x, "Round trip should preserve X coordinate");
        assertEquals(originalScreenY, screenCoords.y, "Round trip should preserve Y coordinate");
    }
    
    @Test
    void testGetScaleExactMatch() {
        double scale = CoordinateTransformer.getScale(GAME_WIDTH, GAME_HEIGHT);
        assertEquals(1.0, scale, 0.001, "Scale should be 1.0 for exact match");
    }
    
    @Test
    void testGetGameAreaBoundsExactMatch() {
        CoordinateTransformer.GameAreaBounds bounds = CoordinateTransformer.getGameAreaBounds(GAME_WIDTH, GAME_HEIGHT);
        
        assertEquals(0, bounds.offsetX, "Offset X should be 0 for exact match");
        assertEquals(0, bounds.offsetY, "Offset Y should be 0 for exact match");
        assertEquals(GAME_WIDTH, bounds.width, "Width should match game width");
        assertEquals(GAME_HEIGHT, bounds.height, "Height should match game height");
        assertEquals(1.0, bounds.scale, 0.001, "Scale should be 1.0");
    }
    
    @Test
    void testEdgeCases() {
        // Test with zero dimensions - this should cause division by zero or other issues
        try {
            CoordinateTransformer.GameCoordinates result = CoordinateTransformer.screenToGame(0, 0, 0, 0);
            // If we get here, the method handled it gracefully
        } catch (Exception e) {
            // Expected behavior for invalid input
        }
        
        // Test with negative dimensions - this should cause issues
        try {
            CoordinateTransformer.GameCoordinates result = CoordinateTransformer.screenToGame(0, 0, -100, -100);
            // If we get here, the method handled it gracefully
        } catch (Exception e) {
            // Expected behavior for invalid input
        }
    }
    
    @Test
    void testContainerClasses() {
        // Test that container classes work correctly
        CoordinateTransformer.GameCoordinates gameCoords = new CoordinateTransformer.GameCoordinates(100, 200, 1.0);
        assertEquals(100, gameCoords.x);
        assertEquals(200, gameCoords.y);
        assertEquals(1.0, gameCoords.scale, 0.001);
        
        CoordinateTransformer.ScreenCoordinates screenCoords = new CoordinateTransformer.ScreenCoordinates(300, 400, 0.5);
        assertEquals(300, screenCoords.x);
        assertEquals(400, screenCoords.y);
        assertEquals(0.5, screenCoords.scale, 0.001);
        
        CoordinateTransformer.GameAreaBounds bounds = new CoordinateTransformer.GameAreaBounds(50, 60, 100, 200, 1.0);
        assertEquals(50, bounds.offsetX);
        assertEquals(60, bounds.offsetY);
        assertEquals(100, bounds.width);
        assertEquals(200, bounds.height);
        assertEquals(1.0, bounds.scale, 0.001);
    }
} 