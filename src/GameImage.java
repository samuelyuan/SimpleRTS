import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import graphics.ImageUtils;

public class GameImage {
    public static Map<Integer, Image> imgData = new HashMap<>();
    public static Map<String, Image> tileData = new HashMap<>();

    public static Map<Integer, Image> darkImgData = new HashMap<>();
    public static Map<String, Image> darkTileData = new HashMap<>();

    public static void generateDarkImages() {
        for (Map.Entry<Integer, Image> entry : imgData.entrySet()) {
            darkImgData.put(entry.getKey(), ImageUtils.darken(entry.getValue()));
        }
        for (Map.Entry<String, Image> entry : tileData.entrySet()) {
            darkTileData.put(entry.getKey(), ImageUtils.darken(entry.getValue()));
        }
    }

    public static Image getImage(int imageId) {
        return imgData.get(imageId);
    }

    public static Image getImage(String imageStr) {
        return tileData.get(imageStr);
    }

    public static Image getImage(String imageStr, boolean isDark) {
        return isDark ? darkTileData.get(imageStr) : tileData.get(imageStr);
    }

    public static void setImgData(Map<Integer, Image> data) {
        imgData = data;
    }

    public static void setTileData(Map<String, Image> data) {
        tileData = data;
    }
}
