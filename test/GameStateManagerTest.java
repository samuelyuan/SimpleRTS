import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

// Simple stubs for dependencies
class SimpleRTSStub extends SimpleRTS {
    // No-op stub, override as needed
}

public class GameStateManagerTest {
    private SimpleRTSStub stubRTS;
    private GameStateManager manager;

    @BeforeEach
    void setUp() {
        stubRTS = new SimpleRTSStub();
        manager = new GameStateManager(stubRTS);

        // Minimal setup for GameMap instance to avoid exceptions
        GameMap gameMap = manager.getGameMap();
        gameMap.numLevel = 1;
        gameMap.mapdata = new int[10][10]; // or whatever minimal size is needed
    }

    @Test
    void startsInMenuState() {
        assertTrue(manager.getCurrentState() instanceof StateGameMenu);
    }

    @Test
    void transitionsToInstructions() {
        manager.setNewState(GameState.STATE_INSTRUCT);
        manager.changeState();
        assertTrue(manager.getCurrentState() instanceof StateGameInstructions);
    }

    @Test
    void transitionsToGameOver() {
        manager.setNewState(GameState.STATE_GAMEOVER);
        manager.changeState();
        assertTrue(manager.getCurrentState() instanceof StateGameOver);
    }

    @Test
    void transitionsToWin() {
        manager.setNewState(GameState.STATE_WIN);
        manager.changeState();
        assertTrue(manager.getCurrentState() instanceof StateGameWin);
    }

    @Test
    void stateNullDoesNotChangeState() {
        StateMachine before = manager.getCurrentState();
        manager.setNewState(GameState.STATE_NULL);
        manager.changeState();
        assertSame(before, manager.getCurrentState());
    }
} 