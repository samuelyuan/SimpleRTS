import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

/*
 * This class basically changes the map array depending on which level needs to be loaded
 */
public class GameMap 
{
	public static int numLevel = 1;
	
	public static int mapdata[][];
	private static String drawData[][];
	public static String[][] getDrawData() { return drawData; }

	public static final int TILE_WIDTH = 50;
	public static final int TILE_HEIGHT = 50;
	
	public static final int TILE_WALL = 1;
	public static final int TILE_ALLY_FLAG = 8;
	public static final int TILE_ENEMY_FLAG = 9;
	
	public static Map<Point, Integer> allyUnitPositions;
	public static Map<Point, Integer> enemyUnitPositions;
	public static Map<Point, Integer> flagPositions;
	public static Map<Point, Integer> getAllyUnitPositions() { return allyUnitPositions; }
	public static Map<Point, Integer> getEnemyUnitPositions() { return enemyUnitPositions; }
	public static Map<Point, Integer> getFlagPositions() { return flagPositions; }
	
	public static ArrayList<String> loadMapDescription(int numLevel, boolean isBegin, int maxLineWidth)
	{
		ArrayList<String> data = parseFile("../maps/str/english.txt");
		ArrayList<String> output = new ArrayList<String>();

		int index = (isBegin) ? (numLevel - 1) * 2 : (numLevel - 1) * 2 + 1; //1a, 1b 
		
		String[] splitValues = data.get(index).split(" ");
		int levelNumber = Integer.parseInt(splitValues[0].substring(1, splitValues[0].length() - 1)); //level is in the format #num, so truncate #
		
		//a level description is missing, so this is a bug
		if (numLevel != levelNumber)
			return null;
		
		String lineStr = "";
		//start from index 1 because index 0 contains the level number, which nobody needs to see
		for (int j = 1; j < splitValues.length; j++)
		{
			if (lineStr.length() + splitValues[j].length() > maxLineWidth)
			{
				//move to new line
				output.add(lineStr);
				lineStr = splitValues[j] + " ";
			}
			else
			{
				lineStr += splitValues[j] + " ";
			}
		}
		
		output.add(lineStr);
		
		return output;
	}
	
	public static void loadMap()
	{
		int width = 0, height = 0;
		
		ArrayList<String> data = parseFile("../maps/newmap" + numLevel + ".txt");
		
		//first two lines contain height and width
		height = Integer.parseInt(data.get(0));
		width = Integer.parseInt(data.get(1));
		
		mapdata = new int[height][width];
		drawData = new String[height][width];

		//load the current map from the remaining file
		int tileCounter = 2;
		allyUnitPositions = new HashMap<Point, Integer>();
		enemyUnitPositions = new HashMap<Point, Integer>();
		flagPositions = new HashMap<Point, Integer>();
		
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				//new format
				String tileStr = data.get(tileCounter);
				int tileId = tileStrToId(tileStr, x , y);
				mapdata[y][x] = tileId;
				drawData[y][x] = tileStr;
				
				//old format
				//mapdata[y][x] = Integer.parseInt(data.get(tileCounter));
				tileCounter++;
			}
		}
		
		GameFogWar.init(height, width);
		
		//updateMapFile(numLevel);
		exportImage();
	}
	
	public static int tileStrToId(String tileStr, int x, int y)
	{
		int tileId = 0;

		//legend
		//0 - snow
		//1 - wall
		//2,3,4 - player units
		//5,6,7 - enemy units
		//8 - player flag
		//9 - enemy flag
		if (tileStr.contains("Land"))
		{
			tileId = 0;
		}
		else if (tileStr.contains("Wall"))
		{
			tileId = 1;
		}
		else if (tileStr.contains("Unit"))
		{
			tileStr = tileStr.substring("Unit ".length());
			if (tileStr.startsWith("+1"))
			{
				tileStr = tileStr.substring("+1 ".length());
				tileId = Integer.parseInt(tileStr) + 1;
				
				allyUnitPositions.put(new Point(x, y), Integer.parseInt(tileStr));
			}
			else if (tileStr.startsWith("-1"))
			{
				tileStr = tileStr.substring("-1 ".length());
				tileId = Integer.parseInt(tileStr) + 4;
				
				enemyUnitPositions.put(new Point(x, y), Integer.parseInt(tileStr));
			}
		}
		else if (tileStr.contains("Flag"))
		{
			if (tileStr.contains("+1"))
			{
				tileId = TILE_ALLY_FLAG;
				flagPositions.put(new Point(x, y), 1);
			}
			else if (tileStr.contains("-1"))
			{
				tileId = TILE_ENEMY_FLAG;
				flagPositions.put(new Point(x, y), -1);
			}
		}
		
		return tileId;
	}
	
	/*//convert from the old format to the new one
	public static void updateMapFile(int numLevel)
	{
		ArrayList<String> data = new ArrayList<String>();
	
		int mapWidth = mapdata[0].length, mapHeight = mapdata.length;
		data.add(String.valueOf(mapHeight));
		data.add(String.valueOf(mapWidth));
		
		for (int y = 0; y < mapHeight; y++)
		{
			for (int x = 0; x < mapWidth; x++)
			{
				int tile = mapdata[y][x];
				if (tile == 0) { data.add("Land"); }
				else if (tile == 1) { data.add("Wall"); }
				else if (tile == 2) { data.add("Unit +1 1"); }
				else if (tile == 3) { data.add("Unit +1 2"); }
				else if (tile == 4) { data.add("Unit +1 3"); }
				else if (tile == 5) { data.add("Unit -1 1"); }
				else if (tile == 6) { data.add("Unit -1 2"); }
				else if (tile == 7) { data.add("Unit -1 3"); }
				else if (tile == 8) { data.add("Flag +1"); }
				else if (tile == 9) { data.add("Flag -1"); }
			}
		}
		
		saveFile(data, new File("../maps/newmap" + numLevel + ".txt"));
	}
	*/
	public static ArrayList<String> parseFile(String filename) 
	{
		ArrayList<String> data = new ArrayList<String>();
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) 
			{
				data.add(line);
			}
			br.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return data;
	}

	public static void saveFile(ArrayList<String> data, File file) 
	{
		try 
		{
			PrintWriter out = new PrintWriter(file);
			for (String str : data)
				out.println(str);
			out.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * scale image
	 * 
	 * @param sbi image to scale
	 * @param imageType type of image
	 * @param dWidth width of destination image
	 * @param dHeight height of destination image
	 * @param fWidth x-factor for transformation / scaling
	 * @param fHeight y-factor for transformation / scaling
	 * @return scaled image
	 */
	public static BufferedImage scale(BufferedImage sbi, int imageType, int dWidth, int dHeight, double fWidth, double fHeight) {
	    BufferedImage dbi = null;
	    if(sbi != null) {
	        dbi = new BufferedImage(dWidth, dHeight, imageType);
	        Graphics2D g = dbi.createGraphics();
	        AffineTransform at = AffineTransform.getScaleInstance(fWidth, fHeight);
	        g.drawRenderedImage(sbi, at);
	    }
	    return dbi;
	}
	
	public static void exportImage() 
	{
		try 
		{
			int imgTileWidth = 8, imgTileHeight = 8;
			int mapWidth = mapdata[0].length, mapHeight = mapdata.length;
			BufferedImage im = new BufferedImage(mapdata[0].length * imgTileWidth, mapdata.length * imgTileHeight, 
					BufferedImage.TYPE_INT_ARGB);
			
			for (int y = 0; y < mapHeight; ++y)
			{
				for (int x = 0; x < mapWidth; ++x)
				{
					int tile = mapdata[y][x];
					String tileStr = "";
					
					if (tile == 0) { tileStr = "Land"; }
					else if (tile == TILE_WALL) { tileStr = "Wall"; } 
					else if (tile == 2) { tileStr = "Unit Light Player"; }
					else if (tile == 5) { tileStr = "Unit Light Enemy"; }
					else if (tile == 3 || tile == 6) { tileStr = "Unit Medium"; }
					else if (tile == 4) { tileStr = "Unit Heavy Player"; }
					else if (tile == 7) { tileStr = "Unit Heavy Enemy"; }
					else if (tile == TILE_ALLY_FLAG || tile == TILE_ENEMY_FLAG) { tileStr = "Flag"; }
					
					//scale the image before drawing
					Image tileImg = GameImage.getImage(tileStr);
					tileImg = scale((BufferedImage)tileImg, BufferedImage.TYPE_INT_ARGB, imgTileWidth, imgTileHeight, 
							(double)imgTileWidth/GameMap.TILE_WIDTH, (double)imgTileHeight/GameMap.TILE_HEIGHT);
					
					im.getGraphics().drawImage(tileImg, x * imgTileWidth, y * imgTileHeight, null);
				}
			}
					
			ImageIO.write(im, "png", new File("../maps/export/map" + numLevel + ".png"));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
