public class Constants {
    public static final int SCREEN_WIDTH = 1076;
    public static final int SCREEN_HEIGHT = 768;

    public static final int TILE_WIDTH = 50;
    public static final int TILE_HEIGHT = 50;

    // map data
    public static final int MAX_LVL = 2;

    /**
     * Unit class types:
     *
     * - LIGHT (1): Regular infantry. Fast, but lightly armored.
     * - MEDIUM (2): Anti-armor infantry. Balanced speed and strength.
     * - HEAVY (3): Tank. Strong armor and high damage, but slow.
     *
     * Damage effectiveness (rock-paper-scissors):
     * 
     * - LIGHT > MEDIUM: Light outruns and outmaneuvers medium.
     * - MEDIUM > HEAVY: Medium can pierce heavy armor effectively.
     * - HEAVY > LIGHT: Heavy reduces light's damage and counterattacks hard.
     */
    public static final int UNIT_ID_LIGHT = 1;
    public static final int UNIT_ID_MEDIUM = 2;
    public static final int UNIT_ID_HEAVY = 3;

    public static final int[][] DAMAGE_MATRIX = {
            // LIGHT (1) → LIGHT (1), MEDIUM (2), HEAVY (3)
            { 2, 3, 2 },
            // MEDIUM (2)
            { 2, 2, 3 },
            // HEAVY (3)
            { 5, 2, 2 }
    };

    public static final int STATE_IDLE = 1;
    public static final int STATE_MOVE = 2;
    public static final int STATE_ATTACK = 3;

    public static final int DIR_NORTH = 0;
    public static final int DIR_SOUTH = 1;
    public static final int DIR_EAST = 2;
    public static final int DIR_WEST = 3;

    private Constants() {
        // prevent instantiation
    }
}