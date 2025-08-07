package graphics;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

/**
 * Enhanced GameImage class with essential resource management and transformation support
 */
public class GameImage {
    private final Object backendImage;
    private final String path;
    private final int width;
    private final int height;
    private boolean disposed = false;
    private final ImageFormat format;
    
    /**
     * Image format types
     */
    public enum ImageFormat {
        PNG, JPG, GIF, BMP, UNKNOWN
    }
    
    /**
     * Constructor for backward compatibility
     * @param backendImage The backend image object
     */
    public GameImage(Object backendImage) {
        this(backendImage, null, ImageFormat.UNKNOWN);
    }
    
    /**
     * Enhanced constructor with path and format information
     * @param backendImage The backend image object
     * @param path The file path of the image
     * @param format The image format
     */
    public GameImage(Object backendImage, String path, ImageFormat format) {
        this.backendImage = backendImage;
        this.path = path;
        this.format = format;
        
        // Extract dimensions from the backend image
        if (backendImage instanceof Image) {
            Image img = (Image) backendImage;
            this.width = img.getWidth(null);
            this.height = img.getHeight(null);
        } else {
            this.width = -1;
            this.height = -1;
        }
    }
    
    /**
     * Gets the backend image object
     * @return The backend image
     */
    public Object getBackendImage() {
        return backendImage;
    }
    
    /**
     * Gets the file path of the image
     * @return The file path, or null if not available
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Gets the width of the image
     * @return The width in pixels, or -1 if unknown
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Gets the height of the image
     * @return The height in pixels, or -1 if unknown
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Gets the image format
     * @return The image format
     */
    public ImageFormat getFormat() {
        return format;
    }
    
    /**
     * Checks if the image has been disposed
     * @return true if disposed, false otherwise
     */
    public boolean isDisposed() {
        return disposed;
    }
    
    /**
     * Creates a scaled version of this image
     * @param newWidth The new width
     * @param newHeight The new height
     * @param highQuality Whether to use high-quality scaling
     * @return A new GameImage with the scaled content
     */
    public GameImage getScaled(int newWidth, int newHeight, boolean highQuality) {
        if (disposed || backendImage == null) {
            return null;
        }
        
        if (!(backendImage instanceof Image)) {
            return null;
        }
        
        Image original = (Image) backendImage;
        BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaled.createGraphics();
        
        if (highQuality) {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        
        g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        g2d.dispose();
        
        return new GameImage(scaled, path, format);
    }
    
    /**
     * Creates a rotated version of this image
     * @param angle Rotation angle in degrees
     * @return A new GameImage with the rotated content
     */
    public GameImage getRotated(double angle) {
        if (disposed || backendImage == null) {
            return null;
        }
        
        if (!(backendImage instanceof Image)) {
            return null;
        }
        
        Image original = (Image) backendImage;
        double radians = Math.toRadians(angle);
        
        // Calculate new dimensions
        double cos = Math.abs(Math.cos(radians));
        double sin = Math.abs(Math.sin(radians));
        int newWidth = (int) Math.round(width * cos + height * sin);
        int newHeight = (int) Math.round(width * sin + height * cos);
        
        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        
        // Set high quality rendering
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Translate to center, rotate, then translate back
        g2d.translate(newWidth / 2.0, newHeight / 2.0);
        g2d.rotate(radians);
        g2d.translate(-width / 2.0, -height / 2.0);
        
        g2d.drawImage(original, 0, 0, null);
        g2d.dispose();
        
        return new GameImage(rotated, path, format);
    }
    
    /**
     * Disposes of the image resources
     * This should be called when the image is no longer needed
     */
    public void dispose() {
        if (!disposed && backendImage instanceof Image) {
            // For AWT images, we can't explicitly dispose, but we can mark as disposed
            disposed = true;
        }
    }
    
    /**
     * Checks if this image is valid (not disposed and has valid backend)
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return !disposed && backendImage != null;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameImage gameImage = (GameImage) o;
        return width == gameImage.width && 
               height == gameImage.height && 
               disposed == gameImage.disposed && 
               java.util.Objects.equals(backendImage, gameImage.backendImage) && 
               java.util.Objects.equals(path, gameImage.path) && 
               format == gameImage.format;
    }
    
    @Override
    public int hashCode() {
        return java.util.Objects.hash(backendImage, path, width, height, disposed, format);
    }
    
    @Override
    public String toString() {
        return "GameImage{" +
                "path='" + path + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", format=" + format +
                ", disposed=" + disposed +
                '}';
    }
} 