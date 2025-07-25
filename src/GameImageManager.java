import java.util.HashMap;
import java.util.Map;

import graphics.GameImage;
import graphics.ImageUtils;

public class GameImageManager {
    public static Map<Integer, GameImage> imgData = new HashMap<>();
    public static Map<String, GameImage> tileData = new HashMap<>();

    public static Map<Integer, GameImage> darkImgData = new HashMap<>();
    public static Map<String, GameImage> darkTileData = new HashMap<>();

    public static void generateDarkImages() {
        for (Map.Entry<Integer, GameImage> entry : imgData.entrySet()) {
            darkImgData.put(entry.getKey(), ImageUtils.darken(entry.getValue()));
        }
        for (Map.Entry<String, GameImage> entry : tileData.entrySet()) {
            darkTileData.put(entry.getKey(), ImageUtils.darken(entry.getValue()));
        }
    }

    public static GameImage getImage(int imageId) {
        return imgData.get(imageId);
    }

    public static GameImage getImage(String imageStr) {
        return tileData.get(imageStr);
    }

    public static GameImage getImage(String imageStr, boolean isDark) {
        return isDark ? darkTileData.get(imageStr) : tileData.get(imageStr);
    }

    public static void setImgData(Map<Integer, GameImage> data) {
        imgData = data;
    }

    public static void setTileData(Map<String, GameImage> data) {
        tileData = data;
    }
}
