import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Iterator;

public class GameFlagManagerTest {

    private GameFlagManager flagManager;

    @BeforeEach
    public void setUp() {
        // This method is run before each test to set up the GameFlagManager instance.
        flagManager = new GameFlagManager();
    }

    @Test
    public void testAddPlayerFlag() {
        flagManager.addPlayerFlag(1, 1);
        assertNotNull(flagManager.getPlayerFlag(), "Player flag should not be null");
        assertEquals(1, flagManager.getPlayerFlag().getMapX(), "Player flag X coordinate should be 1");
        assertEquals(1, flagManager.getPlayerFlag().getMapY(), "Player flag Y coordinate should be 1");
    }

    @Test
    public void testAddEnemyFlag() {
        flagManager.addEnemyFlag(2, 2);
        GameFlag enemyFlag = flagManager.getFlagList().next(); // Accessing the first flag
        assertNotNull(enemyFlag, "Enemy flag should not be null");
        assertEquals(2, enemyFlag.getMapX(), "Enemy flag X coordinate should be 2");
        assertEquals(2, enemyFlag.getMapY(), "Enemy flag Y coordinate should be 2");
    }

    @Test
    public void testIsPlayerFlagsEmptyWhenEmpty() {
        assertTrue(flagManager.isPlayerFlagsEmpty(), "Player flags should be empty initially");
    }

    @Test
    public void testIsPlayerFlagsEmptyWhenNotEmpty() {
        flagManager.addPlayerFlag(1, 1);
        assertFalse(flagManager.isPlayerFlagsEmpty(), "Player flags should not be empty after adding a flag");
    }

    @Test
    public void testIsEnemyFlagsEmptyWhenEmpty() {
        assertTrue(flagManager.isEnemyFlagsEmpty(), "Enemy flags should be empty initially");
    }

    @Test
    public void testIsEnemyFlagsEmptyWhenNotEmpty() {
        flagManager.addEnemyFlag(2, 2);
        assertFalse(flagManager.isEnemyFlagsEmpty(), "Enemy flags should not be empty after adding a flag");
    }

    @Test
    public void testReset() {
        flagManager.addPlayerFlag(1, 1);
        flagManager.addEnemyFlag(2, 2);
        flagManager.reset();

        // After reset, the flags should still exist, but the count should be correct
        assertEquals(1, flagManager.getNumFlagsPlayer(), "There should be 1 player flag after reset");
        assertEquals(1, flagManager.getNumFlagsEnemy(), "There should be 1 enemy flag after reset");
    }

    /*@Test
    public void testCheckFlagState() {
        flagManager.addPlayerFlag(1, 1);

        // Change the player's flag to enemy's faction
        flagManager.checkFlagState(1, 1, GameFlag.FACTION_ENEMY);

        // Assert that the flag is now enemy
        assertTrue(flagManager.getPlayerFlag().isFactionEnemy(), "Player flag should be moved to enemy faction");
    }*/

    @Test
    public void testGetFlagList() {
        flagManager.addPlayerFlag(1, 1);
        flagManager.addEnemyFlag(2, 2);

        // Test that the list contains both flags
        Iterator<GameFlag> flagIterator = flagManager.getFlagList();
        assertTrue(flagIterator.hasNext(), "There should be at least one flag in the list");

        // Check player flag
        GameFlag firstFlag = flagIterator.next();
        assertNotNull(firstFlag, "First flag should not be null");
        assertTrue(firstFlag.isFactionPlayer(), "First flag should be a player flag");

        // Check enemy flag
        assertTrue(flagIterator.hasNext(), "There should be a second flag in the list");
        GameFlag secondFlag = flagIterator.next();
        assertNotNull(secondFlag, "Second flag should not be null");
        assertTrue(secondFlag.isFactionEnemy(), "Second flag should be an enemy flag");
    }
}
