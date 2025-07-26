import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import graphics.Point;
import map.MapFileLoader;
import map.MapParseResult;
import map.MapParser;

/*
 * This class basically changes the map array depending on which level needs to be loaded
 */
public class GameMap {
	public int numLevel = 1;

	public int mapdata[][];
	private String drawData[][];

	public String[][] getDrawData() {
		return drawData;
	}

	public Map<Point, Integer> allyUnitPositions;
	public Map<Point, Integer> enemyUnitPositions;
	public Map<Point, Integer> flagPositions;

	public Map<Point, Integer> getAllyUnitPositions() {
		return allyUnitPositions;
	}

	public Map<Point, Integer> getEnemyUnitPositions() {
		return enemyUnitPositions;
	}

	public Map<Point, Integer> getFlagPositions() {
		return flagPositions;
	}

	public GameMap() {
		// Default constructor
	}

	public ArrayList<String> formatMapDescription(String rawLine, int numLevel, int maxLineWidth) {
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

	public ArrayList<String> loadMapDescription(int numLevel, boolean isBegin, int maxLineWidth) {
		ArrayList<String> data = MapFileLoader.parseFile("../maps/str/english.txt");
		int index = (isBegin) ? (numLevel - 1) * 2 : (numLevel - 1) * 2 + 1;
		return formatMapDescription(data.get(index), numLevel, maxLineWidth);
	}

	public MapParseResult parseMapData(List<String> data) {
		return MapParser.parseMapData(data);
	}

	public void loadMap() {
		String filename = "../maps/newmap" + numLevel + ".txt";
		List<String> data = MapFileLoader.parseFile(filename);

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

	public int tileStrToId(
		String tileStr,
		int x, int y,
		Map<Point, Integer> allyUnitPositions,
		Map<Point, Integer> enemyUnitPositions,
		Map<Point, Integer> flagPositions
	) {
		return MapParser.tileStrToId(tileStr, x, y, allyUnitPositions, enemyUnitPositions, flagPositions);
	}

	public void exportImage() {
		MapImageExporter.exportImage(mapdata, numLevel);
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
}
