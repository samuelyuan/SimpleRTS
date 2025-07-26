import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import graphics.ImageUtils;
import map.MapFileLoader;
import map.TileConverter;

public class MapImageExporter {
    public static String[][] generateMapTileStrings(int[][] mapData) {
        int mapWidth = mapData[0].length, mapHeight = mapData.length;
        String[][] tileStrings = new String[mapHeight][mapWidth];

        for (int y = 0; y < mapHeight; ++y) {
            for (int x = 0; x < mapWidth; ++x) {
                int tileInt = mapData[y][x];
                tileStrings[y][x] = TileConverter.tileIntToStr(tileInt);
            }
        }
        return tileStrings;
    }

    public static BufferedImage createMapImage(String[][] tileStrings) {
        int imgTileWidth = 8, imgTileHeight = 8;
        int mapHeight = tileStrings.length, mapWidth = tileStrings[0].length;

        BufferedImage im = new BufferedImage(mapWidth * imgTileWidth, mapHeight * imgTileHeight, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < mapHeight; ++y) {
            for (int x = 0; x < mapWidth; ++x) {
                String tileStr = tileStrings[y][x];

                Image tileImg = (java.awt.Image) GameImageManager.getImage(tileStr).getBackendImage();
                tileImg = ImageUtils.scale((BufferedImage) tileImg, BufferedImage.TYPE_INT_ARGB, imgTileWidth, imgTileHeight,
                        (double) imgTileWidth / Constants.TILE_WIDTH,
                        (double) imgTileHeight / Constants.TILE_HEIGHT);

                im.getGraphics().drawImage(tileImg, x * imgTileWidth, y * imgTileHeight, null);
            }
        }

        return im;
    }

    public static void writeMapImage(BufferedImage im, String filename) throws IOException {
        MapFileLoader.saveFile(im, new File(filename));
    }

    public static void exportImage(int[][] mapdata, int numLevel) {
        if (mapdata == null || mapdata.length == 0 || mapdata[0].length == 0) {
            System.err.println("Error: No map data to export.");
            return;
        }

        try {
            String[][] tileStrings = generateMapTileStrings(mapdata);
            BufferedImage im = createMapImage(tileStrings);
            writeMapImage(im, "../maps/export/map" + numLevel + ".png");
            System.out.println("[INFO] Map image exported.");
        } catch (IOException e) {
            System.err.println("Error exporting map image: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 