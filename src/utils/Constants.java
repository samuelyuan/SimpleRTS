package utils;
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

    // Legacy 4-direction system (kept for backward compatibility)
    public static final int DIR_NORTH = 0;
    public static final int DIR_SOUTH = 1;
    public static final int DIR_EAST = 2;
    public static final int DIR_WEST = 3;
    
    // 360-degree rotation constants
    public static final double DEGREES_TO_RADIANS = Math.PI / 180.0;
    public static final double RADIANS_TO_DEGREES = 180.0 / Math.PI;
    
    // Rotation smoothing for more natural movement
    public static final double ROTATION_SMOOTHING_FACTOR = 0.15; // Adjust for faster/slower rotation
    public static final double MIN_ROTATION_THRESHOLD = 5.0; // Minimum degrees to trigger rotation

    // Field of View (FOV) constants
    public static final double FOV_ANGLE = 120.0; // Field of view in degrees (120° cone)
    public static final double FOV_HALF_ANGLE = FOV_ANGLE / 2.0; // Half the FOV angle
    public static final int FOV_RENDER_SEGMENTS = 16; // Number of segments for FOV cone rendering
    public static final int FOV_RENDER_RADIUS = 6; // FOV cone radius in tiles for rendering
    
    // FOV Debug and Toggle settings
    public static boolean FOV_RENDERING_ENABLED = true; // Master toggle for FOV rendering
    public static boolean FOV_SHOW_ENEMY_UNITS = false; // Show FOV for enemy units (for debugging)
    public static boolean FOV_SHOW_SELECTED_ONLY = true; // Only show FOV for selected units (when true)

    private Constants() {
        // prevent instantiation
    }
}