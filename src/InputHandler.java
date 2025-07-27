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
    
    // Direct state registration
    private StateMachine currentState = null;
    
    // Camera scrolling constants
    private static final int SCROLL_AMOUNT = 5;
    private static final int SCROLL_MARGIN = 25;
    private static final int CURSOR_MARGIN = 50;
    
    public InputHandler(MouseListenerRegistrar registrar, GameStateManager stateManager) {
        this.registrar = registrar;
        this.stateManager = stateManager;
    }
    
    // MouseListener implementation
    @Override
    public void mouseClicked(MouseEvent e) {
        // Currently unused, but available for future use
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        // Transform screen coordinates to game coordinates
        CoordinateTransformer.GameCoordinates gameCoords = CoordinateTransformer.screenToGame(
            e.getX(), e.getY(), e.getComponent().getWidth(), e.getComponent().getHeight()
        );
        
        if (gameCoords != null) {
            GameMouseEvent ge = new GameMouseEvent(
                GameMouseEvent.Type.PRESSED, gameCoords.x, gameCoords.y, e.getButton()
            );
            dispatchGameMouseEvent(ge);
            
            // Handle mouse command in current state
            if (currentState != null) {
                currentState.handleMouseCommand(ge);
            }
            
            // Handle selection box creation in game state
            if (currentState instanceof StateGameMain) {
                Mouse.createSelectBox(ge);
            }
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        // Transform screen coordinates to game coordinates
        CoordinateTransformer.GameCoordinates gameCoords = CoordinateTransformer.screenToGame(
            e.getX(), e.getY(), e.getComponent().getWidth(), e.getComponent().getHeight()
        );
        
        if (gameCoords != null) {
            GameMouseEvent ge = new GameMouseEvent(
                GameMouseEvent.Type.RELEASED, gameCoords.x, gameCoords.y, e.getButton()
            );
            
            // Handle mouse command in current state
            if (currentState != null) {
                currentState.handleMouseCommand(ge);
            }
            
            // Handle selection box release in game state
            if (currentState instanceof StateGameMain) {
                Mouse.releaseSelectBox();
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
        // Transform screen coordinates to game coordinates
        CoordinateTransformer.GameCoordinates gameCoords = CoordinateTransformer.screenToGame(
            e.getX(), e.getY(), e.getComponent().getWidth(), e.getComponent().getHeight()
        );
        
        if (gameCoords != null) {
            GameMouseEvent ge = new GameMouseEvent(
                GameMouseEvent.Type.DRAGGED, gameCoords.x, gameCoords.y, e.getButton()
            );
            dispatchGameMouseEvent(ge);
            
            // Handle selection box dragging in game state
            if (currentState instanceof StateGameMain) {
                Mouse.dragSelectBox(ge);
            }
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        // Transform screen coordinates to game coordinates
        CoordinateTransformer.GameCoordinates gameCoords = CoordinateTransformer.screenToGame(
            e.getX(), e.getY(), e.getComponent().getWidth(), e.getComponent().getHeight()
        );
        
        if (gameCoords != null) {
            dispatchGameMouseEvent(new GameMouseEvent(
                GameMouseEvent.Type.MOVED, gameCoords.x, gameCoords.y, e.getButton()
            ));
            
            // Handle camera scrolling in game state
            if (currentState instanceof StateGameMain) {
                handleCameraScrolling(gameCoords.x, gameCoords.y);
            }
        }
    }
    
    /**
     * Handles camera scrolling based on mouse position near screen edges
     */
    private void handleCameraScrolling(int gameX, int gameY) {
        int screenWidth = Constants.SCREEN_WIDTH;
        int screenHeight = Constants.SCREEN_HEIGHT;
        
        // Scroll right
        if (gameX > screenWidth - SCROLL_MARGIN) {
            stateManager.addCameraX(SCROLL_AMOUNT);
            setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
        }
        // Scroll left
        else if (gameX < SCROLL_MARGIN) {
            stateManager.addCameraX(-SCROLL_AMOUNT);
            setCursor(new Cursor(Cursor.W_RESIZE_CURSOR));
        }
        // Scroll down
        else if (gameY > screenHeight - SCROLL_MARGIN) {
            stateManager.addCameraY(SCROLL_AMOUNT);
            setCursor(new Cursor(Cursor.S_RESIZE_CURSOR));
        }
        // Scroll up
        else if (gameY < SCROLL_MARGIN) {
            stateManager.addCameraY(-SCROLL_AMOUNT);
            setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
        }
        // Default cursor when not near edges
        else if (gameX > CURSOR_MARGIN && gameX < screenWidth - CURSOR_MARGIN
                && gameY > CURSOR_MARGIN && gameY < screenHeight - CURSOR_MARGIN) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        
        // Keep camera within bounds
        constrainCameraPosition();
    }
    
    /**
     * Constrains camera position to stay within map boundaries
     */
    private void constrainCameraPosition() {
        // Too far left
        if (stateManager.getCameraX() < 0) {
            stateManager.setCameraX(0);
        }
        
        // Too far up
        if (stateManager.getCameraY() < 0) {
            stateManager.setCameraY(0);
        }
        
        // Too far right (adjust based on map size)
        if (stateManager.getCameraX() > 400 + SCROLL_AMOUNT) {
            stateManager.setCameraX(400 + SCROLL_AMOUNT);
        }
        
        // Too far down
        if (stateManager.getCameraY() > Constants.SCREEN_HEIGHT) {
            stateManager.setCameraY(Constants.SCREEN_HEIGHT);
        }
    }
    
    /**
     * Sets the cursor for the game window
     */
    private void setCursor(Cursor cursor) {
        if (registrar instanceof SimpleRTS) {
            ((SimpleRTS) registrar).setCursor(cursor);
        }
    }
    
    // Input abstraction support
    public void addGameMouseListener(GameMouseListener listener) {
        mouseListeners.add(listener);
    }
    
    public void clearGameMouseListeners() {
        mouseListeners.clear();
    }
    
    public void setCurrentState(StateMachine state) {
        this.currentState = state;
        clearGameMouseListeners();
        
        // Register UI components from the new state
        ui.UIComponent root = state.getRoot();
        if (root != null) {
            ui.UIComponent.registerAllListeners(root, registrar);
        }
    }
    
    private void dispatchGameMouseEvent(GameMouseEvent event) {
        for (GameMouseListener listener : mouseListeners) {
            listener.onGameMouseEvent(event);
        }
    }
} 