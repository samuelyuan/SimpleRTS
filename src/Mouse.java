import java.awt.Point;
import java.awt.event.MouseEvent;

public class Mouse 
{
	//mouse data
	public static int selectX1, selectY1, selectX2, selectY2;
	public static boolean isPressed = false;

	//final selection box
	public static int boxX1, boxY1, boxX2, boxY2;
	
	public static void dragSelectBox(MouseEvent e)
	{
		//record new coordinates of selection box
		selectX2 = e.getX();
		selectY2 = e.getY();
	}
	
	public static void createSelectBox(MouseEvent e)
	{
		//left mouse button forms selection box
		if (e.getButton() == MouseEvent.BUTTON1 )
		{
			selectX1 = e.getX();
			selectY1 = e.getY();
			
			selectX2 = selectX1;
			selectY2 = selectY1;
			
			isPressed = true;
		}
	}
	
	public static void sortSelectionCoordinates()
	{
		if (selectX2 < selectX1)
		{
			boxX1 = selectX2;
			boxX2 = selectX1;
		}
		else
		{
			boxX1 = selectX1;
			boxX2 = selectX2;
		}
		
		if (selectY2 < selectY1)
		{
			boxY1 = selectY2;
			boxY2 = selectY1;
		}
		else
		{
			boxY1 = selectY1;
			boxY2 = selectY2;
		}
	}
	
	public static void releaseSelectBox()
	{
		isPressed = false;
	}
	
	public static boolean isInSelectionBox(int x, int y)
	{
		if (x >= boxX1 && x <= boxX2 && y >= boxY1 && y <= boxY2)
			return true;
		else
			return false;
	}
	
	public static boolean isPlayerSelect(Point currentPosition, boolean isClickedOn)
	{
		//Mouse released
		if (Mouse.isPressed == false)
		{
			//Check selection box
			if (isInSelectionBox(currentPosition.x + GameMap.TILE_WIDTH/2 - SimpleRTS.cameraX,
					currentPosition.y + GameMap.TILE_HEIGHT/2 - SimpleRTS.cameraY))
			{
				return true;
			}
			else
			{
				//Or click on the unit
				if (isClickedOn)
					return true;
				else
					return false;
			}
		}
		else
		{
			return false;
		}	
	}
	
	public static boolean isClickOnUnit(MouseEvent e, Point currentPosition)
	{
		if (e.getButton() == MouseEvent.BUTTON1 
				&& currentPosition.x - SimpleRTS.cameraX <= e.getX()  
				&& currentPosition.y - SimpleRTS.cameraY <= e.getY() 
				&& currentPosition.x + GameMap.TILE_WIDTH  - SimpleRTS.cameraX >= e.getX() 
				&& currentPosition.y + GameMap.TILE_HEIGHT - SimpleRTS.cameraY >= e.getY())
			return true;
		else
			return false;
	
	}
}
