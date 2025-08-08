import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import graphics.Color;
import graphics.Point;
import graphics.IGraphics;
import map.TileConverter;
import pathfinding.PathNode;
import utils.Constants;
import utils.GameConfig;
import utils.TileCoordinateConverter;

/**
 * Handles rendering of pathfinding debug information including all map nodes,
 * explored nodes, and path visualization.
 */
public class RendererPathfinding {
    private final GraphicsMain graphicsMain;
    
    public RendererPathfinding(GraphicsMain graphicsMain) {
        this.graphicsMain = graphicsMain;
    }
    
    /**
     * Renders all pathfinding debug information for a unit
     */
    public void renderPathfindingDebug(IGraphics g, GameUnit unit, int[][] mapData) {
        if (!GameConfig.isShowPaths() && !GameConfig.isShowAllMapNodes()) {
            return;
        }
        
        // Render all map nodes with occupancy information (F5 mode)
        if (GameConfig.isShowAllMapNodes()) {
            renderAllMapNodes(g, mapData);
        }
        
        // Render explored nodes from A* algorithm (F5 mode)
        if (GameConfig.isShowAllMapNodes() && unit.getExploredNodes() != null) {
            renderExploredNodes(g, unit);
        }
        
        // Render current path (both F4 and F5 modes)
        if (GameConfig.isShowPaths() && unit.isPathCreated()) {
            renderCurrentPath(g, unit);
        }
        
        // Render pathfinding failures (F5 mode)
        if (GameConfig.isShowAllMapNodes() && unit.isPathfindingFailed()) {
            renderPathfindingFailureDetails(g, unit);
        }
    }
    
    /**
     * Renders all map nodes with color coding for occupancy
     */
    private void renderAllMapNodes(IGraphics g, int[][] mapData) {
        int mapHeight = mapData.length;
        int mapWidth = mapData[0].length;
        
        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                Point screenPos = TileCoordinateConverter.mapToScreen(x, y);
                int tileType = mapData[y][x];
                
                // Determine node color based on tile type
                Color nodeColor = getNodeColor(tileType);
                
                // Draw node with appropriate color
                g.setColor(nodeColor);
                drawNodeOnScreen(g, screenPos.x, screenPos.y, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
                
                // Draw node costs if enabled
                if (GameConfig.isShowNodeCosts()) {
                    renderNodeCosts(g, screenPos, x, y);
                }
            }
        }
    }
    
    /**
     * Returns color for different node types
     */
    private Color getNodeColor(int tileType) {
        switch (tileType) {
            case TileConverter.TILE_LAND: // 0 - Walkable
                return new Color(0, 255, 0, 30); // Light green, very transparent
            case TileConverter.TILE_WALL: // 1 - Wall/Obstacle
                return new Color(255, 0, 0, 100); // Red, semi-transparent
            case TileConverter.TILE_UNIT_LIGHT_PLAYER: // 2
            case TileConverter.TILE_UNIT_MEDIUM_PLAYER: // 3
            case TileConverter.TILE_UNIT_HEAVY_PLAYER: // 5
                return new Color(0, 0, 255, 80); // Blue for player units
            case TileConverter.TILE_UNIT_MEDIUM_ENEMY: // 4
            case TileConverter.TILE_UNIT_LIGHT_ENEMY: // 6
            case TileConverter.TILE_UNIT_HEAVY_ENEMY: // 7
                return new Color(255, 0, 255, 80); // Magenta for enemy units
            case TileConverter.TILE_FLAG_ALLY: // 8
            case TileConverter.TILE_FLAG_ENEMY: // 9
                return new Color(255, 255, 0, 120); // Yellow for flags
            default:
                return new Color(128, 128, 128, 50); // Gray for unknown
        }
    }
    
    /**
     * Renders explored nodes from A* algorithm
     */
    private void renderExploredNodes(IGraphics g, GameUnit unit) {
        ArrayList<PathNode> exploredNodes = unit.getExploredNodes();
        if (exploredNodes == null) return;
        
        // Create a set of path nodes for quick lookup
        Set<String> pathNodeKeys = new HashSet<>();
        if (unit.getPath() != null) {
            for (PathNode pathNode : unit.getPath()) {
                pathNodeKeys.add(pathNode.getX() + "," + pathNode.getY());
            }
        }
        
        for (PathNode node : exploredNodes) {
            Point screenPos = TileCoordinateConverter.mapToScreen(node.getX(), node.getY());
            
            // Use different color for nodes that are part of the final path
            if (pathNodeKeys.contains(node.getX() + "," + node.getY())) {
                g.setColor(new Color(0, 255, 255, 150)); // Cyan for path nodes
            } else {
                g.setColor(new Color(255, 255, 0, 100)); // Yellow for explored nodes
            }
            
            // Draw explored node
            drawNodeOnScreen(g, screenPos.x, screenPos.y, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
            
            // Draw node costs if enabled
            if (GameConfig.isShowNodeCosts()) {
                renderNodeCosts(g, screenPos, node.getX(), node.getY(), node);
            }
        }
    }
    
    /**
     * Renders the current path
     */
    private void renderCurrentPath(IGraphics g, GameUnit unit) {
        if (unit.getPath() == null) return;
        
        g.setColor(new Color(0, 255, 0, 150)); // Green path
        
        for (int i = 0; i < unit.getPath().size() - 1; i++) {
            PathNode currentNode = unit.getPath().get(i);
            PathNode nextNode = unit.getPath().get(i + 1);
            
            Point currentScreen = TileCoordinateConverter.mapToScreen(currentNode.getX(), currentNode.getY());
            Point nextScreen = TileCoordinateConverter.mapToScreen(nextNode.getX(), nextNode.getY());
            
            // Draw line between path nodes
            g.drawLine(
                currentScreen.x + Constants.TILE_WIDTH / 2 - graphicsMain.getCameraX(),
                currentScreen.y + Constants.TILE_HEIGHT / 2 - graphicsMain.getCameraY(),
                nextScreen.x + Constants.TILE_WIDTH / 2 - graphicsMain.getCameraX(),
                nextScreen.y + Constants.TILE_HEIGHT / 2 - graphicsMain.getCameraY()
            );
            
            // Draw path node outline (no fill)
            g.setColor(new Color(0, 255, 0, 200));
            drawRectOnScreen(g, currentScreen.x, currentScreen.y, Constants.TILE_WIDTH, Constants.TILE_HEIGHT, false);
        }
    }
    
    /**
     * Renders pathfinding failure details
     */
    private void renderPathfindingFailureDetails(IGraphics g, GameUnit unit) {
        Point unitPos = unit.getCurrentPosition();
        Point destPos = unit.getDestination();
        
        // Draw line to failed destination
        g.setColor(new Color(255, 0, 0, 150)); // Red line to failed destination
        g.drawLine(
            unitPos.x + Constants.TILE_WIDTH / 2 - graphicsMain.getCameraX(),
            unitPos.y + Constants.TILE_HEIGHT / 2 - graphicsMain.getCameraY(),
            destPos.x + Constants.TILE_WIDTH / 2 - graphicsMain.getCameraX(),
            destPos.y + Constants.TILE_HEIGHT / 2 - graphicsMain.getCameraY()
        );
        
        // Draw failed destination
        g.setColor(new Color(255, 0, 0, 200));
        drawNodeOnScreen(g, destPos.x, destPos.y, Constants.TILE_WIDTH, Constants.TILE_HEIGHT);
    }
    
    /**
     * Renders node costs (F, G, H values)
     */
    private void renderNodeCosts(IGraphics g, Point screenPos, int mapX, int mapY) {
        renderNodeCosts(g, screenPos, mapX, mapY, null);
    }
    
    private void renderNodeCosts(IGraphics g, Point screenPos, int mapX, int mapY, PathNode node) {
        // Only show costs for explored nodes or when explicitly requested
        if (node == null && !GameConfig.isShowNodeCosts()) return;
        
        String costText = "";
        if (node != null) {
            costText = "F:" + node.getF() + " G:" + node.getG() + " H:" + node.getH();
        } else {
            // For non-explored nodes, just show coordinates
            costText = mapX + "," + mapY;
        }
        
        // Draw cost text
        g.setColor(new Color(255, 255, 255, 200)); // White text
        int textX = screenPos.x + 2 - graphicsMain.getCameraX();
        int textY = screenPos.y + Constants.TILE_HEIGHT / 2 - graphicsMain.getCameraY();
        
        // Note: This would need a proper text rendering method
        // For now, we'll just draw a small indicator
        g.setColor(new Color(255, 255, 255, 150));
        g.fillRect(textX, textY - 2, 4, 4);
    }
    
    /**
     * Draws a node on screen with camera offset (filled)
     */
    private void drawNodeOnScreen(IGraphics g, int x, int y, int width, int height) {
        g.fillRect(x - graphicsMain.getCameraX(), y - graphicsMain.getCameraY(), width, height);
    }
    
    /**
     * Draws a rectangle on screen with camera offset (filled or outline)
     */
    private void drawRectOnScreen(IGraphics g, int x, int y, int width, int height, boolean filled) {
        if (filled) {
            g.fillRect(x - graphicsMain.getCameraX(), y - graphicsMain.getCameraY(), width, height);
        } else {
            g.drawRect(x - graphicsMain.getCameraX(), y - graphicsMain.getCameraY(), width, height);
        }
    }
}
