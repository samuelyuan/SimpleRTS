package graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Stack;

public class AwtGraphicsAdapter implements IGraphics {
    private final Graphics g;
    private final Graphics2D g2d;
    private final Stack<AffineTransform> transformStack = new Stack<>();
    private boolean antiAliasingEnabled = false;
    private Color currentColor = Color.BLACK;
    private GameFont currentFont = new GameFont("Arial", GameFont.PLAIN, 12);

    public AwtGraphicsAdapter(Graphics g) {
        this.g = g;
        this.g2d = (Graphics2D) g;
        
        // Initialize with current transform
        transformStack.push(g2d.getTransform());
    }

    public Graphics getGraphics() {
        return this.g;
    }

    @Override
    public void setColor(graphics.Color color) {
        this.currentColor = color;
        g.setColor(color.toAwtColor());
    }

    @Override
    public void setFont(GameFont font) {
        this.currentFont = font;
        g.setFont(toAwtFont(font));
    }

    private java.awt.Font toAwtFont(GameFont font) {
        int style = java.awt.Font.PLAIN;
        if ((font.style & GameFont.BOLD) != 0) style |= java.awt.Font.BOLD;
        if ((font.style & GameFont.ITALIC) != 0) style |= java.awt.Font.ITALIC;
        return new java.awt.Font(font.family, style, font.size);
    }

    @Override
    public IFontMetrics getFontMetrics(GameFont font) {
        java.awt.Font awtFont = toAwtFont(font);
        java.awt.FontMetrics fm = g.getFontMetrics(awtFont);
        return new IFontMetrics() {
            @Override
            public int stringWidth(String text) { return fm.stringWidth(text); }
            @Override
            public int getAscent() { return fm.getAscent(); }
            @Override
            public int getDescent() { return fm.getDescent(); }
            @Override
            public int getHeight() { return fm.getHeight(); }
        };
    }

    @Override
    public void drawString(String str, int x, int y) {
        g.drawString(str, x, y);
    }

    @Override
    public void drawImage(GameImage img, int x, int y, int width, int height) {
        if (img == null) {
            utils.Logger.warn("Attempted to draw null GameImage");
            return;
        }
        
        Object backendImage = img.getBackendImage();
        if (backendImage == null) {
            utils.Logger.warn("GameImage has null backend image");
            return;
        }
        
        if (!(backendImage instanceof java.awt.Image)) {
            utils.Logger.warn("Backend image is not java.awt.Image: " + backendImage.getClass());
            return;
        }
        
        try {
            g.drawImage((java.awt.Image) backendImage, x, y, width, height, null);
        } catch (Exception e) {
            utils.Logger.error("Failed to draw image", e);
        }
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        g.fillRect(x, y, width, height);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        g.drawRect(x, y, width, height);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        g.drawPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
    }

    // ===== ESSENTIAL NEW METHODS IMPLEMENTATION =====

    @Override
    public void save() {
        transformStack.push(g2d.getTransform());
    }

    @Override
    public void restore() {
        if (!transformStack.isEmpty()) {
            g2d.setTransform(transformStack.pop());
        }
    }

    @Override
    public void translate(double dx, double dy) {
        g2d.translate(dx, dy);
    }

    @Override
    public void scale(double sx, double sy) {
        g2d.scale(sx, sy);
    }

    @Override
    public void rotate(double angle) {
        g2d.rotate(Math.toRadians(angle));
    }

    @Override
    public void drawImage(GameImage img, int x, int y, int width, int height, double rotation) {
        if (img == null || img.getBackendImage() == null) {
            return;
        }
        
        if (!(img.getBackendImage() instanceof java.awt.Image)) {
            return;
        }
        
        java.awt.Image awtImage = (java.awt.Image) img.getBackendImage();
        
        // Save current transform
        AffineTransform originalTransform = g2d.getTransform();
        
        // Apply rotation around image center
        g2d.translate(x + width / 2.0, y + height / 2.0);
        g2d.rotate(Math.toRadians(rotation));
        g2d.translate(-width / 2.0, -height / 2.0);
        
        // Draw the image
        g2d.drawImage(awtImage, 0, 0, width, height, null);
        
        // Restore original transform
        g2d.setTransform(originalTransform);
    }

    @Override
    public void setAntiAliasing(boolean enabled) {
        antiAliasingEnabled = enabled;
        if (enabled) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
    }

    // ===== ADDITIONAL UTILITY METHODS IMPLEMENTATION =====

    @Override
    public void drawCircle(int x, int y, int radius, boolean fill) {
        Ellipse2D circle = new Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2);
        if (fill) {
            g2d.fill(circle);
        } else {
            g2d.draw(circle);
        }
    }

    @Override
    public void clear(Color color) {
        Color originalColor = currentColor;
        setColor(color);
        g.fillRect(0, 0, 10000, 10000); // Large rectangle to clear entire area
        setColor(originalColor);
    }

    @Override
    public Color getColor() {
        return currentColor;
    }

    @Override
    public GameFont getFont() {
        return currentFont;
    }
    
    @Override
    public void drawEllipse(int x, int y, int width, int height, boolean fill) {
        Ellipse2D ellipse = new Ellipse2D.Double(x, y, width, height);
        if (fill) {
            g2d.fill(ellipse);
        } else {
            g2d.draw(ellipse);
        }
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight, boolean fill) {
        RoundRectangle2D roundRect = new RoundRectangle2D.Double(x, y, width, height, arcWidth, arcHeight);
        if (fill) {
            g2d.fill(roundRect);
        } else {
            g2d.draw(roundRect);
        }
    }

    @Override
    public void setStrokeWidth(float width) {
        g2d.setStroke(new java.awt.BasicStroke(width));
    }
} 