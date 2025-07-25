package graphics;

import java.awt.Graphics;

public class AwtGraphicsAdapter implements IGraphics {
    private final Graphics g;

    public AwtGraphicsAdapter(Graphics g) {
        this.g = g;
    }

    public Graphics getGraphics() {
        return this.g;
    }

    @Override
    public void setColor(graphics.Color color) {
        g.setColor(color.toAwtColor());
    }

    @Override
    public void setFont(GameFont font) {
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
        g.drawImage((java.awt.Image) img.getBackendImage(), x, y, width, height, null);
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
} 