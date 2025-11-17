import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import entities.GameFlag;
import graphics.Color;
import graphics.DrawingInstruction;
import graphics.Rect;

public class GameFlagTest {

    private GameFlag playerFlag;
    private GameFlag enemyFlag;
    private GameFlag neutralFlag;

    @BeforeEach
    public void setUp() {
        // Initialize flags for testing
        playerFlag = new GameFlag(0, 0, GameFlag.FACTION_PLAYER);
        enemyFlag = new GameFlag(1, 1, GameFlag.FACTION_ENEMY);
        neutralFlag = new GameFlag(2, 2, GameFlag.FACTION_NEUTRAL);
    }

    @Test
    public void testConstructor() {
        // Test the constructor for player, enemy, and neutral flags
        assertEquals(GameFlag.FACTION_PLAYER, playerFlag.getControlFaction(), "Player flag should have player control");
        assertEquals(100, playerFlag.getHealth(), "Player flag should have health 100");

        assertEquals(GameFlag.FACTION_ENEMY, enemyFlag.getControlFaction(), "Enemy flag should have enemy control");
        assertEquals(-100, enemyFlag.getHealth(), "Enemy flag should have health -100");

        assertEquals(GameFlag.FACTION_NEUTRAL, neutralFlag.getControlFaction(), "Neutral flag should have neutral control");
        assertEquals(0, neutralFlag.getHealth(), "Neutral flag should have health 0");
    }

    @Test
    public void testShiftToFaction() {
        // Test that shifting the flag to a new faction updates its health
        playerFlag.shiftToFaction(1, 1, GameFlag.FACTION_ENEMY); // Shift to enemy

        assertEquals(98, playerFlag.getHealth(), "Flag's health should increase by 2 when shifted to enemy");

        playerFlag.shiftToFaction(1, 1, GameFlag.FACTION_PLAYER); // Shift back to player
        assertEquals(100, playerFlag.getHealth(), "Flag's health should increase by 2 when shifted to player");
    }

    @Test
    public void testHandleControlPlayerFlag() {
        // Test handle control method for player flag
        playerFlag.handleControl();
        assertEquals(GameFlag.FACTION_PLAYER, playerFlag.getControlFaction(), "Player flag should remain player");
        assertEquals(100, playerFlag.getHealth(), "Player flag should have health 100");
    }

    @Test
    public void testHandleControlEnemyFlag() {
        // Test handle control method for enemy flag
        enemyFlag.handleControl();
        assertEquals(GameFlag.FACTION_ENEMY, enemyFlag.getControlFaction(), "Enemy flag should remain enemy");
        assertEquals(-100, enemyFlag.getHealth(), "Enemy flag should have health -100");
    }

    @Test
    public void testHandleControlNeutralToPlayer() {
        // Simulate several ticks to gradually change the health and check if control shifts
        for (int i = 0; i < 51; i++) {  // Simulate 50 ticks to gradually increase health
            neutralFlag.shiftToFaction(2, 2, GameFlag.FACTION_PLAYER);
            neutralFlag.handleControl();
        }
        
        assertEquals(GameFlag.FACTION_PLAYER, neutralFlag.getControlFaction(), "Neutral flag should switch to player");
        assertEquals(100, neutralFlag.getHealth(), "Neutral flag should have health 100 after being controlled by player");
    }

    @Test
    public void testHandleControlPlayerToNeutral() {
        // First get the flag to player control
        for (int i = 0; i < 51; i++) {
            neutralFlag.shiftToFaction(2, 2, GameFlag.FACTION_PLAYER);
            neutralFlag.handleControl();
        }
        
        // Then shift to enemy control
        for (int i = 0; i < 51; i++) { 
            neutralFlag.shiftToFaction(2, 2, GameFlag.FACTION_ENEMY);
            neutralFlag.handleControl();
        }

        assertEquals(GameFlag.FACTION_NEUTRAL, neutralFlag.getControlFaction(), "Neutral flag should switch from player to neutral");
        assertEquals(-2, neutralFlag.getHealth(), "Neutral flag should have health -2 after being controlled by enemy");
    }

    @Test
    public void testHandleControlNeutralToEnemy() {
        // Simulate several ticks to gradually change the health and check if control shifts
        for (int i = 0; i < 50; i++) {  // Simulate 50 ticks to gradually decrease health
            neutralFlag.shiftToFaction(2, 2, GameFlag.FACTION_ENEMY);
            neutralFlag.handleControl();
        }

        assertEquals(GameFlag.FACTION_ENEMY, neutralFlag.getControlFaction(), "Neutral flag should switch from neutral to enemy");
        assertEquals(-100, neutralFlag.getHealth(), "Neutral flag should have health -100 after being controlled by enemy");
    }

    @Test
    public void testIsFactionPlayer() {
        // Test that isFactionPlayer correctly returns true for player flags
        assertTrue(playerFlag.isFactionPlayer(), "Player flag should return true for isFactionPlayer");
        assertFalse(enemyFlag.isFactionPlayer(), "Enemy flag should return false for isFactionPlayer");
        assertFalse(neutralFlag.isFactionPlayer(), "Neutral flag should return false for isFactionPlayer");
    }

    @Test
    public void testIsFactionEnemy() {
        // Test that isFactionEnemy correctly returns true for enemy flags
        assertTrue(enemyFlag.isFactionEnemy(), "Enemy flag should return true for isFactionEnemy");
        assertFalse(playerFlag.isFactionEnemy(), "Player flag should return false for isFactionEnemy");
        assertFalse(neutralFlag.isFactionEnemy(), "Neutral flag should return false for isFactionEnemy");
    }

    @Test
    public void testGetColorForFaction() {
		assertEquals(Color.BLUE, playerFlag.getColorForFaction(), "Player flag should be blue");
		assertEquals(Color.RED, enemyFlag.getColorForFaction(), "Enemy flag should be red");
		assertEquals(Color.GRAY, neutralFlag.getColorForFaction(), "Neutral flag should be gray");
	}

    @Test
    public void testGetBoundingBoxForState() {
        assertEquals(new Rect(2, 6, 48, 6), playerFlag.getBoundingBoxForState(0, 0),
                "Player flag bounding box should be correct");

        assertEquals(new Rect(52, 56, 48, 6), enemyFlag.getBoundingBoxForState(0, 0),
                "Enemy flag bounding box should be correct");

        assertEquals(new Rect(102, 106, 0, 6), neutralFlag.getBoundingBoxForState(0, 0),
                "Neutral flag bounding box should be correct");
    }

    @Test
    public void testGetDrawingInstruction() {
        // Test that the drawing instruction is created correctly
        DrawingInstruction playerInstr = playerFlag.getDrawingInstruction(0, 0);
        assertNotNull(playerInstr, "Drawing instruction should not be null");
        assertEquals(Color.BLUE, playerInstr.color, "Player flag should be blue");
        assertTrue(playerInstr.fill, "Flag should be filled");
        assertEquals(new Rect(2, 6, 48, 6), playerInstr.rect, "Player flag rect should be correct");

        DrawingInstruction enemyInstr = enemyFlag.getDrawingInstruction(0, 0);
        assertNotNull(enemyInstr, "Drawing instruction should not be null");
        assertEquals(Color.RED, enemyInstr.color, "Enemy flag should be red");
        assertTrue(enemyInstr.fill, "Flag should be filled");
        assertEquals(new Rect(52, 56, 48, 6), enemyInstr.rect, "Enemy flag rect should be correct");

        DrawingInstruction neutralInstr = neutralFlag.getDrawingInstruction(0, 0);
        assertNotNull(neutralInstr, "Drawing instruction should not be null");
        assertEquals(Color.GRAY, neutralInstr.color, "Neutral flag should be gray");
        assertTrue(neutralInstr.fill, "Flag should be filled");
        assertEquals(new Rect(102, 106, 0, 6), neutralInstr.rect, "Neutral flag rect should be correct");
    }
}
