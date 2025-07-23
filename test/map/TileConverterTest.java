package map;

import static map.TileConverter.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TileConverterTest {

    @Test
    public void testTileStrToBaseId_Land() {
        assertEquals(TILE_LAND, tileStrToBaseId("Land"));
    }

    @Test
    public void testTileStrToBaseId_Wall() {
        assertEquals(TILE_WALL, tileStrToBaseId("Wall"));
    }

    @Test
    public void testTileStrToBaseId_UnitLightPlayer() {
        assertEquals(TILE_UNIT_LIGHT_PLAYER, tileStrToBaseId("Unit Light Player"));
    }

    @Test
    public void testTileStrToBaseId_UnitMedium() {
        assertEquals(TILE_UNIT_MEDIUM, tileStrToBaseId("Unit Medium"));
    }

    @Test
    public void testTileStrToBaseId_UnitHeavyPlayer() {
        assertEquals(TILE_UNIT_HEAVY_PLAYER, tileStrToBaseId("Unit Heavy Player"));
    }

    @Test
    public void testTileStrToBaseId_UnitLightEnemy() {
        assertEquals(TILE_UNIT_LIGHT_ENEMY, tileStrToBaseId("Unit Light Enemy"));
    }

    @Test
    public void testTileStrToBaseId_UnitHeavyEnemy() {
        assertEquals(TILE_UNIT_HEAVY_ENEMY, tileStrToBaseId("Unit Heavy Enemy"));
    }

    @Test
    public void testTileStrToBaseId_Flag() {
        assertEquals(TILE_FLAG_ALLY, tileStrToBaseId("Flag"));
    }

    @Test
    public void testTileStrToBaseId_Invalid() {
        assertEquals(-1, tileStrToBaseId("UnknownThing"));
    }

    @Test
    public void testTileIntToStr_ValidIds() {
        assertEquals("Land", tileIntToStr(TILE_LAND));
        assertEquals("Wall", tileIntToStr(TILE_WALL));
        assertEquals("Unit Light Player", tileIntToStr(TILE_UNIT_LIGHT_PLAYER));
        assertEquals("Unit Medium", tileIntToStr(TILE_UNIT_MEDIUM));
        assertEquals("Unit Medium", tileIntToStr(TILE_UNIT_MEDIUM_ALT)); // maps to same string
        assertEquals("Unit Heavy Player", tileIntToStr(TILE_UNIT_HEAVY_PLAYER));
        assertEquals("Unit Light Enemy", tileIntToStr(TILE_UNIT_LIGHT_ENEMY));
        assertEquals("Unit Heavy Enemy", tileIntToStr(TILE_UNIT_HEAVY_ENEMY));
        assertEquals("Flag", tileIntToStr(TILE_FLAG_ALLY));
        assertEquals("Flag", tileIntToStr(TILE_FLAG_ENEMY));
    }

    @Test
    public void testTileIntToStr_InvalidId() {
        assertEquals("", tileIntToStr(99));
    }

    @Test
    public void testRoundTrip() {
        String[] tileNames = {
            "Land",
            "Wall",
            "Unit Light Player",
            "Unit Medium",
            "Unit Heavy Player",
            "Unit Light Enemy",
            "Unit Heavy Enemy",
            "Flag"
        };

        for (String name : tileNames) {
            int id = tileStrToBaseId(name);
            String result = tileIntToStr(id);
            assertEquals(name, result, "Roundtrip failed for: " + name);
        }
    }
}
