package managers;

import graphics.Point;
import input.GameMouseEvent;
import utils.Constants;

/**
 * Manages unit selection and selection box functionality.
 */
public class SelectionManager {
    
    // Selection box coordinates
    private int selectX1, selectY1, selectX2, selectY2;
    private boolean isPressed = false;
    
    // Final selection box coordinates (sorted)
    private int boxX1, boxY1, boxX2, boxY2;
    
    /**
     * Creates a selection box when mouse is pressed
     */
    public void createSelectBox(GameMouseEvent e) {
        // Left mouse button forms selection box
        if (e.button == 1) {
            selectX1 = e.x;
            selectY1 = e.y;
            
            selectX2 = selectX1;
            selectY2 = selectY1;
            
            isPressed = true;
        }
    }
    
    /**
     * Updates selection box when mouse is dragged
     */
    public void dragSelectBox(GameMouseEvent e) {
        // Record new coordinates of selection box
        selectX2 = e.x;
        selectY2 = e.y;
    }
    
    /**
     * Releases the selection box when mouse is released
     */
    public void releaseSelectBox() {
        isPressed = false;
    }
    
    /**
     * Sorts selection coordinates to ensure proper box bounds
     */
    public void sortSelectionCoordinates() {
        if (selectX2 < selectX1) {
            boxX1 = selectX2;
            boxX2 = selectX1;
        } else {
            boxX1 = selectX1;
            boxX2 = selectX2;
        }
        
        if (selectY2 < selectY1) {
            boxY1 = selectY2;
            boxY2 = selectY1;
        } else {
            boxY1 = selectY1;
            boxY2 = selectY2;
        }
    }
    
    /**
     * Checks if a point is within the current selection box
     */
    public boolean isInSelectionBox(int x, int y) {
        return x >= boxX1 && x <= boxX2 && y >= boxY1 && y <= boxY2;
    }
    
    /**
     * Determines if a unit should be selected based on selection box or direct click
     */
    public boolean isPlayerSelect(Point currentPosition, boolean isClickedOn, int cameraX, int cameraY) {
        // Mouse released
        if (!isPressed) {
            // Check selection box
            if (isInSelectionBox(currentPosition.x + Constants.TILE_WIDTH / 2 - cameraX,
                    currentPosition.y + Constants.TILE_HEIGHT / 2 - cameraY)) {
                return true;
            } 
            // Or click on the unit
            return isClickedOn;
        } 
        return false;
    }
    
    /**
     * Checks if a mouse event represents a click on a unit
     */
    public boolean isClickOnUnit(GameMouseEvent e, Point currentPosition, int cameraX, int cameraY) {
        return e.button == 1
                && currentPosition.x - cameraX <= e.x
                && currentPosition.y - cameraY <= e.y
                && currentPosition.x + Constants.TILE_WIDTH - cameraX >= e.x
                && currentPosition.y + Constants.TILE_HEIGHT - cameraY >= e.y;
    }
    
    /**
     * Gets the current selection box bounds
     */
    public SelectionBox getSelectionBox() {
        sortSelectionCoordinates(); // Ensure coordinates are sorted
        return new SelectionBox(boxX1, boxY1, boxX2, boxY2);
    }
    
    /**
     * Checks if selection is currently active
     */
    public boolean isSelectionActive() {
        return isPressed;
    }
    
    /**
     * Clears the current selection
     */
    public void clearSelection() {
        isPressed = false;
        selectX1 = selectY1 = selectX2 = selectY2 = 0;
        boxX1 = boxY1 = boxX2 = boxY2 = 0;
    }
    
    /**
     * Container class for selection box bounds
     */
    public static class SelectionBox {
        public final int x1, y1, x2, y2;
        
        public SelectionBox(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        public int getWidth() {
            return x2 - x1;
        }
        
        public int getHeight() {
            return y2 - y1;
        }
    }
}

