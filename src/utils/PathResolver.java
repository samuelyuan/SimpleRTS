package utils;

import java.io.File;

/**
 * Utility class for resolving file paths across different working directories.
 * Handles the transition from Applet to JFrame application where working directories may differ.
 */
public class PathResolver {
    
    /**
     * Resolves a file path by trying multiple possible locations.
     * Useful for handling different working directories when running as Applet vs JFrame.
     * 
     * @param filename The original filename or path
     * @param baseDir The base directory to search in (e.g., "maps", "img")
     * @return The resolved path that exists, or the original filename as fallback
     */
    public static String resolvePath(String filename, String baseDir) {
        // Try multiple possible paths
        String[] possiblePaths = {
            filename,
            baseDir + "/" + filename.replace("../" + baseDir + "/", ""),
            "../" + baseDir + "/" + filename.replace("../" + baseDir + "/", ""),
            "./" + baseDir + "/" + filename.replace("../" + baseDir + "/", ""),
            "src/" + baseDir + "/" + filename.replace("../" + baseDir + "/", "")
        };
        
        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                System.out.println("Found file at: " + file.getAbsolutePath());
                return path;
            }
        }
        
        System.out.println("Could not find file: " + filename);
        return filename; // fallback
    }
    
    /**
     * Resolves a file path for maps directory.
     * 
     * @param filename The original filename or path
     * @return The resolved path that exists, or the original filename as fallback
     */
    public static String resolveMapPath(String filename) {
        return resolvePath(filename, "maps");
    }
    
    /**
     * Resolves a file path for images directory.
     * 
     * @param filename The original filename or path
     * @return The resolved path that exists, or the original filename as fallback
     */
    public static String resolveImagePath(String filename) {
        return resolvePath(filename, "img");
    }
    
    /**
     * Resolves a file path for export directory within maps.
     * 
     * @param filename The original filename or path
     * @return The resolved path that exists, or the original filename as fallback
     */
    public static String resolveExportPath(String filename) {
        String basePath = resolveMapPath("export/" + filename.replace("../maps/export/", ""));
        return basePath;
    }
    
    /**
     * Ensures a directory exists, creating it if necessary.
     * 
     * @param path The directory path to ensure exists
     * @return true if the directory exists or was created successfully
     */
    public static boolean ensureDirectoryExists(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            return dir.mkdirs();
        }
        return true;
    }
} 