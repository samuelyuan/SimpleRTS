package graphics;

public interface IGraphics {
    void setColor(Color color);
    void setFont(GameFont font);
    void drawString(String str, int x, int y);
    void drawImage(GameImage img, int x, int y, int width, int height);
    void fillRect(int x, int y, int width, int height);
    void drawRect(int x, int y, int width, int height);
    void fillPolygon(int[] xPoints, int[] yPoints, int nPoints);
    void drawPolygon(int[] xPoints, int[] yPoints, int nPoints);
    void drawLine(int x1, int y1, int x2, int y2);
    IFontMetrics getFontMetrics(GameFont font);
    
    // === Transform and State Management ===
    /**
     * Saves the current graphics state (transform, color, font, etc.)
     */
    void save();
    
    /**
     * Restores the previously saved graphics state
     */
    void restore();
    
    /**
     * Translates the coordinate system
     * @param dx X translation
     * @param dy Y translation
     */
    void translate(double dx, double dy);
    
    /**
     * Scales the coordinate system
     * @param sx X scale factor
     * @param sy Y scale factor
     */
    void scale(double sx, double sy);
    
    /**
     * Rotates the coordinate system
     * @param angle Rotation angle in degrees
     */
    void rotate(double angle);
    
    // === Enhanced Drawing Methods ===
    /**
     * Draws an image with rotation
     * @param img The image to draw
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width
     * @param height Height
     * @param rotation Rotation angle in degrees
     */
    void drawImage(GameImage img, int x, int y, int width, int height, double rotation);
    
    // === Rendering Quality ===
    /**
     * Sets anti-aliasing for smoother rendering
     * @param enabled Whether to enable anti-aliasing
     */
    void setAntiAliasing(boolean enabled);
} 