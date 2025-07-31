package map;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONObject;
import org.json.JSONException;
import utils.PathResolver;

public class MapDescriptionLoader {
    
    private static JSONObject descriptionsData = null;
    private static final String DESCRIPTIONS_FILE = "../maps/str/descriptions.json";
    
    /**
     * Loads the JSON descriptions file and caches it for future use
     */
    private static void loadDescriptionsFile() {
        if (descriptionsData != null) {
            return; // Already loaded
        }
        
        try {
            String resolvedPath = PathResolver.resolveMapPath(DESCRIPTIONS_FILE);
            StringBuilder content = new StringBuilder();
            
            try (BufferedReader br = new BufferedReader(new FileReader(resolvedPath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    content.append(line);
                }
            }
            
            descriptionsData = new JSONObject(content.toString());
            System.out.println("[INFO] Loaded map descriptions from: " + resolvedPath);
            
        } catch (IOException e) {
            System.err.println("Error: Failed to load descriptions file: " + DESCRIPTIONS_FILE);
            e.printStackTrace();
            descriptionsData = new JSONObject(); // Empty object as fallback
        } catch (JSONException e) {
            System.err.println("Error: Invalid JSON in descriptions file: " + DESCRIPTIONS_FILE);
            e.printStackTrace();
            descriptionsData = new JSONObject(); // Empty object as fallback
        }
    }
    
    /**
     * Gets the description for a specific level and phase
     * @param levelNumber The level number (1-based)
     * @param isBegin True for beginning description, false for end description
     * @return The description text, or null if not found
     */
    public static String getDescription(int levelNumber, boolean isBegin) {
        loadDescriptionsFile();
        
        try {
            String levelKey = String.valueOf(levelNumber);
            if (!descriptionsData.has("levels") || !descriptionsData.getJSONObject("levels").has(levelKey)) {
                System.err.println("Warning: No description found for level " + levelNumber);
                return null;
            }
            
            JSONObject levelData = descriptionsData.getJSONObject("levels").getJSONObject(levelKey);
            String descriptionKey = isBegin ? "begin" : "end";
            
            if (!levelData.has(descriptionKey)) {
                System.err.println("Warning: No " + descriptionKey + " description found for level " + levelNumber);
                return null;
            }
            
            return levelData.getString(descriptionKey);
            
        } catch (JSONException e) {
            System.err.println("Error: Failed to parse description for level " + levelNumber);
            e.printStackTrace();
            return null;
        }
    }
    

    
    /**
     * Gets all available level numbers
     * @return List of available level numbers
     */
    public static List<Integer> getAvailableLevels() {
        loadDescriptionsFile();
        List<Integer> levels = new ArrayList<>();
        
        try {
            if (descriptionsData.has("levels")) {
                JSONObject levelsObj = descriptionsData.getJSONObject("levels");
                for (String key : levelsObj.keySet()) {
                    try {
                        levels.add(Integer.parseInt(key));
                    } catch (NumberFormatException e) {
                        System.err.println("Warning: Invalid level key: " + key);
                    }
                }
            }
        } catch (JSONException e) {
            System.err.println("Error: Failed to parse available levels");
            e.printStackTrace();
        }
        
        return levels;
    }
    
    /**
     * Formats a description text to fit within a specified line width
     * @param text The text to format
     * @param maxLineWidth Maximum characters per line
     * @return List of formatted lines
     */
    public static List<String> formatDescription(String text, int maxLineWidth) {
        List<String> lines = new ArrayList<>();
        
        if (text == null || text.trim().isEmpty()) {
            return lines;
        }
        
        String[] words = text.split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 <= maxLineWidth) {
                if (currentLine.length() > 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString().trim());
                    currentLine = new StringBuilder(word);
                } else {
                    // Word is longer than maxLineWidth, add it anyway
                    lines.add(word);
                }
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }
        
        return lines;
    }
} 