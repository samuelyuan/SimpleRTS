import java.awt.Image;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import graphics.GameImage;
import graphics.ImageUtils;
import map.TileConverter;
import utils.PathResolver;
import utils.Logger;

public class ImageService {
    private final Map<Integer, GameImage> gameImages = new HashMap<>();
    private final Map<String, GameImage> tileImages = new HashMap<>();
    private final Map<Integer, GameImage> darkGameImages = new HashMap<>();
    private final Map<String, GameImage> darkTileImages = new HashMap<>();
    
    private final PathResolver pathResolver;
    
    public ImageService(PathResolver pathResolver) {
        this.pathResolver = pathResolver;
    }
    
    public void loadImages() {
        loadGameImages();
        loadTileImages();
        generateDarkImages();
    }
    
    public GameImage getGameImage(int imageId) {
        GameImage image = gameImages.get(imageId);
        if (image == null) {
            // Try to provide a fallback image instead of throwing an exception
            utils.Logger.warn("Game image not found: " + imageId + ". Using fallback.");
            return getFallbackImage(imageId);
        }
        return image;
    }
    
    public GameImage getTileImage(String tileName) {
        GameImage image = tileImages.get(tileName);
        if (image == null) {
            utils.Logger.warn("Tile image not found: " + tileName + ". Using fallback.");
            return getFallbackTileImage(tileName);
        }
        return image;
    }
    
    public GameImage getTileImage(String tileName, boolean isDark) {
        if (isDark) {
            return darkTileImages.getOrDefault(tileName, getTileImage(tileName));
        }
        return getTileImage(tileName);
    }
    
    private void loadGameImages() {
        // Define image ID -> filename pairs
        Object[][] imageList = {
            { ImageConstants.IMGID_BG_MENU, "bg-menu.png" },
            { ImageConstants.IMGID_BG_DESK, "bg-desk.jpg" },
            { ImageConstants.IMGID_BG_BOOK, "bg-book.jpg" },
            { ImageConstants.IMGID_ICON_CAMPAIGN, "icon-campaign.png" },
            { ImageConstants.IMGID_ICON_INSTRUCT, "icon-instruct.png" },
            { ImageConstants.IMGID_ICON_RETURN, "icon-return.png" },
            { ImageConstants.IMGID_ICON_START, "icon-start.png" },
            { ImageConstants.IMGID_MENU_TITLE, "menu-title.png" },
            { ImageConstants.IMGID_MENU_INSTRUCT, "menu-instruct.png" },
            { ImageConstants.IMGID_MENU_START, "menu-start.png" },
            { ImageConstants.IMGID_MENU_VICTORY, "menu-victory.png" },
            { ImageConstants.IMGID_MENU_DEFEAT, "menu-defeat.png" },
            { ImageConstants.IMGID_SUPPLY_PLAYER, "game-supply-player.png" },
            { ImageConstants.IMGID_SUPPLY_ENEMY, "game-supply-enemy.png" },
            { ImageConstants.IMGID_GAME_TIMER, "game-timer.png" }
        };
        
        for (Object[] entry : imageList) {
            int id = (Integer) entry[0];
            String filename = (String) entry[1];
            loadImage(gameImages, id, filename);
        }
    }
    
    private void loadTileImages() {
        String[][] tileList = {
            { TileConverter.STR_LAND, "tile-snow.png" },
            { TileConverter.STR_WALL, "tile-tree.png" },
            { TileConverter.STR_FLAG, "tile-flag.png" },
            { TileConverter.STR_UNIT_LIGHT_PLAYER, "unit-infantry-player.png" },
            { TileConverter.STR_UNIT_LIGHT_ENEMY, "unit-infantry-enemy.png" },
            { TileConverter.STR_UNIT_MEDIUM_PLAYER, "unit-antiarmor-player.png" },
            { TileConverter.STR_UNIT_MEDIUM_ENEMY, "unit-antiarmor-enemy.png" },
            { TileConverter.STR_UNIT_HEAVY_PLAYER, "unit-tank-player.png" },
            { TileConverter.STR_UNIT_HEAVY_ENEMY, "unit-tank-enemy.png" }
        };
        
        for (String[] entry : tileList) {
            loadTile(tileImages, entry[0], entry[1]);
        }
    }
    
    private void loadImage(Map<Integer, GameImage> imgData, int imageId, String filename) {
        Image newImage = loadRawImage(filename);
        if (newImage != null) {
            imgData.put(imageId, new GameImage(newImage));
            		Logger.debug("Loaded image: ID=" + imageId + ", filename=" + filename);
        } else {
            Logger.error("Failed to load image: ID=" + imageId + ", filename=" + filename);
            // Don't add to map - let getGameImage handle the fallback
        }
    }
    
    private void loadTile(Map<String, GameImage> tileData, String imageStr, String filename) {
        Image newImage = loadRawImage(filename);
        if (newImage != null) {
            tileData.put(imageStr, new GameImage(newImage));
            		Logger.debug("Loaded tile: " + imageStr + ", filename=" + filename);
        } else {
            Logger.error("Failed to load tile: " + imageStr + ", filename=" + filename);
            // Don't add to map - let getTileImage handle the fallback
        }
    }
    
    private Image loadRawImage(String filename) {
        String fullPath = pathResolver.resolveImagePath(filename);
        try {
            File file = new File(fullPath);
            if (!file.exists()) {
                Logger.error("Image file does not exist: " + fullPath);
                return null;
            }
            
            Image image = ImageIO.read(file);
            if (image != null) {
                return image;
            } else {
                Logger.error("ImageIO.read returned null for: " + fullPath);
                return null;
            }
        } catch (Exception e) {
            Logger.error("Exception loading image " + fullPath + ": " + e.getMessage());
            return null;
        }
    }
    
    private void generateDarkImages() {
        for (Map.Entry<Integer, GameImage> entry : gameImages.entrySet()) {
            darkGameImages.put(entry.getKey(), ImageUtils.darken(entry.getValue()));
        }
        for (Map.Entry<String, GameImage> entry : tileImages.entrySet()) {
            darkTileImages.put(entry.getKey(), ImageUtils.darken(entry.getValue()));
        }
    }

    /**
     * Provides a fallback image when the requested image is not found
     */
    private GameImage getFallbackImage(int imageId) {
        // Create a simple colored rectangle as fallback
        java.awt.image.BufferedImage fallbackImage = new java.awt.image.BufferedImage(600, 500, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = fallbackImage.createGraphics();
        
        // Choose color based on image type
        java.awt.Color color = java.awt.Color.GRAY;
        String text = "Missing Image: " + imageId;
        
        if (imageId == ImageConstants.IMGID_MENU_DEFEAT) {
            color = new java.awt.Color(139, 0, 0); // Dark red
            text = "DEFEAT";
        } else if (imageId == ImageConstants.IMGID_MENU_VICTORY) {
            color = new java.awt.Color(0, 100, 0); // Dark green
            text = "VICTORY";
        } else if (imageId == ImageConstants.IMGID_BG_MENU) {
            color = java.awt.Color.DARK_GRAY;
            text = "Background";
        }
        
        // Fill background
        g2d.setColor(color);
        g2d.fillRect(0, 0, 600, 500);
        
        // Add border
        g2d.setColor(java.awt.Color.WHITE);
        g2d.setStroke(new java.awt.BasicStroke(5));
        g2d.drawRect(10, 10, 580, 480);
        
        // Add text
        g2d.setColor(java.awt.Color.WHITE);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48));
        java.awt.FontMetrics fm = g2d.getFontMetrics();
        int textX = (600 - fm.stringWidth(text)) / 2;
        int textY = 250;
        g2d.drawString(text, textX, textY);
        
        // Add subtitle for missing images
        if (imageId != ImageConstants.IMGID_MENU_DEFEAT && imageId != ImageConstants.IMGID_MENU_VICTORY) {
            g2d.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 24));
            String subtitle = "Image ID: " + imageId;
            fm = g2d.getFontMetrics();
            textX = (600 - fm.stringWidth(subtitle)) / 2;
            g2d.drawString(subtitle, textX, textY + 40);
        }
        
        g2d.dispose();
        
        return new GameImage(fallbackImage);
    }
    
    /**
     * Provides a fallback tile image when the requested tile is not found
     */
    private GameImage getFallbackTileImage(String tileName) {
        // Create a simple colored rectangle as fallback
        java.awt.image.BufferedImage fallbackImage = new java.awt.image.BufferedImage(64, 64, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = fallbackImage.createGraphics();
        
        g2d.setColor(java.awt.Color.MAGENTA);
        g2d.fillRect(0, 0, 64, 64);
        g2d.setColor(java.awt.Color.WHITE);
        g2d.drawString("Missing", 5, 30);
        g2d.dispose();
        
        return new GameImage(fallbackImage);
    }
} 