public class Constants {
    public static final int SCREEN_WIDTH = 1076;
    public static final int SCREEN_HEIGHT = 768;

    public static final int TILE_WIDTH = 50;
    public static final int TILE_HEIGHT = 50;

    // map data
    public static final int MAX_LVL = 2;

    public static final int[][] DAMAGE_MATRIX = {
		// LIGHT (1) → LIGHT (1), MEDIUM (2), HEAVY (3)
		{2, 3, 2},
		// MEDIUM (2)
		{2, 2, 3},
		// HEAVY (3)
		{5, 2, 2}
	};

    private Constants() {
        // prevent instantiation
    }
}