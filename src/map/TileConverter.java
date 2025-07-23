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

    public static int tileStrToBaseId(String tileStr) {
        if (tileStr.contains("Land")) return TILE_LAND;
        if (tileStr.contains("Wall")) return TILE_WALL;
        if (tileStr.contains("Flag")) return TILE_FLAG_ALLY; // default fallback
        if (tileStr.contains("Unit Light Player")) return TILE_UNIT_LIGHT_PLAYER;
        if (tileStr.contains("Unit Medium")) return TILE_UNIT_MEDIUM;
        if (tileStr.contains("Unit Heavy Player")) return TILE_UNIT_HEAVY_PLAYER;
        if (tileStr.contains("Unit Light Enemy")) return TILE_UNIT_LIGHT_ENEMY;
        if (tileStr.contains("Unit Heavy Enemy")) return TILE_UNIT_HEAVY_ENEMY;
        return -1;
    }

    public static String tileIntToStr(int tileId) {
        switch (tileId) {
            case TILE_LAND: return "Land";
            case TILE_WALL: return "Wall";
            case TILE_UNIT_LIGHT_PLAYER: return "Unit Light Player";
            case TILE_UNIT_MEDIUM:
            case TILE_UNIT_MEDIUM_ALT: return "Unit Medium";
            case TILE_UNIT_HEAVY_PLAYER: return "Unit Heavy Player";
            case TILE_UNIT_LIGHT_ENEMY: return "Unit Light Enemy";
            case TILE_UNIT_HEAVY_ENEMY: return "Unit Heavy Enemy";
            case TILE_FLAG_ALLY:
            case TILE_FLAG_ENEMY: return "Flag";
            default: return "";
        }
    }
}
