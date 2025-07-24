package graphics;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;

import graphics.Color;

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
    public void setFont(Font font) {
        g.setFont(font);
    }

    @Override
    public void drawString(String str, int x, int y) {
        g.drawString(str, x, y);
    }

    @Override
    public void drawImage(Image img, int x, int y, int width, int height) {
        g.drawImage(img, x, y, width, height, null);
    }
} 