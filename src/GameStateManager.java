import java.util.Map;

import entities.GameFlag;
import entities.GameFlagManager;
import entities.GameUnit;
import entities.GameUnitManager;
import managers.CameraManager;
import managers.CombatEffectManager;
import managers.GameFogWar;
import managers.SelectionManager;
import graphics.Point;
import utils.TileCoordinateConverter;

public class GameStateManager {
    private GameState nextState = GameState.STATE_NULL;
    private StateMachine currentState = null;
    private final SimpleRTS simpleRTS;
    private final ImageService imageService;
    private final GameUnitManager unitManager;
    private final GameFlagManager flagManager;
    private final GameMap gameMap;
    private final SelectionManager selectionManager;
    private final CameraManager cameraManager;
    private final CombatEffectManager combatEffectManager;

    public GameStateManager(SimpleRTS simpleRTS, CameraManager cameraManager, ImageService imageService) {
        this.simpleRTS = simpleRTS;
        this.cameraManager = cameraManager;
        this.imageService = imageService;
        this.flagManager = new GameFlagManager();
        this.unitManager = new GameUnitManager();
        this.gameMap = new GameMap(imageService);
        this.selectionManager = new SelectionManager();
        this.combatEffectManager = new CombatEffectManager();
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
            // Notify mouse handler of state change
            if (simpleRTS != null && simpleRTS.getMouseHandler() != null) {
                simpleRTS.getMouseHandler().setCurrentState(currentState);
            }
            nextState = GameState.STATE_NULL;
        }
    }

    private StateGameMain createGameStateMain() {
        gameMap.loadMap();
        unitManager.init(
            gameMap.getAllyUnitPositions(),
            gameMap.getEnemyUnitPositions()
        );
        loadFlags(gameMap.getFlagPositions());
        GameFogWar fogWar = new GameFogWar(gameMap.getMapData().length, gameMap.getMapData()[0].length);
        GraphicsMain graphicsMain = new GraphicsMain(this, fogWar, cameraManager);
        return new StateGameMain(this, unitManager, fogWar, graphicsMain);
    }

    private void loadFlags(Map<Point, Integer> flagPositions) {
        for (Map.Entry<Point, Integer> entry : flagPositions.entrySet()) {
            Point position = entry.getKey();
            int faction = entry.getValue();
            if (faction == GameFlag.FACTION_PLAYER)
                flagManager.addPlayerFlag(position.x, position.y);
            else if (faction == GameFlag.FACTION_ENEMY)
                flagManager.addEnemyFlag(position.x, position.y);
        }
    }

    public void checkFlagStates(GameUnit unit, int factionId) {
        Point unitMapPos = TileCoordinateConverter.screenToMap(unit.getCurrentPosition());
        int unitMapX = unitMapPos.x;
        int unitMapY = unitMapPos.y;
        flagManager.checkFlagState(unitMapX, unitMapY, factionId);
    }

    public boolean isFlagsListEmpty(int factionId) {
        if (factionId == GameFlag.FACTION_PLAYER) {
            return flagManager.isPlayerFlagsEmpty();
        } else if (factionId == GameFlag.FACTION_ENEMY) {
            return flagManager.isEnemyFlagsEmpty();
        } else {
            return true;
        }
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
    
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
    
    public CameraManager getCameraManager() {
        return cameraManager;
    }
    
    public GameFlagManager getFlagManager() {
        return flagManager;
    }
    
    public ImageService getImageService() {
        return imageService;
    }
    
    public CombatEffectManager getCombatEffectManager() {
        return combatEffectManager;
    }
} 