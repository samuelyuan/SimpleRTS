import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;


public class GraphicsMain 
{
	private static boolean isNight;
	
	//helper functions
	public static void drawImageOnScreen(Image img, int x, int y, int width, int height)
	{			
		SimpleRTS.offscr.drawImage(img, x - SimpleRTS.cameraX, y - SimpleRTS.cameraY, width, height, null);
	}
	
	public static void drawRectOnScreen(int x, int y, int width, int height, boolean fill)
	{
		if (fill == true)
			SimpleRTS.offscr.fillRect(x - SimpleRTS.cameraX, y - SimpleRTS.cameraY, width, height);
		else
			SimpleRTS.offscr.drawRect(x - SimpleRTS.cameraX, y - SimpleRTS.cameraY, width, height);
	}
	
	public void drawGraphics(GameTime gameTimer)
	{
		//draw according to a day/night cycle
		isNight = gameTimer.isNight();
		
		GameFogWar.calculateFogOfWar();
		
		//Draw the map first
		drawMapTiles(GameMap.getDrawData());
		
		//Draw the units
		for (int i = 0; i < SimpleRTS.playerList.size(); i++)
		{
			GameUnit playerUnit = SimpleRTS.playerList.get(i);
			drawUnit(playerUnit);
		}
		for (int i = 0; i < SimpleRTS.enemyList.size(); i++ )
		{
			GameUnit enemyUnit = SimpleRTS.enemyList.get(i);
			drawUnit(enemyUnit);
		}
		
		//draw fog (although this should be done before drawing units, not after
		renderFog(GameMap.getDrawData());
		
		//Draw everything else
		drawMouseSelectionBox();
		drawLeftPanel(gameTimer);
	}
	
	public void renderFog(String[][] mapData)
	{
		for (int y = 0; y < mapData.length; y++ )
		{
			for (int x = 0; x < mapData[y].length; x++)
			{	
				if (GameFogWar.IsTileVisible(x, y) == false)
				{
					SimpleRTS.offscr.setColor(new Color(226, 226, 226));
					GraphicsMain.drawRectOnScreen(x*GameMap.TILE_WIDTH, y*GameMap.TILE_HEIGHT, 
							GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT, true);
					continue;
				}
			}
		}
	}
	
	public void drawUnit(GameUnit unit)
	{
		//draw square and black outline
		//highlight selected units
		if (unit.isPlayerSelected)
		{
			SimpleRTS.offscr.setColor(Color.BLACK);
			GraphicsMain.drawRectOnScreen(unit.getCurrentPoint().x, unit.getCurrentPoint().y, GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT, false);
			
			//draw destination point on screen if there is a path created
			if (unit.isPathCreated() && unit.isAlive())
			{
				SimpleRTS.offscr.setColor(Color.GREEN);
				Point mapDest = unit.getMapPoint(unit.destination);
				GraphicsMain.drawRectOnScreen(mapDest.x * GameMap.TILE_WIDTH, mapDest.y * GameMap.TILE_HEIGHT,
						GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT, false);
			}
		}
		
		//change direction for all units, not just player units!
		if (unit.isPathCreated() && unit.isAlive())
		{
			Point mapDest = unit.getMapPoint(unit.destination);
			int deltaX = mapDest.x * GameMap.TILE_WIDTH - unit.getCurrentPoint().x;
			int deltaY = mapDest.y * GameMap.TILE_HEIGHT - unit.getCurrentPoint().y;
			if (Math.abs(deltaX) >= Math.abs(deltaY))
			{
				if (deltaX > 0)
					unit.direction = GameUnit.DIR_EAST; 
				else
					unit.direction = GameUnit.DIR_WEST; 
			}
			else
			{
				if (deltaY > 0)
					unit.direction = GameUnit.DIR_SOUTH; 
				else
					unit.direction = GameUnit.DIR_NORTH;  
			}
		}
		
		//draw the sprite
		GraphicsMain.drawUnitSprite(unit.getCurrentPoint(), unit.getClassType(), unit.getIsPlayerUnit(), unit.direction);
		
		//Health Bar
		GraphicsMain.drawHealthBar(unit.getHealth(), unit.getCurrentPoint());
		
		//offscr.setColor(Color.white);
		//offscr.drawString("" + enemyList.get(i).getHealth(), enemyList.get(i).getPlayerX() - cameraX, enemyList.get(i).getPlayerY() - cameraY + TILE_HEIGHT/2);
	}

	/*
	 * Draw all the snow, walls, units, etc...
	 */
	public void drawMapTiles(String[][] mapData)
	{
		for (int y = 0; y < mapData.length; y++ )
		{
			for (int x = 0; x < mapData[y].length; x++)
			{	
				Image tempImg;
				String tileStr = mapData[y][x];
				if (tileStr.contains("Land") || tileStr.contains("Wall") || tileStr.contains("Flag"))
				{
					tempImg = GameImage.getImage(tileStr.substring(0, 4), isNight);
				}
				else
					tempImg = GameImage.getImage("Land", isNight);
				
				drawImageOnScreen(tempImg, x*GameMap.TILE_WIDTH, y*GameMap.TILE_HEIGHT, 
						GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT);
			}
		}
	}
	
	public static void drawUnitSprite(Point current, int classType, boolean isPlayerUnit, int direction)
	{		
		//display unit based off of class type
		//also, add a distinct team color to units
		SimpleRTS.offscr.setColor(Color.BLACK);
		BufferedImage newImg = null;
		if (classType == GameUnit.UNIT_ID_LIGHT)
		{
			if (isPlayerUnit == true)
				newImg = (BufferedImage) GameImage.getImage("Unit Light Player", isNight);
			else
				newImg = (BufferedImage) GameImage.getImage("Unit Light Enemy", isNight);
			
			//calculate direction
			newImg = newImg.getSubimage(GameMap.TILE_WIDTH * direction, 0, GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT);
		}
		else if (classType == GameUnit.UNIT_ID_MEDIUM)
		{
			newImg = GraphicsMain.addTeamColorToUnit(GameImage.getImage("Unit Medium"), isPlayerUnit);
		}
		else if (classType == GameUnit.UNIT_ID_HEAVY)
		{
			if (isPlayerUnit == true)
				newImg = (BufferedImage) GameImage.getImage("Unit Heavy Player", isNight);
			else
				newImg = (BufferedImage) GameImage.getImage("Unit Heavy Enemy", isNight);
			
			//calculate direction
			newImg = newImg.getSubimage(GameMap.TILE_WIDTH * direction, 0, GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT);
		}
		
		//draw sprite based off of its team color (red or blue)
		GraphicsMain.drawImageOnScreen(newImg, current.x, current.y, GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT);
	}
	
	public static BufferedImage addTeamColorToUnit(Image img, boolean isPlayerUnit)
	{
		BufferedImage bImage = new BufferedImage(GameMap.TILE_WIDTH, GameMap.TILE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = bImage.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		
		for (int y = 0; y < bImage.getHeight(); y++)
		{
			for (int x = 0; x < bImage.getWidth(); x++)
			{
				if (bImage.getRGB(x, y) != Color.WHITE.getRGB())
				{
					if (isPlayerUnit == true)
						bImage.setRGB(x, y, Color.BLUE.getRGB());
					else
						bImage.setRGB(x, y, Color.RED.getRGB());
				}
			}
		}
		
		return bImage;
	}
	
	public static void drawHealthBar(int health, Point current)
	{
		//Health Bar
		//Select color of health bar based off of the amount of health left
		if (health > 75)
			SimpleRTS.offscr.setColor(Color.GREEN);
		else if (health > 50 && health <= 75)
			SimpleRTS.offscr.setColor(Color.YELLOW);
		else if (health > 25 && health <= 50)
			SimpleRTS.offscr.setColor(Color.ORANGE);
		else if (health > 0 && health <= 25)
			SimpleRTS.offscr.setColor(Color.RED);
		
		//Draw the health bar
		GraphicsMain.drawRectOnScreen(current.x + 2, current.y + GameMap.TILE_HEIGHT/8, 
				(int) ((double)(GameMap.TILE_WIDTH-2)/100.0 * health), GameMap.TILE_HEIGHT/8, true);
	}
	
	public void drawMouseSelectionBox()
	{
		if (Mouse.isPressed == true)
		{
			Mouse.sortSelectionCoordinates();
			
			SimpleRTS.offscr.setColor(Color.BLACK);
			SimpleRTS.offscr.drawRect(Mouse.boxX1, Mouse.boxY1, 
					Mouse.boxX2 - Mouse.boxX1, Mouse.boxY2 - Mouse.boxY1);
		}
	}
	
	public void drawLeftPanel(GameTime gameTimer)
	{
		//draw minimap
		for (int y = 0; y < GameMap.mapdata.length; ++y)
		{
			for (int x = 0; x < GameMap.mapdata[y].length; ++x)
			{
				int imgTileWidth = 8, imgTileHeight = 8;
				int tile = GameMap.mapdata[y][x];
				String tileStr = "";
				
				if (tile == 0) { tileStr = "Land"; }
				else if (tile == 1) { tileStr = "Wall"; } 
				else if (tile == 2) { tileStr = "Unit Light Player"; }
				else if (tile == 5) { tileStr = "Unit Light Enemy"; }
				else if (tile == 3 || tile == 6) { tileStr = "Unit Medium"; }
				else if (tile == 4) { tileStr = "Unit Heavy Player"; }
				else if (tile == 7) { tileStr = "Unit Heavy Enemy"; }
				else if (tile == 8 || tile == 9) { tileStr = "Flag"; }
				
				//scale the image before drawing
				Image tileImg = GameImage.getImage(tileStr);
				SimpleRTS.offscr.drawImage(tileImg, 
						0 + x * imgTileWidth, SimpleRTS.screenHeight - 100 + y * imgTileHeight,
						imgTileWidth, imgTileHeight, null);
				
				if (tile == 8)
				{
					SimpleRTS.offscr.setColor(Color.BLUE);
				}
				else if (tile == 9)
				{
					SimpleRTS.offscr.setColor(Color.RED);
				}
				else
				{
					SimpleRTS.offscr.setColor(Color.GRAY);
				}
				SimpleRTS.offscr.fillRect(0 + x * imgTileWidth, SimpleRTS.screenHeight - 200 + y * imgTileHeight,
						imgTileWidth, imgTileHeight);
			}
		}
		
		//draw player's avatar and num units on the left
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_SUPPLY_PLAYER), 
				SimpleRTS.screenWidth/2 - 250, SimpleRTS.screenHeight - 100, 200, 100, null);
			
		GameFont.setFont(new Font("Comic Sans", Font.PLAIN, 32));
		SimpleRTS.offscr.setColor(Color.WHITE);
		GameFont.printString("" + SimpleRTS.playerList.size(), SimpleRTS.screenWidth/2 - 100, SimpleRTS.screenHeight - 100 + 65);	

		//draw enemy's avatar on the right
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_SUPPLY_ENEMY), 
				SimpleRTS.screenWidth/2 + 100, SimpleRTS.screenHeight - 100, 200, 100, null);
		
		//draw game timer in the middle
		GameFont.setFont(new Font("Comic Sans", Font.PLAIN, 20));
		SimpleRTS.offscr.drawImage(GameImage.getImage(GameImage.IMGID_GAME_TIMER), 
				SimpleRTS.screenWidth/2 - 50, SimpleRTS.screenHeight - 100, 150, 100, null);
		GameFont.printString("Day: " + gameTimer.getDay(), SimpleRTS.screenWidth/2 - 25, SimpleRTS.screenHeight - 100 + 25);
		GameFont.printString("" + gameTimer.getHour() + ":00", SimpleRTS.screenWidth/2 - 25, SimpleRTS.screenHeight - 100 + 50);
	}
}
