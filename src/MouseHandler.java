import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import entities.GameUnit;
import input.GameMouseEvent;
import input.GameMouseListener;
import input.MouseListenerRegistrar;
import utils.Constants;
import graphics.Point;

import java.util.ArrayList;

/**
 * Handles all mouse input for the game, converting AWT mouse events
 * to game-specific events and managing input listeners.
 */
public class MouseHandler implements MouseListener, MouseMotionListener {
    
    private final List<GameMouseListener> mouseListeners = new ArrayList<>();
    private final MouseListenerRegistrar registrar;
    private final GameStateManager stateManager;
    private final CameraManager cameraManager;
    
    // Direct state registration
    private StateMachine currentState = null;
    
    public MouseHandler(MouseListenerRegistrar registrar, GameStateManager stateManager, CameraManager cameraManager) {
        this.registrar = registrar;
        this.stateManager = stateManager;
        this.cameraManager = cameraManager;
    }
    
    // MouseListener implementation
    @Override
    public void mouseClicked(MouseEvent e) {
        // Currently unused, but available for future use
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        GameMouseEvent gameEvent = createGameMouseEvent(e, GameMouseEvent.Type.PRESSED);
        if (gameEvent != null) {
            handleMouseEvent(gameEvent);
            
            // Handle selection box creation in game state
            if (currentState instanceof StateGameMain) {
                stateManager.getSelectionManager().createSelectBox(gameEvent);
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        GameMouseEvent gameEvent = createGameMouseEvent(e, GameMouseEvent.Type.RELEASED);
        if (gameEvent != null) {
            handleMouseEvent(gameEvent);
            
            // Handle selection box release in game state
            if (currentState instanceof StateGameMain) {
                stateManager.getSelectionManager().releaseSelectBox();
            }
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        // Currently unused, but available for future use
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        // Currently unused, but available for future use
    }
    
    // MouseMotionListener implementation
    @Override
    public void mouseDragged(MouseEvent e) {
        GameMouseEvent gameEvent = createGameMouseEvent(e, GameMouseEvent.Type.DRAGGED);
        if (gameEvent != null) {
            handleMouseEvent(gameEvent);
            
            // Handle selection box dragging in game state
            if (currentState instanceof StateGameMain) {
                stateManager.getSelectionManager().dragSelectBox(gameEvent);
            }
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        GameMouseEvent gameEvent = createGameMouseEvent(e, GameMouseEvent.Type.MOVED);
        if (gameEvent != null) {
            handleMouseEvent(gameEvent);
            
            // Handle camera scrolling in game state
            if (currentState instanceof StateGameMain) {
                cameraManager.handleCameraScrolling(gameEvent.x, gameEvent.y);
                
                // Handle unit hover detection
                handleUnitHover(gameEvent);
            }
        }
    }
    
    /**
     * Handles hover detection for units
     */
    private void handleUnitHover(GameMouseEvent e) {
        // Clear all hover states first
        for (GameUnit unit : stateManager.getUnitManager().getPlayerList()) {
            unit.setHovered(false);
        }
        for (GameUnit unit : stateManager.getUnitManager().getEnemyList()) {
            unit.setHovered(false);
        }
        
        // Check if mouse is over any unit
        for (GameUnit unit : stateManager.getUnitManager().getPlayerList()) {
            if (isMouseOverUnit(e, unit)) {
                unit.setHovered(true);
                break;
            }
        }
        
        for (GameUnit unit : stateManager.getUnitManager().getEnemyList()) {
            if (isMouseOverUnit(e, unit)) {
                unit.setHovered(true);
                break;
            }
        }
    }
    
    /**
     * Checks if mouse is over a specific unit
     */
    private boolean isMouseOverUnit(GameMouseEvent e, GameUnit unit) {
        Point unitPos = unit.getCurrentPosition();
        int cameraX = cameraManager.getCameraX();
        int cameraY = cameraManager.getCameraY();
        
        return e.x >= unitPos.x - cameraX 
            && e.x <= unitPos.x - cameraX + Constants.TILE_WIDTH
            && e.y >= unitPos.y - cameraY 
            && e.y <= unitPos.y - cameraY + Constants.TILE_HEIGHT;
    }
    
    /**
     * Creates a GameMouseEvent from an AWT MouseEvent
     */
    private GameMouseEvent createGameMouseEvent(MouseEvent e, GameMouseEvent.Type type) {
        if (e.getComponent() == null) {
            return null;
        }
        
        // Transform screen coordinates to game coordinates
        CoordinateTransformer.GameCoordinates gameCoords = CoordinateTransformer.screenToGame(
            e.getX(), e.getY(), e.getComponent().getWidth(), e.getComponent().getHeight()
        );
        
        if (gameCoords == null) {
            return null;
        }
        
        return new GameMouseEvent(type, gameCoords.x, gameCoords.y, e.getButton());
    }
    
    /**
     * Handles a game mouse event by dispatching to listeners and current state
     */
    private void handleMouseEvent(GameMouseEvent event) {
        dispatchGameMouseEvent(event);
        
        // Handle mouse command in current state
        if (currentState != null) {
            currentState.handleMouseCommand(event);
        }
    }

    // Input abstraction support
    public void addGameMouseListener(GameMouseListener listener) {
        if (listener != null) {
            mouseListeners.add(listener);
        }
    }
    
    public void clearGameMouseListeners() {
        mouseListeners.clear();
    }
    
    public void setCurrentState(StateMachine state) {
        this.currentState = state;
        clearGameMouseListeners();
        
        // Register UI components from the new state
        if (state != null) {
            ui.UIComponent root = state.getRoot();
            if (root != null) {
                ui.UIComponent.registerAllListeners(root, registrar);
            }
        }
    }
    
    private void dispatchGameMouseEvent(GameMouseEvent event) {
        for (GameMouseListener listener : mouseListeners) {
            listener.onGameMouseEvent(event);
        }
    }
} 