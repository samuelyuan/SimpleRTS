import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import graphics.GameImage;
import map.TileConverter;

public class GameImagePreloader {

    public static Map<Integer, GameImage> loadGameImages() {
        Map<Integer, GameImage> imgData = new HashMap<>();

        // Define image ID -> filename pairs
        Object[][] imageList = {
                { ImageConstants.IMGID_BG_MENU, "bg-menu.jpg" },
                { ImageConstants.IMGID_BG_DESK, "bg-desk.jpg" },
                { ImageConstants.IMGID_BG_BOOK, "bg-book.jpg" },
                { ImageConstants.IMGID_ICON_CAMPAIGN, "icon-campaign.png" },
                { ImageConstants.IMGID_ICON_INSTRUCT, "icon-instruct.png" },
                { ImageConstants.IMGID_ICON_RETURN, "icon-return.png" },
                { ImageConstants.IMGID_ICON_START, "icon-start.png" },
                { ImageConstants.IMGID_MENU_TITLE, "menu-title.png" },
                { ImageConstants.IMGID_MENU_INSTRUCT, "menu-instruct.png" },
                { ImageConstants.IMGID_MENU_START, "menu-start.png" },
                { ImageConstants.IMGID_MENU_VICTORY, "menu-victory.png" },
                { ImageConstants.IMGID_MENU_DEFEAT, "menu-defeat.png" },
                { ImageConstants.IMGID_SUPPLY_PLAYER, "game-supply-player.jpg" },
                { ImageConstants.IMGID_SUPPLY_ENEMY, "game-supply-enemy.jpg" },
                { ImageConstants.IMGID_GAME_TIMER, "game-timer.jpg" }
        };

        for (Object[] entry : imageList) {
            int id = (Integer) entry[0];
            String filename = (String) entry[1];
            loadImage(imgData, id, filename);
        }

        return imgData;
    }

    public static Map<String, GameImage> loadTileImages() {
        Map<String, GameImage> tileData = new HashMap<>();

        // Define tile name -> filename pairs using TileConverter constants
        String[][] tileList = {
                { TileConverter.STR_LAND, "tile-snow.jpg" },
                { TileConverter.STR_WALL, "tile-wall.jpg" },
                { TileConverter.STR_FLAG, "tile-flag.jpg" },
                { TileConverter.STR_UNIT_LIGHT_PLAYER, "unit-infantry-player.png" },
                { TileConverter.STR_UNIT_LIGHT_ENEMY, "unit-infantry-enemy.png" },
                { TileConverter.STR_UNIT_MEDIUM, "unit-antiarmor.png" },
                { TileConverter.STR_UNIT_HEAVY_PLAYER, "unit-tank-player.png" },
                { TileConverter.STR_UNIT_HEAVY_ENEMY, "unit-tank-enemy.png" }
        };

        for (String[] entry : tileList) {
            loadTile(tileData, entry[0], entry[1]);
        }

        return tileData;
    }

    private static String getImagePath(String filename) {
        return "../img/" + filename;
    }

    private static Image loadRawImage(String filename) {
        String fullPath = getImagePath(filename);
        try {
            File file = new File(fullPath);
            return ImageIO.read(file);
        } catch (Exception e) {
            System.out.println("Failed to load image " + fullPath + ", exception: " + e.getMessage());
            return null;
        }
    }

    private static void loadImage(Map<Integer, GameImage> imgData, int imageId, String filename) {
        Image newImage = loadRawImage(filename);
        if (newImage != null) {
            imgData.put(imageId, new GameImage(newImage));
        }
    }

    private static void loadTile(Map<String, GameImage> tileData, String imageStr, String filename) {
        Image newImage = loadRawImage(filename);
        if (newImage != null) {
            tileData.put(imageStr, new GameImage(newImage));
        }
    }
}
