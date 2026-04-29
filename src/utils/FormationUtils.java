package utils;

import graphics.Point;

/**
 * Shared helpers for circular formation radius and position calculations.
 */
public final class FormationUtils {

    private FormationUtils() {
        // Utility class
    }

    /**
     * Calculates a base circular formation radius from unit count.
     */
    public static int calculateBaseFormationRadius(int unitCount) {
        if (unitCount <= 4) return 1;
        if (unitCount <= 8) return 2;
        if (unitCount <= 16) return 3;
        return 4;
    }

    /**
     * Calculates a circular formation radius with an optional offset.
     */
    public static int calculateFormationRadius(int unitCount, int radiusOffset) {
        return calculateBaseFormationRadius(unitCount) + radiusOffset;
    }

    /**
     * Calculates position for one unit in a circular formation.
     */
    public static Point calculateFormationPosition(Point center, int unitIndex, int totalUnits, int radius) {
        if (totalUnits <= 1) {
            return new Point(center.x, center.y);
        }

        double angle = (2 * Math.PI * unitIndex) / totalUnits;
        int offsetX = (int) (Math.cos(angle) * radius);
        int offsetY = (int) (Math.sin(angle) * radius);
        return new Point(center.x + offsetX, center.y + offsetY);
    }
}
