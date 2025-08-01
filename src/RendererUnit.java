import java.awt.image.BufferedImage;
import graphics.GameImage;
import graphics.Color;
import graphics.Point;
import graphics.Rect;
import graphics.IGraphics;
import graphics.ImageUtils;
import map.TileConverter;
import utils.Constants;
import utils.TileCoordinateConverter;

/**
 * Handles rendering of game units.
 */
public class RendererUnit {
    private final GraphicsMain graphicsMain;

    public RendererUnit(GraphicsMain graphicsMain) {
        this.graphicsMain = graphicsMain;
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
            unit.setDirection(calculateDirection(unit.getCurrentPosition(),
                    TileCoordinateConverter.mapToScreen(mapDest.x, mapDest.y)));
        }

        // Draw the unit sprite
        BufferedImage unitSprite = getUnitSprite(unit);
        drawImageOnScreen(g, unitSprite, unit.getCurrentPosition().x, unit.getCurrentPosition().y,
                Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
    }

    private BufferedImage getUnitSprite(GameUnit unit) {
        int classType = unit.getClassType();
        boolean isPlayerUnit = unit.isPlayerUnit();
        int direction = unit.getDirection();

        BufferedImage sprite = null;

        switch (classType) {
            case Constants.UNIT_ID_LIGHT:
                sprite = getLightUnitSprite(isPlayerUnit, direction);
                break;
            case Constants.UNIT_ID_MEDIUM:
                sprite = getMediumUnitSprite(isPlayerUnit);
                break;
            case Constants.UNIT_ID_HEAVY:
                sprite = getHeavyUnitSprite(isPlayerUnit, direction);
                break;
        }

        return sprite;
    }

    private BufferedImage getLightUnitSprite(boolean isPlayerUnit, int direction) {
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
        return sprite.getSubimage(Constants.TILE_WIDTH * direction, 0,
                Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
    }

    private BufferedImage getMediumUnitSprite(boolean isPlayerUnit) {
        return (BufferedImage) ImageUtils
                .addTeamColorToUnit(graphicsMain.getStateManager().getImageService().getTileImage(TileConverter.STR_UNIT_MEDIUM),
                        isPlayerUnit)
                .getBackendImage();
    }

    private BufferedImage getHeavyUnitSprite(boolean isPlayerUnit, int direction) {
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
        return sprite.getSubimage(Constants.TILE_WIDTH * direction, 0,
                Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
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
}