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
    
    // === Additional Drawing Methods ===
    /**
     * Draws a circle
     * @param x X coordinate of center
     * @param y Y coordinate of center
     * @param radius Radius of circle
     * @param fill Whether to fill the circle
     */
    void drawCircle(int x, int y, int radius, boolean fill);
    
    // === Utility Methods ===
    /**
     * Clears the entire drawing area with a color
     * @param color The color to clear with
     */
    void clear(Color color);
    
    /**
     * Gets the current color
     * @return Current color
     */
    Color getColor();
    
    /**
     * Gets the current font
     * @return Current font
     */
    GameFont getFont();
    
    // === Additional Shape Methods (for testing) ===
    /**
     * Draws an ellipse
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width of ellipse
     * @param height Height of ellipse
     * @param fill Whether to fill the ellipse
     */
    void drawEllipse(int x, int y, int width, int height, boolean fill);
    
    /**
     * Draws a rounded rectangle
     * @param x X coordinate
     * @param y Y coordinate
     * @param width Width of rectangle
     * @param height Height of rectangle
     * @param arcWidth Arc width for corners
     * @param arcHeight Arc height for corners
     * @param fill Whether to fill the rectangle
     */
    void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean fill);
    
    /**
     * Sets the stroke width for drawing
     * @param width Stroke width
     */
    void setStrokeWidth(float width);
} 