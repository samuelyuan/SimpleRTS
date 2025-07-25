import java.util.Objects;

public class GameStateManager {
    private GameState stateID = GameState.STATE_NULL;
    private GameState nextState = GameState.STATE_NULL;
    private StateMachine currentState = null;
    private final SimpleRTS simpleRTS;
    private final GameUnitManager unitManager;

    public GameStateManager(SimpleRTS simpleRTS, GameUnitManager unitManager) {
        this.simpleRTS = simpleRTS;
        this.unitManager = unitManager;
        // Start with menu state
        this.currentState = new StateGameMenu(simpleRTS, unitManager);
    }

    public void setNewState(GameState newState) {
        this.nextState = newState;
    }

    public void changeState() {
        if (nextState != GameState.STATE_NULL) {
            switch (nextState) {
                case STATE_MENU:
                    currentState = new StateGameMenu(simpleRTS, unitManager);
                    break;
                case STATE_INSTRUCT:
                    currentState = new StateGameInstructions(simpleRTS, unitManager);
                    break;
                case STATE_MAIN:
                    currentState = createGameStateMain();
                    break;
                case STATE_STARTLVL:
                    currentState = new StateGameStartLevel(simpleRTS, unitManager);
                    break;
                case STATE_GAMEOVER:
                    currentState = new StateGameOver(simpleRTS, unitManager);
                    break;
                case STATE_NEXTLVL:
                    currentState = new StateGameNextLevel(simpleRTS, unitManager);
                    break;
                case STATE_WIN:
                    currentState = new StateGameWin(simpleRTS, unitManager);
                    break;
            }
            stateID = nextState;
            nextState = GameState.STATE_NULL;
        }
    }

    private StateGameMain createGameStateMain() {
        GameMap.loadMap();
        unitManager.init(
            GameMap.getAllyUnitPositions(),
            GameMap.getEnemyUnitPositions(),
            GameMap.getFlagPositions()
        );
        GameFogWar fogWar = new GameFogWar(GameMap.mapdata.length, GameMap.mapdata[0].length);
        GraphicsMain graphicsMain = new GraphicsMain(fogWar);
        return new StateGameMain(simpleRTS, unitManager, fogWar, graphicsMain);
    }

    public StateMachine getCurrentState() {
        return currentState;
    }
} 