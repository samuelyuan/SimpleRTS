public class GameStateManager {
    private GameState nextState = GameState.STATE_NULL;
    private StateMachine currentState = null;
    private final SimpleRTS simpleRTS;
    private final GameUnitManager unitManager;
    private final GameMap gameMap;
    
    // camera data
    private int cameraX = 0, cameraY = 0;

    public GameStateManager(SimpleRTS simpleRTS) {
        this.simpleRTS = simpleRTS;
        this.unitManager = new GameUnitManager(new GameFlagManager());
        this.gameMap = new GameMap();
        // Start with menu state
        this.currentState = new StateGameMenu(this);
    }

    public void setNewState(GameState newState) {
        this.nextState = newState;
    }

    public void changeState() {
        if (nextState != GameState.STATE_NULL) {
            switch (nextState) {
                case STATE_MENU:
                    currentState = new StateGameMenu(this);
                    break;
                case STATE_INSTRUCT:
                    currentState = new StateGameInstructions(this);
                    break;
                case STATE_MAIN:
                    currentState = createGameStateMain();
                    break;
                case STATE_STARTLVL:
                    currentState = new StateGameStartLevel(this);
                    break;
                case STATE_GAMEOVER:
                    currentState = new StateGameOver(this);
                    break;
                case STATE_NEXTLVL:
                    currentState = new StateGameNextLevel(this);
                    break;
                case STATE_WIN:
                    currentState = new StateGameWin(this);
                    break;
            }
            // Notify input handler of state change
            if (simpleRTS != null && simpleRTS.getInputHandler() != null) {
                simpleRTS.getInputHandler().setCurrentState(currentState);
            }
            nextState = GameState.STATE_NULL;
        }
    }

    private StateGameMain createGameStateMain() {
        gameMap.loadMap();
        unitManager.init(
            gameMap.getAllyUnitPositions(),
            gameMap.getEnemyUnitPositions(),
            gameMap.getFlagPositions()
        );
        GameFogWar fogWar = new GameFogWar(gameMap.getMapData().length, gameMap.getMapData()[0].length);
        GraphicsMain graphicsMain = new GraphicsMain(this, fogWar);
        return new StateGameMain(this, unitManager, fogWar, graphicsMain);
    }

    public StateMachine getCurrentState() {
        return currentState;
    }
    
    public GameMap getGameMap() {
        return gameMap;
    }
    
    public GameUnitManager getUnitManager() {
        return unitManager;
    }
    
    // Camera getters and setters
    public int getCameraX() {
        return cameraX;
    }
    
    public int getCameraY() {
        return cameraY;
    }
    
    public void setCameraX(int cameraX) {
        this.cameraX = cameraX;
    }
    
    public void setCameraY(int cameraY) {
        this.cameraY = cameraY;
    }
    
    public void addCameraX(int deltaX) {
        this.cameraX += deltaX;
    }
    
    public void addCameraY(int deltaY) {
        this.cameraY += deltaY;
    }
} 