package map;

public class TileConverter {
    public static final int TILE_LAND = 0;
    public static final int TILE_WALL = 1;
    public static final int TILE_UNIT_LIGHT_PLAYER = 2;
    public static final int TILE_UNIT_MEDIUM = 3;
    public static final int TILE_UNIT_HEAVY_PLAYER = 4;
    public static final int TILE_UNIT_LIGHT_ENEMY = 5;
    public static final int TILE_UNIT_MEDIUM_ALT = 6; // optional alt mapping
    public static final int TILE_UNIT_HEAVY_ENEMY = 7;
    public static final int TILE_FLAG_ALLY = 8;
    public static final int TILE_FLAG_ENEMY = 9;

    // String constants for tile types
    public static final String STR_LAND = "Land";
    public static final String STR_WALL = "Wall";
    public static final String STR_UNIT_LIGHT_PLAYER = "Unit Light Player";
    public static final String STR_UNIT_MEDIUM = "Unit Medium";
    public static final String STR_UNIT_HEAVY_PLAYER = "Unit Heavy Player";
    public static final String STR_UNIT_LIGHT_ENEMY = "Unit Light Enemy";
    public static final String STR_UNIT_HEAVY_ENEMY = "Unit Heavy Enemy";
    public static final String STR_FLAG = "Flag";

    public static int tileStrToBaseId(String tileStr) {
        if (tileStr.contains(STR_LAND)) return TILE_LAND;
        if (tileStr.contains(STR_WALL)) return TILE_WALL;
        if (tileStr.contains(STR_FLAG)) return TILE_FLAG_ALLY; // default fallback
        if (tileStr.contains(STR_UNIT_LIGHT_PLAYER)) return TILE_UNIT_LIGHT_PLAYER;
        if (tileStr.contains(STR_UNIT_MEDIUM)) return TILE_UNIT_MEDIUM;
        if (tileStr.contains(STR_UNIT_HEAVY_PLAYER)) return TILE_UNIT_HEAVY_PLAYER;
        if (tileStr.contains(STR_UNIT_LIGHT_ENEMY)) return TILE_UNIT_LIGHT_ENEMY;
        if (tileStr.contains(STR_UNIT_HEAVY_ENEMY)) return TILE_UNIT_HEAVY_ENEMY;
        return -1;
    }

    public static String tileIntToStr(int tileId) {
        switch (tileId) {
            case TILE_LAND: return STR_LAND;
            case TILE_WALL: return STR_WALL;
            case TILE_UNIT_LIGHT_PLAYER: return STR_UNIT_LIGHT_PLAYER;
            case TILE_UNIT_MEDIUM:
            case TILE_UNIT_MEDIUM_ALT: return STR_UNIT_MEDIUM;
            case TILE_UNIT_HEAVY_PLAYER: return STR_UNIT_HEAVY_PLAYER;
            case TILE_UNIT_LIGHT_ENEMY: return STR_UNIT_LIGHT_ENEMY;
            case TILE_UNIT_HEAVY_ENEMY: return STR_UNIT_HEAVY_ENEMY;
            case TILE_FLAG_ALLY:
            case TILE_FLAG_ENEMY: return STR_FLAG;
            default: return "";
        }
    }
}
