import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import graphics.ImageUtils;
import graphics.Point;
import map.MapParseResult;
import map.TileConverter;

/*
 * This class basically changes the map array depending on which level needs to be loaded
 */
public class GameMap {
	public static int numLevel = 1;

	public static int mapdata[][];
	private static String drawData[][];

	public static String[][] getDrawData() {
		return drawData;
	}

	public static Map<Point, Integer> allyUnitPositions;
	public static Map<Point, Integer> enemyUnitPositions;
	public static Map<Point, Integer> flagPositions;

	public static Map<Point, Integer> getAllyUnitPositions() {
		return allyUnitPositions;
	}

	public static Map<Point, Integer> getEnemyUnitPositions() {
		return enemyUnitPositions;
	}

	public static Map<Point, Integer> getFlagPositions() {
		return flagPositions;
	}

	public static ArrayList<String> formatMapDescription(String rawLine, int numLevel, int maxLineWidth) {
		ArrayList<String> output = new ArrayList<>();

		String[] splitValues = rawLine.split(" ");
		if (splitValues.length == 0)
			return null;

		int levelNumber;
		try {
			levelNumber = Integer.parseInt(splitValues[0].substring(1, splitValues[0].length() - 1));
		} catch (Exception e) {
			return null;
		}

		if (numLevel != levelNumber)
			return null;

		String lineStr = "";
		for (int j = 1; j < splitValues.length; j++) {
			if (lineStr.length() + splitValues[j].length() > maxLineWidth) {
				output.add(lineStr.trim());
				lineStr = splitValues[j] + " ";
			} else {
				lineStr += splitValues[j] + " ";
			}
		}

		if (!lineStr.isEmpty()) {
			output.add(lineStr.trim());
		}

		return output;
	}

	public static ArrayList<String> loadMapDescription(int numLevel, boolean isBegin, int maxLineWidth) {
		ArrayList<String> data = parseFile("../maps/str/english.txt");
		int index = (isBegin) ? (numLevel - 1) * 2 : (numLevel - 1) * 2 + 1;
		return formatMapDescription(data.get(index), numLevel, maxLineWidth);
	}

	public static MapParseResult parseMapData(List<String> data) {
		int height = parseMapHeight(data);
		int width = parseMapWidth(data);

		int[][] mapdata = new int[height][width];
		String[][] drawData = new String[height][width];

		Map<Point, Integer> allyUnitPositions = new HashMap<>();
		Map<Point, Integer> enemyUnitPositions = new HashMap<>();
		Map<Point, Integer> flagPositions = new HashMap<>();

		populateMapArrays(data, mapdata, drawData, allyUnitPositions, enemyUnitPositions, flagPositions);

		return new MapParseResult(mapdata, drawData, allyUnitPositions, enemyUnitPositions, flagPositions);
	}

	private static int parseMapHeight(List<String> data) {
		return Integer.parseInt(data.get(0));
	}

	private static int parseMapWidth(List<String> data) {
		return Integer.parseInt(data.get(1));
	}

	private static int parseTile(
		String tileStr,
		int x, int y,
		Map<Point, Integer> allyUnitPositions,
		Map<Point, Integer> enemyUnitPositions,
		Map<Point, Integer> flagPositions
	) {
		return tileStrToId(tileStr, x, y, allyUnitPositions, enemyUnitPositions, flagPositions);
	}

	private static void populateMapArrays(
		List<String> data,
		int[][] mapdata,
		String[][] drawData,
		Map<Point, Integer> allyUnitPositions,
		Map<Point, Integer> enemyUnitPositions,
		Map<Point, Integer> flagPositions
	) {
		int height = mapdata.length;
		int width = mapdata[0].length;
		int tileCounter = 2;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				String tileStr = data.get(tileCounter);
				int tileId = parseTile(tileStr, x, y, allyUnitPositions, enemyUnitPositions, flagPositions);
				mapdata[y][x] = tileId;
				drawData[y][x] = tileStr;
				tileCounter++;
			}
		}
	}

	public static void loadMap() {
		String filename = "../maps/newmap" + numLevel + ".txt";
		List<String> data = parseFile(filename);

		if (data == null || data.size() < 2) {
			System.err.println("Error: Failed to load or parse map file: " + filename);
			return;
		}

		System.out.println("[INFO] Loaded map file: " + filename);
		System.out.println("[INFO] Line count: " + data.size());

		MapParseResult result;
		try {
			result = parseMapData(data);
		} catch (Exception e) {
			System.err.println("Error: Failed to parse map data: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		if (result.mapData == null || result.mapData.length == 0 || result.mapData[0].length == 0) {
			System.err.println("Error: Parsed map has invalid dimensions.");
			return;
		}

		// Apply parsed data
		mapdata = result.mapData;
		drawData = result.drawData;
		allyUnitPositions = result.allyUnitPositions;
		enemyUnitPositions = result.enemyUnitPositions;
		flagPositions = result.flagPositions;

		System.out.println("[INFO] Map dimensions: " + mapdata.length + " x " + mapdata[0].length);
		System.out.println("[INFO] Ally units: " + allyUnitPositions.size());
		System.out.println("[INFO] Enemy units: " + enemyUnitPositions.size());
		System.out.println("[INFO] Flags: " + flagPositions.size());

		exportImage();
	}

	public static int tileStrToId(
		String tileStr,
		int x, int y,
		Map<Point, Integer> allyUnitPositions,
		Map<Point, Integer> enemyUnitPositions,
		Map<Point, Integer> flagPositions
	) {
		if (tileStr.startsWith("Land")) {
			return parseLandTile(tileStr);
		}
		if (tileStr.startsWith("Wall")) {
			return parseWallTile(tileStr);
		}
		if (tileStr.startsWith("Unit")) {
			return parseUnitTile(tileStr, x, y, allyUnitPositions, enemyUnitPositions);
		}
		if (tileStr.startsWith("Flag")) {
			return parseFlagTile(tileStr, x, y, flagPositions);
		}
		// fallback
		return 0;
	}

	private static int parseLandTile(String tileStr) {
		return TileConverter.tileStrToBaseId("Land");
	}

	private static int parseWallTile(String tileStr) {
		return TileConverter.tileStrToBaseId("Wall");
	}

	private static int parseUnitTile(
		String tileStr, int x, int y,
		Map<Point, Integer> allyUnitPositions,
		Map<Point, Integer> enemyUnitPositions
	) {
		Point pos = new Point(x, y);
		String unitInfo = tileStr.substring("Unit ".length()); // e.g., "+1 2" or "-1 1"
		String[] parts = unitInfo.trim().split(" ");
		if (parts.length == 2) {
			String faction = parts[0];
			int type = Integer.parseInt(parts[1]);
			if (faction.equals("+1")) {
				allyUnitPositions.put(pos, type);
				return type + 1;
			} else if (faction.equals("-1")) {
				enemyUnitPositions.put(pos, type);
				return type + 4;
			}
		}
		return 0;
	}

	private static int parseFlagTile(
		String tileStr, int x, int y,
		Map<Point, Integer> flagPositions
	) {
		Point pos = new Point(x, y);
		if (tileStr.contains("+1")) {
			flagPositions.put(pos, 1);
			return TileConverter.TILE_FLAG_ALLY;
		} else if (tileStr.contains("-1")) {
			flagPositions.put(pos, -1);
			return TileConverter.TILE_FLAG_ENEMY;
		}
		return 0;
	}

	/*
	 * //convert from the old format to the new one
	 * public static void updateMapFile(int numLevel)
	 * {
	 * ArrayList<String> data = new ArrayList<String>();
	 * 
	 * int mapWidth = mapdata[0].length, mapHeight = mapdata.length;
	 * data.add(String.valueOf(mapHeight));
	 * data.add(String.valueOf(mapWidth));
	 * 
	 * for (int y = 0; y < mapHeight; y++)
	 * {
	 * for (int x = 0; x < mapWidth; x++)
	 * {
	 * int tile = mapdata[y][x];
	 * if (tile == 0) { data.add("Land"); }
	 * else if (tile == 1) { data.add("Wall"); }
	 * else if (tile == 2) { data.add("Unit +1 1"); }
	 * else if (tile == 3) { data.add("Unit +1 2"); }
	 * else if (tile == 4) { data.add("Unit +1 3"); }
	 * else if (tile == 5) { data.add("Unit -1 1"); }
	 * else if (tile == 6) { data.add("Unit -1 2"); }
	 * else if (tile == 7) { data.add("Unit -1 3"); }
	 * else if (tile == 8) { data.add("Flag +1"); }
	 * else if (tile == 9) { data.add("Flag -1"); }
	 * }
	 * }
	 * 
	 * saveFile(data, new File("../maps/newmap" + numLevel + ".txt"));
	 * }
	 */
	public static ArrayList<String> parseFile(String filename) {
		ArrayList<String> data = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = br.readLine()) != null) {
				data.add(line);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	public static void saveFile(ArrayList<String> data, File file) {
		try {
			PrintWriter out = new PrintWriter(file);
			for (String str : data) {
				out.println(str);
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
		ImageIO.write(im, "png", new File(filename));
	}

	public static void exportImage() {
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
