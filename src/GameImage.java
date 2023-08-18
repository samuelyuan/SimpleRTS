import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;


public class GameImage {
	public static Map<Integer, Image> imgData = new HashMap<Integer, Image>();
	public static Map<String, Image> tileData = new HashMap<String, Image>();
	
	public static Map<Integer, Image> darkImgData = new HashMap<Integer, Image>();
	public static Map<String, Image> darkTileData = new HashMap<String, Image>();
	
	public static final int IMGID_BG_MENU = 6;
	public static final int IMGID_BG_DESK = 7;
	public static final int IMGID_BG_BOOK = 8;
	public static final int IMGID_ICON_CAMPAIGN = 9;
	public static final int IMGID_ICON_INSTRUCT = 10;
	public static final int IMGID_ICON_RETURN = 11;
	public static final int IMGID_ICON_START = 12;
	public static final int IMGID_MENU_TITLE = 13;
	public static final int IMGID_MENU_INSTRUCT = 14;
	public static final int IMGID_MENU_START = 15;
	public static final int IMGID_MENU_VICTORY = 16;
	public static final int IMGID_MENU_DEFEAT = 17;
	public static final int IMGID_SUPPLY_PLAYER = 18;
	public static final int IMGID_SUPPLY_ENEMY = 19;
	public static final int IMGID_GAME_TIMER = 20;
	
	public static void loadAllImages()
	{
		loadTile("Land", "tile-snow.jpg");
		loadTile("Wall", "tile-wall.jpg");
		loadTile("Flag", "tile-flag.jpg");
		loadTile("Unit Light Player", "unit-infantry-player.png");
		loadTile("Unit Light Enemy", "unit-infantry-enemy.png");
		loadTile("Unit Medium", "unit-antiarmor.png");
		loadTile("Unit Heavy Player", "unit-tank-player.png");
		loadTile("Unit Heavy Enemy", "unit-tank-enemy.png");
		
		loadImage(IMGID_BG_MENU, "bg-menu.jpg");
		loadImage(IMGID_BG_DESK, "bg-desk.jpg");
		loadImage(IMGID_BG_BOOK, "bg-book.jpg");
		loadImage(IMGID_ICON_CAMPAIGN, "icon-campaign.png");
		loadImage(IMGID_ICON_INSTRUCT, "icon-instruct.png");
		loadImage(IMGID_ICON_RETURN, "icon-return.png");
		loadImage(IMGID_ICON_START, "icon-start.png");
		loadImage(IMGID_MENU_TITLE, "menu-title.png");
		loadImage(IMGID_MENU_INSTRUCT, "menu-instruct.png");
		loadImage(IMGID_MENU_START, "menu-start.png");
		loadImage(IMGID_MENU_VICTORY, "menu-victory.png");
		loadImage(IMGID_MENU_DEFEAT, "menu-defeat.png");
		loadImage(IMGID_SUPPLY_PLAYER, "game-supply-player.jpg");
		loadImage(IMGID_SUPPLY_ENEMY, "game-supply-enemy.jpg");
		loadImage(IMGID_GAME_TIMER, "game-timer.jpg");
	}
	
	public static void loadImage(int imageId, String filename) 
	{
		String fullPath = "../img/" + filename;
		try 
		{
	        File file = new File(fullPath);
			Image newImage = ImageIO.read(file);
			imgData.put(imageId, newImage);
			
			darkImgData.put(imageId, darkenTile(newImage));
		}
		catch (Exception e)
		{
			System.out.println("Failed to load image " + fullPath + ", exception: " + e.getMessage());
		}
	}
	
	public static void loadTile(String imageStr, String filename) 
	{
		String fullPath = "../img/" + filename;
		try 
		{
	        File file = new File(fullPath);
			Image newImage = ImageIO.read(file);
			tileData.put(imageStr, newImage);
			
			BufferedImage darkImage = darkenTile(newImage);
			darkTileData.put(imageStr, darkImage);
		}
		catch (Exception e)
		{
			System.out.println("Failed to load tile " + fullPath + ", exception: " +e.getMessage());
		}
	}
	
	public static Image getImage(int imageId)
	{
		return imgData.get(imageId);
	}
	
	public static Image getImage(String imageStr, boolean isDark)
	{
		if (isDark)
			return darkTileData.get(imageStr);
			
		return tileData.get(imageStr);
	}
	
	public static Image getImage(String imageStr)
	{
		return tileData.get(imageStr);
	}
	
	public static BufferedImage darkenTile(Image img)
	{
		BufferedImage bImage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
		
		Graphics g = bImage.createGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		
		for (int y = 0; y < bImage.getHeight(); y++)
		{
			for (int x = 0; x < bImage.getWidth(); x++)
			{
				bImage.setRGB(x, y, darkRGB(bImage.getRGB(x, y)));
			}
		}
		
		return bImage;
	}
	
	public static int darkRGB(int rgb) {
	    int r = (rgb >> 16) & 255;   // red
	    int g = (rgb >> 8) & 255;    // green
	    int b = rgb & 255;           // blue
	    // now reduce brightness of all channels to 1/3
	    r /= 3;
	    g /= 3;
	    b /= 3;
	    // recombine channels and return
	    return (r << 16) | (g << 8) | b;
	}
}
