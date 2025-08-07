import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import graphics.GameImage;
import graphics.Color;
import graphics.Point;
import graphics.Rect;
import graphics.IGraphics;
import graphics.ImageUtils;
import map.TileConverter;
import utils.Constants;
import utils.GameConfig;
import utils.TileCoordinateConverter;
import pathfinding.PathNode;

/**
 * Handles rendering of game units.
 */
public class RendererUnit {
    private final GraphicsMain graphicsMain;
    
    // Cache for rotated sprites to improve performance
    private final Map<String, BufferedImage> rotationCache = new HashMap<>();
    private static final int ROTATION_CACHE_SIZE = 100; // Limit cache size
    
    // Pathfinding renderer for enhanced visualization
    private final RendererPathfinding pathfindingRenderer;

    public RendererUnit(GraphicsMain graphicsMain) {
        this.graphicsMain = graphicsMain;
        this.pathfindingRenderer = new RendererPathfinding(graphicsMain);
    }

    /**
     * Renders a single unit.
     * 
     * @param g    The graphics context
     * @param unit The unit to render
     */
    public void renderUnit(IGraphics g, GameUnit unit) {
        if (!unit.isAlive()) {
            return;
        }

        renderUnitSelection(g, unit);
        renderUnitSprite(g, unit);
        renderUnitHealthBar(g, unit);
        renderUnitFOV(g, unit);
        renderPathfindingFailureIndicator(g, unit);
        renderPathfindingDebug(g, unit); // Add debug rendering
    }

    private void renderUnitSelection(IGraphics g, GameUnit unit) {
        Point unitPos = unit.getCurrentPosition();
        int x = unitPos.x;
        int y = unitPos.y;
        
        // Draw hover effect for all units
        if (unit.isHovered()) {
            // Draw hover glow effect
            g.setColor(new Color(255, 255, 0)); // Yellow glow
            drawRectOnScreen(g, x - 3, y - 3, Constants.TILE_WIDTH + 6, Constants.TILE_HEIGHT + 6, true);
            
            // Draw hover border
            g.setColor(new Color(255, 255, 0)); // Yellow border
            drawRectOnScreen(g, x - 1, y - 1, Constants.TILE_WIDTH + 2, Constants.TILE_HEIGHT + 2, false);
        }
        
        if (unit.isPlayerSelected()) {
            // Draw a more prominent selection indicator
            // Draw outer glow effect
            g.setColor(new Color(0, 150, 255)); // Blue glow
            drawRectOnScreen(g, x - 2, y - 2, Constants.TILE_WIDTH + 4, Constants.TILE_HEIGHT + 4, true);
            
            // Draw main selection border
            g.setColor(new Color(0, 200, 255)); // Bright blue border
            drawRectOnScreen(g, x, y, Constants.TILE_WIDTH, Constants.TILE_HEIGHT, false);
            
            // Draw inner highlight
            g.setColor(new Color(0, 200, 255)); // Blue fill
            drawRectOnScreen(g, x + 1, y + 1, Constants.TILE_WIDTH - 2, Constants.TILE_HEIGHT - 2, true);

            // Draw destination point if path exists
            if (unit.isPathCreated()) {
                g.setColor(new Color(0, 255, 0)); // Green
                Point mapDest = unit.getMapPoint(unit.getDestination());
                Point screenPos = TileCoordinateConverter.mapToScreen(mapDest.x, mapDest.y);
                
                // Draw destination border only (no fill)
                g.setColor(new Color(0, 255, 0)); // Bright green border
                drawRectOnScreen(g, screenPos.x, screenPos.y, 
                    Constants.TILE_WIDTH, Constants.TILE_HEIGHT, false);
            }
        }
    }

    private void renderUnitSprite(IGraphics g, GameUnit unit) {
        // Update unit direction if moving
        if (unit.isPathCreated()) {
            Point mapDest = unit.getMapPoint(unit.getDestination());
            Point screenDest = TileCoordinateConverter.mapToScreen(mapDest.x, mapDest.y);
            
            // Calculate 360-degree rotation angle
            double targetAngle = calculateRotationAngle(unit.getCurrentPosition(), screenDest);
            unit.setTargetRotationAngle(targetAngle);
            
            // Update rotation smoothly
            unit.updateRotation();
            
            // Keep legacy direction for backward compatibility
            unit.setDirection(calculateDirection(unit.getCurrentPosition(), screenDest));
        }
        // Also update rotation when attacking (even if not moving)
        else if (unit.isAttacking()) {
            // Update rotation smoothly to face target (target angle was set in handleAttack)
            unit.updateRotation();
        }

        // Draw the unit sprite with rotation
        BufferedImage unitSprite = getUnitSprite(unit);
        BufferedImage rotatedSprite = createRotatedSprite(unitSprite, unit.getRotationAngle());
        drawImageOnScreen(g, rotatedSprite, unit.getCurrentPosition().x, unit.getCurrentPosition().y,
                Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
    }

    BufferedImage getUnitSprite(GameUnit unit) {
        int classType = unit.getClassType();
        boolean isPlayerUnit = unit.isPlayerUnit();

        BufferedImage sprite = null;

        switch (classType) {
            case Constants.UNIT_ID_LIGHT:
                sprite = getLightUnitSprite(isPlayerUnit);
                break;
            case Constants.UNIT_ID_MEDIUM:
                sprite = getMediumUnitSprite(isPlayerUnit);
                break;
            case Constants.UNIT_ID_HEAVY:
                sprite = getHeavyUnitSprite(isPlayerUnit);
                break;
        }

        return sprite;
    }

    private BufferedImage getLightUnitSprite(boolean isPlayerUnit) {
        BufferedImage sprite;
        if (isPlayerUnit) {
            sprite = (BufferedImage) graphicsMain.getStateManager().getImageService()
                    .getTileImage(TileConverter.STR_UNIT_LIGHT_PLAYER, graphicsMain.isNight())
                    .getBackendImage();
        } else {
            sprite = (BufferedImage) graphicsMain.getStateManager().getImageService()
                    .getTileImage(TileConverter.STR_UNIT_LIGHT_ENEMY, graphicsMain.isNight())
                    .getBackendImage();
        }
        // Use the east-facing frame (index 2) as base for rotation, which should be more naturally oriented
        // Frame 0 = North, Frame 1 = South, Frame 2 = East, Frame 3 = West
        return sprite.getSubimage(Constants.TILE_WIDTH * 2, 0, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
    }

    private BufferedImage getMediumUnitSprite(boolean isPlayerUnit) {
        BufferedImage sprite;
        if (isPlayerUnit) {
            sprite = (BufferedImage) graphicsMain.getStateManager().getImageService()
                    .getTileImage(TileConverter.STR_UNIT_MEDIUM_PLAYER, graphicsMain.isNight())
                    .getBackendImage();
        } else {
            sprite = (BufferedImage) graphicsMain.getStateManager().getImageService()
                    .getTileImage(TileConverter.STR_UNIT_MEDIUM_ENEMY, graphicsMain.isNight())
                    .getBackendImage();
        }
        return sprite.getSubimage(0, 0, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
    }

    private BufferedImage getHeavyUnitSprite(boolean isPlayerUnit) {
        BufferedImage sprite;
        if (isPlayerUnit) {
            sprite = (BufferedImage) graphicsMain.getStateManager().getImageService()
                    .getTileImage(TileConverter.STR_UNIT_HEAVY_PLAYER, graphicsMain.isNight())
                    .getBackendImage();
        } else {
            sprite = (BufferedImage) graphicsMain.getStateManager().getImageService()
                    .getTileImage(TileConverter.STR_UNIT_HEAVY_ENEMY, graphicsMain.isNight())
                    .getBackendImage();
        }
        // Use the east-facing frame (index 2) as base for rotation, which should be more naturally oriented
        // Frame 0 = North, Frame 1 = South, Frame 2 = East, Frame 3 = West
        return sprite.getSubimage(Constants.TILE_WIDTH * 2, 0, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
    }

    private void renderUnitHealthBar(IGraphics g, GameUnit unit) {
        int health = unit.getHealth();
        if (health <= 0)
            return;

        Color healthColor = getHealthColor(health);
        if (healthColor == null)
            return;

        Point current = unit.getCurrentPosition();
        int barWidth = (int) ((double) (Constants.TILE_WIDTH - 2) / 100.0 * health);
        int barHeight = Constants.TILE_HEIGHT / 8;

        Rect healthBar = new Rect(
                current.x + 2,
                current.y + Constants.TILE_HEIGHT / 8,
                barWidth,
                barHeight);

        g.setColor(healthColor);
        g.fillRect(healthBar.x - graphicsMain.getCameraX(), healthBar.y - graphicsMain.getCameraY(),
                healthBar.width, healthBar.height);
    }

    /**
     * Gets the color for a health bar based on health value.
     * 
     * @param health The health value (0-100)
     * @return The color for the health bar, or null if health <= 0
     */
    public Color getHealthColor(int health) {
        if (health > 75) {
            return Color.GREEN;
        } else if (health > 50) {
            return Color.YELLOW;
        } else if (health > 25) {
            return Color.ORANGE;
        } else if (health > 0) {
            return Color.RED;
        }
        return null;
    }

    private void drawRectOnScreen(IGraphics g, int x, int y, int width, int height, boolean fill) {
        int screenX = x - graphicsMain.getCameraX();
        int screenY = y - graphicsMain.getCameraY();

        if (fill) {
            g.fillRect(screenX, screenY, width, height);
        } else {
            g.drawRect(screenX, screenY, width, height);
        }
    }

    private void drawImageOnScreen(IGraphics g, java.awt.Image img, int x, int y, int width, int height) {
        g.drawImage(new GameImage(img), x - graphicsMain.getCameraX(), y - graphicsMain.getCameraY(), width, height);
    }

    private int calculateDirection(Point current, Point destination) {
        int deltaX = destination.x - current.x;
        int deltaY = destination.y - current.y;
        if (Math.abs(deltaX) >= Math.abs(deltaY)) {
            return (deltaX > 0) ? Constants.DIR_EAST : Constants.DIR_WEST;
        } else {
            return (deltaY > 0) ? Constants.DIR_SOUTH : Constants.DIR_NORTH;
        }
    }

    private double calculateRotationAngle(Point current, Point destination) {
        int deltaX = destination.x - current.x;
        int deltaY = destination.y - current.y;
        
        // Calculate the angle in radians
        double angleRad = Math.atan2(deltaY, deltaX);
        
        // Convert radians to degrees
        double angleDeg = Math.toDegrees(angleRad);
        
        // Since we're using the east-facing sprite as base:
        // Math.atan2 gives 0° = east, 90° = north
        // Our base sprite faces east (0°), so we don't need to subtract 90°
        // Just normalize to a 360-degree angle
        return (angleDeg + 360) % 360;
    }

    private BufferedImage createRotatedSprite(BufferedImage original, double angle) {
        // Round angle to nearest degree for caching
        int roundedAngle = (int) Math.round(angle);
        
        // Create cache key
        String cacheKey = original.hashCode() + "_" + roundedAngle;
        
        // Check cache first
        BufferedImage cached = rotationCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // Create new rotated sprite
        int width = original.getWidth();
        int height = original.getHeight();
        BufferedImage rotated = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        
        // Set rendering hints for better quality
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, 
                            java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, 
                            java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, 
                            java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Rotate around center
        g2d.rotate(Math.toRadians(angle), width / 2.0, height / 2.0);
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();
        
        // Cache the result
        cacheRotatedSprite(cacheKey, rotated);
        
        return rotated;
    }
    
    private void cacheRotatedSprite(String key, BufferedImage sprite) {
        // Limit cache size
        if (rotationCache.size() >= ROTATION_CACHE_SIZE) {
            // Clear half the cache when it gets too large
            rotationCache.clear();
        }
        rotationCache.put(key, sprite);
    }
    
    // Method to clear rotation cache (call when sprites change)
    public void clearRotationCache() {
        rotationCache.clear();
    }
    
    /**
     * Renders the Field of View (FOV) cone for a unit.
     * Configurable to show FOV for different unit types and selection states.
     * 
     * @param g The graphics context
     * @param unit The unit whose FOV to render
     */
    private void renderUnitFOV(IGraphics g, GameUnit unit) {
        // Master toggle check
        if (!GameConfig.isFovRenderingEnabled()) {
            return;
        }
        
        // Determine if we should render FOV for this unit
        boolean shouldRender = false;
        
        if (unit.isPlayerUnit()) {
            // For player units: always show FOV
            shouldRender = true;
        } else {
            // For enemy units: show if FOV_SHOW_ENEMY_UNITS is enabled
            shouldRender = GameConfig.isFovShowEnemyUnits();
        }
        
        if (!shouldRender) {
            return;
        }
        
        Point unitPos = unit.getCurrentPosition();
        double rotationAngle = unit.getRotationAngle();
        
        // Calculate FOV cone points
        int[] xPoints = new int[Constants.FOV_RENDER_SEGMENTS + 2];
        int[] yPoints = new int[Constants.FOV_RENDER_SEGMENTS + 2];
        
        // Start with unit position
        xPoints[0] = unitPos.x + Constants.TILE_WIDTH / 2 - graphicsMain.getCameraX();
        yPoints[0] = unitPos.y + Constants.TILE_HEIGHT / 2 - graphicsMain.getCameraY();
        
        // Calculate cone points
        double startAngle = rotationAngle - Constants.FOV_HALF_ANGLE;
        double angleStep = Constants.FOV_ANGLE / Constants.FOV_RENDER_SEGMENTS;
        int radius = Constants.FOV_RENDER_RADIUS * Constants.TILE_WIDTH;
        
        for (int i = 0; i <= Constants.FOV_RENDER_SEGMENTS; i++) {
            double angle = startAngle + (i * angleStep);
            double angleRad = Math.toRadians(angle);
            
            int x = unitPos.x + Constants.TILE_WIDTH / 2 + (int)(radius * Math.cos(angleRad));
            int y = unitPos.y + Constants.TILE_HEIGHT / 2 + (int)(radius * Math.sin(angleRad));
            
            xPoints[i + 1] = x - graphicsMain.getCameraX();
            yPoints[i + 1] = y - graphicsMain.getCameraY();
        }
        
        // Choose colors based on unit type
        Color fillColor, borderColor, directionColor;
        
        if (unit.isPlayerUnit()) {
            // Player units: yellow/orange colors
            fillColor = new Color(255, 255, 0, 50); // Semi-transparent yellow
            borderColor = new Color(255, 255, 0, 150); // More opaque yellow border
            directionColor = new Color(255, 255, 255, 200); // White direction line
        } else {
            // Enemy units: red colors for debugging
            fillColor = new Color(255, 0, 0, 50); // Semi-transparent red
            borderColor = new Color(255, 0, 0, 150); // More opaque red border
            directionColor = new Color(255, 255, 255, 200); // White direction line
        }
        
        // Draw FOV cone with semi-transparent fill
        g.setColor(fillColor);
        g.fillPolygon(xPoints, yPoints, Constants.FOV_RENDER_SEGMENTS + 2);
        
        // Draw FOV cone border
        g.setColor(borderColor);
        g.drawPolygon(xPoints, yPoints, Constants.FOV_RENDER_SEGMENTS + 2);
        
        // Draw direction indicator (line from unit center in facing direction)
        int directionX = unitPos.x + Constants.TILE_WIDTH / 2 + (int)(radius * 0.7 * Math.cos(Math.toRadians(rotationAngle)));
        int directionY = unitPos.y + Constants.TILE_HEIGHT / 2 + (int)(radius * 0.7 * Math.sin(Math.toRadians(rotationAngle)));
        
        g.setColor(directionColor);
        g.drawLine(
            unitPos.x + Constants.TILE_WIDTH / 2 - graphicsMain.getCameraX(),
            unitPos.y + Constants.TILE_HEIGHT / 2 - graphicsMain.getCameraY(),
            directionX - graphicsMain.getCameraX(),
            directionY - graphicsMain.getCameraY()
        );
    }
    
    /**
     * Renders a visual indicator when pathfinding fails
     */
    private void renderPathfindingFailureIndicator(IGraphics g, GameUnit unit) {
        if (!unit.isPathfindingFailed()) {
            return;
        }

        Point unitPos = unit.getCurrentPosition();
        int x = unitPos.x;
        int y = unitPos.y;
        
        // Calculate alpha based on remaining timer for pulsing effect
        int remainingTime = unit.getPathfindingFailureTimer();
        int alpha = Math.max(50, Math.min(200, 100 + (int)(100 * Math.sin(remainingTime * 0.2)))); // Clamped pulsing effect
        
        // Draw red failure indicator
        g.setColor(new Color(255, 0, 0, alpha));
        drawRectOnScreen(g, x - 2, y - 2, Constants.TILE_WIDTH + 4, Constants.TILE_HEIGHT + 4, true);
        
        // Draw border
        g.setColor(new Color(255, 0, 0, 200));
        drawRectOnScreen(g, x - 2, y - 2, Constants.TILE_WIDTH + 4, Constants.TILE_HEIGHT + 4, false);
    }
    
    /**
     * Renders pathfinding debug information
     */
    public void renderPathfindingDebug(IGraphics g, GameUnit unit) {
        // Use the new enhanced pathfinding renderer
        pathfindingRenderer.renderPathfindingDebug(g, unit, graphicsMain.getStateManager().getGameMap().getMapData());
    }
    

}