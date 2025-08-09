package pathfinding;

import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Cache for storing and retrieving pathfinding results to improve performance.
 * Uses an LRU (Least Recently Used) eviction policy with a maximum cache size.
 */
public class PathCache {
    private static final int DEFAULT_MAX_CACHE_SIZE = 100;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final boolean DEFAULT_ACCESS_ORDER = true; // true for LRU behavior
    
    private final int maxCacheSize;
    private final LinkedHashMap<Long, ArrayList<PathNode>> pathCache;
    
    /**
     * Creates a PathCache with default settings.
     */
    public PathCache() {
        this(DEFAULT_MAX_CACHE_SIZE);
    }
    
    /**
     * Creates a PathCache with specified maximum size.
     * @param maxCacheSize The maximum number of paths to cache
     */
    public PathCache(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        this.pathCache = new LinkedHashMap<Long, ArrayList<PathNode>>(maxCacheSize, DEFAULT_LOAD_FACTOR, DEFAULT_ACCESS_ORDER) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, ArrayList<PathNode>> eldest) {
                return size() > PathCache.this.maxCacheSize;
            }
        };
    }
    
    /**
     * Generates a cache key from start and end coordinates using bit shifting.
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param endX Ending X coordinate
     * @param endY Ending Y coordinate
     * @return A long value representing the cache key
     */
    public long generateCacheKey(int startX, int startY, int endX, int endY) {
        return ((long) startX << 48) | ((long) startY << 32) | 
               ((long) endX << 16) | endY;
    }
    
    /**
     * Retrieves a cached path for the given coordinates.
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param endX Ending X coordinate
     * @param endY Ending Y coordinate
     * @return The cached path, or null if not found
     */
    public ArrayList<PathNode> get(int startX, int startY, int endX, int endY) {
        long cacheKey = generateCacheKey(startX, startY, endX, endY);
        return pathCache.get(cacheKey);
    }
    
    /**
     * Stores a path in the cache for the given coordinates.
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param endX Ending X coordinate
     * @param endY Ending Y coordinate
     * @param path The path to cache
     */
    public void put(int startX, int startY, int endX, int endY, ArrayList<PathNode> path) {
        long cacheKey = generateCacheKey(startX, startY, endX, endY);
        pathCache.put(cacheKey, new ArrayList<>(path));
    }
    
    /**
     * Checks if a path exists in the cache for the given coordinates.
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param endX Ending X coordinate
     * @param endY Ending Y coordinate
     * @return true if the path is cached, false otherwise
     */
    public boolean contains(int startX, int startY, int endX, int endY) {
        long cacheKey = generateCacheKey(startX, startY, endX, endY);
        return pathCache.containsKey(cacheKey);
    }
    
    /**
     * Clears all cached paths.
     */
    public void clear() {
        pathCache.clear();
    }
    
    /**
     * Gets the current number of cached paths.
     * @return The number of cached paths
     */
    public int size() {
        return pathCache.size();
    }
    
    /**
     * Gets the maximum cache size.
     * @return The maximum number of paths that can be cached
     */
    public int getMaxSize() {
        return maxCacheSize;
    }
    
    /**
     * Gets the cache hit ratio (for performance monitoring).
     * @return The ratio of cache hits to total requests (0.0 to 1.0)
     */
    public double getHitRatio() {
        // This would require tracking hits/misses, but for now return a placeholder
        return 0.0;
    }
}
