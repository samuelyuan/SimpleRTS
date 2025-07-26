import graphics.Point;
import input.GameMouseEvent;

public class Mouse {
	// mouse data
	public static int selectX1, selectY1, selectX2, selectY2;
	public static boolean isPressed = false;

	// final selection box
	public static int boxX1, boxY1, boxX2, boxY2;

	public static void dragSelectBox(GameMouseEvent e) {
		// record new coordinates of selection box
		selectX2 = e.x;
		selectY2 = e.y;
	}

	public static void createSelectBox(GameMouseEvent e) {
		// left mouse button forms selection box
		if (e.button == 1) {
			selectX1 = e.x;
			selectY1 = e.y;

			selectX2 = selectX1;
			selectY2 = selectY1;

			isPressed = true;
		}
	}

	public static void sortSelectionCoordinates() {
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

	public static void releaseSelectBox() {
		isPressed = false;
	}

	public static boolean isInSelectionBox(int x, int y) {
		return x >= boxX1 && x <= boxX2 && y >= boxY1 && y <= boxY2;
	}

	public static boolean isPlayerSelect(Point currentPosition, boolean isClickedOn, int cameraX, int cameraY) {
		// Mouse released
		if (!Mouse.isPressed) {
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

	public static boolean isClickOnUnit(GameMouseEvent e, Point currentPosition, int cameraX, int cameraY) {
		return e.button == 1
				&& currentPosition.x - cameraX <= e.x
				&& currentPosition.y - cameraY <= e.y
				&& currentPosition.x + Constants.TILE_WIDTH - cameraX >= e.x
				&& currentPosition.y + Constants.TILE_HEIGHT - cameraY >= e.y;

	}
}
