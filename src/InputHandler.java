import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import input.GameMouseEvent;
import input.GameMouseListener;
import input.MouseListenerRegistrar;

import java.util.ArrayList;

/**
 * Handles all mouse input for the game, converting AWT mouse events
 * to game-specific events and managing input listeners.
 */
public class InputHandler implements MouseListener, MouseMotionListener {
    
    private final List<GameMouseListener> mouseListeners = new ArrayList<>();
    private final MouseListenerRegistrar registrar;
    private final GameStateManager stateManager;
    private final CameraManager cameraManager;
    
    // Direct state registration
    private StateMachine currentState = null;
    
    public InputHandler(MouseListenerRegistrar registrar, GameStateManager stateManager) {
        this.registrar = registrar;
        this.stateManager = stateManager;
        this.cameraManager = new CameraManager(stateManager, registrar);
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
            }
        }
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
            try {
                listener.onGameMouseEvent(event);
            } catch (Exception e) {
                // Log error but don't crash the input handling
                System.err.println("Error in mouse listener: " + e.getMessage());
            }
        }
    }
} 