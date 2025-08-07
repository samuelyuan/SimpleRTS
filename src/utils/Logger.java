package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logging utility to replace scattered System.out.println statements
 */
public class Logger {
    public enum Level {
        DEBUG, INFO, WARN, ERROR
    }
    
    private static Level currentLevel = Level.INFO;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    public static void setLevel(Level level) {
        currentLevel = level;
    }
    
    public static void debug(String message) {
        log(Level.DEBUG, message);
    }
    
    public static void info(String message) {
        log(Level.INFO, message);
    }
    
    public static void warn(String message) {
        log(Level.WARN, message);
    }
    
    public static void error(String message) {
        log(Level.ERROR, message);
    }
    
    public static void error(String message, Throwable throwable) {
        log(Level.ERROR, message + ": " + throwable.getMessage());
    }
    
    private static void log(Level level, String message) {
        if (level.ordinal() >= currentLevel.ordinal()) {
            String timestamp = LocalDateTime.now().format(formatter);
            String logMessage = String.format("[%s] %s: %s", timestamp, level, message);
            
            if (level == Level.ERROR) {
                System.err.println(logMessage);
            } else {
                System.out.println(logMessage);
            }
        }
    }
} 