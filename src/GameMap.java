import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import graphics.Point;
import map.MapParseResult;
import map.MapDescriptionLoader;
import map.MapJsonParser;
import utils.Logger;

/*
 * This class basically changes the map array depending on which level needs to be loaded
 */
public class GameMap {
	private int numLevel = 1;
	private final ImageService imageService;

	private int mapData[][];
	private String drawData[][];

	public String[][] getDrawData() {
		return drawData;
	}

	private Map<Point, Integer> allyUnitPositions;
	private Map<Point, Integer> enemyUnitPositions;
	private Map<Point, Integer> flagPositions;

	public Map<Point, Integer> getAllyUnitPositions() {
		return allyUnitPositions;
	}

	public Map<Point, Integer> getEnemyUnitPositions() {
		return enemyUnitPositions;
	}

	public Map<Point, Integer> getFlagPositions() {
		return flagPositions;
	}

	// Getter and setter for numLevel
	public int getNumLevel() {
		return numLevel;
	}

	public void setNumLevel(int numLevel) {
		this.numLevel = numLevel;
	}

	// Getter for mapData
	public int[][] getMapData() {
		return mapData;
	}

	public GameMap(ImageService imageService) {
		this.imageService = imageService;
	}

	public ArrayList<String> formatMapDescription(String rawLine, int numLevel, int maxLineWidth) {
		// This method is kept for backward compatibility but now delegates to the new JSON-based system
		return loadMapDescription(numLevel, rawLine.contains("a"), maxLineWidth);
	}

	public ArrayList<String> loadMapDescription(int numLevel, boolean isBegin, int maxLineWidth) {
		// Use the new JSON-based description loader
		String description = MapDescriptionLoader.getDescription(numLevel, isBegin);
		if (description == null) {
			Logger.warn("No description found for level " + numLevel + " (" + (isBegin ? "begin" : "end") + ")");
			return new ArrayList<>();
		}
		
		List<String> formattedLines = MapDescriptionLoader.formatDescription(description, maxLineWidth);
		return new ArrayList<>(formattedLines);
	}

	public void loadMap() {
		String jsonFilename = "../maps/newmap" + numLevel + ".json";
		
		MapParseResult result = null;
		
		try {
			result = MapJsonParser.parseMapDataFromJsonFile(jsonFilename);
			Logger.info("Loaded JSON map file: " + jsonFilename);
		} catch (Exception e) {
			Logger.error("Failed to load or parse JSON map file: " + jsonFilename);
			Logger.error("Error details: " + e.getMessage());
			e.printStackTrace();
			return;
		}

		if (result.mapData == null || result.mapData.length == 0 || result.mapData[0].length == 0) {
			Logger.error("Parsed map has invalid dimensions.");
			return;
		}

		// Apply parsed data
		mapData = result.mapData;
		drawData = result.drawData;
		allyUnitPositions = result.allyUnitPositions;
		enemyUnitPositions = result.enemyUnitPositions;
		flagPositions = result.flagPositions;

		Logger.info("Map dimensions: " + mapData.length + " x " + mapData[0].length);
		Logger.info("Ally units: " + allyUnitPositions.size());
		Logger.info("Enemy units: " + enemyUnitPositions.size());
		Logger.info("Flags: " + flagPositions.size());

		// exportImage();
	}


	public void exportImage() {
		MapImageExporter.exportImage(mapData, numLevel, imageService);
	}

	/**
	 * Gets all available level numbers
	 * @return List of available level numbers
	 */
	public List<Integer> getAvailableLevels() {
		return MapDescriptionLoader.getAvailableLevels();
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
