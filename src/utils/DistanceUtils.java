package utils;

import graphics.Point;

/**
 * Shared helpers for distance calculations.
 */
public final class DistanceUtils {

    private DistanceUtils() {
        // Utility class
    }

    /**
     * Returns Euclidean distance between two points.
     */
    public static double euclidean(Point a, Point b) {
        int deltaX = b.x - a.x;
        int deltaY = b.y - a.y;
        return Math.sqrt((double) deltaX * deltaX + (double) deltaY * deltaY);
    }

    /**
     * Returns Euclidean distance between coordinate pairs.
     */
    public static double euclidean(double x1, double y1, double x2, double y2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Returns true when points are within the given Euclidean distance.
     */
    public static boolean isWithinEuclidean(Point a, Point b, double maxDistance) {
        return euclidean(a, b) <= maxDistance;
    }
}
