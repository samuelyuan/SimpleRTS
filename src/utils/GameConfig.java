package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Centralized configuration management for game settings
 */
public class GameConfig {
    private static final String CONFIG_FILE = "game.properties";
    private static final Properties properties = new Properties();
    private static boolean initialized = false;
    
    // Default values - merged from GameSettings and essential settings
    private static final String[][] DEFAULTS = {
        // FOV settings (from GameSettings)
        {"fov.rendering_enabled", "false"},
        {"fov.show_enemy_units", "false"},
        {"fov.show_selected_only", "true"},
        
        // Debug settings
        {"debug.mode", "false"},
        {"debug.show_fps", "false"},
        {"debug.log_level", "INFO"},
        
        // Pathfinding debug settings
        {"debug.show_paths", "false"},
        {"debug.show_all_map_nodes", "false"},
        {"debug.show_node_costs", "false"}
    };
    
    public static void initialize() {
        if (initialized) return;
        
        loadDefaults();
        loadFromFile();
        initialized = true;
    }
    
    private static void loadDefaults() {
        for (String[] defaultSetting : DEFAULTS) {
            properties.setProperty(defaultSetting[0], defaultSetting[1]);
        }
    }
    
    private static void loadFromFile() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
            Logger.info("Loaded configuration from " + CONFIG_FILE);
        } catch (IOException e) {
            Logger.info("No configuration file found, using defaults");
        }
    }
    
    public static void save() {
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "SimpleRTS Configuration");
            Logger.info("Configuration saved to " + CONFIG_FILE);
        } catch (IOException e) {
            Logger.error("Failed to save configuration", e);
        }
    }
    
    public static String getString(String key) {
        return properties.getProperty(key, "");
    }
    
    public static int getInt(String key) {
        try {
            return Integer.parseInt(properties.getProperty(key, "0"));
        } catch (NumberFormatException e) {
            Logger.warn("Invalid integer value for " + key + ", using 0");
            return 0;
        }
    }
    
    public static double getDouble(String key) {
        try {
            return Double.parseDouble(properties.getProperty(key, "0.0"));
        } catch (NumberFormatException e) {
            Logger.warn("Invalid double value for " + key + ", using 0.0");
            return 0.0;
        }
    }
    
    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(properties.getProperty(key, "false"));
    }
    
    public static void setString(String key, String value) {
        properties.setProperty(key, value);
    }
    
    public static void setInt(String key, int value) {
        properties.setProperty(key, String.valueOf(value));
    }
    
    public static void setDouble(String key, double value) {
        properties.setProperty(key, String.valueOf(value));
    }
    
    public static void setBoolean(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
    }
    
    // ===== CONVENIENCE METHODS FOR FOV SETTINGS =====
    
    public static boolean isFovRenderingEnabled() {
        return getBoolean("fov.rendering_enabled");
    }
    
    public static boolean isFovShowEnemyUnits() {
        return getBoolean("fov.show_enemy_units");
    }
    

    
    public static void setFovRenderingEnabled(boolean enabled) {
        setBoolean("fov.rendering_enabled", enabled);
    }
    
    public static void setFovShowEnemyUnits(boolean enabled) {
        setBoolean("fov.show_enemy_units", enabled);
    }
    

    
    public static void toggleFovRendering() {
        setFovRenderingEnabled(!isFovRenderingEnabled());
    }
    
    public static void toggleFovShowEnemyUnits() {
        setFovShowEnemyUnits(!isFovShowEnemyUnits());
    }
    

    
    // ===== CONVENIENCE METHODS FOR DEBUG SETTINGS =====
    
    public static boolean isDebugMode() {
        return getBoolean("debug.mode");
    }
    
    public static boolean isShowFPS() {
        return getBoolean("debug.show_fps");
    }
    
    public static void setDebugMode(boolean enabled) {
        setBoolean("debug.mode", enabled);
    }
    
    public static void setShowFPS(boolean enabled) {
        setBoolean("debug.show_fps", enabled);
    }
    
    // ===== PATHFINDING DEBUG SETTINGS =====
    
    public static boolean isShowPaths() {
        return getBoolean("debug.show_paths");
    }
    
    public static boolean isShowAllMapNodes() {
        return getBoolean("debug.show_all_map_nodes");
    }

    public static boolean isShowNodeCosts() {
        return getBoolean("debug.show_node_costs");
    }
    
    public static void setShowPaths(boolean enabled) {
        setBoolean("debug.show_paths", enabled);
    }
    
    public static void setShowAllMapNodes(boolean enabled) {
        setBoolean("debug.show_all_map_nodes", enabled);
    }

    public static void setShowNodeCosts(boolean enabled) {
        setBoolean("debug.show_node_costs", enabled);
    }
    
    // ===== UTILITY METHODS =====
    
    public static String getFovStatusString() {
        StringBuilder status = new StringBuilder();
        status.append("FOV: ").append(isFovRenderingEnabled() ? "ON" : "OFF");
        if (isFovRenderingEnabled()) {
            status.append(" | Enemy: ").append(isFovShowEnemyUnits() ? "ON" : "OFF");

        }
        return status.toString();
    }
    
    public static String getDebugStatusString() {
        StringBuilder status = new StringBuilder();
        status.append("Debug: ").append(isDebugMode() ? "ON" : "OFF");
        status.append(" | FPS: ").append(isShowFPS() ? "ON" : "OFF");
        return status.toString();
    }
    
    public static void resetToDefaults() {
        loadDefaults();
        Logger.info("Game settings reset to defaults");
    }
} 