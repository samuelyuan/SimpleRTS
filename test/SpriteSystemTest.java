import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import utils.Constants;
import map.TileConverter;

/**
 * Tests for the refactored sprite system constants and mappings.
 * Note: Sprite rendering tests are simplified to avoid complex mocking.
 */
public class SpriteSystemTest {

    @Test
    public void testTileConverterConstants() {
        // Test that the new medium unit constants are properly defined
        assertEquals("Unit Medium Player", TileConverter.STR_UNIT_MEDIUM_PLAYER, 
                "Medium player string constant should be correct");
        assertEquals("Unit Medium Enemy", TileConverter.STR_UNIT_MEDIUM_ENEMY, 
                "Medium enemy string constant should be correct");
    }

    @Test
    public void testTileConverterMapping() {
        // Test that tile string to ID mapping works correctly
        assertEquals(TileConverter.TILE_UNIT_MEDIUM_PLAYER, 
                TileConverter.tileStrToBaseId(TileConverter.STR_UNIT_MEDIUM_PLAYER),
                "Medium player string should map to correct ID");
        assertEquals(TileConverter.TILE_UNIT_MEDIUM_ENEMY, 
                TileConverter.tileStrToBaseId(TileConverter.STR_UNIT_MEDIUM_ENEMY),
                "Medium enemy string should map to correct ID");
    }

    @Test
    public void testTileConverterReverseMapping() {
        // Test that tile ID to string mapping works correctly
        assertEquals(TileConverter.STR_UNIT_MEDIUM_PLAYER, 
                TileConverter.tileIntToStr(TileConverter.TILE_UNIT_MEDIUM_PLAYER),
                "Medium player ID should map to correct string");
        assertEquals(TileConverter.STR_UNIT_MEDIUM_ENEMY, 
                TileConverter.tileIntToStr(TileConverter.TILE_UNIT_MEDIUM_ENEMY),
                "Medium enemy ID should map to correct string");
    }

    @Test
    public void testAllUnitTypeConstants() {
        // Test that all unit type constants are properly defined
        assertEquals(1, Constants.UNIT_ID_LIGHT, "Light unit ID should be 1");
        assertEquals(2, Constants.UNIT_ID_MEDIUM, "Medium unit ID should be 2");
        assertEquals(3, Constants.UNIT_ID_HEAVY, "Heavy unit ID should be 3");
    }

    @Test
    public void testTileConverterCompleteness() {
        // Test that all unit types have corresponding tile constants
        assertNotNull(TileConverter.STR_UNIT_LIGHT_PLAYER, "Light player string should exist");
        assertNotNull(TileConverter.STR_UNIT_LIGHT_ENEMY, "Light enemy string should exist");
        assertNotNull(TileConverter.STR_UNIT_MEDIUM_PLAYER, "Medium player string should exist");
        assertNotNull(TileConverter.STR_UNIT_MEDIUM_ENEMY, "Medium enemy string should exist");
        assertNotNull(TileConverter.STR_UNIT_HEAVY_PLAYER, "Heavy player string should exist");
        assertNotNull(TileConverter.STR_UNIT_HEAVY_ENEMY, "Heavy enemy string should exist");
    }

    @Test
    public void testTileConverterMappingCompleteness() {
        // Test that all unit strings map to valid IDs
        assertNotEquals(-1, TileConverter.tileStrToBaseId(TileConverter.STR_UNIT_LIGHT_PLAYER), 
                "Light player should map to valid ID");
        assertNotEquals(-1, TileConverter.tileStrToBaseId(TileConverter.STR_UNIT_LIGHT_ENEMY), 
                "Light enemy should map to valid ID");
        assertNotEquals(-1, TileConverter.tileStrToBaseId(TileConverter.STR_UNIT_MEDIUM_PLAYER), 
                "Medium player should map to valid ID");
        assertNotEquals(-1, TileConverter.tileStrToBaseId(TileConverter.STR_UNIT_MEDIUM_ENEMY), 
                "Medium enemy should map to valid ID");
        assertNotEquals(-1, TileConverter.tileStrToBaseId(TileConverter.STR_UNIT_HEAVY_PLAYER), 
                "Heavy player should map to valid ID");
        assertNotEquals(-1, TileConverter.tileStrToBaseId(TileConverter.STR_UNIT_HEAVY_ENEMY), 
                "Heavy enemy should map to valid ID");
    }
} 